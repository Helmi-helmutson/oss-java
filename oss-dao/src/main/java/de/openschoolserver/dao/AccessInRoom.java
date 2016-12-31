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
	private long id;

	private Boolean direct;

	private Boolean defaultAccess;

	private Boolean login;

	private Boolean portal;
	
	private Boolean printing;

	private String pointInTime;

	private Boolean proxy;

	private Boolean mo;
	private Boolean tu;
	private Boolean we;
	private Boolean th;
	private Boolean fr;
	private Boolean sa;
	private Boolean su;
	private Boolean holiday;

	//bi-directional many-to-one association to Room
	@ManyToOne
	private Room room;

	public AccessInRoom() {
		this.direct   = false;
		this.login    = true;
		this.portal   = true;
		this.printing = true;
		this.proxy    = true;
		this.mo       = true;
		this.tu       = true;
		this.we       = true;
		this.th       = true;
		this.fr       = true;
		this.sa       = false;
		this.su       = false;
		this.holiday  = false;
	}

	public long getId() {
		return this.id;
	}

	public void setId(long id) {
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
	
	public Boolean getMo() {
		return this.mo;
	}

	public void setMo(Boolean mo) {
		this.mo = mo;
	}
	
	public Boolean getTu() {
		return this.tu;
	}

	public void setTu(Boolean tu) {
		this.tu = tu;
	}
	
	public Boolean getWe() {
		return this.we;
	}

	public void setWe(Boolean we) {
		this.we = we;
	}
	
	public Boolean getTh() {
		return this.th;
	}

	public void setTh(Boolean th) {
		this.th = th;
	}

	public Boolean getFr() {
		return this.fr;
	}

	public void setFr(Boolean fr) {
		this.fr = fr;
	}
	
	public Boolean getSa() {
		return this.sa;
	}

	public void setSa(Boolean sa) {
		this.sa = sa;
	}

	public Boolean getSu() {
		return this.su;
	}

	public void setSu(Boolean su) {
		this.su = su;
	}
	
	public Boolean getHoliday() {
		return this.holiday;
	}

	public void setHoliday(Boolean holiday) {
		this.holiday = holiday;
	}

	public Room getRoom() {
		return this.room;
	}

	public void setRoom(Room room) {
		this.room = room;
	}

}
