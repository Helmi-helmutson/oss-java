/* (c) 2017 Péter Varkoly <peter@varkoly.de> - all rights reserved */
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
	@NamedQuery(name="Category.findAll",          query="SELECT c FROM Category c"),
	@NamedQuery(name="Category.getByName",        query="SELECT c FROM Category c where c.name = :name"),
	@NamedQuery(name="Category.getByDescription", query="SELECT c FROM Category c where c.description = :description"),
	@NamedQuery(name="Category.getByType",        query="SELECT c FROM Category c where c.categoryType = :type"),
	@NamedQuery(name="Category.search",           query="SELECT c FROM Category c WHERE c.name LIKE :search OR c.description = :search")
})

public class Category implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name="CATEGORIES_ID_GENERATOR" )
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="CATEGORIES_ID_GENERATOR")
	private long id;

	private String description;

	private String name;

	private String categoryType;

	//bi-directional many-to-one association to User
	@ManyToOne
	@JsonIgnore
	private User owner;

	@Column(name="owner_id", insertable=false, updatable=false)
	private Long ownerId;

	//bi-directional many-to-many association to Device
	@ManyToMany(cascade ={CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
	@JoinTable(        
			name="DeviceInCategories",
			joinColumns={ @JoinColumn(name="category_id") },
			inverseJoinColumns={ @JoinColumn(name="device_id") }
			)
	@JsonIgnore
	private List<Device> devices;

	//bi-directional many-to-many association to Group
	@ManyToMany(cascade ={CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
	@JoinTable(
			name="GroupInCategories", 
			joinColumns={ @JoinColumn(name="category_id") },
			inverseJoinColumns={ @JoinColumn(name="group_id") }
			)
	@JsonIgnore
	private List<Group> groups;

	//bi-directional many-to-many association to Group
	@ManyToMany(cascade ={CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
	@JoinTable(
			name="HWConfInCategories", 
			joinColumns={ @JoinColumn(name="category_id") },
			inverseJoinColumns={ @JoinColumn(name="hwconf_id") }
			)
	@JsonIgnore
	private List<HWConf> hwconfs;

	//bi-directional many-to-many association to Room
	@ManyToMany(cascade ={CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
	@JoinTable(
			name="RoomInCategories", 
			joinColumns={ @JoinColumn(name="category_id") },
			inverseJoinColumns={ @JoinColumn(name="room_id") }
			)
	@JsonIgnore
	private List<Room> rooms;

	//bi-directional many-to-many association to Software
	@ManyToMany(cascade ={CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
	@JoinTable(
			name="SoftwareInCategories", 
			joinColumns={ @JoinColumn(name="category_id") },
			inverseJoinColumns={ @JoinColumn(name="software_id") }
			)
	@JsonIgnore
	private List<Software> softwares;

	//bi-directional many-to-many association to Software
	@ManyToMany(cascade ={CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
	@JoinTable(
			name="SoftwareRemovedFromCategories", 
			joinColumns={ @JoinColumn(name="category_id") },
			inverseJoinColumns={ @JoinColumn(name="software_id") }
			)
	@JsonIgnore
	private List<Software> removedSoftwares;

	//bi-directional many-to-many association to User
	@ManyToMany(cascade ={CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
	@JoinTable(
			name="UserInCategories", 
			joinColumns={ @JoinColumn(name="category_id") },
			inverseJoinColumns={ @JoinColumn(name="user_id") }
			)
	@JsonIgnore
	private List<User> users;

	//bi-directional many-to-many association to Announcement
	@ManyToMany(cascade ={CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
	@JoinTable(
			name="AnnouncementInCategories", 
			joinColumns={ @JoinColumn(name="category_id") },
			inverseJoinColumns={ @JoinColumn(name="announcement_id") }
			)
	@JsonIgnore
	private List<Announcement> announcements;

	//bi-directional many-to-many association to Contact
	@ManyToMany(cascade ={CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
	@JoinTable(
			name="ContactInCategories", 
			joinColumns={ @JoinColumn(name="category_id") },
			inverseJoinColumns={ @JoinColumn(name="contact_id") }
			)
	@JsonIgnore
	private List<Contact> contacts;

	//bi-directional many-to-many association to FAQ
	@ManyToMany(cascade ={CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
	@JoinTable(
			name="FAQInCategories", 
			joinColumns={ @JoinColumn(name="category_id") },
			inverseJoinColumns={ @JoinColumn(name="faq_id") }
			)
	@JsonIgnore
	private List<FAQ> faqs;

	@Transient
	private List<Long> deviceIds;

	@Transient
	private List<Long> hwConfIds;

	@Transient
	private List<Long> roomIds;

	@Transient
	private List<Long> userIds;

	@Transient
	private List<Long> groupIds;

	@Transient
	private List<Long> softwareIds;

	@Transient
	private List<Long> announcementIds;

	@Transient
	private List<Long> contactIds;

	@Transient
	private List<Long> faqIds;

	@Convert(converter=BooleanToStringConverter.class)
	boolean studentsOnly;


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

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getCategoryType() {
		return this.categoryType;
	}

	public void setCategoryType(String categoryType) {
		this.categoryType = categoryType;
	}


	public User getOwner() {
		return this.owner;
	}

	public void setOwner(User owner) {
		this.owner = owner;
	}

	public List<Device> getDevices() {
		return this.devices;
	}

	public void setDevices(List<Device> devices) {
		this.devices = devices;
	}

	public List<Long> getDeviceIds() {
		return this.deviceIds;
	}

	public void setDeviceIds(List<Long> ids) {
		this.deviceIds = ids;
	}

	public List<Group> getGroups() {
		return this.groups;
	}

	public void setGroups(List<Group> groups) {
		this.groups = groups;
	}

	public List<Long> getGroupIds() {
		return this.groupIds;
	}

	public void setGroupIds(List<Long> ids) {
		this.groupIds = ids;
	}

	public List<HWConf> getHWConfs() {
		return this.hwconfs;
	}

	public void setHWConfs(List<HWConf> hwconfs) {
		this.hwconfs = hwconfs;
	}

	public List<Long> getHWConfIds() {
		return this.hwConfIds;
	}

	public void setHWConfIds(List<Long> ids) {
		this.hwConfIds = ids;
	}

	public List<Room> getRooms() {
		return this.rooms;
	}

	public void setRooms(List<Room> rooms) {
		this.rooms = rooms;
	}

	public List<Long> getRoomIds() {
		return this.roomIds;
	}

	public void setRoomIds(List<Long> ids) {
		this.roomIds = ids;
	}

	public List<Software> getSoftwares() {
		return this.softwares;
	}

	public void setSoftwares(List<Software> softwares) {
		this.softwares = softwares;
	}

	public List<Long> getSoftwareIds() {
		return this.softwareIds;
	}

	public void setSoftwareIds(List<Long> ids) {
		this.softwareIds = ids;
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

	public List<Long> getUserIds() {
		return this.userIds;
	}

	public void setUserIds(List<Long> ids) {
		this.userIds = ids;
	}

	public boolean getStudentsOnly() {
		return this.studentsOnly;
	}

	public void setStudentsOnly( boolean studentsOnly) {
		this.studentsOnly = studentsOnly;
	}

	public List<Announcement> getAnnouncements() {
		return this.announcements;
	}

	public void setAnnouncements(List<Announcement> announcements) {
		this.announcements = announcements;
	}

	public List<Long> getAnnouncementIds() {
		return this.announcementIds;
	}

	public void setAnnouncementIds(List<Long> ids) {
		this.announcementIds = ids;
	}

	public List<Contact> getContacts() {
		return this.contacts;
	}

	public void setContacts(List<Contact> contacts) {
		this.contacts = contacts;
	}

	public List<Long> getContactIds() {
		return this.contactIds;
	}

	public void setContactIds(List<Long> ids) {
		this.contactIds = ids;
	}

	public List<FAQ> getFaqs() {
		return this.faqs;
	}

	public void setFaqs(List<FAQ> faqs) {
		this.faqs = faqs;
	}

	public List<Long> getFAQIds() {
		return this.faqIds;
	}

	public void setFAQIds(List<Long> ids) {
		this.faqIds = ids;
	}


}
