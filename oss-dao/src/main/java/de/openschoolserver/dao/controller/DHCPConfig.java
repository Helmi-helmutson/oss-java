/* (c) 2017 Péter Varkoly <peter@varkoly.de> - all rights reserved */
package de.openschoolserver.dao.controller;

import java.io.IOException;


import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.ArrayList;


import de.openschoolserver.dao.Room;
import de.openschoolserver.dao.Device;
import de.openschoolserver.dao.Session;
import de.openschoolserver.dao.HWConf;
import de.openschoolserver.dao.tools.*;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings( "unchecked" )
public class DHCPConfig extends Controller {
	private static Logger LOG = LoggerFactory.getLogger(DHCPConfig.class);
	private Path DHCP_CONFIG   = Paths.get("/etc/dhcpd.conf");
	private Path SALT_GROUPS   = Paths.get("/etc/salt/master.d/groups.conf");
	private Path DHCP_TEMPLATE = Paths.get("/usr/share/oss/templates/dhcpd.conf");
	private List<String>       dhcpConfigFile;
	private List<String>       saltGroupFile;
	final String domainName     = "." + getConfigValue("DOMAIN");
	
	public DHCPConfig(Session session) {
		super(session);
		try {
			try {
				dhcpConfigFile = Files.readAllLines(DHCP_TEMPLATE);
			} catch (java.nio.file.NoSuchFileException e) {
				LOG.error(e.getMessage());
				dhcpConfigFile = new ArrayList<String>();
			}
			saltGroupFile  = new ArrayList<String>();
		}
		catch( IOException e ) { 
			e.printStackTrace();
		}
	}
	
	public void Create() {
		EntityManager em = getEntityManager();
		Query query = em.createNamedQuery("Room.findAll");
		saltGroupFile.add("nodegroups:");
		for( Room room : (List<Room>) query.getResultList() ) {
			Query subQuery = em.createNamedQuery("Room.getDeviceCount");
			subQuery.setParameter("id", room.getId());
			if( (Long) subQuery.getSingleResult() < 1) {
				continue;
			}
			dhcpConfigFile.add("group {");
			dhcpConfigFile.add("  #Room" + room.getName());
			//TODO add dhcp options and statements from RoomConfig
			for( String dhcpstatement : this.getMConfigs(room, "dhcpStatements")) {
				dhcpConfigFile.add("    " + dhcpstatement + " ;");
			}
			for( String dhcpOption : this.getMConfigs(room, "dhcpOptions")) {
				dhcpConfigFile.add("    option " + dhcpOption + " ;");
			}
			WriteRoom(room);
			dhcpConfigFile.add("}");
		}
		try {
			Files.write(DHCP_CONFIG, dhcpConfigFile );
			if( this.getConfigValue("USE_DHCP").equals("yes") ) {
			    this.systemctl("try-restart", "dhcpd");
			}
			//Build groups by hwconf
			query = em.createNamedQuery("HWConf.findAll");
			for( HWConf hwconf : (List<HWConf>) query.getResultList() ) {
				List<String> line = new ArrayList<String>();
				for( Device device : hwconf.getDevices() ) {
					line.add(device.getName() + domainName);
				}
				if(!line.isEmpty()) {
					saltGroupFile.add("  hwconf-" + hwconf.getName() + ": 'L@" + String.join(",",line) + "'");
				}
			}
			Files.write(SALT_GROUPS, saltGroupFile );
			this.systemctl("try-restart", "salt-master");
			this.systemctl("try-restart", "oss_salt_event_watcher");
		} catch( IOException e ) { 
			e.printStackTrace();
		} finally { 
			em.close();
		}
	}

	private void WriteRoom(Room room) {
		List<String> line = new ArrayList<String>();
		for( Device device : room.getDevices() ){
			//Do not create configuration for devices without mac adress.
			if( device.getMac().isEmpty() )
				continue;
			
			line.add(device.getName() + domainName);
			dhcpConfigFile.add("    host " + device.getName() + " {");
			dhcpConfigFile.add("      hardware ethernet " + device.getMac() + ";");
			dhcpConfigFile.add("      fixed-address " + device.getIp() + ";");
			//TODO add dhcp options and statements from DeviceConfif
			for( String dhcpstatement : this.getMConfigs(device, "dhcpStatements")) {
				dhcpConfigFile.add("      " + dhcpstatement + " ;");
			}
			for( String dhcpOption : this.getMConfigs(device, "dhcpOptions")) {
				dhcpConfigFile.add("      option " + dhcpOption + " ;");
			}
			dhcpConfigFile.add("    }");
			if( IPv4.validateIPAddress(device.getWlanIp()) ){
				dhcpConfigFile.add("    host " + device.getName() + "-wlan {");
				dhcpConfigFile.add("      hardware ethernet " + device.getWlanMac() + ";");
				dhcpConfigFile.add("      fixed-address " + device.getWlanIp() + ";");
				for( String dhcpstatement : this.getMConfigs(room, "dhcpStatements")) {
					dhcpConfigFile.add("    " + dhcpstatement + " ;");
				}
				for( String dhcpOption : this.getMConfigs(device, "dhcpOptions")) {
					dhcpConfigFile.add("      option " + dhcpOption + " ;");
				}
				dhcpConfigFile.add("    }");
				line.add(device.getName() + "-wlan"+ domainName );
			}
		}
		if( !line.isEmpty()) {
			saltGroupFile.add("  room-" + room.getName() + ": 'L@" + String.join(",",line) + "'");
		}
	}
}
