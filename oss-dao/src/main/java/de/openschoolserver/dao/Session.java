/* (c) 2017 Péter Varkoly <peter@varkoly.de> - all rights reserved */
/* (c) 2016 EXTIS GmbH - all rights reserved */
package de.openschoolserver.dao;

import java.security.Principal;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.persistence.*;


import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "Sessions")
@NamedQueries({ @NamedQuery(name = "Session.getByToken", query = "SELECT s FROM Session s WHERE s.token=:token") })
public class Session implements Principal {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = IDENTITY)
    private int id;
     
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "createdate")
    private Date createDate; 
     
    @Transient
    private String password = "dummy";
    
    @Transient
    private String schoolId = "dummy";
    
    @Column(name="device_id")
    private Long deviceId;
    
    //@OneToOne
    @JoinColumn(name = "device_id", insertable = false, updatable = false)
    @JsonIgnore
    private Device device;
        
    @Column(name = "user_id")
    private Long userId;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    @JsonIgnore
    private User user;
    
    @Transient
    private String role = "dummy";
    
    @Column(name = "room_id")
    private Long roomId;

    @OneToOne
    @JoinColumn(name = "room_id", insertable = false, updatable = false)
    @JsonIgnore
    private Room room;
    
    @Transient
    private String mac;
    
    @Transient
    private String dnsName;
    
    @Column(name = "ip")
    private String ip;
    
    @Column(name = "token")
    private String token;
    
    @JsonIgnore
    private transient Object temporaryUploadData;
	
	public Object getTemporaryUploadData() {
		return temporaryUploadData;
	}
	public void setTemporaryUploadData(Object temporaryUploadData) {
		this.temporaryUploadData = temporaryUploadData;
	}
    
    public Session(String token, Long userid, String password, String ip) {
        this.userId = userid;
        this.password = password;
        this.token = token;
        this.schoolId="dummy";
    }

    public Session() {
    	this.deviceId = null;
    	this.roomId   = null;
    	this.dnsName  = null;
    	this.mac      = null;
    }
    
    @Override
    public int hashCode() {
        return token != null ? token.hashCode() : id;
    }

    @Override
    public boolean equals(Object obj) {
        return token != null && obj != null && ((Session) obj).getToken() != null
                && token.equals(((Session) obj).getToken());
    }
    
    @Override
    public String toString() {
    	StringBuilder data = new StringBuilder();
    	if( this.deviceId != null ) {
    		data.append("deviceId: '" + String.valueOf(this.deviceId)).append("' ");
    	} else {
    		data.append("deviceId: 'null' ");
    	}
    	data.append("userId: '" + String.valueOf(this.userId)).append("' ");
    	data.append("token: '" + this.token).append("' ");
    	data.append("mac: '" + this.mac).append("' ");
    	data.append("role: '" + this.role).append("' ");
    	return data.toString();
    }

    @Override
    public String getName() {    
        return "dummy";
    }
    
    public String getSchoolId() {
        return this.schoolId;
    }

    public void setSchoolId(String schoolId) {
        this.schoolId = schoolId;
    }

    public String getIP() {
        return this.ip;
    }

    public void setIP(String IP) {
        this.ip = IP;
    }

    public Room getRoom() {
        return this.room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }
    
    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }
    
    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return this.role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Long getUserId() {
        return this.userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getDeviceId() {
        return this.deviceId;
    }

    public void setDeviceId(Long deviceId) {
        this.deviceId = deviceId;
    }
    
    public Long getRoomId() {
        return this.roomId;
    }

    public void setRoomId(Long roomId) {
        this.roomId = roomId;
    }
    
    public Date getCreateDate() {
        return this.createDate;
    }

    public Device getDevice() {
        return this.device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }
    
    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getToken() {
        return this.token;
    }

    public void setToken(String token) {
        this.token = token;
    }
    
    public String getMac() {
        return this.mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }
    
    public String getDNSName() {
        return this.dnsName;
    }

    public void setDNSName(String dnsName) {
        this.dnsName = dnsName;
    }
}
