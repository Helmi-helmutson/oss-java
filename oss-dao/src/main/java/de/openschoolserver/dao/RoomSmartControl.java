package de.openschoolserver.dao;

import java.io.Serializable;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * The persistent class for the RoomConfig database table.
 *
 */
@Entity
@Table(name="RoomSmartControlls")
@NamedQueries({
	@NamedQuery(name="SmartControl.findAll", query="SELECT s FROM RoomSmartControl s") /*,
	@NamedQuery(name="SmartControl.getAllActive", query="SELECT s FROM RoomSmartControl s WHERE s.endTime < NOW" ),
	@NamedQuery(name="SmartControl.getAllActiveInRoom", query="SELECT s FROM RoomSmartControl s WHERE s.endTime < NOW AND s.room_id = :roomId" ),
	@NamedQuery(name="SmartControl.getAllActiveOfUser", query="SELECT s FROM RoomSmartControl s WHERE s.endTime < NOW AND s.user_id = :userId" )*/
})
@SequenceGenerator(name="seq", initialValue=1, allocationSize=100)
public class RoomSmartControl implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name="ACCESSINROOM_ID_GENERATOR", sequenceName="SEQ")
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="ACCESSINROOM_ID_GENERATOR")
	private long id;
	
	//bi-directional many-to-one association to room
    @Column(name = "room_id")
    private Long roomId;

    @ManyToOne
    @JoinColumn(name = "room_id", insertable = false, updatable = false)
    @JsonIgnore
    private Room room;
	
    //bi-directional many-to-one association to room
    @Column(name = "user_id")
    private Long userId;

    @ManyToOne
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    @JsonIgnore
    private User user;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "startTime")
	private Date startTime; 
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "endTime")
	private Date endTime; 
	   
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof RoomSmartControl && obj !=null) {
			return getId() == ((RoomSmartControl)obj).getId();
		}
		return super.equals(obj);
	}

	public RoomSmartControl() {
	}
	
    public RoomSmartControl(Long roomId, Long userId, Long duration) {
		this.userId = userId;
		this.roomId = roomId;
		this.startTime = new Date();
		this.endTime   = new Date( System.currentTimeMillis( ) + duration * 60 * 1000 );
	}

	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Room getRoom() {
		return this.room;
	}

	public void setRoomId(Long roomId) {
		this.roomId = roomId;
	}

	public User getUser() {
		return this.user;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}
	
	public Date getStartTime() {
		return this.startTime;
	}
	
	public Date getEndTime() {
		return this.endTime;
	}
}
