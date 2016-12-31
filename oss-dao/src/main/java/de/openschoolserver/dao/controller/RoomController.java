/* (c) 2017 Péter Varkoly <peter@varkoly.de> - all rights reserved */
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
import de.openschoolserver.dao.Room;
import de.openschoolserver.dao.User;
import de.openschoolserver.dao.Session;
import de.openschoolserver.dao.AccessInRoom;
import de.openschoolserver.dao.tools.*;

public class RoomController extends Controller {

	public RoomController(Session session) {
		super(session);
	}

	public boolean isNameUnique(String name)
	{
		EntityManager em = getEntityManager();
		try {
			Query query = em.createNamedQuery("Room.getRoomByName");
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
			Query query = em.createNamedQuery("Room.getRoomByDescription");
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

	public boolean add(Room room){
		EntityManager em = getEntityManager();
		// First we check if the parameter are unique.
		if( ! this.isNameUnique(room.getName())){
			return false;
		}
		if( !this.isDescriptionUnique(room.getDescription())){
			return false;
		}
		// If the starIp is not given we have to search the next room IP
		if( room.getStartIP() == "" ) {
			room.setStartIP( getNextRoomIP(room.getNetMask()) );
		}
		try {
			em.getTransaction().begin();
			em.persist(room);
			em.getTransaction().commit();
		} catch (Exception e) {
			System.err.println(e.getMessage());
			return false;
		}
		return true;
	}

	public boolean delete(long roomId){
		EntityManager em = getEntityManager();
		DeviceController devController = new DeviceController(this.getSession());
		Room room = this.getById(roomId);
		List<Long> deviceIds = new ArrayList<Long>();
		for( Device device : room.getDevices()) {
			deviceIds.add(device.getId());
		}
		devController.delete(deviceIds);
		em.remove(room);
		return false;
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

	public String getNextRoomIP( int netMask ) {
		List<String>  startIPAddresses   = new ArrayList<String>();
		EntityManager em = getEntityManager();
		Query query = em.createNamedQuery("Room.findAll");
		for( Room room : (List<Room>) query.getResultList() ) {
			startIPAddresses.add(room.getStartIP());
		}
		IPv4 ipv4 = new IPv4();
		List<String> sortedIPAddresses = ipv4.sortIPAddresses(startIPAddresses);
		String lastNetworkIP = sortedIPAddresses.get(sortedIPAddresses.size()-1);

		// Find the net of the last room
		query = em.createQuery("SELECT r.netmask FROM WHERE startIP = :startIP",Room.class);
		query.setParameter("startIP", lastNetworkIP);
		int lastNetMask = (int) query.getSingleResult();
		//Find the next free net with the network mask of the last room
		IPv4Net net = new IPv4Net( lastNetworkIP + "/" + lastNetMask );
		String nextNet = net.getNext();

		//Now set the last network IP to the last IP in the last network.
		lastNetworkIP = net.getLast();

		//This could be our net
		net = new IPv4Net(nextNet + "/" + netMask );
		while ( net.contains(lastNetworkIP)) {
			//If the end of the last network is in our net it is wrong.
			//In this case get the next one net address
			nextNet = net.getNext();
			net = new IPv4Net(nextNet + "/" + netMask );
		}

		// Check if the nextNet is in school net.
		String lastIP = net.getNext();
		net = new IPv4Net( this.getConfigValue("SCHOOL_NETWORK") + "/" + this.getConfigValue("SCHOOL_NETMASK") );
		if( ! net.contains(lastIP) )
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
				userMap.put("host", device.getName());
				userMap.put("id", String.valueOf(user.getId()));
				userMap.put("uid", user.getUid());
				userMap.put("sureName", user.getSureName());
				userMap.put("givenName", user.getGivenName());
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
	public void setAccessList(long roomId,List<AccessInRoom> AccessList){
		Room room = this.getById(roomId);
		room.setAccessInRooms(AccessList);
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

		// Direct internet
		program[3] = "direct";
		if( access.getDirect() )
			program[1] = "1";
		else
			program[1] = "0";
		OSSShellTools.exec(program, reply, error, null);

		// Portal Access
		program[3] = "portal";
		if( access.getPortal() )
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
		if( access.getLogin()) 
			program[1] = "1";
		else
			program[1] = "0";
		OSSShellTools.exec(program, reply, error, null);
	}

	/*
	 * Sets the actual access status in a room 
	 */
	public void setAccessStatus(long roomId, AccessInRoom access) {
		Room room = this.getById(roomId);
		this.setAccessStatus(room, access);
	}

	/*
	 * Sets the scheduled access status in all rooms
	 */
	public void setScheduledAccess(){
		EntityManager em = getEntityManager();
		Calendar rightNow = Calendar.getInstance();
		rightNow.set(Calendar.SECOND, 0);
		rightNow.set(Calendar.MILLISECOND,0);
		Query query = em.createNamedQuery("AccessInRoom.findActualAccesses");
		query.setParameter("time", rightNow.getTime());
		for( AccessInRoom access : (List<AccessInRoom>) query.getResultList() ){
			Room room = access.getRoom();
			this.setAccessStatus(room, access);
		}	
	}

	/*
	 * Sets the actual access status in a room
	 */
	public AccessInRoom getAccessStatus(Room room) {

		AccessInRoom access = new AccessInRoom();
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
			access.setDirect(true);

		// Portal Access
		program[2] = "portal";
		OSSShellTools.exec(program, reply, error, null);
		if( reply.toString() == "0" )
			access.setPortal(false);

		// Proxy Access
		program[2] = "proxy";
		OSSShellTools.exec(program, reply, error, null);
		if( reply.toString() == "0" )
			access.setProxy(false);

		// Printing Access
		program[2] = "printing";
		OSSShellTools.exec(program, reply, error, null);
		if( reply.toString() == "0" )
			access.setPrinting(false);

		// Login
		program[2] = "login";
		OSSShellTools.exec(program, reply, error, null);
		if( reply.toString() == "0" ) 
			access.setLogin(false);

		return access;
	}

	/*
	 * Sets the actual access status in a room 
	 */
	public AccessInRoom getAccessStatus(long roomId) {
		Room room = this.getById(roomId);
		return this.getAccessStatus(room);
	}

}
