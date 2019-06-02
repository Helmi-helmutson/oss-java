/* (c) Péter Varkoly <peter@varkoly.de> - all rights reserved */
package de.openschoolserver.api.resourceimpl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.UriInfo;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.openschoolserver.api.resources.SystemResource;
import de.openschoolserver.dao.Acl;
import de.openschoolserver.dao.DnsRecord;
import de.openschoolserver.dao.Group;
import de.openschoolserver.dao.Job;
import de.openschoolserver.dao.OssResponse;
import de.openschoolserver.dao.ProxyRule;
import de.openschoolserver.dao.Session;
import de.openschoolserver.dao.Translation;
import de.openschoolserver.dao.User;
import de.openschoolserver.dao.controller.SystemController;
import de.openschoolserver.dao.internal.CommonEntityManagerFactory;
import de.openschoolserver.dao.tools.OSSShellTools;
import de.openschoolserver.dao.controller.ProxyController;
import de.openschoolserver.dao.controller.SessionController;
import de.openschoolserver.dao.controller.Controller;
import de.openschoolserver.dao.controller.JobController;

public class SystemResourceImpl implements SystemResource {

	Logger logger = LoggerFactory.getLogger(SystemResourceImpl.class);

	public SystemResourceImpl() {
	}

	@Override
	public Object getStatus(Session session) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		SystemController systemController = new SystemController(session,em);
		Object resp = systemController.getStatus();
		em.close();
		return resp;
	}

	@Override
	public Object getDiskStatus(Session session) {
		String[] program    = new String[1];
		StringBuffer reply  = new StringBuffer();
		StringBuffer stderr = new StringBuffer();
		program[0] = "/usr/share/oss/tools/check_partitions.sh";
		OSSShellTools.exec(program, reply, stderr, null);
		return reply.toString();
	}

	@Override
	public OssResponse customize(Session session, InputStream fileInputStream,
			FormDataContentDisposition contentDispositionHeader) {
		String fileName = contentDispositionHeader.getFileName();
		File file = new File("/srv/www/admin/assets/" + fileName );
		try {
			Files.copy(fileInputStream, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			return new OssResponse(session,"ERROR", e.getMessage());
		}
		return new OssResponse(session,"OK", "File was saved succesfully.");
	}

	@Override
	public List<String> getEnumerates(Session session, String type) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		SystemController systemController = new SystemController(session,em);
		List<String> resp = systemController.getEnumerates(type);
		em.close();
		return resp;
	}

	@Override
	public OssResponse addEnumerate(Session session, String type, String value) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		SystemController systemController = new SystemController(session,em);
		OssResponse resp = systemController.addEnumerate(type, value);
		em.close();
		return resp;
	}

	@Override
	public OssResponse deleteEnumerate(Session session, String type, String value) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		SystemController systemController = new SystemController(session,em);
		OssResponse resp = systemController.deleteEnumerate(type, value);
		em.close();
		return resp;
	}

	@Override
	public List<Map<String, String>> getConfig(Session session) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		SystemController systemController = new SystemController(session,em);
		List<Map<String, String>> resp = systemController.getConfig();
		em.close();
		return resp;
	}

	@Override
	public String getConfig(Session session, String key) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		SystemController systemController = new SystemController(session,em);
		String resp = systemController.getConfigValue(key);
		em.close();
		return resp;
	}

	@Override
	public OssResponse setConfig(Session session, String key, String value) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		SystemController systemController = new SystemController(session,em);
		if( systemController.setConfigValue(key, value) ) {
			em.close();
			return new OssResponse(session,"OK","Global configuration value was set succesfully.");
		} else {
			em.close();
			return new OssResponse(session,"ERROR","Global configuration value could not be set.");
		}
	}

	@Override
	public OssResponse setConfig(Session session, Map<String, String> config) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		SystemController systemController = new SystemController(session,em);
		try {
			if( systemController.setConfigValue(config.get("key"), config.get("value")) ) {
				return new OssResponse(session,"OK","Global configuration value was set succesfully.");
			} else {
				return new OssResponse(session,"ERROR","Global configuration value could not be set.");
			}
		} catch(Exception e) {
			return new OssResponse(session,"ERROR","Global configuration value could not be set.");
		} finally {
			em.close();
		}
	}

	@Override
	public Map<String, String> getFirewallIncomingRules(Session session) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		SystemController systemController = new SystemController(session,em);
		Map<String, String> resp = systemController.getFirewallIncomingRules();
		em.close();
		return resp;
	}

	@Override
	public OssResponse setFirewallIncomingRules(Session session, Map<String, String> incommingRules) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		SystemController systemController = new SystemController(session,em);
		OssResponse resp = systemController.setFirewallIncomingRules(incommingRules);
		em.close();
		return resp;
	}

	@Override
	public List<Map<String, String>> getFirewallOutgoingRules(Session session) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		SystemController systemController = new SystemController(session,em);
		List<Map<String, String>> resp = systemController.getFirewallOutgoingRules();
		em.close();
		return resp;
	}

	@Override
	public OssResponse setFirewallOutgoingRules(Session session, List<Map<String, String>> outgoingRules) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		SystemController systemController = new SystemController(session,em);
		OssResponse resp = systemController.setFirewallOutgoingRules(outgoingRules);
		em.close();
		return resp;
	}

	@Override
	public List<Map<String, String>> getFirewallRemoteAccessRules(Session session) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		SystemController systemController = new SystemController(session,em);
		List<Map<String, String>> resp =  systemController.getFirewallRemoteAccessRules();
		em.close();
		return resp;
	}

	@Override
	public OssResponse setFirewallRemoteAccessRules(Session session, List<Map<String, String>> remoteAccessRules) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		SystemController systemController = new SystemController(session,em);
		OssResponse resp = systemController.setFirewallRemoteAccessRules(remoteAccessRules);
		em.close();
		return resp;
	}

	@Override
	public String translate(Session session, Translation translation) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		String resp = new SystemController(session,em).translate(translation.getLang(), translation.getString());
		em.close();
		return resp;
	}

	@Override
	public OssResponse addTranslation(Session session, Translation translation) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		OssResponse resp = new SystemController(session,em).addTranslation(translation);
		em.close();
		return resp;
	}

	@Override
	public List<Translation> getMissedTranslations(Session session, String lang) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		List<Translation> resp = new SystemController(session,em).getMissedTranslations(lang);
		em.close();
		return resp;
	}

	@Override
	public OssResponse register(Session session) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		OssResponse resp = new SystemController(session,em).registerSystem();
		em.close();
		return resp;
	}

	@Override
	public List<Map<String, String>> searchPackages(Session session, String filter) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		List<Map<String, String>> resp = new SystemController(session,em).searchPackages(filter);
		em.close();
		return resp;
	}

	@Override
	public OssResponse installPackages(Session session, List<String> packages) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		OssResponse resp = new SystemController(session,em).installPackages(packages);
		em.close();
		return resp;
	}

	@Override
	public OssResponse updatePackages(Session session, List<String> packages) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		OssResponse resp = new SystemController(session,em).updatePackages(packages);
		em.close();
		return resp;
	}

	@Override
	public OssResponse updateSyste(Session session) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		OssResponse resp = new SystemController(session,em).updateSystem();
		em.close();
		return resp;
	}

	@Override
	public  List<ProxyRule> getProxyDefault(Session session, String role) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		List<ProxyRule> resp = new ProxyController(session,em).readDefaults(role);
		em.close();
		return resp;
	}

	@Override
	public OssResponse setProxyDefault(Session session, String role, List<ProxyRule> acl) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		OssResponse resp = new ProxyController(session,em).setDefaults(role, acl);
		em.close();
		return resp;
	}

	@Override
	public Map<String, List<ProxyRule>> getProxyDefaults(Session session) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		Map<String, List<ProxyRule>> resp = new ProxyController(session,em).readDefaults();
		em.close();
		return resp;
	}

	@Override
	public OssResponse setProxyDefaults(Session session, String role, Map<String, List<ProxyRule>> acls) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		OssResponse resp = new ProxyController(session,em).setDefaults(acls);
		em.close();
		return resp;
	}

	@Override
	public List<String> getTheCustomList(Session session, String list) {
		try {
			return	Files.readAllLines(Paths.get("/var/lib/squidGuard/db/custom/" +list + "/domains"));
		}
		catch( IOException e ) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public OssResponse setTheCustomList(Session session, String list, List<String> domains) {
		try {
			Files.write(Paths.get("/var/lib/squidGuard/db/custom/" +list + "/domains"),domains);
			String[] program   = new String[5];
			StringBuffer reply = new StringBuffer();
			StringBuffer error = new StringBuffer();
			program[0] = "/usr/sbin/squidGuard";
			program[1] = "-c";
			program[2] = "/etc/squid/squidguard.conf";
			program[3] = "-C";
			program[4] = "custom/" +list + "/domains";
			OSSShellTools.exec(program, reply, error, null);
			new Controller(session,null).systemctl("try-restart", "squid");
			return new OssResponse(session,"OK","Custom list was written successfully");
		} catch( IOException e ) {
			e.printStackTrace();
		}
		return new OssResponse(session,"ERROR","Could not write custom list.");
	}

	@Override
	public OssResponse createJob(Session session, Job job) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		return new JobController(session,em).createJob(job);
	}

	@Override
	public List<Job> searchJob(Session session, Job job ) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		return new JobController(session,em).searchJobs(job.getDescription(), job.getStartTime(), job.getEndTime());
	}

	@Override
	public Job getJob(Session session, Long jobId) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		return new JobController(session,em).getById(jobId);
	}

	@Override
	public OssResponse setJobExitValue(Session session, Long jobId, Integer exitValue) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		OssResponse resp = new JobController(session,em).setExitCode(jobId, exitValue);
		em.close();
		return resp;
	}

	@Override
	public OssResponse restartJob(Session session, Long jobId) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		OssResponse resp = new JobController(session,em).restartJob(jobId);
		em.close();
		return resp;
	}

	/*
	 * (non-Javadoc)
	 * ACL management
	 */
	@Override
	public List<Acl> getAcls(Session session) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		List<Acl> resp = new SystemController(session,em).getAvailableAcls();
		em.close();
		return resp;
	}

	@Override
	public List<Acl> getAclsOfGroup(Session session, Long groupId) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		List<Acl> resp = new SystemController(session,em).getAclsOfGroup(groupId);
		em.close();
		return resp;
	}

	@Override
	public OssResponse setAclOfGroup(Session session, Long groupId, Acl acl) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		OssResponse resp = new SystemController(session,em).setAclToGroup(groupId,acl);
		em.close();
		return resp;
	}

	@Override
	public List<Acl> getAvailableAclsForGroup(Session session, Long groupId) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		List<Acl> resp = new SystemController(session,em).getAvailableAclsForGroup(groupId);
		em.close();
		return resp;
	}

	@Override
	public OssResponse deleteAclsOfGroup(Session session, Long groupId) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		Group group = em.find(Group.class, groupId);
		OssResponse resp = new OssResponse(session,"OK","Acls was deleted succesfully.");
		if( group != null ) {
			em.getTransaction().begin();
			for(Acl acl : group.getAcls() ) {
				em.remove(acl);
			}
			group.setAcls(new ArrayList<Acl>());
			em.merge(group);
			em.getTransaction().commit();
		} else {
			resp = new OssResponse(session,"ERROR","Group can not be find.");
		}
		return resp;
	}

	@Override
	public List<Acl> getAclsOfUser(Session session, Long userId) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		List<Acl> resp = new SystemController(session,em).getAclsOfUser(userId);
		em.close();
		return resp;
	}

	@Override
	public OssResponse setAclOfUser(Session session, Long userId, Acl acl) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		OssResponse resp = new SystemController(session,em).setAclToUser(userId,acl);
		em.close();
		return resp;
	}

	@Override
	public List<Acl> getAvailableAclsForUser(Session session, Long userId) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		List<Acl> resp = new SystemController(session,em).getAvailableAclsForUser(userId);
		em.close();
		return resp;
	}

	@Override
	public OssResponse deleteAclsOfUser(Session session, Long userId) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		User user = em.find(User.class, userId);
		OssResponse resp = new OssResponse(session,"OK","Acls was deleted succesfully.");
		if( user != null ) {
			em.getTransaction().begin();
			for(Acl acl : user.getAcls() ) {
				em.remove(acl);
			}
			user.setAcls(new ArrayList<Acl>());
			em.merge(user);
			em.getTransaction().commit();
		} else {
			resp = new OssResponse(session,"ERROR","Group can not be find.");
		}
		return resp;
	}

	@Override
	public String getName(UriInfo ui, HttpServletRequest req) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		Session session  = new SessionController(em).getLocalhostSession();
		String resp = new SystemController(session,em).getConfigValue("NAME");
		em.close();
		return resp;
	}

	@Override
	public String getType(UriInfo ui, HttpServletRequest req) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		Session session  = new SessionController(em).getLocalhostSession();
		String resp = new SystemController(session,em).getConfigValue("TYPE");
		em.close();
		return resp;
	}

	@Override
	public List<Job> getRunningJobs(Session session) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		List<Job> resp = new JobController(session,em).getRunningJobs();
		em.close();
		return resp;
	}

	@Override
	public List<Job> getFailedJobs(Session session) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		List<Job> resp = new JobController(session,em).getFailedJobs();
		em.close();
		return resp;
	}

	@Override
	public List<Job> getSucceededJobs(Session session) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		List<Job> resp = new JobController(session,em).getSucceededJobs();
		em.close();
		return resp;
	}

	@Override
	public String[] getDnsDomains(Session session) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		String[] resp = new SystemController(session,em).getDnsDomains();
		em.close();
		return resp;
	}

	@Override
	public OssResponse addDnsDomain(Session session, String domainName) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		OssResponse resp = new SystemController(session,em).addDnsDomain(domainName);
		em.close();
		return resp;
	}

	@Override
	public OssResponse deleteDnsDomain(Session session, String domainName) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		OssResponse resp = new SystemController(session,em).deleteDnsDomain(domainName);
		em.close();
		return resp;
	}
	@Override
	public List<DnsRecord> getRecords(Session session, String domainName) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		List<DnsRecord> resp = new SystemController(session,em).getRecords(domainName);
		em.close();
		return resp;
	}

	@Override
	public OssResponse addDnsRecord(Session session, DnsRecord dnsRecord) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		OssResponse resp = new SystemController(session,em).addDnsRecord(dnsRecord);
		em.close();
		return resp;
	}

	@Override
	public OssResponse deleteDnsRecord(Session session, DnsRecord dnsRecord) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		OssResponse resp = new SystemController(session,em).deleteDnsRecord(dnsRecord);
		em.close();
		return resp;
	}

	@Override
	public OssResponse findObject(Session session, String objectType, LinkedHashMap<String,Object> object) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		OssResponse resp = new SystemController(session,em).findObject(objectType, object);
		em.close();
		return resp;
	}

	@Override
	public Response getFile(Session session, String path) {
		logger.debug("getFile" + path);
		File file = new File(path);
		ResponseBuilder response = Response.ok((Object) file);
		response.header("Content-Disposition","attachment; filename=\""+ file.getName() + "\"");
		return response.build();
	}

	@Override
	public Object getServicesStatus(Session session) {
		String[] program    = new String[1];
		StringBuffer reply  = new StringBuffer();
		StringBuffer stderr = new StringBuffer();
		program[0] = "/usr/share/oss/tools/check_services.sh";
		OSSShellTools.exec(program, reply, stderr, null);
		return reply.toString();
	}

	@Override
	public OssResponse setServicesStatus(Session session, String name, String what, String value) {
		String[] program    = new String[3];
		StringBuffer reply  = new StringBuffer();
		StringBuffer stderr = new StringBuffer();
		program[0] = "/usr/bin/systemctl";
		program[2] = name;
		if( what.equals("enabled") ) {
			if( value.toLowerCase().equals("true")) {
				program[1] = "enable";
			} else {
				program[1] = "disable";
			}
		} else {
			if( value.toLowerCase().equals("true")) {
				program[1] = "start";
			} else if(value.toLowerCase().equals("false") ) {
				program[1] = "stop";
			} else {
				program[1] = "restart";
			}
		}
		logger.debug(program[0] + " " + program[1] + " " + program[2]);
		if( OSSShellTools.exec(program, reply, stderr, null) == 0 ) {
			return new OssResponse(session,"OK","Service state was set successfully.");
		} else {
			return new OssResponse(session,"ERROR",stderr.toString());
		}
	}

	@Override
	public OssResponse applyActionForAddon(Session session, String name, String action) {
		String[] program    = new String[2];
		StringBuffer reply  = new StringBuffer();
		StringBuffer stderr = new StringBuffer();
		program[0] = "/usr/share/oss/addons/" + name + "/action.sh";
		program[1] =  action;
		if( OSSShellTools.exec(program, reply, stderr, null) == 0 ) {
			return new OssResponse(session,"OK","Service state was set successfully.");
		} else {
			return new OssResponse(session,"ERROR",stderr.toString());
		}
	}

	@Override
	public String[] getDataFromAddon(Session session, String name, String key) {
		String[] program    = new String[2];
		StringBuffer reply  = new StringBuffer();
		StringBuffer stderr = new StringBuffer();
		program[0] = "/usr/share/oss/addons/" + name + "/getvalue.sh";
		program[1] = key;
		OSSShellTools.exec(program, reply, stderr, null);
		return reply.toString().split("\\s");
	}

	@Override
	public List<String> getAddOns(Session session) {
		List<String> res = new ArrayList<String>();
		File addonsDir = new File( "/usr/share/oss/addons" );
		if( addonsDir != null && addonsDir.exists() ) {
			for( String addon : addonsDir.list() ) {
				File tmp = new File("/usr/share/oss/addons/" + addon);
				if( tmp.isDirectory() ) {
					res.add(addon);
				}
			}
		}
		return res;
	}

}
