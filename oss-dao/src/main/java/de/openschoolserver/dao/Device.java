/* (c) 2017 P��ter Varkoly <peter@varkoly.de> - all rights reserved */
package de.openschoolserver.dao;

import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.persistence.*;
import java.util.List;


/**
 * The persistent class for the Devices database table.
 * 
 */
@Entity
@Table(name="Devices")
@NamedQueries( {
	@NamedQuery(name="Device.findAll",    query="SELECT d FROM Device d"),
	@NamedQuery(name="Device.getByIP",    query="SELECT d FROM Device d where d.ip = :IP OR d.wlanip = :IP"),
	@NamedQuery(name="Device.getByMAC",   query="SELECT d FROM Device d where d.mac = :MAC OR d.wlanmac = :MAC"),
	@NamedQuery(name="Device.getByName",  query="SELECT d FROM Device d where d.name = :name"),
	@NamedQuery(name="Device.search",     query="SELECT d FROM Device d where d.name LIKE :name OR d.ip LIKE :name" ),
	@NamedQuery(name="Device.getConfig",  query="SELECT c.value FROM DeviceConfig c WHERE c.device.id = :device_id AND c.keyword = :keyword" ),
	@NamedQuery(name="Device.getMConfig", query="SELECT c.value FROM DeviceMConfig c WHERE c.device.id = :device_id AND c.keyword = :keyword" )
})
@SequenceGenerator(name="seq", initialValue=1, allocationSize=100)
public class Device implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="seq")
	private long id;

	private String name;

	private int place;

	private int row;

	@Column(name="IP")
	private String ip;

	@Column(name="MAC")
	private String mac;

	@Column(name="WLANIP")
	private String wlanip;

	@Column(name="WLANMAC")
	private String wlanmac;

	//bi-directional many-to-one association to DeviceMConfig
	@OneToMany(mappedBy="device", cascade=CascadeType.ALL, orphanRemoval=true )
	@JsonIgnore
	private List<DeviceMConfig> deviceMConfigs;

	//bi-directional many-to-one association to DeviceConfig
	@OneToMany(mappedBy="device", cascade=CascadeType.ALL, orphanRemoval=true )
	@JsonIgnore
	private List<DeviceConfig> deviceConfigs;

	//bi-directional many-to-many association to Device
	@ManyToMany
	@JoinTable(
			name="AvailablePrinters"
			, joinColumns={
					@JoinColumn(name="device_id")
			}
			, inverseJoinColumns={
					@JoinColumn(name="printer_id")
			}
			)
	@JsonIgnore
	private List<Device> availablePrinters;

	//bi-directional many-to-many association to Device
	@ManyToMany(mappedBy="availablePrinters")
	@JsonIgnore
	private List<Device> availableForDevices;

	//bi-directional many-to-many association to Device
	@ManyToOne
	@JoinTable(
			name="DefaultPrinter"
			, joinColumns={
					@JoinColumn(name="device_id")
			}
			, inverseJoinColumns={
					@JoinColumn(name="printer_id")
			}
			)
	@JsonIgnore
	private Device defaultPrinter;

	//bi-directional many-to-many association to Device
	@OneToMany(mappedBy="defaultPrinter")
	@JsonIgnore
	private List<Device> defaultForDevices;

	//bi-directional many-to-one association to HWConf
	@ManyToOne
	@JsonIgnore
	private HWConf hwconf;

	//bi-directional many-to-one association to Room
	@ManyToOne
	@JsonIgnore
	private Room room;

	//bi-directional many-to-one association to User
	@ManyToOne
	@JsonIgnore
	private User owner;

	//bi-directional many-to-many association to Room
	@ManyToMany(mappedBy="availablePrinters")
	@JsonIgnore
	private List<Room> availableInRooms;

	//bi-directional many-to-many association to Room
	@OneToMany(mappedBy="defaultPrinter")
	@JsonIgnore
	private List<Room> defaultInRooms;

	//bi-directional many-to-one association to TestUser
	@OneToMany(mappedBy="device")
	@JsonIgnore
	private List<TestUser> testUsers;

	//bi-directional many-to-many association to User
	@ManyToMany(mappedBy="loggedOn")
	@JsonIgnore
	private List<User> loggedIn;

	@Transient
	long hwconfId;

	public Device() {
		this.hwconfId = -1;
		this.wlanip   = "";
		this.wlanmac  = "";
	}

	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Device && obj !=null) {
			return getId() == ((Device)obj).getId();
		}
		return super.equals(obj);
	}

	public long getHwconfId() {
		return this.hwconfId;
	}

	public void setHwconfId(long id) {
		this.hwconfId = id;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getPlace() {
		return this.place;
	}

	public void setPlace(int place) {
		this.place = place;
	}

	public String getIp() {
		return this.ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getMac() {
		return this.mac;
	}

	public void setMac(String mac) {
		this.mac = mac;
	}

	public String getWlanIp() {
		return this.wlanip;
	}

	public void setWlanIp(String wlanip) {
		this.wlanip = wlanip;
	}

	public String getWlanMac() {
		return this.wlanmac;
	}

	public void setWlanMac(String wlanmac) {
		this.wlanmac = wlanmac;
	}

	public int getRow() {
		return this.row;
	}

	public void setRow(int row) {
		this.row = row;
	}

	public List<Device> getAvailablePrinters() {
		return this.availablePrinters;
	}

	public void setAvailablePrinters(List<Device> availablePrinters) {
		this.availablePrinters = availablePrinters;
	}

	public List<Device> getAvailableForDevices() {
		return this.availableForDevices;
	}

	public void setAvailableForDevices(List<Device> availableForDevices) {
		this.availableForDevices = availableForDevices;
	}

	public Device getDefaultPrinter() {
		return this.defaultPrinter;
	}

	public void setDefaultPrinter(Device defaultPrinter) {
		this.defaultPrinter = defaultPrinter;
	}

/*
 * 	public List<Device> getDefaultForDevices() {
		return this.defaultForDevices;
	}

	public void setDefaultForDevices(List<Device> defaultForDevices) {
		this.defaultForDevices = defaultForDevices;
	}
*/
	public HWConf getHwconf() {
		return this.hwconf;
	}

	public void setHwconf(HWConf hwconf) {
		this.hwconf = hwconf;
	}

	public Room getRoom() {
		return this.room;
	}

	public void setRoom(Room room) {
		this.room = room;
	}

	public User getOwner() {
		return this.owner;
	}

	public void setOwner(User owner) {
		this.owner = owner;
	}

	public List<Room> getAvailableInRooms() {
		return this.availableInRooms;
	}

	public void setAvailableInRooms(List<Room> availableInRooms) {
		this.availableInRooms = availableInRooms;
	}

	public List<Room> getDefaultInRooms() {
		return this.defaultInRooms;
	}

	public void setDefaultInRooms(List<Room> defaultInRooms) {
		this.defaultInRooms = defaultInRooms;
	}

	public List<TestUser> getTestUsers() {
		return this.testUsers;
	}

	public void setTestUsers(List<TestUser> testUsers) {
		this.testUsers = testUsers;
	}

	public TestUser addTestUser(TestUser testUser) {
		getTestUsers().add(testUser);
		testUser.setDevice(this);

		return testUser;
	}

	public TestUser removeTestUser(TestUser testUser) {
		getTestUsers().remove(testUser);
		testUser.setDevice(null);

		return testUser;
	}

	public List<User> getLoggedIn() {
		return this.loggedIn;
	}

	public void setLoggedIn(List<User> loggedIn) {
		this.loggedIn = loggedIn;
	}


	public List<DeviceConfig> getDeviceConfigs() {
		return this.deviceConfigs;
	}

	public void setDeviceConfigs(List<DeviceConfig> deviceConfigs) {
		this.deviceConfigs = deviceConfigs;
	}

	public DeviceConfig addDeviceConfig(DeviceConfig deviceConfig) {
		getDeviceConfigs().add(deviceConfig);
		deviceConfig.setDevice(this);
		return deviceConfig;
	}

	public DeviceConfig removeDeviceConfig(DeviceConfig deviceConfig) {
		getDeviceConfigs().remove(deviceConfig);
		deviceConfig.setDevice(null);
		return deviceConfig;
	}

	public List<DeviceMConfig> getDeviceMConfigs() {
		return this.deviceMConfigs;
	}

	public void setDeviceMConfigs(List<DeviceMConfig> deviceMConfigs) {
		this.deviceMConfigs = deviceMConfigs;
	}

	public DeviceMConfig addDeviceMConfig(DeviceMConfig deviceMConfig) {
		getDeviceMConfigs().add(deviceMConfig);
		deviceMConfig.setDevice(this);
		return deviceMConfig;
	}

	public DeviceMConfig removeDeviceMConfig(DeviceMConfig deviceMConfig) {
		getDeviceMConfigs().remove(deviceMConfig);
		deviceMConfig.setDevice(null);
		return deviceMConfig;
	}

}
