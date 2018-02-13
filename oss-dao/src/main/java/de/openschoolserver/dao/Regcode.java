/* (c) 2018 Péter Varkoly <peter@varkoly.de> - all rights reserved */
package de.openschoolserver.dao;

import java.io.Serializable;
import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Date;


/**
 * The persistent class for the Regcode database table.
 * 
 */
@Entity
@NamedQuery(name="Regcode.findAll", query="SELECT r FROM Regcode r")
public class Regcode implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private Long id;

	private String name;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="rec_date")
	private Date recDate;

	private String status;

	@Temporal(TemporalType.TIMESTAMP)
	private Date validity;

    //bi-directional many-to-one association to CephalixInstitue
    @ManyToOne
    @JsonIgnore
    private CephalixInstitute cephalixInstitute;

	public Regcode() {
	}

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getRecDate() {
		return this.recDate;
	}

	public void setRecDate(Date recDate) {
		this.recDate = recDate;
	}

	public String getStatus() {
		return this.status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Date getValidity() {
		return this.validity;
	}

	public void setValidity(Date validity) {
		this.validity = validity;
	}

	public CephalixInstitute getCephalixInstitute() {
		return this.cephalixInstitute;
	}

	public void setCephalixInstitute(CephalixInstitute cephalixInstitute) {
		this.cephalixInstitute = cephalixInstitute;
	}

}
