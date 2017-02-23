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
@SequenceGenerator(name="seq", initialValue=1, allocationSize=100)
@Table(name="AccessInRoomFW")
public class AccessInRoomFW implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="seq")
	private long id;
	private AccessInRoom accessinroom;
	public AccessInRoom getAccessinroom() {
		return accessinroom;
	}

	public void setAccessinroom(AccessInRoom accessinroom) {
		this.accessinroom = accessinroom;
	}

	@Convert(converter=BooleanToStringConverter.class)
	private Boolean direct;

	@Convert(converter=BooleanToStringConverter.class)
	private Boolean login;

	@Convert(converter=BooleanToStringConverter.class)
	private Boolean portal;
	
	@Convert(converter=BooleanToStringConverter.class)
	private Boolean printing;

	@Convert(converter=BooleanToStringConverter.class)
	private Boolean proxy;

        @Override
        public boolean equals(Object obj) {
                if (obj instanceof AccessInRoomFW && obj !=null) {
                        return getId() == ((AccessInRoomFW)obj).getId();
                }
                return super.equals(obj);
        }

	public AccessInRoomFW() {
		this.direct   = false;
		this.login    = true;
		this.portal   = true;
		this.printing = true;
		this.proxy    = true;
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
}
