/* (c) 2017 P��ter Varkoly <peter@varkoly.de> - all rights reserved */
package de.openschoolserver.dao.controller;

import java.util.ArrayList;


import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Calendar;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.sql.Time;

import de.openschoolserver.dao.Device;
import de.openschoolserver.dao.HWConf;
import de.openschoolserver.dao.Response;
import de.openschoolserver.dao.Room;
import de.openschoolserver.dao.User;
import de.openschoolserver.dao.Session;
import de.openschoolserver.dao.AccessInRoom;
import de.openschoolserver.dao.AccessInRoomACT;
import de.openschoolserver.dao.AccessInRoomFW;
import de.openschoolserver.dao.AccessInRoomPIT;
import de.openschoolserver.dao.tools.*;

public class RoomController extends Controller {

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
			//logger.error(e.getMessage());
			System.err.println(e.getMessage());
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
			//logger.error(e.getMessage());
			System.err.println(e.getMessage());
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
			// logger.error(e.getMessage());
			System.err.println(e.getMessage()); //TODO
			return null;
		} finally {
			em.close();
		}
	}

	public List<Room> getAll() {
		EntityManager em = getEntityManager();
		try {
			Query query = em.createNamedQuery("Room.findAll"); 
			return query.getResultList();
		} catch (Exception e) {
			//logger.error(e.getMessage());
			System.err.println(e.getMessage()); //TODO
			return new ArrayList<>();
		} finally {
			em.close();
		}
	}

	public Response add(Room room){
		EntityManager em = getEntityManager();
		// First we check if the parameter are unique.
		if( ! this.isNameUnique(room.getName())){
			return new Response(this.getSession(),"ERROR", "Room name is not unique.");
		}
		if( !this.isDescriptionUnique(room.getDescription())){
			return new Response(this.getSession(),"ERROR", "Room description is not unique.");
		}
		// If no network was configured we will use net school network.
		if( room.getNetwork().equals(""))
			room.setNetwork(this.getConfigValue("SCHOOL_NETWORK") + "/" + this.getConfigValue("SCHOOL_NETMASK"));
		
		// If the starIp is not given we have to search the next room IP
		if( room.getStartIP() == "" ) {
			room.setStartIP( getNextRoomIP(room.getNetwork(),room.getNetMask()) );
		}
		room.setHwconf(em.find(HWConf.class,room.getHwconfId()));
		try {
			em.getTransaction().begin();
			em.persist(room);
			em.getTransaction().commit();
		} catch (Exception e) {
			System.err.println(e.getMessage());
			return new Response(this.getSession(),"ERROR ", e.getMessage());
		}
		return new Response(this.getSession(),"OK ", "Room was created succesfully.");
	}

	public Response delete(long roomId){
		EntityManager em = getEntityManager();
		em.getTransaction().begin();
		DeviceController devController = new DeviceController(this.getSession());
		Room room = this.getById(roomId);
		List<Long> deviceIds = new ArrayList<Long>();
		for( Device device : room.getDevices()) {
			deviceIds.add(device.getId());
		}
		devController.delete(deviceIds);
		em.remove(room);
		em.getTransaction().commit();
		return new Response(this.getSession(),"OK", "Room was removed successfully.");
	}

	/*
	 * Return the list of the available adresses in the room
	 */
	public List<String> getAvailableIPAddresses(long roomId){
		Room room   = this.getById(roomId);
		IPv4Net net = new IPv4Net(room.getStartIP() + "/" + room.getNetMask());
		List<String> allIPs  = net.getAvailableIPs(0);
		List<String> usedIPs = new ArrayList<String>();
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
		List<String> availableIPs = new ArrayList<String>();
		int i = 0;
		for( String IP : net.getAvailableIPs(0) ){
			String name =  this.isIPUnique(IP);
			if( name == "" ){
				availableIPs.add(String.format("%s %s-%02d", IP,room.getName(),i));
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
		if( subnet == null || subnet.equals("") ){
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
		List<AccessInRoom> accesses = new ArrayList<AccessInRoom>();
		Room room = this.getById(roomId);
		for( AccessInRoom access : room.getAccessInRooms() ) {
			HashMap accessMap = new HashMap<String, Object>();
			if( access.getAccessType() == "DEFAULT" || access.getAccessType() =="FW") {
				accessMap.put("direct",   access.getAccessInRoomFW().getDirect());
				accessMap.put("login",    access.getAccessInRoomFW().getLogin());
				accessMap.put("portal",   access.getAccessInRoomFW().getPortal());
				accessMap.put("printing", access.getAccessInRoomFW().getPrinting());
				accessMap.put("proxy",    access.getAccessInRoomFW().getProxy());
			}
			if( access.getAccessType() == "FW" || access.getAccessType() == "ACT" ) {
				accessMap.put("Monday", access.getAccessInRoomPIT().getMonday());
				accessMap.put("Tusday", access.getAccessInRoomPIT().getTusday());
				accessMap.put("Wednesday", access.getAccessInRoomPIT().getWednesday());
				accessMap.put("Thursday", access.getAccessInRoomPIT().getThursday());
				accessMap.put("Friday", access.getAccessInRoomPIT().getFriday());
				accessMap.put("Saturday", access.getAccessInRoomPIT().getSaturday());
				accessMap.put("Sunday", access.getAccessInRoomPIT().getSunday());
				accessMap.put("Holiday", access.getAccessInRoomPIT().getHoliday());
			}
			if( access.getAccessType() == "ACT" ) {
				accessMap.put("action", access.getAccessInRoomACT().getAction());
			}
			AccessInRoom tmp = new AccessInRoom();
			tmp.setAccessType(access.getAccessType());
			tmp.setAccess(accessMap);
			accesses.add(tmp);
		}
		return accesses;
	}

	/*
	 * Sets the list of accesses in a room
	 */
	public Response setAccessList(long roomId,List<AccessInRoom> AccessList){
		Room room = this.getById(roomId);
		//Es ist nicht soo einfach. Als erstes werden die Vorhandene gel��scht und dann die Neue angelegt.
		EntityManager em = getEntityManager();
		em.getTransaction().begin();
		for(AccessInRoom access : room.getAccessInRooms() ){
			em.remove(access);
		}
		for(AccessInRoom access : AccessList ){
			AccessInRoom    tmp    = new AccessInRoom();
			tmp.setAccessType(access.getAccessType());
			
			AccessInRoomFW  tmpFW  = new AccessInRoomFW();
			AccessInRoomPIT tmpPIT = new AccessInRoomPIT();
			AccessInRoomACT tmpACT = new AccessInRoomACT();
			if( access.getAccessType() == "DEFAULT" || access.getAccessType() =="FW") {
				tmpFW.setDirect((Boolean)   access.getAccess().get("direct"));
				tmpFW.setLogin((Boolean)    access.getAccess().get("login"));
				tmpFW.setPortal((Boolean)   access.getAccess().get("portal"));
				tmpFW.setPrinting((Boolean) access.getAccess().get("printing"));
			}
			if( access.getAccessType() == "FW" || access.getAccessType() == "ACT" ) {
				tmpPIT.setMonday((Boolean) access.getAccess().get("Monday"));
				tmpPIT.setMonday((Boolean) access.getAccess().get("Monday"));
				tmpPIT.setMonday((Boolean) access.getAccess().get("Monday"));
				tmpPIT.setMonday((Boolean) access.getAccess().get("Monday"));
				tmpPIT.setMonday((Boolean) access.getAccess().get("Monday"));
				tmpPIT.setMonday((Boolean) access.getAccess().get("Monday"));
				tmpPIT.setMonday((Boolean) access.getAccess().get("Monday"));
				tmpPIT.setMonday((Boolean) access.getAccess().get("Monday"));
			}
			if(access.getAccessType() == "ACT" ) {
				tmpACT.setAction((String) access.getAccess().get("action"));
			}
			switch (access.getAccessType()) {
			case "DEFAULT":
				tmp.setAccessInRoomFW(tmpFW);
				break;
			case "FW":
				tmp.setAccessInRoomFW(tmpFW);
				tmp.setAccessInRoomPIT(tmpPIT);
				break;
			case "ACT":
				tmp.setAccessInRoomACT(tmpACT);
				tmp.setAccessInRoomPIT(tmpPIT);
				break;
			}
			em.persist(tmp);
		}
		em.getTransaction().commit();
		return new Response(this.getSession(),"OK","Acces was created succesfully");
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

		if(access.getAccessType() == "ACT" ) {
			//TODO 	
		}
		else
		{
			// Direct internet
			program[3] = "direct";
			if( access.getAccessInRoomFW().getDirect() )
				program[1] = "1";
			else
				program[1] = "0";
			OSSShellTools.exec(program, reply, error, null);

			// Portal Access
			program[3] = "portal";
			if( access.getAccessInRoomFW().getPortal())
				program[1] = "1";
			else
				program[1] = "0";
			OSSShellTools.exec(program, reply, error, null);

			// Proxy Access
			program[3] = "proxy";
			if( access.getAccessInRoomFW().getProxy() )
				program[1] = "1";
			else
				program[1] = "0";
			OSSShellTools.exec(program, reply, error, null);

			// Printing Access
			program[3] = "printing";
			if( access.getAccessInRoomFW().getPrinting() )
				program[1] = "1";
			else
				program[1] = "0";
			OSSShellTools.exec(program, reply, error, null);

			// Login
			program[3] = "login";
			if( access.getAccessInRoomFW().getLogin() ) 
				program[1] = "1";
			else
				program[1] = "0";
			OSSShellTools.exec(program, reply, error, null);
		}
	}

	/*
	 * Sets the actual access status in a room 
	 */
	public Response setAccessStatus(long roomId, AccessInRoom access) {
		Room room = this.getById(roomId);
		this.setAccessStatus(room, access);
		return new Response(this.getSession(),"OK", "Access state in "+room.getName()+" was set succesfully." );
	}

	/*
	 * Sets the scheduled access status in all rooms
	 */
	public Response setScheduledAccess(){
		EntityManager em = getEntityManager();
		Calendar rightNow = Calendar.getInstance();
		String   actTime  = String.format("%02d:%02d", rightNow.get(Calendar.HOUR_OF_DAY),rightNow.get(Calendar.MINUTE));
		Query query = em.createNamedQuery("AccessInRoom.findActualAccesses");
		query.setParameter("time", actTime);
		for( AccessInRoom access : (List<AccessInRoom>) query.getResultList() ){
			Room room = access.getRoom();
			this.setAccessStatus(room, access);
		}
		return new Response(this.getSession(),"OK", "Scheduled access states where set succesfully." );
	}

	/*
	 * Gets the actual access status in a room
	 */
	public AccessInRoom getAccessStatus(Room room) {

		AccessInRoom access = new AccessInRoom();
		access.setAccessType("FW");
		HashMap<String, Object> accessMap = new HashMap<String, Object>();
		String[] program = new String[4];
		program[0] = "/usr/sbin/oss-get-access-state.sh";
		program[1] = room.getStartIP() + "/" + room.getNetMask();
		access.setRoom(room);
		StringBuffer reply = new StringBuffer();
		StringBuffer error = new StringBuffer();

		// Direct internet
		program[2] = "direct";
		OSSShellTools.exec(program, reply, error, null);
		if( reply.toString() == "1" )
			accessMap.put("direct", true);
		else
			accessMap.put("direct", false);

		// Portal Access
		program[2] = "portal";
		OSSShellTools.exec(program, reply, error, null);
		if( reply.toString() == "1" )
			accessMap.put("portal", true);
		else
			accessMap.put("portal", false);

		// Proxy Access
		program[2] = "proxy";
		OSSShellTools.exec(program, reply, error, null);
		if( reply.toString() == "1" )
			accessMap.put("proxy", true);
		else
			accessMap.put("proxy", false);

		// Printing Access
		program[2] = "printing";
		OSSShellTools.exec(program, reply, error, null);
		if( reply.toString() == "1" )
			accessMap.put("printing", true);
		else
			accessMap.put("printing", false);

		// Login
		program[2] = "login";
		OSSShellTools.exec(program, reply, error, null);
		if( reply.toString() == "1" )
			accessMap.put("login", true);
		else
			accessMap.put("login", false);
		
		access.setAccess(accessMap);
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
	public Response addDevices(long roomId,List<Device> devices){
		EntityManager em = getEntityManager();
		Room room = this.getById(roomId);
		DeviceController deviceController = new DeviceController(this.session);
		StringBuilder error = new StringBuilder();
		for(Device device : devices) {
			error.append(deviceController.check(device, room));
			device.setRoom(room);
			if(device.getHwconfId() != -1){
				device.setHwconf(em.find(HWConf.class,device.getHwconfId()));
			} else {
				device.setHwconf(room.getHwconf());
			}
			room.addDevice(device);
		}
		if(error.length() > 0){
			em.close();
			return new Response(this.getSession(),"ERROR",error.toString());
		}
		try {
			em.getTransaction().begin();
			em.merge(room);
			em.getTransaction().commit();
		} catch (Exception e) {
			System.err.println(e.getMessage());
			return new Response(this.getSession(),"ERROR", e.getMessage());
		} finally {
			em.close();
		}
		DHCPConfig dhcpconfig = new DHCPConfig(this.session);
		dhcpconfig.Create();
		return new Response(this.getSession(),"OK", "Devices were created succesfully." );
	}

	/*
	 * Deletes devices in the room
	 */
	public Response deleteDevices(long roomId,List<Long> deviceIDs){
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
		} catch (Exception e) {
			System.err.println(e.getMessage());
			return new Response(this.getSession(),"ERROR", e.getMessage());
		} finally {
			em.close();
		}
		return new Response(this.getSession(),"OK ", "Devices were deleted succesfully.");
	}

	public List<Device> getDevices(long roomId) {
		return this.getById(roomId).getDevices();
	}

	public Response addDevice(long roomId, String macAddress, String name) {
		// First we check if there is enough IP-Addresses in this room
		List<String> ipAddress = this.getAvailableIPAddresses(roomId, 1);
		if( ipAddress.isEmpty() ){
			return new Response(this.getSession(),"ERROR","There are no more free ip addresses in this room.");
		} 
		EntityManager em = getEntityManager();
		Device device = new Device();
		Room   room   = em.find(Room.class, roomId);
		User   owner  = this.getSession().getUser();
		if( ! owner.getRole().contains("sysadmins") ) {
			//non sysadmin user want to register his workstation
			Query query = em.createNamedQuery("User.checkMConfig");
			query.setParameter("user_id", this.getSession().getUserId()).setParameter("keyword", "adhocRoom").setParameter("varlue", roomId);
			if( query.getResultList().isEmpty() ) {
				return new Response(this.getSession(),"ERROR","You have no rights to register devices in this room");
			}
			else
			{
				device.setMac(macAddress);
				device.setName(name + "-" + owner.getUid());
				device.setOwner(owner);
				device.setIp(ipAddress.get(0).split(" ")[0]);
				device.setHwconf(null);
			}
		} else {
			device.setMac(macAddress);
			device.setIp(ipAddress.get(0).split(" ")[0]);
			device.setIp(ipAddress.get(0).split(" ")[0]);
			if( name == "nextFreeName" ) {
				device.setName(ipAddress.get(0).split(" ")[1]);
			} else {
				device.setName(name);
			}
		}
		//Check if the Device settings are OK
		DeviceController deviceController = new DeviceController(this.session);
		String error = deviceController.check(device, room);
		if( error != "" ) {
			return new Response(this.getSession(),"ERROR",error);
		}
		device.setRoom(room);
		room.addDevice(device);
		try {
			em.getTransaction().begin();
			em.merge(room);
			em.getTransaction().commit();
		} catch (Exception e) {
			System.err.println(e.getMessage());
			return new Response(this.getSession(),"ERROR", e.getMessage());
		} finally {
			em.close();
		}
		return new Response(this.getSession(),"OK","Device was created succesfully.");
	}

	public HWConf getHWConf(long roomId) {
		EntityManager em = getEntityManager();
		try {
			return em.find(Room.class, roomId).getHwconf();
		} catch (Exception e) {
			System.err.println(e.getMessage()); //TODO
			return null;
		} finally {
			em.close();
		}
	}

	public Response setHWConf(long roomId, long hwConfId) {
		EntityManager em = getEntityManager();
		try {
			Room room = em.find(Room.class, roomId);
			room.setHwconf(em.find(HWConf.class, hwConfId));
		} catch (Exception e) {
			System.err.println(e.getMessage()); //TODO
			return new Response(this.getSession(),"ERROR", e.getMessage());
		} finally {
			em.close();
		}
		return new Response(this.getSession(),"OK","The hardware configuration of the room was set succesfully.");
	}
}
