/* (c) 2016 EXTIS GmbH - all rights reserved */
package de.openschoolserver.api.resourceimpl;

import de.openschoolserver.api.resources.SessionsResource;
import de.openschoolserver.dao.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.UriInfo;
import java.net.URI;

public class SessionsResourceImpl implements SessionsResource {

    Logger logger = LoggerFactory.getLogger(SessionsResourceImpl.class);

    @Override
    public Session createSession(UriInfo ui, String username, String password, String device) {

        if (username == null || password == null || device == null) {
            throw new WebApplicationException(400);
        }

        final URI uri = ui.getAbsolutePath();
//        final SessionController sessionController = new SessionController(uri.getHost() + ":" + uri.getPort(), null);
//        final Session session = sessionController.createSession(username, password, device);
//
//        if (session == null) {
//            logger.debug("Person authentication failed!");
//            throw new WebApplicationException(401);
//        } else {
//            logger.debug("Person authenticated: " + session.getPerson());
//            return session;
//        }
        return new Session(); //TODO implement
    }

    @Override
    public Session getStatus(Session session) {
      //  logger.debug("Validating session " + session.getToken());
        return session;
    }

    @Override
    public void deleteSession(Session session, String token) {
//        final SessionController sessionController = new SessionController(session);
//
//        Session toBeDeletedSession = sessionController.getByToken(token);
//
//        if (session == null || session.getPersonId() != toBeDeletedSession.getPersonId()) {
//            logger.info("deletion of session denied " + token);
//            throw new WebApplicationException(401);
//        }
//
//        sessionController.deleteSession(toBeDeletedSession);
//        logger.debug("deleted session " + token);
    	
    	//TODO implement
    }

}
