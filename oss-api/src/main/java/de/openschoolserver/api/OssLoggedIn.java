package de.openschoolserver.api;
import de.openschoolserver.dao.Session;
import de.openschoolserver.dao.User;
import de.openschoolserver.dao.controller.DeviceController;
import de.openschoolserver.dao.controller.SessionController;

import java.io.InputStreamReader;

import java.io.BufferedReader;
import java.io.IOException;

public class OssLoggedIn {

    public OssLoggedIn() {
        // TODO Auto-generated constructor stub
    }

    public static void main(String[] args) {
         Session session = new Session();
         SessionController sessionController = new SessionController(session);
         String token = sessionController.getProperty("de.openschoolserver.api.auth.localhost");
         session = sessionController.getByToken(token);
         final DeviceController deviceController = new DeviceController(session);
         BufferedReader in = new BufferedReader(new  InputStreamReader(System.in));
         while( true ){
        	 try {
        		 String ip = in.readLine();
        		 if( deviceController.getLoggedInUsersObject(ip).isEmpty() ) {
        			 System.out.println("ERR user=\"No user logged in " + ip + "\"");
        		 } else {
        			 User user = deviceController.getLoggedInUsersObject(ip).get(0);
        			 //TODO check internetDisabled
        			 System.out.println("OK user=\"" + user.getUid() + "\"");
        		 }
        	 } catch (IOException e) {
        		 System.err.println("IO ERROR: " + e.getMessage());
        	 }
         }
    }
}
