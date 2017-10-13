/* (c) 2017 Péter Varkoly <peter@varkoly.de> - all rights reserved  
 * (c) 2017 EXTIS GmbH www.extis.de - all rights reserved */
package de.openschoolserver.dao.controller;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import de.openschoolserver.dao.Device;
import de.openschoolserver.dao.OssResponse;
import de.openschoolserver.dao.Room;
import de.openschoolserver.dao.Session;
import de.openschoolserver.dao.User;
import de.openschoolserver.dao.tools.*;

@SuppressWarnings( "unchecked" )
public class DeviceController extends Controller {
	
	Logger logger = LoggerFactory.getLogger(DeviceController.class);

	public DeviceController(Session session) {
		super(session);
	}

	/*
	 * Return a device found by the Id
	 */
	public Device getById(long deviceId) {
		EntityManager em = getEntityManager();
		try {
			return em.find(Device.class, deviceId);
		} catch (Exception e) {
			logger.error("DeviceId:" + deviceId + " " + e.getMessage(),e);
			return null;
		} finally {
			em.close();
		}
	}

	/*
	 * Delivers a list of devices wit the given device type
	 */
	public List<Device> getByTpe(String type) {
		EntityManager em = getEntityManager();
		try {
			Query query = em.createNamedQuery("Device.getDeviceByType");
			query.setParameter("deviceType", type);
			return query.getResultList();
		} catch (Exception e) {
			logger.error("DeviceType:" + type + " " + e.getMessage(),e);
			return null;
		} finally {
			em.close();
		}
	}

	/*
	 * Delivers a list of all existing devices
	 */
	public List<Device> getAll() {
		EntityManager em = getEntityManager();
		try {
			Query query = em.createNamedQuery("Device.findAll");
			return query.getResultList();
		} catch (Exception e) {
			logger.error("getAll " + e.getMessage(),e);
			return null;
		} finally {
			em.close();
		}
	}

	/*
	 * Delivers a list of all existing devices
	 */
	public List<Long> getAllId() {
		EntityManager em = getEntityManager();
		try {
			Query query = em.createNamedQuery("Device.findAllId");
			return query.getResultList();
		} catch (Exception e) {
			logger.error("getAllId " + e.getMessage(),e);
			return null;
		} finally {
			em.close();
		}
	}

	/*
	 * Deletes a list of device given by the device Ids.
	 */
	public OssResponse delete(List<Long> deviceIds) {
		EntityManager em = getEntityManager();
		UserController userController = new UserController(this.session);
		try {
			em.getTransaction().begin();
			for( Long deviceId : deviceIds) {
				Device dev = em.find(Device.class, deviceId);
				if( this.isProtected(dev) ) {
					return new OssResponse(this.getSession(),"ERROR","This device must not be deleted: " + dev.getName() );
				}
				if( !em.contains(dev)) {
					dev = em.merge(dev);
				}
				User user = userController.getByUid(dev.getName());
				if( user != null ) {
					userController.delete(user);
				}
				this.startPlugin("delete_device", dev);
				em.remove(dev);
			}
			em.getTransaction().commit();
			em.getEntityManagerFactory().getCache().evictAll();
			DHCPConfig dhcpconfig = new DHCPConfig(this.session);
			dhcpconfig.Create();
			return new OssResponse(this.getSession(),"OK", "Devices were deleted succesfully.");
		} catch (Exception e) {
			logger.error("delete: " + e.getMessage(),e);
			return new OssResponse(this.getSession(),"ERROR", e.getMessage());
		} finally {
			em.close();
		}
	}

	/*
	 * Deletes a device given by the device Ids.
	 */
	public OssResponse delete(Long deviceId, boolean atomic) {
		EntityManager em = getEntityManager();
		try {
			em.getTransaction().begin();
			Device dev = em.find(Device.class, deviceId);
			if( this.isProtected(dev) ) {
				return new OssResponse(this.getSession(),"ERROR","This device must not be deleted: " + dev.getName() );
			}
			em.remove(dev);
			em.getTransaction().commit();
			if( atomic ) {
				em.getEntityManagerFactory().getCache().evictAll();
				this.startPlugin("delete_device", dev);
				DHCPConfig dhcpconfig = new DHCPConfig(this.session);
				dhcpconfig.Create();
			}
			return new OssResponse(this.getSession(),"OK", "Device was deleted succesfully.");
		} catch (Exception e) {
			logger.error("deviceId: " + deviceId + " " + e.getMessage(),e);
			return new OssResponse(this.getSession(),"ERROR", e.getMessage());
		} finally {
			em.close();
		}
	}

	protected String check(Device device,Room room) {
		List<String> error = new ArrayList<String>();
		IPv4Net net = new IPv4Net(room.getStartIP() + "/" + room.getNetMask());
		
		//Check the MAC address
		device.setMac(device.getMac().toUpperCase().replaceAll("-", ":"));
		String name =  this.isMacUnique(device.getMac());
		if( name != "" ){
			error.add("The MAC address '" + device.getMac() + "' will be used allready:" + name );
		}
		if( ! IPv4.validateMACAddress(device.getMac())) {
			error.add("The MAC address is not valid:" + device.getMac() );	
		}
		//Check the name
		if( ! this.isNameUnique(device.getName())){
			error.add("Devices name is not unique. " );
		}
		if( this.checkBadHostName(device.getName())){
			error.add("Devices name contains not allowed characters. " );
		}
		//Check the IP address
		name =  this.isIPUnique(device.getIp());
		if( name != "" ){
			error.add("The IP address will be used allready:" + name );
		}
		if( ! IPv4.validateIPAddress(device.getIp())) {
			error.add("The IP address is not valid:" + device.getIp() );	
		}
		if( !net.contains(device.getIp())) {
			error.add("The IP address is not in the room ip address range.");
		}
		
		if( device.getWlanMac().isEmpty() ) {
			device.setWlanIp("");
		} else {
			//Check the MAC address
			device.setWlanMac(device.getWlanMac().toUpperCase().replaceAll("-", ":"));
			name =  this.isMacUnique(device.getWlanMac());
			if( name != "" ){
				error.add("The WLAN MAC address will be used allready:" + name );
			}
			if( ! IPv4.validateMACAddress(device.getMac())) {
				error.add("The WLAN MAC address is not valid:" + device.getWlanMac() );	
			}
			//Check the IP address
			name =  this.isIPUnique(device.getWlanIp());
			if( name != "" ){
				error.add("The IP address will be used allready:" + name );
			}
			if( ! IPv4.validateIPAddress(device.getWlanIp())) {
				error.add("The IP address is not valid:" + device.getIp() );	
			}
			if( !net.contains(device.getWlanIp())) {
				error.add("The IP address is not in the room ip address range.");
			}
		}
		return String.join(System.lineSeparator(), error);
	}
	
	/*
	 * Creates devices
	 */
	public OssResponse add(List<Device> devices) {
		EntityManager em = getEntityManager();
		try {
			for(Device dev: devices){
				em.getTransaction().begin();
				em.persist(dev);
				em.getTransaction().commit();
			}
			return new OssResponse(this.getSession(),"OK", "Devices were created succesfully.");
		} catch (Exception e) {
			logger.error("add " + e.getMessage(),e);
			return new OssResponse(this.getSession(),"ERROR", e.getMessage());
		} finally {
			em.close();
		}
	}

	/*
	 * Find a device given by the IP address
	 */
	public Device getByIP(String IP) {
		EntityManager em = getEntityManager();
		try {
			Query query = em.createNamedQuery("Device.getByIP");
			query.setParameter("IP", IP);
			return (Device) query.getSingleResult();
		} catch (Exception e) {
			logger.debug("device.getByIP " + IP + " "+ e.getMessage());
			return null;
		} finally {
			em.close();
		}
	}

	/*
	 * Find a device given by the MAC address
	 */
	public Device getByMAC(String MAC) {
		EntityManager em = getEntityManager();
		try {
			Query query = em.createNamedQuery("Device.getByMAC");
			query.setParameter("MAC", MAC);
			return (Device) query.getSingleResult();
		} catch (Exception e) {
			logger.debug("MAC " + MAC + " " + e.getMessage());
			return null;
		} finally {
			em.close();
		}
	}

	/*
	 * Find a device given by the name
	 */
	public Device getByName(String name) {
		EntityManager em = getEntityManager();
		try {
			Query query = em.createNamedQuery("Device.getByName");
			query.setParameter("name", name);
			return (Device) query.getSingleResult();
		} catch (Exception e) {
			logger.debug("name " + name  + " " + e.getMessage());
			return null;
		} finally {
			em.close();
		}
	}

	/*
	 * Search devices given by a substring
	 */
	public List<Device> search(String search) {
		EntityManager em = getEntityManager();
		try {
			Query query = em.createNamedQuery("Device.search");
			query.setParameter("search", search + "%");
			return (List<Device>) query.getResultList();
		} catch (Exception e) {
			logger.debug("search " + search + " " + e.getMessage(),e);
			return null;
		} finally {
			em.close();
		}
	}

	/*
	 * Find the default printer for a device
	 * If no printer was defined by the device find this from the room
	 */
	public String getDefaultPrinter(long deviceId) {
		Device device = this.getById(deviceId);
		Device printer = device.getDefaultPrinter();
		if( printer != null)
			return printer.getName();
		printer = device.getRoom().getDefaultPrinter();
		if( printer != null)
			return printer.getName();
		return "";
	}

	/*
	 * Find the available printer for a device
	 * If no printer was defined by the device find these from the room
	 */
	public List<String> getAvailablePrinters(long deviceId) {
		Device device = this.getById(deviceId);
		List<String> printers   = new ArrayList<String>();
		for( Device printer : device.getAvailablePrinters() ) {
			printers.add(printer.getName());
		}
		if( printers.isEmpty() ){
			for(Device printer : device.getRoom().getAvailablePrinters()){
				printers.add(printer.getName());
			}
		}
		return printers;
	}

	/*
	 * Return the list of users which are logged in on this device
	 */
	public List<String> getLoggedInUsers(String IP) {
		Device device = this.getByIP(IP);
		List<String> users = new ArrayList<String>();
		if( device == null)
			return users;
		for( User user : device.getLoggedIn() )
			users.add(user.getUid());
		//users.add(user.getUid() + " " + user.getGivenName() + " " +user.getSureName());
		return users;
	}

	/*
	 * Return the list of users which are logged in on this device
	 */
	public List<User> getLoggedInUsersObject(String IP) {
		Device device = this.getByIP(IP);
		List<User> users = new ArrayList<User>();
		if( device == null)
			return users;
		for( User user : device.getLoggedIn() )
			users.add(user);
		return users;
	}

	/*
	 * Return the list of users which are logged in on this device
	 */
	public List<String> getLoggedInUsers(Long deviceId) {
		Device device = this.getById(deviceId);
		List<String> users = new ArrayList<String>();
		if( device == null)
			return users;
		for( User user : device.getLoggedIn() )
			users.add(user.getUid());
		return users;
	}

	public OssResponse setDefaultPrinter(long deviceId, long defaultPrinterId) {
		// TODO Auto-generated method stub
		Device device         = this.getById(deviceId);
		Device defaultPrinter = this.getById(defaultPrinterId);
		device.setDefaultPrinter(defaultPrinter);
		EntityManager em = getEntityManager();
		try {
			em.getTransaction().begin();
			em.merge(device);
			em.getTransaction().commit();
		} catch (Exception e) {
			return new OssResponse(this.getSession(),"ERROR", e.getMessage());
		} finally {
			em.close();
		}
		return new OssResponse(this.getSession(),"OK", "Default printer was set succesfully.");
	}

	public OssResponse setAvailablePrinters(long deviceId, List<Long> availablePrinterIds) {
		// TODO Auto-generated method stub
		Device device         = this.getById(deviceId);
		List<Device> availablePrinters = new ArrayList<Device>();
		for( Long aP : availablePrinterIds ) {
			availablePrinters.add(this.getById(aP));
		}
		device.setAvailablePrinters(availablePrinters);
		EntityManager em = getEntityManager();
		try {
			em.getTransaction().begin();
			em.merge(device);
			em.getTransaction().commit();
		} catch (Exception e) {
			return new OssResponse(this.getSession(),"ERROR", e.getMessage());
		} finally {
			em.close();
		}
		return new OssResponse(this.getSession(),"OK", "Available printers were set succesfully.");
	}

	public OssResponse addLoggedInUser(String IP, String userName) {
		Device device = this.getByIP(IP);
		EntityManager em = getEntityManager();
		UserController userController = new UserController(this.session);
		User user = userController.getByUid(userName);
		device.getLoggedIn().add(user);
		user.getLoggedOn().add(device);
		try {
			em.getTransaction().begin();
			em.merge(device);
			em.merge(user);
			em.flush();
			em.getTransaction().commit();
		} catch (Exception e) {
			return new OssResponse(this.getSession(),"ERROR", e.getMessage());
		} finally {
			em.close();
		}
		return new OssResponse(this.getSession(),"OK", "Logged in user was added succesfully:" + device.getName() + ";" + IP + ";" + userName);
	}

	public OssResponse removeLoggedInUser(String IP, String userName) {
		Device device = this.getByIP(IP);
		EntityManager em = getEntityManager();
		UserController userController = new UserController(this.session);
		User user = userController.getByUid(userName);
		List<User> loggedInUsers = device.getLoggedIn();
		loggedInUsers.remove(user);
		device.setLoggedIn(loggedInUsers);
		try {
			em.getTransaction().begin();
			em.merge(device);
			em.getTransaction().commit();
		} catch (Exception e) {
			return new OssResponse(this.getSession(),"ERROR", e.getMessage());
		} finally {
			em.close();
		}
		return new OssResponse(this.getSession(),"OK", "Logged in user was removed succesfully:" + device.getName() + ";" + IP + ";" + userName);
	}
	
	public OssResponse modify(Device device) {
		Device oldDevice = this.getById(device.getId());
		List<String> error = new ArrayList<String>();
		//Check the MAC address
		device.setMac(device.getMac().toUpperCase().replaceAll("-", ":"));
		String name =  this.isMacUnique(device.getMac());
		if( name != "" ){
			error.add("The MAC address '" + device.getMac() + "' will be used allready:" + name );
		}
		if( ! IPv4.validateMACAddress(device.getMac())) {
			error.add("The MAC address is not valid:" + device.getMac() );	
		}
		if( !device.getWlanMac().isEmpty() ) {
			//Check the MAC address
			device.setWlanMac(device.getWlanMac().toUpperCase().replaceAll("-", ":"));
			name =  this.isMacUnique(device.getWlanMac());
			if( name != "" ){
				error.add("The WLAN MAC address will be used allready:" + name );
			}
			if( ! IPv4.validateMACAddress(device.getMac())) {
				error.add("The WLAN MAC address is not valid:" + device.getWlanMac() );	
			}
		}
		if(!error.isEmpty()){
			return new OssResponse(this.getSession(),"ERROR",String.join(System.lineSeparator(),error));
		}
		EntityManager em = getEntityManager();
		try {
			oldDevice.setMac(device.getMac());
			oldDevice.setWlanMac(device.getWlanMac());
			oldDevice.setPlace(device.getPlace());
			oldDevice.setRow(device.getPlace());
			em.getTransaction().begin();
			em.merge(oldDevice);
			em.getTransaction().commit();
		} catch (Exception e) {
			return new OssResponse(this.getSession(),"ERROR", e.getMessage());
		} finally {
			em.close();
		}
		this.startPlugin("modify_device", oldDevice);
		return new OssResponse(this.getSession(),"OK", "Device was modified succesfully.");
	}

	public List<Device> getDevices(List<Long> deviceIds) {
		List<Device> devices = new ArrayList<Device>();
		for( Long id : deviceIds) {
			devices.add(this.getById(id));
		}
		return devices;
	}
}
