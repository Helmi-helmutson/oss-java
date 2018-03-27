/* (c) 2017 Péter Varkoly <peter@varkoly.de> - all rights reserved  
 * (c) 2017 EXTIS GmbH www.extis.de - all rights reserved */
package de.openschoolserver.dao.controller;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.ValidatorFactory;

import de.openschoolserver.dao.Device;
import de.openschoolserver.dao.HWConf;
import de.openschoolserver.dao.OssResponse;
import de.openschoolserver.dao.Room;
import de.openschoolserver.dao.Session;
import de.openschoolserver.dao.User;
import de.openschoolserver.dao.tools.*;

@SuppressWarnings( "unchecked" )
public class DeviceController extends Controller {
	
	Logger logger = LoggerFactory.getLogger(DeviceController.class);
	private List<String>  parameters;

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
			logger.debug("DeviceId:" + deviceId + " " + e.getMessage(),e);
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
		boolean needWriteSalt = false;
		try {
			for( Long deviceId : deviceIds) {
				Device device = em.find(Device.class, deviceId);
				if( device.getHwconf().getDeviceType().equals("FatClient")) {
					needWriteSalt = true;
				}
				this.delete(deviceId, false);
			}
			em.getEntityManagerFactory().getCache().evictAll();
			new DHCPConfig(this.session).Create();
			if( needWriteSalt ) {
				new SoftwareController(this.session).applySoftwareStateToHosts();
			}
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
		UserController userController = new UserController(this.session);
		boolean needWriteSalt = false;
		Device device = em.find(Device.class, deviceId);
		try {
			em.getTransaction().begin();
			if( this.isProtected(device) ) {
				return new OssResponse(this.getSession(),"ERROR","This device must not be deleted.");
			}
			if( !this.mayModify(device) ) {
				return new OssResponse(this.getSession(),"ERROR","You must not delete this device.");
			}
			if( device.getHwconf().getDeviceType().equals("FatClient")) {
				needWriteSalt = true;
				User user = userController.getByUid(device.getName());
				if( user != null ) {
					userController.delete(user);
				}
			}
			this.startPlugin("delete_device", device);
			em.merge(device);
			em.remove(device);
			em.getTransaction().commit();
			if( atomic ) {
				em.getEntityManagerFactory().getCache().evictAll();
				new DHCPConfig(this.session).Create();
				if( needWriteSalt ) {
					new SoftwareController(this.session).applySoftwareStateToHosts();
				}
			}
			return new OssResponse(this.getSession(),"OK", "Device was deleted succesfully.");
		} catch (Exception e) {
			logger.error("device: " + device.getName() + " " + e.getMessage(),e);
			return new OssResponse(this.getSession(),"ERROR", e.getMessage());
		} finally {
			em.close();
		}
	}

	public OssResponse delete(Device device, boolean atomic) {
		return this.delete(device.getId(), atomic);
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
		//Check user parameter
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		for (ConstraintViolation<Device> violation : factory.getValidator().validate(device) ) {
			error.add(violation.getMessage());
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
				dev.setOwner(session.getUser());
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
			if( query.getResultList().isEmpty() ) {
				return null;
			}
			return (Device) query.getResultList().get(0);
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
		if( device == null) {
			return users;
		}
		for( User user : device.getLoggedIn() ) {
			users.add(user.getUid());
			//users.add(user.getUid() + " " + user.getGivenName() + " " +user.getSureName());
		}
		return users;
	}

	/*
	 * Return the list of users which are logged in on this device
	 */
	public List<User> getLoggedInUsersObject(String IP) {
		Device device = this.getByIP(IP);
		return device.getLoggedIn();
	}

	/*
	 * Return the list of users which are logged in on this device
	 */
	public List<String> getLoggedInUsers(Long deviceId) {
		Device device = this.getById(deviceId);
		List<String> users = new ArrayList<String>();
		if( device == null) {
			return users;
		}
		for( User user : device.getLoggedIn() ) {
			users.add(user.getUid());
		}
		return users;
	}

	/*
	 * Import devices from a CSV file. This MUST have following format:
	 * Separator: semicolon
	 * All fields MUST exists. 
	 * Fields: Room; MAC; Serial; Inventary; Locality; HWConf; Owner; Name; IP; WLANMAC; WLANIP; Row; Place; 
	 * Mandatory fields which must not be empty: Room and MAC;
	 */
	public OssResponse importDevices(InputStream fileInputStream,
			FormDataContentDisposition contentDispositionHeader) {
		File file = null;
		List<String> importFile;
		try {
			file = File.createTempFile("oss_uploadFile", ".ossb", new File("/opt/oss-java/tmp/"));
			Files.copy(fileInputStream, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
			importFile = Files.readAllLines(file.toPath());
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			return new OssResponse(this.getSession(),"ERROR", e.getMessage());
		}
		RoomController        roomController      = new RoomController(this.session);
		CloneToolController   cloneToolController = new CloneToolController(this.session);
		UserController        userController      = new UserController(this.session);
		Map<Long,List<Device>> devicesToImport    = new HashMap<>();
		Map<String,Integer> header                = new HashMap<>();
		StringBuilder Error                       = new StringBuilder();
		
		//Initialize the the hash for the rooms
		for( Room r : roomController.getAll() ) {
			devicesToImport.put(r.getId(), new ArrayList<Device>() );
		}
		String headerLine = importFile.get(0);
		int i = 0;
		for(String field : headerLine.split(";")) {
			header.put(field.toLowerCase(), i);
			i++;
		}
		if( !header.containsKey("mac") || !header.containsKey("room")) {
			return new OssResponse(this.getSession(),"ERROR", "MAC and Room are mandatory fields.");
		}
		for(String line : importFile.subList(1, importFile.size()-1) ) {
			String[] values = line.split(";");
			Room room = roomController.getByName(values[header.get("room")]);
			if( room == null ) {
				
			}
			Device device = new Device();
			device.setRoom(room);
			device.setMac(values[header.get("mac")]);
			if(header.containsKey("serial")) {
				device.setSerial(values[header.get("serial")]);
			}
			if(header.containsKey("inventary")) {
				device.setInventary(values[header.get("inventary")]);
			}
			if(header.containsKey("locality")) {
				device.setLocality(values[header.get("locality")]);
			}
			if(header.containsKey("name")) {
				device.setName(values[header.get("name")]);
			}
			if(header.containsKey("wlanmac")) {
				device.setWlanMac(values[header.get("wlanmac")]);
			}
			if(header.containsKey("ip")) {
				device.setIp(values[header.get("ip")]);
			}
			if(header.containsKey("wlanip")) {
				device.setWlanIp(values[header.get("wlanip")]);
			}
			if(header.containsKey("raw")) {
				device.setRow(Integer.parseInt(values[header.get("raw")]));
			}
			if(header.containsKey("serial")) {
				device.setPlace(Integer.parseInt(values[header.get("raw")]));
			}
			if(header.containsKey("owner")) {
				device.setOwner(userController.getByUid(values[header.get("owner")]));
			}
			if(header.containsKey("hwconf")) {
				device.setHwconf(cloneToolController.getByName(values[header.get("hwconf")]));
			}
			devicesToImport.get(room.getId()).add(device);
		}
		
		for( Room r : roomController.getAll() ) {
			if( !devicesToImport.get(r.getId()).isEmpty() ) {
				OssResponse ossResponse = roomController.addDevices(r.getId(), devicesToImport.get(r.getId()));
				if( ossResponse.getCode().equals("ERROR")) {
					Error.append(ossResponse.getValue()).append("<br>");
				}
			}
		}
		if( Error.length() == 0 ) {
			return new OssResponse(this.getSession(),"OK", "Devices were imported succesfully.");
		}
		return new OssResponse(this.getSession(),"ERROR",Error.toString());
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
		parameters = new ArrayList<String>();
		if( device == null ) {
			return new OssResponse(this.getSession(),"ERROR", "There is no registered device with IP: %s",null,IP);
		}
		User user = new UserController(this.session).getByUid(userName);
		if( user == null ) {
			return new OssResponse(this.getSession(),"ERROR", "There is no registered user with uid: %s",null,userName);
		}
		if( user.getLoggedOn().contains(device)) {
			parameters.add(device.getName());
			parameters.add(IP);
			parameters.add(userName);
			return new OssResponse(this.getSession(),"OK", "Logged in user was already added on this device for you:%s;%s;%s",null,parameters);
		}
		device.getLoggedIn().add(user);
		user.getLoggedOn().add(device);
		EntityManager em = getEntityManager();
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
		parameters.add(device.getName());
		parameters.add(IP);
		parameters.add(userName);
		return new OssResponse(this.getSession(),"OK", "Logged in user was added succesfully:%s;%s;%s",null,parameters);
	}

	public OssResponse removeLoggedInUser(String IP, String userName) {
		Device device = this.getByIP(IP);
		parameters = new ArrayList<String>();
		EntityManager em = getEntityManager();
		User user = new UserController(this.session).getByUid(userName);
		if( !user.getLoggedOn().contains(device)) {
			parameters.add(device.getName());
			parameters.add(IP);
			parameters.add(userName);
			return new OssResponse(this.getSession(),"OK", "Logged in user was already removed from this device for you:%s;%s;%s",null,parameters);
		}
		device.getLoggedIn().remove(user);
		user.getLoggedOn().remove(device);
		try {
			em.getTransaction().begin();
			em.merge(device);
			em.merge(user);
			em.getTransaction().commit();
		} catch (Exception e) {
			return new OssResponse(this.getSession(),"ERROR", e.getMessage());
		} finally {
			em.close();
		}
		parameters.add(device.getName());
		parameters.add(IP);
		parameters.add(userName);
		return new OssResponse(this.getSession(),"OK", "Logged in user was removed succesfully:%s;%s;%s",null,parameters);
	}
	
	public OssResponse modify(Device device) {
		Device oldDevice = this.getById(device.getId());
		List<String> error = new ArrayList<String>();
		parameters = new ArrayList<String>();
		/*
		 * If the mac was changed.
		 */
		boolean  macChange = false;
		String   name = "";
		//Check the MAC address
		if( !this.mayModify(oldDevice) ) {
			return new OssResponse(this.getSession(),"ERROR","You must not modify this device: %s",null,oldDevice.getName());
		}
		device.setMac(device.getMac().toUpperCase().replaceAll("-", ":"));
		if( ! oldDevice.getMac().equals(device.getMac())) {
			name =  this.isMacUnique(device.getMac());
			if( name != "" ){
				parameters.add(device.getMac());
				parameters.add(name);
				error.add("The MAC address '%s' will be used allready: %s");
			}
			if( ! IPv4.validateMACAddress(device.getMac())) {
				parameters.add(device.getMac());
				error.add("The MAC address is not valid: '%s'");
			}
			macChange = true;
		}
		if( !device.getWlanMac().isEmpty() ) {
			//Check the MAC address
			device.setWlanMac(device.getWlanMac().toUpperCase().replaceAll("-", ":"));
			if( ! oldDevice.getWlanMac().equals(device.getWlanMac() ) ) {
				name =  this.isMacUnique(device.getWlanMac());
				if( name != "" ){
					parameters.add(device.getWlanMac());
					parameters.add(name);
					error.add("The WLAN MAC address '%s' will be used allready: %s");
				}
				if( ! IPv4.validateMACAddress(device.getMac())) {
					parameters.add(device.getWlanMac());
					error.add("The WLAN MAC address is not valid: '%s'");
				}
			}
			if( oldDevice.getWlanMac().isEmpty() ) {
				//There was no WLAN-Mac befor we need a new IP-Address
				RoomController rc = new RoomController(this.session);
				List<String> wlanIps = rc.getAvailableIPAddresses(oldDevice.getRoom().getId());
				if( wlanIps.isEmpty() ) {
					error.add("The are no more IP addesses in room" );
				} else {
					oldDevice.setWlanIp(wlanIps.get(0));
				}
			}
			macChange = true;
		} 
		else if( ! oldDevice.getWlanMac().isEmpty() ) {
			// The wlan mac was removed
			device.setWlanIp("");
			macChange = true;
		}
		if(!error.isEmpty()){
			return new OssResponse(this.getSession(),"ERROR",String.join(System.lineSeparator(),error),null,parameters);
		}
		EntityManager em = getEntityManager();
		try {
			HWConf hwconf = new CloneToolController(this.session).getById(device.getHwconfId());
			oldDevice.setMac(device.getMac());
			oldDevice.setWlanMac(device.getWlanMac());
			oldDevice.setPlace(device.getPlace());
			oldDevice.setRow(device.getPlace());
			oldDevice.setHwconf(hwconf);
			em.getTransaction().begin();
			em.merge(oldDevice);
			em.getTransaction().commit();
		} catch (Exception e) {
			return new OssResponse(this.getSession(),"ERROR", e.getMessage());
		} finally {
			em.close();
		}
		this.startPlugin("modify_device", oldDevice);
		if( macChange ) {
			new DHCPConfig(this.session).Create();
		}
		return new OssResponse(this.getSession(),"OK", "Device was modified succesfully.");
	}

	public List<Device> getDevices(List<Long> deviceIds) {
		List<Device> devices = new ArrayList<Device>();
		for( Long id : deviceIds) {
			devices.add(this.getById(id));
		}
		return devices;
	}

	public List<Device> getByHWConf(Long id) {
		HWConf hwconf = new CloneToolController(this.session).getById(id);
		return hwconf.getDevices();
	}
	

	public OssResponse manageDevice(long deviceId, String action, Map<String, String> actionContent) {
		Device device = new DeviceController(this.session).getById(deviceId);
		if( this.session.getDevice() != null  && this.session.getDevice().equals(device)) {
			return new OssResponse(this.getSession(),"ERROR", "Do not control the own client.");
		}
		StringBuilder FQHN = new StringBuilder();
		FQHN.append(device.getName()).append(".").append(this.getConfigValue("DOMAIN"));
		File file;
		String graceTime    = "0";
		if( actionContent != null && actionContent.containsKey("graceTime")) {
			graceTime = actionContent.get("graceTime");
		}
		String[] program    = null;
		StringBuffer reply  = new StringBuffer();
		StringBuffer stderr = new StringBuffer();
		switch(action) {
		case "shutDown":
			program = new String[4];
			program[0] = "/usr/bin/salt";
			program[1] = FQHN.toString();
			program[2] = "system.shutdown";		
			program[3] = graceTime;
			break;
		case "reboot":
			program = new String[4];
			program[0] = "/usr/bin/salt";
			program[1] = FQHN.toString();
			program[2] = "system.reboot";
			program[3] = graceTime;
			break;
		case "logout":
			//TODO
			break;
		case "close":
			//TODO
			break;
		case "open":
			//TODO
			break;
		case "wol":
			program = new String[4];
			program[0] = "/usr/bin/wol";
			program[1] = "-i";
			program[2] = device.getIp();
			program[3] = device.getMac();
			break;
		case "controlProxy":
			//TODO
			break;
		case "saveFile":
			List<String>   fileContent =new ArrayList<String>();
			fileContent.add(actionContent.get("content"));
			String fileName = actionContent.get("fileName");
			try {
				file  = File.createTempFile("oss_", fileName + ".ossb", new File("/opt/oss-java/tmp/"));
				Files.write(file.toPath(), fileContent);
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
				return new OssResponse(this.getSession(),"ERROR", e.getMessage());
			}
			program = new String[4];
			program[0] = "/usr/bin/salt-cp";
			program[1] = FQHN.toString();
			program[2] = file.toPath().toString();
			program[3] = actionContent.get("path");
			break;
		default:
				return new OssResponse(this.getSession(),"ERROR", "Unknonw action.");	
		}
		OSSShellTools.exec(program, reply, stderr, null);
		return new OssResponse(this.getSession(),"OK", "Device control was applied on '%s'.",null,FQHN.toString());
	}


}
