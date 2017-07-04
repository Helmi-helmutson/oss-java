/* (c) 2017 P��ter Varkoly <peter@varkoly.de> - all rights reserved */
package de.openschoolserver.api.resourceimpl;

import de.openschoolserver.dao.AccessInRoom;
import de.openschoolserver.dao.Device;
import de.openschoolserver.dao.HWConf;
import de.openschoolserver.dao.Room;
import de.openschoolserver.dao.Session;
import de.openschoolserver.dao.controler.DeviceControler;
import de.openschoolserver.dao.controler.RoomControler;
import de.openschoolserver.dao.Response;
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
       final RoomControler roomControler = new RoomControler(session);
       final Room room = roomControler.getById(roomId);
        if (room == null) {
            throw new WebApplicationException(404);
        }
        return room;
    }

    @Override
    public List<Room> getAll(Session session) {
        final RoomControler roomControler = new RoomControler(session);
        final List<Room> rooms = roomControler.getAll();
        return rooms;
    }

    @Override
    public Response delete(Session session, long roomId) {
        // TODO Auto-generated method stub
    	final RoomControler roomControler = new RoomControler(session);
    	return roomControler.delete(roomId);
    }

    @Override
    public Response add(Session session, Room room) {
    	final RoomControler roomControler = new RoomControler(session);
    	return roomControler.add(room);
    }

    @Override
    public List<String> getAvailableIPAddresses(Session session, long roomId) {
        final RoomControler roomControler = new RoomControler(session);
        final List<String> ips = roomControler.getAvailableIPAddresses(roomId);
        if ( ips == null) {
            throw new WebApplicationException(404);
        }
        return ips;
    }
    
    @Override
	public List<String> getAvailableIPAddresses(Session session, long roomId, long count) {
    	final RoomControler roomControler = new RoomControler(session);
        final List<String> ips = roomControler.getAvailableIPAddresses(roomId,count);
        if ( ips == null) {
            throw new WebApplicationException(404);
        }
        return ips;
	}

	@Override
	public String getNextRoomIP(Session session, String network, int netMask) {
		final RoomControler roomControler = new RoomControler(session);
		final String nextIP = roomControler.getNextRoomIP(network, netMask);
        if ( nextIP == null) {
            throw new WebApplicationException(404);
        }
		return nextIP;
	}

	@Override
	public List<Map<String, String>> getLoggedInUsers(Session session, long roomId) {
		// TODO Auto-generated method stub
		final RoomControler roomControler = new RoomControler(session);
		final List<Map<String, String>> users = roomControler.getLoggedInUsers(roomId);
        if ( users == null) {
            throw new WebApplicationException(404);
        }
        return users;
	}

	@Override
	public List<AccessInRoom> getAccessList(Session session, long roomId) {
		// TODO Auto-generated method stub
		final RoomControler roomControler = new RoomControler(session);
		final List<AccessInRoom> accesses = roomControler.getAccessList(roomId);
        if ( accesses == null) {
            throw new WebApplicationException(404);
        }
        return accesses;
	}

	@Override
	public Response setAccessList(Session session, long roomId, List<AccessInRoom> accessList) {
		// TODO Auto-generated method stub
		final RoomControler roomControler = new RoomControler(session);
		return roomControler.setAccessList(roomId, accessList);
	}

	@Override
	public Response setScheduledAccess(Session session) {
		// TODO Auto-generated method stub
		final RoomControler roomControler = new RoomControler(session);
		return roomControler.setScheduledAccess();
	}

	@Override
	public List<AccessInRoom> getAccessStatus(Session session) {
		// TODO Auto-generated method stub
		final RoomControler roomControler = new RoomControler(session);
		final List<AccessInRoom> accesses = roomControler.getAccessStatus();
		return accesses;
	}

	@Override
	public AccessInRoom getAccessStatus(Session session, long roomId) {
		// TODO Auto-generated method stub
		final RoomControler roomControler = new RoomControler(session);
		final AccessInRoom access = roomControler.getAccessStatus(roomId);
		return access;
	}

	@Override
	public Response setAccessStatus(Session session, long roomId, AccessInRoom access) {
		// TODO Auto-generated method stub
		final RoomControler roomControler = new RoomControler(session);
		return roomControler.setAccessStatus(roomId, access);
	}
	
	@Override
	public Response addDevices(Session session, long roomId, List<Device> devices) {
		final RoomControler roomControler = new RoomControler(session);
		Response response = roomControler.addDevices(roomId,devices);
		return response;
	}

	@Override
	public Response addDevice(Session session, long roomId, String macAddress, String name) {
		final RoomControler roomControler = new RoomControler(session);
		return roomControler.addDevice(roomId,macAddress,name);
	}
	
	@Override
	public Response deleteDevices(Session session, long roomId, List<Long> deviceIds) {
		// TODO Auto-generated method stub
		final RoomControler roomControler = new RoomControler(session);
		return roomControler.deleteDevices(roomId,deviceIds);
	}
	
	@Override
	public Response deleteDevice(Session session, long roomId, Long deviceId) {
		final RoomControler roomControler = new RoomControler(session);
		List<Long> deviceIds = new ArrayList<Long>();
		deviceIds.add(deviceId);
		return roomControler.deleteDevices(roomId,deviceIds);
	}

	@Override
	public List<Device> getDevices(Session session, long roomId) {
		final RoomControler roomControler = new RoomControler(session);
		return roomControler.getDevices(roomId);
	}

	@Override
	public HWConf getHwConf(Session session, long roomId) {
		final RoomControler roomControler = new RoomControler(session);
		return roomControler.getHWConf(roomId);
	}

	@Override
	public Response setHwConf(Session session, long roomId, long hwConfId) {
		final RoomControler roomControler = new RoomControler(session);
		return roomControler.setHWConf(roomId,hwConfId);
	}

	@Override
	public List<Room> search(Session session, String search) {
		final RoomControler roomControler = new RoomControler(session);
		return roomControler.search(search);
	}

	@Override
	public List<Room> getRoomsToRegister(Session session) {
		final RoomControler roomControler = new RoomControler(session);
		return roomControler.getAllToRegister();
	}

	@Override
	public List<Room> getRooms(Session session, List<Long> roomIds) {
		final RoomControler roomControler = new RoomControler(session);
		return roomControler.getRooms(roomIds);
	}

}
