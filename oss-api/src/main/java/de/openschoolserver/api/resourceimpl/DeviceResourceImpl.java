/* (c) 2017 Péter Varkoly <peter@varkoly.de> - all rights reserved */
package de.openschoolserver.api.resourceimpl;

import java.util.List;


import javax.ws.rs.WebApplicationException;

import de.openschoolserver.api.resources.DeviceResource;
import de.openschoolserver.dao.Device;
import de.openschoolserver.dao.Response;
import de.openschoolserver.dao.Session;
import de.openschoolserver.dao.controller.DeviceController;
import de.openschoolserver.dao.controller.DHCPConfig;

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
	public String getDefaultPrinter(Session session, long deviceId) {
		final DeviceController deviceController = new DeviceController(session);
		return deviceController.getDefaultPrinter(deviceId);
	}

	@Override
	public List<String> getAvailablePrinters(Session session, long deviceId) {
		final DeviceController deviceController = new DeviceController(session);
		return deviceController.getAvailablePrinters(deviceId);
	}

	@Override
	public List<String> getLoggedInUsers(Session session, String IP) {
		final DeviceController deviceController = new DeviceController(session);
		return deviceController.getLoggedInUsers(IP);
	}

	@Override
	public List<String> getLoggedInUsers(Session session, long deviceId) {
		final DeviceController deviceController = new DeviceController(session);
		return deviceController.getLoggedInUsers(deviceId);
	}

	@Override
	public Response setDefaultPrinter(Session session, long deviceId, long defaultPrinterId) {
		final DeviceController deviceController = new DeviceController(session);
		return deviceController.setDefaultPrinter(deviceId,defaultPrinterId);
	}

	@Override
	public Response setAvailablePrinters(Session session, long deviceId, List<Long> availablePrinters) {
		final DeviceController deviceController = new DeviceController(session);
		return deviceController.setAvailablePrinters(deviceId,availablePrinters);
	}

	@Override
	public Response addLoggedInUser(Session session, String IP, String userName) {
		final DeviceController deviceController = new DeviceController(session);
		return deviceController.addLoggedInUser(IP, userName);
	}

	@Override
	public Response removeLoggedInUser(Session session, String IP, String userName) {
		final DeviceController deviceController = new DeviceController(session);
		return deviceController.removeLoggedInUser(IP, userName);
	}
	
	@Override
	public void refreshConfig(Session session) {
		final DHCPConfig dhcpConfig = new DHCPConfig(session);
		dhcpConfig.Create();
	}
}
