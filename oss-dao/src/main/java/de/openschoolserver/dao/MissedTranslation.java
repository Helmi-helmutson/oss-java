package de.openschoolserver.dao;

import java.io.Serializable;
import javax.persistence.*;


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

	private String lang;

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

}