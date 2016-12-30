/* (c) 2017 Péter Varkoly <peter@varkoly.de> - all rights reserved */
/* (c) 2016 EXTIS GmbH - all rights reserved */
package de.openschoolserver.dao;

import java.security.Principal;
import javax.security.auth.Subject;


public class Session implements Principal {
	private String schoolId = "dummy";
	private String userName = "dummy";
	private String role     = "dummy";
	private String password = "dummy";
	private String deviceIP = "dummy";
	
	public String getUserName() {
		return userName;
	}
	
	public void setUserName(String username) {
	     this.userName = username;
	}
	
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
	
	@Override
	public boolean implies(Subject subject) {
		return true;
	}
//TODO implement
}
