/* (c) 2017 P��ter Varkoly <peter@varkoly.de> - all rights reserved */
package de.openschoolserver.dao;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Date;
import java.util.List;

import javax.persistence.*;


/**
 * The persistent class for the Users database table.
 * 
 */
@Entity
@Table(name="Users")
@NamedQueries({
	@NamedQuery(name="User.findAll", query="SELECT u FROM User u"),
	@NamedQuery(name="User.findAllStudents", query="SELECT u FROM User u WHERE u.role = 'students' "),
	@NamedQuery(name="User.findAllTeachers", query="SELECT u FROM User u WHERE u.role = 'teachers' "),
	@NamedQuery(name="User.getByRole",  query="SELECT u FROM User u WHERE u.role = :role "),
	@NamedQuery(name="User.getByUid",   query="SELECT u FROM User u WHERE u.uid = :uid "),
	@NamedQuery(name="User.search", query="SELECT u FROM User u WHERE u.uid LIKE :search OR u.givenName LIKE :search OR u.sureName LIKE :search"),
	@NamedQuery(name="User.getConfig",  query="SELECT c.value FROM UserConfig c WHERE c.user.id = :user_id AND c.keyword = :keyword" ),
	@NamedQuery(name="User.getMConfig", query="SELECT c.value FROM UserMConfig c WHERE c.user.id = :user_id AND c.keyword = :keyword" ),
	@NamedQuery(name="User.checkConfig", query="SELECT COUNT(c) FROM UserConfig c WHERE c.user.id = :user_id AND c.keyword = :keyword AND c.value = :value" ),
	@NamedQuery(name="User.checMkConfig", query="SELECT COUNT(c) FROM UserMConfig c WHERE c.user.id = :user_id AND c.keyword = :keyword AND c.value = :value" ),
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

	//bi-directional many-to-one association to TestFile
	@OneToMany(mappedBy="user", cascade=CascadeType.ALL, orphanRemoval=true)
	private List<UserMConfig> userMConfigs;

	//bi-directional many-to-one association to TestFile
	@OneToMany(mappedBy="user", cascade=CascadeType.ALL, orphanRemoval=true)
	private List<UserConfig> userConfigs;

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

	//bi-directional many-to-many association to Device
	@ManyToMany
	@JoinTable(
			name="LoggedOn"
			, joinColumns={
					@JoinColumn(name="user_id")
			}
			, inverseJoinColumns={
					@JoinColumn(name="device_id")
			}
			)
	@JsonIgnore
	private List<Device> loggedOn;

	//bi-directional many-to-many association to Group
	@ManyToMany
	@JoinTable(
			name="GroupMember"
			, joinColumns={
					@JoinColumn(name="user_id")
			}
			, inverseJoinColumns={
					@JoinColumn(name="group_id")
			}
			)
	//@JsonManagedReference
	@JsonIgnore
	private List<Group> groups;

	@Transient
	private String password ="";

	public User() {
		this.uid = "";
		this.sureName = "";
		this.givenName = "";
		this.password = "";
		this.role = "";
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

	public List<Device> getOwnedDevices() {
		return this.ownedDevices;
	}

	public void setOwnedDevices(List<Device> ownedDevices) {
		this.ownedDevices = ownedDevices;
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

	public List<UserConfig> getUserConfigs() {
		return this.userConfigs;
	}

	public void setUserConfigs(List<UserConfig> userConfigs) {
		this.userConfigs = userConfigs;
	}

	public UserConfig addUserConfig(UserConfig userConfig) {
		getUserConfigs().add(userConfig);
		userConfig.setUser(this);
		return userConfig;
	}

	public UserConfig removeUserConfig(UserConfig userConfig) {
		getUserConfigs().remove(userConfig);
		userConfig.setUser(null);
		return userConfig;
	}

	public List<UserMConfig> getUserMConfigs() {
		return this.userMConfigs;
	}

	public void setUserMConfigs(List<UserMConfig> userMConfigs) {
		this.userMConfigs = userMConfigs;
	}

	public UserMConfig addUserMConfig(UserMConfig userMConfig) {
		getUserMConfigs().add(userMConfig);
		userMConfig.setUser(this);
		return userMConfig;
	}

	public UserMConfig removeUserMConfig(UserMConfig userMConfig) {
		getUserMConfigs().remove(userMConfig);
		userMConfig.setUser(null);
		return userMConfig;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPassword() {
		return this.password;
	}

}
