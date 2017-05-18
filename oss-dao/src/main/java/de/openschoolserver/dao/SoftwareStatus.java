package de.openschoolserver.dao;

import java.io.Serializable;
import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;


/**
 * The persistent class for the SoftwareStatus database table.
 * 
 */
@Entity
@NamedQueries({
	@NamedQuery(name="SoftwareStatus.findAll", query="SELECT s FROM SoftwareStatus s"),
	@NamedQuery(name="SoftwareStatus.findByStatus", query="SELECT s FROM SoftwareStatus s WHERE s.status = :STATUS")
})
public class SoftwareStatus implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private SoftwareStatusPK id;

	private String status;

	//bi-directional many-to-one association to SoftwareVersion
	@ManyToOne
	@JoinColumn(name="version_id")
    @JsonIgnore
	private SoftwareVersion softwareVersion;

	@Column(name = "version_id", insertable = false, updatable = false)
    private Long versionId;

	//bi-directional many-to-one association to device
	@ManyToOne
	@JoinColumn(name="device_id")
    @JsonIgnore
	private Device device;

	@Column(name = "device_id", insertable = false, updatable = false)
    private Long deviceId;
	    
	public SoftwareStatus() {
	}

	public SoftwareStatusPK getId() {
		return this.id;
	}

	public void setId(SoftwareStatusPK id) {
		this.id = id;
	}

	public String getStatus() {
		return this.status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public SoftwareVersion getSoftwareVersion() {
		return this.softwareVersion;
	}

	public void setSoftwareVersion(SoftwareVersion softwareVersion) {
		this.softwareVersion = softwareVersion;
	}

	public Device getDevice() {
		return this.device;
	}

	public void setDevice(Device device) {
		this.device = device;
	}

}