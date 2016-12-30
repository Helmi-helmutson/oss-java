/* (c) 2017 Péter Varkoly <peter@varkoly.de> - all rights reserved */
package de.openschoolserver.dao;

import java.io.Serializable;

import javax.persistence.*;
import java.sql.Time;


/**
 * The persistent class for the AccessInRoom database table.
 * 
 */
@Entity
@NamedQueries( {
	@NamedQuery(name="AccessInRoom.findAll", query="SELECT a FROM AccessInRoom a"),
	@NamedQuery(name="AccessInRoom.findByRoom", query="SELECT a FROM AccessInRoom WHERE room = :room ORDER by pointOfTime"),
	@NamedQuery(name="AccessInRoom.findActualAccesses", query="SELECT a FROM AccessInRoom WHERE pointInTime = :time")
})
public class AccessInRoom implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private int id;

	private Boolean direct;

	private Boolean defaultAccess;

	private Boolean logon;

	private Boolean mail;

	private Time pointInTime;

	private Boolean proxy;

	//bi-directional many-to-one association to Room
	@ManyToOne
	private Room room;

	public AccessInRoom() {
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Boolean getDirect() {
		return this.direct;
	}

	public void setDirect(Boolean direct) {
		this.direct = direct;
	}

	public Boolean getDefaultAccess() {
		return this.defaultAccess;
	}

	public void setDefaultAccess(Boolean defaultAccess) {
		this.defaultAccess = defaultAccess;
	}

	public Boolean getLogon() {
		return this.logon;
	}

	public void setLogon(Boolean logon) {
		this.logon = logon;
	}

	public Boolean getMail() {
		return this.mail;
	}

	public void setMail(Boolean mail) {
		this.mail = mail;
	}

	public Time getPointInTime() {
		return this.pointInTime;
	}

	public void setPointInTime(Time pointInTime) {
		this.pointInTime = pointInTime;
	}

	public Boolean getProxy() {
		return this.proxy;
	}

	public void setProxy(Boolean proxy) {
		this.proxy = proxy;
	}

	public Room getRoom() {
		return this.room;
	}

	public void setRoom(Room room) {
		this.room = room;
	}

}