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
@NamedQuery(name="Category.findAll", query="SELECT c FROM Category c")
public class Category implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name="CATEGORIES_ID_GENERATOR" )
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="CATEGORIES_ID_GENERATOR")
	private long id;

	private String desciption;

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

	public String getDesciption() {
		return this.desciption;
	}

	public void setDesciption(String desciption) {
		this.desciption = desciption;
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

	public List<User> getUsers() {
		return this.users;
	}

	public void setUsers(List<User> users) {
		this.users = users;
	}

}
