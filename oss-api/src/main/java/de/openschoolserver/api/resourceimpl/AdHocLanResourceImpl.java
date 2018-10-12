/* (c) 2017 Péter Varkoly <peter@varkoly.de> - all rights reserved */
package de.openschoolserver.api.resourceimpl;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.openschoolserver.api.resources.AdHocLanResource;
import de.openschoolserver.dao.Category;
import de.openschoolserver.dao.Device;
import de.openschoolserver.dao.Group;
import de.openschoolserver.dao.OssResponse;
import de.openschoolserver.dao.Room;
import de.openschoolserver.dao.Session;
import de.openschoolserver.dao.User;
import de.openschoolserver.dao.controller.*;

public class AdHocLanResourceImpl implements AdHocLanResource {

	Logger logger = LoggerFactory.getLogger(AdHocLanResource.class);

	public AdHocLanResourceImpl() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public List<User> getUsersOfRoom(Session session, Long roomId) {
		Room room  = new RoomController(session).getById(roomId);
		for( Category category : room.getCategories() ) {
			if( category.getCategoryType().equals("AdHocAccess")) {
				return category.getUsers();
			}
		}
		return new ArrayList<User>();
	}

	@Override
	public List<Group> getGroupsOfRoom(Session session, Long roomId) {
		Room room  = new RoomController(session).getById(roomId);
		for( Category category : room.getCategories() ) {
			if( category.getCategoryType().equals("AdHocAccess")) {
				return category.getGroups();
			}
		}
		return new ArrayList<Group>();
	}

	@Override
	public List<User> getUsers(Session session) {
		AdHocLanController adHocLan = new AdHocLanController(session);
		return adHocLan.getUsers();
	}

	@Override
	public List<Group> getGroups(Session session) {
		AdHocLanController adHocLan = new AdHocLanController(session);
		return adHocLan.getGroups();
	}

	@Override
	public List<Room> getRooms(Session session) {
		return new RoomController(session).getByType("AdHocAccess");
	}

	@Override
	public OssResponse add(Session session, Room room) {
		return new AdHocLanController(session).add(room);
	}

	@Override
	public OssResponse putObjectIntoRoom(Session session, Long roomId, String objectType, Long objectId) {
		return new AdHocLanController(session).putObjectIntoRoom(roomId,objectType,objectId);
	}

	@Override
	public OssResponse deleteObjectIntoRoom(Session session, Long roomId, String objectType, Long objectId) {
		return new AdHocLanController(session).deleteObjectInRoom(roomId,objectType,objectId);
	}

	@Override
	public List<Device> getDevices(Session session) {
		return session.getUser().getOwnedDevices();
	}

	@Override
	public OssResponse deleteDevice(Session session, Long deviceId) {
		DeviceController deviceController = new DeviceController(session);
		if( deviceController.isSuperuser() ) {
			return deviceController.delete(deviceId, true);
		} else {
			Device device = deviceController.getById(deviceId);
			if( deviceController.mayModify(device) ) {
				return deviceController.delete(deviceId, true);
			} else {
				return new OssResponse(session,"ERROR", "This is not your device.");
			}
		}
	}

	@Override
	public OssResponse addDevice(Session session, long roomId, String macAddress, String name) {
		return new RoomController(session).addDevice(roomId, macAddress, name);
	}

	@Override
	public OssResponse modifyDevice(Session session, Long deviceId, Device device) {
		DeviceController deviceController = new DeviceController(session);
		EntityManager em = deviceController.getEntityManager();
		try {
			Device oldDevice = em.find(Device.class, deviceId);
			if( oldDevice == null ) {
				return new OssResponse(session,"ERROR","Can not find the device.");
			}
			if( deviceId != device.getId() ) {
				return new OssResponse(session,"ERROR","Device ID mismatch.");
			}
			if( ! deviceController.mayModify(device) ) {
				return new OssResponse(session,"ERROR", "This is not your device.");
			}
			em.getTransaction().begin();
			oldDevice.setMac(device.getMac());
			em.merge(oldDevice);
			em.getTransaction().commit();
			new DHCPConfig(session).Create();
		}  catch (Exception e) {
			logger.error(e.getMessage());
			return new OssResponse(session,"ERROR", e.getMessage());
		} finally {
			em.close();
		}
		return new OssResponse(session,"OK", "Device was modified successfully");
	}


	@Override
	public OssResponse turnOn(Session session, Long roomId) {
		final RoomController roomController = new RoomController(session);
		Room room = roomController.getById(roomId);
		Category category = new Category();
		category.setCategoryType("AdHocAccess");
		category.setName(room.getName());
		category.setDescription(room.getDescription());
		category.setOwner(session.getUser());
		category.setPublicAccess(false);
		category.getRooms().add(room);
		CategoryController categoryController = new CategoryController(session);
		OssResponse ossResponseCategory = categoryController.add(category);
		room.setRoomType("AdHocAccess");
		room.getCategories().add(category);
		return roomController.modify(room);
	}

	@Override
	public List<User> getAvailableUser(Session session, long roomId) {
		List<User> users = new ArrayList<User>();
		Category category = new AdHocLanController(session).getAdHocCategoryOfRoom(roomId);
		for( User user : new UserController(session).getAll() ) {
			if( !category.getUsers().contains(user) ) {
				users.add(user);
			}
		}
		return users;
	}

	@Override
	public List<Group> getAvailableGroups(Session session, long roomId) {
		List<Group> groups = new ArrayList<Group>();
		Category category = new AdHocLanController(session).getAdHocCategoryOfRoom(roomId);
		for( Group group : new GroupController(session).getAll() ) {
			if( !category.getGroups().contains(group) ) {
				groups.add(group);
			}
		}
		return groups;
	}

	@Override
	public Room getRoomById(Session session, Long roomId) {
		final RoomController roomController = new RoomController(session);
		Room room = roomController.getById(roomId);
		if( room == null ) {
			return null;
		}
		if( room.getRoomType().equals("AdHocAccess")) {
			return room;
		}
		return null;
	}

	@Override
	public OssResponse modify(Session session, Long roomId, Room room) {
		final RoomController rc =  new RoomController(session);
		Room oldRoom = rc.getById(roomId);
		if( !oldRoom.getRoomType().equals("AdHocAccess")) {
			return new OssResponse(session,"ERROR","This is not an AdHocLan room");
		}
		room.setId(oldRoom.getId());
		return rc.modify(oldRoom);
	}

	@Override
	public boolean getStudentsOnly(Session session, Long roomId) {
		return new AdHocLanController(session).getAdHocCategoryOfRoom(roomId).getStudentsOnly();
	}

	@Override
	public OssResponse setStudentsOnly(Session session, Long roomId, boolean studentsOnly) {
		Category category = new AdHocLanController(session).getAdHocCategoryOfRoom(roomId);
		category.setStudentsOnly(studentsOnly);
		return new CategoryController(session).modify(category);
	}

}
