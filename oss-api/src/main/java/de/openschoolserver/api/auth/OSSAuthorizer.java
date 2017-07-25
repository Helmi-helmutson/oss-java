/* (c) 2017 Péter Varkoly <peter@varkoly.de> - all rights reserved */
/* (c) 2016 EXTIS GmbH - all rights reserved */
package de.openschoolserver.api.auth;


import io.dropwizard.auth.Authorizer;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.openschoolserver.dao.Session;
import de.openschoolserver.dao.controller.SessionController;

public class OSSAuthorizer implements Authorizer<Session> {

    Logger logger = LoggerFactory.getLogger(OSSAuthorizer.class);

    @Override
    public boolean authorize(Session session, String requiredRole) {

        logger.info("authorize() Person: " + session.getUser().getUid() + ", required role category: " + requiredRole);
        final SessionController sessionController = new SessionController();
        return sessionController.authorize(session, requiredRole);
    }

}
