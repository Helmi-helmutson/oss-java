/* (c) 2017 Péter Varkoly <peter@varkoly.de> - all rights reserved */
package de.openschoolserver.dao;

import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import javax.persistence.*;
import javax.validation.constraints.Size;

import java.util.ArrayList;
import java.util.List;


/**
 * The persistent class for the Rooms database table.
 * 
 */
@Entity
@Table(name="Rooms")
@NamedQueries ({
	@NamedQuery(name="Room.findAll",        query="SELECT r    FROM Room r WHERE r.roomType != 'smartRoom'"),
	@NamedQuery(name="Room.findAllId",      query="SELECT r.id FROM Room r WHERE r.roomType != 'smartRoom'"),
	@NamedQuery(name="Room.findAllWithControl",query="SELECT r FROM Room r WHERE r.roomType != 'smartRoom' AND r.roomControl != 'no'"),
	@NamedQuery(name="Room.findAllWithFirewallControl", query="SELECT r FROM Room r WHERE r.roomType != 'smartRoom'"),
	@NamedQuery(name="Room.findAllToUse",   query="SELECT r    FROM Room r WHERE r.roomType != 'smartRoom' AND r.name != 'ANON_DHCP' AND r.roomType != 'ANON_DHCP'"),
	@NamedQuery(name="Room.findAllToUseId", query="SELECT r.id FROM Room r WHERE r.roomType != 'smartRoom' AND r.name != 'ANON_DHCP' AND r.roomType != 'ANON_DHCP'"),
	@NamedQuery(name="Room.findAllToRegister", query="SELECT r FROM Room r WHERE r.roomType != 'smartRoom' AND r.name != 'ANON_DHCP' AND r.roomType != 'ANON_DHCP'"),
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
	@Size(max=32, message="Name must not be longer then 32 characters.")
	private String name;

	private int places;

	private int rows;

	@Size(max=64, message="Description must not be longer then 64 characters.")
	private String description;

	@Column(name = "netMask", updatable = false)
	private int netMask;

	@Column(name = "startIP", updatable = false)
	private String startIP;

	private String roomType;

	private String roomControl;

    //bi-directional many-to-many association to Category
	@ManyToMany(mappedBy="rooms")
	@JsonIgnore
	private List<Category> categories = new ArrayList<Category>();

	//bi-directional many-to-one association to AccessInRoom
	@OneToMany(mappedBy="room", cascade=CascadeType.ALL, orphanRemoval=true)
	@JsonIgnore
	private List<AccessInRoom> accessInRooms = new ArrayList<AccessInRoom>();

	//bi-directional many-to-one association to Device
	@OneToMany(mappedBy="room")
	@JsonIgnore
	private List<Device> devices = new ArrayList<Device>();

	//bi-directional many-to-many association to Device
	@ManyToMany( cascade ={CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH} )
	@JoinTable(
			name="AvailablePrinters",
			joinColumns={ @JoinColumn(name="room_id") },
			inverseJoinColumns={@JoinColumn(name="printer_id")}
			)
	@JsonIgnore
	private List<Printer> availablePrinters = new ArrayList<Printer>();

	//bi-directional many-to-one association to Device
	@ManyToOne( cascade ={CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH} )
	@JoinTable(
			name="DefaultPrinter",
			joinColumns={ @JoinColumn(name="room_id") },
			inverseJoinColumns={ @JoinColumn(name="printer_id") }
			)
	@JsonIgnore
	private Printer defaultPrinter;

	//bi-directional many-to-one association to Test
	@OneToMany(mappedBy="room")
	@JsonIgnore
	private List<Test> tests = new ArrayList<Test>();

	//bi-directional many-to-one association to RoomSmartControl
	@OneToMany(mappedBy="room")
	@JsonIgnore
	private List<RoomSmartControl> smartControls = new ArrayList<RoomSmartControl>();
		
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
		this.network           = "";
		this.roomControl       = "inRoom";
		this.roomType		   = "ComputerRoom";
		this.startIP           = "";
		this.categories        = new ArrayList<Category>();
		this.accessInRooms     = new ArrayList<AccessInRoom>();
		this.availablePrinters = new ArrayList<Printer>();
		this.devices           = new ArrayList<Device>();
		this.smartControls     = new ArrayList<RoomSmartControl>();
	}

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public String toString() {
		try {
			return new ObjectMapper().writeValueAsString(this);
		} catch (Exception e) {
			return "{ \"ERROR\" : \"CAN NOT MAP THE OBJECT\" }";
		}
	}
	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Room other = (Room) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
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

	public List<Printer> getAvailablePrinters() {
		return this.availablePrinters;
	}

	public void setAvailablePrinters(List<Printer> availablePrinters) {
		this.availablePrinters = availablePrinters;
	}

	public Printer getDefaultPrinter() {
		return this.defaultPrinter;
	}

	public void setDefaultPrinter(Printer defaultPrinter) {
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
