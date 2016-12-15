package de.openschoolserver.dao;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the TestUsers database table.
 * 
 */
@Entity
@Table(name="TestUsers")
@NamedQuery(name="TestUser.findAll", query="SELECT t FROM TestUser t")
public class TestUser implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private TestUserPK id;

	//bi-directional many-to-one association to Device
	@ManyToOne
	private Device device;

	//bi-directional many-to-one association to Test
	@ManyToOne
	private Test test;

	//bi-directional many-to-one association to User
	@ManyToOne
	private User user;

	public TestUser() {
	}

	public TestUserPK getId() {
		return this.id;
	}

	public void setId(TestUserPK id) {
		this.id = id;
	}

	public Device getDevice() {
		return this.device;
	}

	public void setDevice(Device device) {
		this.device = device;
	}

	public Test getTest() {
		return this.test;
	}

	public void setTest(Test test) {
		this.test = test;
	}

	public User getUser() {
		return this.user;
	}

	public void setUser(User user) {
		this.user = user;
	}

}
