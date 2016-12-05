package oss.dao;

import java.io.Serializable;
import javax.persistence.*;
import java.util.Date;
import java.util.Set;


/**
 * The persistent class for the Tests database table.
 * 
 */
@Entity
@Table(name="Tests")
@NamedQuery(name="Test.findAll", query="SELECT t FROM Test t")
public class Test implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private int id;

	private String currentStep;

	@Column(name="DirectInternetAccess")
	private Object directInternetAccess;

	@Temporal(TemporalType.TIMESTAMP)
	private Date endTime;

	private Object proxyAccess;

	@Temporal(TemporalType.TIMESTAMP)
	private Date startTime;

	private String testDir;

	private String testName;

	private Object windowsAccess;

	//bi-directional many-to-one association to TestFile
	@OneToMany(mappedBy="test")
	private Set<TestFile> testFiles;

	//bi-directional many-to-one association to TestUser
	@OneToMany(mappedBy="test")
	private Set<TestUser> testUsers;

	//bi-directional many-to-one association to Room
	@ManyToOne
	private Room room;

	//bi-directional many-to-one association to User
	@ManyToOne
	@JoinColumn(name="teacher_id")
	private User user;

	public Test() {
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getCurrentStep() {
		return this.currentStep;
	}

	public void setCurrentStep(String currentStep) {
		this.currentStep = currentStep;
	}

	public Object getDirectInternetAccess() {
		return this.directInternetAccess;
	}

	public void setDirectInternetAccess(Object directInternetAccess) {
		this.directInternetAccess = directInternetAccess;
	}

	public Date getEndTime() {
		return this.endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public Object getProxyAccess() {
		return this.proxyAccess;
	}

	public void setProxyAccess(Object proxyAccess) {
		this.proxyAccess = proxyAccess;
	}

	public Date getStartTime() {
		return this.startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public String getTestDir() {
		return this.testDir;
	}

	public void setTestDir(String testDir) {
		this.testDir = testDir;
	}

	public String getTestName() {
		return this.testName;
	}

	public void setTestName(String testName) {
		this.testName = testName;
	}

	public Object getWindowsAccess() {
		return this.windowsAccess;
	}

	public void setWindowsAccess(Object windowsAccess) {
		this.windowsAccess = windowsAccess;
	}

	public Set<TestFile> getTestFiles() {
		return this.testFiles;
	}

	public void setTestFiles(Set<TestFile> testFiles) {
		this.testFiles = testFiles;
	}

	public TestFile addTestFile(TestFile testFile) {
		getTestFiles().add(testFile);
		testFile.setTest(this);

		return testFile;
	}

	public TestFile removeTestFile(TestFile testFile) {
		getTestFiles().remove(testFile);
		testFile.setTest(null);

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
		testUser.setTest(this);

		return testUser;
	}

	public TestUser removeTestUser(TestUser testUser) {
		getTestUsers().remove(testUser);
		testUser.setTest(null);

		return testUser;
	}

	public Room getRoom() {
		return this.room;
	}

	public void setRoom(Room room) {
		this.room = room;
	}

	public User getUser() {
		return this.user;
	}

	public void setUser(User user) {
		this.user = user;
	}

}