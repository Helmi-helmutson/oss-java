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

	private Boolean login;

	private Boolean portal;
	
	private Boolean printing;

	private Time pointInTime;

	private Boolean proxy;

	//bi-directional many-to-one association to Room
	@ManyToOne
	private Room room;

	public AccessInRoom() {
		this.direct = false;
		this.login  = true;
		this.portal = true;
		this.printing = true;
		this.proxy  = true;
		
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

	public Boolean getLogin() {
		return this.login;
	}

	public void setLogin(Boolean login) {
		this.login = login;
	}

	public Boolean getPortal() {
		return this.portal;
	}

	public void setPortal(Boolean portal) {
		this.portal = portal;
	}

	public Time getPointInTime() {
		return this.pointInTime;
	}

	public void setPointInTime(Time pointInTime) {
		this.pointInTime = pointInTime;
	}

	public Boolean getPrinting() {
		return this.printing;
	}

	public void setPrinting(Boolean printing) {
		this.printing = printing;
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