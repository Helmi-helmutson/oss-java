package de.openschoolserver.api.resourceimpl;

import java.util.List;

import java.util.Map;

import de.openschoolserver.api.resources.SystemResource;
import de.openschoolserver.dao.Response;
import de.openschoolserver.dao.Session;
import de.openschoolserver.dao.controler.SystemControler;

public class SystemResourceImpl implements SystemResource {

	@Override
	public List<Map<String, String>> getStatus(Session session) {
		SystemControler systemControler = new SystemControler(session);
		return systemControler.getStatus();
	}

	@Override
	public List<String> getEnumerates(Session session, String type) {
		SystemControler systemControler = new SystemControler(session);
		return systemControler.getEnumerates(type);
	}

	@Override
	public Response addEnumerate(Session session, String type, String value) {
		SystemControler systemControler = new SystemControler(session);
		return systemControler.addEnumerate(type, value);
	}

	@Override
	public Response removeEnumerate(Session session, String type, String value) {
		SystemControler systemControler = new SystemControler(session);
		return systemControler.removeEnumerate(type, value);
	}

	@Override
	public List<Map<String, String>> getConfig(Session session) {
		SystemControler systemControler = new SystemControler(session);
		return systemControler.getConfig();
	}

	@Override
	public String getConfig(Session session, String key) {
		SystemControler systemControler = new SystemControler(session);
		return systemControler.getConfigValue(key);
	}

	@Override
	public Response setConfig(Session session, String key, String value) {
		SystemControler systemControler = new SystemControler(session);
		if( systemControler.setConfigValue(key, value) )
			return new Response(session,"OK","Global configuration value was set succesfully."); 
		else
			return new Response(session,"ERROR","Global configuration value could not be set.");
	}

	@Override
	public Map<String, String> getFirewallIncomingRules(Session session) {
		SystemControler systemControler = new SystemControler(session);
		return systemControler.getFirewallIncomingRules();
	}

	@Override
	public Response setFirewallIncomingRules(Session session, Map<String, String> incommingRules) {
		SystemControler systemControler = new SystemControler(session);
		return systemControler.setFirewallIncomingRules(incommingRules);
	}

	@Override
	public List<Map<String, String>> getFirewallOutgoingRules(Session session) {
		SystemControler systemControler = new SystemControler(session);
		return systemControler.getFirewallOutgoingRules();
	}

	@Override
	public Response setFirewallOutgoingRules(Session session, List<Map<String, String>> outgoingRules) {
		SystemControler systemControler = new SystemControler(session);
		return systemControler.setFirewallOutgoingRules(outgoingRules);
	}

	@Override
	public List<Map<String, String>> getFirewallRemoteAccessRules(Session session) {
		SystemControler systemControler = new SystemControler(session);
		return systemControler.getFirewallRemoteAccessRules();
	}

	@Override
	public Response setFirewallRemoteAccessRules(Session session, List<Map<String, String>> remoteAccessRules) {
		SystemControler systemControler = new SystemControler(session);
		return systemControler.setFirewallRemoteAccessRules(remoteAccessRules);
		}
}
