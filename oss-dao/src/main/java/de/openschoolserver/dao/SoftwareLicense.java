package de.openschoolserver.dao;

import java.io.Serializable;
import java.util.List;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;


/**
 * The persistent class for the SoftwareLicenses database table.
 * 
 */
@Entity
@Table(name="SoftwareLicenses")
@NamedQuery(name="SoftwareLicense.findAll", query="SELECT s FROM SoftwareLicense s")
public class SoftwareLicense implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name="SOFTWARELICENSES_ID_GENERATOR" )
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="SOFTWARELICENSES_ID_GENERATOR")
	private long id;

	private int count;

	private String licenseType;

	private String value;
	
	//bi-directional many-to-many association to Device
    @ManyToMany
    @JoinTable(        
    	name="LicenseToDevice",
		joinColumns={ @JoinColumn(name="license_id") },
		inverseJoinColumns={ @JoinColumn(name="device_id") }
    )
    @JsonIgnore
    private List<Device> devices;

	//bi-directional many-to-one association to Software
	@ManyToOne
	private Software software;

	@Override
    public boolean equals(Object obj) {
	      if (obj instanceof SoftwareLicense && obj !=null) {
	                  return getId() == ((SoftwareLicense)obj).getId();
	      }
	      return super.equals(obj);
	}

	public SoftwareLicense() {
	}

	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public int getCount() {
		return this.count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public String getLicenseType() {
		return this.licenseType;
	}

	public void setLicenseType(String licenseType) {
		this.licenseType = licenseType;
	}

	public String getValue() {
		return this.value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Software getSoftware() {
		return this.software;
	}

	public void setSoftware(Software software) {
		this.software = software;
	}
	
	public List<Device> getDevices() {
		return this.devices;
	}

	public boolean addDevice(Device device) {
		if( this.devices.size()+1 <= this.count) {
			this.devices.add(device);
			return true;
		} else {
			return false;
		}
	}
	
	public void removeDevice(Device device) {
		this.devices.remove(device);
	}
}
