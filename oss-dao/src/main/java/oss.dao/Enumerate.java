package oss.dao;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the Enumerates database table.
 * 
 */
@Entity
@Table(name="Enumerates")
@NamedQuery(name="Enumerate.findAll", query="SELECT e FROM Enumerate e")
public class Enumerate implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private int id;

	private String name;

	private String value;

	public Enumerate() {
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return this.value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}