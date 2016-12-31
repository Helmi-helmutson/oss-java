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
	private long id;

	private String acl;

	@Column(name="object_id")
	private long objectId;

	@Column(name="target_id")
	private long targetId;

	private String targetType;

	public Acl() {
	}

	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getAcl() {
		return this.acl;
	}

	public void setAcl(String acl) {
		this.acl = acl;
	}

	public long getObjectId() {
		return this.objectId;
	}

	public void setObjectId(long objectId) {
		this.objectId = objectId;
	}

	public long getTargetId() {
		return this.targetId;
	}

	public void setTargetId(long targetId) {
		this.targetId = targetId;
	}

	public String getTargetType() {
		return this.targetType;
	}

	public void setTargetType(String targetType) {
		this.targetType = targetType;
	}

}
