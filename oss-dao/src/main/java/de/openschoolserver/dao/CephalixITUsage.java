/* (c) 2017 Péter Varkoly <peter@varkoly.de> - all rights reserved */
package de.cephalix.api.dao;

import java.io.Serializable;
import javax.persistence.*;
import java.math.BigInteger;


/**
 * The persistent class for the CephalixITUsage database table.
 * 
 */
@Entity
@NamedQuery(name="CephalixITUsage.findAll", query="SELECT c FROM CephalixITUsage c")
public class CephalixITUsage implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name="CEPHALIXITUSAGE_ID_GENERATOR" )
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="CEPHALIXITUSAGE_ID_GENERATOR")
	private Long id;

	private BigInteger counter;

	private String device;

	//bi-directional many-to-one association to CephalixInstitute
	@ManyToOne
	@JoinColumn(name="institute_id")
	private CephalixInstitute cephalixInstitute;

	public CephalixITUsage() {
	}

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public BigInteger getCounter() {
		return this.counter;
	}

	public void setCounter(BigInteger counter) {
		this.counter = counter;
	}

	public String getDevice() {
		return this.device;
	}

	public void setDevice(String device) {
		this.device = device;
	}

	public CephalixInstitute getCephalixInstitute() {
		return this.cephalixInstitute;
	}

	public void setCephalixInstitute(CephalixInstitute cephalixInstitute) {
		this.cephalixInstitute = cephalixInstitute;
	}

}