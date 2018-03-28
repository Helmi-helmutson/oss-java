/* (c) 2017 Péter Varkoly <peter@varkoly.de> - all rights reserved */
package de.openschoolserver.dao.controller;

import de.openschoolserver.dao.tools.OSSShellTools;

import org.apache.http.client.fluent.*;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.ws.rs.WebApplicationException;

import de.openschoolserver.dao.*;

import java.io.IOException;
import java.util.*;

@SuppressWarnings( "unchecked" )
public class SystemController extends Controller {

	Logger logger = LoggerFactory.getLogger(SystemController.class);

	public SystemController(Session session) {
		super(session);
	}

	/*
	 * Translate a sting in a required language.
	 * If the translation does not exists the string will be written into the missed translations table.
	 *
	 * @param	lang	Two letter description of the required language
	 * @param	key		The text to be translated
	 * @return			The translated text if there was found a translation or the original text
	 * @see				AddTranslation
	 */
	public String translate(String lang, String key) {
		EntityManager em = getEntityManager();
		Query query = em.createNamedQuery("Translation.find").setParameter("lang", lang.toUpperCase()).setParameter("string", key);
		try {
			Translation trans = (Translation) query.getSingleResult();
			return trans.getValue();
		}  catch (Exception e) {
			MissedTranslation trans = null;
			query = em.createNamedQuery("MissedTranslation.find").setParameter("lang", lang.toUpperCase()).setParameter("string", key);
			try {
				trans = (MissedTranslation) query.getSingleResult();
			}  catch (Exception f) {
				trans = new MissedTranslation(lang.toUpperCase(),key);
				try {
					em.getTransaction().begin();
					em.persist(trans);
					em.getTransaction().commit();
				}  catch (Exception g) {
					logger.error(g.getMessage());
				}
			}
			return key;
		} finally {
			em.close();
		}
	}

	/*
	 * Add a translated text to the Translations table.
	 * If the translation already exists this will be updated.
	 * If there are an entry in MissedTranslations table this will be removed.
	 *
	 * @param	translation 	Two letter description of the language of the translation.
	 * 							The text to be translated. Must not be longer then 256 characters
	 * 							The translated text. Must not be longer then 256 characters
	 * @return			The result of the DB operations
	 * @see				Translate
	 * @see				GetMissedTranslations
	 */
	public OssResponse addTranslation(Translation translation) {
		EntityManager em = getEntityManager();
		translation.setLang(translation.getLang().toUpperCase());
		Query query = em.createNamedQuery("Translation.find")
				.setParameter("lang", translation.getLang())
				.setParameter("string", translation.getString());
		String	responseText = "Translation was created";
		if( query.getResultList().isEmpty()) {
			try {
				em.getTransaction().begin();
				em.persist(translation);
				em.getTransaction().commit();
				responseText = "Translation was updated";
			}  catch (Exception b) {
				em.close();
				logger.error(b.getMessage());
				return new OssResponse(this.session,"ERROR", b.getMessage());
			}
		} else {
			try {
				em.getTransaction().begin();
				em.merge(translation);
				em.getTransaction().commit();
			}  catch (Exception e) {
				em.close();
				logger.error(e.getMessage());
				return new OssResponse(this.session,"ERROR", e.getMessage());
			}
		}

		/* Delete missed translation */
		query = em.createNamedQuery("MissedTranslation.find")
				.setParameter("lang", translation.getLang())
				.setParameter("string", translation.getString());
		try {
			MissedTranslation missedTrans = (MissedTranslation) query.getSingleResult();
			em.getTransaction().begin();
			em.remove(missedTrans);
			em.getTransaction().commit();
		} catch (Exception e) {
		} finally {
			em.close();
		}
		return new OssResponse(this.session,"OK",responseText);
	}

	/*
	 * Delivers a list of the missed translations to a language.
	 *
	 * @param	lang	Two letter description of the language of the translation.
	 * @return			The list of the missed translations
	 * @see				Translate
	 * @see				AddTranslation
	 */
	public List<String> getMissedTranslations(String lang){
		List<String> missed = new ArrayList<String>();
		EntityManager em = getEntityManager();
		Query query = em.createNamedQuery("MissedTranslation.findAll").setParameter("lang", lang.toUpperCase());
		for(MissedTranslation missedTrans : (List<MissedTranslation>) query.getResultList() ) {
			missed.add(missedTrans.getString());
		}
		return missed;
	}


	/*
	 * Delivers a list of the status of the system
	 *
	 * @return		List of status hashes:
	 * 				[
	 * 					{
	 * 						"name"			: "groups"
	 * 						"primary"		: 5,
	 * 						"class"			: 40,
	 * 						"workgroups"	: 122
	 * 					},
	 * 					{
	 * 						"name"			: "users",
	 * 						"students"		: 590,
	 * 						"students-loggedOn"	205,
	 * 						...
	 * 					}
	 * 					....
	 * 				]
	 *
	 */
	public List<Map<String, String>> getStatus() {
		//Initialize of some variable
		List<Map<String, String>> statusList = new ArrayList<>();
		EntityManager em = getEntityManager();
		Map<String,String> statusMap;
		Query query;
		Integer count;

		//TODO System Load, HD, License, ....

		//Groups;
		statusMap = new HashMap<>();
		statusMap.put("name","groups");
		for( String groupType : this.getEnumerates("groupType")) {
			query = em.createNamedQuery("Group.getByType").setParameter("groupType",groupType);
			count = query.getResultList().size();
			statusMap.put(groupType,count.toString());
		}
		statusList.add(statusMap);

		//Users
		statusMap = new HashMap<>();
		statusMap.put("name","users");
		for( String role : this.getEnumerates("role")) {
			query = em.createNamedQuery("User.getByRole").setParameter("role",role);
			count = query.getResultList().size();
			statusMap.put(role,count.toString());
			Integer loggedOn = 0;
			for( User u : (List<User>) query.getResultList() ) {
				loggedOn += u.getLoggedOn().size();
			}
			statusMap.put(role + "-loggedOn", loggedOn.toString());
		}
		statusList.add(statusMap);

		//Rooms
		statusMap = new HashMap<>();
		statusMap.put("name","rooms");
		for( String roomType : this.getEnumerates("roomType")) {
			query = em.createNamedQuery("Room.getByType").setParameter("type",roomType);
			count = query.getResultList().size();
			statusMap.put(roomType,count.toString());
		}
		statusList.add(statusMap);

		//Devices
		statusMap = new HashMap<>();
		statusMap.put("name","devices");
		for( String deviceType : this.getEnumerates("deviceType")) {
			statusMap.put(deviceType,"0");
		}
		statusMap.put("non_typed","0");
		DeviceController deviceController = new DeviceController(this.session);
		for( Device device: deviceController.getAll() ){
		String deviceType = "non_typed";
		if( device.getHwconf() != null ) {
			deviceType = device.getHwconf().getDeviceType();
		}
		Integer i = Integer.decode(statusMap.get(deviceType)) + 1 ;
		statusMap.put(deviceType,String.valueOf(i));
		}
		statusList.add(statusMap);

		//Software
		SoftwareController softwareController = new SoftwareController(this.session);
		statusList.add(softwareController.statistic());

		return statusList;
	}

	/*
	 * Returns the list of the member of an enumerate.
	 *
	 * @param		type	Name of the enumerates: roomType, groupType, deviceType ...
	 * @return				The list of the member of the enumerate
	 * @see					addEnumerate
	 */
	public List<String> getEnumerates(String type ) {
		EntityManager em = getEntityManager();
		try {
			Query query = em.createNamedQuery("Enumerate.getByType").setParameter("type", type);
			List<String> results = new ArrayList<String>();
			for( Enumerate e :  (List<Enumerate>) query.getResultList() ) {
				results.add(e.getValue());
			}
			return results;
		} catch (Exception e) {
			logger.error(e.getMessage());
			return null;
		} finally {
			em.close();
		}
	}

	/*
	 * Add a new enumerate
	 *
	 * @param		type	Name of the enumerates: roomType, groupType, deviceType ...
	 * @param		value	The new value
	 * @see					getEnumerates
	 * @see					deleteEnumerate
	 */
	public OssResponse addEnumerate(String type, String value) {
		EntityManager em = getEntityManager();
		Query	query = em.createNamedQuery("Enumerate.get").setParameter("name", value).setParameter("value", value);
		if( ! query.getResultList().isEmpty() ) {
				return new OssResponse(this.getSession(),"ERROR","Entry alread does exists");
		}
		Enumerate en = new Enumerate(type,value);
		try {
			em.getTransaction().begin();
			em.persist(en);
			em.getTransaction().commit();
		} catch (Exception e) {
			logger.error(e.getMessage());
			return new OssResponse(this.getSession(),"ERROR", e.getMessage());
		} finally {
			em.close();
		}
		return new OssResponse(this.getSession(),"OK","Enumerate was created succesfully.");
	}

	/*
	 * Deletes an existing enumerate
	 *
	 * @param		type	Name of the enumerates: roomType, groupType, deviceType ...
	 * @param		value	The new value
	 * @see					getEnumerates
	 * @see					addEnumerate
	 */
	public OssResponse deleteEnumerate(String type, String value) {
		EntityManager em = getEntityManager();
		Query query = em.createNamedQuery("Enumerate.getByType").setParameter("type", type).setParameter("value", value);
		try {
			Enumerate en = (Enumerate) query.getSingleResult();
			em.getTransaction().begin();
			em.remove(en);
			em.getTransaction().commit();
		} catch (Exception e) {
			logger.error(e.getMessage());
			return new OssResponse(this.getSession(),"ERROR", e.getMessage());
		} finally {
			em.close();
		}
		return new OssResponse(this.getSession(),"OK","Enumerate was removed succesfully.");
	}

	////////////////////////////////////////////////////////
	// Functions for setting firewall
	///////////////////////////////////////////////////////

	public Map<String, String> getFirewallIncomingRules() {
		Config fwConfig = new Config("/etc/sysconfig/SuSEfirewall2","FW_");
		Map<String,String> statusMap;
		//External Ports
		statusMap = new HashMap<>();
		statusMap.put("ssh", "false");
		statusMap.put("https", "false");
		statusMap.put("admin", "false");
		statusMap.put("rdesktop", "false");
		statusMap.put("other", "");
		for( String extPort : fwConfig.getConfigValue("SERVICES_EXT_TCP").split(" ") ) {
			switch(extPort) {
			case "ssh":
			case "22":
				statusMap.put("ssh","true");
				break;
			case "443":
			case "https":
				statusMap.put("https", "true");
				break;
			case "444":
				statusMap.put("admin", "true");
				break;
			case "3389":
			case "ms-wbt-server":
				statusMap.put("rdesktop", "true");
				break;
			default:
				statusMap.put("other",statusMap.get("other")+" "+ extPort);

			}
		}
		return statusMap;
	}

	public OssResponse setFirewallIncomingRules(Map<String, String> firewallExt) {
		List<String> fwServicesExtTcp = new ArrayList<String>();
		Config fwConfig = new Config("/etc/sysconfig/SuSEfirewall2","FW_");
		if( firewallExt.get("ssh").equals("true") ) {
			fwServicesExtTcp.add("ssh");
		}
		if( firewallExt.get("https").equals("true")) {
			fwServicesExtTcp.add("https");
		}
		if( firewallExt.get("admin").equals("true")) {
			fwServicesExtTcp.add("444");
		}
		if( firewallExt.get("rdesktop").equals("true") )  {
			fwServicesExtTcp.add("3389");
		}
		if( firewallExt.get("other") != null && !firewallExt.get("other").isEmpty()) {
			fwServicesExtTcp.add(firewallExt.get("other"));
		}
		fwConfig.setConfigValue("SERVICES_EXT_TCP", String.join(" ", fwServicesExtTcp));
		this.systemctl("restart", "SuSEfirewall2");
		return new OssResponse(this.getSession(),"OK","Firewall incoming access rule  was set succesfully.");
	}

	public List<Map<String, String>> getFirewallOutgoingRules() {
		Config fwConfig = new Config("/etc/sysconfig/SuSEfirewall2","FW_");
		List<Map<String, String>> firewallList = new ArrayList<>();
		Map<String,String> statusMap;
		RoomController roomController = new RoomController(this.session);
		DeviceController deviceController = new DeviceController(this.session);

		for( String outRule : fwConfig.getConfigValue("MASQ_NETS").split(" ") ) {
			if (outRule.length() > 0) {
				statusMap = new HashMap<>();
				String[] rule = outRule.split(",");
				String[] host = rule[0].split("/");
				String   dest = rule[1];
				String   prot = rule.length > 2 ? rule[2] : "all";
				String   port = rule.length > 3 ? rule[3] : "all";
				if(host[1].equals("32")) {
					Device device = deviceController.getByIP(host[0]);
					if( device == null ) {
			continue;
					}
					statusMap.put("id", Long.toString(device.getId()));
					statusMap.put("name", device.getName());
					statusMap.put("type", "host");
				} else {
			Room room = roomController.getByIP(host[0]);
			if( room == null ) {
			continue;
			}
					statusMap.put("id", Long.toString(room.getId()));
					statusMap.put("name", room.getName());
					statusMap.put("type", "room" );
				}
				statusMap.put("dest", dest);
				statusMap.put("prot", prot);
				statusMap.put("port", port);
				firewallList.add(statusMap);
			}
		}
		return firewallList;
	}

	public OssResponse setFirewallOutgoingRules(List<Map<String, String>> firewallList) {
		List<String> fwMasqNets = new ArrayList<String>();
		try {
			logger.debug(new ObjectMapper().writeValueAsString(firewallList));
		} catch (Exception e) {
			logger.debug("{ \"ERROR\" : \"CAN NOT MAP THE OBJECT\" }");
		}
		Config fwConfig = new Config("/etc/sysconfig/SuSEfirewall2","FW_");
		RoomController roomController = new RoomController(this.session);
		DeviceController deviceController = new DeviceController(this.session);
		Device device;
		Room   room;
		for( Map<String,String> map : firewallList ) {
			StringBuilder data = new StringBuilder();
			if( map.get("type").equals("room")) {
				room = roomController.getById(Long.parseLong(map.get("id")));
				data.append(room.getStartIP()).append("/").append(String.valueOf(room.getNetMask())).append(",");
			} else {
				device = deviceController.getById(Long.parseLong(map.get("id")));
				data.append(device.getIp()).append("/32,");
			}
			data.append(map.get("dest"));
			if( !map.get("prot").equals("all") ) {
				data.append(",").append(map.get("prot")).append(",").append(map.get("port"));
			}
			fwMasqNets.add(data.toString());
		}
		fwConfig.setConfigValue("ROUTE","yes");
		if( fwMasqNets.isEmpty() ) {
			fwConfig.setConfigValue("MASQUERADE","no");
			fwConfig.setConfigValue("MASQ_NETS", " ");
		} else {
			fwConfig.setConfigValue("MASQUERADE","yes");
			fwConfig.setConfigValue("MASQ_NETS", String.join(" ", fwMasqNets));
		}
		this.systemctl("restart", "SuSEfirewall2");
		return new OssResponse(this.getSession(),"OK","Firewall outgoing access rule  was set succesfully.");
	}

	public List<Map<String, String>> getFirewallRemoteAccessRules() {
		Config fwConfig = new Config("/etc/sysconfig/SuSEfirewall2","FW_");
		List<Map<String, String>> firewallList = new ArrayList<>();
		Map<String,String> statusMap;
		DeviceController deviceController = new DeviceController(this.session);

		for( String outRule : fwConfig.getConfigValue("FORWARD_MASQ").split(" ") ) {
		   if (outRule!=null && outRule.length()>0) {
			   statusMap = new HashMap<>();
			   String[] rule = outRule.split(",");
			   if (rule!=null && rule.length>=4) {
				   Device device = deviceController.getByIP(rule[1]);
				   statusMap.put("ext", rule[3]);
				   statusMap.put("id",  Long.toString(device.getId()) );
				   statusMap.put("name", device.getName() );
				   statusMap.put("port", rule[4]);
				   firewallList.add(statusMap);
			   }
		   }
		}
		return firewallList;
	}

	public OssResponse setFirewallRemoteAccessRules(List<Map<String, String>> firewallList) {
		List<String> fwForwardMasq = new ArrayList<String>();
		Config fwConfig = new Config("/etc/sysconfig/SuSEfirewall2","FW_");
		DeviceController deviceController = new DeviceController(this.session);
		for( Map<String,String> map : firewallList ) {
			Device device = deviceController.getById(Long.parseLong(map.get("id")));
			fwForwardMasq.add("0/0," + device.getIp() + ",tcp," + map.get("ext") + "," + map.get("port") );
		}
		fwConfig.setConfigValue("FORWARD_MASQ", String.join(" ", fwForwardMasq));
		this.systemctl("restart", "SuSEfirewall2");
		return new OssResponse(this.getSession(),"OK","Firewall remote access rule  was set succesfully.");
	}

	/*
	 * Functions for package management of the system
	 */

	public Date getValidityOfRegcode() {
		StringBuilder url = new StringBuilder();
		url.append("https://").append("UPDATE_SERVER").append("/api/customers/validateRegcode/regcode=\"").append(this.getConfigValue("REG_CODE")).append("\"");
		try {
			Long milis = Long.parseLong(Request.Get(url.toString()).toString());
			return new Date(milis);
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new WebApplicationException(500);
		}
	}

	public boolean validateRegcode() {
		Date valid = this.getValidityOfRegcode();
		if( valid == null ) {
			return false;
		}
		return valid.after(this.now());
	}

	public OssResponse registerSystem() {
		if( ! this.validateRegcode() ) {
			return new OssResponse(this.getSession(),"ERROR","Registration Code is invalid.");
		}
		String[] program   = new String[1];
		StringBuffer reply = new StringBuffer();
		StringBuffer error = new StringBuffer();
		program[0] = "/usr/share/oss/tools/register.sh";
		OSSShellTools.exec(program, reply, error, null);
		if( error.toString().startsWith("OK")) {
			return new OssResponse(this.getSession(),"OK","System was registered succesfully.");
		} else {
			return new OssResponse(this.getSession(),"ERROR",error.toString());
		}
	}

	public List<Map<String,String>> searchPackages(String filter) {
		List<Map<String,String>> packages = new ArrayList<Map<String,String>>();
		Map<String,String> software = null;
		String[] program   = new String[3];
		StringBuffer reply = new StringBuffer();
		StringBuffer error = new StringBuffer();
		program[0] = "zypper";
		program[1] = "-x";
		program[2] = filter;
		OSSShellTools.exec(program, reply, error, null);
		try {
			Document doc = new SAXBuilder().build( reply.toString() );
			Element rootNode = doc.getRootElement();
			for( Element node : (List<Element>) rootNode.getChild("search-result").getChild("solvable-list").getChildren("solvable") ) {
				if( !node.getAttribute("kind").getValue().equals("package")) {
					continue;
				}
				software = new HashMap<String,String>();
				software.put("name",	node.getAttributeValue("name"));
				software.put("summary", node.getAttributeValue("summary"));
				software.put("status",  node.getAttributeValue("status"));
				packages.add(software);
			}
		} catch(IOException e ) {
			logger.error(e.getMessage());
			throw new WebApplicationException(500);
		} catch(JDOMException e)  {
			logger.error(e.getMessage());
			throw new WebApplicationException(500);
		}
		return packages;
	}

	public OssResponse installPackages(List<String> packages) {
		String[] program   = new String[3 + packages.size()];
		StringBuffer reply = new StringBuffer();
		StringBuffer error = new StringBuffer();
		program[0] = "zypper";
		program[1] = "-n";
		program[2] = "install";
		int i = 3;
		for(String prog : packages) {
			program[i] = prog;
			i++;
		}
		if( OSSShellTools.exec(program, reply, error, null) == 0 ) {
			return new OssResponse(this.getSession(),"OK","Packages were installed succesfully.");
		} else {
			return new OssResponse(this.getSession(),"ERROR",error.toString());
		}
	}

	public OssResponse updatePackages(List<String> packages) {
		String[] program   = new String[3 + packages.size()];
		StringBuffer reply = new StringBuffer();
		StringBuffer error = new StringBuffer();
		program[0] = "zypper";
		program[1] = "-n";
		program[2] = "update";
		int i = 3;
		for(String prog : packages) {
			program[i] = prog;
			i++;
		}
		if( OSSShellTools.exec(program, reply, error, null) == 0 ) {
			return new OssResponse(this.getSession(),"OK","Packages were updated succesfully.");
		} else {
			return new OssResponse(this.getSession(),"ERROR",error.toString());
		}
	}

	public OssResponse updateSystem() {
		String[] program   = new String[3];
		StringBuffer reply = new StringBuffer();
		StringBuffer error = new StringBuffer();
		program[0] = "zypper";
		program[1] = "-n";
		program[2] = "update";
		if( OSSShellTools.exec(program, reply, error, null) == 0 ) {
			return new OssResponse(this.getSession(),"OK","System was updated succesfully.");
		} else {
			return new OssResponse(this.getSession(),"ERROR",error.toString());
		}
	}

	/*
	 * Acl Management
	 */
	public Acl getAclById(Long aclId) {
		EntityManager em = getEntityManager();
		try {
			return em.find(Acl.class, aclId);
		} catch (Exception e) {
			return null;
		} finally {
			em.close();
		}
	}

	public List<Acl> getAvailableAcls() {
		List<Acl> acls = new ArrayList<Acl>();
		for( String aclName : 	this.getEnumerates("apiAcl") ) {
			acls.add(new Acl(aclName,false));
		}
		return acls;
	}

	public List<Acl> getAclsOfGroup(Long groupId) {
		return new GroupController(session).getById(groupId).getAcls();
	}

	public List<Acl> getAvailableAclsForGroup(Long groupId) {
		List<Acl> acls	= new ArrayList<Acl>();
		List<Acl> ownAcls = this.getAclsOfGroup(groupId);
		for( String aclName : 	this.getEnumerates("apiAcl") ) {
			boolean have = false;
			for( Acl ownAcl : ownAcls ) {
				if( ownAcl.getAcl().equals(aclName) ) {
					have = true;
					break;
				}
			}
			if( !have ) {
				acls.add(new Acl(aclName,false));
			}
		}
		return acls;
	}


	public OssResponse setAclToGroup(Long groupId, Acl acl) {
		Group group = new GroupController(session).getById(groupId);
		EntityManager em = getEntityManager();
		logger.debug("Group acl to set: " + acl);
		try {
			em.getTransaction().begin();
			Acl oldAcl = this.getAclById(acl.getId());
			if( oldAcl != null ) {
				if( acl.getAllowed() ) {
					oldAcl.setAllowed(true);
					em.merge(oldAcl);
				} else {
					em.merge(oldAcl);
					em.remove(oldAcl);
				}
			} else {
				acl.setGroup(group);
				acl.setCreator(this.session.getUser());
				em.persist(acl);
				group.addAcl(acl);
				em.merge(group);
			}
			em.getTransaction().commit();
		} catch(Exception e) {
			logger.debug("ERROR in setAclToGroup:" + e.getMessage());
			return new OssResponse(session,"ERROR",e.getMessage());
		} finally {
			em.close();
		}
		return new OssResponse(session,"OK","ACL was set succesfully.");
	}

	public List<Acl> getAclsOfUser(Long userId) {
		User user = new UserController(session).getById(userId);
		List<Acl> acls = new ArrayList<Acl>();
		for( Group group : user.getGroups() ) {
			for( Acl acl : group.getAcls() ) {
				acls.add(acl);
			}
		}
		for( Acl acl : user.getAcls() ){
			boolean identicalByGroup = false;
			for( Acl groupAcl : acls ) {
				if( groupAcl.getAcl().equals(acl.getAcl()) &&
						groupAcl.getAllowed() == acl.getAllowed() ) {
					identicalByGroup = true;
					break;
				}
			}
			if( ! identicalByGroup ) {
				acls.add(acl);
			}
		}
		return acls;
	}

	public List<Acl> getAvailableAclsForUser(Long userId) {
		List<Acl> acls	= new ArrayList<Acl>();
		List<Acl> ownAcls = this.getAclsOfUser(userId);
		for( String aclName : 	this.getEnumerates("apiAcl") ) {
			boolean have = false;
			for( Acl ownAcl : ownAcls ) {
				if( ownAcl.getAcl().equals(aclName) ) {
					have = true;
					break;
				}
			}
			if( !have ) {
				acls.add(new Acl(aclName,false));
			}
		}
		return acls;
	}

	public OssResponse setAclToUser(Long userId, Acl acl) {
		User user = new UserController(session).getById(userId);
		EntityManager em = getEntityManager();
		logger.debug("User acl to set: " + acl);
		try {
			em.getTransaction().begin();
			Acl oldAcl = this.getAclById(acl.getId());
			if( oldAcl != null && oldAcl.getUser() != null && oldAcl.getUser().equals(user) ) {
				if( acl.getAllowed() ) {
					oldAcl.setAllowed(true);
					em.merge(oldAcl);
				} else {
					em.merge(oldAcl);
					em.remove(oldAcl);
				}
			} else  {
				acl.setGroup(null);
				acl.setUser(user);
				acl.setCreator(this.session.getUser());
				em.persist(acl);
				user.addAcl(acl);
				em.merge(user);
				}
			em.getTransaction().commit();
		} catch(Exception e) {
			logger.debug("ERROR in setAclToUser:" + e.getMessage());
			return new OssResponse(session,"ERROR",e.getMessage());
		} finally {
			em.close();
		}
		return new OssResponse(session,"OK","ACL was set succesfully.");
	}
}
