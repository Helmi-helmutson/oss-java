package de.openschoolserver.dao.controller;

import javax.persistence.EntityManager;

import de.openschoolserver.dao.Session;
import de.openschoolserver.dao.internal.CommonEntityManagerFactory;

public class Controller {
	private Session session ;
	public Controller(Session session) {
		this.session=session;
	}
	
    protected EntityManager getEntityManager() {
        return CommonEntityManagerFactory.instance(session.getSchoolId()).getEntityManagerFactory().createEntityManager();
    }
    
}
