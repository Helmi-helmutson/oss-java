/* (c) 2017 Péter Varkoly <peter@varkoly.de> - all rights reserved */
/* (c) 2016 EXTIS GmbH - all rights reserved */
package de.openschoolserver.dao;

import java.security.Principal;
import java.util.List;

import javax.persistence.*;
import javax.security.auth.Subject;


public class Session implements Principal {

	@Transient
	private String password = "dummy";
	
	@Transient
	private String schoolId = "dummy";
	
	@OneToOne
	private Device device;
		
	@OneToOne
	private User user;
	
	@OneToOne
	private Room room;
	
	private String IP;
	
	@Override
	public String getName() {	
		return "dummy";
	}
	
	public String getSchoolId() {
		return this.schoolId;
	}

	public void setSchoolId(String schoolId) {
		this.schoolId = schoolId;
	}

	public String getIP() {
		return this.IP;
	}

	public void setIP(String IP) {
		this.IP = IP;
	}

	public Room getRoom() {
		return this.room;
	}

	public void setRoom(Room room) {
		this.room = room;
	}
	
	public User getUser() {
		return this.user;
	}

	public void setUser(User user) {
		this.user = user;
	}
	
	public Device getDevice() {
		return this.device;
	}

	public void setDevice(Device device) {
		this.device = device;
	}

//TODO implement
}
