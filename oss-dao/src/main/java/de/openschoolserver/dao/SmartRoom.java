package de.openschoolserver.dao;

import java.util.List;
import de.openschoolserver.dao.controller.*;

public class SmartRoom {
	
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

	public SmartRoom(Session session, Long roomId) {
		DeviceController    dc = new DeviceController(session);
		EducationController ec = new EducationController(session);
		RoomController      rc = new RoomController(session);
		UserController      uc = new UserController(session);
		this.loggedIns         = ec.getRoom(roomId);
		Room         room = rc.getById(roomId);
		Category category = room.getCategories().get(0);
		this.users   = category.getUsers();
		this.groups  = category.getGroups();
		this.devices = category.getDevices(); 
		for(List<Long> loggedIn : loggedIns) {
			User   user    = uc.getById(loggedIn.get(0));
			Device device  = dc.getById(loggedIn.get(1));
			if( user != null && !this.users.contains(user) ) {
				this.users.add(user);
			}
			if( device != null && !this.devices.contains(device) ) {
				this.devices.add(device);
			}
		}
		this.accessInRooms = rc.getAccessStatus(roomId);
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
