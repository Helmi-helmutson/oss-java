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
    public List<Room> getByIds(Session session, List<Integer> roomIds) {
        final RoomController roomController = new RoomController(session);
        final List<Room> rooms = roomController.getByIds(roomIds);
        return rooms;
    	
    }

}
