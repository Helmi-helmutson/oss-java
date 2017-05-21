package de.openschoolserver.dao;

import java.io.Serializable;

import javax.persistence.*;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;



/**
 * The persistent class for the Categories database table.
 * 
 */
@Entity
@Table(name="Categories")
@NamedQueries({
	@NamedQuery(name="Category.findAll", query="SELECT c FROM Category c"),
	@NamedQuery(name="Category.getByName",  query="SELECT c FROM Category c where c.name = :name"),
	@NamedQuery(name="Category.getByDescription",  query="SELECT c FROM Category c where c.description = :description")
})

public class Category implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name="CATEGORIES_ID_GENERATOR" )
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="CATEGORIES_ID_GENERATOR")
	private long id;

	private String description;

	private String name;

	//bi-directional many-to-many association to Device
        @ManyToMany
        @JoinTable(        
        	name="DeviceInCategories",
			joinColumns={ @JoinColumn(name="category_id") },
			inverseJoinColumns={ @JoinColumn(name="device_id") }
        )
	@JsonIgnore
	private List<Device> devices;

	//bi-directional many-to-many association to Group
        @ManyToMany
        @JoinTable(
            name="GroupInCategories", 
			joinColumns={ @JoinColumn(name="category_id") },
			inverseJoinColumns={ @JoinColumn(name="group_id") }
        )
        @JsonIgnore
	private List<Group> groups;

    //bi-directional many-to-many association to Group
        @ManyToMany
        @JoinTable(
            name="HWConfInCategories", 
			joinColumns={ @JoinColumn(name="category_id") },
			inverseJoinColumns={ @JoinColumn(name="hwconf_id") }
        )
        @JsonIgnore
	private List<HWConf> hwconfs;
        
	//bi-directional many-to-many association to Room
        @ManyToMany
        @JoinTable(
            name="RoomInCategories", 
			joinColumns={ @JoinColumn(name="category_id") },
			inverseJoinColumns={ @JoinColumn(name="room_id") }
        )
        @JsonIgnore
	private List<Room> rooms;

	//bi-directional many-to-many association to Software
        @ManyToMany
        @JoinTable(
            name="SoftwareInCategories", 
			joinColumns={ @JoinColumn(name="category_id") },
			inverseJoinColumns={ @JoinColumn(name="software_id") }
        )
        @JsonIgnore
	private List<Software> softwares;
        
    //bi-directional many-to-many association to Software
    @ManyToMany
    @JoinTable(
        name="SoftwareRemovedFromCategories", 
	    joinColumns={ @JoinColumn(name="category_id") },
		inverseJoinColumns={ @JoinColumn(name="software_id") }
    )
    @JsonIgnore
	private List<Software> removedSoftwares;

	//bi-directional many-to-many association to User
        @ManyToMany
        @JoinTable(
            name="UserInCategories", 
			joinColumns={ @JoinColumn(name="category_id") },
			inverseJoinColumns={ @JoinColumn(name="user_id") }
        )
        @JsonIgnore
	private List<User> users;

    @Override
    public boolean equals(Object obj) {
          if (obj instanceof Category && obj !=null) {
                  return getId() == ((Category)obj).getId();
          }
          return super.equals(obj);
    }
        
	public Category() {
	}

	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Device> getDevices() {
		return this.devices;
	}

	public void setDevices(List<Device> devices) {
		this.devices = devices;
	}

	public List<Group> getGroups() {
		return this.groups;
	}

	public void setGroups(List<Group> groups) {
		this.groups = groups;
	}

	public List<HWConf> getHWConfs() {
		return this.hwconfs;
	}

	public void setHWConfs(List<HWConf> hwconfs) {
		this.hwconfs = hwconfs;
	}

	public List<Room> getRooms() {
		return this.rooms;
	}

	public void setRooms(List<Room> rooms) {
		this.rooms = rooms;
	}

	public List<Software> getSoftwares() {
		return this.softwares;
	}

	public void setSoftwares(List<Software> softwares) {
		this.softwares = softwares;
	}

	public List<Software> getRemovedSoftwares() {
		return this.removedSoftwares;
	}

	public void setRemovedSoftwares(List<Software> softwares) {
		this.removedSoftwares = softwares;
	}
	public List<User> getUsers() {
		return this.users;
	}

	public void setUsers(List<User> users) {
		this.users = users;
	}

}
