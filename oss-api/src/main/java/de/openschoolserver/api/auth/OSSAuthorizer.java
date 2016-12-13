/* (c) 2016 EXTIS GmbH - all rights reserved */
package de.openschoolserver.api.auth;


import io.dropwizard.auth.Authorizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class OSSAuthorizer implements Authorizer<Session> {

    Logger logger = LoggerFactory.getLogger(OSSAuthorizer.class);

    @Override
    public boolean authorize(Session session, String requiredRole) {

//        logger.info("authorize() Person: " + session.getPerson() + ", required role category: " + requiredRole);
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
