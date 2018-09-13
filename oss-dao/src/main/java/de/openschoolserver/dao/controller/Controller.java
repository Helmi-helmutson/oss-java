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
			Enumeration<Object> enuKeys = props.keys();
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

	public EntityManager getEntityManager() {
		if( session != null) {
			return CommonEntityManagerFactory.instance(session.getSchoolId()).getEntityManagerFactory().createEntityManager();
		}
		else {
			return CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		}
	}

	public Session getSession() {
		return this.session;
	}

	public String getProperty(String property) {
		if( properties.containsKey(property) ) {
			return properties.get(property);
		}
		return null;
	}
	
	public String getNl() {
		return System.getProperty("line.separator");
	}

	public boolean isNameUnique(String name){
		if( this.getConfigValue("WORKGROUP").equals(name)) {
			return false;
		}
		EntityManager em = this.getEntityManager();
		Query query = em.createNamedQuery("User.getByUid");
		query.setParameter("uid", name);
		List<User> user = (List<User>) query.getResultList();
		if( ! user.isEmpty() ){
			logger.debug("Found user " + user);
			em.close();
			return false;
		}
		query = em.createNamedQuery("Group.getByName");
		query.setParameter("name", name);
		List<Group> group = (List<Group>) query.getResultList();
		if( ! group.isEmpty() ){
			logger.debug("Found group " + group );
			em.close();
			return false;
		}
		query = em.createNamedQuery("Device.getByName");
		query.setParameter("name", name);
		List<Device> device = (List<Device>) query.getResultList();
		if( ! device.isEmpty() ){
			logger.debug("Found device " + device );
			em.close();
			return false;
		}
		query = em.createNamedQuery("Room.getByName");
		query.setParameter("name", name);
		List<Room> room = (List<Room>) query.getResultList();
		if( ! room.isEmpty() ){
			logger.debug("Found room " + room );
			em.close();
			return false;
		}
		em.close();
		return true;
	}
	
	public OssResponse checkPassword(String password) {
		String[] program    = new String[1];
		StringBuffer reply  = new StringBuffer();
		StringBuffer stderr = new StringBuffer();
		program[0] = "/usr/share/oss/tools/check_password_complexity.sh";
		OSSShellTools.exec(program, reply, stderr, password);
		if( ! reply.toString().isEmpty() ) {
			List<String> parameters = new ArrayList<String>();
			String[] error = reply.toString().split("##");
			if( error.length > 1 ) {
				parameters.add(error[1]);
				return new OssResponse(this.getSession(),"ERROR", error[0], null, parameters );
			} else {
				return new OssResponse(this.getSession(),"ERROR",reply.toString());
			}
		}
		return null;
	}

	public boolean checkNonASCII(String name) {
		return ! Pattern.matches("[a-zA-Z0-9\\.\\-_]+",name);
	}

	public boolean checkBadHostName(String name) {
		if( !name.matches("[a-zA-Z0-9\\-]+")) {
			logger.debug("Bad name match '" + name + "'");
			return true;
		}
		return name.matches(".*-wlan");
	}

	public String isMacUnique(String name){
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

	public String isIPUnique(String name){
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

	public void startPlugin(String pluginName, Object object){
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
				data.append(String.format("surName: %s%n", user.getSurName()));
				data.append(String.format("birthDay: %s%n", user.getBirthDay()));
				data.append(String.format("password: %s%n", user.getPassword()));
				data.append(String.format("uid: %s%n", user.getUid()));
				data.append(String.format("role: %s%n", user.getRole()));
				data.append(String.format("fsQuota: %d%n", user.getFsQuota()));
				data.append(String.format("msQuota: %d%n", user.getMsQuota()));
				if( user.isMustChange() ) {
					data.append("mpassword: yes");
				}
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
				data.append(String.format("groupType: %s%n", group.getGroupType()));
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
					data.append(String.format("wlanIp: %s%n", device.getWlanIp()));
					data.append(String.format("wlanMac: %s%n", device.getWlanMac()));
				}
				break;
			case "delete_device":
				data.append(String.format("name: %s%n", device.getName()));
				data.append(String.format("ip: %s%n", device.getIp()));
				data.append(String.format("mac: %s%n", device.getMac()));
				if( ! device.getWlanIp().isEmpty() ) {
					data.append(String.format("wlanIp: %s%n", device.getWlanIp()));
					data.append(String.format("wlanMac: %s%n", device.getWlanMac()));
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
		case "de.cephalix.api.dao.CephalixInstitute":
			try {
				switch(pluginName){
				case "add_institute":
				case "modify_institute":
				case "delete_institue":
					data.append(object);
					break;
				}
			} catch (Exception e) {
				logger.error("pluginHandler : CephalixInstitute:" + e.getMessage());
			}
		}
		OSSShellTools.exec(program, reply, error, data.toString());
		logger.debug(pluginName + " : " + data.toString() + " : " + error);
	}
	
	public void changeMemberPlugin(String type, Group group, List<User> users){
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
	
	public void changeMemberPlugin(String type, Group group, User user){
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

	public boolean isSuperuser() {
		if(properties.containsKey("de.openschoolserver.dao.Session.superusers")){
			for( String s : properties.get("de.openschoolserver.dao.Session.superusers").split(",") ){
				if( s.startsWith("@") ) {
					for( Group g: this.session.getUser().getGroups() ) {
						if( g.getName().equals(s.substring(1))) {
							logger.debug("isSuperuser by group" + s + " " + this.session.getUser().getUid());
							return true;
						}
					}
				} else if( s.equals(this.session.getUser().getUid())) {
					logger.debug("isSuperuser by uid" + this.session.getUser().getUid());
					return true;
				}
			}
		}
		return false;
	}

	public boolean isSuperuser(Session session) {
		if(properties.containsKey("de.openschoolserver.dao.Session.superusers")){
			for( String s : properties.get("de.openschoolserver.dao.Session.superusers").split(",") ){
				if( s.startsWith("@") ) {
					for( Group g: session.getUser().getGroups() ) {
						if( g.getName().equals(s.substring(1))) {
							return true;
						}
					}
				} else if( s.equals(session.getUser().getUid())) {
					return true;
				}
			}
		}
		return false;
	}

	public boolean mayModify(Object object) {
		if( this.session.getUser().getId() == 0 ) {
			return true;
		}
		
		User owner   = null;
		Long ownerId = null;
		switch(object.getClass().getName()) {
		case "de.openschoolserver.dao.Acl":
			Acl Acl = (Acl)object;
			owner = Acl.getCreator();
			break;
		case "de.openschoolserver.dao.AccessInRoom":
			AccessInRoom AccessInRoom = (AccessInRoom)object;
			owner = AccessInRoom.getCreator();
			break;
		case "de.openschoolserver.dao.Announcement":
			Announcement an = (Announcement)object;
			owner = an.getOwner();
			break;
		case "de.openschoolserver.dao.Contact":
			Contact con = (Contact)object;
			owner = con.getOwner();
			break;
		case "de.openschoolserver.dao.FAQ":
			FAQ faq = (FAQ)object;
			owner = faq.getOwner();
			break;
		case "de.openschoolserver.dao.Device":
			Device Device = (Device)object;
			owner = Device.getOwner();
			break;
		case "de.openschoolserver.dao.Group":
			Group group = (Group)object;
			owner = group.getOwner();
			break;
		case "de.openschoolserver.dao.HWConf":
			HWConf HWConf = (HWConf)object;
			owner = HWConf.getCreator();
			break;
		case "de.openschoolserver.dao.OSSConfig":
			OSSConfig ossConfig = (OSSConfig)object;
			owner = ossConfig.getCreator();
			break;
		case "de.openschoolserver.dao.OSSMConfig":
			OSSMConfig ossMConfig = (OSSMConfig)object;
			owner = ossMConfig.getCreator();
			break;
		case "de.openschoolserver.dao.Room":
			Room room = (Room)object;
			owner = room.getCreator();
			break;
		case "de.openschoolserver.dao.RoomSmartControl":
			RoomSmartControl rsc  = (RoomSmartControl)object;
			owner = rsc.getOwner();
			break;
		case "de.openschoolserver.dao.Partition":
			Partition partition = (Partition)object;
			owner = partition.getCreator();
			break;
		case "de.openschoolserver.dao.Software":
			Software software = (Software)object;
			owner = software.getCreator();
			break;
		case "de.openschoolserver.dao.SoftwareLicence":
			SoftwareLicense softwareLicense = (SoftwareLicense)object;
			owner = softwareLicense.getCreator();
			break;
		case "de.openschoolserver.dao.User":
			User user = (User)object;
			ownerId = user.getCreatorId();
			break;
		}
		if( ownerId == null ) {
			ownerId = 1L;
		}
		if( owner != null ) {
			ownerId = owner.getId();
		}
		if( this.isSuperuser() && ownerId != 6 ) {
			//Super User must not delete the objects of CEPHALIX
			//TODO 6 need be evaluated eventually
			return true;
		}
		if( owner != null && this.session.getUser().equals(owner) ) {
				return true;
		}
				
		//TODO some other acls based on object
		return false;
	}

	public boolean isProtected(Object object){
		if (object!=null) {
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
		}
		return false;

	}
	
	public List<String> allowedModules(User user) {
		List<String> modules = new ArrayList<String>();
		//Is it allowed by the groups.
		for( Group group : user.getGroups() ) {
			for( Acl acl : group.getAcls() ) {
				if( acl.getAllowed() ) {
					modules.add(acl.getAcl());
				}
			}
		}
		//Is it allowed by the user
		for( Acl acl : user.getAcls() ){
			if( acl.getAllowed() && !modules.contains(acl.getAcl())) {
				modules.add(acl.getAcl());
			} else if( modules.contains(acl.getAcl()) ) {
				//It is forbidden by the user
				modules.remove(acl.getAcl());
			}
		}
		return modules;
	}
	
	public boolean isAllowed(User user, String acl) {
		return this.allowedModules(user).contains(acl);
	}

	public boolean isAllowed(String acl) {
			return this.isAllowed(session.getUser(), acl);
	}
	
	public boolean systemctl(String action, String service) {
		 String[] program = new String[3];
		 program[0] = "systemctl";
		 program[1] = action;
		 program[2] = service;
		 StringBuffer reply = new StringBuffer();
		 StringBuffer error = new StringBuffer();
		 OSSShellTools.exec(program, reply, error, null);
		 return  error.length() == 0;
	}

	public OSSMConfig getMConfigObject(Object object, String key, String value) {
		Long id = null;
		EntityManager em = this.getEntityManager();
		Query query = em.createNamedQuery("OSSMConfig.check");
		switch(object.getClass().getName()) {
		case "de.openschoolserver.dao.Group":
			 query.setParameter("type","Group");
			 id    = ((Group) object ).getId();
			 break;
		case "de.openschoolserver.dao.User":
			 query.setParameter("type","User");
			 id    = ((User) object ).getId();
			 break;
		case "de.openschoolserver.dao.Room":
			 query.setParameter("type","Room");
			id    = ((Room) object ).getId();
			break;
		case "de.openschoolserver.dao.Device":
			 query.setParameter("type","Device");
			 id    = ((Device) object ).getId();
			 break;
		}
		query.setParameter("id", id).setParameter("keyword", key).setParameter("value", value);
		if( query.getResultList().isEmpty() ) {
			return null;
		}
		return (OSSMConfig) query.getResultList().get(0);
	}
	
	public boolean checkMConfig(Object object, String key, String value) {
		if( this.getMConfigObject(object, key, value) == null ) {
			return false;
		}
		return true;
	}

	public OSSConfig getConfigObject(Object object, String key) {
		Long id = null;
		EntityManager em = this.getEntityManager();
		Query query = em.createNamedQuery("OSSConfig.get");
		switch(object.getClass().getName()) {
		case "de.openschoolserver.dao.Group":
			 query.setParameter("type","Group");
			 id    = ((Group) object ).getId();
			 break;
		case "de.openschoolserver.dao.User":
			 query.setParameter("type","User");
			 id    = ((User) object ).getId();
			 break;
		case "de.openschoolserver.dao.Room":
			query.setParameter("type","Room");
			id    = ((Room) object ).getId();
			break;
		case "de.openschoolserver.dao.Device":
			query.setParameter("type","Device");
			id    = ((Device) object ).getId();
			break;
		}
		query.setParameter("id", id).setParameter("keyword", key);
		if( query.getResultList().isEmpty() ) {
			return null;
		}
		return (OSSConfig) query.getResultList().get(0);
	}

	public boolean checkConfig(Object object, String key) {
		if( this.getConfigObject(object, key) == null ) {
			return false;
		}
		return true;
	}

	public OSSConfig getConfig(String type, String key) {
		OSSConfig     ossConfig = null;
		EntityManager em = this.getEntityManager();
		Query query      = em.createNamedQuery("OSSConfig.getAllByKey");
		query.setParameter("type",type).setParameter("keyword",key);
		if( ! query.getResultList().isEmpty() ) {
			ossConfig = (OSSConfig)  query.getResultList().get(0);
		}
		em.close();
		return ossConfig;
	}

	public List<OSSMConfig> getMConfigs(String key) {
		EntityManager em = this.getEntityManager();
		Query query = em.createNamedQuery("OSSMConfig.getAllForKey");
		query.setParameter("keyword",key);
		List<OSSMConfig> ossMConfigs = (List<OSSMConfig>) query.getResultList();
		em.close();
		return ossMConfigs;
	}

	public List<OSSMConfig> getMConfigs(String type, String key) {
		EntityManager em = this.getEntityManager();
		Query query = em.createNamedQuery("OSSMConfig.getAllByKey");
		query.setParameter("type",type).setParameter("keyword",key);
		List<OSSMConfig> ossMConfigs = (List<OSSMConfig>) query.getResultList();
		em.close();
		return ossMConfigs;
	}

	public List<String> getMConfigs(Object object, String key) {
		Long id = null;
		EntityManager em = this.getEntityManager();
		Query query = em.createNamedQuery("OSSMConfig.get");
		switch(object.getClass().getName()) {
		case "de.openschoolserver.dao.Group":
			 query.setParameter("type","Group");
			 id    = ((Group) object ).getId();
			 break;
		case "de.openschoolserver.dao.User":
			query.setParameter("type","User");
			id    = ((User) object ).getId();
			break;
		case "de.openschoolserver.dao.Room":
			query.setParameter("type","Room");
			id    = ((Room) object ).getId();
			break;
		case "de.openschoolserver.dao.Device":
			query.setParameter("type","Device");
			id    = ((Device) object ).getId();
			break;
		}
		query.setParameter("id", id).setParameter("keyword", key);
		ArrayList<String> values = new ArrayList<String>();
		for(OSSMConfig config : (List<OSSMConfig>) query.getResultList() ) {
			values.add(config.getValue());
		}
		em.close();
		return values;
	}
	
	public StringBuilder getImportDir(String startTime) {
		StringBuilder importDir = new StringBuilder();
		importDir.append(getConfigValue("HOME_BASE")).append("/groups/SYSADMINS/userimports/").append(startTime);
		return importDir;
	}

	public String getConfig(Object object, String key) {
		Long id = null;
		EntityManager em = this.getEntityManager();
		Query query = em.createNamedQuery("OSSConfig.get");
		switch(object.getClass().getName()) {
		case "de.openschoolserver.dao.Group":
			 query.setParameter("type","Group");
			 id    = ((Group) object ).getId();
			 break;
		case "de.openschoolserver.dao.User":
			 query.setParameter("type","User");
			 id    = ((User) object ).getId();
			 break;
		case "de.openschoolserver.dao.Room":
			query.setParameter("type","Room");
			id    = ((Room) object ).getId();
			break;
		case "de.openschoolserver.dao.Device":
			query.setParameter("type","Device");
			id    = ((Device) object ).getId();
			break;
		}
		query.setParameter("id", id).setParameter("keyword", key);
		if( query.getResultList().isEmpty() ) {
			return null;
		}
		OSSConfig config = (OSSConfig) query.getResultList().get(0);
		em.close();
		return config.getValue();
	}

	public OssResponse addMConfig(Object object, String key, String value) {
		if( this.checkMConfig(object, key, value) ){
			return new OssResponse(this.getSession(),"ERROR","This mconfig value already exists.");
		}
		EntityManager em = this.getEntityManager();
		OSSMConfig mconfig = new OSSMConfig();
		switch(object.getClass().getName()) {
		case "de.openschoolserver.dao.Group":
			 mconfig.setObjectType("Group");
			 mconfig.setObjectId(((Group) object ).getId());
			 break;
		case "de.openschoolserver.dao.User":
			mconfig.setObjectType("User");
			mconfig.setObjectId(((User) object ).getId());
			 break;
		case "de.openschoolserver.dao.Room":
			mconfig.setObjectType("Room");
			mconfig.setObjectId(((Room) object ).getId());
			break;
		case "de.openschoolserver.dao.Device":
			mconfig.setObjectType("Device");
			mconfig.setObjectId(((Device) object ).getId());
			break;
		}
		mconfig.setKeyword(key);
		mconfig.setValue(value);
		mconfig.setCreator(this.session.getUser());
		try {
			em.getTransaction().begin();
			em.persist(mconfig);
			em.getTransaction().commit();
		} catch (Exception e) {
			logger.error("addMConfig: " + e.getMessage());
			return new OssResponse(this.getSession(),"ERROR",e.getMessage());
		} finally {
			em.close();
		}
		return new OssResponse(this.getSession(),"OK","Mconfig was created");
	}

	public OssResponse addConfig(Object object, String key, String value) {
		if( this.checkConfig(object, key) ){
			return new OssResponse(this.getSession(),"ERROR","This config already exists.");
		}
		EntityManager em = this.getEntityManager();
		OSSConfig config = new OSSConfig();
		switch(object.getClass().getName()) {
		case "de.openschoolserver.dao.Group":
			 config.setObjectType("Group");
			 config.setObjectId(((Group) object ).getId());
			 break;
		case "de.openschoolserver.dao.User":
			config.setObjectType("User");
			config.setObjectId(((User) object ).getId());
			 break;
		case "de.openschoolserver.dao.Room":
			config.setObjectType("Room");
			config.setObjectId(((Room) object ).getId());
			break;
		case "de.openschoolserver.dao.Device":
			config.setObjectType("Device");
			config.setObjectId(((Device) object ).getId());
			break;
		}
		config.setKeyword(key);
		config.setValue(value);
		config.setCreator(this.session.getUser());
		try {
			em.getTransaction().begin();
			em.persist(config);
			em.getTransaction().commit();
		} catch (Exception e) {
			logger.error("addConfig: " + e.getMessage());
			return new OssResponse(this.getSession(),"ERROR",e.getMessage());
		} finally {
			em.close();
		}
		return new OssResponse(this.getSession(),"OK","Config was created");
	}

	public OssResponse setConfig(Object object, String key, String value) {
		if( ! this.checkConfig(object, key) ){
			return this.addConfig(object, key, value);
		}
		EntityManager em = this.getEntityManager();
		OSSConfig config = this.getConfigObject(object, key);
		try {
			config = em.find(OSSConfig.class, config.getId());
			config.setValue(value);
			em.getTransaction().begin();
			em.merge(config);
			em.getTransaction().commit();
		} catch (Exception e) {
			logger.error("setConfig: " + e.getMessage());
			return new OssResponse(this.getSession(),"ERROR",e.getMessage());
		} finally {
			em.close();
		}
		return new OssResponse(this.getSession(),"OK","Config was updated");
	}

	public OssResponse deleteConfig(Object object, String key) {
		OSSConfig config = this.getConfigObject(object, key);
		if( config == null ) {
			return new OssResponse(this.getSession(),"ERROR","Config does not exists.");
		}
		EntityManager em = this.getEntityManager();
		try {
			em.getTransaction().begin();
			config = em.find(OSSConfig.class, config.getId());
			em.merge(config);
			em.remove(config);
			em.getTransaction().commit();
		} catch (Exception e) {
			logger.error("deleteConfig: " + e.getMessage());
			return new OssResponse(this.getSession(),"ERROR",e.getMessage());
		} finally {
			em.close();
		}
		return new OssResponse(this.getSession(),"OK","Config was deleted");
	}

	public OssResponse deleteMConfig(Object object, String key, String value) {
		OSSMConfig config = this.getMConfigObject(object, key, value);
		if( config == null ) {
			return new OssResponse(this.getSession(),"ERROR","MConfig does not exists.");
		}
		EntityManager em = this.getEntityManager();
		try {
			em.getTransaction().begin();
			config = em.find(OSSMConfig.class, config.getId());
			em.merge(config);
			em.remove(config);
			em.getTransaction().commit();
		} catch (Exception e) {
			logger.error("deleteMConfig: " + e.getMessage());
			return new OssResponse(this.getSession(),"ERROR",e.getMessage());
		} finally {
			em.close();
		}
		return new OssResponse(this.getSession(),"OK","Config was deleted");
	}
}
