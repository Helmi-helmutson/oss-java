/* (c) 2017 Péter Varkoly <peter@varkoly.de> - all rights reserved */
package de.openschoolserver.api.resourceimpl;

import de.openschoolserver.dao.AccessInRoom;
import de.openschoolserver.dao.Device;
import de.openschoolserver.dao.HWConf;
import de.openschoolserver.dao.Room;
import de.openschoolserver.dao.Session;
import de.openschoolserver.dao.controller.RoomController;
import de.openschoolserver.dao.controller.EducationController;
import de.openschoolserver.dao.OssResponse;
import de.openschoolserver.api.resources.RoomResource;


import javax.ws.rs.WebApplicationException;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RoomRescourceImpl implements RoomResource {
	
	Logger logger = LoggerFactory.getLogger(RoomRescourceImpl.class);

    @Override
    public Room getById(Session session, long roomId) {
       final RoomController roomController = new RoomController(session);
       final Room room = roomController.getById(roomId);
        if (room == null) {
            throw new WebApplicationException(404);
        }
        return room;
    }

    @Override
    public List<Room> getAll(Session session) {
        final RoomController roomController = new RoomController(session);
        final List<Room> rooms = roomController.getAll();
        return rooms;
    }

    @Override
    public OssResponse delete(Session session, long roomId) {
        // TODO Auto-generated method stub
    	final RoomController roomController = new RoomController(session);
    	return roomController.delete(roomId);
    }

    @Override
    public OssResponse add(Session session, Room room) {
    	final RoomController roomController = new RoomController(session);
    	return roomController.add(room);
    }

    @Override
    public List<String> getAvailableIPAddresses(Session session, long roomId) {
        final RoomController roomController = new RoomController(session);
        final List<String> ips = roomController.getAvailableIPAddresses(roomId);
        if ( ips == null) {
            throw new WebApplicationException(404);
        }
        return ips;
    }
    
    @Override
	public List<String> getAvailableIPAddresses(Session session, long roomId, long count) {
    	final RoomController roomController = new RoomController(session);
        final List<String> ips = roomController.getAvailableIPAddresses(roomId,count);
        if ( ips == null) {
            throw new WebApplicationException(404);
        }
        return ips;
	}

	@Override
	public String getNextRoomIP(Session session, String network, int netMask) {
		final RoomController roomController = new RoomController(session);
		final String nextIP = roomController.getNextRoomIP(network, netMask);
        if ( nextIP == null) {
            throw new WebApplicationException(404);
        }
		return nextIP;
	}

	@Override
	public List<Map<String, String>> getLoggedInUsers(Session session, long roomId) {
		// TODO Auto-generated method stub
		final RoomController roomController = new RoomController(session);
		final List<Map<String, String>> users = roomController.getLoggedInUsers(roomId);
        if ( users == null) {
            throw new WebApplicationException(404);
        }
        return users;
	}

	@Override
	public List<AccessInRoom> getAccessList(Session session, long roomId) {
		// TODO Auto-generated method stub
		final RoomController roomController = new RoomController(session);
		final List<AccessInRoom> accesses = roomController.getAccessList(roomId);
        if ( accesses == null) {
            throw new WebApplicationException(404);
        }
        return accesses;
	}

	@Override
	public OssResponse setAccessList(Session session, long roomId, List<AccessInRoom> accessList) {
		// TODO Auto-generated method stub
		final RoomController roomController = new RoomController(session);
		return roomController.setAccessList(roomId, accessList);
	}

	@Override
	public OssResponse setScheduledAccess(Session session) {
		// TODO Auto-generated method stub
		final RoomController roomController = new RoomController(session);
		return roomController.setScheduledAccess();
	}

	@Override
	public List<AccessInRoom> getAccessStatus(Session session) {
		// TODO Auto-generated method stub
		final RoomController roomController = new RoomController(session);
		final List<AccessInRoom> accesses = roomController.getAccessStatus();
		return accesses;
	}

	@Override
	public AccessInRoom getAccessStatus(Session session, long roomId) {
		// TODO Auto-generated method stub
		final RoomController roomController = new RoomController(session);
		final AccessInRoom access = roomController.getAccessStatus(roomId);
		return access;
	}

	@Override
	public OssResponse setAccessStatus(Session session, long roomId, AccessInRoom access) {
		// TODO Auto-generated method stub
		final RoomController roomController = new RoomController(session);
		return roomController.setAccessStatus(roomId, access);
	}
	
	@Override
	public OssResponse addDevices(Session session, long roomId, List<Device> devices) {
		final RoomController roomController = new RoomController(session);
		OssResponse ossResponse = roomController.addDevices(roomId,devices);
		return ossResponse;
	}

	@Override
	public OssResponse addDevice(Session session, long roomId, String macAddress, String name) {
		final RoomController roomController = new RoomController(session);
		return roomController.addDevice(roomId,macAddress,name);
	}
	
	@Override
	public OssResponse deleteDevices(Session session, long roomId, List<Long> deviceIds) {
		// TODO Auto-generated method stub
		final RoomController roomController = new RoomController(session);
		return roomController.deleteDevices(roomId,deviceIds);
	}
	
	@Override
	public OssResponse deleteDevice(Session session, long roomId, Long deviceId) {
		final RoomController roomController = new RoomController(session);
		List<Long> deviceIds = new ArrayList<Long>();
		deviceIds.add(deviceId);
		return roomController.deleteDevices(roomId,deviceIds);
	}

	@Override
	public List<Device> getDevices(Session session, long roomId) {
		final RoomController roomController = new RoomController(session);
		return roomController.getDevices(roomId);
	}

	@Override
	public HWConf getHwConf(Session session, long roomId) {
		final RoomController roomController = new RoomController(session);
		return roomController.getHWConf(roomId);
	}

	@Override
	public OssResponse setHwConf(Session session, long roomId, long hwConfId) {
		final RoomController roomController = new RoomController(session);
		return roomController.setHWConf(roomId,hwConfId);
	}

	@Override
	public List<Room> search(Session session, String search) {
		final RoomController roomController = new RoomController(session);
		return roomController.search(search);
	}

	@Override
	public List<Room> getRoomsToRegister(Session session) {
		final RoomController roomController = new RoomController(session);
		return roomController.getAllToRegister();
	}

	@Override
	public List<Room> getRooms(Session session, List<Long> roomIds) {
		final RoomController roomController = new RoomController(session);
		return roomController.getRooms(roomIds);
	}

	@Override
	public OssResponse modify(Session session, Room room) {
		return new RoomController(session).modify(room);
	}

	@Override
	public OssResponse setDefaultPrinter(Session session, long roomId, Long deviceId) {
		return new RoomController(session).setDefaultPrinter(roomId, deviceId);
	}

	@Override
	public OssResponse deleteDefaultPrinter(Session session, long roomId) {
		return new RoomController(session).deleteDefaultPrinter(roomId);
	}

	@Override
	public Device getDefaultPrinter(Session session, long roomId) {
		return new RoomController(session).getById(roomId).getDefaultPrinter();
	}

	@Override
	public OssResponse setAvailablePrinters(Session session, long roomId, List<Long> deviceIds) {
		return new RoomController(session).setAvailablePrinters(roomId, deviceIds);
	}

	@Override
	public OssResponse addAvailablePrinters(Session session, long roomId, long deviceId) {
		return new RoomController(session).addAvailablePrinter(roomId, deviceId);
	}

	@Override
	public OssResponse deleteAvailablePrinters(Session session, long roomId, long deviceId) {
		return new RoomController(session).deleteAvailablePrinter(roomId, deviceId);
	}

	@Override
	public List<Device> getAvailablePrinters(Session session, long roomId) {
		return new RoomController(session).getById(roomId).getAvailablePrinters();
	}

	@Override
	public List<String> getAvailableRoomActions(Session session, Long roomId) {
		return new EducationController(session).getAvailableRoomActions(roomId);
	}

	@Override
	public OssResponse manageRoom(Session session, Long roomId, String action) {
		return new EducationController(session).manageRoom(roomId,action, null);
	}

	@Override
	public OssResponse manageRoom(Session session, Long roomId, String action, Map<String, String> actionContent) {
		return new EducationController(session).manageRoom(roomId,action, actionContent);
	}

}
