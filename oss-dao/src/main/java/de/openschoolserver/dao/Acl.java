/* (c) 2017 Péter Varkoly <peter@varkoly.de> - all rights reserved */
package de.openschoolserver.dao;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the Acls database table.
 * 
 */
@Entity
@Table(name="Acls")
@NamedQuery(name="Acl.findAll", query="SELECT a FROM Acl a")
public class Acl implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private int id;

	private String acl;

	@Column(name="object_id")
	private int objectId;

	@Column(name="target_id")
	private int targetId;

	private String targetType;

	public Acl() {
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getAcl() {
		return this.acl;
	}

	public void setAcl(String acl) {
		this.acl = acl;
	}

	public int getObjectId() {
		return this.objectId;
	}

	public void setObjectId(int objectId) {
		this.objectId = objectId;
	}

	public int getTargetId() {
		return this.targetId;
	}

	public void setTargetId(int targetId) {
		this.targetId = targetId;
	}

	public String getTargetType() {
		return this.targetType;
	}

	public void setTargetType(String targetType) {
		this.targetType = targetType;
	}

}