/* (c) 2020 Péter Varkoly <peter@varkoly.de> - all rights reserved */
package de.openschoolserver.api;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

import org.apache.http.client.fluent.* ;

public class OssSquidSSO implements Runnable {

	
	private String Token = "";
	
	public OssSquidSSO(String token) {
		this.Token = token;
	}

	@Override
	public void run() {
		BufferedReader in = new BufferedReader(new  InputStreamReader(System.in));
		while( true ){
			String result = "";
			try {
				String ip = in.readLine();
				String url = "http://localhost:9080/api/devices/loggedIn/" + ip;
				try {
					result = Request.Get(url)
							.addHeader("Authorization",this.Token)
							.execute().returnContent().asString();
				} catch (Exception e) {
					System.out.println("ERR user=\"Server Error\"");
					continue;
				}
				if( result.isEmpty() ) {
					System.out.println("ERR user=\"No user logged in " + ip + "\"");

				} else {
					System.out.println("OK user=\"" + result+ "\"");
				}
			} catch (IOException e) {
				System.err.println("ERROR: " + e.getMessage());
			}
		}
	}

	public static void main(String[] args) {
		String tmp = "";
		try {
			File file = new File("/opt/oss-java/conf/oss-api.properties");
			FileInputStream fileInput = new FileInputStream(file);
			Properties props = new Properties();
			props.load(fileInput);
			fileInput.close();
			tmp = "Bearer " + props.getProperty("de.openschoolserver.api.auth.localhost");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		new OssSquidSSO(tmp).run();
	}

}

