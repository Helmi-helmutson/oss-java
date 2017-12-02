package de.openschoolserver.api.resourceimpl;

import java.util.List;

import java.util.Map;

import de.openschoolserver.api.resources.SystemResource;
import de.openschoolserver.dao.MissedTranslation;
import de.openschoolserver.dao.OssResponse;
import de.openschoolserver.dao.Session;
import de.openschoolserver.dao.Translation;
import de.openschoolserver.dao.controller.SystemController;
import de.openschoolserver.dao.controller.ProxyController;

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
	public  List<Map<String,Map<String,Boolean>>> getProxyDefault(Session session) {
		return new ProxyController(session).readDefaults();
	}

	@Override
	public OssResponse setProxyDefault(Session session, List<Map<String, Map<String, Boolean>>> acls) {
		return new ProxyController(session).setDefaults(acls);
	}
}
