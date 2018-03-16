package de.openschoolserver.dao;

public class ProxyRule {

	
	private String   name;
	private boolean  enabled;
	
	public ProxyRule() {
	}
	
	public ProxyRule(String name, boolean enabled) {
		this.name    = name;
		this.enabled = enabled;
	}
	

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	

}
