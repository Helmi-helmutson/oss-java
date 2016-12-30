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
@NamedQuery(name="AccessInRoom.findAll", query="SELECT a FROM AccessInRoom a")
public class AccessInRoom implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private int id;

	private Object all;

	private Object defaultAccess;

	private Object logon;

	private Object mail;

	private Time moment;

	private Object proxy;

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

	public Object getAll() {
		return this.all;
	}

	public void setAll(Object all) {
		this.all = all;
	}

	public Object getDefaultAccess() {
		return this.defaultAccess;
	}

	public void setDefaultAccess(Object defaultAccess) {
		this.defaultAccess = defaultAccess;
	}

	public Object getLogon() {
		return this.logon;
	}

	public void setLogon(Object logon) {
		this.logon = logon;
	}

	public Object getMail() {
		return this.mail;
	}

	public void setMail(Object mail) {
		this.mail = mail;
	}

	public Time getMoment() {
		return this.moment;
	}

	public void setMoment(Time moment) {
		this.moment = moment;
	}

	public Object getProxy() {
		return this.proxy;
	}

	public void setProxy(Object proxy) {
		this.proxy = proxy;
	}

	public Room getRoom() {
		return this.room;
	}

	public void setRoom(Room room) {
		this.room = room;
	}

}