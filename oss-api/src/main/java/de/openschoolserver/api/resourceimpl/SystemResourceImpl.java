package de.openschoolserver.api.resourceimpl;

import java.util.List;

import java.util.Map;

import de.openschoolserver.api.resources.SystemResource;
import de.openschoolserver.dao.MissedTranslation;
import de.openschoolserver.dao.Response;
import de.openschoolserver.dao.Session;
import de.openschoolserver.dao.Translation;
import de.openschoolserver.dao.controller.SystemController;

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
	public Response addEnumerate(Session session, String type, String value) {
		SystemController systemController = new SystemController(session);
		return systemController.addEnumerate(type, value);
	}

	@Override
	public Response deleteEnumerate(Session session, String type, String value) {
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
	public Response setConfig(Session session, String key, String value) {
		SystemController systemController = new SystemController(session);
		if( systemController.setConfigValue(key, value) )
			return new Response(session,"OK","Global configuration value was set succesfully."); 
		else
			return new Response(session,"ERROR","Global configuration value could not be set.");
	}

	@Override
	public Map<String, String> getFirewallIncomingRules(Session session) {
		SystemController systemController = new SystemController(session);
		return systemController.getFirewallIncomingRules();
	}

	@Override
	public Response setFirewallIncomingRules(Session session, Map<String, String> incommingRules) {
		SystemController systemController = new SystemController(session);
		return systemController.setFirewallIncomingRules(incommingRules);
	}

	@Override
	public List<Map<String, String>> getFirewallOutgoingRules(Session session) {
		SystemController systemController = new SystemController(session);
		return systemController.getFirewallOutgoingRules();
	}

	@Override
	public Response setFirewallOutgoingRules(Session session, List<Map<String, String>> outgoingRules) {
		SystemController systemController = new SystemController(session);
		return systemController.setFirewallOutgoingRules(outgoingRules);
	}

	@Override
	public List<Map<String, String>> getFirewallRemoteAccessRules(Session session) {
		SystemController systemController = new SystemController(session);
		return systemController.getFirewallRemoteAccessRules();
	}

	@Override
	public Response setFirewallRemoteAccessRules(Session session, List<Map<String, String>> remoteAccessRules) {
		SystemController systemController = new SystemController(session);
		return systemController.setFirewallRemoteAccessRules(remoteAccessRules);
		}

	@Override
	public String translate(Session session, MissedTranslation missedTranslataion) {
		return new SystemController(session).translate(missedTranslataion.getLang(), missedTranslataion.getString());
	}

	@Override
	public Response addTranslation(Session session, Translation translation) {
		return new SystemController(session).addTranslation(translation);
	}

	@Override
	public List<String> getMissedTranslations(Session session, String lang) {
		return new SystemController(session).getMissedTranslations(lang);
	}
}
