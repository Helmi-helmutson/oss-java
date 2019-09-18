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
import de.openschoolserver.dao.internal.CommonEntityManagerFactory;

public class AdHocLanResourceImpl implements AdHocLanResource {

	Logger logger = LoggerFactory.getLogger(AdHocLanResource.class);


	public AdHocLanResourceImpl() {
		super();
	}

	@Override
	public List<User> getUsersOfRoom(Session session, Long roomId) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		Room room  = new RoomController(session,em).getById(roomId);
		em.close();
		for( Category category : room.getCategories() ) {
			if( category.getCategoryType().equals("AdHocAccess")) {
				return category.getUsers();
			}
		}
		return new ArrayList<User>();
	}

	@Override
	public List<Group> getGroupsOfRoom(Session session, Long roomId) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		Room room  = new RoomController(session,em).getById(roomId);
		em.close();
		for( Category category : room.getCategories() ) {
			if( category.getCategoryType().equals("AdHocAccess")) {
				return category.getGroups();
			}
		}
		return new ArrayList<Group>();
	}

	@Override
	public List<User> getUsers(Session session) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		AdHocLanController adHocLan = new AdHocLanController(session,em);
		em.close();
		return adHocLan.getUsers();
	}

	@Override
	public List<Group> getGroups(Session session) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		AdHocLanController adHocLan = new AdHocLanController(session,em);
		List<Group> resp = adHocLan.getGroups();
		em.close();
		return resp;
	}

	@Override
	public List<Room> getRooms(Session session) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		List<Room> resp;
		RoomController roomController = new RoomController(session,em);
		if( roomController.isSuperuser() ) {
			resp = roomController.getByType("AdHocAccess");
		} else {
			resp = roomController.getAllToRegister();
		}
		em.close();
		return resp;
	}

	@Override
	public OssResponse add(Session session, Room room) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		OssResponse resp = new AdHocLanController(session,em).add(room);
		em.close();
		return resp;
	}

	@Override
	public OssResponse putObjectIntoRoom(Session session, Long roomId, String objectType, Long objectId) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		OssResponse resp = new AdHocLanController(session,em).putObjectIntoRoom(roomId,objectType,objectId);
		em.close();
		return resp;
	}

	@Override
	public OssResponse deleteObjectInRoom(Session session, Long roomId, String objectType, Long objectId) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		OssResponse resp = new AdHocLanController(session,em).deleteObjectInRoom(roomId,objectType,objectId);
		em.close();
		return resp;
	}

	@Override
	public List<Device> getDevices(Session session) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		List<Device> resp = session.getUser().getOwnedDevices();
		em.close();
		return resp;
	}

	@Override
	public OssResponse deleteDevice(Session session, Long deviceId) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		DeviceController deviceController = new DeviceController(session,em);
		OssResponse resp;
		if( deviceController.isSuperuser() ) {
			resp = deviceController.delete(deviceId, true);
		} else {
			Device device = deviceController.getById(deviceId);
			if( deviceController.mayModify(device) ) {
				resp = deviceController.delete(deviceId, true);
			} else {
				resp = new OssResponse(session,"ERROR", "This is not your device.");
			}
		}
		em.close();
		return resp;
	}

	@Override
	public OssResponse addDevice(Session session, long roomId, String macAddress, String name) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		OssResponse resp = new RoomController(session,em).addDevice(roomId, macAddress, name);
		em.close();
		return resp;
	}

	@Override
	public OssResponse modifyDevice(Session session, Long deviceId, Device device) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		DeviceController deviceController = new DeviceController(session,em);
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
			new DHCPConfig(session,em).Create();
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
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		final RoomController roomController = new RoomController(session,em);
		Room room = roomController.getById(roomId);
		Category category = new Category();
		category.setCategoryType("AdHocAccess");
		category.setName(room.getName());
		category.setDescription(room.getDescription());
		category.setOwner(session.getUser());
		category.setPublicAccess(false);
		category.getRooms().add(room);
		CategoryController categoryController = new CategoryController(session,em);
		OssResponse resp = categoryController.add(category);
		if( resp.getCode().equals("OK")  ) {
			room.setRoomType("AdHocAccess");
			room.getCategories().add(category);
			resp = roomController.modify(room);
		}
		em.close();
		return resp;
	}

	@Override
	public List<User> getAvailableUser(Session session, long roomId) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		List<User> users = new ArrayList<User>();
		Category category = new AdHocLanController(session,em).getAdHocCategoryOfRoom(roomId);
		for( User user : new UserController(session,em).getAll() ) {
			if( !category.getUsers().contains(user) ) {
				users.add(user);
			}
		}
		em.close();
		return users;
	}

	@Override
	public List<Group> getAvailableGroups(Session session, long roomId) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		List<Group> groups = new ArrayList<Group>();
		Category category = new AdHocLanController(session,em).getAdHocCategoryOfRoom(roomId);
		for( Group group : new GroupController(session,em).getAll() ) {
			if( !category.getGroups().contains(group) ) {
				groups.add(group);
			}
		}
		em.close();
		return groups;
	}

	@Override
	public Room getRoomById(Session session, Long roomId) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		final RoomController roomController = new RoomController(session,em);
		Room room = roomController.getById(roomId);
		if( room != null && !room.getRoomType().equals("AdHocAccess")) {
			room = null;
		}
		em.close();
		return room;
	}

	@Override
	public OssResponse modify(Session session, Long roomId, Room room) {
		OssResponse resp;
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		final RoomController rc =  new RoomController(session,em);
		Room oldRoom = rc.getById(roomId);
		if( !oldRoom.getRoomType().equals("AdHocAccess")) {
			resp = new OssResponse(session,"ERROR","This is not an AdHocLan room");
		} else {
			room.setId(oldRoom.getId());
			resp = rc.modify(room);
		}
		em.close();
		return resp;
	}

	@Override
	public boolean getStudentsOnly(Session session, Long roomId) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		boolean resp = new AdHocLanController(session,em).getAdHocCategoryOfRoom(roomId).getStudentsOnly();
		em.close();
		return resp;
	}

	@Override
	public OssResponse setStudentsOnly(Session session, Long roomId, boolean studentsOnly) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		Category category = new AdHocLanController(session,em).getAdHocCategoryOfRoom(roomId);
		category.setStudentsOnly(studentsOnly);
		OssResponse resp = new CategoryController(session,em).modify(category);
		em.close();
		return resp;
	}

	@Override
	public List<Device> getDevicesOfRoom(Session session, Long adHocRoomId) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		Room room = em.find(Room.class, adHocRoomId);
		List<Device> devices = new ArrayList<Device>();
		if( room != null ) {
			for(Device device : room.getDevices() ) {
				if(device.getOwner() != null ) {
					device.setOwnerName(
							String.format("%s (%s, %s)",
									device.getOwner().getUid(),
									device.getOwner().getSurName(),
									device.getOwner().getGivenName()
									)
							);
				}
				devices.add(device);
			}
		}
		em.close();
		return devices;
	}

	@Override
	public OssResponse delete(Session session, Long adHocRoomId) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		OssResponse resp = new RoomController(session,em).delete(adHocRoomId);
		em.close();
		return resp;
	}

}
