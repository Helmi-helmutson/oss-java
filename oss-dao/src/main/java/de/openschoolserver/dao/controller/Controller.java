/* (c) 2017 Péter Varkoly <peter@varkoly.de> - all rights reserved */
package de.openschoolserver.dao.controller;

import javax.persistence.EntityManager;


import javax.persistence.Query;
import java.util.List;
import de.openschoolserver.dao.Session;
import de.openschoolserver.dao.internal.CommonEntityManagerFactory;
import de.openschoolserver.dao.controller.Controller;
import de.openschoolserver.dao.controller.Config;
import de.openschoolserver.dao.Group;
import de.openschoolserver.dao.User;
import de.openschoolserver.dao.Device;

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
    
    public boolean isNameUnique(String name){
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
    	return true;
    }
}
