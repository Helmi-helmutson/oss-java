package de.openschoolserver.api;
import de.openschoolserver.dao.controller.DeviceController;
import de.openschoolserver.dao.controller.SessionController;
import de.openschoolserver.dao.Session;
import java.util.Scanner;

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
         Scanner sc = new Scanner(System.in);
         while( sc.hasNextLine() ){
             String ip = sc.nextLine();
             if( deviceController.getLoggedInUsers(ip).isEmpty() )
                 System.out.println("no");
             else
                 System.out.println(deviceController.getLoggedInUsers(ip).get(0));
         }
         sc.close();
    }

}
