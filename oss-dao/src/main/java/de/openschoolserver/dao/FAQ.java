/* (c) 2017 Péter Varkoly <peter@varkoly.de> - all rights reserved */
package de.openschoolserver.dao;

import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;


/**
 * The persistent class for the FAQs database table.
 * 
 */
@Entity
@Table(name="FAQs")
@NamedQuery(name="FAQ.findAll", query="SELECT f FROM FAQ f")
public class FAQ implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name="FAQS_ID_GENERATOR" )
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="FAQS_ID_GENERATOR")
	private Long id;

	@Lob
	@Column(name="abstract")
	private byte[] abstract_;

	@Size(max=128, message="Issue must not be longer then 128 characters.")
	private String issue;

	@Lob
	private byte[] text;

	@Size(max=128, message="Title must not be longer then 128 characters.")
	private String title;

	//bi-directional many-to-many association to Category
	@ManyToMany
	@JoinColumn(name="id")
	@JsonIgnore
	private List<Category> categories;

	//bi-directional many-to-one association to User
	@ManyToOne
	@JsonIgnore
	private User owner;

	@Column(name="owner_id", insertable=false, updatable=false)
	private Long ownerId;

	public FAQ() {
	}

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public byte[] getAbstract_() {
		return this.abstract_;
	}

	public void setAbstract_(byte[] abstract_) {
		this.abstract_ = abstract_;
	}

	public String getIssue() {
		return this.issue;
	}

	public void setIssue(String issue) {
		this.issue = issue;
	}

	public byte[] getText() {
		return this.text;
	}

	public void setText(byte[] text) {
		this.text = text;
	}

	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public List<Category> getCategories() {
		return this.categories;
	}

	public void setCategories(List<Category> categories) {
		this.categories = categories;
	}
	public User getOwner() {
		return this.owner;
	}

	public void setOwner(User owner) {
		this.owner = owner;
	}

	@Override
	public String toString() {
		try {
			return new ObjectMapper().writeValueAsString(this);
		} catch (Exception e) {
			return "{ \"ERROR\" : \"CAN NOT MAP THE OBJECT\" }";
		}
	}
	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FAQ other = (FAQ) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
}
