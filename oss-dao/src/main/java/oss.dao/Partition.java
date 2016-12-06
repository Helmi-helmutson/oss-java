package oss.dao;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the Partitions database table.
 * 
 */
@Entity
@Table(name="Partitions")
@NamedQuery(name="Partition.findAll", query="SELECT p FROM Partition p")
public class Partition implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private int id;

	private String description;

	private String format;

	private String join;

	private String name;

	@Column(name="OS")
	private String os;

	private String tool;

	//bi-directional many-to-one association to HWConf
	@ManyToOne
	private HWConf hwconf;

	public Partition() {
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
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

	public String getJoin() {
		return this.join;
	}

	public void setJoin(String join) {
		this.join = join;
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