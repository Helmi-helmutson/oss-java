/* (c) 2017 Péter Varkoly <peter@varkoly.de> - all rights reserved */
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
	private long testId;

	@Column(name="user_id", insertable=false, updatable=false)
	private long userId;

	public TestUserPK() {
	}
	public long getTestId() {
		return this.testId;
	}
	public void setTestId(long testId) {
		this.testId = testId;
	}
	public long getUserId() {
		return this.userId;
	}
	public void setUserId(long userId) {
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
		hash = hash * prime + (int) this.testId;
		hash = hash * prime + (int) this.userId;
		
		return hash;
	}
}
