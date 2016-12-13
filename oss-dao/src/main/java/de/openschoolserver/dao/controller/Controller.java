package de.openschoolserver.dao.controller;

import javax.persistence.EntityManager;


import de.openschoolserver.dao.Session;
import de.openschoolserver.dao.internal.CommonEntityManagerFactory;
import de.openschoolserver.dao.controller.Controller;
import de.openschoolserver.dao.Group;
import de.openschoolserver.dao.User;

public class Controller {
	private Session session ;
	private Config  config;
	public Controller(Session session) {
		this.session=session;
		this.config = new Config();
	}
	
    protected EntityManager getEntityManager() {
        return CommonEntityManagerFactory.instance(session.getSchoolId()).getEntityManagerFactory().createEntityManager();
    }
    
    public boolean isNameUnique(String name){
    	EntityManager em = this.getEntityManager();
    	return true;
    }
}
