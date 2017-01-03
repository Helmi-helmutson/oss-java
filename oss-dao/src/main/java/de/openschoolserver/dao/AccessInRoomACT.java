/* (c) 2017 Péter Varkoly <peter@varkoly.de> - all rights reserved */
package de.openschoolserver.dao;

import java.io.Serializable;

import javax.persistence.*;

/**
 * The persistent class for the AccessInRoom database table.
 * 
 */
@Entity
public class AccessInRoomACT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private long id;

	private String action;

	public AccessInRoomACT() {
		this.action  = "";
	}

	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getAction() {
		return this.action;
	}

	public void setAction(String action) {
		this.action = action;
	}
}
