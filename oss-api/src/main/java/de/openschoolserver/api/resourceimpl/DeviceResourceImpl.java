
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

	public DeviceResourceImpl() {
	}

	@Override
	public Device getById(Session session, Long deviceId) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		final DeviceController deviceController = new DeviceController(session,em);
		final Device device = deviceController.getById(deviceId);
		em.close();
		if (device == null) {
		        throw new WebApplicationException(404);
		}
		return device;
	}

	@Override
	public List<Device> getAll(Session session) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		final DeviceController deviceController = new DeviceController(session,em);
		final List<Device> devices = deviceController.getAll();
		em.close();
		if (devices == null) {
	            throw new WebApplicationException(404);
		}
		return devices;
	}

	@Override
	public String getAllNames(Session session) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		final DeviceController deviceController = new DeviceController(session,em);
		StringBuilder devices = new StringBuilder();
		for( Device device : deviceController.getAll() ) {
			devices.append(device.getName()).append(deviceController.getNl());
		}
		em.close();
		return devices.toString();
	}

	@Override
	public Device getByIP(Session session, String IP) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		final DeviceController deviceController = new DeviceController(session,em);
		final Device device = deviceController.getByIP(IP);
		em.close();
		if (device == null) {
	            throw new WebApplicationException(404);
	    }
		return device;
	}

	@Override
	public Device getByMAC(Session session, String MAC) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		final DeviceController deviceController = new DeviceController(session,em);
		final Device device = deviceController.getByMAC(MAC);
		em.close();
		if (device == null) {
	            throw new WebApplicationException(404);
	    }
		return device;
	}

	@Override
	public Device getByName(Session session, String Name) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		final DeviceController deviceController = new DeviceController(session,em);
		final Device device = deviceController.getByName(Name);
		em.close();
		if (device == null) {
            throw new WebApplicationException(404);
		}
		return device;
	}

	@Override
	public Printer getDefaultPrinter(Session session, Long deviceId) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		final DeviceController deviceController = new DeviceController(session,em);
		Printer resp = deviceController.getDefaultPrinter(deviceId);
		em.close();
		return resp;
		
	}

	@Override
	public List<Printer> getAvailablePrinters(Session session, Long deviceId) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		final DeviceController deviceController = new DeviceController(session,em);
		List<Printer> resp = deviceController.getAvailablePrinters(deviceId);
		em.close();
		return resp;
	}

	@Override
	public List<String> getLoggedInUsers(Session session, String IP) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		List<String> resp = new DeviceController(session,em).getLoggedInUsers(IP);
		em.close();
		return resp;
	}

	@Override
	public String getFirstLoggedInUser(String IP) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		Session session  = new SessionController(em).getLocalhostSession();
		DeviceController deviceController = new DeviceController(session,em);
		Device device = deviceController.getByIP(IP);
		if( device != null && !device.getLoggedIn().isEmpty() ) {
			if( ! deviceController.checkConfig(device.getLoggedIn().get(0), "disableInternet")) {
				em.close();
				return device.getLoggedIn().get(0).getUid();
			}
		}
		em.close();
		return "";
	}

	@Override
	public List<String> getLoggedInUsers(Session session, Long deviceId) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		final DeviceController deviceController = new DeviceController(session,em);
		List<String> resp = deviceController.getLoggedInUsers(deviceId);
		em.close();
		return resp;
	}

	@Override
	public OssResponse setDefaultPrinter(Session session, Long deviceId, Long defaultPrinterId) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		final DeviceController deviceController = new DeviceController(session,em);
		OssResponse resp = deviceController.setDefaultPrinter(deviceId,defaultPrinterId);
		em.close();
		return resp;
	}

	@Override
	public OssResponse deleteDefaultPrinter(Session session, Long deviceId) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		OssResponse resp = new DeviceController(session,em).deleteDefaultPrinter(deviceId);
		em.close();
		return resp;
	}

	@Override
	public OssResponse addAvailablePrinters(Session session, Long deviceId, Long printerId) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		OssResponse resp = new DeviceController(session,em).addAvailablePrinter(deviceId, printerId);
		em.close();
		return resp;
	}

	@Override
	public OssResponse deleteAvailablePrinters(Session session, Long deviceId, Long printerId) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		OssResponse resp = new DeviceController(session,em).deleteAvailablePrinter(deviceId, printerId);
		em.close();
		return resp;
	}
	@Override
	public OssResponse setLoggedInUsers(Session session, String IP, String userName) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		final DeviceController deviceController = new DeviceController(session,em);
		OssResponse resp = deviceController.setLoggedInUsers(IP, userName);
		em.close();
		return resp;
	}

	@Override
	public OssResponse deleteLoggedInUser(Session session, String IP, String userName) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		final DeviceController deviceController = new DeviceController(session,em);
		OssResponse resp =  deviceController.removeLoggedInUser(IP, userName);
		em.close();
		return resp;
	}

	@Override
	public void refreshConfig(Session session) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		new DHCPConfig(session,em).Create();
		em.close();
	}

	@Override
	public List<Device> search(Session session, String search) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		final DeviceController deviceController = new DeviceController(session,em);
		List<Device> resp = deviceController.search(search);
		em.close();
		return resp;
	}

	@Override
	public OssResponse modify(Session session, Device device) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		OssResponse resp =  new DeviceController(session,em).modify(device);
		em.close();
		return resp;
	}

	@Override
	public OssResponse modify(Session session, Long deviceId, Device device) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		device.setId(deviceId);
		OssResponse resp =  new DeviceController(session,em).modify(device);
		em.close();
		return resp;
	}

	@Override
	public OssResponse delete(Session session, Long deviceId) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		final DeviceController deviceController = new DeviceController(session,em);
		OssResponse resp = deviceController.delete(deviceId,true);
		em.close();
		return resp;
	}

	@Override
	public List<Device> getDevices(Session session, List<Long> deviceIds) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		final DeviceController deviceController = new DeviceController(session,em);
		List<Device> resp = deviceController.getDevices(deviceIds);
		em.close();
		return resp;
	}

	@Override
	public List<Device> getByHWConf(Session session, Long id) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		List<Device> resp = new DeviceController(session,em).getByHWConf(id);
		em.close();
		return resp;
	}

	@Override
	public OssResponse importDevices(Session session, InputStream fileInputStream,
			FormDataContentDisposition contentDispositionHeader) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		OssResponse resp = new DeviceController(session,em).importDevices(fileInputStream, contentDispositionHeader);
		em.close();
		return resp;
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
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		List<String> resp = new EducationController(session,em).getAvailableDeviceActions(deviceId);
		em.close();
		return resp;
	}

	@Override
	public OssResponse manageDevice(Session session, Long deviceId, String action) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		OssResponse resp = new DeviceController(session,em).manageDevice(deviceId,action,null);
		em.close();
		return resp;
	}

	@Override
	public OssResponse manageDevice(Session session, String deviceName, String action) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		OssResponse resp = new DeviceController(session,em).manageDevice(deviceName,action,null);
		em.close();
		return resp;
	}

	@Override
	public OssResponse manageDevice(Session session, Long deviceId, String action, Map<String, String> actionContent) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		OssResponse resp = new DeviceController(session,em).manageDevice(deviceId,action,actionContent);
		em.close();
		return resp;
	}

	@Override
	public OssResponse cleanUpLoggedIn(Session session) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		OssResponse resp = new DeviceController(session,em).cleanUpLoggedIn();
		em.close();
		return resp;
	}

	@Override
	public String getDefaultPrinter(Session session, String IP) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		DeviceController deviceController = new DeviceController(session,em);
		Device device =  deviceController.getByIP(IP);
		if( device == null ) {
			em.close();
			return "";
		}
		Printer printer = deviceController.getDefaultPrinter(device.getId());
		em.close();
		if( printer != null ) {
			return printer.getName();
		}
		return "";
	}

	@Override
	public String getAvailablePrinters(Session session, String IP) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		DeviceController deviceController = new DeviceController(session,em);
		Device device = deviceController.getByIP(IP);
		if( device == null ) {
			em.close();
			return "";
		}
		List<String> printers = new ArrayList<String>();
		for( Printer printer : deviceController.getAvailablePrinters(device.getId()) ) {
			printers.add(printer.getName());
		}
		em.close();
		return String.join(" ", printers);
	}

	@Override
	public String getAllUsedDevices(Session session, Long saltClientOnly) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		String resp = new DeviceController(session,em).getAllUsedDevices(saltClientOnly);
		em.close();
		return resp;
	}

	@Override
	public List<OSSMConfig> getDHCP(Session session, Long deviceId) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		List<OSSMConfig> dhcpParameters = new ArrayList<OSSMConfig>();
		DeviceController deviceController = new DeviceController(session,em);
		Device device = deviceController.getById(deviceId);
		for(OSSMConfig config : deviceController.getMConfigObjects(device, "dhcpStatements") ) {
			dhcpParameters.add(config);
		}
		for(OSSMConfig config : deviceController.getMConfigObjects(device, "dhcpOptions") ) {
			dhcpParameters.add(config);
		}
		em.close();
		return dhcpParameters;
	}

	@Override
	public OssResponse addDHCP(Session session, Long deviceId, OSSMConfig dhcpParameter) {
		if( !dhcpParameter.getKeyword().equals("dhcpStatements") && !dhcpParameter.getKeyword().equals("dhcpOptions") ) {
			return new OssResponse(session,"ERROR","Bad DHCP parameter.");
		}
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		DeviceController deviceController = new DeviceController(session,em);
		Device device = deviceController.getById(deviceId);
		OssResponse ossResponse = deviceController.addMConfig(device, dhcpParameter.getKeyword(), dhcpParameter.getValue());
		if( ossResponse.getCode().equals("ERROR") ) {
			em.close();
			return ossResponse;
		}
		Long dhcpParameterId = ossResponse.getObjectId();
		ossResponse = new DHCPConfig(session,em).Test();
		if( ossResponse.getCode().equals("ERROR") ) {
			deviceController.deleteMConfig(null, dhcpParameterId);
			em.close();
			return ossResponse;
		}
		new DHCPConfig(session,em).Create();
		em.close();
		return new OssResponse(session,"OK","DHCP Parameter was added succesfully");
	}

	@Override
	public OssResponse deleteDHCP(Session session, Long deviceId, Long parameterId) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		DeviceController deviceController = new DeviceController(session,em);
		Device device = deviceController.getById(deviceId);
		OssResponse resp = deviceController.deleteMConfig(device,parameterId);
		em.close();
		return resp;
	}
}
