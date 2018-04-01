package de.openschoolserver.dao;

import java.io.Serializable;
import java.lang.Long;
import java.lang.String;
import javax.persistence.*;

/**
 * Entity implementation class for Entity: Translation
 *
 */
@Entity
@Table(name="Translations")
@NamedQueries({
	@NamedQuery(name="Translation.findAll",   query="SELECT t FROM Translation t"),
	@NamedQuery(name="Translation.findByLang",query="SELECT t FROM Translation t WHERE t.lang = :lang"),
	@NamedQuery(name="Translation.find",	  query="SELECT t FROM Translation t WHERE t.lang = :lang AND t.string = :string")
})
public class Translation implements Serializable {

	
	private Long id;
	private String lang;
	private String string;
	private String value;
	private static final long serialVersionUID = 1L;

	public Translation() {
		super();
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
