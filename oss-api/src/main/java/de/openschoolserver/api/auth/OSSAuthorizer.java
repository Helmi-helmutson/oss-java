/* (c) 2017 Péter Varkoly <peter@varkoly.de> - all rights reserved */
/* (c) 2016 EXTIS GmbH - all rights reserved */
package de.openschoolserver.api.auth;


import io.dropwizard.auth.Authorizer;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.openschoolserver.dao.Session;
import de.openschoolserver.dao.controler.SessionControler;

public class OSSAuthorizer implements Authorizer<Session> {

    Logger logger = LoggerFactory.getLogger(OSSAuthorizer.class);

    @Override
    public boolean authorize(Session session, String requiredRole) {

        logger.info("authorize() Person: " + session.getUser().getUid() + ", required role category: " + requiredRole);
        final SessionControler sessionControler = new SessionControler();
        return sessionControler.authorize(session, requiredRole);
    }

}
