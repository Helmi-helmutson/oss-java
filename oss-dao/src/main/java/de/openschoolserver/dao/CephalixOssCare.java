package de.openschoolserver.dao;

import java.io.Serializable;
import java.lang.Long;
import java.lang.String;
import java.util.Date;
import java.util.List;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Entity implementation class for Entity: CephalixOssCare
 *
 */
@Entity
@NamedQuery(name="CephalixOssCare.findAll", query="SELECT r FROM CephalixOssCare r")
public class CephalixOssCare implements Serializable {

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
	private CephalixRegcode  cephalixRegcode;

	@OneToMany(mappedBy="cephalixOssCare")
	@JsonIgnore
	private  List<CephalixOssCareMessage> cephalixOssCareMessages;

	public CephalixOssCare() {
		super();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof CephalixOssCare && obj !=null) {
			return getId() == ((CephalixOssCare)obj).getId();
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
	public CephalixRegcode getRegcode() {
		return this.cephalixRegcode;
	}

	public void setRegcode(CephalixRegcode cephalixRegcode) {
		this.cephalixRegcode = cephalixRegcode;
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
	
	public List<CephalixOssCareMessage> getOssCareMessages() {
		return this.cephalixOssCareMessages;
	}
	
	public void setOssCareMessages(List<CephalixOssCareMessage> cephalixOssCareMessages) {
		this.cephalixOssCareMessages = cephalixOssCareMessages; 
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
