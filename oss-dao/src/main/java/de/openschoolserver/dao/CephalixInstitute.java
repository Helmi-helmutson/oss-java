/* (c) 2017 Péter Varkoly <peter@varkoly.de> - all rights reserved */
package de.openschoolserver.dao;

import java.io.Serializable;
import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Date;
import java.util.List;

/**
 * The persistent class for the CephalixInstitutes database table.
 * 
 */
@Entity
@Table(name="CephalixInstitutes")
@NamedQuery(name="CephalixInstitute.findAll", query="SELECT c FROM CephalixInstitute c")
public class CephalixInstitute implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name="CEPHALIXINSTITUTES_ID_GENERATOR" )
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="CEPHALIXINSTITUTES_ID_GENERATOR")
	private Long id;

	private String adminPW;

	private String anonDhcp;

	private String cephalixPW;

	private String cn;

	private String domain;

	private String firstRoom;

	private String gwTrNet;

	private String ipAdmin;

	private String ipBackup;

	private String ipMail;

	private String ipPrint;

	private String ipProxy;

	private String ipTrNet;

	private String ipVPN;

	private String locality;

	private String name;

	private String netmask;

	private String network;

	private String nmServerNet;

	private String nmTrNet;

	private String state;

	private String type;
	
	@Convert(converter=BooleanToStringConverter.class)
	private boolean deleted;
	
	//bi-directional many-to-one association to Room
	@ManyToOne
	@JsonIgnore
	private CephalixCustomer cephalixCustomer;
	
    //bi-directional many-to-one association to Device
    @OneToMany(mappedBy="cephalixInstitute", cascade=CascadeType.ALL, orphanRemoval=true)
    private List<CephalixRegcode> cephalixRegcodes;
	
    @Temporal(TemporalType.TIMESTAMP)
	private Date recDate;

	//bi-directional many-to-one association to CephalixITUsage
	@OneToMany(mappedBy="cephalixInstitute")
	@JsonIgnore
	private List<CephalixITUsage> cephalixItusages;

	//bi-directional many-to-one association to CephalixITUsageAvarage
	@OneToMany(mappedBy="cephalixInstitute")
	@JsonIgnore
	private List<CephalixITUsageAvarage> cephalixItusageAvarages;

	//bi-directional many-to-one association to CephalixMapping
	@OneToMany(mappedBy="cephalixInstitute")
	@JsonIgnore
	private List<CephalixMapping> cephalixMappings;

	public CephalixInstitute() {
	}

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getAdminPW() {
		return this.adminPW;
	}

	public void setAdminPW(String adminPW) {
		this.adminPW = adminPW;
	}

	public String getAnonDhcp() {
		return this.anonDhcp;
	}

	public void setAnonDhcp(String anonDhcp) {
		this.anonDhcp = anonDhcp;
	}

	public String getCephalixPW() {
		return this.cephalixPW;
	}

	public void setCephalixPW(String cephalixPW) {
		this.cephalixPW = cephalixPW;
	}

	public String getCn() {
		return this.cn;
	}

	public void setCn(String cn) {
		this.cn = cn;
	}

	public String getDomain() {
		return this.domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getFirstRoom() {
		return this.firstRoom;
	}

	public void setFirstRoom(String firstRoom) {
		this.firstRoom = firstRoom;
	}

	public String getGwTrNet() {
		return this.gwTrNet;
	}

	public void setGwTrNet(String gwTrNet) {
		this.gwTrNet = gwTrNet;
	}

	public String getIpAdmin() {
		return this.ipAdmin;
	}

	public void setIpAdmin(String ipAdmin) {
		this.ipAdmin = ipAdmin;
	}

	public String getIpBackup() {
		return this.ipBackup;
	}

	public void setIpBackup(String ipBackup) {
		this.ipBackup = ipBackup;
	}

	public String getIpMail() {
		return this.ipMail;
	}

	public void setIpMail(String ipMail) {
		this.ipMail = ipMail;
	}

	public String getIpPrint() {
		return this.ipPrint;
	}

	public void setIpPrint(String ipPrint) {
		this.ipPrint = ipPrint;
	}

	public String getIpProxy() {
		return this.ipProxy;
	}

	public void setIpProxy(String ipProxy) {
		this.ipProxy = ipProxy;
	}

	public String getIpTrNet() {
		return this.ipTrNet;
	}

	public void setIpTrNet(String ipTrNet) {
		this.ipTrNet = ipTrNet;
	}

	public String getIpVPN() {
		return this.ipVPN;
	}

	public void setIpVPN(String ipVPN) {
		this.ipVPN = ipVPN;
	}

	public String getLocality() {
		return this.locality;
	}

	public void setLocality(String locality) {
		this.locality = locality;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNetmask() {
		return this.netmask;
	}

	public void setNetmask(String netmask) {
		this.netmask = netmask;
	}

	public String getNetwork() {
		return this.network;
	}

	public void setNetwork(String network) {
		this.network = network;
	}

	public String getNmServerNet() {
		return this.nmServerNet;
	}

	public void setNmServerNet(String nmServerNet) {
		this.nmServerNet = nmServerNet;
	}

	public String getNmTrNet() {
		return this.nmTrNet;
	}

	public void setNmTrNet(String nmTrNet) {
		this.nmTrNet = nmTrNet;
	}

	public String getState() {
		return this.state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	public boolean getDeleted() {
		return this.deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public List<CephalixITUsage> getCephalixItusages() {
		return this.cephalixItusages;
	}

	public void setCephalixItusages(List<CephalixITUsage> cephalixItusages) {
		this.cephalixItusages = cephalixItusages;
	}

	public CephalixITUsage addCephalixItusage(CephalixITUsage cephalixItusage) {
		getCephalixItusages().add(cephalixItusage);
		cephalixItusage.setCephalixInstitute(this);

		return cephalixItusage;
	}

	public CephalixITUsage removeCephalixItusage(CephalixITUsage cephalixItusage) {
		getCephalixItusages().remove(cephalixItusage);
		cephalixItusage.setCephalixInstitute(null);

		return cephalixItusage;
	}

	public List<CephalixITUsageAvarage> getCephalixItusageAvarages() {
		return this.cephalixItusageAvarages;
	}

	public void setCephalixItusageAvarages(List<CephalixITUsageAvarage> cephalixItusageAvarages) {
		this.cephalixItusageAvarages = cephalixItusageAvarages;
	}

	public CephalixITUsageAvarage addCephalixItusageAvarage(CephalixITUsageAvarage cephalixItusageAvarage) {
		getCephalixItusageAvarages().add(cephalixItusageAvarage);
		cephalixItusageAvarage.setCephalixInstitute(this);

		return cephalixItusageAvarage;
	}

	public CephalixITUsageAvarage removeCephalixItusageAvarage(CephalixITUsageAvarage cephalixItusageAvarage) {
		getCephalixItusageAvarages().remove(cephalixItusageAvarage);
		cephalixItusageAvarage.setCephalixInstitute(null);

		return cephalixItusageAvarage;
	}

	public List<CephalixMapping> getCephalixMappings() {
		return this.cephalixMappings;
	}

	public void setCephalixMappings(List<CephalixMapping> cephalixMappings) {
		this.cephalixMappings = cephalixMappings;
	}

	public CephalixMapping addCephalixMapping(CephalixMapping cephalixMapping) {
		getCephalixMappings().add(cephalixMapping);
		cephalixMapping.setCephalixInstitute(this);

		return cephalixMapping;
	}

	public CephalixMapping removeCephalixMapping(CephalixMapping cephalixMapping) {
		getCephalixMappings().remove(cephalixMapping);
		cephalixMapping.setCephalixInstitute(null);

		return cephalixMapping;
	}

	public Date getRecdate() {
		return recDate;
	}

	public void setRecdate(Date date) {
		this.recDate = date;
	}
	public List<CephalixRegcode> getRegcodes() {
		return cephalixRegcodes;
	}

	public void setRegcodes(List<CephalixRegcode> cephalixRegcodes) {
		this.cephalixRegcodes = cephalixRegcodes;
	}

}