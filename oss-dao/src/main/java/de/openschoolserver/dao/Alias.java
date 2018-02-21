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
@SequenceGenerator(name="seq", initialValue=1, allocationSize=100)
public class Alias implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="seq")
	private Long id;

	private String alias;

	//bi-directional many-to-one association to User
	@ManyToOne
	@JsonIgnore
	private User user;

	@Override
	public boolean equals(Object obj) {
	       if (obj instanceof Alias && obj !=null) {
	               return getId() == ((Alias)obj).getId();
	       }
	       return super.equals(obj);
	}

	public Alias() {
	}

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
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
