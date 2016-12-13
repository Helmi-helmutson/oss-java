package de.openschoolserver.api.auth;

import java.security.Principal;

public class Session implements Principal {

	@Override
	public String getName() {
		
		return "dummy";
	}
//TODO implement
}
