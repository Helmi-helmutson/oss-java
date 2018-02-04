/* (c) 2017 Péter Varkoly <peter@varkoly.de> - all rights reserved */
package de.openschoolserver.dao;

import java.io.Serializable;

import javax.persistence.*;
import java.sql.Timestamp;


/**
 * The persistent class for the CephalixJobs database table.
 * 
 */
@Entity
@Table(name="Jobs")
@NamedQueries({
	@NamedQuery(name="Job.findAll",                 query="SELECT c FROM Job c"),
	@NamedQuery(name="Job.findAllByTime",           query="SELECT c FROM Job c WHERE c.startTime > :after AND c.startTime < :befor"),
	@NamedQuery(name="Job.getByDescriptionAndTime", query="SELECT c FROM Job c WHERE c.description LIKE :description AND c.startTime > :after AND c.startTime < :befor"),
	@NamedQuery(name="Job.getByDescription",        query="SELECT c FROM Job c WHERE c.description LIKE :description"),
	@NamedQuery(name="Job.getByExtiCode",           query="SELECT c FROM Job c WHERE c.exitCode = :extiCode"),
	@NamedQuery(name="Job.getByNonExtiCode",        query="SELECT c FROM Job c WHERE c.exitCode != :extiCode")
})
public class Job implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name="CEPHALIXJOBS_ID_GENERATOR" )
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="CEPHALIXJOBS_ID_GENERATOR")
	private Long id;

	private String description;

	private Timestamp startTime;
	
	private Timestamp endTime;

	private Integer exitCode;
	
	@Transient
	private String command;
	
	@Transient
	private boolean promptly;
	
	@Transient
	private String result;

	public Job() {
	}

	public Job(String description, Timestamp startTime, String command, boolean promptly) {
		super();
		this.description = description;
		this.startTime   = startTime;
		this.command     = command;
		this.promptly    = promptly;
	}

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Timestamp getStartTime() {
		return this.startTime;
	}

	public void setStartTime(Timestamp startTime) {
		this.startTime = startTime;
	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public boolean isPromptly() {
		return promptly;
	}

	public void setPromptly(boolean promptly) {
		this.promptly = promptly;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public Integer getExitCode() {
		return exitCode;
	}

	public void setExitCode(Integer exitCode) {
		this.exitCode = exitCode;
	}

	public Timestamp getEndTime() {
		return endTime;
	}

	public void setEndTime(Timestamp endTime) {
		this.endTime = endTime;
	}
}
