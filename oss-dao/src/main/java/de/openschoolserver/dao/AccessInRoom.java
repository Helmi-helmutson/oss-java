package de.openschoolserver.dao;

import java.io.Serializable;
import javax.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;


/**
 * The persistent class for the AccessInRoom database table.
 * 
 */
@Entity
@NamedQueries( {
    @NamedQuery(name="AccessInRoom.findAll",            query="SELECT a FROM AccessInRoom a"),
    @NamedQuery(name="AccessInRoom.findByRoom",         query="SELECT a FROM AccessInRoom a WHERE a.room = :room"),
    @NamedQuery(name="AccessInRoom.findActualAccesses", query="SELECT a FROM AccessInRoom a WHERE a.pointInTime = :time")
})

public class AccessInRoom implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name="ACCESSINROOM_ID_GENERATOR", sequenceName="SEQ")
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="ACCESSINROOM_ID_GENERATOR")
	private long id;

	//uni-directional many-to-one association to Room
	@ManyToOne
	@JsonIgnore
	private Room room;

	private String accessType;

	private String action;

	@Column(name="room_id", insertable = false, updatable = false)
	private long roomId;

	@Convert(converter=BooleanToStringConverter.class)
	private Boolean monday;

	@Convert(converter=BooleanToStringConverter.class)
	private Boolean tusday;

	@Convert(converter=BooleanToStringConverter.class)
	private Boolean wednesday;

	@Convert(converter=BooleanToStringConverter.class)
	private Boolean thursday;

	@Convert(converter=BooleanToStringConverter.class)
	private Boolean friday;

	@Convert(converter=BooleanToStringConverter.class)
	private Boolean saturday;

	@Convert(converter=BooleanToStringConverter.class)
	private Boolean sunday;

	@Convert(converter=BooleanToStringConverter.class)
	private Boolean holiday;

	@Convert(converter=BooleanToStringConverter.class)
	private Boolean direct;

	@Convert(converter=BooleanToStringConverter.class)
	private Boolean login;

	@Convert(converter=BooleanToStringConverter.class)
	private Boolean portal;

	@Convert(converter=BooleanToStringConverter.class)
	private Boolean printing;

	@Convert(converter=BooleanToStringConverter.class)
	private Boolean proxy;
	
	private String  pointInTime;

	public AccessInRoom() {
		this.pointInTime = "06:00";
		this.monday   = true;
		this.tusday   = true;
		this.wednesday= true;
		this.thursday = true;
		this.friday   = true;
		this.saturday = false;
		this.sunday   = false;
		this.holiday  = false;
		this.direct   = false;
		this.login    = true;
		this.portal   = true;
		this.printing = true;
		this.proxy    = true;
		this.action  = "";
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof AccessInRoom && obj !=null) {
			return getId() == ((AccessInRoom)obj).getId();
		}
		return super.equals(obj);
	}

	public Room getRoom() {
		return this.room;
	}

	public void setRoom(Room room) {
		this.room = room;
	}

	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getAccessType() {
		return this.accessType;
	}

	public void setAccessType(String accessType) {
		this.accessType = accessType;
	}

	public String getAction() {
		return this.action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public Boolean getDirect() {
		return this.direct;
	}

	public void setDirect(Boolean direct) {
		this.direct = direct;
	}

	public Boolean getFriday() {
		return this.friday;
	}

	public void setFriday(Boolean friday) {
		this.friday = friday;
	}

	public Boolean getHoliday() {
		return this.holiday;
	}

	public void setHoliday(Boolean holiday) {
		this.holiday = holiday;
	}

	public Boolean getLogin() {
		return this.login;
	}

	public void setLogin(Boolean login) {
		this.login = login;
	}

	public Boolean getMonday() {
		return this.monday;
	}

	public void setMonday(Boolean monday) {
		this.monday = monday;
	}

	public String getPointInTime() {
		return this.pointInTime;
	}

	public void setPointInTime(String pointInTime) {
		this.pointInTime = pointInTime;
	}

	public Boolean getPortal() {
		return this.portal;
	}

	public void setPortal(Boolean portal) {
		this.portal = portal;
	}

	public Boolean getPrinting() {
		return this.printing;
	}

	public void setPrinting(Boolean printing) {
		this.printing = printing;
	}

	public Boolean getProxy() {
		return this.proxy;
	}

	public void setProxy(Boolean proxy) {
		this.proxy = proxy;
	}

	public long getRoomId() {
		return this.roomId;
	}

	public void setRoomId(long roomId) {
		this.roomId = roomId;
	}

	public Boolean getSaturday() {
		return this.saturday;
	}

	public void setSaturday(Boolean saturday) {
		this.saturday = saturday;
	}

	public Boolean getSunday() {
		return this.sunday;
	}

	public void setSunday(Boolean sunday) {
		this.sunday = sunday;
	}

	public Boolean getThursday() {
		return this.thursday;
	}

	public void setThursday(Boolean thursday) {
		this.thursday = thursday;
	}

	public Boolean getTusday() {
		return this.tusday;
	}

	public void setTusday(Boolean tusday) {
		this.tusday = tusday;
	}

	public Boolean getWednesday() {
		return this.wednesday;
	}

	public void setWednesday(Boolean wednesday) {
		this.wednesday = wednesday;
	}

}
