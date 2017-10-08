package de.openschoolserver.dao;

import java.io.Serializable;
import javax.persistence.*;
import java.math.BigInteger;


/**
 * The persistent class for the CephalixMappings database table.
 * 
 */
@Entity
@Table(name="CephalixMappings")
@NamedQueries({
	@NamedQuery(name="CephalixMapping.findAll",			query="SELECT c FROM CephalixMapping c"),
	@NamedQuery(name="CephalixMapping.getByCephalixId",	query="SELECT c FROM CephalixMapping c WHERE c instituteId = :instituteId AND objectName = :objectName AND cephalixId = :cephalixId"),
	@NamedQuery(name="CephalixMapping.getByOssId",		query="SELECT c FROM CephalixMapping c WHERE c instituteId = :instituteId AND objectName = :objectName AND ossId = :ossId"),
	@NamedQuery(name="CephalixMapping.ofInstitute",		query="SELECT c FROM CephalixMapping c WHERE c instituteId = :instituteId AND objectName = :objectName")
})
public class CephalixMapping implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name="CEPHALIXMAPPINGS_ID_GENERATOR" )
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="CEPHALIXMAPPINGS_ID_GENERATOR")
	private Long id;

	private Long cephalixId;

	private String objectName;

	private Long ossId;

	//bi-directional many-to-one association to CephalixInstitute
	@ManyToOne
	@JoinColumn(name="institute_id")
	private CephalixInstitute cephalixInstitute;
	
	@Column(name = "institute_id", insertable = false, updatable = false)
    private Long instituteId;

	public CephalixMapping() {
	}
	
	public CephalixMapping(CephalixInstitute institute,Long cephalixId, String objectName, Long ossId ){
		this.cephalixInstitute = institute;
		this.cephalixId = cephalixId;
		this.objectName = objectName;
		this.ossId      = ossId;
	}
	
	public CephalixMapping(CephalixInstitute institute,Object object, Long ossId ){
		this.cephalixInstitute	= institute;
		this.ossId				= ossId;
		switch(object.getClass().getName()) {
		case "de.openschoolserver.dao.Announcement":
			Announcement Announcement = (Announcement)object;
			this.cephalixId = Announcement.getId();
			this.objectName = "Announcement";
			break;
		case "de.openschoolserver.dao.Category":
			Category Category = (Category)object;
			this.cephalixId = Category.getId();
			this.objectName = "Category";
			break;
		case "de.openschoolserver.dao.Contact":
			Contact Contact = (Contact)object;
			this.cephalixId = Contact.getId();
			this.objectName = "Contact";
			break;
		case "de.openschoolserver.dao.Device":
			Device Device = (Device)object;
			this.cephalixId = Device.getId();
			this.objectName = "Device";
			break;
		case "de.openschoolserver.dao.FAQ":
			FAQ FAQ = (FAQ)object;
			this.cephalixId = FAQ.getId();
			this.objectName = "FAQ";
			break;
		case "de.openschoolserver.dao.Group":
			Group group = (Group)object;
			this.cephalixId = group.getId();
			this.objectName = "Group";
			break;
		case "de.openschoolserver.dao.HWConf":
			HWConf HWConf = (HWConf)object;
			this.cephalixId = HWConf.getId();
			this.objectName = "HWConf";
			break;
		case "de.openschoolserver.dao.Room":
			Room Room = (Room)object;
			this.cephalixId = Room.getId();
			this.objectName = "Room";
			break;
		case "de.openschoolserver.dao.Software":
			Software Software = (Software)object;
			this.cephalixId = Software.getId();
			this.objectName = "Software";
			break;
		case "de.openschoolserver.dao.User":
			User user = (User)object;
			this.cephalixId = user.getId();
			this.objectName = "User";
			break;
		}
	}

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getCephalixId() {
		return this.cephalixId;
	}

	public void setCephalixId(Long cephalixId) {
		this.cephalixId = cephalixId;
	}

	public String getObjectName() {
		return this.objectName;
	}

	public void setObjectName(String objectName) {
		this.objectName = objectName;
	}

	public Long getOssId() {
		return this.ossId;
	}

	public void setOssId(Long ossId) {
		this.ossId = ossId;
	}

	public CephalixInstitute getCephalixInstitute() {
		return this.cephalixInstitute;
	}

	public void setCephalixInstitute(CephalixInstitute cephalixInstitute) {
		this.cephalixInstitute = cephalixInstitute;
	}

}