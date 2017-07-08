package de.openschoolserver.api;
import de.openschoolserver.dao.Session;
import de.openschoolserver.dao.User;
import de.openschoolserver.dao.controler.DeviceControler;
import de.openschoolserver.dao.controler.SessionControler;

import java.io.InputStreamReader;

import java.io.BufferedReader;
import java.io.IOException;

public class OssLoggedIn {

    public OssLoggedIn() {
        // TODO Auto-generated constructor stub
    }

    public static void main(String[] args) {
         Session session = new Session();
         SessionControler sessionControler = new SessionControler(session);
         String token = sessionControler.getProperty("de.openschoolserver.api.auth.localhost");
         session = sessionControler.getByToken(token);
         final DeviceControler deviceControler = new DeviceControler(session);
         BufferedReader in = new BufferedReader(new  InputStreamReader(System.in));
         while( true ){
        	 try {
        		 String ip = in.readLine();
        		 if( deviceControler.getLoggedInUsersObject(ip).isEmpty() ) {
        			 System.out.println("ERR user=\"No user logged in " + ip + "\"");
        		 } else {
        			 User user = deviceControler.getLoggedInUsersObject(ip).get(0);
        			 //TODO check internetDisabled
        			 System.out.println("OK user=\"" + user.getUid() + "\"");
        		 }
        	 } catch (IOException e) {
        		 System.err.println("IO ERROR: " + e.getMessage());
        	 }
         }
    }
}
