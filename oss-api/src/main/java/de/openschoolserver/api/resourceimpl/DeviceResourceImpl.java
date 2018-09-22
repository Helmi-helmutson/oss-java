
/* (c) 2017 Péter Varkoly <peter@varkoly.de> - all rights reserved */
package de.openschoolserver.api.resourceimpl;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.ws.rs.WebApplicationException;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

import de.openschoolserver.api.resources.DeviceResource;
import de.openschoolserver.dao.Device;
import de.openschoolserver.dao.OssResponse;
import de.openschoolserver.dao.Session;
import de.openschoolserver.dao.controller.DHCPConfig;
import de.openschoolserver.dao.controller.DeviceController;
import de.openschoolserver.dao.controller.EducationController;
import de.openschoolserver.dao.controller.SessionController;

public class DeviceResourceImpl implements DeviceResource {

	@Override
	public Device getById(Session session, long deviceId) {
	    final DeviceController deviceController = new DeviceController(session);
	    final Device device = deviceController.getById(deviceId);
	    if (device == null) {
	            throw new WebApplicationException(404);
	    }
	    return device;
	}

	@Override
	public List<Device> getAll(Session session) {
		final DeviceController deviceController = new DeviceController(session);
		final List<Device> devices = deviceController.getAll();
		if (devices == null) {
	            throw new WebApplicationException(404);
	    }
		return devices;
	}

	@Override
	public Device getByIP(Session session, String IP) {
		final DeviceController deviceController = new DeviceController(session);
		final Device device = deviceController.getByIP(IP);
		if (device == null) {
	            throw new WebApplicationException(404);
	    }
		return device;
	}

	@Override
	public Device getByMAC(Session session, String MAC) {
		final DeviceController deviceController = new DeviceController(session);
		final Device device = deviceController.getByMAC(MAC);
		if (device == null) {
	            throw new WebApplicationException(404);
	    }
		return device;
	}

	@Override
	public List<Device> getByType(Session session, String type) {
		final DeviceController deviceController = new DeviceController(session);
		final List<Device> devices = deviceController.getByTpe(type);
		if (devices == null) {
	            throw new WebApplicationException(404);
	    }
		return devices;
	}

	@Override
	public Device getByName(Session session, String Name) {
		final DeviceController deviceController = new DeviceController(session);
		final Device device = deviceController.getByName(Name);
		if (device == null) {
            throw new WebApplicationException(404);
		}
		return device;
	}

	@Override
	public Device getDefaultPrinter(Session session, long deviceId) {
		final DeviceController deviceController = new DeviceController(session);
		return deviceController.getDefaultPrinter(deviceId);
	}

	@Override
	public List<Device> getAvailablePrinters(Session session, long deviceId) {
		final DeviceController deviceController = new DeviceController(session);
		return deviceController.getAvailablePrinters(deviceId);
	}

	@Override
	public List<String> getLoggedInUsers(Session session, String IP) {
		return new DeviceController(session).getLoggedInUsers(IP);
	}

	@Override
	public String getFirstLoggedInUser(String IP) {
		Session session  = new SessionController().getLocalhostSession();
		DeviceController deviceController = new DeviceController(session);
		Device device = deviceController.getByIP(IP);
		if( device != null && !device.getLoggedIn().isEmpty() ) {
			if( ! deviceController.checkConfig(device.getLoggedIn().get(0), "disableInternet")) {
				return device.getLoggedIn().get(0).getUid();
			}
		}
		return "";
	}

	@Override
	public List<String> getLoggedInUsers(Session session, long deviceId) {
		final DeviceController deviceController = new DeviceController(session);
		return deviceController.getLoggedInUsers(deviceId);
	}

	@Override
	public OssResponse setDefaultPrinter(Session session, long deviceId, long defaultPrinterId) {
		final DeviceController deviceController = new DeviceController(session);
		return deviceController.setDefaultPrinter(deviceId,defaultPrinterId);
	}

	@Override
	public OssResponse setAvailablePrinters(Session session, long deviceId, List<Long> availablePrinters) {
		final DeviceController deviceController = new DeviceController(session);
		return deviceController.setAvailablePrinters(deviceId,availablePrinters);
	}

	@Override
	public OssResponse setLoggedInUsers(Session session, String IP, String userName) {
		final DeviceController deviceController = new DeviceController(session);
		return deviceController.setLoggedInUsers(IP, userName);
	}

	@Override
	public OssResponse removeLoggedInUser(Session session, String IP, String userName) {
		final DeviceController deviceController = new DeviceController(session);
		return deviceController.removeLoggedInUser(IP, userName);
	}
	
	@Override
	public void refreshConfig(Session session) {
		new DHCPConfig(session).Create();
	}

	@Override
	public List<Device> search(Session session, String search) {
		final DeviceController deviceController = new DeviceController(session);
		return deviceController.search(search);
	}

	@Override
	public OssResponse modify(Session session, Device device) {
		return new DeviceController(session).modify(device);
	}

	@Override
	public OssResponse delete(Session session, long deviceId) {
		final DeviceController deviceController = new DeviceController(session);
		return deviceController.delete(deviceId,true);
	}

	@Override
	public List<Device> getDevices(Session session, List<Long> deviceIds) {
		final DeviceController deviceController = new DeviceController(session);
		return deviceController.getDevices(deviceIds);
	}

	@Override
	public List<Device> getByHWConf(Session session, Long id) {
		return new DeviceController(session).getByHWConf(id);
	}

	@Override
	public OssResponse importDevices(Session session, InputStream fileInputStream,
			FormDataContentDisposition contentDispositionHeader) {
		return new DeviceController(session).importDevices(fileInputStream, contentDispositionHeader);
	}

	@Override
	public String getHostnameByIP(Session session, String IP) {
		Device device = this.getByIP(session, IP);
		if( device == null ) {
			return "";
		}
		return device.getName();
	}

	@Override
	public String getHostnameByMAC(Session session, String MAC) {
		Device device = this.getByMAC(session, MAC);
		if( device == null ) {
			return "";
		}
		return device.getName();
	}

	@Override
	public List<String> getAvailableDeviceActions(Session session, Long deviceId) {
		return new EducationController(session).getAvailableDeviceActions(deviceId);
	}

	@Override
	public OssResponse manageDevice(Session session, Long deviceId, String action) {
		return new DeviceController(session).manageDevice(deviceId,action,null);
	}

	@Override
	public OssResponse manageDevice(Session session, Long deviceId, String action, Map<String, String> actionContent) {
		return new DeviceController(session).manageDevice(deviceId,action,actionContent);
	}

	@Override
	public OssResponse cleanUpLoggedIn(Session session) {
		return new DeviceController(session).cleanUpLoggedIn();
	}

	@Override
	public String getDefaultPrinter(Session session, String IP) {
		DeviceController deviceController = new DeviceController(session);
		Device device = deviceController.getByIP(IP);
		if( device == null ) {
			return "";
		}
		Device printer = deviceController.getDefaultPrinter(device.getId());
		if( printer != null ) {
			return printer.getName();
		}
		return "";
	}

	@Override
	public String getAvailablePrinters(Session session, String IP) {
		DeviceController deviceController = new DeviceController(session);
		Device device = deviceController.getByIP(IP);
		if( device == null ) {
			return "";
		}
		List<String> printers = new ArrayList<String>();
		for( Device printer : deviceController.getAvailablePrinters(device.getId()) ) {
			printers.add(printer.getName());
		}
		return String.join(" ", printers);
	}

	@Override
	public String getAllUsedDevices(Session session, Long saltClientOnly) {
		return new DeviceController(session).getAllUsedDevices(saltClientOnly);
	}

	
}
