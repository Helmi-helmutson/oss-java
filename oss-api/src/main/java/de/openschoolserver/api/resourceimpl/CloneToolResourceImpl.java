/* (c) 2017 Péter Varkoly <peter@varkoly.de> - all rights reserved */
package de.openschoolserver.api.resourceimpl;

import de.openschoolserver.dao.HWConf;


import de.openschoolserver.dao.Clone;
import de.openschoolserver.dao.Device;
import de.openschoolserver.dao.Partition;
import de.openschoolserver.dao.OssResponse;
import de.openschoolserver.dao.Session;
import de.openschoolserver.dao.Room;
import de.openschoolserver.dao.controller.CloneToolController;
import de.openschoolserver.dao.controller.Config;
import de.openschoolserver.dao.controller.RoomController;
import de.openschoolserver.dao.controller.SessionController;
import de.openschoolserver.dao.controller.DeviceController;
import de.openschoolserver.api.resources.CloneToolResource;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.UriInfo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class CloneToolResourceImpl implements CloneToolResource {

	@Override
	public String getHWConf(UriInfo ui, HttpServletRequest req) {
		Session session  = new SessionController().getLocalhostSession();
		DeviceController deviceController = new DeviceController(session);
		Device device = deviceController.getByIP(req.getRemoteAddr());
		if( device != null && device.getHwconf() != null ) {
			return Long.toString(device.getHwconf().getId());
		}
		return "";
    }

	@Override
	public String isMaster(Session session) {
		if( session.getDevice() == null ) {
			return "";
		}
		final CloneToolController cloneToolController = new CloneToolController(session);
		if( cloneToolController.checkConfig(session.getDevice(),"isMaster" ) ) {
			return "true";
		}
		return "";
	}
	
	@Override
	public String isMaster(Session session, Long deviceId) {
		final DeviceController deviceController = new DeviceController(session);
		Device device = deviceController.getById(deviceId);
		if( device != null &&  deviceController.checkConfig(device,"isMaster" ) ) {
			return "true";
		}
		return "";
	}

	@Override
	public Long getMaster(Session session, Long hwconfId) {
		CloneToolController cloneToolController = new CloneToolController(session);
		HWConf hwconf = cloneToolController.getById(hwconfId);
		if( hwconf == null ) {
			return null;
		}
		for( Device device : hwconf.getDevices() ) {
			if( cloneToolController.checkConfig(device, "isMaster") ) {
				return device.getId();
			}
		}
		return null;
	}

	@Override
	public OssResponse setMaster(Session session, Long deviceId, int isMaster) {
		final DeviceController deviceController = new DeviceController(session);
		Device device = deviceController.getById(deviceId);
		if( device == null ) {
			return new OssResponse(session,"ERRO","Device was not found.");
		}
		if( deviceController.checkConfig(device,"isMaster" ) && isMaster == 0) {
			return deviceController.deleteConfig(device, "isMaster");
		}
		if( isMaster == 1 ) {
			for( Device dev : device.getHwconf().getDevices() ) {
				if( !dev.equals(device) ) {
					deviceController.deleteConfig(dev,"isMaster");
				}
			}
		}
		if( ! deviceController.checkConfig(device,"isMaster" ) && isMaster == 1 ) {
			return deviceController.setConfig(device, "isMaster","true");
		}

		return new OssResponse(session,"OK","Nothing to change.");
	}

	@Override
	public OssResponse setMaster(Session session, int isMaster) {
		return this.setMaster(session, session.getDevice().getId(), isMaster);
	}

	@Override
	public HWConf getById(Session session, Long hwconfId) {
		final HWConf hwconf = new CloneToolController(session).getById(hwconfId);
		if (hwconf == null) {
			throw new WebApplicationException(404);
		}
		return hwconf;
	}

	@Override
	public String getPartitions(UriInfo ui,
	        HttpServletRequest req, Long hwconfId) {
		Session session  = new SessionController().getLocalhostSession();
		return new CloneToolController(session).getPartitions(hwconfId);
	}
	
	@Override
	public String getDescription(Session session, Long hwconfId) {
		return new CloneToolController(session).getDescription(hwconfId);
	}

	@Override
	public Partition getPartition(Session session, Long hwconfId, String partition) {
		return new CloneToolController(session).getPartition(hwconfId, partition);
	}

	@Override
	public String getConfigurationValue(UriInfo ui,
	        HttpServletRequest req,
	        Long hwconfId,
	        String partition,
	        String key) {
		Session session  = new SessionController().getLocalhostSession();
		return new CloneToolController(session).getConfigurationValue(hwconfId,partition,key);
	}

	@Override
	public OssResponse addHWConf(Session session, HWConf hwconf) {
		return new CloneToolController(session).addHWConf(hwconf);
	}

	@Override
	public OssResponse modifyHWConf(Session session, Long hwconfId, HWConf hwconf) {
		return new CloneToolController(session).modifyHWConf(hwconfId, hwconf);
	}

	@Override
	public OssResponse addPartition(Session session, Long hwconfId, Partition partition) {
		return new CloneToolController(session).addPartitionToHWConf(hwconfId, partition);
	}

	@Override
	public OssResponse addPartition(Session session, Long hwconfId, String partitionName) {
		return new CloneToolController(session).addPartitionToHWConf(hwconfId, partitionName );
	}
	
	@Override
	public OssResponse setConfigurationValue(Session session, Long hwconfId, String partitionName, String key, String value) {
		return new CloneToolController(session).setConfigurationValue(hwconfId,partitionName,key,value);
	}

	@Override
	public OssResponse delete(Session session, Long hwconfId) {
		return new CloneToolController(session).delete(hwconfId);
	}

	@Override
	public OssResponse deletePartition(Session session, Long hwconfId, String partitionName) {
		return new CloneToolController(session).deletePartition(hwconfId,partitionName);
	}

	@Override
	public OssResponse deleteConfigurationValue(Session session, Long hwconfId, String partitionName, String key) {
		return new CloneToolController(session).deleteConfigurationValue(hwconfId,partitionName,key);
	}

	@Override
	public List<HWConf> getAllHWConf(Session session) {
		return new CloneToolController(session).getAllHWConf();
	}

	@Override
	public String getRoomsToRegister(Session session) {
		StringBuilder roomList = new StringBuilder();
		for( Room room : new RoomController(session).getAllToRegister() ) {
			roomList.append(room.getId()).append("##").append(room.getName()).append(" ");
		}
		return roomList.toString();
	}

	@Override
	public String getAvailableIPAddresses(Session session, long roomId) {
		StringBuilder roomList = new StringBuilder();
		for( String name : new RoomController(session).getAvailableIPAddresses(roomId, 0) ) {
			roomList.append(name.replaceFirst(" ","/")).append(" ").append(name.split(" ")[1]).append(" ");
		}
		return roomList.toString();
	}

	@Override
	public OssResponse addDevice(Session session, long roomId, String macAddress, String IP, String name) {
		RoomController rc = new RoomController(session);
		Room room = rc.getById(roomId);
		Device device = new Device();
		device.setName(name);
		device.setMac(macAddress);
		device.setIp(IP);
		if( room.getHwconf() != null ) {
			device.setHwconfId(room.getHwconf().getId());
		}
		ArrayList<Device> devices = new ArrayList<Device>();
		devices.add(device);
		return rc.addDevices(roomId, devices);
	}
	
	@Override
	public OssResponse startCloning(Session session, Long hwconfId, Clone parameters) {
		return new CloneToolController(session).startCloning(hwconfId,parameters);
	}

	@Override
	public OssResponse startCloning(Session session, Long hwconfId, int multiCast) {
		return new CloneToolController(session).startCloning("hwconf", hwconfId, multiCast);
	}

	@Override
	public OssResponse startCloningInRoom(Session session, Long roomId, int multiCast) {
		return new CloneToolController(session).startCloning("room", roomId, multiCast);
	}

	@Override
	public OssResponse startCloningOnDevice(Session session, Long deviceId) {
		return new CloneToolController(session).startCloning("device", deviceId, 0);
	}

	@Override
	public OssResponse stopCloning(Session session, Long hwconfId) {
		return new CloneToolController(session).stopCloning("hwconf",hwconfId);
	}

	@Override
	public OssResponse stopCloningInRoom(Session session, Long roomId) {
		return new CloneToolController(session).stopCloning("room",roomId);
	}

	@Override
	public OssResponse stopCloningOnDevice(Session session, Long deviceId) {
		return new CloneToolController(session).stopCloning("device",deviceId);
	}

	@Override
	public String resetMinion(Session session) {
		if( session.getDevice() == null ) {
			throw new WebApplicationException(404);
		}
		return new CloneToolController(session).resetMinion(session.getDevice().getId());
	}

	@Override
	public String resetMinion(Session session, Long deviceId) {
		return new CloneToolController(session).resetMinion(deviceId);
	}

	@Override
	public OssResponse stopCloningOnDevice(Session session, String deviceIP) {
		Device device = new DeviceController(session).getByIP(deviceIP);
		String pathPxe  = String.format("/srv/tftp/pxelinux.cfg/01-%s", device.getMac().toLowerCase().replace(":", "-"));
		String pathElilo= String.format("/srv/tftp/%s.conf", device.getMac().toUpperCase().replace(":", "-"));
		try {
			Files.deleteIfExists(Paths.get(pathPxe));
			Files.deleteIfExists(Paths.get(pathElilo));
		}catch( IOException e ) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public String[] getMulticastDevices(Session session) {
		Config config = new Config("/etc/sysconfig/dhcpd","DHCPD_");
		return config.getConfigValue("INTERFACE").split("\\s+");
	}

	@Override
	public OssResponse startMulticast(Session session, Long partitionId, String networkDevice) {
		return new CloneToolController(session).startMulticast(partitionId,networkDevice);
	}

	@Override
	public OssResponse modifyPartition(Session session, Long partitionId, Partition partition) {
		return new CloneToolController(session).modifyPartition(partitionId, partition);
	}

	@Override
	public String getFqhn(UriInfo ui, HttpServletRequest req) {
		Session session  = new SessionController().getLocalhostSession();
		DeviceController deviceController = new DeviceController(session);
		Device device = deviceController.getByIP(req.getRemoteAddr());
		if( device != null ) {
			return device.getName().concat(".").concat(deviceController.getConfigValue("DOMAIN"));
		}
		return "";
	}

	@Override
	public OssResponse importHWConfs(Session session, List<HWConf> hwconfs) {
		CloneToolController  cloneToolController = new CloneToolController(session);
		OssResponse ossResponse = null;
		for( HWConf hwconf : hwconfs ) {
			ossResponse = cloneToolController.addHWConf(hwconf);
			if( ossResponse.getCode().equals("ERROR")) {
				return ossResponse;
			}
		}
		return ossResponse;
	}
}
