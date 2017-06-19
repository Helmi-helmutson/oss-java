/* (c) 2017 Péter Varkoly <peter@varkoly.de> - all rights reserved */
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

	@Id
	@SequenceGenerator(name="TESTUSERS_ID_GENERATOR" )
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="TESTUSERS_ID_GENERATOR")
	private long id;

	//bi-directional many-to-one association to Device
	@ManyToOne
	private Device device;

	//bi-directional many-to-one association to Test
	@ManyToOne
	private Test test;

	//bi-directional many-to-one association to User
	@ManyToOne
	private User user;

        @Override
        public boolean equals(Object obj) {
                if (obj instanceof TestUser && obj !=null) {
                        return getId() == ((TestUser)obj).getId();
                }
                return super.equals(obj);
        }

	public TestUser() {
	}

	public long getId() {
		return this.id;
	}

	public void setId(long id) {
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
