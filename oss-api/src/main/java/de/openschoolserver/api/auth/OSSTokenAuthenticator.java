/* (c) 2017 Péter Varkoly <peter@varkoly.de> - all rights reserved */
/* (c) 2016 EXTIS GmbH - all rights reserved */
package de.openschoolserver.api.auth;

import java.util.Optional;

import javax.persistence.EntityManager;

import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.openschoolserver.dao.Session;
import de.openschoolserver.dao.controller.SessionController;
import de.openschoolserver.dao.internal.CommonEntityManagerFactory;

public class OSSTokenAuthenticator implements Authenticator<String, Session> {

    Logger logger = LoggerFactory.getLogger(OSSTokenAuthenticator.class);

    @Override
    public Optional<Session> authenticate(String token) throws AuthenticationException {

        logger.debug("Token: " + token);
        EntityManager em = new CommonEntityManagerFactory().getEntityManager(null);
        final SessionController sessionController = new SessionController(em);
        final Session session = sessionController.validateToken(token);
       
        if (session != null) {
            logger.debug("authentication successful!");
            return Optional.of(session);
        } else {
            logger.debug("authentication failed!");
            return Optional.empty();
        }
    }

}
