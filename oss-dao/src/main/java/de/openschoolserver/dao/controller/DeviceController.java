/* (c) 2017 Péter Varkoly <peter@varkoly.de> - all rights reserved
 * (c) 2017 EXTIS GmbH www.extis.de - all rights reserved */
package de.openschoolserver.dao.controller;
import java.io.File;
import static java.nio.file.LinkOption.NOFOLLOW_LINKS;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
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

import de.openschoolserver.dao.Category;
import de.openschoolserver.dao.Device;
import de.openschoolserver.dao.HWConf;
import de.openschoolserver.dao.OssResponse;
import de.openschoolserver.dao.Printer;
import de.openschoolserver.dao.Room;
import de.openschoolserver.dao.Session;
import de.openschoolserver.dao.SoftwareLicense;
import de.openschoolserver.dao.SoftwareStatus;
import de.openschoolserver.dao.User;
import de.openschoolserver.dao.tools.*;

@SuppressWarnings( "unchecked" )
public class DeviceController extends Controller {

	Logger logger = LoggerFactory.getLogger(DeviceController.class);
	private List<String>  parameters;

	public DeviceController(Session session,EntityManager em) {
		super(session,em);
	}

	/*
	 * Return a device found by the Id
	 */
	public Device getById(long deviceId) {
		try {
			return this.em.find(Device.class, deviceId);
		} catch (Exception e) {
			return null;
		} finally {
		}
	}

	/*
	 * Delivers a list of all existing devices
	 */
	public List<Device> getAll() {
		try {
			Query query = this.em.createNamedQuery("Device.findAll");
			return query.getResultList();
		} catch (Exception e) {
			logger.error("getAll " + e.getMessage(),e);
			return null;
		} finally {
		}
	}

	/*
	 * Delivers a list of all existing devices
	 */
	public List<Long> getAllId() {
		try {
			Query query = this.em.createNamedQuery("Device.findAllId");
			return query.getResultList();
		} catch (Exception e) {
			logger.error("getAllId " + e.getMessage(),e);
			return null;
		} finally {
		}
	}

	/*
	 * Deletes a list of device given by the device Ids.
	 */
	public OssResponse delete(List<Long> deviceIds) {
		boolean needReloadSalt = false;
		try {
			for( Long deviceId : deviceIds) {
				Device device = this.em.find(Device.class, deviceId);
				if(device.getHwconf() != null &&  device.getHwconf().getDeviceType().equals("FatClient")) {
					needReloadSalt = true;
				}
				//TODO Evaluate the response
				this.delete(deviceId, false);
			}
			new DHCPConfig(session,em).Create();
			if( needReloadSalt ) {
				new SoftwareController(this.session,this.em).applySoftwareStateToHosts();
			}
			return new OssResponse(this.getSession(),"OK", "Devices were deleted succesfully.");
		} catch (Exception e) {
			logger.error("delete: " + e.getMessage(),e);
			return new OssResponse(this.getSession(),"ERROR", e.getMessage());
		} finally {
		}
	}

	/**
	 * Deletes a device given by the device id.
	 * @param deviceId The technical id of the device to be deleted
	 * @param atomic If it is true means no other devices will be deleted. DHCP and salt should be reloaded.
	 * @return
	 */
	public OssResponse delete(Device device, boolean atomic) {
		boolean needReloadSalt = false;
		if( device == null ) {
			return new OssResponse(this.getSession(),"ERROR", "Can not delete null device.");
		}
		User user = null;
		try {
			HWConf hwconf = device.getHwconf();
			Room   room   = device.getRoom();
			if( this.isProtected(device) ) {
				return new OssResponse(this.getSession(),"ERROR","This device must not be deleted.");
			}
			if( !this.mayModify(device) ) {
				return new OssResponse(this.getSession(),"ERROR","You must not delete this device.");
			}
			this.em.getTransaction().begin();
			if(hwconf != null )
			{
				hwconf.getDevices().remove(device);
				this.em.merge(hwconf);
				if( hwconf.getDeviceType().equals("FatClient")) {
					needReloadSalt = true;
				}
			}
			this.startPlugin("delete_device", device);
			if( device.getOwner() != null ) {
				User owner = device.getOwner();
				logger.debug("Deleting private device owner:" + owner + " device " + device);
				owner.getOwnedDevices().remove(device);
				if( session.getUser().equals(owner)) {
					session.getUser().getOwnedDevices().remove(device);
				}
				this.em.merge(owner);
			}
			//Clean up softwareStatus
			for( SoftwareStatus st : device.getSoftwareStatus() ) {
				logger.debug("Deleteing software status from device:" + device.getName() +" " + st.getId());
				this.em.remove(st);
			}
			//Clean up softwareLicences
			for( SoftwareLicense sl : device.getSoftwareLicenses()  ) {
				sl.getDevices().remove(device);
				this.em.merge(sl);
			}
			//Clean up printers
			for( Printer pr : device.getAvailablePrinters() ) {
				pr.getAvailableForDevices().remove(device);
				this.em.merge(pr);
			}
			if( device.getDefaultPrinter() != null ) {
				Printer pr = device.getDefaultPrinter();
				pr.getDefaultForDevices().remove(device);
				this.em.merge(pr);
			}
			//Clean up categories
			for( Category cat : device.getCategories() ) {
				cat.getDevices().remove(device);
				this.em.merge(cat);
			}
			//Clean up sessions
			for( Session session : device.getSessions() ) {
				this.em.remove(session);
			}
			//Remove salt sls file if exists
			File saltFile = new File("/srv/salt/oss_device_" + device.getName() + ".sls");
			if( saltFile.exists() ) {
				try {
					saltFile.delete();
					needReloadSalt = true;
				} catch( Exception e ) {
					logger.error("Deleting salt file:" + e.getMessage());
				}
			}
			this.em.remove(device);
			//Clean up room
			room.getDevices().remove(device);
			this.em.merge(room);
			this.em.flush();
			this.em.getTransaction().commit();
			if( atomic ) {
				new DHCPConfig(session,em).Create();
				if( needReloadSalt ) {
					new SoftwareController(this.session,this.em).applySoftwareStateToHosts();
				}
			}
			UserController userController = new UserController(this.session,this.em);
			user = userController.getByUid(device.getName());
			if( user != null ) {
				userController.delete(user);
			}
			return new OssResponse(this.getSession(),"OK", "Device was deleted succesfully.");
		} catch (Exception e) {
			logger.error("device: " + device.getName() + " " + e.getMessage(),e);
			return new OssResponse(this.getSession(),"ERROR", e.getMessage());
		} finally {
		}
	}

	/**
	 * Deletes a device.
	 * @param device The device to be deleted
	 * @param atomic If it is true means no other devices will be deleted. DHCP and salt should be reloaded.
	 * @return
	 */
	public OssResponse delete(Long deviceId, boolean atomic) {

		Device device = this.em.find(Device.class, deviceId);
		if( device == null ) {
			return new OssResponse(this.getSession(),"ERROR", "Can not find device with id %s.",null,String.valueOf(deviceId));
		}
		return this.delete(device, atomic);
	}

	protected OssResponse check(Device device,Room room) {
		List<String> error = new ArrayList<String>();
		List<String> parameters  = new ArrayList<String>();
		IPv4Net net = new IPv4Net(room.getStartIP() + "/" + room.getNetMask());

		//Check the MAC address
		device.setMac(device.getMac().toUpperCase().replaceAll("-", ":"));
		String name =  this.isMacUnique(device.getMac());
		if( name != "" ){
			parameters.add(device.getMac());
			parameters.add(name);
			return new OssResponse(this.session,"ERROR","The MAC address '%s' will be used allready by '%s'.",null,parameters);
		}
		if( ! IPv4.validateMACAddress(device.getMac())) {
			parameters.add(device.getMac());
			return new OssResponse(this.session,"ERROR","The MAC address '%s' is not valid.",null,parameters);
		}
		//Check the name
		if( ! this.isNameUnique(device.getName())){
			return new OssResponse(this.session,"ERROR","Devices name is not unique." );
		}
		if( this.checkBadHostName(device.getName())){
			return new OssResponse(this.session,"ERROR","Devices name contains not allowed characters. " );
		}
		//Check the IP address
		name =  this.isIPUnique(device.getIp());
		if( name != "" ){
			parameters.add(name);
			return new OssResponse(this.session,"ERROR","The IP address will be used allready by '%s'",null,parameters );
		}
		if( ! IPv4.validateIPAddress(device.getIp())) {
			parameters.add(device.getIp());
			return new OssResponse(this.session,"ERROR","The IP address '%s' is not valid.", null, parameters );
		}
		if( !net.contains(device.getIp())) {
			return new OssResponse(this.session,"ERROR","The IP address is not in the room ip address range.");
		}

		if( device.getWlanMac().isEmpty() ) {
			device.setWlanIp("");
		} else {
			//Check the MAC address
			device.setWlanMac(device.getWlanMac().toUpperCase().replaceAll("-", ":"));
			name =  this.isMacUnique(device.getWlanMac());
			if( name != "" ){
				parameters.add(name);
				return new OssResponse(this.session,"ERROR","The WLAN MAC address will be used allready '%s'.",null,parameters);
			}
			if( ! IPv4.validateMACAddress(device.getWlanMac())) {
				parameters.add(device.getMac());
				return new OssResponse(this.session,"ERROR","The WLAN-MAC address '%s' is not valid.",null,parameters);
			}
			//Check the IP address
			name =  this.isIPUnique(device.getWlanIp());
			if( name != "" ){
				parameters.add(name);
				return new OssResponse(this.session,"ERROR","The WLAN-IP address will be used allready by '%s'",null,parameters );
			}
			if( ! IPv4.validateIPAddress(device.getWlanIp())) {
				parameters.add(device.getWlanIp());
				return new OssResponse(this.session,"ERROR","The WLAN-IP address '%s' is not valid.", null, parameters );
			}
			if( !net.contains(device.getWlanIp())) {
				return new OssResponse(this.session,"ERROR","The WLAN-IP address is not in the room ip address range.");
			}
		}
		//Check user parameter
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		for (ConstraintViolation<Device> violation : factory.getValidator().validate(device) ) {
			error.add(violation.getMessage());
		}
		if( error.isEmpty() ) {
			return new OssResponse(this.session,"OK","");
		}
		return new OssResponse(this.session,"ERROR",String.join(System.lineSeparator(),error));
	}

	/*
	 * Creates devices
	 */
	public OssResponse add(List<Device> devices) {
		try {
			for(Device dev: devices){
				dev.setOwner(session.getUser());
				this.beginTransaction();
				this.em.persist(dev);
				this.em.getTransaction().commit();
			}
			return new OssResponse(this.getSession(),"OK", "Devices were created succesfully.");
		} catch (Exception e) {
			logger.error("add " + e.getMessage(),e);
			return new OssResponse(this.getSession(),"ERROR", e.getMessage());
		} finally {
		}
	}

	/*
	 * Find a device given by the IP address
	 */
	public Device getByIP(String IP) {
		try {
			Query query = this.em.createNamedQuery("Device.getByIP");
			query.setParameter("IP", IP);
			if( query.getResultList().isEmpty() ) {
				return null;
			}
			return (Device) query.getResultList().get(0);
		} catch (Exception e) {
			logger.debug("device.getByIP " + IP + " "+ e.getMessage());
			return null;
		} finally {
		}
	}

	/*
	 * Find a device given by the MAC address
	 */
	public Device getByMAC(String MAC) {
		try {
			Query query = this.em.createNamedQuery("Device.getByMAC");
			query.setParameter("MAC", MAC);
			return (Device) query.getSingleResult();
		} catch (Exception e) {
			logger.debug("MAC " + MAC + " " + e.getMessage());
			return null;
		} finally {
		}
	}

	/*
	 * Find a device given by the name
	 */
	public Device getByName(String name) {
		try {
			Query query = this.em.createNamedQuery("Device.getByName");
			query.setParameter("name", name);
			return (Device) query.getSingleResult();
		} catch (Exception e) {
			logger.debug("name " + name  + " " + e.getMessage());
			return null;
		} finally {
		}
	}

	/*
	 * Search devices given by a substring
	 */
	public List<Device> search(String search) {
		try {
			Query query = this.em.createNamedQuery("Device.search");
			query.setParameter("search", "%" + search + "%");
			return (List<Device>) query.getResultList();
		} catch (Exception e) {
			logger.debug("search " + search + " " + e.getMessage(),e);
			return null;
		} finally {
		}
	}

	/*
	 * Find the default printer for a device
	 * If no printer was defined by the device find this from the room
	 */
	public Printer getDefaultPrinter(long deviceId) {
		Device device = this.getById(deviceId);
		Printer printer = device.getDefaultPrinter();
		if( printer != null) {
			return printer;
		}
		printer = device.getRoom().getDefaultPrinter();
		if( printer != null) {
			return printer;
		}
		return null;
	}

	/*
	 * Find the available printer for a device
	 * If no printer was defined by the device find these from the room
	 */
	public List<Printer> getAvailablePrinters(long deviceId) {
		Device device = this.getById(deviceId);
		List<Printer> printers   = new ArrayList<Printer>();
		for( Printer printer : device.getAvailablePrinters() ) {
			printers.add(printer);
		}
		if( printers.isEmpty() ){
			for(Printer printer : device.getRoom().getAvailablePrinters()){
				printers.add(printer);
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

	/**
	 * Import devices from a CSV file. This MUST have following format:
	 * Separator: semicolon
	 * Fields: Room; MAC; Serial; Inventary; Locality; HWConf; Owner; Name; IP; WLANMAC; WLANIP; Row; Place;
	 * Mandatory fields which must not be empty: Room and MAC;
	 * @param fileInputStream
	 * @param contentDispositionHeader
	 * @return
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
			logger.error("File error:" + e.getMessage(), e);
			return new OssResponse(this.getSession(),"ERROR", e.getMessage());
		}
		RoomController        roomController      = new RoomController(this.session,this.em);
		CloneToolController   cloneToolController = new CloneToolController(this.session,this.em);
		UserController        userController      = new UserController(this.session,this.em);
		Map<Long,List<Device>> devicesToImport    = new HashMap<>();
		Map<Integer,String> header                = new HashMap<>();
		StringBuilder error                       = new StringBuilder();
		List<String>  parameters				  = new ArrayList<String>();

		//Initialize the the hash for the rooms
		for( Room r : roomController.getAllToUse() ) {
			devicesToImport.put(r.getId(), new ArrayList<Device>() );
		}
		String headerLine = importFile.get(0);
		int i = 0;
		for(String field : headerLine.split(";")) {
			header.put(i,field.toLowerCase());
			i++;
		}

		logger.debug("header" + header);
		if( !header.containsValue("mac") || !header.containsValue("room")) {
			return new OssResponse(this.getSession(),"ERROR", "MAC and Room are mandatory fields.");
		}
		for(String line : importFile.subList(1, importFile.size()) ) {
			Map<String,String> values = new HashMap<>();
			i = 0;
			for ( String value : line.split(";") ) {
				values.put(header.get(i),value);
				i++;
			}
			logger.debug("values" + values);
			Room room = roomController.getByName(values.get("room"));
			if( room == null ) {
				logger.debug("Can Not find the Room" +values.get("room") );
				error.append("Can not find the Room: ").append(values.get("room")).append("<br>");
				continue;
			}
			Device device = new Device();
			device.setRoom(room);
			device.setMac(values.get("mac"));
			if(values.containsKey("serial") && !values.get("serial").isEmpty() ) {
				device.setSerial(values.get("serial"));
			}
			if(values.containsKey("inventary") && !values.get("inventary").isEmpty() ) {
				device.setInventary(values.get("inventary"));
			}
			if(values.containsKey("locality") && !values.get("locality").isEmpty()) {
				device.setLocality(values.get("locality"));
			}
			if(values.containsKey("name") && !values.get("name").isEmpty() ) {
				device.setName(values.get("name"));
			}
			if(values.containsKey("wlanmac") && !values.get("wlanmac").isEmpty()) {
				device.setWlanMac(values.get("wlanmac"));
			}
			if(values.containsKey("ip") && !values.get("ip").isEmpty() ) {
				device.setIp(values.get("ip"));
			}
			if(values.containsKey("wlanip") && !values.get("wlanip").isEmpty() ) {
				device.setWlanIp(values.get("wlanip"));
			}
			if(values.containsKey("row") && !values.get("row").isEmpty()) {
				device.setRow(Integer.parseInt(values.get("row")));
			}
			if(values.containsKey("place") && !values.get("place").isEmpty()) {
				device.setRow(Integer.parseInt(values.get("place")));
			}
			if(values.containsKey("serial") && !values.get("serial").isEmpty() ) {
				device.setSerial(values.get("serial"));
			}
			if(values.containsKey("owner") && !values.get("owner").isEmpty() ) {
				User user = userController.getByUid(values.get("owner"));
				if( user != null ) {
					device.setOwner(user);
				}
			}
			if(values.containsKey("hwconf") && !values.get("hwconf").isEmpty() ) {
				HWConf hwconf = cloneToolController.getByName(values.get("hwconf"));
				if( hwconf != null ) {
					device.setHwconf(hwconf);
					device.setHwconfId(hwconf.getId());
				}
			} else if( room.getHwconf() != null ) {
				device.setHwconf(room.getHwconf());
				device.setHwconfId(room.getHwconf().getId());
			}
			devicesToImport.get(room.getId()).add(device);
		}

		for( Room r : roomController.getAllToUse() ) {
			if( !devicesToImport.get(r.getId()).isEmpty() ) {
				OssResponse ossResponse = roomController.addDevices(r.getId(), devicesToImport.get(r.getId()));
				if( ossResponse.getCode().equals("ERROR")) {
					error.append(ossResponse.getValue()).append("<br>");
					if( !ossResponse.getParameters().isEmpty() ) {
						parameters.addAll(ossResponse.getParameters());
					}
				}
			}
		}
		if( error.length() == 0 ) {
			return new OssResponse(this.getSession(),"OK", "Devices were imported succesfully.");
		}
		logger.error("ImportDevices:" + error.toString() + " Parameters: " + String.join(";",parameters));
		return new OssResponse(this.getSession(),"ERROR","End error:" + error.toString());
	}

	public OssResponse setDefaultPrinter(long deviceId, long printerId) {
		try {
			logger.debug("deviceId:" +deviceId + " printerId:" +  printerId);
			Printer printer = this.em.find(Printer.class, printerId);
			Device device   = this.em.find(Device.class, deviceId);
			if( device == null ) {
				return new OssResponse(this.getSession(),"ERROR", "Device cannot be found.");
			}
			if( printer == null ) {
				return new OssResponse(this.getSession(),"ERROR", "Printer cannot be found.");
			}
			this.beginTransaction();
			device.setDefaultPrinter(printer);
			printer.getDefaultForDevices().add(device);
			this.em.merge(device);
			this.em.merge(printer);
			this.em.getTransaction().commit();
		} catch (Exception e) {
			return new OssResponse(this.getSession(),"ERROR", e.getMessage());
		} finally {
		}
		return new OssResponse(this.getSession(),"OK", "Default printer was set succesfully.");
	}


	public OssResponse deleteDefaultPrinter(long deviceId) {
		Device  device  = this.em.find(Device.class, deviceId);
		if( device == null ) {
			return new OssResponse(this.getSession(),"ERROR", "Device cannot be found.");
		}
		Printer printer = device.getDefaultPrinter();
		if( printer != null  ) {
			try {
				this.beginTransaction();
				device.setDefaultPrinter(null);
				printer.getDefaultForDevices().remove(device);
				this.em.merge(device);
				this.em.merge(printer);
				this.em.getTransaction().commit();
			} catch (Exception e) {
				return new OssResponse(this.getSession(),"ERROR", e.getMessage());
			} finally {
			}
		}
		return new OssResponse(this.getSession(),"OK","The default printer of the device was deleted succesfully.");
	}

	public OssResponse addAvailablePrinter(long deviceId, long printerId) {
		try {
			Printer printer = this.em.find(Printer.class, printerId);
			Device device   = this.em.find(Device.class, deviceId);
			if( device == null || printer == null) {
				return new OssResponse(this.getSession(),"ERROR", "Device or printer cannot be found.");
			}
			if( device.getAvailablePrinters().contains(printer) ) {
				return new OssResponse(this.getSession(),"OK","The printer is already assigned to device.");
			}
			this.beginTransaction();
			device.getAvailablePrinters().add(printer);
			printer.getDefaultForDevices().add(device);
			this.em.merge(device);
			this.em.merge(printer);
			this.em.getTransaction().commit();
		} catch (Exception e) {
			return new OssResponse(this.getSession(),"ERROR", e.getMessage());
		} finally {
		}
		return new OssResponse(this.getSession(),"OK","The selected printer was added to the device.");
	}

	public OssResponse deleteAvailablePrinter(long deviceId, long printerId) {
		try {
			Printer printer = this.em.find(Printer.class, printerId);
			Device device   = this.em.find(Device.class, deviceId);
			if( device == null || printer == null) {
				return new OssResponse(this.getSession(),"ERROR", "Device or printer cannot be found.");
			}
			this.beginTransaction();
			device.getAvailablePrinters().remove(printer);
			printer.getDefaultForDevices().remove(device);
			this.em.merge(device);
			this.em.merge(printer);
			this.em.getTransaction().commit();
		} catch (Exception e) {
			return new OssResponse(this.getSession(),"ERROR", e.getMessage());
		} finally {
		}
		return new OssResponse(this.getSession(),"OK","The selected printer was removed from device.");
	}

	public OssResponse addLoggedInUser(String IP, String userName) {
		Device device = this.getByIP(IP);
		if( device == null ) {
			return new OssResponse(this.getSession(),"ERROR", "There is no registered device with IP: %s",null,IP);
		}
		User user = new UserController(this.session,this.em).getByUid(userName);
		if( user == null ) {
			return new OssResponse(this.getSession(),"ERROR", "There is no registered user with uid: %s",null,userName);
		}
		return this.addLoggedInUser(device, user);
	}
	public OssResponse addLoggedInUser(Long deviceId, Long userId) {
		Device device = this.getById(deviceId);
		if( device == null ) {
			return new OssResponse(this.getSession(),"ERROR", "There is no registered device with ID: %s",null,String.valueOf(deviceId));
		}
		User user = new UserController(this.session,this.em).getById(userId);
		if( user == null ) {
			return new OssResponse(this.getSession(),"ERROR", "There is no registered user with uid: %s",null,String.valueOf(userId));
		}
		return this.addLoggedInUser(device, user);
	}
	public OssResponse addLoggedInUser(Device device, User user) {

		parameters = new ArrayList<String>();
		parameters.add(device.getName());
		parameters.add(device.getIp());
		parameters.add(user.getUid());
		if( user.getLoggedOn().contains(device)) {
			return new OssResponse(this.getSession(),"OK", "Logged in user was already added on this device for you:%s;%s;%s",null,parameters);
		}
		device.getLoggedIn().add(user);
		user.getLoggedOn().add(device);
		logger.debug("addLoggedInUser: " + device.toString());
		logger.debug("addLoggedInUser: " + user.toString());
		try {
			this.beginTransaction();
			this.em.merge(device);
			this.em.merge(user);
			this.em.getTransaction().commit();
		} catch (Exception e) {
			return new OssResponse(this.getSession(),"ERROR", e.getMessage());
		} finally {
		}
		return new OssResponse(this.getSession(),"OK", "Logged in user was added succesfully:%s;%s;%s",null,parameters);
	}


	public OssResponse removeLoggedInUser(String IP, String userName) {
		Device device = this.getByIP(IP);
		User user = new UserController(this.session,this.em).getByUid(userName);
		if( device == null  || user == null ) {
			return new OssResponse(this.getSession(),"ERROR","Can not find user or device");
		}
		return this.removeLoggedInUser(device, user);
	}
	public OssResponse removeLoggedInUser(Long deviceId, Long userId) {
		Device device = this.getById(deviceId);
		User   user   = new UserController(this.session,this.em).getById(userId);
		if( device == null  || user == null ) {
			return new OssResponse(this.getSession(),"ERROR","Can not find user or device");
		}
		return this.removeLoggedInUser(device, user);
	}
	public OssResponse removeLoggedInUser(Device device, User user) {
		parameters = new ArrayList<String>();
		if( !user.getLoggedOn().contains(device)) {
			parameters.add(device.getName());
			parameters.add(user.getUid());
			return new OssResponse(this.getSession(),"OK", "Logged in user was already removed from this device for you:%s;%s;%s",null,parameters);
		}
		device.getLoggedIn().remove(user);
		user.getLoggedOn().remove(device);
		try {
			this.beginTransaction();
			this.em.merge(device);
			this.em.merge(user);
			this.em.getTransaction().commit();
		} catch (Exception e) {
			return new OssResponse(this.getSession(),"ERROR", e.getMessage());
		} finally {
		}
		parameters.add(device.getName());
		parameters.add(user.getUid());
		return new OssResponse(this.getSession(),"OK", "Logged in user was removed succesfully:%s;%s",null,parameters);
	}

	public OssResponse modify(Device device) {
		logger.debug("modify new device: " + device);
		Device oldDevice;
		HWConf hwconf;
		Room   room;
		try {
			oldDevice= this.em.find(Device.class, device.getId());
			hwconf   = this.em.find(HWConf.class, device.getHwconfId());
			room     = this.em.find(Room.class, oldDevice.getRoom().getId());
		} catch (Exception e) {
			logger.debug("DeviceId:" + device.getId() + " " + e.getMessage(),e);
			return new OssResponse(this.getSession(),"ERROR","Device or HWConf can not be found.");
		}
		HWConf oldHwconf = oldDevice.getHwconf();

		logger.debug("modify old device: " + oldDevice);
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
				//There was no WLAN-Mac before we need a new IP-Address
				RoomController rc = new RoomController(this.session,this.em);
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
		logger.debug("ERROR" + error);
		if(!error.isEmpty()){
			return new OssResponse(this.getSession(),"ERROR","ERROR" + String.join(System.lineSeparator(),error),null,parameters);
		}
		try {
			oldDevice.setMac(device.getMac());
			oldDevice.setWlanMac(device.getWlanMac());
			oldDevice.setPlace(device.getPlace());
			oldDevice.setRow(device.getRow());
			oldDevice.setInventary(device.getInventary());
			oldDevice.setSerial(device.getSerial());
			logger.debug("OLD-Device-After-Merge" + oldDevice);
			this.beginTransaction();
			this.em.merge(oldDevice);
			logger.debug("OLDHwconf " + oldHwconf + " new hw " + hwconf);
			if( hwconf != oldHwconf) {
				if( hwconf.getDevices() != null ) {
					hwconf.getDevices().add(oldDevice);
				} else {
					List<Device> devices = new ArrayList<Device>();
					devices.add(oldDevice);
					hwconf.setDevices(devices);
				}
				oldDevice.setHwconf(hwconf);
				oldDevice.setHwconfId(hwconf.getId());
				logger.debug(" new hw " + hwconf);
				this.em.merge(hwconf);
				if(oldHwconf != null  ) {
					oldHwconf.getDevices().remove(oldDevice);
					logger.debug("OLDHwconf " + oldHwconf );
					this.em.merge(oldHwconf);
				}
			}
			this.em.merge(room);
			this.em.getTransaction().commit();
		} catch (Exception e) {
			return new OssResponse(this.getSession(),"ERROR", "ERROR-3"+  e.getMessage());
		} finally {
		}
		this.startPlugin("modify_device", oldDevice);
		if( macChange ) {
			new DHCPConfig(session,em).Create();
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
		HWConf hwconf = new CloneToolController(this.session,this.em).getById(id);
		return hwconf.getDevices();
	}

	public OssResponse manageDevice(long deviceId, String action, Map<String, String> actionContent) {
		Device device = this.getById(deviceId);
		if( device == null ) {
			return new OssResponse(this.getSession(),"ERROR", "Can not find the client.");
		}
		return this.manageDevice(device, action, actionContent);
	}

	public OssResponse manageDevice(String deviceName, String action, Map<String, String> actionContent) {
		Device device = this.getByName(deviceName);
		if( device == null ) {
			return new OssResponse(this.getSession(),"ERROR", "Can not find the client.");
		}
		return this.manageDevice(device, action, actionContent);
	}

	public OssResponse manageDevice(Device device, String action, Map<String, String> actionContent) {
		if( this.session.getDevice() != null  && this.session.getDevice().equals(device)) {
			return new OssResponse(this.getSession(),"ERROR", "Do not control the own client.");
		}
		StringBuilder FQHN = new StringBuilder();
		FQHN.append(device.getName()).append(".").append(this.getConfigValue("DOMAIN"));
		File file;
		String graceTime    = "0";
		String message      = "";
		if( actionContent != null ) {
			if( actionContent.containsKey("graceTime")) {
				graceTime = actionContent.get("graceTime");
			}
			if( actionContent.containsKey("message")) {
				message = actionContent.get("message");
			}
		}
		String[] program    = null;
		StringBuffer reply  = new StringBuffer();
		StringBuffer stderr = new StringBuffer();
		switch(action.toLowerCase()) {
		case "shutdown":
			if( message.isEmpty() ) {
				message = "System will shutdown in " + graceTime + "minutes";
			}
			program = new String[6];
			program[0] = "/usr/bin/salt";
			program[1] = "--async";
			program[2] = FQHN.toString();
			program[3] = "system.shutdown";
			program[4] = message;
			program[5] = graceTime;
			break;
		case "reboot":
			program = new String[5];
			program[0] = "/usr/bin/salt";
			program[1] = "--async";
			program[2] = FQHN.toString();
			program[3] = "system.reboot";
			program[4] = graceTime;
			break;
		case "close":
			program = new String[4];
			program[0] = "/usr/bin/salt";
			program[1] = "--async";
			program[2] = FQHN.toString();
			program[3] = "oss_client.lockClient";
			break;
		case "open":
			program = new String[4];
			program[0] = "/usr/bin/salt";
			program[1] = "--async";
			program[2] = FQHN.toString();
			program[3] = "oss_client.unLockClient";
			break;
		case "lockinput":
			program = new String[4];
			program[0] = "/usr/bin/salt";
			program[1] = "--async";
			program[2] = FQHN.toString();
			program[3] = "oss_client.blockInput";
			break;
		case "unlockinput":
			program = new String[4];
			program[0] = "/usr/bin/salt";
			program[1] = "--async";
			program[2] = FQHN.toString();
			program[3] = "oss_client.unBlockInput";
			break;
		case "applystate":
			program = new String[4];
			program[0] = "/usr/bin/salt";
			program[1] = "--async";
			program[2] = FQHN.toString();
			program[3] = "state.apply";
			break;
		case "wol":
			program = new String[3];
			program[0] = "/usr/sbin/oss_wol.sh";
			program[1] = device.getMac();
			program[2] = device.getIp();
			break;
		case "controlProxy":
			//TODO
			break;
		case "savefile":
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
		case "logoff":
		case "logout":
			program = new String[4];
			program[0] = "/usr/bin/salt";
			program[1] = "--async";
			program[2] = FQHN.toString();
			program[3] = "oss_client.logOff";
		case "cleanuploggedin":
			this.beginTransaction();
			for( User user : device.getLoggedIn() ) {
				user.getLoggedOn().remove(device);
				this.em.merge(user);
			}
			device.setLoggedIn(new ArrayList<User>());
			this.em.merge(device);
			this.em.getTransaction().commit();
			break;
		case "download":
			UserController uc = new UserController(this.session,this.em);;
			boolean cleanUpExport = true;
			boolean sortInDirs    = true;
			String  projectName   = this.nowString();
			if( actionContent != null ) {
				if( actionContent.containsKey("projectName")) {
					projectName = actionContent.get("projectName");
				}
				if( actionContent.containsKey("sortInDirs")) {
					sortInDirs = actionContent.get("sortInDirs").equals("true");
				}
				if( actionContent.containsKey("cleanUpExport")) {
					cleanUpExport = actionContent.get("cleanUpExport").equals("true");
				}
			}
			for( User user : device.getLoggedIn() ) {
				 uc.collectFileFromUser(user, projectName, cleanUpExport, sortInDirs);
			}
			return new OssResponse(this.getSession(),"OK", "Device control was applied on '%s'.",null,FQHN.toString());
		default:
				return new OssResponse(this.getSession(),"ERROR", "Unknonw action.");
		}
		OSSShellTools.exec(program, reply, stderr, null);
		return new OssResponse(this.getSession(),"OK", "Device control was applied on '%s'.",null,FQHN.toString());
	}

	public OssResponse cleanUpLoggedIn() {
		for( Device device : this.getAll() ) {
			cleanUpLoggedIn(device);
		}
		return new OssResponse(this.getSession(),"OK", "LoggedIn attributes was cleaned up.");
	}

	public OssResponse cleanUpLoggedIn(Device device) {
		if( device.getLoggedIn() == null || device.getLoggedIn().isEmpty() ) {
			return new OssResponse(this.getSession(),"OK", "No logged in user to remove.");
		}
		try {
			this.beginTransaction();
			for( User user : device.getLoggedIn() ) {
				user.getLoggedOn().remove(device);
				this.em.merge(user);
			}
			device.setLoggedIn(new ArrayList<User>());
			this.em.merge(device);
			this.em.getTransaction().commit();
		} catch (Exception e) {
			logger.debug("cleanUpLoggedIn: " + e.getMessage());
			return new OssResponse(this.getSession(),"ERROR", e.getMessage());
		} finally {
		}
		return new OssResponse(this.getSession(),"OK", "LoggedIn attributes was cleaned up.");
	}

	public List<Device> getDevicesOnMyPlace(Device device) {
		List<Device> devices = new ArrayList<Device>();
		for(Device dev: device.getRoom().getDevices()) {
			if( dev.getId() == device.getId() ) {
				continue;
			}
			if( device.getRow() == dev.getRow() && device.getPlace() == dev.getPlace() ) {
				devices.add(device);
			}
		}
		return devices;
	}

	public String getAllUsedDevices(Long saltClientOnly) {
		List<String> devices = new ArrayList<String>();
		String domainName    = "." + this.getConfigValue("DOMAIN");
		for( Device device : this.getAll() ) {
			if( !device.getLoggedIn().isEmpty() ) {
				if( saltClientOnly == 0 ) {
					devices.add(device.getName() + domainName );
				}
				else {
					StringBuilder path = new StringBuilder("/etc/salt/pki/master/minions/");
					path.append(device.getName()).append(".").append(this.getConfigValue("DOMAIN"));
					if( Files.exists(Paths.get(path.toString()), NOFOLLOW_LINKS) ) {
						devices.add(device.getName() + domainName );
					}
				}
			}
		}
		return String.join(",", devices);
	}

	public OssResponse setLoggedInUsers(String IP, String userName) {
		Device device = this.getByIP(IP);
		if( device == null ) {
			return new OssResponse(this.getSession(),"ERROR", "There is no registered device with IP: %s",null,IP);
		}
		User user = new UserController(this.session,this.em).getByUid(userName);
		if( user == null ) {
			return new OssResponse(this.getSession(),"ERROR", "There is no registered user with uid: %s",null,userName);
		}
		return this.setLoggedInUsers(device, user);
	}

	public OssResponse setLoggedInUsers(Long deviceId, Long userId) {
		Device device = this.getById(deviceId);
		if( device == null ) {
			return new OssResponse(this.getSession(),"ERROR", "There is no registered device with ID: %s",null,String.valueOf(deviceId));
		}
		User user = new UserController(this.session,this.em).getById(userId);
		if( user == null ) {
			return new OssResponse(this.getSession(),"ERROR", "There is no registered user with uid: %s",null,String.valueOf(userId));
		}
		return this.setLoggedInUsers(device, user);
	}

	public OssResponse setLoggedInUsers(Device device, User user) {
		parameters = new ArrayList<String>();
		parameters.add(device.getName());
		parameters.add(device.getIp());
		parameters.add(user.getUid());;
		logger.debug("addLoggedInUser: " + device.toString());
		logger.debug("addLoggedInUser: " + user.toString());
		this.cleanUpLoggedIn(device);
		try {
			this.beginTransaction();
			device.setLoggedIn(new ArrayList<User>());
			device.getLoggedIn().add(user);
			user.getLoggedOn().add(device);
			this.em.merge(device);
			this.em.merge(user);
			this.em.getTransaction().commit();
		} catch (Exception e) {
			return new OssResponse(this.getSession(),"ERROR", e.getMessage());
		} finally {
		}
		return new OssResponse(this.getSession(),"OK", "Logged in user was added succesfully:%s;%s;%s",null,parameters);
	}


}
