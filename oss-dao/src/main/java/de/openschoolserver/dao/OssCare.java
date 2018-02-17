package de.openschoolserver.dao;

import java.io.Serializable;
import java.lang.Long;
import java.lang.String;
import java.util.Date;
import java.util.List;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Entity implementation class for Entity: OssCare
 *
 */
@Entity
@NamedQuery(name="OssCare.findAll", query="SELECT r FROM OssCare r")
public class OssCare implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name="OSSCARES_ID_GENERATOR", sequenceName="SEQ")
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="OSSCARES_ID_GENERATOR")
	private Long id;

	private String   description;

	private String   access;

	private String   contact;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date recDate;

	@Temporal(TemporalType.TIMESTAMP)
	private Date validity;
	
	@OneToOne
	private Regcode  regcode;

	@OneToMany(mappedBy="ossCare")
	@JsonIgnore
	private  List<OssCareMessage> ossCareMessages;

	public OssCare() {
		super();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof OssCare && obj !=null) {
			return getId() == ((OssCare)obj).getId();
		}
		return super.equals(obj);
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
	public Regcode getRegcode() {
		return this.regcode;
	}

	public void setRegcode(Regcode regcode) {
		this.regcode = regcode;
	}   
	public String getAccess() {
		return this.access;
	}

	public void setAccess(String access) {
		this.access = access;
	}

	public String getContact() {
		return this.contact;
	}

	public void setContact(String contact) {
		this.contact = contact;
	}
	
	public List<OssCareMessage> getOssCareMessages() {
		return this.ossCareMessages;
	}
	
	public void setOssCareMessages(List<OssCareMessage> ossCareMessages) {
		this.ossCareMessages = ossCareMessages; 
	}
	public Date getRecDate() {
		return this.recDate;
	}

	public void setRecDate(Date recDate) {
		this.recDate = recDate;
	}

	public Date getValidity() {
		return this.validity;
	}

	public void setValidity(Date validity) {
		this.validity = validity;
	}


}
