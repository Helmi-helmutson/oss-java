package de.openschoolserver.dao;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the SoftwareStatus database table.
 * 
 */
@Embeddable
public class SoftwareStatusPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="version_id", insertable=false, updatable=false)
	private String versionId;

	@Column(name="device_id", insertable=false, updatable=false)
	private String deviceId;

	public SoftwareStatusPK() {
	}
	public String getVersionId() {
		return this.versionId;
	}
	public void setVersionId(String versionId) {
		this.versionId = versionId;
	}
	public String getDeviceId() {
		return this.deviceId;
	}
	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof SoftwareStatusPK)) {
			return false;
		}
		SoftwareStatusPK castOther = (SoftwareStatusPK)other;
		return 
			this.versionId.equals(castOther.versionId)
			&& this.deviceId.equals(castOther.deviceId);
	}

	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.versionId.hashCode();
		hash = hash * prime + this.deviceId.hashCode();
		
		return hash;
	}
}