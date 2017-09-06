/* (c) 2017 Péter Varkoly <peter@varkoly.de> - all rights reserved 
 * (c) 2017 EXTIS GmbH - all rights reserved */
package de.openschoolserver.dao;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Date;
import java.util.List;

import javax.persistence.*;


/**
 * The persistent class for the Users database table.
 */
@Entity
@Table(name="Users")
@NamedQueries({
	@NamedQuery(name="User.findAll", query="SELECT u FROM User u"),
	@NamedQuery(name="User.findAllId", query="SELECT u.id FROM User u"),
	@NamedQuery(name="User.findAllStudents", query="SELECT u FROM User u WHERE u.role = 'students' "),
	@NamedQuery(name="User.findAllTeachers", query="SELECT u FROM User u WHERE u.role = 'teachers' "),
	@NamedQuery(name="User.getByRole",  query="SELECT u FROM User u WHERE u.role = :role "),
	@NamedQuery(name="User.getByUid",   query="SELECT u FROM User u WHERE u.uid = :uid "),
	@NamedQuery(name="User.findByName",   query="SELECT u FROM User u WHERE u.givenName = :givenName and u.sureName = :sureName"),
	@NamedQuery(name="User.findByNameAndRole",   query="SELECT u FROM User u WHERE u.givenName = :givenName and u.sureName = :sureName and u.role = :role"),
	@NamedQuery(name="User.search", query="SELECT u FROM User u WHERE u.uid LIKE :search OR u.givenName LIKE :search OR u.sureName LIKE :search")
})
@SequenceGenerator(name="seq", initialValue=1, allocationSize=100)
public class User implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="seq")
	long id;

	private String givenName;

	private String role;

	private String sureName;

	@Column(name="uid", updatable=false)
	private String uid;
	
	private String uuid;

	@Temporal(TemporalType.DATE)	
	private Date birthDay;

	//bi-directional many-to-one association to Alias
	@OneToMany(mappedBy="user", cascade=CascadeType.ALL, orphanRemoval=true)
	private List<Alias> aliases;

	//bi-directional many-to-one association to Alias
	@OneToMany(mappedBy="user", cascade=CascadeType.ALL, orphanRemoval=true)
	private List<Acl> acls;
		
	//bi-directional many-to-one association to Device
	@OneToMany(mappedBy="owner",cascade=CascadeType.ALL, orphanRemoval=true)
	private List<Device> ownedDevices;
	
	//bi-directional many-to-one association to groups
	@OneToMany(mappedBy="owner",cascade=CascadeType.ALL, orphanRemoval=true)
	@JsonIgnore
	private List<Group> ownedGroups;

	//bi-directional many-to-one association to Device
	@OneToMany(mappedBy="owner",cascade=CascadeType.ALL, orphanRemoval=true)
	private List<Category> ownedCategories;

	//bi-directional many-to-one association to TestFile
	@OneToMany(mappedBy="user", cascade=CascadeType.ALL, orphanRemoval=true)
	@JsonIgnore
	private List<TestFile> testFiles;

	//bi-directional many-to-one association to TestUser
	@OneToMany(mappedBy="user", cascade=CascadeType.ALL, orphanRemoval=true)
	@JsonIgnore
	private List<TestUser> testUsers;

	//bi-directional many-to-one association to Test
	@OneToMany(mappedBy="user", cascade=CascadeType.ALL, orphanRemoval=true)
	@JsonIgnore
	private List<Test> tests;
	
	//bi-directional many-to-one association to RoomSmartControl
	@OneToMany(mappedBy="user")
	@JsonIgnore
	private List<RoomSmartControl> smartControls;
	
	//bi-directional many-to-one association to Device
	@OneToMany(mappedBy="owner")
	private List<FAQ> myFAQs;

	//bi-directional many-to-one association to Device
	@OneToMany(mappedBy="owner",cascade=CascadeType.ALL, orphanRemoval=true)
	private List<Contact> myContacts;

	//bi-directional many-to-one association to Device
	@OneToMany(mappedBy="owner",cascade=CascadeType.ALL, orphanRemoval=true)
	private List<Announcement> myAnnouncements;

	//bi-directional many-to-many association to Category
	@ManyToMany(mappedBy="users", cascade ={CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH} )
	private List<Category> categories;
		
	//bi-directional many-to-many association to Device
	@ManyToMany( cascade ={CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH} )
	@JoinTable(
			name="LoggedOn", 
			joinColumns={ @JoinColumn(name="user_id") },
			inverseJoinColumns={@JoinColumn(name="device_id")}
			)
	@JsonIgnore
	private List<Device> loggedOn;

	//bi-directional many-to-many association to Group
	@ManyToMany( cascade ={CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH} )
	@JoinTable(
			name="GroupMember",
			joinColumns={@JoinColumn(name="user_id")},
			inverseJoinColumns={@JoinColumn(name="group_id")}
			)
	//@JsonManagedReference
	@JsonIgnore
	private List<Group> groups;
	
	//bi-directional many-to-many association to Group
	@ManyToMany( cascade ={CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH} )
	@JoinTable(
			name="HaveSeen",
			joinColumns={@JoinColumn(name="user_id")},
			inverseJoinColumns={@JoinColumn(name="announcement_id")}
			)
	//@JsonManagedReference
	@JsonIgnore
	private List<Announcement> readAnnouncements;
	

	private Integer fsQuotaUsed;
	private Integer fsQuota;
	private Integer msQuotaUsed;
	private Integer msQuota;

	@Transient
	private String password ="";

	public User() {
		this.uid = "";
		this.uuid = "";
		this.sureName = "";
		this.givenName = "";
		this.password = "";
		this.role = "";
		this.fsQuota = 0;
		this.fsQuotaUsed = 0;
		this.msQuota = 0;
		this.msQuotaUsed = 0;
		this.birthDay = new Date(System.currentTimeMillis());
	}

	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof User && obj !=null) {
			return getId() == ((User)obj).getId();
		}
		return super.equals(obj);
	}
	public String getGivenName() {
		return this.givenName;
	}

	public void setGivenName(String givenName) {
		this.givenName = givenName;
	}

	public String getRole() {
		return this.role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getSureName() {
		return this.sureName;
	}

	public void setSureName(String surename) {
		this.sureName = surename;
	}

	public String getUid() {
		return this.uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getUuid() {
		return this.uuid;
	}

	public void setUuid(String uuid) {
		this.uid = uuid;
	}

	public Date getBirthDay() {
		return this.birthDay;
	}

	public void setBirthDay(Date birthday) {
		this.birthDay = birthday;
	}

	public List<Alias> getAliases() {
		return this.aliases;
	}

	public void setAliases(List<Alias> aliases) {
		this.aliases = aliases;
	}

	public void addAlias(Alias alias) {
		getAliases().add(alias);
		alias.setUser(this);	
	}

	public void removeAlias(Alias alias) {
		getAliases().remove(alias);
		alias.setUser(null);
	}
	
	public List<Acl> getAcls() {
		return this.acls;
	}

	public void setAcls(List<Acl> acls) {
		this.acls = acls;
	}

	public void addAcl(Acl acl) {
		getAcls().add(acl);
		acl.setUser(this);	
	}

	public void removeAcl(Acl acl) {
		getAcls().remove(acl);
		acl.setUser(null);
	}

	public List<Category> getOwnedCategories() {
		return this.ownedCategories;
	}

	public void setOwnedCagegories(List<Category> ownedCategories) {
		this.ownedCategories = ownedCategories;
	}
	
	public List<Device> getOwnedDevices() {
		return this.ownedDevices;
	}

	public void setOwnedDevices(List<Device> ownedDevices) {
		this.ownedDevices = ownedDevices;
	}

	public List<Group> getOwnedGroups() {
		return this.ownedGroups;
	}

	public void setOwnedGroups(List<Group> ownedGroups) {
		this.ownedGroups = ownedGroups;
	}

	public Device addOwnedDevice(Device ownedDevice) {
		getOwnedDevices().add(ownedDevice);
		ownedDevice.setOwner(this);
		return ownedDevice;
	}

	public Device removeOwnedDevice(Device ownedDevice) {
		getOwnedDevices().remove(ownedDevice);
		ownedDevice.setOwner(null);
		return ownedDevice;
	}

	public List<TestFile> getTestFiles() {
		return this.testFiles;
	}

	public void setTestFiles(List<TestFile> testFiles) {
		this.testFiles = testFiles;
	}

	public TestFile addTestFile(TestFile testFile) {
		getTestFiles().add(testFile);
		testFile.setUser(this);
		return testFile;
	}

	public TestFile removeTestFile(TestFile testFile) {
		getTestFiles().remove(testFile);
		testFile.setUser(null);
		return testFile;
	}

	public List<TestUser> getTestUsers() {
		return this.testUsers;
	}

	public void setTestUsers(List<TestUser> testUsers) {
		this.testUsers = testUsers;
	}

	public TestUser addTestUser(TestUser testUser) {
		getTestUsers().add(testUser);
		testUser.setUser(this);
		return testUser;
	}

	public TestUser removeTestUser(TestUser testUser) {
		getTestUsers().remove(testUser);
		testUser.setUser(null);
		return testUser;
	}

	public List<Test> getTests() {
		return this.tests;
	}

	public void setTests(List<Test> tests) {
		this.tests = tests;
	}

	public Test addTest(Test test) {
		getTests().add(test);
		test.setUser(this);
		return test;
	}

	public Test removeTest(Test test) {
		getTests().remove(test);
		test.setUser(null);
		return test;
	}

	public List<Device> getLoggedOn() {
		return this.loggedOn;
	}

	public void setLoggedOn(List<Device> loggedOn) {
		this.loggedOn = loggedOn;
	}

	public List<Group> getGroups() {
		return this.groups;
	}

	public void setGroups(List<Group> groups) {
		this.groups = groups;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPassword() {
		return this.password;
	}

	public void setFsQuotaUsed(Integer quota) {
		this.fsQuotaUsed = quota;
	}

	public void setFsQuota(Integer quota) {
		this.fsQuota = quota;
	}

	public void setMsQuotaUsed(Integer quota) {
		this.msQuotaUsed = quota;
	}

	public void setMsQuota(Integer quota) {
		this.msQuota = quota;
	}

	public Integer getFsQuotaUsed() {
		return this.fsQuotaUsed;
	}

	public Integer getFsQuota() {
		return this.fsQuota;
	}

	public Integer getMsQuotaUsed() {
		return this.msQuotaUsed;
	}

	public Integer getMsQuota() {
		return this.msQuota;
	}
    public List<Category> getCategories() {
        return this.categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }
    
    public List<RoomSmartControl> getSmartControls() {
    	return this.smartControls;
    }
	public List<Announcement> getReadAnnouncements() {
		return this.readAnnouncements;
	}

	public void setReadAnnouncements(List<Announcement> announcements) {
		this.readAnnouncements = announcements;
	}

	public List<Announcement> getMyAnnouncements() {
		return this.myAnnouncements;
	}

	public void setAnnouncement(List<Announcement> values) {
		this.myAnnouncements = values;
	}

	public List<Contact> getMyContacts() {
		return this.myContacts;
	}

	public void setMyContacts(List<Contact> values) {
		this.myContacts = values;
	}

	public List<FAQ> getMyFAQs() {
		return this.myFAQs;
	}

	public void setMyFAQs(List<FAQ> values) {
		this.myFAQs = values;
	}
}
