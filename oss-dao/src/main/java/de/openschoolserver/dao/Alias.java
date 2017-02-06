/* (c) 2017 Péter Varkoly <peter@varkoly.de> - all rights reserved */
package de.openschoolserver.dao;

import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.persistence.*;


/**
 * The persistent class for the Aliases database table.
 * 
 */
@Entity
@Table(name="Aliases")
@NamedQuery(name="Alias.findAll", query="SELECT a FROM Alias a")
public class Alias implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private long id;

	private String alias;

	//bi-directional many-to-one association to User
	@ManyToOne
	@JsonIgnore
	private User user;

	public Alias() {
	}

	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getAlias() {
		return this.alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public User getUser() {
		return this.user;
	}

	public void setUser(User user) {
		this.user = user;
	}

}
