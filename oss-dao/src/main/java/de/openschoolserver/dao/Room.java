package de.openschoolserver.dao;

import java.io.Serializable;
import javax.persistence.*;
import java.util.Set;


/**
 * The persistent class for the Rooms database table.
 * 
 */
@Entity
@Table(name="Rooms")
@NamedQueries ({
  @NamedQuery(name="Room.findAll", query="SELECT r FROM Room r"),
  @NamedQuery(name="Room.getRoomByName", query="SELECT r FROM Room r WHERE r.name = :name"),
  @NamedQuery(name="Room.getRoomByDescription", query="SELECT r FROM Room r WHERE r.description = :description"),
  @NamedQuery(name="Room.getRoomByType", query="SELECT r FROM Room r WHERE r.type = :type")
})
public class Room implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private int id;

	private String name;

	private int columns;

	private String description;

	private int netMask;

	private String startIP;

	private String type;
	
	private int rows;

	//bi-directional many-to-one association to AccessInRoom
	@OneToMany(mappedBy="room")
	private Set<AccessInRoom> accessInRooms;

	//bi-directional many-to-one association to Device
	@OneToMany(mappedBy="room")
	private Set<Device> devices;

	//bi-directional many-to-many association to Device
	@ManyToMany
	@JoinTable(
		name="AvailablePrinters"
		, joinColumns={
			@JoinColumn(name="room_id")
			}
		, inverseJoinColumns={
			@JoinColumn(name="printer_id")
			}
		)
	private Set<Device> availablePrinters;

	//bi-directional many-to-many association to Device
	@ManyToMany
	@JoinTable(
		name="DefaultPrinters"
		, joinColumns={
			@JoinColumn(name="room_id")
			}
		, inverseJoinColumns={
			@JoinColumn(name="printer_id")
			}
		)
	private Set<Device> defaultPrinter;

	//bi-directional many-to-one association to HWConf
	@ManyToOne
	private HWConf hwconf;

	//bi-directional many-to-one association to Test
	@OneToMany(mappedBy="room")
	private Set<Test> tests;

	public Room() {
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getColumns() {
		return this.columns;
	}

	public void setColumns(int columns) {
		this.columns = columns;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getNetMask() {
		return this.netMask;
	}

	public void setNetMask(int netMask) {
		this.netMask = netMask;
	}

	public String getStartIP() {
		return this.startIP;
	}

	public void setStartIP(String startIP) {
		this.startIP = startIP;
	}

	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getRows() {
		return this.rows;
	}

	public void setRows(int rows) {
		this.rows = rows;
	}

	public Set<AccessInRoom> getAccessInRooms() {
		return this.accessInRooms;
	}

	public void setAccessInRooms(Set<AccessInRoom> accessInRooms) {
		this.accessInRooms = accessInRooms;
	}

	public AccessInRoom addAccessInRoom(AccessInRoom accessInRoom) {
		getAccessInRooms().add(accessInRoom);
		accessInRoom.setRoom(this);

		return accessInRoom;
	}

	public AccessInRoom removeAccessInRoom(AccessInRoom accessInRoom) {
		getAccessInRooms().remove(accessInRoom);
		accessInRoom.setRoom(null);

		return accessInRoom;
	}

	public Set<Device> getDevices() {
		return this.devices;
	}

	public void setDevices(Set<Device> devices) {
		this.devices = devices;
	}

	public Device addDevice(Device device) {
		getDevices().add(device);
		device.setRoom(this);

		return device;
	}

	public Device removeDevice(Device device) {
		getDevices().remove(device);
		device.setRoom(null);

		return device;
	}

	public Set<Device> getAvailablePrinters() {
		return this.availablePrinters;
	}

	public void setAvailablePrinters(Set<Device> availablePrinters) {
		this.availablePrinters = availablePrinters;
	}

	public Set<Device> getDefaultPrinter() {
		return this.defaultPrinter;
	}

	public void setDefaultPrinter(Set<Device> defaultPrinter) {
		this.defaultPrinter = defaultPrinter;
	}

	public HWConf getHwconf() {
		return this.hwconf;
	}

	public void setHwconf(HWConf hwconf) {
		this.hwconf = hwconf;
	}

	public Set<Test> getTests() {
		return this.tests;
	}

	public void setTests(Set<Test> tests) {
		this.tests = tests;
	}

	public Test addTest(Test test) {
		getTests().add(test);
		test.setRoom(this);

		return test;
	}

	public Test removeTest(Test test) {
		getTests().remove(test);
		test.setRoom(null);

		return test;
	}

}