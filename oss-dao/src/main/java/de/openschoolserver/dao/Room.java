/* (c) 2017 Péter Varkoly <peter@varkoly.de> - all rights reserved */
package de.openschoolserver.dao;

import java.io.Serializable;
import javax.persistence.*;
import java.util.List;


/**
 * The persistent class for the Rooms database table.
 * 
 */
@Entity
@Table(name="Rooms")
@NamedQueries ({
  @NamedQuery(name="Room.findAll", query="SELECT r FROM Room r"),
  @NamedQuery(name="Room.getByName", query="SELECT r FROM Room r WHERE r.name = :name"),
  @NamedQuery(name="Room.getByDescription", query="SELECT r FROM Room r WHERE r.description = :description"),
  @NamedQuery(name="Room.getByType", query="SELECT r FROM Room r WHERE r.roomType = :type"),
  @NamedQuery(name="Room.getDeviceCount", query="SELECT COUNT( d ) FROM  Device d WHERE d.room_id = :id"),
  @NamedQuery(name="Room.getConfig",  query="SELECT c.value FROM RoomConfig  WHERE user_id = :user_id AND key = :key" ),
  @NamedQuery(name="Room.getMConfig", query="SELECT c.value FROM RoomMConfig WHERE user_id = :user_id AND key = :key" )
})
public class Room implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private long id;

	private String name;

	private int columns;

	private String description;

	private int netMask;

	private String startIP;

	private String roomType;
	
	private int rows;

        //bi-directional many-to-one association to RoomMConfig
        @OneToMany(mappedBy="room", cascade=CascadeType.REMOVE)
        private List<RoomMConfig> RoomMConfig;

        //bi-directional many-to-one association to RoomConfig
        @OneToMany(mappedBy="room", cascade=CascadeType.REMOVE)
        private List<RoomConfig> RoomConfig;

	//bi-directional many-to-one association to AccessInRoom
	@OneToMany(mappedBy="room")
	private List<AccessInRoom> accessInRooms;

	//bi-directional many-to-one association to Device
	@OneToMany(mappedBy="room")
	private List<Device> devices;

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
	private List<Device> availablePrinters;

	//bi-directional many-to-one association to Device
	@ManyToOne
	@JoinTable(
		name="DefaultPrinter"
		, joinColumns={
			@JoinColumn(name="room_id")
			}
		, inverseJoinColumns={
			@JoinColumn(name="printer_id")
			}
		)
	private Device defaultPrinter;

	//bi-directional many-to-one association to HWConf
	@ManyToOne
	private HWConf hwconf;

	//bi-directional many-to-one association to Test
	@OneToMany(mappedBy="room")
	private List<Test> tests;

	@Transient
	private String network;
	
	public Room() {
		this.network = "";
	}

	public long getId() {
		return this.id;
	}

	public void setId(long id) {
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
		return this.roomType;
	}

	public void setRoomType(String roomtype) {
		this.roomType = roomtype;
	}

	public int getRows() {
		return this.rows;
	}

	public void setRows(int rows) {
		this.rows = rows;
	}

	public List<AccessInRoom> getAccessInRooms() {
		return this.accessInRooms;
	}

	public List<Device> getDevices() {
		return this.devices;
	}

	public void setDevices(List<Device> devices) {
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

	public List<Device> getAvailablePrinters() {
		return this.availablePrinters;
	}

	public void setAvailablePrinters(List<Device> availablePrinters) {
		this.availablePrinters = availablePrinters;
	}

	public Device getDefaultPrinter() {
		return this.defaultPrinter;
	}

	public void setDefaultPrinter(Device defaultPrinter) {
		this.defaultPrinter = defaultPrinter;
	}

	public HWConf getHwconf() {
		return this.hwconf;
	}

	public void setHwconf(HWConf hwconf) {
		this.hwconf = hwconf;
	}

	public List<Test> getTests() {
		return this.tests;
	}

	public void setTests(List<Test> tests) {
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
	
	public String getNetwork() {
		return this.network;
	}

	public void setNetwork(String network) {
		this.network = network;
	}



}
