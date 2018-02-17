package de.openschoolserver.dao;

import java.io.Serializable;
import java.lang.Long;
import java.lang.String;
import javax.persistence.*;
import java.util.Date;

/**
 * Entity implementation class for Entity: CephalixOssCareMessage
 *
 */
@Entity
public class CephalixOssCareMessage implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name="OSSCARES_ID_GENERATOR", sequenceName="SEQ")
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="OSSCARES_ID_GENERATOR")
	private Long id;

	@ManyToOne
	private CephalixOssCare cephalixOssCare;

	@Temporal(TemporalType.TIMESTAMP)
	private Date recDate;

	private String description;

	private String text;

	private String type;

	public CephalixOssCareMessage() {
		super();
	}   
	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}   
	public CephalixOssCare getOssCare() {
		return this.cephalixOssCare;
	}

	public void setOssCare(CephalixOssCare osscare) {
		this.cephalixOssCare = osscare;
	}  
	public Date getRecDate() {
		return this.recDate;
	}

	public void setRecDate(Date recDate) {
		this.recDate = recDate;
	}   
	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}   
	public String getText() {
		return this.text;
	}

	public void setText(String text) {
		this.text = text;
	}   
	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

}
