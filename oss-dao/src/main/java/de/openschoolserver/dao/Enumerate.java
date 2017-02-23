/* (c) 2017 Péter Varkoly <peter@varkoly.de> - all rights reserved */
package de.openschoolserver.dao;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the Enumerates database table.
 * 
 */
@Entity
@Table(name="Enumerates")
@NamedQuery(name="Enumerate.findAll", query="SELECT e FROM Enumerate e")
@SequenceGenerator(name="seq", initialValue=1, allocationSize=100)
public class Enumerate implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="seq")
	private long id;

	private String name;

	private String value;

        @Override
        public boolean equals(Object obj) {
                if (obj instanceof Enumerate && obj !=null) {
                        return getId() == ((Enumerate)obj).getId();
                }
                return super.equals(obj);
        }

	public Enumerate() {
	}

	public long getId() {
		return this.id;
	}

	public void setId(long id) {
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
