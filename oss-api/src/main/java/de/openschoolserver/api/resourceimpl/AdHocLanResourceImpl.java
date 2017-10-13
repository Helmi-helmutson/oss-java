/* (c) 2017 Péter Varkoly <peter@varkoly.de> - all rights reserved */
package de.openschoolserver.api.resourceimpl;

import java.util.List;


import de.openschoolserver.api.resources.AdHocLanResource;
import de.openschoolserver.dao.Device;
import de.openschoolserver.dao.Group;
import de.openschoolserver.dao.OssResponse;
import de.openschoolserver.dao.Room;
import de.openschoolserver.dao.Session;
import de.openschoolserver.dao.User;

import de.openschoolserver.dao.controller.*;

public class AdHocLanResourceImpl implements AdHocLanResource {

	public AdHocLanResourceImpl() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public List<Long> getObjectIdsOfRoom(Session session, Long roomId, String objectType) {
		AdHocLanController adHocLan = new AdHocLanController(session);
		return adHocLan.getObjectIdsOfRoom(roomId,objectType);
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
		AdHocLanController adHocLan = new AdHocLanController(session);
		return adHocLan.getRooms();
	}

	@Override
	public OssResponse add(Session session, Room room) {
		AdHocLanController adHocLan = new AdHocLanController(session);
		return adHocLan.add(room);
	}

	@Override
	public OssResponse putObjectIntoRoom(Session session, Long roomId, String objectType, Long objectId) {
		AdHocLanController adHocLan = new AdHocLanController(session);
		return adHocLan.putObjectIntoRoom(roomId,objectType,objectId);
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
			if( device.getOwner().equals(session.getUser()) ) {
				return deviceController.delete(deviceId, true);
			} else {
				return new OssResponse(session,"ERROR", "This is not your device.");
			}
		}
	}

	@Override
	public OssResponse addDevice(Session session, long roomId, String macAddress, String name) {
		RoomController roomController = new RoomController(session);
		return roomController.addDevice(roomId, macAddress, name);
	}

}
