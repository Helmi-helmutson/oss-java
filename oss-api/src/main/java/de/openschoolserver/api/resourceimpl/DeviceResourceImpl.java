/* (c) 2017 Péter Varkoly <peter@varkoly.de> - all rights reserved */
package de.openschoolserver.api.resourceimpl;

import java.util.List;


import javax.ws.rs.WebApplicationException;

import de.openschoolserver.api.resources.DeviceResource;
import de.openschoolserver.dao.Device;
import de.openschoolserver.dao.Response;
import de.openschoolserver.dao.Session;
import de.openschoolserver.dao.controler.DHCPConfig;
import de.openschoolserver.dao.controler.DeviceControler;

public class DeviceResourceImpl implements DeviceResource {

	@Override
	public Device getById(Session session, long deviceId) {
	    final DeviceControler deviceControler = new DeviceControler(session);
	    final Device device = deviceControler.getById(deviceId);
	    if (device == null) {
	            throw new WebApplicationException(404);
	    }
	    return device;
	}

	@Override
	public List<Device> getAll(Session session) {
		final DeviceControler deviceControler = new DeviceControler(session);
		final List<Device> devices = deviceControler.getAll();
		if (devices == null) {
	            throw new WebApplicationException(404);
	    }
		return devices;
	}

	@Override
	public Device getByIP(Session session, String IP) {
		final DeviceControler deviceControler = new DeviceControler(session);
		final Device device = deviceControler.getByIP(IP);
		if (device == null) {
	            throw new WebApplicationException(404);
	    }
		return device;
	}

	@Override
	public Device getByMAC(Session session, String MAC) {
		final DeviceControler deviceControler = new DeviceControler(session);
		final Device device = deviceControler.getByMAC(MAC);
		if (device == null) {
	            throw new WebApplicationException(404);
	    }
		return device;
	}

	@Override
	public List<Device> getByType(Session session, String type) {
		final DeviceControler deviceControler = new DeviceControler(session);
		final List<Device> devices = deviceControler.getByTpe(type);
		if (devices == null) {
	            throw new WebApplicationException(404);
	    }
		return devices;
	}

	@Override
	public Device getByName(Session session, String Name) {
		final DeviceControler deviceControler = new DeviceControler(session);
		final Device device = deviceControler.getByName(Name);
		if (device == null) {
            throw new WebApplicationException(404);
		}
		return device;
	}

	@Override
	public String getDefaultPrinter(Session session, long deviceId) {
		final DeviceControler deviceControler = new DeviceControler(session);
		return deviceControler.getDefaultPrinter(deviceId);
	}

	@Override
	public List<String> getAvailablePrinters(Session session, long deviceId) {
		final DeviceControler deviceControler = new DeviceControler(session);
		return deviceControler.getAvailablePrinters(deviceId);
	}

	@Override
	public List<String> getLoggedInUsers(Session session, String IP) {
		final DeviceControler deviceControler = new DeviceControler(session);
		return deviceControler.getLoggedInUsers(IP);
	}

	@Override
	public List<String> getLoggedInUsers(Session session, long deviceId) {
		final DeviceControler deviceControler = new DeviceControler(session);
		return deviceControler.getLoggedInUsers(deviceId);
	}

	@Override
	public Response setDefaultPrinter(Session session, long deviceId, long defaultPrinterId) {
		final DeviceControler deviceControler = new DeviceControler(session);
		return deviceControler.setDefaultPrinter(deviceId,defaultPrinterId);
	}

	@Override
	public Response setAvailablePrinters(Session session, long deviceId, List<Long> availablePrinters) {
		final DeviceControler deviceControler = new DeviceControler(session);
		return deviceControler.setAvailablePrinters(deviceId,availablePrinters);
	}

	@Override
	public Response addLoggedInUser(Session session, String IP, String userName) {
		final DeviceControler deviceControler = new DeviceControler(session);
		return deviceControler.addLoggedInUser(IP, userName);
	}

	@Override
	public Response removeLoggedInUser(Session session, String IP, String userName) {
		final DeviceControler deviceControler = new DeviceControler(session);
		return deviceControler.removeLoggedInUser(IP, userName);
	}
	
	@Override
	public void refreshConfig(Session session) {
		final DHCPConfig dhcpConfig = new DHCPConfig(session);
		dhcpConfig.Create();
	}

	@Override
	public List<Device> search(Session session, String search) {
		final DeviceControler deviceControler = new DeviceControler(session);
		return deviceControler.search(search);
	}

	@Override
	public Response modify(Session session, Device device) {
		final DeviceControler deviceControler = new DeviceControler(session);
		return deviceControler.modify(device);
	}

	@Override
	public Response delete(Session session, long deviceId) {
		final DeviceControler deviceControler = new DeviceControler(session);
		return deviceControler.delete(deviceId);
	}

	@Override
	public List<Device> getDevice(Session session, List<Long> deviceIds) {
		final DeviceControler deviceControler = new DeviceControler(session);
		return deviceControler.getDevices(deviceIds);
	}
}
