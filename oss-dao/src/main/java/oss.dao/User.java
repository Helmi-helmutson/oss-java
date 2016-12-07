package oss.dao;

import java.io.Serializable;
import javax.persistence.*;
import java.util.Set;


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
	@NamedQuery(name="User.findUserByRole",  query="SELECT u FROM User u WHERE u.role = :role "),
	@NamedQuery(name="User.findUserByUid",   query="SELECT u FROM User u WHERE u.uid = :uid ")
})
public class User implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
        int id;

	private String givenName;

	private String role;

	private String sn;

	private String uid;

	//bi-directional many-to-one association to Alias
	@OneToMany(mappedBy="user")
	private Set<Alias> aliases;

	//bi-directional many-to-one association to Device
	@OneToMany(mappedBy="owner")
	private Set<Device> ownedDevices;

	//bi-directional many-to-one association to TestFile
	@OneToMany(mappedBy="user")
	private Set<TestFile> testFiles;

	//bi-directional many-to-one association to TestUser
	@OneToMany(mappedBy="user")
	private Set<TestUser> testUsers;

	//bi-directional many-to-one association to Test
	@OneToMany(mappedBy="user")
	private Set<Test> tests;

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
	private Set<Device> loggedOn;

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
	private Set<Group> groups;

	public User() {
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
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

	public String getSn() {
		return this.sn;
	}

	public void setSn(String sn) {
		this.sn = sn;
	}

	public String getUid() {
		return this.uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public Set<Alias> getAliases() {
		return this.aliases;
	}

	public void setAliases(Set<Alias> aliases) {
		this.aliases = aliases;
	}

	public Alias addAlias(Alias alias) {
		getAliases().add(alias);
		alias.setUser(this);

		return alias;
	}

	public Alias removeAlias(Alias alias) {
		getAliases().remove(alias);
		alias.setUser(null);

		return alias;
	}

	public Set<Device> getOwnedDevices() {
		return this.ownedDevices;
	}

	public void setOwnedDevices(Set<Device> ownedDevices) {
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

	public Set<TestFile> getTestFiles() {
		return this.testFiles;
	}

	public void setTestFiles(Set<TestFile> testFiles) {
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

	public Set<TestUser> getTestUsers() {
		return this.testUsers;
	}

	public void setTestUsers(Set<TestUser> testUsers) {
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

	public Set<Test> getTests() {
		return this.tests;
	}

	public void setTests(Set<Test> tests) {
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

	public Set<Device> getLoggedOn() {
		return this.loggedOn;
	}

	public void setLoggedOn(Set<Device> loggedOn) {
		this.loggedOn = loggedOn;
	}

	public Set<Group> getGroups() {
		return this.groups;
	}

	public void setGroups(Set<Group> groups) {
		this.groups = groups;
	}

}
