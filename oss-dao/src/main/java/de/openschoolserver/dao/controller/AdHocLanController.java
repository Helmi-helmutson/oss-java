/* (c) 2017 Péter Varkoly <peter@varkoly.de> - all rights reserved */
package de.openschoolserver.dao.controller;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import java.util.ArrayList;
import de.openschoolserver.dao.*;

@SuppressWarnings("unchecked")
public class AdHocLanController extends Controller {

	public AdHocLanController(Session session) {
		super(session);
		// TODO Auto-generated constructor stub
	}


	/*
	 * Returns a list of object Ids which habe AdHocAccess in
	 */
	public List<Long> getObjectIdsOfRoom(Long roomId, String objectType) {
		ArrayList<Long> objectIds = new ArrayList<Long>();
		EntityManager em = getEntityManager();
		Query query = em.createNamedQuery("OSSMConfig.getAllObject").setParameter("keyword", "AdHocAccess");
		query.setParameter("objectType", objectType).setParameter("value", String.valueOf(roomId));
		for( OSSMConfig mConfig: (List<OSSMConfig>) query.getResultList() ) {
			objectIds.add(mConfig.getObjectId());
		}
		return objectIds;
	}

	public List<Long> getUsers() {
		ArrayList<Long> userIds = new ArrayList<Long>();
		for(OSSMConfig mconfig : this.getMConfigs("User", "AdHocAccess")) {
			userIds.add(mconfig.getObjectId());
		}
		return userIds;
	}
	
	public List<Long> getGroups() {
		ArrayList<Long> groupIds = new ArrayList<Long>();
		for(OSSMConfig mconfig : this.getMConfigs("Group", "AdHocAccess")) {
			groupIds.add(mconfig.getObjectId());
		}
		return groupIds;
	}

	public List<Room> getRooms() {
		ArrayList<Room> rooms = new ArrayList<Room>();
		RoomController rc = new RoomController(this.session);
		if( this.isSuperuser() ) {
			for( Room room : new RoomController(this.session).getAll() ) {
				if( room.getRoomType().equals("AdHocAccess")) {
					rooms.add(room);
				}
			}
		} else {
			for( String value : this.getMConfigs(this.session.getUser(),"AdHocAccess" )) {
				Room room = rc.getById(Long.parseLong(value));
				if( !rooms.contains(room)) {
					rooms.add(room);
				}
			}
			for( Group group : this.session.getUser().getGroups() ) {
				for( String value : this.getMConfigs(group,"AdHocAccess" )) {
					Room room = rc.getById(Long.parseLong(value));
					if( !rooms.contains(room)) {
						rooms.add(room);
					}
				}	
			}
		}
		return rooms;
	}

	public OssResponse add(Room room) {
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

	public OssResponse putObjectIntoRoom(Long roomId, String objectType, Long objectId) {
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
		return new OssResponse(this.getSession(),"ERROR","Invalid Object Type");
	}
}
 