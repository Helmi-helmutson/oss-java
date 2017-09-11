/* (c) 2017 Péter Varkoly <peter@varkoly.de> - all rights reserved */
package de.openschoolserver.dao.controller;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import java.util.ArrayList;
import de.openschoolserver.dao.*;

public class AdHocLanController extends Controller {

	public AdHocLanController(Session session) {
		super(session);
		// TODO Auto-generated constructor stub
	}

	public List<Long> getObjectIdsOfRoom(Long roomId, String objectType) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<User> getUsers() {
		UserController userController = new UserController(session);
		ArrayList<User> users = new ArrayList<User>();
		for(OSSMConfig mconfig : this.getMConfigs("User", "AdHocAccess")) {
			users.add(userController.getById(mconfig.getObjectId()));
		}
		return users;
	}
	
	public List<Group> getGroups() {
		GroupController groupController = new GroupController(session);
		ArrayList<Group> groups = new ArrayList<Group>();
		for(OSSMConfig mconfig : this.getMConfigs("Group", "AdHocAccess")) {
			groups.add(groupController.getById(mconfig.getObjectId()));
		}
		return groups;
	}

	public List<Room> getRooms() {
		RoomController roomController = new RoomController(session);
		ArrayList<Room> rooms = new ArrayList<Room>();
		if( this.isSuperuser() ) {
			for(OSSMConfig mconfig : this.getMConfigs("Room", "AdHocAccess")) {
				rooms.add(roomController.getById(mconfig.getObjectId()));
			}
		} else {
			for( String value : this.getMConfig(this.session.getUser(),"AdHocAccess" )) {
				rooms.add(roomController.getById(Long.parseLong(value)));
			}
		}
		return rooms;
	}

	public List<Device> getDevices() {
		ArrayList<Device> devices = new ArrayList<Device>();
		return devices;
	}

	public Response add(Room room) {
		EntityManager em = getEntityManager();
		//Search the BYOD HwConf
		if( room.getHwconf() == null ) {
			Query query = em.createNamedQuery("HWConf.getByName");
			query.setParameter("name", "BYOD");
			HWConf hwconf = (HWConf) query.getResultList().get(0);
			room.setHwconf(hwconf);
		}
		room.setRoomType("AdHocAccess");
		RoomController roomConrtoller = new RoomController(session);
		return roomConrtoller.add(room);
	}

	public Response putObjectIntoRoom(Long roomId, String objectType, Long objectId) {
		switch(objectType) {
		case("User"):
			UserController userController = new UserController(session);
			User user = userController.getById(objectId);
			return this.addMConfig(user, "AdHocAccess", String.valueOf(roomId));
		case("Group"):
			GroupController groupController = new GroupController(session);
			Group group = groupController.getById(objectId);
			return this.addMConfig(group, "AdHocAccess", String.valueOf(roomId));
		}
		return new Response(this.getSession(),"ERROR","Invalid Object Type");
	}
}
 