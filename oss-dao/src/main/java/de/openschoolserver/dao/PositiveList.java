package de.openschoolserver.dao;

import java.io.Serializable;
import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;


/**
 * The persistent class for the PositiveList database table.
 * 
 */
@Entity
@NamedQuery(name="PositiveList.findAll", query="SELECT p FROM PositiveList p")
public class PositiveList implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name="POSITIVELIST_ID_GENERATOR" )
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="POSITIVELIST_ID_GENERATOR")
	private Long id;

	private String description;

	private String name;

		
	private String subject;

	//bi-directional many-to-one association to User
	@ManyToOne
	@JsonIgnore
	private User owner;
	
	@Transient
	String domains;
	
	public PositiveList() {
	}

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSubject() {
		return this.subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public User getOwner() {
		return owner;
	}

	public void setOwner(User owner) {
		this.owner = owner;
	}

	public String getDomains() {
		return domains;
	}

	public void setDomains(String domains) {
		this.domains = domains;
	}

}