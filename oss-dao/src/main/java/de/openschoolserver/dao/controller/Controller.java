/* (c) 2017 Péter Varkoly <peter@varkoly.de> - all rights reserved
 * (c) 2017 EXTIS GmbH - www.extis.de - all rights reserved */
package de.openschoolserver.dao.controller;

import javax.persistence.EntityManager;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.Query;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.*;
import de.openschoolserver.dao.*;
import de.openschoolserver.dao.internal.CommonEntityManagerFactory;
import de.openschoolserver.dao.controller.Config;
import de.openschoolserver.dao.tools.OSSShellTools;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Properties;
import java.util.ArrayList;

@SuppressWarnings( "unchecked" )
public class Controller extends Config {

    Logger logger = LoggerFactory.getLogger(Controller.class);

	protected Session session ;
	private Map<String, String> properties;
	private static String basePath;
	static {
		String os = System.getProperty("os.name", "generic").toLowerCase(Locale.ENGLISH);
		if ((os.indexOf("mac") >= 0) || (os.indexOf("darwin") >= 0)) {
			basePath = "/usr/local/oss/";
		} else {
			basePath = "/usr/share/oss/";
		}
	}

	public Controller(Session session) {
		super();
		this.session=session;
		properties = new HashMap<String, String>();
		try {
			File file = new File("/opt/oss-java/conf/oss-api.properties");
			FileInputStream fileInput = new FileInputStream(file);
			Properties props = new Properties();
			props.load(fileInput);
			fileInput.close();
			Enumeration enuKeys = props.keys();
			while (enuKeys.hasMoreElements()) {
				String key = (String) enuKeys.nextElement();
				String value = props.getProperty(key);
				properties.put(key, value);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	protected EntityManager getEntityManager() {
		if( session != null)
			return CommonEntityManagerFactory.instance(session.getSchoolId()).getEntityManagerFactory().createEntityManager();
		else
			return CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
	}

	protected Session getSession() {
		return this.session;
	}

	public String getProperty(String property) {
		return properties.get(property);
	}

	protected boolean isNameUnique(String name){
		EntityManager em = this.getEntityManager();
		Query query = em.createNamedQuery("User.getByUid");
		query.setParameter("uid", name);
		List<User> user = (List<User>) query.getResultList();
		if( ! user.isEmpty() ){
			em.close();
			return false;
		}
		query = em.createNamedQuery("Group.getByName");
		query.setParameter("name", name);
		List<Group> group = (List<Group>) query.getResultList();
		if( ! group.isEmpty() ){
			em.close();
			return false;
		}
		query = em.createNamedQuery("Device.getByName");
		query.setParameter("name", name);
		List<Device> device = (List<Device>) query.getResultList();
		if( ! device.isEmpty() ){
			em.close();
			return false;
		}
		query = em.createNamedQuery("Room.getByName");
		query.setParameter("name", name);
		List<Room> room = (List<Room>) query.getResultList();
		if( ! room.isEmpty() ){
			em.close();
			return false;
		}
		em.close();
		return true;
	}
	
	protected Response checkPassword(String password) {
		List<String> error = new ArrayList<String>();
		if( password.length() < Integer.parseInt(this.getConfigValue("SCHOOL_MINIMAL_PASSWORD_LENGTH")) ) {
			error.add("User password is to short.");
		}
		if( password.length() > Integer.parseInt(this.getConfigValue("SCHOOL_MAXIMAL_PASSWORD_LENGTH")) ) {
			error.add("User password is to long.");
		}
		if(  this.getConfigValue("SCHOOL_CHECK_PASSWORD_QUALITY") == "yes" ) {
			if( ! Pattern.matches("[A-Z]",password) )
				error.add("User password should contains upper case letters.");
			if(! Pattern.matches("[0-9]",password) )
				error.add("User password should contains numbers.");
			String[] program    = new String[1];
			StringBuffer reply  = new StringBuffer();
			StringBuffer stderr = new StringBuffer();
			program[0] = "/usr/sbin/cracklib-check";
			OSSShellTools.exec(program, reply, stderr, password);
			if( ! reply.toString().startsWith(password + ": OK"))
				error.add(reply.toString());
		}
		if( error.size() > 0 )
			return new Response(this.getSession(),"ERROR", String.join(System.lineSeparator(), error));
		
		return null;
	}

	protected boolean checkNonASCII(String name) {
		return ! Pattern.matches("[a-zA-Z0-9\\.\\-_]+",name);
	}

	protected boolean checkBadHostName(String name) {
		if( !name.matches("[a-zA-Z0-9\\-]+")) {
			logger.debug("Bad name match '" + name + "'");
			return true;
		}
		return name.matches(".*-wlan");
	}

	protected String isMacUnique(String name){
		EntityManager em = this.getEntityManager();
		Query query = em.createNamedQuery("Device.getByMAC");
		query.setParameter("MAC", name);
		List<Device> devices = (List<Device>) query.getResultList();
		em.close();
		if( ! devices.isEmpty() ){
			return devices.get(0).getName();
		}
		return "";
	}

	protected String isIPUnique(String name){
		EntityManager em = this.getEntityManager();
		Query query = em.createNamedQuery("Device.getByIP");
		query.setParameter("IP", name);
		List<Device> devices = (List<Device>) query.getResultList();
		em.close();
		if( ! devices.isEmpty() ){
			return devices.get(0).getName();
		}
		return "";
	}

	protected void startPlugin(String pluginName, Object object){
		StringBuilder data = new StringBuilder();
		String[] program   = new String[2];
		StringBuffer reply = new StringBuffer();
		StringBuffer error = new StringBuffer();
		program[0] = basePath + "plugins/plugin_handler.sh";
		program[1] = pluginName;
		switch(object.getClass().getName()) {
		case "de.openschoolserver.dao.User":
			User user = (User)object;
			switch(pluginName) {
			case "add_user":
			case "modify_user":
				data.append(String.format("givenName: %s%n", user.getGivenName()));
				data.append(String.format("sureName: %s%n", user.getSureName()));
				data.append(String.format("birthDay: %s%n", user.getBirthDay()));
				data.append(String.format("password: %s%n", user.getPassword()));
				data.append(String.format("uid: %s%n", user.getUid()));
				data.append(String.format("role: %s%n", user.getRole()));
				String myGroups = "";
				for(Group g : user.getGroups()) {
					myGroups.concat(g.getName() + " ");
				}
				data.append(String.format("groups: %s%n", myGroups));
				break;
			case "delete_user":
				data.append(String.format("uid: %s%n", user.getUid()));
				break;
			}
			break;
		case "de.openschoolserver.dao.Group":
			//TODO 
			Group group = (Group)object;
			switch(pluginName){
			case "add_group":
			case "modify_group":
				data.append(String.format("name: %s%n", group.getName()));
				data.append(String.format("description: %s%n", group.getDescription()));
				break;
			case "delete_group":
				data.append(String.format("name: %s%n", group.getName()));
				break;
			}
			break;
		case "de.openschoolserver.dao.Device":
			//TODO
			Device device = (Device)object;
			switch(pluginName){
			case "add_device":
			case "modify_device":
				data.append(String.format("name: %s%n", device.getName()));
				data.append(String.format("ip: %s%n", device.getIp()));
				data.append(String.format("mac: %s%n", device.getMac()));
				if( ! device.getWlanIp().isEmpty() ) {
					data.append(String.format("wlanip: %s%n", device.getWlanIp()));
					data.append(String.format("wlanmac: %s%n", device.getWlanMac()));
				}
				break;
			case "delete_device":
				data.append(String.format("name: %s%n", device.getName()));
				data.append(String.format("ip: %s%n", device.getIp()));
				data.append(String.format("mac: %s%n", device.getMac()));
				if( ! device.getWlanIp().isEmpty() ) {
					data.append(String.format("wlanip: %s%n", device.getWlanIp()));
					data.append(String.format("wlanmac: %s%n", device.getWlanMac()));
				}
				break;
			}
			break;
		case "de.openschoolserver.dao.Room":
			//TODO
			Room room = (Room)object;
			switch(pluginName){
			case "add_doom":
			case "modify_room":
				data.append(String.format("name: %s%n", room.getName()));
				data.append(String.format("description: %s%n", room.getDescription()));
				break;
			case "delete_room":
				data.append(String.format("name: %s%n", room.getName()));
				break;
			}
			break;
		}
		OSSShellTools.exec(program, reply, error, data.toString());
		logger.debug(pluginName + " : " + data.toString() + " : " + error);
	}
	
	protected void changeMemberPlugin(String type, Group group, List<User> users){
		//type can be only add or remove
		StringBuilder data = new StringBuilder();
		String[] program   = new String[2];
		StringBuffer reply = new StringBuffer();
		StringBuffer error = new StringBuffer();
		program[0] = basePath + "plugins/plugin_handler.sh";
		program[1] = "change_member";
		data.append(String.format("changeType: %s%n",type));
		data.append(String.format("group: %s%n", group.getName()));
		data.append("users: ");
		for( User user : users ) {
			data.append(user.getUid() + " ");
		}
		OSSShellTools.exec(program, reply, error, data.toString());
		logger.debug("change_member  : " + data.toString() + " : " + error);
	}
	
	protected void changeMemberPlugin(String type, Group group, User user){
		//type can be only add or remove
		StringBuilder data = new StringBuilder();
		String[] program   = new String[2];
		StringBuffer reply = new StringBuffer();
		StringBuffer error = new StringBuffer();
		program[0] = basePath + "plugins/plugin_handler.sh";
		program[1] = "change_member";
		data.append(String.format("changeType: %s%n",type));
		data.append(String.format("group: %s%n", group.getName()));
		data.append(String.format("users: %s%n", user.getUid()));
		OSSShellTools.exec(program, reply, error, data.toString());
		logger.debug("change_member  : " + data.toString() + " : " + error);
	}

	protected boolean isSuperuser() {
		if(properties.containsKey("de.openschoolserver.dao.Session.superusers")){
			for( String s : properties.get("de.openschoolserver.dao.Session.superusers").split(",") ){
				if( s.equals(this.session.getUser().getUid())) {
					return true;
				}
			}
		}
		return false;
	}

	protected boolean isProtected(Object object){
		switch(object.getClass().getName()) {
		case "de.openschoolserver.dao.User":
			if(properties.containsKey("de.openschoolserver.dao.User.protected")){
				User u = (User)object;
				for( String s : properties.get("de.openschoolserver.dao.User.protected").split(",") ){
					if( s.equals(u.getUid()))
						return true;
				}
			}
			return false;
		case "de.openschoolserver.dao.Room":
			if(properties.containsKey("de.openschoolserver.dao.Room.protected")){
				Room r = (Room) object;
				for( String s : properties.get("de.openschoolserver.dao.Room.protected").split(",") ){
					if( s.equals(r.getName()))
						return true;
				}
			}
			return false;
		case "de.openschoolserver.dao.Device":
			if(properties.containsKey("de.openschoolserver.dao.Device.protected")){
				Device d = (Device)object;
				for( String s : properties.get("de.openschoolserver.dao.Device.protected").split(",") ){
					if( s.equals(d.getName()))
						return true;
				}
			}
			return false;
		case "de.openschoolserver.dao.Group":
			if(properties.containsKey("de.openschoolserver.dao.Group.protected")){
				Group g = (Group)object;
				for( String s : properties.get("de.openschoolserver.dao.Group.protected").split(",") ){
					if( s.equals(g.getName()))
						return true;
				}
			}
			return false;
		case "de.openschoolserver.dao.HWConf":
			if(properties.containsKey("de.openschoolserver.dao.HWConf.protected")){
				HWConf hw = (HWConf)object;
				for( String s : properties.get("de.openschoolserver.dao.HWConf.protected").split(",") ){
					if( s.equals(hw.getName()))
						return true;
				}
			}
			return false;
		}
		return false;
	}
	
	protected boolean systemctl(String action, String service) {
		 String[] program = new String[3];
		 program[0] = "systemctl";
		 program[1] = action;
		 program[2] = service;
		 StringBuffer reply = new StringBuffer();
		 StringBuffer error = new StringBuffer();
		 OSSShellTools.exec(program, reply, error, null);
		 return  error.length() == 0;
	}
	
	public boolean checkMConfig(Object object, String key, String value) {
		Long id = null;
		EntityManager em = this.getEntityManager();
		Query query = null;
		switch(object.getClass().getName()) {
		case "de.openschoolserver.dao.User":
			 query = em.createNamedQuery("User.getMConfig");
			 id    = ((User) object ).getId();
			 break;
		case "de.openschoolserver.dao.Room":
			query = em.createNamedQuery("Room.getMConfig");
			id    = ((Room) object ).getId();
			break;
		case "de.openschoolserver.dao.Device":
			 query = em.createNamedQuery("Device.getMConfig");
			 id    = ((Device) object ).getId();
			 break;
		}
		query.setParameter("id", id).setParameter("keyword", key).setParameter("value", value);
		return ! query.getResultList().isEmpty();
	}
	
	public boolean checkConfig(Object object, String key, String value) {
		Long id = null;
		EntityManager em = this.getEntityManager();
		Query query = null;
		switch(object.getClass().getName()) {
		case "de.openschoolserver.dao.User":
			 query = em.createNamedQuery("User.getConfig");
			 id    = ((User) object ).getId();
			 break;
		case "de.openschoolserver.dao.Room":
			query = em.createNamedQuery("Room.getConfig");
			id    = ((Room) object ).getId();
			break;
		case "de.openschoolserver.dao.Device":
			 query = em.createNamedQuery("Device.getConfig");
			 id    = ((Device) object ).getId();
			 break;
		}
		query.setParameter("id", id).setParameter("keyword", key).setParameter("value", value);
		return ! query.getResultList().isEmpty();
	}
	
	public List<String> getMConfig(Object object, String key) {
		Long id = null;
		EntityManager em = this.getEntityManager();
		Query query = null;
		switch(object.getClass().getName()) {
		case "de.openschoolserver.dao.User":
			 query = em.createNamedQuery("User.getMConfig");
			 id    = ((User) object ).getId();
			 break;
		case "de.openschoolserver.dao.Room":
			query = em.createNamedQuery("Room.getMConfig");
			id    = ((Room) object ).getId();
			break;
		case "de.openschoolserver.dao.Device":
			 query = em.createNamedQuery("Device.getMConfig");
			 id    = ((Device) object ).getId();
			 break;
		}
		query.setParameter("id", id).setParameter("keyword", key);
		return (List<String>) query.getResultList();
	}
	
	public String getConfig(Object object, String key) {
		Long id = null;
		EntityManager em = this.getEntityManager();
		Query query = null;
		switch(object.getClass().getName()) {
		case "de.openschoolserver.dao.User":
			 query = em.createNamedQuery("User.getConfig");
			 id    = ((User) object ).getId();
			 break;
		case "de.openschoolserver.dao.Room":
			query = em.createNamedQuery("Room.getConfig");
			id    = ((Room) object ).getId();
			break;
		case "de.openschoolserver.dao.Device":
			 query = em.createNamedQuery("Device.getConfig");
			 id    = ((Device) object ).getId();
			 break;
		}
		query.setParameter("id", id).setParameter("keyword", key);
		if( query.getResultList().isEmpty() ) {
			return null;
		}
		return (String) query.getResultList().get(0);
	}
}
