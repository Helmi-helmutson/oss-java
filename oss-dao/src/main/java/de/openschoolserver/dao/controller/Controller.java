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
import de.openschoolserver.dao.controller.Config;
import de.openschoolserver.dao.internal.CommonEntityManagerFactory;
import de.openschoolserver.dao.tools.OSSShellTools;
import static de.openschoolserver.dao.internal.OSSConstants.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.ArrayList;

@SuppressWarnings( "unchecked" )
public class Controller extends Config {

    Logger logger = LoggerFactory.getLogger(Controller.class);

	protected Session session ;
	protected EntityManager em;
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

	public Controller(Session session,EntityManager em) {
		super();
		this.session=session;
		this.em     = em;
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

	public String createRandomPassword()
	{
		String[] salt = new String[3];
	    salt[0] = "ABCDEFGHIJKLMNOPQRSTVWXYZ";
	    salt[1] = "1234567890";
	    salt[2] = "abcdefghijklmnopqrstvwxyz";
	    Random rand = new Random();
	    StringBuilder builder = new StringBuilder();
	    int saltIndex  = 2;
	    int beginIndex = Math.abs(rand.nextInt() % salt[saltIndex].length());
	    builder.append(salt[saltIndex].substring(beginIndex, beginIndex + 1));
	    saltIndex  = 1;
	    beginIndex = Math.abs(rand.nextInt() % salt[saltIndex].length());
	    builder.append(salt[saltIndex].substring(beginIndex, beginIndex + 1));
	    saltIndex  = 0;
	    beginIndex = Math.abs(rand.nextInt() % salt[saltIndex].length());
	    builder.append(salt[saltIndex].substring(beginIndex, beginIndex + 1));
	    for (int i = 3; i < 8; i++)
	    {
	      saltIndex  = Math.abs(rand.nextInt() % 3 );
	      beginIndex = Math.abs(rand.nextInt() % salt[saltIndex].length());
	      builder.append(salt[saltIndex].substring(beginIndex, beginIndex + 1));
	    }
	    return builder.toString();
	}

	public Session getSession() {
		return this.session;
	}

	public String getProperty(String property) {
		if( properties.containsKey(property) ) {
			return properties.get(property);
		}
		return "";
	}

	public String getNl() {
		return System.getProperty("line.separator");
	}

	public boolean isNameUnique(String name){
		if( this.getConfigValue("WORKGROUP").equals(name)) {
			return false;
		}
		Query query = this.em.createNamedQuery("User.getByUid");
		query.setParameter("uid", name);
		List<User> user = (List<User>) query.getResultList();
		if( ! user.isEmpty() ){
			logger.debug("Found user " + user);
			return false;
		}
		query = this.em.createNamedQuery("Group.getByName");
		query.setParameter("name", name);
		List<Group> group = (List<Group>) query.getResultList();
		if( ! group.isEmpty() ){
			logger.debug("Found group " + group );
			return false;
		}
		query = this.em.createNamedQuery("Device.getByName");
		query.setParameter("name", name);
		List<Device> device = (List<Device>) query.getResultList();
		if( ! device.isEmpty() ){
			logger.debug("Found device " + device );
			return false;
		}
		query = this.em.createNamedQuery("Room.getByName");
		query.setParameter("name", name);
		List<Room> room = (List<Room>) query.getResultList();
		if( ! room.isEmpty() ){
			logger.debug("Found room " + room );
			return false;
		}
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
		Query query = this.em.createNamedQuery("Device.getByMAC");
		query.setParameter("MAC", name);
		List<Device> devices = (List<Device>) query.getResultList();
		if( ! devices.isEmpty() ){
			return devices.get(0).getName();
		}
		return "";
	}

	public String isIPUnique(String name){
		Query query = this.em.createNamedQuery("Device.getByIP");
		query.setParameter("IP", name);
		List<Device> devices = (List<Device>) query.getResultList();
		if( ! devices.isEmpty() ){
			return devices.get(0).getName();
		}
		return "";
	}

	public boolean isUserAliasUnique(String name){
		Query query = this.em.createNamedQuery("Alias.getByName");
		query.setParameter("alias", name);
		boolean result = query.getResultList().isEmpty();
		return result;
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
			String myGroups = "";
			for(Group g : user.getGroups()) {
				myGroups.concat(g.getName() + " ");
			}
			switch(pluginName) {
			case "add_user":
			case "modify_user":
				data.append(String.format("givenName: %s%n", user.getGivenName()));
				data.append(String.format("surName: %s%n", user.getSurName()));
				data.append(String.format("birthDay: %s%n", user.getBirthDay()));
				data.append(String.format("password: %s%n", user.getPassword()));
				data.append(String.format("uid: %s%n", user.getUid()));
				data.append(String.format("uuid: %s%n", user.getUuid()));
				data.append(String.format("role: %s%n", user.getRole()));
				data.append(String.format("fsQuota: %d%n", user.getFsQuota()));
				data.append(String.format("msQuota: %d%n", user.getMsQuota()));
				if( user.isMustChange() ) {
					data.append("mpassword: yes");
				}
				data.append(String.format("groups: %s%n", myGroups));
				break;
			case "delete_user":
				data.append(String.format("uid: %s%n", user.getUid()));
				data.append(String.format("uuid: %s%n", user.getUuid()));
				data.append(String.format("role: %s%n", user.getRole()));
				data.append(String.format("groups: %s%n", myGroups));
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
				data.append(String.format("grouptype: %s%n", group.getGroupType()));
				break;
			case "delete_group":
				data.append(String.format("name: %s%n", group.getName()));
				break;
			}
			break;
		case "de.openschoolserver.dao.Device":
			Device device = (Device)object;
			data.append(String.format("name: %s%n", device.getName()));
			data.append(String.format("ip: %s%n", device.getIp()));
			data.append(String.format("mac: %s%n", device.getMac()));
			if( ! device.getWlanIp().isEmpty() ) {
				data.append(String.format("wlanip: %s%n", device.getWlanIp()));
				data.append(String.format("wlanmac: %s%n", device.getWlanMac()));
			}
			if( device.getHwconf() != null ) {
				data.append(String.format("hwconf: %s%n", device.getHwconf().getName()));
				data.append(String.format("hwconfid: %s%n", device.getHwconfId()));
			}
			break;
		case "de.openschoolserver.dao.HWconf":
			HWConf hwconf = (HWConf)object;
			data.append(String.format("name: %s%n", hwconf.getName()));
			data.append(String.format("id: %d%n", hwconf.getId()));
			data.append(String.format("devicetype: %s%n", hwconf.getDeviceType()));
			break;
		case "de.openschoolserver.dao.Room":
			Room room = (Room)object;
			switch(pluginName){
			case "add_doom":
			case "modify_room":
				data.append(String.format("name: %s%n", room.getName()));
				data.append(String.format("description: %s%n", room.getDescription()));
				data.append(String.format("startip: %s%n", room.getStartIP()));
				break;
			case "delete_room":
				data.append(String.format("name: %s%n", room.getName()));
				data.append(String.format("startip: %s%n", room.getStartIP()));
				break;
			}
			break;
		case "de.cephalix.api.dao.CephalixCustomer":
		case "de.cephalix.api.dao.CephalixInstitute":
		case "de.cephalix.api.dao.CephalixRegcode":
			try {
				data.append(object);
			} catch (Exception e) {
				logger.error("pluginHandler : Cephalix****:" + e.getMessage());
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
		List<String> uids = new ArrayList<String>();
		for( User user : users ) {
			uids.add(user.getUid());
		}
		data.append(String.format("users: %s%n", String.join(",",uids)));
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
		List<String> neededRights = new ArrayList<String>();
		switch(object.getClass().getName()) {
		case "de.openschoolserver.dao.Acl":
			Acl Acl = (Acl)object;
			owner = Acl.getCreator();
			neededRights.add("acl.modify");
			break;
		case "de.openschoolserver.dao.AccessInRoom":
			AccessInRoom AccessInRoom = (AccessInRoom)object;
			owner = AccessInRoom.getCreator();
			neededRights.add("room.modify");
			break;
		case "de.openschoolserver.dao.Announcement":
			Announcement an = (Announcement)object;
			owner = an.getOwner();
			break;
		case "de.openschoolserver.dao.Category":
			Category cat = (Category)object;
			owner = cat.getOwner();
			neededRights.add("category.modify");
			break;
		case "de.openschoolserver.dao.Contact":
			Contact con = (Contact)object;
			owner = con.getOwner();
			neededRights.add("contact.modify");
			break;
		case "de.openschoolserver.dao.FAQ":
			FAQ faq = (FAQ)object;
			owner = faq.getOwner();
			neededRights.add("faq.modify");
			break;
		case "de.openschoolserver.dao.Device":
			Device Device = (Device)object;
			owner = Device.getOwner();
			neededRights.add("device.modify");
			break;
		case "de.openschoolserver.dao.Group":
			Group group = (Group)object;
			owner = group.getOwner();
			neededRights.add("group.modify");
			break;
		case "de.openschoolserver.dao.HWConf":
			HWConf HWConf = (HWConf)object;
			owner = HWConf.getCreator();
			neededRights.add("hwconf.modify");
			break;
		case "de.openschoolserver.dao.OSSConfig":
			OSSConfig ossConfig = (OSSConfig)object;
			owner = ossConfig.getCreator();
			neededRights.add("ossconfig.modify");
			break;
		case "de.openschoolserver.dao.OSSMConfig":
			OSSMConfig ossMConfig = (OSSMConfig)object;
			owner = ossMConfig.getCreator();
			neededRights.add("ossmconfig.modify");
			break;
		case "de.openschoolserver.dao.Room":
			Room room = (Room)object;
			owner = room.getCreator();
			neededRights.add("room.modify");
			break;
		case "de.openschoolserver.dao.RoomSmartControl":
			RoomSmartControl rsc  = (RoomSmartControl)object;
			owner = rsc.getOwner();
			break;
		case "de.openschoolserver.dao.Partition":
			Partition partition = (Partition)object;
			owner = partition.getCreator();
			neededRights.add("hwconf.modify");
			break;
		case "de.openschoolserver.dao.Software":
			Software software = (Software)object;
			owner = software.getCreator();
			neededRights.add("software.modify");
			break;
		case "de.openschoolserver.dao.SoftwareLicence":
			SoftwareLicense softwareLicense = (SoftwareLicense)object;
			owner = softwareLicense.getCreator();
			neededRights.add("softwarelicence.modify");
			break;
		case "de.openschoolserver.dao.User":
			User user = (User)object;
			ownerId = user.getCreator().getId();
			neededRights.add("user.modify");
			if( session.getUser().getRole().equals(roleTeacher) &&
				user.getRole().equals(roleStudent)) {
				neededRights.add("education.users");
			}
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
		for( String right : neededRights ) {
			if( this.session.getAcls().contains(right) ) {
				return true;
			}
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
		Query query = this.em.createNamedQuery("OSSMConfig.check");
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
		Query query = this.em.createNamedQuery("OSSConfig.get");
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

	/**
	 * Checks if a value of an enumerate is allowed.
	 * @param name  The of the enumerate.
	 * @param value The value of the enumerate
	 * @return Boolean True if the enumerate value is allowed.
	 *         False if the enumerate is not allowed and in case of error.
	 */
	public boolean checkEnumerate(String type, String value) {
		Query query      = this.em.createNamedQuery("Enumerate.get");
		try {
			query.setParameter("type",type).setParameter("value",value);
			return  !query.getResultList().isEmpty() ;
		}	catch  (Exception e) {
			logger.error("checkEnumerate: " + e.getMessage());
			return false;
		} finally {
		}
	}

	/**
	 * Returns the list of the member of an enumerate.
	 *
	 * @param		type	Name of the enumerates: roomType, groupType, deviceType ...
	 * @return				The list of the member of the enumerate
	 * @see					addEnumerate
	 */
	public List<String> getEnumerates(String type ) {
		try {
			Query query = this.em.createNamedQuery("Enumerate.getByType").setParameter("type", type);
			List<String> results = new ArrayList<String>();
			for( Enumerate e :  (List<Enumerate>) query.getResultList() ) {
				results.add(e.getValue());
			}
			return results;
		} catch (Exception e) {
			logger.error(e.getMessage());
			return null;
		} finally {
		}
	}

	public boolean checkConfig(Object object, String key) {
		if( this.getConfigObject(object, key) == null ) {
			return false;
		}
		return true;
	}

	public OSSConfig getConfig(String type, String key) {
		OSSConfig     ossConfig = null;
		Query query      = this.em.createNamedQuery("OSSConfig.getAllByKey");
		query.setParameter("type",type).setParameter("keyword",key);
		if( ! query.getResultList().isEmpty() ) {
			ossConfig = (OSSConfig)  query.getResultList().get(0);
		}
		return ossConfig;
	}

	public List<OSSMConfig> getMConfigs(String key) {
		Query query = this.em.createNamedQuery("OSSMConfig.getAllForKey");
		query.setParameter("keyword",key);
		List<OSSMConfig> ossMConfigs = (List<OSSMConfig>) query.getResultList();
		return ossMConfigs;
	}

	public List<OSSMConfig> getMConfigs(String type, String key) {
		Query query = this.em.createNamedQuery("OSSMConfig.getAllByKey");
		query.setParameter("type",type).setParameter("keyword",key);
		List<OSSMConfig> ossMConfigs = (List<OSSMConfig>) query.getResultList();
		return ossMConfigs;
	}

	public List<String> getMConfigs(Object object, String key) {
		Long id = null;
		Query query = this.em.createNamedQuery("OSSMConfig.get");
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
		return values;
	}

	public List<OSSMConfig> getAllMConfig(Object object) {
		Long id = null;
		Query query = this.em.createNamedQuery("OSSMConfig.getAllById");
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
		query.setParameter("id", id);
		return (List<OSSMConfig>) query.getResultList();
	}

	public List<OSSMConfig> getMConfigObjects(Object object, String key) {
		Long id = null;
		Query query = this.em.createNamedQuery("OSSMConfig.get");
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
		ArrayList<OSSMConfig> values = new ArrayList<OSSMConfig>();
		for(OSSMConfig config : (List<OSSMConfig>) query.getResultList() ) {
			values.add(config);
		}
		return values;
	}

	public StringBuilder getImportDir(String startTime) {
		StringBuilder importDir = new StringBuilder();
		importDir.append(getConfigValue("HOME_BASE")).append("/groups/SYSADMINS/userimports/").append(startTime);
		return importDir;
	}

	public String getConfig(Object object, String key) {
		Long id = null;
		Query query = this.em.createNamedQuery("OSSConfig.get");
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
		return config.getValue();
	}

	public List<OSSConfig> getAllConfig(Object object) {
		Long id = null;
		Query query = this.em.createNamedQuery("OSSConfig.getAllById");
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
		query.setParameter("id", id);
		return (List<OSSConfig>) query.getResultList();
	}

	public OssResponse addMConfig(Object object, String key, String value) {
		if( this.checkMConfig(object, key, value) ){
			return new OssResponse(this.getSession(),"ERROR","This mconfig value already exists.");
		}
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
			this.em.getTransaction().begin();
			this.em.persist(mconfig);
			this.em.getTransaction().commit();
		} catch (Exception e) {
			logger.error("addMConfig: " + e.getMessage());
			return new OssResponse(this.getSession(),"ERROR",e.getMessage());
		} finally {
		}
		return new OssResponse(this.getSession(),"OK","Mconfig was created",mconfig.getId());
	}

	public OssResponse addConfig(Object object, String key, String value) {
		if( this.checkConfig(object, key) ){
			return new OssResponse(this.getSession(),"ERROR","This config already exists.");
		}
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
			this.em.getTransaction().begin();
			this.em.persist(config);
			this.em.getTransaction().commit();
		} catch (Exception e) {
			logger.error("addConfig: " + e.getMessage());
			return new OssResponse(this.getSession(),"ERROR",e.getMessage());
		} finally {
		}
		return new OssResponse(this.getSession(),"OK","Config was created");
	}

	public OssResponse setConfig(Object object, String key, String value) {
		if( ! this.checkConfig(object, key) ){
			return this.addConfig(object, key, value);
		}
		OSSConfig config = this.getConfigObject(object, key);
		try {
			config = this.em.find(OSSConfig.class, config.getId());
			config.setValue(value);
			this.em.getTransaction().begin();
			this.em.merge(config);
			this.em.getTransaction().commit();
		} catch (Exception e) {
			logger.error("setConfig: " + e.getMessage());
			return new OssResponse(this.getSession(),"ERROR",e.getMessage());
		} finally {
		}
		return new OssResponse(this.getSession(),"OK","Config was updated");
	}

	public OssResponse deleteConfig(Object object, Long configId) {
		try {
			OSSConfig config = this.em.find(OSSConfig.class, configId);
			this.em.getTransaction().begin();
			this.em.merge(config);
			this.em.remove(config);
			this.em.getTransaction().commit();
		} catch (Exception e) {
			logger.error("deleteConfig: " + e.getMessage());
			return new OssResponse(this.getSession(),"ERROR",e.getMessage());
		} finally {
		}
		return new OssResponse(this.getSession(),"OK","Config was deleted");
	}

	public OssResponse deleteMConfig(Object object, Long configId) {
		try {
			OSSMConfig config = this.em.find(OSSMConfig.class, configId);
			this.em.getTransaction().begin();
			this.em.merge(config);
			this.em.remove(config);
			this.em.getTransaction().commit();
		} catch (Exception e) {
			logger.error("deleteConfig: " + e.getMessage());
			return new OssResponse(this.getSession(),"ERROR",e.getMessage());
		} finally {
		}
		return new OssResponse(this.getSession(),"OK","Config was deleted");
	}

	public OssResponse deleteConfig(Object object, String key) {
		OSSConfig config = this.getConfigObject(object, key);
		if( config == null ) {
			return new OssResponse(this.getSession(),"ERROR","Config does not exists.");
		}
		try {
			this.em.getTransaction().begin();
			config = this.em.find(OSSConfig.class, config.getId());
			this.em.merge(config);
			this.em.remove(config);
			this.em.getTransaction().commit();
		} catch (Exception e) {
			logger.error("deleteConfig: " + e.getMessage());
			return new OssResponse(this.getSession(),"ERROR",e.getMessage());
		} finally {
		}
		return new OssResponse(this.getSession(),"OK","Config was deleted");
	}

	public OssResponse deleteMConfig(Object object, String key, String value) {
		OSSMConfig config = this.getMConfigObject(object, key, value);
		if( config == null ) {
			return new OssResponse(this.getSession(),"ERROR","MConfig does not exists.");
		}
		try {
			this.em.getTransaction().begin();
			config = this.em.find(OSSMConfig.class, config.getId());
			this.em.merge(config);
			this.em.remove(config);
			this.em.getTransaction().commit();
		} catch (Exception e) {
			logger.error("deleteMConfig: " + e.getMessage());
			return new OssResponse(this.getSession(),"ERROR",e.getMessage());
		} finally {
		}
		return new OssResponse(this.getSession(),"OK","Config was deleted");
	}

	public void deletAllConfigs(Object object) {
		//Remove corresponding OSSConfig
		for( OSSConfig o : this.getAllConfig(object)) {
			this.em.remove(o);
		}
		//Remove corresponding OSSMConfig
		for( OSSMConfig o : this.getAllMConfig(object)) {
			this.em.remove(o);
		}
	}
}
