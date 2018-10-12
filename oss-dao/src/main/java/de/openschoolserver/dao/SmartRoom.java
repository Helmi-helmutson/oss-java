package de.openschoolserver.dao;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonIgnore;

import de.openschoolserver.dao.controller.*;

public class SmartRoom {

	Logger logger = LoggerFactory.getLogger(SmartRoom.class);

	private Long id;

	private String name;

	private String description;

	private int places;

	private int rows;

	private AccessInRoom accessInRooms;

	private List<Device> devices;

	private List<User> users;

	private List<Group> groups;

	private List<List<Long>> loggedIns;

	private Long[][]  deviceMatrix;

	public SmartRoom(Session session, Long roomId) {
		DeviceController    dc = new DeviceController(session);
		EducationController ec = new EducationController(session);
		RoomController      rc = new RoomController(session);
		UserController      uc = new UserController(session);
		this.loggedIns         = ec.getRoom(roomId);
		rc.organizeRoom(roomId);
		Room              room = rc.getById(roomId);
		this.id          = room.getId();
		this.description = room.getDescription();
		this.name        = room.getName();
		this.rows        = room.getRows();
		this.places      = room.getPlaces();
		if( room.getRoomType().equals("smartRoom") ) {
			Category category = room.getCategories().get(0);
			this.users   = new ArrayList<User>();
			for( User user : category.getUsers() ) {
				if( user.getRole().equals("students")) {
					this.users.add(user);
				}
			}
			this.groups  = category.getGroups();
			this.devices = category.getDevices();
		} else {
			this.devices = room.getDevices();
			this.users   = new ArrayList<User>();
		}
		for(List<Long> loggedIn : this.loggedIns) {
			User   user    = uc.getById(loggedIn.get(0));
			Device device  = dc.getById(loggedIn.get(1));
			if( user != null && !this.users.contains(user) ) {
				this.users.add(user);
			}
			if( device != null && !this.devices.contains(device) ) {
				this.devices.add(device);
			}
		}
		/*If this is a smart room, we have to organize the devices.
		if( isSmartRoom ) {
			int workstationCount = (this.devices.size() > this.users.size()) ?  this.devices.size() : this.users.size();
			int availablePlaces  = room.getPlaces() * room.getRows();
			while( workstationCount > availablePlaces ) {
				this.places++;
				this.rows++;
				availablePlaces  = this.places * this.rows ;
				changed = true;
			}
			if( changed ) {
				room.setPlaces(this.places);
				room.setRows(this.rows);
				try {
					em.getTransaction().begin();
					em.merge(room);
					em.getTransaction().commit();
				} catch (Exception e) {
					logger.debug("Modify room:" + e.getMessage());
				}
			}
			deviceMatrix = new Long[this.rows+1][this.places+1];
			for ( Device device : this.devices ) {
				String coordinates = dc.getConfig(device, "smartRoom-" + this.id + "-coordinates");
				if( coordinates != null ) {
					int r = Integer.parseInt(coordinates.split(",")[0]);
					int p = Integer.parseInt(coordinates.split(",")[1]);
					device.setRow(r);
					device.setRow(p);
					deviceMatrix[r][p]=device.getId();
				}
			}
			for ( Device device : this.devices ) {
				String coordinates = dc.getConfig(device, "smartRoom-" + this.id + "-koordinates");
				if( coordinates == null ) {
					List<Integer> newCoordinates = this.getNextFreePlace();
					int r = newCoordinates.get(0);
					int p = newCoordinates.get(1);
					device.setRow(r);
					device.setRow(p);
					deviceMatrix[r][p]=device.getId();
					dc.setConfig(device, "smartRoom-" + this.id + "-coordinates", String.format("%d,%d", r,p));
				}
			}
		}*/
		this.accessInRooms = rc.getAccessStatus(roomId);
	}

	@JsonIgnore
	private List<Integer> getNextFreePlace() {
		List<Integer> coordinates = new ArrayList<Integer>();
		int row   = 1;
		int place = 1;
		while( this.deviceMatrix[row][place] > 0) {
			logger.debug("getNextFreePlace:" + row + " " + place);
			if( place < this.places ) {
				place++;
			} else {
				place = 1;
				row ++;
			}
		}
		coordinates.add(row);
		coordinates.add(place);
		return coordinates;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getPlaces() {
		return places;
	}

	public void setPlaces(int places) {
		this.places = places;
	}

	public int getRows() {
		return rows;
	}

	public void setRows(int rows) {
		this.rows = rows;
	}

	public AccessInRoom getAccessInRooms() {
		return accessInRooms;
	}

	public void setAccessInRooms(AccessInRoom accessInRooms) {
		this.accessInRooms = accessInRooms;
	}

	public List<Device> getDevices() {
		return devices;
	}

	public void setDevices(List<Device> devices) {
		this.devices = devices;
	}

	public List<User> getUsers() {
		return users;
	}

	public void setUsers(List<User> users) {
		this.users = users;
	}

	public List<Group> getGroups() {
		return groups;
	}

	public void setGroups(List<Group> groups) {
		this.groups = groups;
	}
	public List<List<Long>> getLoggedIns() {
		return loggedIns;
	}

	public void setLoggedIns(List<List<Long>> loggedIns) {
		this.loggedIns = loggedIns;
	}
}
