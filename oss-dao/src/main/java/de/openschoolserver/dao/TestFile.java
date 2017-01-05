/* (c) 2017 Péter Varkoly <peter@varkoly.de> - all rights reserved */
package de.openschoolserver.dao;

import java.io.Serializable;
import javax.persistence.*;
import java.util.Date;


/**
 * The persistent class for the TestFiles database table.
 * 
 */
@Entity
@Table(name="TestFiles")
@NamedQuery(name="TestFile.findAll", query="SELECT t FROM TestFile t")
public class TestFile implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private long id;

	@Temporal(TemporalType.TIMESTAMP)
	private Date dateTime;

	private String fileName;

	private String getOrPost;

	//bi-directional many-to-one association to Test
	@ManyToOne
	private Test test;

	//bi-directional many-to-one association to User
	@ManyToOne
	private User user;

	public TestFile() {
	}

	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Date getDateTime() {
		return this.dateTime;
	}

	public void setDateTime(Date dateTime) {
		this.dateTime = dateTime;
	}

	public String getFileName() {
		return this.fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getGetOrPost() {
		return this.getOrPost;
	}

	public void setGetOrPost(String getOrPost) {
		this.getOrPost = getOrPost;
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
