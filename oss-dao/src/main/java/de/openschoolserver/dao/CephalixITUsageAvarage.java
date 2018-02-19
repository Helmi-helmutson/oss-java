/* (c) 2017 Péter Varkoly <peter@varkoly.de> - all rights reserved */
package de.cephalix.api.dao;

import java.io.Serializable;
import javax.persistence.*;
import java.sql.Timestamp;
import java.math.BigInteger;


/**
 * The persistent class for the CephalixITUsageAvarage database table.
 * 
 */
@Entity
@NamedQuery(name="CephalixITUsageAvarage.findAll", query="SELECT c FROM CephalixITUsageAvarage c")
public class CephalixITUsageAvarage implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name="CEPHALIXITUSAGEAVARAGE_ID_GENERATOR" )
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="CEPHALIXITUSAGEAVARAGE_ID_GENERATOR")
	private Long id;

	private BigInteger avarage;

	private BigInteger counter;

	private BigInteger counter0;

	private String device;

	private Timestamp time;

	private Timestamp time0;

	//bi-directional many-to-one association to CephalixInstitute
	@ManyToOne
	@JoinColumn(name="institute_id")
	private CephalixInstitute cephalixInstitute;

	public CephalixITUsageAvarage() {
	}

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public BigInteger getAvarage() {
		return this.avarage;
	}

	public void setAvarage(BigInteger avarage) {
		this.avarage = avarage;
	}

	public BigInteger getCounter() {
		return this.counter;
	}

	public void setCounter(BigInteger counter) {
		this.counter = counter;
	}

	public BigInteger getCounter0() {
		return this.counter0;
	}

	public void setCounter0(BigInteger counter0) {
		this.counter0 = counter0;
	}

	public String getDevice() {
		return this.device;
	}

	public void setDevice(String device) {
		this.device = device;
	}

	public Timestamp getTime() {
		return this.time;
	}

	public void setTime(Timestamp time) {
		this.time = time;
	}

	public Timestamp getTime0() {
		return this.time0;
	}

	public void setTime0(Timestamp time0) {
		this.time0 = time0;
	}

	public CephalixInstitute getCephalixInstitute() {
		return this.cephalixInstitute;
	}

	public void setCephalixInstitute(CephalixInstitute cephalixInstitute) {
		this.cephalixInstitute = cephalixInstitute;
	}

}