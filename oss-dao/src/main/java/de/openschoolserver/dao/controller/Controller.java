/* (c) 2017 Péter Varkoly <peter@varkoly.de> - all rights reserved */
package de.openschoolserver.dao.controller;

import javax.persistence.EntityManager;



import javax.persistence.Query;
import java.util.List;
import de.openschoolserver.dao.Session;
import de.openschoolserver.dao.internal.CommonEntityManagerFactory;
import de.openschoolserver.dao.controller.Config;
import de.openschoolserver.dao.Group;
import de.openschoolserver.dao.User;
import de.openschoolserver.dao.Device;
import de.openschoolserver.dao.Room;
import de.openschoolserver.dao.tools.OSSShellTools;

public class Controller extends Config {
	
	protected Session session ;
	
	public Controller(Session session) {
		super();
		this.session=session;
	}
	
    protected EntityManager getEntityManager() {
        return CommonEntityManagerFactory.instance(session.getSchoolId()).getEntityManagerFactory().createEntityManager();
    }
    
    protected Session getSession() {
    	return this.session;
    }
    
    protected boolean isNameUnique(String name){
    	EntityManager em = this.getEntityManager();
    	Query query = em.createNamedQuery("User.getByUid");
    	query.setParameter("uid", name);
    	List<User> user = (List<User>) query.getResultList();
    	if( ! user.isEmpty() ){
    		return false;
    	}
    	query = em.createNamedQuery("Group.getByName");
    	query.setParameter("name", name);
    	List<Group> group = (List<Group>) query.getResultList();
    	if( ! group.isEmpty() ){
    		return false;
    	}
    	query = em.createNamedQuery("Device.getByName");
    	query.setParameter("name", name);
    	List<Device> device = (List<Device>) query.getResultList();
    	if( ! device.isEmpty() ){
    		return false;
    	}
    	query = em.createNamedQuery("Room.getByName");
    	query.setParameter("name", name);
    	List<Room> room = (List<Room>) query.getResultList();
    	if( ! room.isEmpty() ){
    		return false;
    	}
    	return true;
    }
    
    protected void startPlugin(String pluginName, Object object){
    	StringBuilder data = new StringBuilder();
    	String[] program   = new String[4];
        StringBuffer reply = new StringBuffer();
        StringBuffer error = new StringBuffer();
    	program[0] = "/usr/share/oss/plugins/plugin_handler.sh";
    	program[1] = pluginName;
    	switch(object.getClass().getName()) {
    	case "User":
    		User user = (User)object;
    		switch(pluginName) {
    		case "add_user", "modify_user":
    		    data.append(String.format("givenName: %s%n", user.getGivenName()));
    		    data.append(String.format("sureName: %s%n", user.getSureName()));
    		    data.append(String.format("birthDay: %s%n", user.getBirthDay()));
    		    data.append(String.format("password: %s%n", user.getPassword()));
    		    data.append(String.format("uid: %s%n", user.getUid()));
    		    data.append(String.format("role: %s%n", user.getRole()));
    		    break;
    		case "delete_user":
    			program[1] = "delete_user";
    			data.append(String.format("uid: %s%n", user.getUid()));
    			break;
    		}
    		break;
    	case "Group":
    		//TODO 
    		Group group = (Group)object;
    		switch(pluginName){
    		case "add_group":
    			break;
    		case "delete_group":
    			break;
    		case "modify_group":
    			break;
    		}
    		break;
    	case "Device":
    		//TODO
    		break;
    	case "Room":
    		//TODO
    		break;
    	}
    	OSSShellTools.exec(program, reply, error, data.toString());
    }
 
}
