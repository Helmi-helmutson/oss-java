/* (c) 2017 Péter Varkoly <peter@varkoly.de> - all rights reserved */
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
	private Long id;

	/*
	 * The amount of the devices the license can be used for.
	 */
	private int count;

	/*
	 * The type of the license. This can be F for licenses saved in files or C for Licenses passed by command name.
	 */
	private Character licenseType;

	/*
	 * By C licenses this is the value of the license.
	 * By F licenses this is the name of the file in which the license was saved.
	 */
	private String value;
	
	//bi-directional many-to-many association to Device
	@ManyToMany( cascade ={CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH} )
	@JoinTable(        
		name="LicenseToDevice",
	    	joinColumns={ @JoinColumn(name="license_id") },
	    	inverseJoinColumns={ @JoinColumn(name="device_id") }
	)
	@JsonIgnore
	private List<Device> devices;

	//bi-directional many-to-one association to Software
	@ManyToOne
	@JsonIgnore
	private Software software;
	
	//bi-directional many-to-one association to User
	@ManyToOne
	@JsonIgnore
	private User creator;

	@Override
	public boolean equals(Object obj) {
	      if (obj instanceof SoftwareLicense && obj !=null) {
	                  return getId() == ((SoftwareLicense)obj).getId();
	      }
	      return super.equals(obj);
	}

	public SoftwareLicense() {
	}

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public int getCount() {
		return this.count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public Character getLicenseType() {
		return this.licenseType;
	}

	public void setLicenseType(Character licenseType) {
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

	public User getCreator() {
		return creator;
	}

	public void setCreator(User creator) {
		this.creator = creator;
	}

	public void setDevices(List<Device> devices) {
		this.devices = devices;
	}
}
