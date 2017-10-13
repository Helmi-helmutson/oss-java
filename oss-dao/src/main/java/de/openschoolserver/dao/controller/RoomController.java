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

	public List<Room> getAllToRegister() {
		EntityManager em = getEntityManager();
		try {
			if( this.getSession().getUser().getRole().contains("sysadmins") ) {
				Query query = em.createNamedQuery("Room.findAllToRegister"); 
				return query.getResultList();
			} else {
				List<Room> rooms = new ArrayList<Room>();
				for(String roomid : this.getMConfig(this.session.getUser(),"AdHocAccess" ) ) {
					rooms.add(this.getById(Long.parseLong(roomid)));
				}
				for(Group group : this.session.getUser().getGroups() ) {
					for(String roomid : this.getMConfig(group,"AdHocAccess" ) ) {
						rooms.add(this.getById(Long.parseLong(roomid)));
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
		EntityManager em = getEntityManager();

		// First we check if the parameter are unique.
		if( !this.isNameUnique(room.getName())){
			return new OssResponse(this.getSession(),"ERROR", "Room name is not unique.");
		}
		if( !this.isDescriptionUnique(room.getDescription())){
			return new OssResponse(this.getSession(),"ERROR", "Room description is not unique.");
		}

		// If no network was configured we will use net school network.
		if( room.getNetwork().isEmpty() ) {
			room.setNetwork(this.getConfigValue("SCHOOL_NETWORK") + "/" + this.getConfigValue("SCHOOL_NETMASK"));
		}

		// If the starIp is not given we have to search the next room IP
		if( room.getStartIP().isEmpty() ) {
			room.setStartIP( getNextRoomIP(room.getNetwork(),room.getNetMask()) );
		}

		//		Set default control mode
		if( room.getRoomControl().isEmpty() ) {
			room.setRoomControl("inRoom");
		}
		room.setHwconf(em.find(HWConf.class,room.getHwconfId()));
		try {
			em.getTransaction().begin();
			em.persist(room);
			em.getTransaction().commit();
		} catch (Exception e) {
			logger.error(e.getMessage());
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
		String subnet = this.getConfigValue("SCHOOL_NETWORK") + "/" + this.getConfigValue("SCHOOL_NETMASK");
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
		String subnet = this.getConfigValue("SCHOOL_NETWORK") + "/" + this.getConfigValue("SCHOOL_NETMASK");
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
			subnet = this.getConfigValue("SCHOOL_NETWORK") + "/" + this.getConfigValue("SCHOOL_NETMASK");
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
				userMap.put("sureName", user.getSureName());
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
		return new OssResponse(this.getSession(),"OK", "Access state in "+room.getName()+" was set succesfully." );
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
		DeviceController deviceController = new DeviceController(this.session);
		StringBuilder error = new StringBuilder();
		List<String> ipAddress;
		for(Device device : devices) {
			//Remove trailing and ending spaces.
			device.setName(device.getName().trim());
			ipAddress = this.getAvailableIPAddresses(roomId, 2);
			if( device.getIp().isEmpty() ){
				if( ipAddress.isEmpty() ) {
					return new OssResponse(this.getSession(),"ERROR","There are no more free ip addresses in this room.");
				}
				if( device.getName().isEmpty() ) {
				  device.setName(ipAddress.get(0).split(" ")[1]);
				}
				device.setIp(ipAddress.get(0).split(" ")[0]);
			}
			if( !device.getWlanMac().isEmpty() ){
				if( ipAddress.size() < 2 ) {
					return new OssResponse(this.getSession(),"ERROR","There are no more free ip addresses in this room.");
				}
				device.setWlanIp(ipAddress.get(1).split(" ")[0]);
			}
			error.append(deviceController.check(device, room));
			device.setRoom(room);
			if(device.getHwconfId() == null){
				device.setHwconf(room.getHwconf());
			} else {
				device.setHwconf(em.find(HWConf.class,device.getHwconfId()));
			}
			room.addDevice(device);
			if(error.length() > 0){
				em.close();
				return new OssResponse(this.getSession(),"ERROR",error.toString());
			}
			try {
				em.getTransaction().begin();
				em.merge(room);
				em.getTransaction().commit();
			} catch (Exception e) {
				logger.error(e.getMessage());
				em.close();
				return new OssResponse(this.getSession(),"ERROR", e.getMessage());
			}
		}
		em.close();
		UserController userController = new UserController(this.session);
		for(Device device : devices) {
			this.startPlugin("add_device", device);
			// We'll create only for fatClients workstation users
			if( device.getHwconf().getDeviceType().equals("fatClient")) {
				User user = new User();
				user.setUid(device.getName());
				user.setSureName(device.getName() + "  Workstation-User");
				user.setRole("workstations");
				//TODO do not ignore response.
				OssResponse answer = userController.add(user);
				logger.debug(answer.getValue());
			}
		}
		DHCPConfig dhcpconfig = new DHCPConfig(this.session);
		dhcpconfig.Create();
		return new OssResponse(this.getSession(),"OK", "Devices were created succesfully." );
	}

	/*
	 * Deletes devices in the room
	 */
	public OssResponse deleteDevices(long roomId,List<Long> deviceIDs){
		EntityManager em = getEntityManager();
		Room room = em.find(Room.class, roomId);
		for(Long deviceId : deviceIDs) {
			Device device = em.find(Device.class, deviceId);
			room.removeDevice(device);
		}
		try {
			em.getTransaction().begin();
			em.merge(room);
			em.getTransaction().commit();
			em.getEntityManagerFactory().getCache().evictAll();
		} catch (Exception e) {
			logger.error(e.getMessage());
			return new OssResponse(this.getSession(),"ERROR", e.getMessage());
		} finally {
			em.close();
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
				device.setOwner(owner);
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
			logger.debug("Sysadmin register:" + device.getMac() +"#" +device.getIp() +"#" +device.getName());
		}
		//Check if the Device settings are OK
		DeviceController deviceController = new DeviceController(this.session);
		String error = deviceController.check(device, room);
		if( !error.isEmpty() ) {
			return new OssResponse(this.getSession(),"ERROR",error);
		}
		device.setRoom(room);
		room.addDevice(device);
		try {
			em.getTransaction().begin();
			em.merge(room);
			em.getTransaction().commit();
		} catch (Exception e) {
			logger.error(e.getMessage());
			return new OssResponse(this.getSession(),"ERROR", e.getMessage());
		} finally {
			em.close();
		}
		//Start plugin and create DHCP and salt configuration
		this.startPlugin("add_device", device);
		new DHCPConfig(this.session).Create();
		return new OssResponse(this.getSession(),"OK","Device was created succesfully.");
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
			Room room = em.find(Room.class, roomId);
			room.setHwconf(em.find(HWConf.class, hwConfId));
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
		oldRoom.setDescription(room.getDescription());
		oldRoom.setHwconf(room.getHwconf());
		oldRoom.setRoomType(room.getRoomType());
		oldRoom.setRows(room.getRows());
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
}
