/* (c) 2017 Péter Varkoly <peter@varkoly.de> - all rights reserved */
package de.openschoolserver.dao;

import java.io.Serializable;
import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Date;
import java.util.List;


/**
 * The persistent class for the Announcements database table.
 * 
 */
@Entity
@Table(name="Announcements")
@NamedQuery(name="Announcement.findAll", query="SELECT a FROM Announcement a")
public class Announcement implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name="ANNOUNCEMENTS_ID_GENERATOR" )
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="ANNOUNCEMENTS_ID_GENERATOR")
	private Long id;

	@Lob
	@Column(name="abstract")
	private byte[] abstract_;

	private String issuer;

	private String keywords;

	@Lob
	private byte[] text;

	private String title;


	@Temporal(TemporalType.TIMESTAMP)
	private Date validFrom;

	@Temporal(TemporalType.TIMESTAMP)
	private Date validUntil;

	//bi-directional many-to-many association to User
	@ManyToMany(mappedBy="readAnnouncements",cascade ={CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
	private List<User> haveSeenUsers;

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

	public Announcement() {
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

	public String getIssuer() {
		return this.issuer;
	}

	public void setIssuer(String issuer) {
		this.issuer = issuer;
	}

	public String getKeywords() {
		return this.keywords;
	}

	public void setKeywords(String keywords) {
		this.keywords = keywords;
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

	public Date getValidFrom() {
		return this.validFrom;
	}

	public void setValidFrom(Date validFrom) {
		this.validFrom = validFrom;
	}

	public Date getValidUntil() {
		return this.validUntil;
	}

	public void setValidUntil(Date validUntil) {
		this.validUntil = validUntil;
	}

	public List<User> getHaveSeenUsers() {
		return this.haveSeenUsers;
	}

	public void setHaveSeenUsers(List<User> haveSeenUsers) {
		this.haveSeenUsers = haveSeenUsers;
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
}