/* (c) 2017 P��ter Varkoly <peter@varkoly.de> - all rights reserved */
package de.openschoolserver.dao.controller;

import javax.persistence.EntityManager;

import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.*;
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
			em.close();
			return false;
		}
		query = em.createNamedQuery("Group.getByName");
		query.setParameter("name", name);
		List<Group> group = (List<Group>) query.getResultList();
		if( ! group.isEmpty() ){
			em.close();
			return false;
		}
		query = em.createNamedQuery("Device.getByName");
		query.setParameter("name", name);
		List<Device> device = (List<Device>) query.getResultList();
		if( ! device.isEmpty() ){
			em.close();
			return false;
		}
		query = em.createNamedQuery("Room.getByName");
		query.setParameter("name", name);
		List<Room> room = (List<Room>) query.getResultList();
		if( ! room.isEmpty() ){
			em.close();
			return false;
		}
		em.close();
		return true;
	}

	protected boolean checkNonASCII(String name) {
		return Pattern.matches("[^a-zA-Z0-9\\.\\-_]",name);
	}

	protected boolean checkBadHostName(String name) {
		if( ! Pattern.matches("[^a-zA-Z0-9\\-]",name) ){
			return Pattern.matches("-wlan$",name);
		}
		return true;
	}

	protected String isMacUnique(String name){
		EntityManager em = this.getEntityManager();
		Query query = em.createNamedQuery("Device.getByMAC");
		query.setParameter("MAC", name);
		List<Device> devices = (List<Device>) query.getResultList();
		em.close();
		if( ! devices.isEmpty() ){
			return devices.get(0).getName();
		}
		return "";
	}

	protected String isIPUnique(String name){
		EntityManager em = this.getEntityManager();
		Query query = em.createNamedQuery("Device.getByIP");
		query.setParameter("IP", name);
		List<Device> devices = (List<Device>) query.getResultList();
		em.close();
		if( ! devices.isEmpty() ){
			return devices.get(0).getName();
		}
		return "";
	}

	protected void startPlugin(String pluginName, Object object){
		StringBuilder data = new StringBuilder();
		String[] program   = new String[2];
		StringBuffer reply = new StringBuffer();
		StringBuffer error = new StringBuffer();
		program[0] = "/usr/share/oss/plugins/plugin_handler.sh";
		program[1] = pluginName;
		switch(object.getClass().getName()) {
		case "de.openschoolserver.dao.User":
			User user = (User)object;
			switch(pluginName) {
			case "add_user":
			case "modify_user":
				data.append(String.format("givenName: %s%n", user.getGivenName()));
				data.append(String.format("sureName: %s%n", user.getSureName()));
				data.append(String.format("birthDay: %s%n", user.getBirthDay()));
				data.append(String.format("password: %s%n", user.getPassword()));
				data.append(String.format("uid: %s%n", user.getUid()));
				data.append(String.format("role: %s%n", user.getRole()));
				String myGroups = "";
				for(Group g : user.getGroups()) {
					myGroups.concat(g.getName() + " ");
				}
				data.append(String.format("groups: %s%n", myGroups));
				break;
			case "delete_user":
				data.append(String.format("uid: %s%n", user.getUid()));
				break;
			}
			break;
		case "de.openschoolserver.dao.Group":
			//TODO 
			Group group = (Group)object;
			switch(pluginName){
			case "add_group":
			case "modify_group":
				data.append(String.format("name: %s%n", group.getName()));
				data.append(String.format("description: %s%n", group.getDescription()));
				break;
			case "delete_group":
				data.append(String.format("name: %s%n", group.getName()));
				break;
			}
			break;
		case "de.openschoolserver.dao.Device":
			//TODO
			Device device = (Device)object;
			switch(pluginName){
			case "add_device":
			case "modify_device":
				data.append(String.format("name: %s%n", device.getName()));
				data.append(String.format("ip: %s%n", device.getIp()));
				data.append(String.format("mac: %s%n", device.getMac()));
				if( ! device.getWlanIp().isEmpty() ) {
					data.append(String.format("wlanip: %s%n", device.getWlanIp()));
					data.append(String.format("wlanmac: %s%n", device.getWlanMac()));
				}
				break;
			case "delete_device":
				data.append(String.format("name: %s%n", device.getName()));
				data.append(String.format("ip: %s%n", device.getIp()));
				data.append(String.format("mac: %s%n", device.getMac()));
				if( ! device.getWlanIp().isEmpty() ) {
					data.append(String.format("wlanip: %s%n", device.getWlanIp()));
					data.append(String.format("wlanmac: %s%n", device.getWlanMac()));
				}
				break;
			}
			break;
		case "de.openschoolserver.dao.Room":
			//TODO
			Room room = (Room)object;
			switch(pluginName){
			case "add_doom":
			case "modify_room":
				break;
			case "delete_room":
				break;
			}
			break;
		}
		OSSShellTools.exec(program, reply, error, data.toString());
		System.err.println(pluginName + " : " + data.toString() + " : " + error);
	}
	
	protected void changeMemberPlugin(String type, Group group, List<User> users){
		//type can be only add or remove
		StringBuilder data = new StringBuilder();
		String[] program   = new String[2];
		StringBuffer reply = new StringBuffer();
		StringBuffer error = new StringBuffer();
		program[0] = "/usr/share/oss/plugins/plugin_handler.sh";
		program[1] = "change_member";
		data.append(String.format("changetype: %s%n",type));
		data.append(String.format("group: %s%n", group.getName()));
		data.append("users: ");
		for( User user : users ) {
			data.append(user.getUid() + " ");
		}
		OSSShellTools.exec(program, reply, error, data.toString());
		System.err.println("change_member  : " + data.toString() + " : " + error);
	}
	
	protected void changeMemberPlugin(String type, Group group, User user){
		//type can be only add or remove
		StringBuilder data = new StringBuilder();
		String[] program   = new String[2];
		StringBuffer reply = new StringBuffer();
		StringBuffer error = new StringBuffer();
		program[0] = "/usr/share/oss/plugins/plugin_handler.sh";
		program[1] = "change_member";
		data.append(String.format("changetype: %s%n",type));
		data.append(String.format("group: %s%n", group.getName()));
		data.append(String.format("user: %s%n", user.getUid()));
		OSSShellTools.exec(program, reply, error, data.toString());
		System.err.println("change_member  : " + data.toString() + " : " + error);
	}
	
}
