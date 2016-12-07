package de.openschoolserver.dao;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the TestUsers database table.
 * 
 */
@Embeddable
public class TestUserPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="test_id", insertable=false, updatable=false)
	private int testId;

	@Column(name="user_id", insertable=false, updatable=false)
	private int userId;

	public TestUserPK() {
	}
	public int getTestId() {
		return this.testId;
	}
	public void setTestId(int testId) {
		this.testId = testId;
	}
	public int getUserId() {
		return this.userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof TestUserPK)) {
			return false;
		}
		TestUserPK castOther = (TestUserPK)other;
		return 
			(this.testId == castOther.testId)
			&& (this.userId == castOther.userId);
	}

	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.testId;
		hash = hash * prime + this.userId;
		
		return hash;
	}
}