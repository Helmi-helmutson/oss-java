/* (c) 2017 Péter Varkoly <peter@varkoly.de> - all rights reserved */
/* (c) 2016 EXTIS GmbH - all rights reserved */
package de.openschoolserver.api.auth;


import io.dropwizard.auth.Authorizer;

import javax.persistence.EntityManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.openschoolserver.dao.Session;
import de.openschoolserver.dao.controller.SessionController;
import de.openschoolserver.dao.internal.CommonEntityManagerFactory;

public class OSSAuthorizer implements Authorizer<Session> {

	Logger logger = LoggerFactory.getLogger(OSSAuthorizer.class);

	@Override
	public boolean authorize(Session session, String requiredRole) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		logger.debug("authorize() Person: " + session.getUser().getUid() + ", required role category: " + requiredRole);
		final SessionController sessionController = new SessionController(em);
		boolean result = sessionController.authorize(session, requiredRole);
		logger.debug("result " + result);
		em.close();
		logger.debug("em closed.");
		return result;
	}
}
