/* (c) 2017 Péter Varkoly <peter@varkoly.de> - all rights reserved */
package de.openschoolserver.api.resourceimpl;

import de.openschoolserver.dao.AccessInRoom;
import de.openschoolserver.dao.Device;
import de.openschoolserver.dao.Room;
import de.openschoolserver.dao.Session;
import de.openschoolserver.dao.Response;
import de.openschoolserver.dao.controller.DeviceController;
import de.openschoolserver.dao.controller.RoomController;
import de.openschoolserver.api.resources.RoomResource;


import javax.ws.rs.WebApplicationException;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

public class RoomRescourceImpl implements RoomResource {
	

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
    public Response delete(Session session, long roomId) {
        // TODO Auto-generated method stub
    	final RoomController roomController = new RoomController(session);
    	return roomController.delete(roomId);
    }

    @Override
    public Response add(Session session, Room room) {
        // TODO Auto-generated method stub
    	final RoomController roomController = new RoomController(session);
    	return roomController.add(room);
    }

    @Override
    public List<String> getAvailableIPAddresses(Session session, long roomId) {
        // TODO Auto-generated method stub
        final RoomController roomController = new RoomController(session);
        final List<String> ips = roomController.getAvailableIPAddresses(roomId);
        if ( ips == null) {
            throw new WebApplicationException(404);
        }
        return ips;
    }

	@Override
	public String getNextRoomIP(Session session, String network, int netMask) {
		// TODO Auto-generated method stub
		final RoomController roomController = new RoomController(session);
		final String nextIP = roomController.getNextRoomIP(network, netMask);
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
	public Response setAccessList(Session session, long roomId, List<AccessInRoom> accessList) {
		// TODO Auto-generated method stub
		final RoomController roomController = new RoomController(session);
		return roomController.setAccessList(roomId, accessList);
	}

	@Override
	public Response setScheduledAccess(Session session) {
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
	public Response setAccessStatus(Session session, long roomId, AccessInRoom access) {
		// TODO Auto-generated method stub
		final RoomController roomController = new RoomController(session);
		return roomController.setAccessStatus(roomId, access);
	}
	
	@Override
	public Response addDevices(Session session, long roomId, List<Device> devices) {
		final RoomController roomController = new RoomController(session);
		return roomController.addDevices(roomId,devices);
	}

	@Override
	public Response deleteDevices(Session session, long roomId, List<Long> deviceIds) {
		// TODO Auto-generated method stub
		final RoomController roomController = new RoomController(session);
		return roomController.deleteDevices(roomId,deviceIds);
	}
	
	@Override
	public Response deleteDevice(Session session, long roomId, Long deviceId) {
		// TODO Auto-generated method stub
		final RoomController roomController = new RoomController(session);
		List<Long> deviceIds = new ArrayList<Long>();
		deviceIds.add(deviceId);
		return roomController.deleteDevices(roomId,deviceIds);
	}
}
