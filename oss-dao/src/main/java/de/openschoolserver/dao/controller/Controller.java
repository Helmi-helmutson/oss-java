package de.openschoolserver.dao.controller;

import javax.persistence.EntityManager;


import de.openschoolserver.dao.Session;
import de.openschoolserver.dao.internal.CommonEntityManagerFactory;
import de.openschoolserver.dao.controller.Controller;
import de.openschoolserver.dao.controller.Config;
import de.openschoolserver.dao.Group;
import de.openschoolserver.dao.User;

public class Controller extends Config {
	private Session session ;
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
    	return true;
    }
}
