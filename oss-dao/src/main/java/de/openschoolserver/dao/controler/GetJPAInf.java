package de.openschoolserver.dao.controler;

import de.openschoolserver.dao.Session;



import java.io.IOException;
import java.io.PrintWriter;
import java.lang.Runnable;
import java.net.Socket;
import de.openschoolserver.dao.User;
import java.util.Scanner;


public class GetJPAInf extends Controler implements Runnable {
	private Socket client;

	public GetJPAInf(Session session,Socket client) {
		super(session);
		this.client = client;
	}

	public void run(){
		final DeviceControler deviceControler = new DeviceControler(session);
		String ip;
	//	BufferedReader in = null;
		Scanner in = null;
		PrintWriter out = null;
		try{
	//in = new BufferedReader(new )
	//				InputStreamReader(client.getInputStream()));
			in  = new Scanner(client.getInputStream());
			out = new 
					PrintWriter(client.getOutputStream(), true);
		} catch (IOException e) {
			System.out.println("in or out failed");
			in.close();
			System.exit(-1);
		}

		while(true){
				ip = in.nextLine();
				 if( deviceControler.getLoggedInUsersObject(ip).isEmpty() ) {
	                 out.println("ERR user=\"No user logged in " + ip + "\"");
	             } else {
	            	 User user = deviceControler.getLoggedInUsersObject(ip).get(0);
	            	 //TODO check internetDisabled
	                 out.println("OK user=\"" + user.getUid() + "\"");
	             }
		}
	}
}
