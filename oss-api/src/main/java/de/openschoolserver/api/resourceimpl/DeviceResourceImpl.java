
/* (c) 2017 Péter Varkoly <peter@varkoly.de> - all rights reserved */
package de.openschoolserver.api.resourceimpl;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.ws.rs.WebApplicationException;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

import de.openschoolserver.api.resources.DeviceResource;
import de.openschoolserver.dao.Device;
import de.openschoolserver.dao.OSSMConfig;
import de.openschoolserver.dao.OssResponse;
import de.openschoolserver.dao.Printer;
import de.openschoolserver.dao.Session;
import de.openschoolserver.dao.controller.DHCPConfig;
import de.openschoolserver.dao.controller.DeviceController;
import de.openschoolserver.dao.controller.EducationController;
import de.openschoolserver.dao.controller.SessionController;
import de.openschoolserver.dao.internal.CommonEntityManagerFactory;

public class DeviceResourceImpl implements DeviceResource {

	private EntityManager em;

	public DeviceResourceImpl() {
		super();
		this.em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
	}

	protected void finalize()
	{
	   this.em.close();
	}

	@Override
	public Device getById(Session session, long deviceId) {
	    final DeviceController deviceController = new DeviceController(session,this.em);
	    final Device device = deviceController.getById(deviceId);
	    if (device == null) {
	            throw new WebApplicationException(404);
	    }
	    return device;
	}

	@Override
	public List<Device> getAll(Session session) {
		final DeviceController deviceController = new DeviceController(session,this.em);
		final List<Device> devices = deviceController.getAll();
		if (devices == null) {
	            throw new WebApplicationException(404);
	    }
		return devices;
	}

	@Override
	public String getAllNames(Session session) {
		final DeviceController deviceController = new DeviceController(session,this.em);
		StringBuilder devices = new StringBuilder();
		for( Device device : deviceController.getAll() ) {
			devices.append(device.getName()).append(deviceController.getNl());
		}
		return devices.toString();
	}

	@Override
	public Device getByIP(Session session, String IP) {
		final DeviceController deviceController = new DeviceController(session,this.em);
		final Device device = deviceController.getByIP(IP);
		if (device == null) {
	            throw new WebApplicationException(404);
	    }
		return device;
	}

	@Override
	public Device getByMAC(Session session, String MAC) {
		final DeviceController deviceController = new DeviceController(session,this.em);
		final Device device = deviceController.getByMAC(MAC);
		if (device == null) {
	            throw new WebApplicationException(404);
	    }
		return device;
	}

	@Override
	public Device getByName(Session session, String Name) {
		final DeviceController deviceController = new DeviceController(session,this.em);
		final Device device = deviceController.getByName(Name);
		if (device == null) {
            throw new WebApplicationException(404);
		}
		return device;
	}

	@Override
	public Printer getDefaultPrinter(Session session, long deviceId) {
		final DeviceController deviceController = new DeviceController(session,this.em);
		return deviceController.getDefaultPrinter(deviceId);
	}

	@Override
	public List<Printer> getAvailablePrinters(Session session, long deviceId) {
		final DeviceController deviceController = new DeviceController(session,this.em);
		return deviceController.getAvailablePrinters(deviceId);
	}

	@Override
	public List<String> getLoggedInUsers(Session session, String IP) {
		return new DeviceController(session,this.em).getLoggedInUsers(IP);
	}

	@Override
	public String getFirstLoggedInUser(String IP) {
		Session session  = new SessionController(em).getLocalhostSession();
		DeviceController deviceController = new DeviceController(session,this.em);
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
		final DeviceController deviceController = new DeviceController(session,this.em);
		return deviceController.getLoggedInUsers(deviceId);
	}

	@Override
	public OssResponse setDefaultPrinter(Session session, long deviceId, long defaultPrinterId) {
		final DeviceController deviceController = new DeviceController(session,this.em);
		return deviceController.setDefaultPrinter(deviceId,defaultPrinterId);
	}

	@Override
	public OssResponse deleteDefaultPrinter(Session session, long deviceId) {
		return new DeviceController(session,this.em).deleteDefaultPrinter(deviceId);
	}

	@Override
	public OssResponse addAvailablePrinters(Session session, long deviceId, long printerId) {
		return new DeviceController(session,this.em).addAvailablePrinter(deviceId, printerId);
	}

	@Override
	public OssResponse deleteAvailablePrinters(Session session, long deviceId, long printerId) {
		return new DeviceController(session,this.em).deleteAvailablePrinter(deviceId, printerId);
	}
	@Override
	public OssResponse setLoggedInUsers(Session session, String IP, String userName) {
		final DeviceController deviceController = new DeviceController(session,this.em);
		return deviceController.setLoggedInUsers(IP, userName);
	}

	@Override
	public OssResponse deleteLoggedInUser(Session session, String IP, String userName) {
		final DeviceController deviceController = new DeviceController(session,this.em);
		return deviceController.removeLoggedInUser(IP, userName);
	}

	@Override
	public void refreshConfig(Session session) {
		new DHCPConfig(session,this.em).Create();
	}

	@Override
	public List<Device> search(Session session, String search) {
		final DeviceController deviceController = new DeviceController(session,this.em);
		return deviceController.search(search);
	}

	@Override
	public OssResponse modify(Session session, Device device) {
		return new DeviceController(session,this.em).modify(device);
	}

	@Override
	public OssResponse delete(Session session, long deviceId) {
		final DeviceController deviceController = new DeviceController(session,this.em);
		return deviceController.delete(deviceId,true);
	}

	@Override
	public List<Device> getDevices(Session session, List<Long> deviceIds) {
		final DeviceController deviceController = new DeviceController(session,this.em);
		return deviceController.getDevices(deviceIds);
	}

	@Override
	public List<Device> getByHWConf(Session session, Long id) {
		return new DeviceController(session,this.em).getByHWConf(id);
	}

	@Override
	public OssResponse importDevices(Session session, InputStream fileInputStream,
			FormDataContentDisposition contentDispositionHeader) {
		return new DeviceController(session,this.em).importDevices(fileInputStream, contentDispositionHeader);
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
		return new EducationController(session,this.em).getAvailableDeviceActions(deviceId);
	}

	@Override
	public OssResponse manageDevice(Session session, Long deviceId, String action) {
		return new DeviceController(session,this.em).manageDevice(deviceId,action,null);
	}

	@Override
	public OssResponse manageDevice(Session session, String deviceName, String action) {
		return new DeviceController(session,this.em).manageDevice(deviceName,action,null);
	}

	@Override
	public OssResponse manageDevice(Session session, Long deviceId, String action, Map<String, String> actionContent) {
		return new DeviceController(session,this.em).manageDevice(deviceId,action,actionContent);
	}

	@Override
	public OssResponse cleanUpLoggedIn(Session session) {
		return new DeviceController(session,this.em).cleanUpLoggedIn();
	}

	@Override
	public String getDefaultPrinter(Session session, String IP) {
		DeviceController deviceController = new DeviceController(session,this.em);
		Device device =  deviceController.getByIP(IP);
		if( device == null ) {
			return "";
		}
		Printer printer = deviceController.getDefaultPrinter(device.getId());
		if( printer != null ) {
			return printer.getName();
		}
		return "";
	}

	@Override
	public String getAvailablePrinters(Session session, String IP) {
		DeviceController deviceController = new DeviceController(session,this.em);
		Device device = deviceController.getByIP(IP);
		if( device == null ) {
			return "";
		}
		List<String> printers = new ArrayList<String>();
		for( Printer printer : deviceController.getAvailablePrinters(device.getId()) ) {
			printers.add(printer.getName());
		}
		return String.join(" ", printers);
	}

	@Override
	public String getAllUsedDevices(Session session, Long saltClientOnly) {
		return new DeviceController(session,this.em).getAllUsedDevices(saltClientOnly);
	}

	@Override
	public List<OSSMConfig> getDHCP(Session session, Long deviceId) {
		List<OSSMConfig> dhcpParameters = new ArrayList<OSSMConfig>();
		DeviceController deviceController = new DeviceController(session,this.em);
		Device device = deviceController.getById(deviceId);
		for(OSSMConfig config : deviceController.getMConfigObjects(device, "dhcpStatements") ) {
			dhcpParameters.add(config);
		}
		for(OSSMConfig config : deviceController.getMConfigObjects(device, "dhcpOptions") ) {
			dhcpParameters.add(config);
		}
		return dhcpParameters;
	}

	@Override
	public OssResponse addDHCP(Session session, Long deviceId, OSSMConfig dhcpParameter) {
		if( !dhcpParameter.getKeyword().equals("dhcpStatements") && !dhcpParameter.getKeyword().equals("dhcpOptions") ) {
			return new OssResponse(session,"ERROR","Bad DHCP parameter.");
		}
		DeviceController deviceController = new DeviceController(session,this.em);
		Device device = deviceController.getById(deviceId);
		OssResponse ossResponse = deviceController.addMConfig(device, dhcpParameter.getKeyword(), dhcpParameter.getValue());
		if( ossResponse.getCode().equals("ERROR") ) {
			return ossResponse;
		}
		Long dhcpParameterId = ossResponse.getObjectId();
		ossResponse = new DHCPConfig(session,this.em).Test();
		if( ossResponse.getCode().equals("ERROR") ) {
			deviceController.deleteMConfig(null, dhcpParameterId);
			return ossResponse;
		}
		new DHCPConfig(session,this.em).Create();
		return new OssResponse(session,"OK","DHCP Parameter was added succesfully");
	}

	@Override
	public OssResponse deleteDHCP(Session session, Long deviceId, Long parameterId) {
		DeviceController deviceController = new DeviceController(session,this.em);
		Device device = deviceController.getById(deviceId);
		return deviceController.deleteMConfig(device,parameterId);
	}
}
