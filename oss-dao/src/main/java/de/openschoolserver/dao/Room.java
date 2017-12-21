/* (c) 2017 Péter Varkoly <peter@varkoly.de> - all rights reserved */
package de.openschoolserver.dao;

import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.persistence.*;
import java.util.List;


/**
 * The persistent class for the Rooms database table.
 * 
 */
@Entity
@Table(name="Rooms")
@NamedQueries ({
	@NamedQuery(name="Room.findAll",        query="SELECT r FROM Room r WHERE r.roomType != 'smartRoom'"),
	@NamedQuery(name="Room.findAllId",      query="SELECT r.id FROM Room r WHERE r.roomType != 'smartRoom'"),
	@NamedQuery(name="Room.findAllToUse",   query="SELECT r FROM Room r WHERE r.roomType != 'smartRoom' AND r.name != 'ANON_DHCP'"),
	@NamedQuery(name="Room.findAllToUseId", query="SELECT r.id FROM Room r WHERE r.roomType != 'smartRoom' AND r.name != 'ANON_DHCP'"),
	@NamedQuery(name="Room.findAllToRegister", query="SELECT r FROM Room r WHERE r.name != 'ANON_DHCP' AND r.roomType != 'smartRoom'"),
	@NamedQuery(name="Room.getByName", query="SELECT r FROM Room r WHERE r.name = :name"),
	@NamedQuery(name="Room.getByDescription", query="SELECT r FROM Room r WHERE r.description = :description"),
	@NamedQuery(name="Room.getByType", query="SELECT r FROM Room r WHERE r.roomType = :type"),
	@NamedQuery(name="Room.getByControl", query="SELECT r FROM Room r WHERE r.roomControl = :control"),
	@NamedQuery(name="Room.getByIp", query="SELECT r FROM Room r WHERE r.startIP = :ip"),
	@NamedQuery(name="Room.search", query="SELECT r FROM Room r WHERE r.name LIKE :search OR r.description LIKE :search OR r.roomType LIKE :search AND r.roomType != 'smartRoom'"),
	@NamedQuery(name="Room.getDeviceCount", query="SELECT COUNT( d ) FROM  Device d WHERE d.room.id = :id")
})
@SequenceGenerator(name="seq", initialValue=1, allocationSize=100)
public class Room implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="seq")
	private Long id;

	@Column(name = "name", updatable = false)
	private String name;

	private int places;

	private int rows;

	private String description;

	@Column(name = "netMask", updatable = false)
	private int netMask;

	@Column(name = "startIP", updatable = false)
	private String startIP;

	private String roomType;

	private String roomControl;

        //bi-directional many-to-many association to Category
	@ManyToMany(mappedBy="rooms", cascade ={CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
	@JsonIgnore
	private List<Category> categories;

	//bi-directional many-to-one association to AccessInRoom
	@OneToMany(mappedBy="room", cascade=CascadeType.ALL, orphanRemoval=true)
	@JsonIgnore
	private List<AccessInRoom> accessInRooms;

	//bi-directional many-to-one association to Device
	@OneToMany(mappedBy="room", cascade=CascadeType.ALL, orphanRemoval=true)
	@JsonIgnore
	private List<Device> devices;

	//bi-directional many-to-many association to Device
	@ManyToMany( cascade ={CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH} )
	@JoinTable(
			name="AvailablePrinters"
			, joinColumns={
					@JoinColumn(name="room_id")
			}
			, inverseJoinColumns={
					@JoinColumn(name="printer_id")
			}
			)
	@JsonIgnore
	private List<Device> availablePrinters;

	//bi-directional many-to-one association to Device
	@ManyToOne( cascade ={CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH} )
	@JoinTable(
			name="DefaultPrinter"
			, joinColumns={
					@JoinColumn(name="room_id")
			}
			, inverseJoinColumns={
					@JoinColumn(name="printer_id")
			}
			)
	@JsonIgnore
	private Device defaultPrinter;

	//bi-directional many-to-one association to Test
	@OneToMany(mappedBy="room")
	@JsonIgnore
	private List<Test> tests;

	//bi-directional many-to-one association to RoomSmartControl
	@OneToMany(mappedBy="room")
	@JsonIgnore
	private List<RoomSmartControl> smartControls;
		
	@Transient
	private String network;

	//bi-directional many-to-one association to HWConf
	@ManyToOne
	@JsonIgnore
	private HWConf hwconf;

	@Column(name="hwconf_id", insertable=false, updatable=false)
	private Long hwconfId;
	
    //bi-directional many-to-one association to User
	@ManyToOne
	@JsonIgnore
	private User creator;

	public Room() {
		this.network      = "";
		this.roomControl  = "inRoom";
		this.startIP      = "";
	}

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Room && obj !=null) {
			return getId() == ((Room)obj).getId();
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

	public int getPlaces() {
		return this.places;
	}

	public void setPlaces(int places) {
		this.places = places;
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

	public String getRoomType() {
		return this.roomType;
	}

	public void setRoomType(String roomtype) {
		this.roomType = roomtype;
	}

	public String getRoomControl() {
		return this.roomControl;
	}

	public void setRoomControl(String roomcontrol) {
		this.roomControl = roomcontrol;
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

	public void setAccessInRoom(List<AccessInRoom> accessinrooms) {
		this.accessInRooms = accessinrooms;
	}

	public AccessInRoom addAccessInRoom(AccessInRoom accessinroom) {
		getAccessInRooms().add(accessinroom);
		accessinroom.setRoom(this);
		return accessinroom;
	}

	public AccessInRoom removeAccessInRoome(AccessInRoom accessinroom) {
		getAccessInRooms().remove(accessinroom);
		accessinroom.setRoom(null);
		return accessinroom;
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
		this.hwconf   = hwconf;
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

        public List<Category> getCategories() {
                return this.categories;
        }

        public void setCategories(List<Category> categories) {
                this.categories = categories;
        }
        
        public List<RoomSmartControl> getSmartControls() {
        	return this.smartControls;
        }

		public User getCreator() {
			return creator;
		}

		public void setCreator(User creator) {
			this.creator = creator;
		}

		public void setAccessInRooms(List<AccessInRoom> accessInRooms) {
			this.accessInRooms = accessInRooms;
		}

		public void setSmartControls(List<RoomSmartControl> smartControls) {
			this.smartControls = smartControls;
		}

		public void setHwconfId(Long hwconfId) {
			this.hwconfId = hwconfId;
		}

}
