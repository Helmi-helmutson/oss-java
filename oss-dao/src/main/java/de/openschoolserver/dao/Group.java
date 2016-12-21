package de.openschoolserver.dao;

import java.io.Serializable;
import javax.persistence.*;
import java.util.List;


/**
 * The persistent class for the Groups database table.
 * 
 */
@Entity
@Table(name="Groups")
@NamedQuery(name="Group.findAll", query="SELECT g FROM Group g")
public class Group implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private int id;

	private String cn;

	private String description;

	private String groupType;

	//bi-directional many-to-many association to User
	@ManyToMany(mappedBy="groups")
	private List<User> users;

	public Group() {
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getCn() {
		return this.cn;
	}

	public void setCn(String cn) {
		this.cn = cn;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getGroupType() {
		return this.groupType;
	}

	public void setGroupType(String grouptype) {
		this.groupType = grouptype;
	}

	public List<User> getUsers() {
		return this.users;
	}

	public void setUsers(List<User> users) {
		this.users = users;
	}

}
