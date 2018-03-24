/* (c) 2017 Péter Varkoly <peter@varkoly.de> - all rights reserved */
package de.openschoolserver.api.resourceimpl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import de.openschoolserver.api.resources.SystemResource;
import de.openschoolserver.dao.Acl;
import de.openschoolserver.dao.Job;
import de.openschoolserver.dao.MissedTranslation;
import de.openschoolserver.dao.OssResponse;
import de.openschoolserver.dao.ProxyRule;
import de.openschoolserver.dao.Session;
import de.openschoolserver.dao.Translation;
import de.openschoolserver.dao.controller.SystemController;
import de.openschoolserver.dao.controller.ProxyController;
import de.openschoolserver.dao.controller.JobController;

public class SystemResourceImpl implements SystemResource {

	@Override
	public List<Map<String, String>> getStatus(Session session) {
		SystemController systemController = new SystemController(session);
		return systemController.getStatus();
	}

	@Override
	public List<String> getEnumerates(Session session, String type) {
		SystemController systemController = new SystemController(session);
		return systemController.getEnumerates(type);
	}

	@Override
	public OssResponse addEnumerate(Session session, String type, String value) {
		SystemController systemController = new SystemController(session);
		return systemController.addEnumerate(type, value);
	}

	@Override
	public OssResponse deleteEnumerate(Session session, String type, String value) {
		SystemController systemController = new SystemController(session);
		return systemController.deleteEnumerate(type, value);
	}

	@Override
	public List<Map<String, String>> getConfig(Session session) {
		SystemController systemController = new SystemController(session);
		return systemController.getConfig();
	}

	@Override
	public String getConfig(Session session, String key) {
		SystemController systemController = new SystemController(session);
		return systemController.getConfigValue(key);
	}

	@Override
	public OssResponse setConfig(Session session, String key, String value) {
		SystemController systemController = new SystemController(session);
		if( systemController.setConfigValue(key, value) )
			return new OssResponse(session,"OK","Global configuration value was set succesfully."); 
		else
			return new OssResponse(session,"ERROR","Global configuration value could not be set.");
	}

	@Override
	public Map<String, String> getFirewallIncomingRules(Session session) {
		SystemController systemController = new SystemController(session);
		return systemController.getFirewallIncomingRules();
	}

	@Override
	public OssResponse setFirewallIncomingRules(Session session, Map<String, String> incommingRules) {
		SystemController systemController = new SystemController(session);
		return systemController.setFirewallIncomingRules(incommingRules);
	}

	@Override
	public List<Map<String, String>> getFirewallOutgoingRules(Session session) {
		SystemController systemController = new SystemController(session);
		return systemController.getFirewallOutgoingRules();
	}

	@Override
	public OssResponse setFirewallOutgoingRules(Session session, List<Map<String, String>> outgoingRules) {
		SystemController systemController = new SystemController(session);
		return systemController.setFirewallOutgoingRules(outgoingRules);
	}

	@Override
	public List<Map<String, String>> getFirewallRemoteAccessRules(Session session) {
		SystemController systemController = new SystemController(session);
		return systemController.getFirewallRemoteAccessRules();
	}

	@Override
	public OssResponse setFirewallRemoteAccessRules(Session session, List<Map<String, String>> remoteAccessRules) {
		SystemController systemController = new SystemController(session);
		return systemController.setFirewallRemoteAccessRules(remoteAccessRules);
		}

	@Override
	public String translate(Session session, MissedTranslation missedTranslation) {
		return new SystemController(session).translate(missedTranslation.getLang(), missedTranslation.getString());
	}

	@Override
	public OssResponse addTranslation(Session session, Translation translation) {
		return new SystemController(session).addTranslation(translation);
	}

	@Override
	public List<String> getMissedTranslations(Session session, String lang) {
		return new SystemController(session).getMissedTranslations(lang);
	}

	@Override
	public OssResponse register(Session session) {
		return new SystemController(session).registerSystem();
	}

	@Override
	public List<Map<String, String>> searchPackages(Session session, String filter) {
		return new SystemController(session).searchPackages(filter);
	}

	@Override
	public OssResponse installPackages(Session session, List<String> packages) {
		return new SystemController(session).installPackages(packages);
	}

	@Override
	public OssResponse updatePackages(Session session, List<String> packages) {
		return new SystemController(session).updatePackages(packages);
	}

	@Override
	public OssResponse updateSyste(Session session) {
		return new SystemController(session).updateSystem();
	}

	@Override
	public  List<ProxyRule> getProxyDefault(Session session, String role) {
		return new ProxyController(session).readDefaults(role);
	}

	@Override
	public OssResponse setProxyDefault(Session session, String role, List<ProxyRule> acl) {
		return new ProxyController(session).setDefaults(role, acl);
	}

	@Override
	public String getTheCustomList(Session session, String list) {
		try {
			return	Files.readAllLines(Paths.get("/var/lib/squidGuard/db/custom/" +list + "/domains")).toString();
		}
		catch( IOException e ) { 
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public OssResponse setTheCustomList(Session session, String list, String domains) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OssResponse createJob(Session session, Job job) {
		return new JobController(session).createJob(job);
	}

	@Override
	public List<Job> searchJob(Session session, Job job ) {
		return new JobController(session).searchJobs(job.getDescription(), job.getStartTime(), job.getEndTime());
	}

	@Override
	public Job getJob(Session session, Long jobId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OssResponse setJobExitValue(Session session, Long jobId, Integer exitValue) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OssResponse restartJob(Session session, Long jobId) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * ACL management
	 */
	@Override
	public List<Acl> getAcls(Session session) {
		return new SystemController(session).getAvailableAcls();
	}

	@Override
	public List<Acl> getAclsOfGroup(Session session, Long groupId) {
		return new SystemController(session).getAclsOfGroup(groupId);
	}

	@Override
	public OssResponse setAclOfGroup(Session session, Long groupId, Acl acl) {
		return new SystemController(session).setAclToGroup(groupId,acl);
	}

	@Override
	public List<Acl> getAclsOfUser(Session session, Long userId) {
		return new SystemController(session).getAclsOfUser(userId);
	}

	@Override
	public OssResponse setAclOfUser(Session session, Long userId, Acl acl) {
		return new SystemController(session).setAclToUser(userId,acl);
	}



}
