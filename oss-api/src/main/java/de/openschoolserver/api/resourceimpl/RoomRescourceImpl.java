package de.openschoolserver.api.resourceimpl;

import de.openschoolserver.dao.Room;
import de.openschoolserver.dao.Session;
import de.openschoolserver.dao.controller.RoomController;
import de.openschoolserver.api.resources.RoomResource;


import javax.ws.rs.WebApplicationException;
import java.util.List;

public class RoomRescourceImpl implements RoomResource {

    @Override
    public Room getById(Session session, int roomId) {
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
    public boolean delete(Session session, int roomId) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean add(Session session, Room room) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public List<String> getAvailableIPAddresses(Session session, int roomId) {
        // TODO Auto-generated method stub
        final RoomController roomController = new RoomController(session);
        final List<String> ips = roomController.getAvailableIPAddresses(roomId);
        if ( ips == null) {
            throw new WebApplicationException(404);
        }
        return ips;
    }

	@Override
	public String getNextRoomIP(Session session, Integer netMask) {
		// TODO Auto-generated method stub
		final RoomController roomController = new RoomController(session);
		final String nextIP = roomController.getNextRoomIP(netMask);
		return nextIP;
	}

}
