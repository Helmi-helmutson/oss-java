package de.openschoolserver.dao;

import java.io.Serializable;
import javax.persistence.*;
import java.util.List;


/**
 * The persistent class for the SoftwareVersion database table.
 * 
 */
@Entity
@NamedQuery(name="SoftwareVersion.findAll", query="SELECT s FROM SoftwareVersion s")
public class SoftwareVersion implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name="SOFTWAREVERSION_ID_GENERATOR" )
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="SOFTWAREVERSION_ID_GENERATOR")
	private long id;

	private String version;

	//bi-directional many-to-one association to SoftwareStatus
	@OneToMany(mappedBy="softwareVersion")
	private List<SoftwareStatus> softwareStatuses;

	//bi-directional many-to-one association to Software
	@ManyToOne
	private Software software;

	public SoftwareVersion() {
	}

	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getVersion() {
		return this.version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public List<SoftwareStatus> getSoftwareStatuses() {
		return this.softwareStatuses;
	}

	public void setSoftwareStatuses(List<SoftwareStatus> softwareStatuses) {
		this.softwareStatuses = softwareStatuses;
	}

	public SoftwareStatus addSoftwareStatus(SoftwareStatus softwareStatus) {
		getSoftwareStatuses().add(softwareStatus);
		softwareStatus.setSoftwareVersion(this);

		return softwareStatus;
	}

	public SoftwareStatus removeSoftwareStatus(SoftwareStatus softwareStatus) {
		getSoftwareStatuses().remove(softwareStatus);
		softwareStatus.setSoftwareVersion(null);

		return softwareStatus;
	}

	public Software getSoftware() {
		return this.software;
	}

	public void setSoftware(Software software) {
		this.software = software;
	}

}