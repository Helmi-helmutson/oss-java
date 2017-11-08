/* (c) 2017 Péter Varkoly <peter@varkoly.de> - all rights reserved */
package de.openschoolserver.dao;

import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.persistence.*;


/**
 * The persistent class for the Acls database table.
 * 
 */
@Entity
@Table(name="Acls")
@NamedQueries({
	@NamedQuery(name="Acl.findAll", query="SELECT a FROM Acl a"),
	@NamedQuery(name="Acl.findByRole", query="SELECT a FROM Acl a where a.role = :role "),
	@NamedQuery(name="Acl.checkByRole", query="SELECT a FROM Acl a where a.role = :role AND a.acl = :acl"),
})
@SequenceGenerator(name="seq", initialValue=1, allocationSize=100)
public class Acl implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="seq")
	private long id;

	private String acl;
	
	private String role;
	
	//bi-directional many-to-one association to User
	@ManyToOne
	@JsonIgnore
	private User user;
	
    @Column(name = "user_id", insertable = false, updatable = false)
    private Long userId;

    //bi-directional many-to-one association to Group
	@ManyToOne
	@JsonIgnore
	private Group group;
	
    @Column(name = "group_id", insertable = false, updatable = false)
    private Long groupId;

    //bi-directional many-to-one association to User
	@ManyToOne
	@JsonIgnore
	private User creator;
		
	@Override
    public boolean equals(Object obj) {
                if (obj instanceof Acl && obj !=null) {
                        return getId() == ((Acl)obj).getId();
                }
                return super.equals(obj);
    }

	public Acl() {
	}

	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getAcl() {
		return this.acl;
	}

	public void setAcl(String acl) {
		this.acl = acl;
	}
	
	public String getRole() {
		return this.role;
	}

	public void setRole(String role) {
		this.role = role;
	}
	
	public void setUser(User user) {
		this.user = user;
	}
	
	public User getUser(){
		return this.user;
	}
	
	public void setGroup(Group group){
		this.group = group;
	}
	
	public Group getGroup(){
		return this.group;
	}

	public User getCreator() {
		return creator;
	}

	public void setCreator(User creator) {
		this.creator = creator;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getGroupId() {
		return groupId;
	}

	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}

	
}
