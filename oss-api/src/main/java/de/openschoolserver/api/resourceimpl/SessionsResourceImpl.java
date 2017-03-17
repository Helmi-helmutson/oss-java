/* (c) 2017 Péter Varkoly <peter@varkoly.de> - all rights reserved */
/* (c) 2016 EXTIS GmbH - all rights reserved */
package de.openschoolserver.api.resourceimpl;

import de.openschoolserver.api.resources.SessionsResource;


import de.openschoolserver.dao.Session;
import de.openschoolserver.dao.controller.SessionController;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.UriInfo;
import java.net.URI;

public class SessionsResourceImpl implements SessionsResource {
	
    Logger logger = LoggerFactory.getLogger(SessionsResourceImpl.class);
    
    @Override
    public Session createSession(UriInfo ui, String username, String password, String device, HttpServletRequest req) {

        if (username == null || password == null ) {
            throw new WebApplicationException(400);
        }
        if( device == null)
        	device = "dummy";

        Session session =  new Session();
        session.setIP(req.getRemoteAddr());
        SessionController sessionController = new SessionController(session);
        return sessionController.createSessionWithUser(username, password, device);
    }

    @Override
    public Session getStatus(Session session) {
        return session;
    }

    @Override
    public void deleteSession(Session session, String token) {
         final SessionController sessionController = new SessionController(session);
         if( session == null || ! session.getToken().equals(token) ) {
        	 logger.info("deletion of session denied " + token);
        	 throw new WebApplicationException(401);
         }
         sessionController.deleteSession(session);
         logger.debug("deleted session " + token);
    }
}
