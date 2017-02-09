/* (c) 2017 Péter Varkoly <peter@varkoly.de> - all rights reserved */
package de.openschoolserver.dao;

import java.io.Serializable;

import javax.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;


/**
 * The persistent class for the Partitions database table.
 * 
 */
@Entity
@Table(name="Partitions")
@NamedQueries({
	@NamedQuery(name="Partition.findAll", query="SELECT p FROM Partition p"),
	@NamedQuery(name="Partition.getPartitionByName", query="SELECT p FROM Partition p WHERE p.hwconf.id = :hwconfId AND p.name = :name")
})
public class Partition implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private long id;

	private String description;

	private String format;

	private String joinType;

	private String name;

	@Column(name="OS")
	private String os;

	private String tool;

	//bi-directional many-to-one association to HWConf
	@ManyToOne
	@JsonIgnore
	private HWConf hwconf;

	public Partition() {
	}

	public Partition(String name) {
		this.name = name;
	}

	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Partition && obj !=null) {
			return getId() == ((Partition)obj).getId();
		}
		return super.equals(obj);
	}
	

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getFormat() {
		return this.format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public String getJoinType() {
		return this.joinType;
	}

	public void setJoinType(String joinType) {
		this.joinType = joinType;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getOs() {
		return this.os;
	}

	public void setOs(String os) {
		this.os = os;
	}

	public String getTool() {
		return this.tool;
	}

	public void setTool(String tool) {
		this.tool = tool;
	}

	public HWConf getHwconf() {
		return this.hwconf;
	}

	public void setHwconf(HWConf hwconf) {
		this.hwconf = hwconf;
	}

}
