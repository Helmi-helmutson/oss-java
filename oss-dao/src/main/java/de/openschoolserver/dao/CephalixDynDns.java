package de.cephalix.api.dao;

import java.io.Serializable;
import java.lang.Boolean;
import java.lang.Integer;
import java.lang.Long;
import java.lang.String;
import java.util.Date;

import javax.persistence.*;

import de.openschoolserver.dao.BooleanToStringConverter;

/**
 * Entity implementation class for Entity: CephalixDynDns
 *
 */
@Entity
public class CephalixDynDns implements Serializable {

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
	private CephalixRegcode cephalixRegcode;

	public CephalixDynDns() {
	}

	public CephalixDynDns(String ip, CephalixRegcode cephalixRegcode) {
		this.ip      = ip;
		this.cephalixRegcode = cephalixRegcode;
		this.ro      = false;
		this.port    = 22;
		this.domain  = "cephalix.de";
		cephalixRegcode.setOssDynDns(this);
	}   

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof CephalixDynDns && obj !=null) {
			return getId() == ((CephalixDynDns)obj).getId();
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
	public CephalixRegcode getRegcode() {
		return this.cephalixRegcode;
	}

	public void setRegcode(CephalixRegcode cephalixRegcode) {
		this.cephalixRegcode = cephalixRegcode;
	}
   
}
