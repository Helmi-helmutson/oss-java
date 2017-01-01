/* (c) 2017 Péter Varkoly <peter@varkoly.de> - all rights reserved */
package de.openschoolserver.dao;

import java.io.Serializable;

import javax.persistence.*;

import java.util.HashMap;


/**
 * The persistent class for the AccessInRoom database table.
 * 
 */
@Entity
@NamedQueries( {
	@NamedQuery(name="AccessInRoom.findAll",            query="SELECT a FROM AccessInRoom a"),
	@NamedQuery(name="AccessInRoom.findByRoom",         query="SELECT a FROM AccessInRoom WHERE room = :room"),
	@NamedQuery(name="AccessInRoom.findActualAccesses", query="SELECT a FROM AccessInRoomPIT WHERE pointInTime = :time")
})
public class AccessInRoom implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private long id;

	private String accessType;

	//bi-directional many-to-one association to Room
	@ManyToOne
	private Room room;

	@OneToOne(mappedBy="accessinroom")
	private AccessInRoomFW fwAccess;

	@OneToOne(mappedBy="accessinroom")
	private AccessInRoomACT actAccess;

	@OneToOne(mappedBy="accessinroom")
	private AccessInRoomPIT accessPIT;

	@Transient
	privat HashMap<String, Object> access = new HashMap<String, Object>();

	public AccessInRoom() {
		this.accessType = "DEFAULT";
                this.defaultAccess = null;
                this.fwAccess      = null;
                this.actAccess     = null;
                this.accessPIT     = null;
	}

	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Room getRoom() {
		return this.room;
	}

	public void setRoom(Room room) {
		this.room = room;
	}

	public String getAccessType() {
		return this.accessType;
	}

	public void setAccessType(String accesstype) {
		this.accessType = accesstype;
	}

	public HashMap<String, Object> getAccess() {
		return this.access;
	}

	public void setAccess(HashMap<String, Object> access) {
		this.access = access;
	}

        public AccessInRoomFW getAccessInRoomFW() {
                return this.fwAccess;
        }

        public void setRoom(AccessInRoomFW fwAccess) {
                this.fwAccess = fwAccess;
        }

        public AccessInRoomACT getAccessInRoomACT() {
                return this.actAccess;
        }

        public void setRoom(AccessInRoomACT actAccess) {
                this.actAccess = actAccess;
        }

        public AccessInRoomPIT getAccessInRoomPIT() {
                return this.accessPIT;
        }

        public void setRoom(AccessInRoomPIT accessPIT) {
                this.accessPIT = accessPIT;
        }

}
