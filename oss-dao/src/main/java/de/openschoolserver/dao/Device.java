/* (c) 2017 Peter Varkoly <peter@varkoly.de> - all rights reserved */
package de.openschoolserver.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;


/**
 * The persistent class for the Devices database table.
 * 
 */
@Entity
@Table(name="Devices")
@NamedQueries( {
	@NamedQuery(name="Device.findAll",      query="SELECT d FROM Device d"),
	@NamedQuery(name="Device.findAllId",    query="SELECT d.id FROM Device d"),
	@NamedQuery(name="Device.getByIP",      query="SELECT d FROM Device d where d.ip = :IP OR d.wlanIp = :IP"),
	@NamedQuery(name="Device.getByMAC",     query="SELECT d FROM Device d where d.mac = :MAC OR d.wlanMac = :MAC"),
	@NamedQuery(name="Device.getByName",    query="SELECT d FROM Device d where d.name = :name"),
	@NamedQuery(name="Device.search",       query="SELECT d FROM Device d where d.name LIKE :search OR d.ip LIKE :search OR d.wlanIp LIKE :search OR d.mac LIKE :search OR d.wlanMac LIKE :search" ),
})
@SequenceGenerator(name="seq", initialValue=1, allocationSize=100)
public class Device implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="seq")
	private Long id;

	@Column(name = "name", updatable = false)
	private String name;

	private int place;

	private int row;

	@Column(name="IP")
	private String ip;

	@Column(name="MAC")
	private String mac;

	private String wlanIp;

	private String wlanMac;

	private String serial;

	private String inventary;

	private String locality;

	//bi-directional many-to-many association to Category
	@ManyToMany(mappedBy="devices", cascade ={CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH} )
	@JsonIgnore
	private List<Category> categories;

	//bi-directional many-to-many association to Device
	@ManyToMany(cascade ={CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
	@JoinTable(
			name="AvailablePrinters",
			joinColumns={ @JoinColumn(name="device_id")	},
			inverseJoinColumns={ @JoinColumn(name="printer_id") }
	)
	@JsonIgnore
	private List<Device> availablePrinters;

	//bi-directional many-to-many association to Device
	@ManyToMany(mappedBy="availablePrinters",cascade ={CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
	@JsonIgnore
	private List<Device> availableForDevices;

	//bi-directional many-to-many association to Device
	@ManyToOne
	@JoinTable(
			name="DefaultPrinter",
			joinColumns={@JoinColumn(name="device_id")},
			inverseJoinColumns={@JoinColumn(name="printer_id")}
		)
	@JsonIgnore
	private Device defaultPrinter;

	//bi-directional many-to-many association to Device
	@OneToMany(mappedBy="defaultPrinter")
	@JsonIgnore
	private List<Device> defaultForDevices;

	//bi-directional many-to-many association to Device
	@ManyToMany(mappedBy="devices", cascade ={CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
	@JsonIgnore
	private List<SoftwareLicense> softwareLicenses;

	//bi-directional many-to-one association to SoftwareStatus
	@OneToMany(mappedBy="device")
	@JsonIgnore
	private List<SoftwareStatus> softwareStatus;

	//bi-directional many-to-one association to HWConf
	@ManyToOne
	@JsonIgnore
	private HWConf hwconf;

	@Column(name="hwconf_id", insertable=false, updatable=false)
	private Long hwconfId;

	//bi-directional many-to-one association to Room
	@ManyToOne
	@JsonIgnore
	private Room room;

	//bi-directional many-to-one association to User
	@ManyToOne
	@JsonIgnore
	private User owner;

	//bi-directional many-to-many association to Room
	@ManyToMany(mappedBy="availablePrinters", cascade ={CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
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
	@ManyToMany(mappedBy="loggedOn", cascade ={CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
	@JsonIgnore
	private List<User> loggedIn;

	public Device() {
		this.hwconfId = null;
		this.name = "";
		this.ip = "";
		this.mac = "";
		this.wlanIp   = "";
		this.wlanMac  = "";
		this.softwareLicenses = new ArrayList<SoftwareLicense>();
		this.softwareStatus   = new ArrayList<SoftwareStatus>();
	}

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Device && obj !=null) {
			return getId() == ((Device)obj).getId();
		}
		return super.equals(obj);
	}

	public Long getHwconfId() {
		return this.hwconfId;
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

	public int getRow() {
		return this.row;
	}

	public void setRow(int row) {
		this.row = row;
	}

	public String getSerial() {
		return this.serial;
	}

	public void setSerial(String value) {
		this.serial = value;
	}

	public String getInventary() {
		return this.inventary;
	}

	public void setInventary(String value) {
		this.inventary = value;
	}

	public String getLocality() {
		return this.locality;
	}

	public void setLocality(String value) {
		this.locality = value;
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


	public List<Category> getCategories() {
	        return this.categories;
	}

	public void setCategories(List<Category> categories) {
	        this.categories = categories;
	}

	public void setSofwareStatus(List<SoftwareStatus> st) {
		this.softwareStatus = st;
	}

	public List<SoftwareStatus> getSofwareStatus() {
		return this.softwareStatus;
	}

	public String getWlanIp() {
		return wlanIp;
	}

	public void setWlanIp(String wlanip) {
		this.wlanIp = wlanip;
	}

	public String getWlanMac() {
		return wlanMac;
	}

	public void setWlanMac(String wlanmac) {
		this.wlanMac = wlanmac;
	}

	public List<Device> getDefaultForDevices() {
		return defaultForDevices;
	}

	public void setDefaultForDevices(List<Device> defaultForDevices) {
		this.defaultForDevices = defaultForDevices;
	}

	public List<SoftwareLicense> getSoftwareLicenses() {
		return softwareLicenses;
	}

	public void setSoftwareLicenses(List<SoftwareLicense> softwareLicenses) {
		this.softwareLicenses = softwareLicenses;
	}

	public List<SoftwareStatus> getSoftwareStatus() {
		return softwareStatus;
	}

	public void setSoftwareStatus(List<SoftwareStatus> softwareStatus) {
		this.softwareStatus = softwareStatus;
	}

	public void setHwconfId(Long hwconfId) {
		this.hwconfId = hwconfId;
	}
}
