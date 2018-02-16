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
@NamedQueries({
	@NamedQuery(name="Regcode.findAll",   query="SELECT r FROM Regcode r"),
	@NamedQuery(name="Regcode.getByName", query="SELECT r FROM Regcode r WHERE r.name = :name" )
})
public class Regcode implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name="REGCODES_ID_GENERATOR", sequenceName="SEQ")
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="REGCODES_ID_GENERATOR")
	private Long id;

	private String name;

	private String status;
	
	private String description;

	@Temporal(TemporalType.TIMESTAMP)
	private Date recDate;

	@Temporal(TemporalType.TIMESTAMP)
	private Date validity;

    //bi-directional many-to-one association to CephalixInstitue
    @ManyToOne
    @JsonIgnore
    private CephalixInstitute cephalixInstitute;
    
    @OneToOne(mappedBy="regcode")
    private OssCare osscare;
    
    @OneToOne(mappedBy="regcode")
    private OssDynDns ossdyndns;

	public Regcode() {
		osscare   = null;
		ossdyndns = null;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Regcode && obj !=null) {
			return getId() == ((Regcode)obj).getId();
		}
		return super.equals(obj);
	}


	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public OssCare getOssCare() {
		return this.osscare;
	}

	public void setOssCare(OssCare osscare) {
		this.osscare = osscare;
	}

	public OssDynDns getOssDynDns() {
		return this.ossdyndns;
	}

	public void setOssDynDns(OssDynDns ossdyndns) {
		this.ossdyndns = ossdyndns;
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

	public Date getValidity() {
		return this.validity;
	}

	public void setValidity(Date validity) {
		this.validity = validity;
	}

	public String getStatus() {
		return this.status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public CephalixInstitute getCephalixInstitute() {
		return this.cephalixInstitute;
	}

	public void setCephalixInstitute(CephalixInstitute cephalixInstitute) {
		this.cephalixInstitute = cephalixInstitute;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
