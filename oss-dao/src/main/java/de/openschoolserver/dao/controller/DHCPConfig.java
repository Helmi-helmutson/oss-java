/* (c) 2017 Péter Varkoly <peter@varkoly.de> - all rights reserved */
package de.openschoolserver.dao.controller;

import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;

import de.openschoolserver.dao.Room;
import de.openschoolserver.dao.Device;
import de.openschoolserver.dao.Session;
import de.openschoolserver.dao.tools.*;
import javax.persistence.EntityManager;
import javax.persistence.Query;

public class DHCPConfig extends Controller {
	
	private Path DHCP_CONFIG   = Paths.get("/etc/dhcpd.conf");
	private Path DHCP_TEMPLATE = Paths.get("/usr/share/oss/templates/dhcpd.conf");
	private List<String>       dhcpConfigFile;
	private List<String>       dhcpConfigFileTemplate;
	
	public DHCPConfig(Session session) {
		super(session);
		try {
			dhcpConfigFileTemplate = Files.readAllLines(DHCP_TEMPLATE);
		}
		catch( IOException e ) { 
			e.printStackTrace();
		}
	}
	
	public void Create() {
		EntityManager em = getEntityManager();
		dhcpConfigFile.addAll(dhcpConfigFileTemplate);
		
		//Let us start with the SCHOOL_NETWORK
		IPv4Net network = new IPv4Net(this.getConfigValue("SCHOOL_NETWORK") + "/" + 
		                              this.getConfigValue("SCHOOL_NETMASK")
				                     );
		dhcpConfigFile.add("subnet " + this.getConfigValue("SCHOOL_NETWORK") + " netmask " + this.getConfigValue("SCHOOL_NETMASK") + " {");
		dhcpConfigFile.add("  pool {");
		dhcpConfigFile.add("    range dynamic-bootp " + this.getConfigValue("SCHOOL_ANON_DHCP_RANGE") + " ;");
		dhcpConfigFile.add("    allow unknown clients;");
		dhcpConfigFile.add("    deny  known clients;");
		dhcpConfigFile.add("    default-lease-time 300;");
		dhcpConfigFile.add("    max-lease-time 600;");
		dhcpConfigFile.add("  }");
		Query query = em.createNamedQuery("Room.findAll");
		for( Room room : (List<Room>) query.getResultList() ) {
			Query subQuery = em.createNamedQuery("Room.getDeviceCount");
			subQuery.setParameter("id", room.getId());
			int deviceCount = (Integer) subQuery.getSingleResult();
			if( deviceCount < 1)
				continue;
			if( !network.contains(room.getStartIP()) ) 
				continue;
			dhcpConfigFile.add("  group {");
			WriteRoom(room);
			dhcpConfigFile.add("  }");
		}
		dhcpConfigFile.add("}");
		
		// Now we are writing the shared networks
		for( String shared : this.getConfigValue("SCHOOL_SHARED_NETWORKS").split(" ")){
			String[] n = shared.split(",");
			network = new IPv4Net(n[0]);
			dhcpConfigFile.add("subnet " + network.getBase() + " netmask " + network.getNetmask() + " {");
			dhcpConfigFile.add("  option routers " + n[1] + ";");
			query = em.createNamedQuery("Room.findAll");
			for( Room room : (List<Room>) query.getResultList() ) {
				Query subQuery = em.createNamedQuery("Room.getDeviceCount");
				subQuery.setParameter("id", room.getId());
				int deviceCount = (Integer) subQuery.getSingleResult();
				if( deviceCount < 1)
					continue;
				if( !network.contains(room.getStartIP()) ) 
					continue;
				dhcpConfigFile.add("  group {");
				WriteRoom(room);
				dhcpConfigFile.add("  }");
			}
			dhcpConfigFile.add("}");	
		}
		try {
		    Files.write(DHCP_CONFIG, dhcpConfigFile );
		    String[] program = new String[3];
		    //TODO write a own class for controlling systemctl
		    program[0] = "systemctl";
		    program[1] = "restart";
		    program[2] = "dhcpd";
		    StringBuffer reply = new StringBuffer();
			StringBuffer error = new StringBuffer();
			OSSShellTools.exec(program, reply, error, null);
		}
		catch( IOException e ) { 
			e.printStackTrace();
		}
	}
	
	private void WriteRoom(Room room) {
		for( Device device : room.getDevices() ){
			dhcpConfigFile.add("      host " + device.getName() + " {");
			dhcpConfigFile.add("        hardware ethernet " + device.getMac() + ";");
			dhcpConfigFile.add("        fixed-address " + device.getIp() + ";");
			dhcpConfigFile.add("      }");
			//TODO Additional DHCP parameter
			if( IPv4.validateIPAddress(device.getWlanIp()) ){
				dhcpConfigFile.add("    host " + device.getName() + "-wlan {");
				dhcpConfigFile.add("      hardware ethernet " + device.getWlanMac() + ";");
				dhcpConfigFile.add("      fixed-address " + device.getWlanIp() + ";");
				dhcpConfigFile.add("    }");	
				//TODO Additional DHCP parameter
			}
		}
	}
}
