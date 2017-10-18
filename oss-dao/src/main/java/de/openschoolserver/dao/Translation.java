package de.openschoolserver.dao;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the Translations database table.
 * 
 */
@Entity
@Table(name="Translations")
@NamedQueries({
	@NamedQuery(name="Translation.findAll", query="SELECT t FROM Translation t"),
	@NamedQuery(name="Translation.find",	query="SELECT t FROM Translation t WHERE t.lang = :lang AND t.string = :string")
})
public class Translation implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name="TRANSLATIONS_ID_GENERATOR" )
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="TRANSLATIONS_ID_GENERATOR")
	private Long id;

	private String lang;

	private String string;

	private String value;

	public Translation() {
	}
	
	public Translation(String lang, String string, String value) {
		this.lang   = lang;
		this.string = string;
		this.value  = value;
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

	public String getValue() {
		return this.value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
