package de.openschoolserver.dao;

import de.openschoolserver.dao.controller.*;
import de.openschoolserver.dao.tools.*;

import java.io.Serializable;
import java.util.regex.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name="Printers")
@NamedQueries({
	@NamedQuery(name="Printer.findAll",   query="SELECT p FROM Printer p"),
	@NamedQuery(name="Printer.findAllId", query="SELECT p.id FROM Printer p"),
	@NamedQuery(name="Printer.getPrinterByName", query="SELECT p FROM Printer p WHERE p.name = :name")
})
@SequenceGenerator(name="seq", initialValue=1, allocationSize=100)
public class Printer implements Serializable  {
	private static final long serialVersionUID = 1L;

	/*
	 * Variables required for creating a printer
	 */
	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="seq")
	private Long      id;


	private String    name;
	private String    manufacturer;
	private String    model;
	private String    mac;
	private Long      roomId;
	private boolean   windowsDriver;

	/*
	 * State variables
	 */
	private String    state;
	private boolean   acceptingJobs;
	private int       activeJobs;

	//bi-directional many-to-one association to HWConf
	@ManyToOne
	@JsonIgnore
	private Device    device;

	//bi-directional many-to-one association to User
	@ManyToOne
	@JsonIgnore
	private User creator;

	public Printer() {
	}

	public Printer(String name, Session session) {
		this.name = name;
		DeviceController deviceController = new DeviceController(session);
		this.device = deviceController.getByName(name);
		this.device = deviceController.getByName(name);
		String[] program = new String[3];
		StringBuffer reply  = new StringBuffer();
		StringBuffer stderr = new StringBuffer();
		program[0] = "/usr/bin/ipptool";
		program[1] = "ipp://localhost/printers/" +name;
		program[2] = "get-printers.test";
		OSSShellTools.exec(program, reply, stderr, null);
		String[] lines = reply.toString().split(deviceController.getNl());
		Pattern pattern = Pattern.compile("\\S+");
		if( lines.length > 2) {
			Matcher matcher = pattern.matcher(lines[2]);
			if( matcher.find() ) {
				this.state = matcher.group(0);
			}
			if( matcher.find() ) {
				this.acceptingJobs = matcher.group(1).equals("true");
			}
		}
		program[2] = "get-jobs.test";
		OSSShellTools.exec(program, reply, stderr, null);
		lines = reply.toString().split(deviceController.getNl());
		this.activeJobs = lines.length-2;
		this.windowsDriver = false;
	}

	public Printer(String name, DeviceController deviceController) {
		this.name = name;
		this.device = deviceController.getByName(name);
		if( device != null ) {
			this.setId(device.getId());
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public boolean isAcceptingJobs() {
		return acceptingJobs;
	}

	public void setAcceptingJobs(boolean acceptingJobs) {
		this.acceptingJobs = acceptingJobs;
	}

	public int getActiveJobs() {
		return activeJobs;
	}

	public void setActiveJobs(int activeJobs) {
		this.activeJobs = activeJobs;
	}

	public Device getDevice() {
		return device;
	}

	public void setDevice(Device device) {
		this.device = device;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getManufacturer() {
		return manufacturer;
	}

	public void setManufacturer(String manufacturer) {
		this.manufacturer = manufacturer;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getMac() {
		return mac;
	}

	public void setMac(String mac) {
		this.mac = mac;
	}

	public Long getRoomId() {
		return roomId;
	}

	public void setRoomId(Long roomId) {
		this.roomId = roomId;
	}

	public boolean isWindowsDriver() {
		return windowsDriver;
	}

	public void setWindowsDriver(boolean windowsDriver) {
		this.windowsDriver = windowsDriver;
	}
}
