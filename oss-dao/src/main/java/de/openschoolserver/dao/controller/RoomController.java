/* (c) 2017 Péter Varkoly <peter@varkoly.de> - all rights reserved */
package de.openschoolserver.dao.controller;

import java.util.ArrayList;




import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Calendar;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.ValidatorFactory;

import de.openschoolserver.dao.Device;
import de.openschoolserver.dao.Group;
import de.openschoolserver.dao.HWConf;
import de.openschoolserver.dao.OssResponse;
import de.openschoolserver.dao.Room;
import de.openschoolserver.dao.User;
import de.openschoolserver.dao.Session;
import de.openschoolserver.dao.AccessInRoom;
import de.openschoolserver.dao.tools.*;

@SuppressWarnings( "unchecked" )
public class RoomController extends Controller {

	Logger logger = LoggerFactory.getLogger(RoomController.class);

	public RoomController(Session session) {
		super(session);
	}

	public boolean isNameUnique(String name)
	{
		EntityManager em = getEntityManager();
		try {
			Query query = em.createNamedQuery("Room.getByName");
			query.setParameter("name", name);
			List<Room> rooms = (List<Room>) query.getResultList();
			return rooms.isEmpty();
		} catch (Exception e) {
			logger.error(e.getMessage());
			return false;
		} finally {
			em.close();
		}
	}

	public boolean isDescriptionUnique(String description)
	{
		EntityManager em = getEntityManager();
		try {
			Query query = em.createNamedQuery("Room.getByDescription");
			query.setParameter("description", description);
			List<Room> rooms = query.getResultList();
			return rooms.isEmpty();
		} catch (Exception e) {
			logger.error(e.getMessage());
			return false;
		} finally {
			em.close();
		}
	}

	public Room getById(long roomId) {
		EntityManager em = getEntityManager();

		try {
			return em.find(Room.class, roomId);
		} catch (Exception e) {
			logger.error(e.getMessage());
			return null;
		} finally {
			em.close();
		}
	}

	public List<Room> getAll() {
		EntityManager em = getEntityManager();
		try {
			Query query = em.createNamedQuery("Room.findAllToUse"); 
			return (List<Room>) query.getResultList();
		} catch (Exception e) {
			logger.error(e.getMessage());
			return new ArrayList<>();
		} finally {
			em.close();
		}
	}
	
	public Room getByIP(String ip) {
		EntityManager em = getEntityManager();
		try {
			Query query = em.createNamedQuery("Room.getByIp").setParameter("ip", ip); 
			return (Room) query.getResultList().get(0);
		} catch (Exception e) {
			logger.error(e.getMessage());
			return null;
		} finally {
			em.close();
		}
	}

	public Room getByName(String name) {
		EntityManager em = getEntityManager();
		try {
			Query query = em.createNamedQuery("Room.getByName").setParameter("name", name); 
			return (Room) query.getResultList().get(0);
		} catch (Exception e) {
			logger.error(e.getMessage());
			return null;
		} finally {
			em.close();
		}
	}

	/*
	 * Return a list the rooms in which the session user can register devices
	 * 
	 * @return For super user all rooms will be returned
	 *         For normal user the list his AdHocAccess rooms of those of his groups 
	 */
	public List<Room> getAllToRegister() {
		EntityManager em = getEntityManager();
		Room room  = null;
		try {
			if( this.isSuperuser() ) {
				Query query = em.createNamedQuery("Room.findAllToRegister"); 
				return query.getResultList();
			} else {
				List<Room> rooms = new ArrayList<Room>();
				for(String roomid : this.getMConfigs(this.session.getUser(),"AdHocAccess" ) ) {
					room = this.getById(Long.parseLong(roomid));
					if( !rooms.contains(room)) {
						rooms.add(room);
					}
				}
				for(Group group : this.session.getUser().getGroups() ) {
					for(String roomid : this.getMConfigs(group,"AdHocAccess" ) ) {
						room = this.getById(Long.parseLong(roomid));
						if( !rooms.contains(room)) {
							rooms.add(room);
						}
					}
				}
				return rooms;
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			return new ArrayList<>();
		} finally {
			em.close();
		}
	}
	
	/*
	 * Search devices given by a substring
	 */
	public List<Room> search(String search) {
		EntityManager em = getEntityManager();
		try {
			Query query = em.createNamedQuery("Device.search");
			query.setParameter("search", search + "%");
			return (List<Room>) query.getResultList();
		} catch (Exception e) {
			logger.error(e.getMessage());
			return null;
		} finally {
			em.close();
		}
	}

	public OssResponse add(Room room){
		if( room.getRoomType().equals("smartRoom") ) {
			return new OssResponse(this.getSession(),"ERROR", "Smart Rooms can only be created by Education Controller.");
		}
                HWConf hwconf = new HWConf();
                CloneToolController cloneToolController = new CloneToolController(this.session);
                HWConf firstFatClient = cloneToolController.getByType("FatClient").get(0);
		logger.debug("First HWConf:" +  firstFatClient.toString());

		//Check parameters
		StringBuilder errorMessage = new StringBuilder();
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		for (ConstraintViolation<Room> violation : factory.getValidator().validate(room) ) {
			errorMessage.append(violation.getMessage()).append(getNl());
		}
		if( errorMessage.length() > 0 ) {
			return new OssResponse(this.getSession(),"ERROR", errorMessage.toString());
		}

		// First we check if the parameter are unique.
		if( !this.isNameUnique(room.getName())){
			return new OssResponse(this.getSession(),"ERROR", "Room name is not unique.");
		}
		if( room.getDescription() != null && !room.getDescription().isEmpty() && !this.isDescriptionUnique(room.getDescription())){
			return new OssResponse(this.getSession(),"ERROR", "Room description is not unique.");
		}

		// If no network was configured we will use net school network.
		if( room.getNetwork() == null || room.getNetwork().isEmpty() ) {
			room.setNetwork(this.getConfigValue("NETWORK") + "/" + this.getConfigValue("NETMASK"));
		}

		// If the starIp is not given we have to search the next room IP
		if( room.getStartIP() == null || room.getStartIP().isEmpty() ) {
			room.setStartIP( getNextRoomIP(room.getNetwork(),room.getNetMask()) );
		}

		//	Set default control mode
		if( room.getRoomControl() == null || room.getRoomControl().isEmpty() ) {
			room.setRoomControl("inRoom");
		}
		EntityManager em = getEntityManager();
		// Check HWConf
		hwconf = cloneToolController.getById(room.getHwconfId());
		if( hwconf == null ) {
			if( room.getHwconf() != null){
				hwconf = room.getHwconf();
			} else {
				hwconf =  firstFatClient;
			}
		}
		room.setHwconf(hwconf);
		room.setCreator(this.session.getUser());
		hwconf.getRooms().add(room);
		try {
			logger.debug("Create Room:" + room);
			em.getTransaction().begin();
			em.persist(room);
			em.merge(hwconf);
			em.getTransaction().commit();
		} catch (Exception e) {
			logger.error("Error by creating Room:" + e.getMessage());
			return new OssResponse(this.getSession(),"ERROR", e.getMessage());
		} finally {
			em.close();
		}
		this.startPlugin("add_room", room);
		return new OssResponse(this.getSession(),"OK", "Room was created succesfully.",room.getId());
	}

	public OssResponse delete(long roomId){
		EntityManager em = getEntityManager();
		Room room = this.getById(roomId);
		if( !this.mayModify(room) ) {
			return new OssResponse(this.getSession(),"ERROR","You must not delete this room.");
		}
		try {

			em.getTransaction().begin();
			DeviceController devController = new DeviceController(this.getSession());
			if( this.isProtected(room) ) {
				return new OssResponse(this.getSession(),"ERROR","This room must not be deleted.");
			}
			List<Long> deviceIds = new ArrayList<Long>();
			for( Device device : room.getDevices()) {
				deviceIds.add(device.getId());
			}
			OssResponse ossResponse = devController.delete(deviceIds);
			//If an error happened during deleting the devices the room must not be removed.
			if( ossResponse.getCode().equals("ERROR") ) {
				return ossResponse;
			}
			if( ! em.contains(room)) {
				room = em.merge(room);
			}
			em.remove(room);
			em.getTransaction().commit();
			em.getEntityManagerFactory().getCache().evictAll();
		} catch (Exception e) {
			logger.error(e.getMessage());
			return new OssResponse(this.getSession(),"ERROR", e.getMessage());
		} finally {
			em.close();
		}
		DHCPConfig dhcpconfig = new DHCPConfig(this.session);
		dhcpconfig.Create();
		this.startPlugin("delete_room", room);
		return new OssResponse(this.getSession(),"OK", "Room was removed successfully.");
	}
	

	/*
	 * Return the list of the available adresses in the room
	 */
	public List<String> getAvailableIPAddresses(long roomId){
		Room room   = this.getById(roomId);
		IPv4Net net = new IPv4Net(room.getStartIP() + "/" + room.getNetMask());
		List<String> allIPs  = net.getAvailableIPs(0);
		List<String> usedIPs = new ArrayList<String>();
		//TODO it is only for the school network. We need to check for all other subnets
		String subnet = this.getConfigValue("NETWORK") + "/" + this.getConfigValue("NETMASK");
	    IPv4Net subNetwork = new IPv4Net( subnet );
	    usedIPs.add(subNetwork.getBase());
	    usedIPs.add(subNetwork.getLast());
		for( Device dev : room.getDevices() ){
			usedIPs.add(dev.getIp());
			if( dev.getWlanIp() != "" ) {
				usedIPs.add(dev.getWlanIp());
			}
		}
		allIPs.removeAll(usedIPs);
		return allIPs;
	}

	/*
	 * Return the list of the available adresses in the room
	 */
	public List<String> getAvailableIPAddresses(long roomId, long count){
		Room room   = this.getById(roomId);
		IPv4Net net = new IPv4Net(room.getStartIP() + "/" + room.getNetMask());
		//TODO it is only for the school network. We need to check for all other subnets
		String subnet = this.getConfigValue("NETWORK") + "/" + this.getConfigValue("NETMASK");
	    IPv4Net subNetwork = new IPv4Net( subnet );
	    String firstIP = subNetwork.getBase();
	    String lastIP  = subNetwork.getLast();
		List<String> availableIPs = new ArrayList<String>();
		int i = 0;
		for( String IP : net.getAvailableIPs(0) ){
			if( IP.equals(lastIP) || IP.equals(firstIP)) {
				continue;
			}
			String name =  this.isIPUnique(IP);
			if( name.isEmpty() ){
				availableIPs.add(String.format("%s %s-pc%02d", IP,room.getName().replace("_", "-").toLowerCase(),i));
			}
			if( count > 0 && availableIPs.size() == count ) {
				break;
			}
			i++;
		}
		return availableIPs;
	}

	/*
	 * Delivers the next room IP address in a given subnet with the given netmask.
	 * If the subnet is "" the default school network is meant.
	 */
	public String getNextRoomIP( String subnet, int roomNetMask ) throws NumberFormatException {
		if( subnet == null || subnet.isEmpty() ){
			subnet = this.getConfigValue("NETWORK") + "/" + this.getConfigValue("NETMASK");
		}
		IPv4Net subNetwork = new IPv4Net( subnet );
		if(roomNetMask < subNetwork.getNetmaskNumeric() ) {
			throw new NumberFormatException("The network netmask must be less then the room netmask:" + roomNetMask + ">" + subNetwork.getNetmaskNumeric() );
		}
		
		List<String>  startIPAddresses   = new ArrayList<String>();
		EntityManager em = getEntityManager();
		Query query = em.createNamedQuery("Room.findAll");
		for( Room room : (List<Room>) query.getResultList() ) {
			if( !subNetwork.contains(room.getStartIP()))
				continue;
			startIPAddresses.add(room.getStartIP());
		}
		// When no room was found in this network we return the network address of the network.
		if( startIPAddresses.isEmpty() )
			return subNetwork.getBase();

		List<String> sortedIPAddresses = IPv4.sortIPAddresses(startIPAddresses);
		String lastNetworkIP = sortedIPAddresses.get(sortedIPAddresses.size()-1);

		// Find the net of the last room
		query = em.createQuery("SELECT r FROM Room r WHERE r.startIP = :startIP",Room.class);
		query.setParameter("startIP", lastNetworkIP);
		Room lastRoom = (Room)query.getSingleResult();
		int lastNetMask = lastRoom.getNetMask(); 
		//Find the next free net with the network mask of the last room
		IPv4Net net = new IPv4Net( lastNetworkIP + "/" + lastNetMask );
		String nextNet = net.getNext();

		//Now set the last network IP to the last IP in the last network.
		lastNetworkIP = net.getLast();

		//This could be our net
		net = new IPv4Net(nextNet + "/" + roomNetMask );
		while ( net.contains(lastNetworkIP)) {
			//If the end of the last network is in our net it is wrong.
			//In this case get the next one net address
			nextNet = net.getNext();
			net = new IPv4Net(nextNet + "/" + roomNetMask );
		}

		// Check if the nextNet is in school net.
		String lastIP = net.getNext();
		if( ! subNetwork.contains(lastIP) )
		{
			return "";
		}
		return nextNet;
	}

	/*
	 * Returns a list of the users logged in in the room
	 */
	public List<Map<String, String>> getLoggedInUsers(long roomId){
		List<Map<String, String>> users = new ArrayList<>();
		Room room = this.getById(roomId);
		for(Device device : room.getDevices()){
			for(User user: device.getLoggedIn()){
				Map<String,String> userMap = new HashMap<>();
				userMap.put("device", device.getName());
				userMap.put("deviceId", String.valueOf(device.getId()));
				userMap.put("uid", user.getUid());
				userMap.put("userId", String.valueOf(user.getId()));
				userMap.put("surName", user.getSurName());
				userMap.put("givenName", user.getGivenName());
				users.add(userMap);
			}
		}
		return users;
	}

	/*
	 * Returns the list of accesses in a room
	 */
	public List<AccessInRoom> getAccessList(long roomId){
		Room room = this.getById(roomId);
		return room.getAccessInRooms();
	}

	/*
	 * Sets the list of accesses in a room
	 */
	public OssResponse setAccessList(long roomId,List<AccessInRoom> AccessList){
		Room room = this.getById(roomId);
		EntityManager em = getEntityManager();
		try {
			em.getTransaction().begin();
			for( AccessInRoom air : room.getAccessInRooms() ) {
				room.removeAccessInRoome(air);
			}
			for( AccessInRoom air : AccessList ) {
				air.setRoom(room);
				room.addAccessInRoom(air);
			}
			em.merge(room);
			em.getTransaction().commit();
		} catch (Exception e) {
			logger.error(e.getMessage());
			return new OssResponse(this.getSession(),"ERROR", e.getMessage());
		} finally {
			em.close();
		}
		return new OssResponse(this.getSession(),"OK","Acces was created succesfully");
	}

	/*
	 * Sets the actual access status in a room
	 */
	public void setAccessStatus(Room room, AccessInRoom access) {
		String[] program = new String[4];
		program[0] = "/usr/sbin/oss-set-access-state.sh";
		program[2] = room.getStartIP() + "/" + room.getNetMask();
		access.setRoom(room);
		StringBuffer reply = new StringBuffer();
		StringBuffer error = new StringBuffer();

		if(access.getAccessType().equals("ACT") ) {
			//TODO 	
		}
		else
		{
			// Direct internet
			program[3] = "direct";
			if( access.getDirect() )
				program[1] = "1";
			else
				program[1] = "0";
			OSSShellTools.exec(program, reply, error, null);

			// Portal Access
			program[3] = "portal";
			if( access.getPortal())
				program[1] = "1";
			else
				program[1] = "0";
			OSSShellTools.exec(program, reply, error, null);

			// Proxy Access
			program[3] = "proxy";
			if( access.getProxy() )
				program[1] = "1";
			else
				program[1] = "0";
			OSSShellTools.exec(program, reply, error, null);

			// Printing Access
			program[3] = "printing";
			if( access.getPrinting() )
				program[1] = "1";
			else
				program[1] = "0";
			OSSShellTools.exec(program, reply, error, null);

			// Login
			program[3] = "login";
			if( access.getLogin() ) 
				program[1] = "1";
			else
				program[1] = "0";
			OSSShellTools.exec(program, reply, error, null);
		}
	}

	/*
	 * Sets the actual access status in a room 
	 */
	public OssResponse setAccessStatus(long roomId, AccessInRoom access) {
		Room room = this.getById(roomId);
		this.setAccessStatus(room, access);
		return new OssResponse(this.getSession(),"OK", "Access state in was set succesfully." );
	}

	/*
	 * Sets the scheduled access status in all rooms
	 */
	public OssResponse setScheduledAccess(){
		EntityManager em = getEntityManager();
		Calendar rightNow = Calendar.getInstance();
		String   actTime  = String.format("%02d:%02d", rightNow.get(Calendar.HOUR_OF_DAY),rightNow.get(Calendar.MINUTE));
		Query query = em.createNamedQuery("AccessInRoom.findActualAccesses");
		query.setParameter("time", actTime);
		for( AccessInRoom access : (List<AccessInRoom>) query.getResultList() ){
			Room room = access.getRoom();
			this.setAccessStatus(room, access);
		}
		return new OssResponse(this.getSession(),"OK", "Scheduled access states where set succesfully." );
	}

	/*
	 * Gets the actual access status in a room
	 */
	public AccessInRoom getAccessStatus(Room room) {

		AccessInRoom access = new AccessInRoom();
		access.setAccessType("FW");
		access.setRoomId(room.getId());
		
		String[] program = new String[3];
		program[0] = "/usr/sbin/oss-get-access-state.sh";
		program[1] = room.getStartIP() + "/" + room.getNetMask();
		access.setRoom(room);
		StringBuffer reply = new StringBuffer();
		StringBuffer error = new StringBuffer();

		// Direct internet
		program[2] = "direct";
		OSSShellTools.exec(program, reply, error, null);
		if( reply.toString().equals("1") )
			access.setDirect(true);
		else
			access.setDirect(false);

		// Portal Access
		program[2] = "portal";
		OSSShellTools.exec(program, reply, error, null);
		if( reply.toString().equals("1") )
			access.setPortal(true);
		else
			access.setPortal(false);

		// Proxy Access
		program[2] = "proxy";
		OSSShellTools.exec(program, reply, error, null);
		if( reply.toString().equals("1") )
			access.setProxy(true);
		else
			access.setProxy(false);

		// Printing Access
		program[2] = "printing";
		OSSShellTools.exec(program, reply, error, null);
		if( reply.toString().equals("1") )
			access.setPrinting(true);
		else
			access.setPrinting(false);

		// Login
		program[2] = "login";
		OSSShellTools.exec(program, reply, error, null);
		if( reply.toString().equals("1") )
			access.setLogin(true);
		else
			access.setLogin(false);
		return access;
	}

	/*
	 * Sets the actual access status in a room.
	 * Room is given by roomId
	 */
	public AccessInRoom getAccessStatus(long roomId) {
		Room room = this.getById(roomId);
		return this.getAccessStatus(room);
	}
	
	/*
	 * Sets the actual access status in all rooms.
	 * Room is given by roomId
	 */
	public List<AccessInRoom> getAccessStatus() {
		List<AccessInRoom> accesses = new ArrayList<AccessInRoom>();
		for(Room room : this.getAll()) {
			accesses.add(this.getAccessStatus(room));
		}
		return accesses;
	}
	
	/*
	 * Creates new devices in the room
	 */
	public OssResponse addDevices(long roomId,List<Device> devices){
		EntityManager em = getEntityManager();
		Room room = this.getById(roomId);
		HWConf hwconf = new HWConf();
		DeviceController deviceController = new DeviceController(this.session);
		CloneToolController cloneToolController = new CloneToolController(this.session);
		HWConf firstFatClient = cloneToolController.getByType("FatClient").get(0);
		List<String> ipAddress;
		List<Device> newDevices = new ArrayList<Device>();
		List<String> parameters  = new ArrayList<String>();
		try {
			for(Device device : devices) {
				//Remove trailing and ending spaces.
				em.getTransaction().begin();
				device.setName(device.getName().trim());
				ipAddress = this.getAvailableIPAddresses(roomId, 2);
				if( device.getIp().isEmpty() ){
					if( ipAddress.isEmpty() ) {
						em.getTransaction().rollback();
						parameters.add(device.getMac());
						return new OssResponse(this.getSession(),"ERROR",
								"There are no more free ip addresses in this room for the MAC: %s.",room.getId(),parameters);
					}
					if( device.getName().isEmpty() ) {
						device.setName(ipAddress.get(0).split(" ")[1]);
					}
					device.setIp(ipAddress.get(0).split(" ")[0]);
				}
				if( !device.getWlanMac().isEmpty() ){
					if( ipAddress.size() < 2 ) {
						em.getTransaction().rollback();
						parameters.add(device.getWlanMac());
						return new OssResponse(this.getSession(),"ERROR",
								"There are no more free ip addresses in this room for the MAC: %s.",room.getId(),parameters);
					}
					device.setWlanIp(ipAddress.get(1).split(" ")[0]);
				}
				String error = deviceController.check(device, room);
				if( ! error.isEmpty() ) {
					em.getTransaction().rollback();
					return new OssResponse(this.getSession(),"ERROR",device.getMac() + " " + error);
				}
				device.setRoom(room);
				if( device.getOwner() == null ) {
					device.setOwner(this.session.getUser());
				}
				hwconf = cloneToolController.getById(device.getHwconfId());
				if( hwconf == null ) {
					if( room.getHwconf() != null ){
						hwconf = room.getHwconf();
					} else {
						hwconf = firstFatClient;
					}
				}
				device.setHwconf(hwconf);
				em.persist(device);
				hwconf.getDevices().add(device);
				room.addDevice(device);
				em.merge(hwconf);
				em.merge(room);
				newDevices.add(device);
				logger.debug(device.toString());
				em.getTransaction().commit();
			}
		} catch (Exception e) { 
			logger.error(e.getMessage());
			return new OssResponse(this.getSession(),"ERROR", "Error by creating the device: " + e.getMessage());
		} finally {
			em.close();
		}
		UserController userController = new UserController(this.session);
		boolean needWriteSalt = false;
		for(Device device : newDevices) {
			this.startPlugin("add_device", device);
			logger.debug("Created Device" + device);
			logger.debug("HWCONF" + device.getHwconf());
			// We'll create only for fatClients workstation users
			if( device.getHwconf() != null && 
					device.getHwconf().getDeviceType() != null &&
					device.getHwconf().getDeviceType().equals("FatClient"))
			{
				User user = new User();
				user.setUid(device.getName());
				user.setGivenName(device.getName());
				user.setSurName("Workstation-User");
				user.setRole("workstations");
				//TODO do not ignore response.
				OssResponse answer = userController.add(user);
				logger.debug(answer.getValue());
				needWriteSalt = true;
			}
		}
		new DHCPConfig(this.session).Create();
		if( needWriteSalt ) {
			new SoftwareController(this.session).applySoftwareStateToHosts();
		}
		return new OssResponse(this.getSession(),"OK", "Devices were created succesfully." );
	}

	/*
	 * Deletes devices in the room
	 */
	public OssResponse deleteDevices(long roomId,List<Long> deviceIDs){
		EntityManager em = getEntityManager();
		Room room = em.find(Room.class, roomId);
		DeviceController deviceController = new DeviceController(this.session);
		boolean needWriteSalt = false;
		try {
			for(Long deviceId : deviceIDs) {
				Device device = em.find(Device.class, deviceId);
				if( room.getDevices().contains(device) ) {
					if( device.getHwconf().getDeviceType().equals("FatClient")) {
						needWriteSalt = true;
					}
					deviceController.delete(device, false);
				}
			}
			em.getEntityManagerFactory().getCache().evictAll();
		} catch (Exception e) {
			logger.error(e.getMessage());
			return new OssResponse(this.getSession(),"ERROR", e.getMessage());
		} finally {
			em.close();
		}
		new DHCPConfig(this.session).Create();
		if( needWriteSalt ) {
			new SoftwareController(this.session).applySoftwareStateToHosts();
		}
		return new OssResponse(this.getSession(),"OK ", "Devices were deleted succesfully.");
	}

	public List<Device> getDevices(long roomId) {
		return this.getById(roomId).getDevices();
	}

	public OssResponse addDevice(long roomId, String macAddress, String name) {
		// First we check if there is enough IP-Addresses in this room
		List<String> ipAddress = this.getAvailableIPAddresses(roomId, 1);
		if( ipAddress.isEmpty() ){
			return new OssResponse(this.getSession(),"ERROR","There are no more free ip addresses in this room.");
		} 
		EntityManager em = getEntityManager();
		Device device = new Device();
		Room   room   = em.find(Room.class, roomId);
		User   owner  = this.getSession().getUser();
		if( ! owner.getRole().contains("sysadmins") ) {
			//non sysadmin user want to register his workstation
			boolean allowed = false;
			for( Group group : owner.getGroups() ) {
				if( this.checkMConfig(group,"AdHocAccess",String.valueOf(roomId)) ) {
					allowed = true;
					break;
				}
			}
			if( !allowed ) {
				if( this.checkMConfig(owner,"AdHocAccess",String.valueOf(roomId)) ) {
					allowed = true;
				}
			}
			if( allowed ) {
				HWConf hwconf = room.getHwconf();
				if( hwconf == null ) { 
					Query query = em.createNamedQuery("HWConf.getByName");
					query.setParameter("name", "BYOD");
					hwconf = (HWConf) query.getResultList().get(0);
				}
				device.setMac(macAddress);
				device.setName(name + "-" + owner.getUid().replaceAll("_", "-").replaceAll(".", ""));
				device.setIp(ipAddress.get(0).split(" ")[0]);
				device.setHwconf(hwconf);
			} else {
				return new OssResponse(this.getSession(),"ERROR","You have no rights to register devices in this room");
			}
		} else {
			device.setMac(macAddress);
			device.setIp(ipAddress.get(0).split(" ")[0]);
			if( name.equals("nextFreeName") ) {
				device.setName(ipAddress.get(0).split(" ")[1]);
			} else {
				device.setName(name);
			}
			device.setHwconf(room.getHwconf());
			logger.debug("Sysadmin register:" + device.getMac() +"#" +device.getIp() +"#" +device.getName());
		}
		//Check if the Device settings are OK
		DeviceController deviceController = new DeviceController(this.session);
		String error = deviceController.check(device, room);
		if( !error.isEmpty() ) {
			return new OssResponse(this.getSession(),"ERROR",error);
		}
		device.setOwner(owner);
		device.setRoom(room);
		logger.debug(device.toString());
		try {
			em.getTransaction().begin();
			em.persist(device);
			em.merge(room);
			em.getTransaction().commit();
		} catch (Exception e) {
			logger.error(e.getMessage());
			return new OssResponse(this.getSession(),"ERROR","Error by registering: " +  e.getMessage());
		} finally {
			em.close();
		}
		//Start plugin and create DHCP and salt configuration
		this.startPlugin("add_device", device);
		new DHCPConfig(this.session).Create();
		return new OssResponse(this.getSession(),"OK","Device was created succesfully.",device.getId());
	}

	public HWConf getHWConf(long roomId) {
		EntityManager em = getEntityManager();
		try {
			return em.find(Room.class, roomId).getHwconf();
		} catch (Exception e) {
			logger.error(e.getMessage());
			return null;
		} finally {
			em.close();
		}
	}

	public OssResponse setHWConf(long roomId, long hwConfId) {
		EntityManager em = getEntityManager();
		try {
			em.getTransaction().begin();
			Room room = em.find(Room.class, roomId);
			room.setHwconf(em.find(HWConf.class, hwConfId));
			em.merge(room);
			em.getTransaction().commit();
		} catch (Exception e) {
			logger.error(e.getMessage());
			return new OssResponse(this.getSession(),"ERROR", e.getMessage());
		} finally {
			em.close();
		}
		return new OssResponse(this.getSession(),"OK","The hardware configuration of the room was set succesfully.");
	}
	
	public OssResponse modify(Room room){
		EntityManager em = getEntityManager();
		Room oldRoom = this.getById(room.getId());
		HWConf hwconf = new CloneToolController(this.session).getById(room.getHwconfId());
		oldRoom.setDescription(room.getDescription());
		oldRoom.setHwconf(hwconf);
		oldRoom.setRoomType(room.getRoomType());
		oldRoom.setRows(room.getRows());
		oldRoom.setRoomControl(room.getRoomControl());
		oldRoom.setPlaces(room.getPlaces());
		try {
			em.getTransaction().begin();
			em.merge(oldRoom);
			em.getTransaction().commit();
		} catch (Exception e) {
			return new OssResponse(this.getSession(),"ERROR", e.getMessage());
		} finally {
			em.close();
		}
		this.startPlugin("modify_room", oldRoom);
		
		return new OssResponse(this.getSession(),"OK","The room was modified succesfully.");
	}

	public List<Room> getRooms(List<Long> roomIds) {
		List<Room> rooms = new ArrayList<Room>();
		for (Long id : roomIds ) {
			rooms.add(this.getById(id));
		}
		return rooms;
	}

	/*
	 * Control of printer in this room
	 */
	public OssResponse setDefaultPrinter(Long roomId, Long deviceId) {

		Room room = this.getById(roomId);
		DeviceController deviceController = new DeviceController(session);
		Device device = deviceController.getById(deviceId);
		if( room.getDefaultPrinter() != null && room.getDefaultPrinter().equals(device) ) {
			return new OssResponse(this.getSession(),"OK","The printer is already assigned to room.");
		}
		room.setDefaultPrinter(device);
		device.getDefaultInRooms().add(room);
		EntityManager em = getEntityManager();
		try {
			em.getTransaction().begin();
			em.merge(room);
			em.merge(device);
			em.getTransaction().commit();
		} catch (Exception e) {
			return new OssResponse(this.getSession(),"ERROR", e.getMessage());
		} finally {
			em.close();
		}
		return new OssResponse(this.getSession(),"OK","The default printer of the room was set succesfully.");
	}

	public OssResponse deleteDefaultPrinter(long roomId) {
		EntityManager em = getEntityManager();
		Room room = this.getById(roomId);
		Device device = room.getDefaultPrinter();
		if( device != null  ) {
			room.setDefaultPrinter(null);
			device.getDefaultInRooms().remove(room);
			try {
				em.getTransaction().begin();
				em.merge(room);
				em.merge(device);
				em.getTransaction().commit();
			} catch (Exception e) {
				return new OssResponse(this.getSession(),"ERROR", e.getMessage());
			} finally {
				em.close();
			}
		}
		return new OssResponse(this.getSession(),"OK","The default printer of the room was deleted succesfully.");
	}

	public OssResponse setAvailablePrinters(long roomId, List<Long> deviceIds) {
		EntityManager em = getEntityManager();
		Room room = this.getById(roomId);
		DeviceController deviceController = new DeviceController(session);
		try {
			em.getTransaction().begin();
			for( Device device : deviceController.getDevices(deviceIds)) {
				device.getAvailableInRooms().add(room);
				em.merge(device);
			}
			room.setAvailablePrinters(deviceController.getDevices(deviceIds));
			em.merge(room);
			em.getTransaction().commit();
			
		} catch (Exception e) {
			return new OssResponse(this.getSession(),"ERROR", e.getMessage());
		} finally {
			em.close();
		}
		return new OssResponse(this.getSession(),"OK","The available printers of the room was set succesfully.");
	}

	public OssResponse addAvailablePrinter(long roomId, long deviceId) {

		Room room = this.getById(roomId);
		DeviceController deviceController = new DeviceController(session);
		Device device = deviceController.getById(deviceId);
		if( room.getAvailablePrinters().contains(device) ) {
			return new OssResponse(this.getSession(),"OK","The printer is already assigned to room.");
		}
		room.getAvailablePrinters().add(device);
		device.getAvailableInRooms().add(room);
		EntityManager em = getEntityManager();
		try {
			em.getTransaction().begin();
			em.merge(room);
			em.merge(device);
			em.getTransaction().commit();
		} catch (Exception e) {
			return new OssResponse(this.getSession(),"ERROR", e.getMessage());
		} finally {
			em.close();
		}
		return new OssResponse(this.getSession(),"OK","The selected printer was added to the room.");
	}
	
	public OssResponse deleteAvailablePrinter(long roomId, long deviceId) {
		EntityManager em = getEntityManager();
		Room room = this.getById(roomId);
		DeviceController deviceController = new DeviceController(session);
		Device device = deviceController.getById(deviceId);
		room.getAvailablePrinters().remove(device);
		device.getAvailableInRooms().remove(room);
		try {
			em.getTransaction().begin();
			em.merge(room);
			em.merge(device);
			em.getTransaction().commit();
		} catch (Exception e) {
			return new OssResponse(this.getSession(),"ERROR", e.getMessage());
		} finally {
			em.close();
		}
		return new OssResponse(this.getSession(),"OK","The selected printer was removed from room.");
	}

	public OssResponse manageRoom(long roomId, String action, Map<String, String> actionContent) {
		OssResponse ossResponse = null;
		List<String> errors = new ArrayList<String>();
		DeviceController dc = new DeviceController(this.session);
		for( Device device : this.getById(roomId).getDevices() ) {
			//Do not control the own workstation
			if( this.session.getDevice().getId().equals(device.getId())) {
				continue;
			}
			ossResponse = dc.manageDevice(device.getId(), action, actionContent);
			if( ossResponse.getCode().equals("ERROR")) {
				errors.add(ossResponse.getValue());
			}
		}
		if( errors.isEmpty() ) {
			new OssResponse(this.getSession(),"OK", "Device control was applied.");
		} else {
			return new OssResponse(this.getSession(),"ERROR",String.join("<br>", errors));
		}
		return null;
	}

}
