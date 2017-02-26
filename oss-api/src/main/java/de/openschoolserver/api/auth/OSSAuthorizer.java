/* (c) 2017 Péter Varkoly <peter@varkoly.de> - all rights reserved */
/* (c) 2016 EXTIS GmbH - all rights reserved */
package de.openschoolserver.api.auth;


import io.dropwizard.auth.Authorizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.openschoolserver.dao.Session;

import java.util.List;

public class OSSAuthorizer implements Authorizer<Session> {

    Logger logger = LoggerFactory.getLogger(OSSAuthorizer.class);

    @Override
    public boolean authorize(Session session, String requiredRole) {

    	session.getUser().getRole().equals(requiredRole);
        logger.info("authorize() Person: " + session.getUser().getUid() + ", required role category: " + requiredRole);
//
//        List<Role> rolesList = session.getPerson().getRoleItems();
//        if (!rolesList.isEmpty()) {
//            for (Role role : rolesList) {
//                if (requiredRole.equals(role.getCategory())) {
//                    return true;
//                }
//            }
//        }
//
//        return false;
    	return true; //TODO implement
    }

}
