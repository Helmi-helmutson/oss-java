/* (c) 2017 Péter Varkoly <peter@varkoly.de> - all rights reserved */
/* (c) 2016 EXTIS GmbH - all rights reserved */
package de.openschoolserver.dao;

import java.security.Principal;
import java.util.Date;

import javax.persistence.*;


import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "Session")
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
	
	@OneToOne
	private Device device;
		
	@Column(name = "user_id")
	private long userId;
	   
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;
	
	@OneToOne
	private Room room;
	
	@Column(name = "IP")
	private String IP;
	
    @Column(name = "Token")
	private String token;
	
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
		return this.IP;
	}

	public void setIP(String IP) {
		this.IP = IP;
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
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public Device getDevice() {
		return this.device;
	}

	public void setDevice(Device device) {
		this.device = device;
	}
	
	
	    public int getId() {
	        return id;
	    }

	    public void setId(int id) {
	        this.id = id;
	    }

	 
	    public String getToken() {
	        return token;
	    }

	    public void setToken(String token) {
	        this.token = token;
	    }

	    public Session(String token, long userid, String password, String ip) {
	        this.userId = userid;
	        this.password = password;
	        this.token = token;
	       
	    }

	    public Session() {
	        // empty constructor
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
}
