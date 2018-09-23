package de.openschoolserver.dao;

import de.openschoolserver.dao.controller.*;
import de.openschoolserver.dao.tools.*;

import java.io.Serializable;
import java.util.List;
import java.util.regex.*;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name="Printers")
@NamedQueries({
	@NamedQuery(name="Printer.findAll",   query="SELECT p FROM Printer p"),
	@NamedQuery(name="Printer.findAllId", query="SELECT p.id FROM Printer p"),
	@NamedQuery(name="Printer.getByName", query="SELECT p FROM Printer p WHERE p.name = :name")
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

	//bi-directional many-to-one association to HWConf
	@ManyToOne
	@JsonIgnore
	private Device    device;

	//bi-directional many-to-one association to User
	@ManyToOne
	@JsonIgnore
	privateUser creator;

	//bi-directional many-to-many association to Device
	@ManyToMany(mappedBy="availablePrinters",cascade ={CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
	@JsonIgnore
	private List<Device> availableForDevices;

	//bi-directional many-to-many association to Device
	@OneToMany(mappedBy="defaultPrinter")
	@JsonIgnore
	private List<Device> defaultForDevices;

	//bi-directional many-to-many association to Room
	@ManyToMany(mappedBy="availablePrinters", cascade ={CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
	@JsonIgnore
	private List<Room> availableInRooms;

	//bi-directional many-to-many association to Room
	@OneToMany(mappedBy="defaultPrinter")
	@JsonIgnore
	private List<Room> defaultInRooms;

	// Transient variable:

	@Transient
	private String    manufacturer;

	@Transient
	private String    model;

	@Transient
	private String    mac;

	@Transient
	private Long      roomId;

	@Transient
	private boolean   windowsDriver;

	/*
	 * State variables
	 */
	@Transient
	private String    state;

	@Transient
	private boolean   acceptingJobs;

	@Transient
	private int       activeJobs;

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

	public User getCreator() {
		return creator;
	}

	public void setCreator(User creator) {
		this.creator = creator;
	}

	public List<Device> getAvailableForDevices() {
		return availableForDevices;
	}

	public void setAvailableForDevices(List<Device> availableForDevices) {
		this.availableForDevices = availableForDevices;
	}

	public List<Device> getDefaultForDevices() {
		return defaultForDevices;
	}

	public void setDefaultForDevices(List<Device> defaultForDevices) {
		this.defaultForDevices = defaultForDevices;
	}

	public List<Room> getAvailableInRooms() {
		return availableInRooms;
	}

	public void setAvailableInRooms(List<Room> availableInRooms) {
		this.availableInRooms = availableInRooms;
	}

	public List<Room> getDefaultInRooms() {
		return defaultInRooms;
	}

	public void setDefaultInRooms(List<Room> defaultInRooms) {
		this.defaultInRooms = defaultInRooms;
	}
}
