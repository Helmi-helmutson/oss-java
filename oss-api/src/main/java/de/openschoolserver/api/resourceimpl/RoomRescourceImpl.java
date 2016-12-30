/* (c) 2017 Péter Varkoly <peter@varkoly.de> - all rights reserved */
package de.openschoolserver.api.resourceimpl;

import de.openschoolserver.dao.AccessInRoom;
import de.openschoolserver.dao.Room;
import de.openschoolserver.dao.Session;
import de.openschoolserver.dao.controller.RoomController;
import de.openschoolserver.api.resources.RoomResource;


import javax.ws.rs.WebApplicationException;
import java.util.List;
import java.util.Map;

public class RoomRescourceImpl implements RoomResource {
	

    @Override
    public Room getById(Session session, int roomID) {
       final RoomController roomController = new RoomController(session);
       final Room room = roomController.getById(roomID);
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
    public boolean delete(Session session, int roomID) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean add(Session session, Room room) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public List<String> getAvailableIPAddresses(Session session, int roomID) {
        // TODO Auto-generated method stub
        final RoomController roomController = new RoomController(session);
        final List<String> ips = roomController.getAvailableIPAddresses(roomID);
        if ( ips == null) {
            throw new WebApplicationException(404);
        }
        return ips;
    }

	@Override
	public String getNextRoomIP(Session session, int netMask) {
		// TODO Auto-generated method stub
		final RoomController roomController = new RoomController(session);
		final String nextIP = roomController.getNextRoomIP(netMask);
		return nextIP;
	}

	@Override
	public List<Map<String, String>> getLoggedInUsers(Session session, int roomID) {
		// TODO Auto-generated method stub
		final RoomController roomController = new RoomController(session);
		final List<Map<String, String>> users = roomController.getLoggedInUsers(roomID);
        if ( users == null) {
            throw new WebApplicationException(404);
        }
        return users;
	}

	@Override
	public List<AccessInRoom> getAccessList(Session session, int roomID) {
		// TODO Auto-generated method stub
		final RoomController roomController = new RoomController(session);
		final List<AccessInRoom> users = roomController.getAccessList(roomID);
        if ( users == null) {
            throw new WebApplicationException(404);
        }
        return users;
	}

	@Override
	public boolean setAccessList(Session session, int roomID, List<AccessInRoom> accessList) {
		// TODO Auto-generated method stub
		final RoomController roomController = new RoomController(session);
		roomController.setAccessList(roomID, accessList);
		return false;
	}

	@Override
	public void setScheduledAccess(Session session) {
		// TODO Auto-generated method stub
		final RoomController roomController = new RoomController(session);
	}

	@Override
	public List<AccessInRoom> getAccessStatus(Session session) {
		// TODO Auto-generated method stub
		final RoomController roomController = new RoomController(session);
		return null;
	}

	@Override
	public AccessInRoom getAccessStatus(Session session, int roomID) {
		// TODO Auto-generated method stub
		final RoomController roomController = new RoomController(session);
		return null;
	}

	@Override
	public void setAccessStatus(Session session, int roomID, AccessInRoom access) {
		// TODO Auto-generated method stub
		final RoomController roomController = new RoomController(session);
	}

}
