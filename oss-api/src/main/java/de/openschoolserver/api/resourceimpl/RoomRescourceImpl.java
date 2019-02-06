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
import de.openschoolserver.dao.internal.CommonEntityManagerFactory;
import de.openschoolserver.api.resources.RoomResource;

import javax.persistence.EntityManager;
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

	public RoomRescourceImpl() {
	}

	@Override
	public Room getById(Session session, long roomId) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		Room room = new RoomController(session,em).getById(roomId);
		if (room == null) {
			throw new WebApplicationException(404);
		}
		return room;
	}

	@Override
	public List<Room> getAll(Session session) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		final RoomController roomController = new RoomController(session,em);
		final List<Room> rooms = roomController.getAllToUse();
		return rooms;
	}
	
	@Override
	public String getAllNames(Session session) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		final RoomController roomController = new RoomController(session,em);
		StringBuilder rooms = new StringBuilder();
		for( Room room : roomController.getAllToUse() ) {
			rooms.append(room.getName()).append(roomController.getNl());
		}
		return rooms.toString();
	}
	
	@Override
	public List<Room> allWithControl(Session session) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		final RoomController roomController = new RoomController(session,em);
		final List<Room> rooms = roomController.getAllWithControl();
		return rooms;
	}
	
	@Override
	public List<Room> allWithFirewallControl(Session session) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		final RoomController roomController = new RoomController(session,em);
		final List<Room> rooms = roomController.getAllWithFirewallControl();
		return rooms;
	}
	
	@Override
	public OssResponse delete(Session session, long roomId) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		final RoomController roomController = new RoomController(session,em);
		return roomController.delete(roomId);
	}
	
	@Override
	public OssResponse add(Session session, Room room) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		final RoomController roomController = new RoomController(session,em);
		return roomController.add(room);
	}
	
	@Override
	public List<String> getAvailableIPAddresses(Session session, long roomId) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		final RoomController roomController = new RoomController(session,em);
		final List<String> ips = roomController.getAvailableIPAddresses(roomId);
		if ( ips == null) {
			throw new WebApplicationException(404);
		}
		return ips;
	}

	@Override
	public List<String> getAvailableIPAddresses(Session session, long roomId, long count) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		final RoomController roomController = new RoomController(session,em);
		final List<String> ips = roomController.getAvailableIPAddresses(roomId,count);
		if ( ips == null) {
		    throw new WebApplicationException(404);
		}
		return ips;
	}

	@Override
	public String getNextRoomIP(Session session, String network, int netMask) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		final RoomController roomController = new RoomController(session,em);
		final String nextIP = roomController.getNextRoomIP(network, netMask);
		if ( nextIP == null) {
			throw new WebApplicationException(404);
		}
		return nextIP;
	}

	@Override
	public List<Map<String, String>> getLoggedInUsers(Session session, long roomId) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		final RoomController roomController = new RoomController(session,em);
		final List<Map<String, String>> users = roomController.getLoggedInUsers(roomId);
		if ( users == null) {
			throw new WebApplicationException(404);
		}
		return users;
	}

	@Override
	public List<AccessInRoom> getAccessList(Session session, long roomId) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		final RoomController roomController = new RoomController(session,em);
		final List<AccessInRoom> accesses = roomController.getAccessList(roomId);
		if ( accesses == null) {
			throw new WebApplicationException(404);
		}
		return accesses;
	}


	@Override
	public OssResponse addAccessList(Session session, long roomId, AccessInRoom accessList) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		return new RoomController(session,em).addAccessList(roomId,accessList);
	}

	@Override
	public OssResponse deleteAccessList(Session session, long accessInRoomId) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		return new RoomController(session,em).deleteAccessList(accessInRoomId);
	}

	@Override
	public OssResponse setScheduledAccess(Session session) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		final RoomController roomController = new RoomController(session,em);
		return roomController.setScheduledAccess();
	}

	@Override
	public OssResponse setDefaultAccess(Session session) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		final RoomController roomController = new RoomController(session,em);
		return roomController.setDefaultAccess();
	}

	@Override
	public List<AccessInRoom> getAccessStatus(Session session) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		final RoomController roomController = new RoomController(session,em);
		final List<AccessInRoom> accesses = roomController.getAccessStatus();
		return accesses;
	}

	@Override
	public AccessInRoom getAccessStatus(Session session, long roomId) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		return new RoomController(session,em).getAccessStatus(roomId);
	}

	@Override
	public OssResponse setAccessStatus(Session session, long roomId, AccessInRoom access) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		return new RoomController(session,em).setAccessStatus(roomId, access);
	}

	@Override
	public OssResponse addDevices(Session session, long roomId, List<Device> devices) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		final RoomController roomController = new RoomController(session,em);
		OssResponse ossResponse = roomController.addDevices(roomId,devices);
		return ossResponse;
	}

	@Override
	public OssResponse addDevice(Session session, long roomId, String macAddress, String name) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		final RoomController roomController = new RoomController(session,em);
		return roomController.addDevice(roomId,macAddress,name);
	}

	@Override
	public OssResponse deleteDevices(Session session, long roomId, List<Long> deviceIds) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		final RoomController roomController = new RoomController(session,em);
		return roomController.deleteDevices(roomId,deviceIds);
	}

	@Override
	public OssResponse deleteDevice(Session session, long roomId, Long deviceId) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		final RoomController roomController = new RoomController(session,em);
		List<Long> deviceIds = new ArrayList<Long>();
		deviceIds.add(deviceId);
		return roomController.deleteDevices(roomId,deviceIds);
	}

	@Override
	public List<Device> getDevices(Session session, long roomId) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		final RoomController roomController = new RoomController(session,em);
		return roomController.getDevices(roomId);
	}

	@Override
	public HWConf getHwConf(Session session, long roomId) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		final RoomController roomController = new RoomController(session,em);
		return roomController.getHWConf(roomId);
	}

	@Override
	public OssResponse setHwConf(Session session, long roomId, long hwConfId) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		final RoomController roomController = new RoomController(session,em);
		return roomController.setHWConf(roomId,hwConfId);
	}

	@Override
	public List<Room> search(Session session, String search) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		final RoomController roomController = new RoomController(session,em);
		return roomController.search(search);
	}

	@Override
	public List<Room> getRoomsToRegister(Session session) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		final RoomController roomController = new RoomController(session,em);
		return roomController.getAllToRegister();
	}

	@Override
	public List<Room> getRooms(Session session, List<Long> roomIds) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		final RoomController roomController = new RoomController(session,em);
		return roomController.getRooms(roomIds);
	}

	@Override
	public OssResponse modify(Session session, Room room) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		return new RoomController(session,em).modify(room);
	}

	@Override
	public OssResponse setDefaultPrinter(Session session, Long roomId, Long printerIds) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		return new RoomController(session,em).setDefaultPrinter(roomId, printerIds);
	}

	@Override
	public OssResponse setDefaultPrinter(Session session, String roomName, String printerName) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		return new RoomController(session,em).setDefaultPrinter(roomName, printerName);
	}

	@Override
	public OssResponse deleteDefaultPrinter(Session session, long roomId) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		return new RoomController(session,em).deleteDefaultPrinter(roomId);
	}

	@Override
	public Printer getDefaultPrinter(Session session, long roomId) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		return new RoomController(session,em).getById(roomId).getDefaultPrinter();
	}

	@Override
	public OssResponse setAvailablePrinters(Session session, long roomId, List<Long> printerIds) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		return new RoomController(session,em).setAvailablePrinters(roomId, printerIds);
	}

	@Override
	public OssResponse addAvailablePrinters(Session session, long roomId, long printerId) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		return new RoomController(session,em).addAvailablePrinter(roomId, printerId);
	}

	@Override
	public OssResponse deleteAvailablePrinters(Session session, long roomId, long printerId) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		return new RoomController(session,em).deleteAvailablePrinter(roomId, printerId);
	}

	@Override
	public List<Printer> getAvailablePrinters(Session session, long roomId) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		return new RoomController(session,em).getById(roomId).getAvailablePrinters();
	}

	@Override
	public List<String> getAvailableRoomActions(Session session, Long roomId) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		return new EducationController(session,em).getAvailableRoomActions(roomId);
	}

	@Override
	public OssResponse manageRoom(Session session, Long roomId, String action) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		return new EducationController(session,em).manageRoom(roomId,action, null);
	}

	@Override
	public OssResponse manageRoom(Session session, Long roomId, String action, Map<String, String> actionContent) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		return new EducationController(session,em).manageRoom(roomId,action, actionContent);
	}

	@Override
	public OssResponse importRooms(Session session, InputStream fileInputStream,
			FormDataContentDisposition contentDispositionHeader) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		return new RoomController(session,em).importRooms(fileInputStream, contentDispositionHeader);
	}

	@Override
	public List<OSSMConfig> getDHCP(Session session, Long roomId) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		List<OSSMConfig> dhcpParameters = new ArrayList<OSSMConfig>();
		RoomController roomController = new RoomController(session,em);
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
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		RoomController roomController = new RoomController(session,em);
		Room room = roomController.getById(roomId);
		OssResponse ossResponse = roomController.addMConfig(room, dhcpParameter.getKeyword(), dhcpParameter.getValue());
		if( ossResponse.getCode().equals("ERROR") ) {
			return ossResponse;
		}
		Long dhcpParameterId = ossResponse.getObjectId();
		ossResponse = new DHCPConfig(session,em).Test();
		if( ossResponse.getCode().equals("ERROR") ) {
			roomController.deleteMConfig(null, dhcpParameterId);
			return ossResponse;
		}
		new DHCPConfig(session,em).Create();
		return new OssResponse(session,"OK","DHCP Parameter was added succesfully");
	}

	@Override
	public OssResponse deleteDHCP(Session session, Long roomId, Long parameterId) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		RoomController roomController = new RoomController(session,em);
		Room room = roomController.getById(roomId);
		return roomController.deleteMConfig(room,parameterId);
	}
}
