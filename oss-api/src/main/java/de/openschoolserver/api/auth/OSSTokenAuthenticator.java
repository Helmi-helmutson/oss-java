package de.openschoolserver.api.auth;

import java.util.Optional;

import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;
import de.openschoolserver.api.auth.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OSSTokenAuthenticator implements Authenticator<String, Session> {

    Logger logger = LoggerFactory.getLogger(OSSTokenAuthenticator.class);

    @Override
    public Optional<Session> authenticate(String token) throws AuthenticationException {

        logger.debug("Token: " + token);

//        final SessionController sessionController = new SessionController(token);
//        final Session session = sessionController.validateToken(token);
       Session session = new Session(); //TODO implement

        if (session != null) {
            logger.debug("authentication successful!");
            return Optional.of(session);
        } else {
            logger.debug("authentication failed!");
            return Optional.empty();
        }

    }

}
