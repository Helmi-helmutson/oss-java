/* (c) 2017 Péter Varkoly <peter@varkoly.de> - all rights reserved */
package de.openschoolserver.dao;

import java.io.Serializable;

import javax.persistence.*;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;


/**
 * The persistent class for the Groups database table.
 * 
 */
@Entity
@Table(name="Groups")
@NamedQueries({
	@NamedQuery(name="Group.findAll", query="SELECT g FROM Group g"),
	@NamedQuery(name="Group.getByName",  query="SELECT g FROM Group g WHERE g.name = :name OR g.description = :name"),
	@NamedQuery(name="Group.getByType",  query="SELECT g FROM Group g WHERE g.groupType = :groupType"),
	@NamedQuery(name="Group.search", query="SELECT g FROM Group g WHERE g.name LIKE :search OR g.description LIKE :search OR g.groupType LIKE :search"),
})
@SequenceGenerator(name="seq", initialValue=1, allocationSize=100)
public class Group implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="seq")
	private long id;

	@Column(name = "name", updatable = false)
	private String name;

	private String description;

	private String groupType;

	//bi-directional many-to-many association to Category
	@ManyToMany(mappedBy="groups")
	private List<Category> categories;

	//bi-directional many-to-one association to Acls
	@OneToMany(mappedBy="group", cascade=CascadeType.ALL, orphanRemoval=true)
	private List<Acl> acls;
	
	//bi-directional many-to-many association to User
	@ManyToMany(mappedBy="groups")
	@JsonIgnore
	private List<User> users;

	public Group() {
		this.name = "";
		this.description = "";
		this.groupType = "";
	}

	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Group && obj !=null) {
			return getId() == ((Group)obj).getId();
		}
		return super.equals(obj);
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
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

	public void setGroupType(String groupType) {
		this.groupType = groupType;
	}

	public List<User> getUsers() {
		return this.users;
	}

	public void setUsers(List<User> users) {
		this.users = users;
	}
	public List<Acl> getAcls() {
		return this.acls;
	}

	public void setAcls(List<Acl> acls) {
		this.acls = acls;
	}

	public void addAcl(Acl acl) {
		getAcls().add(acl);
		acl.setGroup(this);	
	}

	public void removeAcl(Acl acl) {
		getAcls().remove(acl);
		acl.setGroup(null);
	}

        public List<Category> getCategories() {
                return this.categories;
        }

        public void setCategories(List<Category> categories) {
                this.categories = categories;
        }
}
