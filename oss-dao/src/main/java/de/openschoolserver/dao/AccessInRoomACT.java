/* (c) 2017 Péter Varkoly <peter@varkoly.de> - all rights reserved */
package de.openschoolserver.dao;

import java.io.Serializable;

import javax.persistence.*;

/**
 * The persistent class for the AccessInRoom database table.
 * 
 */
@Entity
@SequenceGenerator(name="seq", initialValue=1, allocationSize=100)
@Table(name="AccessInRoomACT")
public class AccessInRoomACT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="seq")
	private long id;

	private String action;
	
	private AccessInRoom accessinroom;


	public AccessInRoom getAccessinroom() {
		return accessinroom;
	}

	public void setAccessinroom(AccessInRoom accessinroom) {
		this.accessinroom = accessinroom;
	}
        @Override
        public boolean equals(Object obj) {
                if (obj instanceof AccessInRoomACT && obj !=null) {
                        return getId() == ((AccessInRoomACT)obj).getId();
                }
                return super.equals(obj);
        }

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
