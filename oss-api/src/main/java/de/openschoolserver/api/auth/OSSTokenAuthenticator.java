/* (c) 2017 Péter Varkoly <peter@varkoly.de> - all rights reserved */
/* (c) 2016 EXTIS GmbH - all rights reserved */
package de.openschoolserver.api.auth;

import java.util.Optional;


import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.openschoolserver.dao.Session;
import de.openschoolserver.dao.controller.SessionController;

public class OSSTokenAuthenticator implements Authenticator<String, Session> {

    Logger logger = LoggerFactory.getLogger(OSSTokenAuthenticator.class);

    @Override
    public Optional<Session> authenticate(String token) throws AuthenticationException {

        logger.debug("Token: " + token);
        
        final SessionController sessionController = new SessionController();
        final Session session = sessionController.getByToken(token);
       
        if (session != null) {
            logger.debug("authentication successful!");
            return Optional.of(session);
        } else {
            logger.debug("authentication failed!");
            return Optional.empty();
        }

    }

}
