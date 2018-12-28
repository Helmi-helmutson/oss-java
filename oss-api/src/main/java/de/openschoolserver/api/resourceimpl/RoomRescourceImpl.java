/* (c) 2017 Péter Varkoly <peter@varkoly.de> - all rights reserved */
package de.openschoolserver.api.resourceimpl;

import de.openschoolserver.dao.AccessInRoom;
import de.openschoolserver.dao.Device;
import de.openschoolserver.dao.HWConf;
import de.openschoolserver.dao.OSSMConfig;
import de.openschoolserver.dao.OssResponse;
import de.openschoolserver.dao.Printer;
import de.openschoolserver.dao.Room;
import de.openschoolserver.dao.Session;
import de.openschoolserver.dao.controller.DHCPConfig;
import de.openschoolserver.dao.controller.EducationController;
import de.openschoolserver.dao.controller.RoomController;
import de.openschoolserver.api.resources.RoomResource;


import javax.ws.rs.WebApplicationException;
import java.util.List;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Map;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
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
        final List<Room> rooms = roomController.getAllToUse();
        return rooms;
    }

    @Override
    public String getAllNames(Session session) {
   final RoomController roomController = new RoomController(session);
   StringBuilder rooms = new StringBuilder();
        for( Room room : roomController.getAllToUse() ) {
       rooms.append(room.getName()).append(roomController.getNl());
        }
        return rooms.toString();
    }

    @Override
    public List<Room> allWithControl(Session session) {
        final RoomController roomController = new RoomController(session);
        final List<Room> rooms = roomController.getAllWithControl();
        return rooms;
    }

    @Override
    public List<Room> allWithFirewallControl(Session session) {
        final RoomController roomController = new RoomController(session);
        final List<Room> rooms = roomController.getAllWithFirewallControl();
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
	public OssResponse addAccessList(Session session, long roomId, AccessInRoom accessList) {
		return new RoomController(session).addAccessList(roomId,accessList);
	}

	@Override
	public OssResponse deleteAccessList(Session session, long accessInRoomId) {
		return new RoomController(session).deleteAccessList(accessInRoomId);
	}

	@Override
	public OssResponse setScheduledAccess(Session session) {
		// TODO Auto-generated method stub
		final RoomController roomController = new RoomController(session);
		return roomController.setScheduledAccess();
	}

	@Override
	public OssResponse setDefaultAccess(Session session) {
		// TODO Auto-generated method stub
		final RoomController roomController = new RoomController(session);
		return roomController.setDefaultAccess();
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
		return new RoomController(session).getAccessStatus(roomId);
	}

	@Override
	public OssResponse setAccessStatus(Session session, long roomId, AccessInRoom access) {
		return new RoomController(session).setAccessStatus(roomId, access);
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
	public OssResponse setDefaultPrinter(Session session, long roomId, Long printerIds) {
		return new RoomController(session).setDefaultPrinter(roomId, printerIds);
	}

	@Override
	public OssResponse deleteDefaultPrinter(Session session, long roomId) {
		return new RoomController(session).deleteDefaultPrinter(roomId);
	}

	@Override
	public Printer getDefaultPrinter(Session session, long roomId) {
		return new RoomController(session).getById(roomId).getDefaultPrinter();
	}

	@Override
	public OssResponse setAvailablePrinters(Session session, long roomId, List<Long> printerIds) {
		return new RoomController(session).setAvailablePrinters(roomId, printerIds);
	}

	@Override
	public OssResponse addAvailablePrinters(Session session, long roomId, long printerId) {
		return new RoomController(session).addAvailablePrinter(roomId, printerId);
	}

	@Override
	public OssResponse deleteAvailablePrinters(Session session, long roomId, long printerId) {
		return new RoomController(session).deleteAvailablePrinter(roomId, printerId);
	}

	@Override
	public List<Printer> getAvailablePrinters(Session session, long roomId) {
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

	@Override
	public OssResponse importRooms(Session session, InputStream fileInputStream,
			FormDataContentDisposition contentDispositionHeader) {
		return new RoomController(session).importRooms(fileInputStream, contentDispositionHeader);
	}

	@Override
	public List<OSSMConfig> getDHCP(Session session, Long roomId) {
		List<OSSMConfig> dhcpParameters = new ArrayList<OSSMConfig>();
		RoomController roomController = new RoomController(session);
		Room room = roomController.getById(roomId);
		for(OSSMConfig config : roomController.getMConfigObjects(room, "dhcpStatements") ) {
			dhcpParameters.add(config);
		}
		for(OSSMConfig config : roomController.getMConfigObjects(room, "dhcpOptions") ) {
			dhcpParameters.add(config);
		}
		return dhcpParameters;
	}

	@Override
	public OssResponse addDHCP(Session session, Long roomId, OSSMConfig dhcpParameter) {
		if( !dhcpParameter.getKeyword().equals("dhcpStatements") && !dhcpParameter.getKeyword().equals("dhcpOptions") ) {
			return new OssResponse(session,"ERROR","Bad DHCP parameter.");
		}
		RoomController roomController = new RoomController(session);
		Room room = roomController.getById(roomId);
		OssResponse ossResponse = roomController.addMConfig(room, dhcpParameter.getKeyword(), dhcpParameter.getValue());
		if( ossResponse.getCode().equals("ERROR") ) {
			return ossResponse;
		}
		Long dhcpParameterId = ossResponse.getObjectId();
		ossResponse = new DHCPConfig(session).Test();
		if( ossResponse.getCode().equals("ERROR") ) {
			roomController.deleteMConfig(null, dhcpParameterId);
			return ossResponse;
		}
		new DHCPConfig(session).Create();
		return new OssResponse(session,"OK","DHCP Parameter was added succesfully");
	}

	@Override
	public OssResponse deleteDHCP(Session session, Long roomId, Long parameterId) {
		RoomController roomController = new RoomController(session);
		Room room = roomController.getById(roomId);
		return roomController.deleteMConfig(room,parameterId);
	}
}
