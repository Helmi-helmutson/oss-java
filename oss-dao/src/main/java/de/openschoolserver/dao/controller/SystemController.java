/* (c) Péter Varkoly <peter@varkoly.de> - all rights reserved */
package de.openschoolserver.dao.controller;

import de.openschoolserver.dao.tools.OSSShellTools;

import org.apache.http.client.fluent.*;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.filter.ElementFilter;
import org.jdom.input.SAXBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.ws.rs.WebApplicationException;

import de.openschoolserver.dao.*;

import java.io.IOException;
import java.io.StringReader;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings( "unchecked" )
public class SystemController extends Controller {

	Logger logger = LoggerFactory.getLogger(SystemController.class);

	public SystemController(Session session,EntityManager em) {
		super(session,em);
	}

	/**
	 * Translate a sting in a required language.
	 * If the translation does not exists the string will be written into the missed translations table.
	 *
	 * @param	lang	Two letter description of the required language
	 * @param	key		The text to be translated
	 * @return			The translated text if there was found a translation or the original text
	 * @see				AddTranslation
	 */
	public String translate(String lang, String key) {
		Query query = this.em.createNamedQuery("Translation.find").setParameter("lang", lang.toUpperCase()).setParameter("string", key);
		try {
			Translation trans = (Translation) query.getSingleResult();
			if( trans.getValue().isEmpty() ) {
				return key;
			}
			return trans.getValue();
		}  catch (Exception e) {
			Translation newTrans = new Translation(lang,key);
			this.addTranslation(newTrans);
		} finally {
		}
		return key;
	}

	/**
	 * Add a translated text to the Translations table.
	 * If the translation already exists this will be updated.
	 * If there are an entry in MissedTranslations table this will be removed.
	 *
	 * @param	translationTwo letter description of the language of the translation.
	 *						The text to be translated. Must not be longer then 256 characters
	 *						The translated text. Must not be longer then 256 characters
	 * @return			The result of the DB operations
	 * @see				Translate
	 */
	public OssResponse addTranslation(Translation translation) {
		translation.setLang(translation.getLang().toUpperCase());
		Query query = this.em.createNamedQuery("Translation.find")
				.setParameter("lang", translation.getLang())
				.setParameter("string", translation.getString());
		String	responseText = "Translation was created";
		if( query.getResultList().isEmpty()) {
			try {
				this.em.getTransaction().begin();
				this.em.persist(translation);
				this.em.getTransaction().commit();
				responseText = "Translation was updated";
			}  catch (Exception b) {
				logger.error(b.getMessage());
				return new OssResponse(this.session,"ERROR", b.getMessage());
			}
		} else {
			try {
				this.em.getTransaction().begin();
				this.em.merge(translation);
				this.em.getTransaction().commit();
			}  catch (Exception e) {
				logger.error(e.getMessage());
				return new OssResponse(this.session,"ERROR", e.getMessage());
			}
		}

		return new OssResponse(this.session,"OK",responseText);
	}

	/**
	 * Delivers a list of the missed translations to a language.
	 *
	 * @param	lang	Two letter description of the language of the translation.
	 * @return			The list of the missed translations
	 * @see				Translate
	 * @see				AddTranslation
	 */
	public List<Translation> getMissedTranslations(String lang){
		Query query = this.em.createNamedQuery("Translation.untranslated").setParameter("lang", lang.toUpperCase());
		return query.getResultList();
	}


	/**
	 * Delivers a list of the status of the system
	 *
	 * @return		Hash of status lists:
	 *			[
	 *				{
	 *					"name"			: "groups"
	 *					"primary"		: 5,
	 *					"class"			: 40,
	 *					"workgroups"	: 122
	 *				},
	 *				{
	 *					"name"			: "users",
	 *					"students"		: 590,
	 *					"students-loggedOn"	205,
	 *					...
	 *				}
	 *				....
	 *			]
	 *
	 */
	public Map<String, List<Map<String, String>>> getStatus() {
		//Initialize of some variable
		Map<String, List<Map<String, String>>> systemStatus = new HashMap<>();
		List<Map<String,String>> statusMapList;
		Map<String,String> statusMap;
		Query query;
		Integer count;

		//TODO System Load, HD, License, ....

		//Groups;
		statusMapList = new ArrayList<Map<String,String>>();
		for( String groupType : this.getEnumerates("groupType")) {
			query = this.em.createNamedQuery("Group.getByType").setParameter("groupType",groupType);
			count = query.getResultList().size();
			statusMap = new HashMap<>();
			statusMap.put("name", groupType);
			statusMap.put("count",count.toString());
			statusMapList.add(statusMap);
		}
		systemStatus.put("groups", statusMapList);

		//Users
		statusMapList = new ArrayList<Map<String,String>>();
		for( String role : this.getEnumerates("role")) {
			query = this.em.createNamedQuery("User.getByRole").setParameter("role",role);
			count = query.getResultList().size();
			Integer loggedOn = 0;
			for( User u : (List<User>) query.getResultList() ) {
				loggedOn += u.getLoggedOn().size();
			}
			statusMap = new HashMap<>();
			statusMap.put("name", role);
			statusMap.put("count",count.toString());
			statusMap.put("loggedOn",loggedOn.toString());
			statusMapList.add(statusMap);
		}
		systemStatus.put("users", statusMapList);

		//Rooms
		statusMapList = new ArrayList<Map<String,String>>();
		for( String roomType : this.getEnumerates("roomType")) {
			query = this.em.createNamedQuery("Room.getByType").setParameter("type",roomType);
			count = query.getResultList().size();
			statusMap = new HashMap<>();
			statusMap.put("name", roomType);
			statusMap.put("count",count.toString());
			statusMapList.add(statusMap);
		}
		query = this.em.createNamedQuery("Room.getByType").setParameter("type","adHocAccess");
		count = query.getResultList().size();
		statusMap = new HashMap<>();
		statusMap.put("name", "adHocAccess");
		statusMap.put("count",count.toString());
		statusMapList.add(statusMap);
		systemStatus.put("rooms", statusMapList);

		Integer deviceCount = new DeviceController(this.session,this.em).getAll().size();
		statusMapList = new ArrayList<Map<String,String>>();
		CloneToolController ctc = new CloneToolController(this.session,this.em);
		for( HWConf hwconf : ctc.getAllHWConf() ) {
			count = hwconf.getDevices().size();
			deviceCount -= count;
			statusMap = new HashMap<>();
			statusMap.put("name", hwconf.getName());
			statusMap.put("count",count.toString());
			statusMapList.add(statusMap);
		}
		statusMap = new HashMap<>();
		statusMap.put("name","non_typed");
		statusMap.put("count",deviceCount.toString());
		statusMapList.add(statusMap);
		systemStatus.put("devices", statusMapList);
		//Software
		SoftwareController softwareController = new SoftwareController(this.session,this.em);
		systemStatus.put("softwares",softwareController.statistic());

		return systemStatus;
	}

	/**
	 * Add a new enumerate
	 *
	 * @param		type	Name of the enumerates: roomType, groupType, deviceType ...
	 * @param		value	The new value
	 * @see					getEnumerates
	 * @see					deleteEnumerate
	 */
	public OssResponse addEnumerate(String type, String value) {
		Query	query = this.em.createNamedQuery("Enumerate.get").setParameter("name", value).setParameter("value", value);
		if( ! query.getResultList().isEmpty() ) {
				return new OssResponse(this.getSession(),"ERROR","Entry alread does exists");
		}
		Enumerate en = new Enumerate(type,value);
		try {
			this.em.getTransaction().begin();
			this.em.persist(en);
			this.em.getTransaction().commit();
		} catch (Exception e) {
			logger.error(e.getMessage());
			return new OssResponse(this.getSession(),"ERROR", e.getMessage());
		} finally {
		}
		return new OssResponse(this.getSession(),"OK","Enumerate was created succesfully.");
	}

	/**
	 * Deletes an existing enumerate
	 *
	 * @param		type	Name of the enumerates: roomType, groupType, deviceType ...
	 * @param		value	The new value
	 * @see					getEnumerates
	 * @see					addEnumerate
	 */
	public OssResponse deleteEnumerate(String type, String value) {
		Query query = this.em.createNamedQuery("Enumerate.getByType").setParameter("type", type).setParameter("value", value);
		try {
			Enumerate en = (Enumerate) query.getSingleResult();
			this.em.getTransaction().begin();
			this.em.remove(en);
			this.em.getTransaction().commit();
		} catch (Exception e) {
			logger.error(e.getMessage());
			return new OssResponse(this.getSession(),"ERROR", e.getMessage());
		} finally {
		}
		return new OssResponse(this.getSession(),"OK","Enumerate was removed succesfully.");
	}

	////////////////////////////////////////////////////////
	// Functions for setting firewall
	///////////////////////////////////////////////////////

	public Map<String, String> getFirewallIncomingRules() {
		String[] program   = new String[3];
		StringBuffer reply = new StringBuffer();
		StringBuffer error = new StringBuffer();
		program[0] = "/usr/bin/firewall-cmd";
		program[1] = "--zone=external";
		program[2] = "--list-services";
		OSSShellTools.exec(program, reply, error, null);
		Map<String,String> statusMap;
		//External Ports
		statusMap = new HashMap<>();
		statusMap.put("ssh", "false");
		statusMap.put("https", "false");
		statusMap.put("admin", "false");
		statusMap.put("rdesktop", "false");
		statusMap.put("other", "");
		for( String extPort : reply.toString().split(" ") ) {
			switch(extPort) {
			case "ssh":
				statusMap.put("ssh","true");
				break;
			case "https":
				statusMap.put("https", "true");
				break;
			case "admin":
				statusMap.put("admin", "true");
				break;
			case "rdp":
				statusMap.put("rdesktop", "true");
				break;
			default:
				statusMap.put("other",statusMap.get("other")+" "+ extPort);

			}
		}
		return statusMap;
	}

	public OssResponse setFirewallIncomingRules(Map<String, String> firewallExt) {
		String[] program   = new String[2];
		StringBuffer reply = new StringBuffer();
		StringBuffer error = new StringBuffer();
		program[0] = "/usr/share/oss/tools/set_firewall_external.sh";
		List<String> fwServicesExtTcp = new ArrayList<String>();
		if( firewallExt.get("ssh").equals("true") ) {
			fwServicesExtTcp.add("ssh");
		}
		if( firewallExt.get("https").equals("true")) {
			fwServicesExtTcp.add("https");
		}
		if( firewallExt.get("admin").equals("true")) {
			fwServicesExtTcp.add("admin");
		}
		if( firewallExt.get("rdesktop").equals("true") )  {
			fwServicesExtTcp.add("rdp");
		}
		if( firewallExt.get("other") != null && !firewallExt.get("other").isEmpty()) {
			fwServicesExtTcp.add(firewallExt.get("other"));
		}
		program[1] = String.join(",", fwServicesExtTcp);
		OSSShellTools.exec(program, reply, error, null);
		return new OssResponse(this.getSession(),"OK","Firewall incoming access rule  was set succesfully.");
	}
	//TODO This function must be adapted to firwalld
	public List<Map<String, String>> getFirewallOutgoingRules() {
		Config fwConfig = new Config("/etc/sysconfig/SuSEfirewall2","FW_");
		List<Map<String, String>> firewallList = new ArrayList<>();
		Map<String,String> statusMap;
		RoomController roomController = new RoomController(this.session,this.em);
		DeviceController deviceController = new DeviceController(this.session,this.em);

		for( String outRule : fwConfig.getConfigValue("MASQ_NETS").split(" ") ) {
			if (outRule.length() > 0) {
				statusMap = new HashMap<>();
				String[] rule = outRule.split(",");
				String[] host = rule[0].split("/");
				String   dest = rule[1];
				String   prot = rule.length > 2 ? rule[2] : "all";
				String   port = rule.length > 3 ? rule[3] : "all";
				if(host.length == 1 || host[1].equals("32")) {
					Device device = deviceController.getByIP(host[0]);
					if( device == null ) {
						continue;
					}
					statusMap.put("id", Long.toString(device.getId()));
					statusMap.put("name", device.getName());
					statusMap.put("type", "host");
				} else {
					if( host[1].equals(this.getConfigValue("NETMASK")) ) {
						statusMap.put("id", "0");
						statusMap.put("name", "INTRANET");
						statusMap.put("type", "room" );
					} else {
						Room room = roomController.getByIP(host[0]);
						if( room == null ) {
							continue;
						}
						statusMap.put("id", Long.toString(room.getId()));
						statusMap.put("name", room.getName());
						statusMap.put("type", "room" );
					}
				}
				statusMap.put("dest", dest);
				statusMap.put("prot", prot);
				statusMap.put("port", port);
				firewallList.add(statusMap);
			}
		}
		return firewallList;
	}

	//TODO This function must be adapted to firwalld
	public OssResponse setFirewallOutgoingRules(List<Map<String, String>> firewallList) {
		List<String> fwMasqNets = new ArrayList<String>();
		try {
			logger.debug(new ObjectMapper().writeValueAsString(firewallList));
		} catch (Exception e) {
			logger.debug("{ \"ERROR\" : \"CAN NOT MAP THE OBJECT\" }");
		}
		//TODO
		Config fwConfig = new Config("/etc/sysconfig/SuSEfirewall2","FW_");
		RoomController roomController = new RoomController(this.session,this.em);
		DeviceController deviceController = new DeviceController(this.session,this.em);
		Device device;
		Room   room;
		for( Map<String,String> map : firewallList ) {
			StringBuilder data = new StringBuilder();
			if( map.get("type").equals("room")) {
				if( map.get("id").equals("0") ) {
					data.append(this.getConfigValue("NETWORK")).append("/").append(this.getConfigValue("NETMASK")).append(",");
				} else {
					room = roomController.getById(Long.parseLong(map.get("id")));
					data.append(room.getStartIP()).append("/").append(String.valueOf(room.getNetMask())).append(",");
				}
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
		this.systemctl("try-restart", "SuSEfirewall2");
		return new OssResponse(this.getSession(),"OK","Firewall outgoing access rule  was set succesfully.");
	}

	//TODO This function must be adapted to firwalld
	public List<Map<String, String>> getFirewallRemoteAccessRules() {
		Config fwConfig = new Config("/etc/sysconfig/SuSEfirewall2","FW_");
		List<Map<String, String>> firewallList = new ArrayList<>();
		Map<String,String> statusMap;
		DeviceController deviceController = new DeviceController(this.session,this.em);

		for( String outRule : fwConfig.getConfigValue("FORWARD_MASQ").split(" ") ) {
		   if (outRule!=null && outRule.length()>0) {
			   statusMap = new HashMap<>();
			   String[] rule = outRule.split(",");
			   if (rule!=null && rule.length>=4) {
				   Device device = deviceController.getByIP(rule[1]);
				   if( device != null ) {
					   statusMap.put("ext", rule[3]);
					   statusMap.put("id",  Long.toString(device.getId()) );
					   statusMap.put("name", device.getName() );
					   if( rule.length > 3 ) {
						   statusMap.put("port", rule[4]);
					   } else {
						   statusMap.put("port", rule[3]);
					   }
					   firewallList.add(statusMap);
				   }
			   }
		   }
		}
		return firewallList;
	}

	//TODO This function must be adapted to firwalld
	public OssResponse setFirewallRemoteAccessRules(List<Map<String, String>> firewallList) {
		List<String> fwForwardMasq = new ArrayList<String>();
		Config fwConfig = new Config("/etc/sysconfig/SuSEfirewall2","FW_");
		DeviceController deviceController = new DeviceController(this.session,this.em);
		for( Map<String,String> map : firewallList ) {
			Device device = deviceController.getById(Long.parseLong(map.get("id")));
			fwForwardMasq.add("0/0," + device.getIp() + ",tcp," + map.get("ext") + "," + map.get("port") );
		}
		fwConfig.setConfigValue("FORWARD_MASQ", String.join(" ", fwForwardMasq));
		this.systemctl("try-restart", "SuSEfirewall2");
		return new OssResponse(this.getSession(),"OK","Firewall remote access rule  was set succesfully.");
	}

	/*
	 * Functions for package management of the system
	 */

	public Date getValidityOfRegcode() {
		StringBuilder url = new StringBuilder();
		url.append(this.getConfigValue("UPDATE_URL")).append("/api/customers/regcodes/").append(this.getConfigValue("REG_CODE"));
		try {
			String response = Request.Get(url.toString()).execute().returnContent().asString();
			Long milis = Long.parseLong(response);
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
		if( error.toString().isEmpty() ) {
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

	/**
	 * List of the available updates
	 * @return The list of packages can be updated
	 */
	public List<Map<String, String>> listUpdates() {
		Map<String,String>        update;
		List<Map<String, String>> updates = new ArrayList<Map<String,String>>();
		String[] program    = new String[3];
		StringBuffer reply  = new StringBuffer();
		StringBuffer stderr = new StringBuffer();
		program[0] = "/usr/bin/zypper";
		program[1] = "-nx";
		program[2] = "lu";
		program[3] = "-r";
		if( this.getConfigValue("TYPE").equals("cephalix") ) {
			program[4] = "CEPHALIX";
		} else {
			program[4] = "OSS";
		}
		OSSShellTools.exec(program, reply, stderr, null);
		try {
			Document doc = new SAXBuilder().build( new StringReader(reply.toString()) );
			logger.debug(reply.toString());
			Element rootNode = doc.getRootElement();
			Iterator<Element> processDescendants = rootNode.getDescendants(new ElementFilter("update"));
			while(processDescendants.hasNext()) {
				Element node = processDescendants.next();
				update = new HashMap<String,String>();
				update.put("name", node.getAttributeValue("name").substring(8));
				/*software.put("description", node.getAttributeValue("kind"));*/
				update.put("version-old", node.getAttributeValue("edition-old"));
				update.put("version", node.getAttributeValue("edition"));
				updates.add(update);
			}
		} catch(IOException e ) {
			logger.error("1 " + reply.toString());
			logger.error("1 " + stderr.toString());
			logger.error("1 " + e.getMessage());
			throw new WebApplicationException(500);
		} catch(JDOMException e)  {
			logger.error("2 " + reply.toString());
			logger.error("2 " + stderr.toString());
			logger.error("2 " + e.getMessage());
			throw new WebApplicationException(500);
		}
		return updates;
	}

	/**
	 * Update selected packages
	 * @param packages The list of packages to update
	 * @return The result of Update as OssResponse object
	 */
	public OssResponse updatePackages(List<String> packages) {
		String[] program   = new String[1 + packages.size()];
		StringBuffer reply = new StringBuffer();
		StringBuffer error = new StringBuffer();
		program[0] = "/usr/sbin/oss_update.sh";
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

	/**
	 * Update all packages
	 * @return The result of Update as OssResponse object
	 */
	public OssResponse updateSystem() {
		String[] program   = new String[1];
		StringBuffer reply = new StringBuffer();
		StringBuffer error = new StringBuffer();
		program[0] = "/usr/sbin/oss_update.sh";
		if( OSSShellTools.exec(program, reply, error, null) == 0 ) {
			return new OssResponse(this.getSession(),"OK","System was updated succesfully.");
		} else {
			return new OssResponse(this.getSession(),"ERROR",error.toString());
		}
	}

	/**
	 * Returns an ACL searched by the technical id.
	 * @param aclId
	 * @return The found acl.
	 */
	public Acl getAclById(Long aclId) {
		try {
			return this.em.find(Acl.class, aclId);
		} catch (Exception e) {
			return null;
		} finally {
		}
	}

	public List<Acl> getAvailableAcls() {
		List<Acl> acls = new ArrayList<Acl>();
		for( String aclName :this.getEnumerates("apiAcl") ) {
			acls.add(new Acl(aclName,false));
		}
		return acls;
	}

	public List<Acl> getAclsOfGroup(Long groupId) {
		return new GroupController(this.session,this.em).getById(groupId).getAcls();
	}

	public List<Acl> getAvailableAclsForGroup(Long groupId) {
		List<Acl> acls	= new ArrayList<Acl>();
		List<Acl> ownAcls = this.getAclsOfGroup(groupId);
		for( String aclName :this.getEnumerates("apiAcl") ) {
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
		Group group = new GroupController(this.session,this.em).getById(groupId);
		Acl oldAcl;
		logger.debug("Group acl to set: " + acl);
		try {
			oldAcl = this.em.find(Acl.class, acl.getId());
		} catch(Exception e) {
			oldAcl = null;
		}
		try {
			this.em.getTransaction().begin();
			if( oldAcl != null ) {
				if( acl.getAllowed() ) {
					oldAcl.setAllowed(true);
					this.em.merge(oldAcl);
				} else {
					group.getAcls().remove(oldAcl);
					this.em.merge(group);
					this.em.remove(oldAcl);
				}
			} else {
				acl.setGroup(group);
				acl.setCreator(this.session.getUser());
				this.em.persist(acl);
				group.addAcl(acl);
				this.em.merge(group);
			}
			this.em.getTransaction().commit();
		} catch(Exception e) {
			logger.debug("ERROR in setAclToGroup:" + e.getMessage());
			return new OssResponse(session,"ERROR",e.getMessage());
		} finally {
		}
		return new OssResponse(session,"OK","ACL was set succesfully.");
	}

	public List<Acl> getAclsOfUser(Long userId) {
		User         user     = new UserController(this.session,this.em).getById(userId);
		List<Acl>    acls     = user.getAcls();
		List<String> aclNames = new ArrayList<String>();
		for( Acl acl : user.getAcls() ){
			aclNames.add(acl.getAcl());
		}
		for( Group group : user.getGroups() ) {
			for( Acl acl : group.getAcls() ) {
				if( !aclNames.contains(acl.getAcl())) {
					acls.add(acl);
				}
			}
		}
		acls.sort(Comparator.comparing(Acl::getAcl));
		return acls;
	}

	public boolean hasUsersGroupAcl(User user, Acl acl ) {
		for( Group group : user.getGroups() ) {
			for( Acl groupAcl : group.getAcls() ) {
				if( acl.getAcl().equals(groupAcl.getAcl()) &&
					( acl.getAllowed() == groupAcl.getAllowed() ) ) {
					return true;
				}
			}
		}
		return false;
	}

	public List<Acl> getAvailableAclsForUser(Long userId) {
		List<Acl> acls	= new ArrayList<Acl>();
		List<Acl> ownAcls = this.getAclsOfUser(userId);
		for( String aclName :this.getEnumerates("apiAcl") ) {
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
		acls.sort(Comparator.comparing(Acl::getAcl));
		return acls;
	}

	public OssResponse setAclToUser(Long userId, Acl acl) {
		User user   = new UserController(this.session,this.em).getById(userId);
		Acl oldAcl  = null;
		logger.debug("User acl to set: " + acl);
		try {
			oldAcl = this.em.find(Acl.class, acl.getId());
		} catch(Exception e) {
			oldAcl = null;
		}
		try {
			this.em.getTransaction().begin();
			if( oldAcl != null && oldAcl.getUser() != null && oldAcl.getUser().equals(user) ) {
				logger.debug("User old to modify: " + oldAcl);
				if( !this.hasUsersGroupAcl(user,acl)) {
					user.getAcls().remove(oldAcl);
					this.em.remove(oldAcl);
					this.em.merge(user);
				} else {
					oldAcl.setAllowed(acl.getAllowed());
					this.em.merge(oldAcl);
				}
			} else  {
				logger.debug("This is a new acl.");
				acl.setGroup(null);
				acl.setUser(user);
				acl.setCreator(this.session.getUser());
				this.em.persist(acl);
				user.addAcl(acl);
				this.em.merge(user);
			}
			this.em.getTransaction().commit();
		} catch(Exception e) {
			logger.debug("ERROR in setAclToUser:" + e.getMessage());
			return new OssResponse(session,"ERROR",e.getMessage());
		} finally {
		}
		return new OssResponse(session,"OK","ACL was set succesfully.");
	}

	public String[] getDnsDomains() {
		String[] program   = new String[1];
		StringBuffer reply = new StringBuffer();
		StringBuffer error = new StringBuffer();
		program[0] = "/usr/sbin/oss_get_dns_domains.sh";
		OSSShellTools.exec(program, reply, error, null);
		return reply.toString().split("\\n");
	}

	public OssResponse addDnsDomain(String domainName) {
		String[] program   = new String[7];
		StringBuffer reply = new StringBuffer();
		StringBuffer error = new StringBuffer();
		program[0] = "/usr/bin/samba-tool";
		program[1] = "dns";
		program[2] = "zonecreate";
		program[3] = "localhost";
		program[4] = domainName;
		program[5] = "-U";
		program[6] = "register%" + this.getProperty("de.openschoolserver.dao.User.Register.Password");
		OSSShellTools.exec(program, reply, error, null);
		//TODO evaluate error
		return new OssResponse(session,"OK","DNS Zone was created succesfully.");
	}

	public List<DnsRecord> getRecords(String domainName) {
		List<DnsRecord> dnsRecords = new ArrayList<DnsRecord>();
		String[] program   = new String[2];
		StringBuffer reply = new StringBuffer();
		StringBuffer error = new StringBuffer();
		program[0] = "/usr/sbin/oss_dump_dns_domain.sh";
		program[1] = domainName;
		OSSShellTools.exec(program, reply, error, null);

		String name = null;
		String type = null;
		String data = null;
		String patternNameString = "Name=(.+?), Records";
		String patternTypeString = "(.+?): (.+?) \\(flags";
		Pattern patternName = Pattern.compile(patternNameString);
		Pattern patternType = Pattern.compile(patternTypeString);
		DeviceController dc = new DeviceController(this.session,this.em);
		for( String line : reply.toString().split(this.getNl()) ) {
			Matcher matcher = patternName.matcher(line);
			while(matcher.find()) {
				name = matcher.group(1);
				continue;
			}
			matcher = patternType.matcher(line);
			while(matcher.find()) {
				if( name == null ) {
					continue;
				}
				type = matcher.group(1);
				data = matcher.group(2);
				Device device = dc.getByName(name);
				if( device != null && device.getIp().equals(data) ) {
					continue;
				}
				DnsRecord dnsRecord = new DnsRecord(domainName,type,name,data);
				dnsRecords.add(dnsRecord);
			}
		}
		return dnsRecords;
	}

	public OssResponse addDnsRecord(DnsRecord dnsRecord) {
		String[] program   = new String[10];
		StringBuffer reply = new StringBuffer();
		StringBuffer error = new StringBuffer();
		program[0] = "/usr/bin/samba-tool";
		program[1] = "dns";
		program[2] = "add";
		program[3] = "localhost";
		program[4] = dnsRecord.getDomainName();
		program[5] = dnsRecord.getRecordName();
		program[6] = dnsRecord.getRecordType();
		program[7] = dnsRecord.getRecordData();
		program[8] = "-U";
		program[9] = "register%" + this.getProperty("de.openschoolserver.dao.User.Register.Password");
		OSSShellTools.exec(program, reply, error, null);
		//TODO evaluate error
		logger.debug("addDnsRecord reply" + reply.toString());
		logger.debug("addDnsRecord error" + error.toString());
		if( error.toString().isEmpty() ) {
			return new OssResponse(session,"OK","DNS record was created succesfully.");
		} else {
			return new OssResponse(session,"ERROR",error.toString());
		}
	}

	public OssResponse deleteDnsRecord(DnsRecord dnsRecord) {
		String[] program   = new String[10];
		StringBuffer reply = new StringBuffer();
		StringBuffer error = new StringBuffer();
		program[0] = "/usr/bin/samba-tool";
		program[1] = "dns";
		program[2] = "delete";
		program[3] = "localhost";
		program[4] = dnsRecord.getDomainName();
		program[5] = dnsRecord.getRecordName();
		program[6] = dnsRecord.getRecordType();
		program[7] = dnsRecord.getRecordData();
		program[8] = "-U";
		program[9] = "register%" + this.getProperty("de.openschoolserver.dao.User.Register.Password");
		OSSShellTools.exec(program, reply, error, null);
		logger.debug("deleteDnsRecord reply" + reply.toString());
		logger.debug("deleteDnsRecord error" + error.toString());
		if( error.toString().isEmpty() ) {
			return new OssResponse(session,"OK","DNS record was created succesfully.");
		} else {
			logger.error(
					dnsRecord.getDomainName() + "#" +
					dnsRecord.getRecordName() + "#" +
					dnsRecord.getRecordType() + "#" +
					dnsRecord.getRecordData() + "# " + error.toString());
			return new OssResponse(session,"ERROR",error.toString());
		}
	}

	public OssResponse deleteDnsDomain(String domainName) {
		String[] program   = new String[7];
		StringBuffer reply = new StringBuffer();
		StringBuffer error = new StringBuffer();
		program[0] = "/usr/bin/samba-tool";
		program[1] = "dns";
		program[2] = "zonedelete";
		program[3] = "localhost";
		program[4] = domainName;
		program[5] = "-U";
		program[6] = "register%" + this.getProperty("de.openschoolserver.dao.User.Register.Password");
		OSSShellTools.exec(program, reply, error, null);
		//TODO evaluate error
		return new OssResponse(session,"OK","DNS Zone was created succesfully.");	}

	public OssResponse findObject(String objectType, LinkedHashMap<String,Object> object) {
		Long objectId = null;
		String name = null;
		//TODO Implement all searches
		switch(objectType.toLowerCase()) {
		case "acl":

			break;
		case "accessinroom":
			break;
		case "announcement":
			break;
		case "contact":
			break;
		case "category":
			name = (String) object.get("name");
			Category category = new CategoryController(this.session,this.em).getByName(name);
			if( category != null ) {
				objectId = category.getId();
			}
			break;
		case "faq":
			break;
		case "device":
			break;
		case "group":
			break;
		case "hwconf":
			break;
		case "ossonfig":
			break;
		case "ossmonfig":
			break;
		case "room":
			break;
		case "software":
			name = (String) object.get("name");
			Software software = new SoftwareController(this.session,this.em).getByName(name);
			if( software != null ) {
				objectId = software.getId();
			}
			break;
		case "softwarelicence":
			break;
		case "user":
			break;
		}
		if( objectId == null ) {
			return new OssResponse(session,"ERROR","Object was not found.");
		} else {
			return new OssResponse(session,"OK","Object was found.",objectId);
		}
	}

}
