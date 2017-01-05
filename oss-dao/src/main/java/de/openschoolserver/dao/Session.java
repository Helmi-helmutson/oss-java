/* (c) 2017 Péter Varkoly <peter@varkoly.de> - all rights reserved */
/* (c) 2016 EXTIS GmbH - all rights reserved */
package de.openschoolserver.dao;

import java.security.Principal;
import java.util.List;

import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.security.auth.Subject;


public class Session implements Principal {
	
	private String password = "dummy";
	
	@OneToOne
	private Device device;
		
	@OneToOne
	private User user;
	
	@OneToOne
	private Room room;
	
	@Override
	public String getName() {	
		return "dummy";
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

	@Override
	public boolean implies(Subject subject) {
		return true;
	}
//TODO implement
}
