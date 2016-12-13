/* (c) 2016 EXTIS GmbH - all rights reserved */
package de.openschoolserver.dao;

import java.security.Principal;

public class Session implements Principal {
	private String schoolId = "dummy";

	public String getSchoolId() {
		return schoolId;
	}

	public void setSchoolId(String schoolId) {
		this.schoolId = schoolId;
	}

	@Override
	public String getName() {
		
		return "dummy";
	}
//TODO implement
}
