/* (c) 2017 Péter Varkoly <peter@varkoly.de> - all rights reserved */
package de.openschoolserver.dao;

import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

/**
 * The persistent class for the Software database table.
 * 
 */
@Entity
@Table(name = "Softwares")
@NamedQueries({
	@NamedQuery(name="Software.findAll",   query="SELECT s FROM Software s"),
	@NamedQuery(name="Software.findAllId", query="SELECT s.id FROM Software s"),
	@NamedQuery(name="Software.getByName", query="SELECT s FROM Software s WHERE s.name = :name")
})
public class Software implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@SequenceGenerator(name="SOFTWARE_ID_GENERATOR" )
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="SOFTWARE_ID_GENERATOR")
	private Long id;

	@Size(max=128, message="Description must not be longer then 128 characters.")
	private String description;

	@Convert(converter=BooleanToStringConverter.class)
	private Boolean manually;

	@Size(max=128, message="Name must not be longer then 128 characters.")
	private String name;

	private Integer weight;
	
	//bi-directional many-to-one association to SoftwareLicens
	@OneToMany(mappedBy="software", cascade=CascadeType.REMOVE)
	@JsonIgnore
	private List<SoftwareLicense> softwareLicenses;

	//bi-directional many-to-one association to SoftwareVersion
	@OneToMany(mappedBy="software", cascade=CascadeType.REMOVE)
	private List<SoftwareVersion> softwareVersions;
	
	//bi-directional many-to-many association to Category
	@ManyToMany(mappedBy="softwares", cascade ={CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
	@JsonIgnore
	private List<Category> categories;

	//bi-directional many-to-many association to Category
	@ManyToMany(mappedBy="removedSoftwares", cascade ={CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
	@JsonIgnore
	private List<Category> removedFromCategories;
	
	    //bi-directional many-to-one association to User
	@ManyToOne
	@JsonIgnore
	private User creator;

	public Software() {
		this.manually = false;
		this.weight   = 50;
	}

	@Override
	public String toString() {
		try {
			return new ObjectMapper().writeValueAsString(this);
		} catch (Exception e) {
			return "{ \"ERROR\" : \"CAN NOT MAP THE OBJECT\" }";
		}
	}
	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Software other = (Software) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getWeight() {
		return this.weight;
	}

	public void setWeight(Integer weight) {
		this.weight = weight;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Boolean getManually() {
		return this.manually;
	}

	public void setManually(Boolean manually) {
		this.manually = manually;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<SoftwareLicense> getSoftwareLicenses() {
		return this.softwareLicenses;
	}

	public void setSoftwareLicenses(List<SoftwareLicense> softwareLicenses) {
		this.softwareLicenses = softwareLicenses;
	}

	public SoftwareLicense addSoftwareLicens(SoftwareLicense softwareLicens) {
		getSoftwareLicenses().add(softwareLicens);
		softwareLicens.setSoftware(this);

		return softwareLicens;
	}

	public SoftwareLicense removeSoftwareLicens(SoftwareLicense softwareLicens) {
		getSoftwareLicenses().remove(softwareLicens);
		softwareLicens.setSoftware(null);

		return softwareLicens;
	}

	public List<SoftwareVersion> getSoftwareVersions() {
		return this.softwareVersions;
	}

	public void setSoftwareVersions(List<SoftwareVersion> softwareVersions) {
		this.softwareVersions = softwareVersions;
	}

	public SoftwareVersion addSoftwareVersion(SoftwareVersion softwareVersion) {
		getSoftwareVersions().add(softwareVersion);
		softwareVersion.setSoftware(this);

		return softwareVersion;
	}

	public SoftwareVersion removeSoftwareVersion(SoftwareVersion softwareVersion) {
		getSoftwareVersions().remove(softwareVersion);
		softwareVersion.setSoftware(null);

		return softwareVersion;
	}

    public List<Category> getCategories() {
        return this.categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }
    
    public List<Category> getRemovedFromCategories() {
        return this.removedFromCategories;
    }

    public void setRemovedFromCategories(List<Category> categories) {
        this.removedFromCategories = categories;
    }

	public User getCreator() {
		return creator;
	}

	public void setCreator(User creator) {
		this.creator = creator;
	}

}
