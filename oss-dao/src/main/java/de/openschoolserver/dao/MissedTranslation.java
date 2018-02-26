/* (c) 2017 Péter Varkoly <peter@varkoly.de> - all rights reserved */
package de.openschoolserver.dao;

import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.Size;

/**
 * The persistent class for the MissedTranslations database table.
 * 
 */
@Entity
@Table(name="MissedTranslations")
@NamedQueries({
	@NamedQuery(name="MissedTranslation.findAll", 	query="SELECT m FROM MissedTranslation m"),
	@NamedQuery(name="MissedTranslation.findByLang",query="SELECT t FROM MissedTranslation t WHERE t.lang = :lang"),
	@NamedQuery(name="MissedTranslation.find",		query="SELECT t FROM MissedTranslation t WHERE t.lang = :lang AND t.string = :string")
})
public class MissedTranslation implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name="MISSEDTRANSLATIONS_ID_GENERATOR" )
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="MISSEDTRANSLATIONS_ID_GENERATOR")
	private Long id;

	@Size(max=2, message="Language name must not be longer then 2 characters.")
	private String lang;

	@Size(max=250, message="String must not be longer then 250 characters.")
	private String string;

	public MissedTranslation() {
	}

	public MissedTranslation(String lang, String string) {
		this.lang   = lang;
		this.string = string;
	}

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getLang() {
		return this.lang;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}

	public String getString() {
		return this.string;
	}

	public void setString(String string) {
		this.string = string;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MissedTranslation other = (MissedTranslation) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

}
