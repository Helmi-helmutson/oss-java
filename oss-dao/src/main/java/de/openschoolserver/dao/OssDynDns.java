package de.openschoolserver.dao;

import java.io.Serializable;
import java.lang.Boolean;
import java.lang.Integer;
import java.lang.Long;
import java.lang.String;
import java.util.Date;

import javax.persistence.*;

/**
 * Entity implementation class for Entity: OssDynDns
 *
 */
@Entity
public class OssDynDns implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Id
	@SequenceGenerator(name="OSSDYNDNS_ID_GENERATOR", sequenceName="SEQ")
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="OSSDYNDNS_ID_GENERATOR")
	private Long id;
	
	private String hostname;
	
	private String domain;
	
	private String ip;
	
	private Integer port;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date ts;
	
	@Convert(converter=BooleanToStringConverter.class)
	private Boolean ro;
	
	@OneToOne
	private Regcode regcode;

	public OssDynDns() {
	}

	public OssDynDns(String ip, Regcode regcode) {
		this.ip      = ip;
		this.regcode = regcode;
		this.ro      = false;
		this.port    = 22;
		this.domain  = "cephalix.de";
		regcode.setOssDynDns(this);
	}   

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof OssDynDns && obj !=null) {
			return getId() == ((OssDynDns)obj).getId();
		}
		return super.equals(obj);
	}

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}   
	public String getHostname() {
		return this.hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}   
	public String getDomain() {
		return this.domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}   
	public String getIp() {
		return this.ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}   
	public Integer getPort() {
		return this.port;
	}

	public void setPort(Integer port) {
		this.port = port;
	}   
	public Date getTs() {
		return this.ts;
	}

	public void setTs(Date ts) {
		this.ts = ts;
	}   
	public Boolean getRo() {
		return this.ro;
	}

	public void setRo(Boolean ro) {
		this.ro = ro;
	}   
	public Regcode getRegcode() {
		return this.regcode;
	}

	public void setRegcode(Regcode regcode) {
		this.regcode = regcode;
	}
   
}
