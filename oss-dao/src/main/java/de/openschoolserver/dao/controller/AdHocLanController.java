/* (c) 2017 Péter Varkoly <peter@varkoly.de> - all rights reserved */
package de.openschoolserver.dao.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import de.openschoolserver.dao.*;

public class AdHocLanController extends Controller {

	Logger logger = LoggerFactory.getLogger(AdHocLanController.class);

	public AdHocLanController(Session session) {
		super(session);
		// TODO Auto-generated constructor stub
	}


	public Category getAdHocCategoryOfRoom(Room room) {
		for( Category category : room.getCategories() ) {
			logger.debug("getAdHocCategoryOfRoom" + category);
			if( category.getCategoryType().equals("AdHocAccess")) {
				return category;
			}
		}
		return null;
	}

	public Category getAdHocCategoryOfRoom(Long roomId) {
		Room room = new RoomController(session).getById(roomId);
		return (( room == null ) ? null :getAdHocCategoryOfRoom(room) );
	}

	public List<Group> getGroups() {
		ArrayList<Group> groups = new ArrayList<Group>();
		for( Room room : new RoomController(session).getByType("AdHocAccess") ) {
			for( Category category : room.getCategories() ) {
				if( category.getCategoryType().equals("AdHocAccess")) {
					groups.addAll(category.getGroups());
				}
			}
		}
		return groups;
	}

	public List<User> getUsers() {
		ArrayList<User> users = new ArrayList<User>();
		for( Room room : new RoomController(session).getByType("AdHocAccess") ) {
			for( Category category : room.getCategories() ) {
				if( category.getCategoryType().equals("AdHocAccess")) {
					users.addAll(category.getUsers());
				}
			}
		}
		return users;
	}


	public OssResponse add(Room room) {
		logger.debug("Add AdHocLan: " + room);
		//Search the BYOD HwConf
		if( room.getHwconf() == null ) {
			HWConf hwconf = new CloneToolController(session).getByName("BYOD");
			room.setHwconfId(hwconf.getId());
		}
		room.setRoomType("AdHocAccess");
		RoomController roomConrtoller = new RoomController(session);
		OssResponse ossResponseRoom =  roomConrtoller.add(room);
		logger.debug("Add AdHocLan after creating: " + room);
		if( ossResponseRoom.getCode().equals("ERROR")) {
			return ossResponseRoom;
		}
		Long roomId = ossResponseRoom.getObjectId();
		Category category = new Category();
		category.setCategoryType("AdHocAccess");
		category.setName(room.getName());
		category.setDescription(room.getDescription());
		category.setOwner(this.session.getUser());
		category.setPublicAccess(false);
		logger.debug("Add AdHocLan category: " + category);
		CategoryController categoryController = new CategoryController(session);
		OssResponse ossResponseCategory = categoryController.add(category);
		if( ossResponseCategory.getCode().equals("ERROR")) {
			roomConrtoller.delete(ossResponseRoom.getObjectId());
			return ossResponseCategory;
		}
		Long categoryId = ossResponseCategory.getObjectId();
		ossResponseCategory = categoryController.addMember(categoryId, "Room", roomId);
		logger.debug("Add room to Category:"+ categoryId + " " + roomId);
		if( ossResponseCategory.getCode().equals("ERROR")) {
			if( ossResponseRoom.getObjectId() != null ) {
				roomConrtoller.delete(ossResponseRoom.getObjectId());
			}
			if( ossResponseCategory.getObjectId() != null ) {
				categoryController.delete(ossResponseCategory.getObjectId());
			}
			return ossResponseCategory;
		}
		return ossResponseRoom;
	}

	public OssResponse putObjectIntoRoom(Long roomId, String objectType, Long objectId) {
		Long categoryId = getAdHocCategoryOfRoom(roomId).getId();
		if( categoryId == null ) {
			return new OssResponse(session,"ERROR","AdHocAccess not found");
		}
		return new CategoryController(session).addMember(categoryId, objectType, objectId);
	}


	public OssResponse deleteObjectInRoom(Long roomId, String objectType, Long objectId) {
		Long categoryId = getAdHocCategoryOfRoom(roomId).getId();
		if( categoryId == null ) {
			return new OssResponse(session,"ERROR","AdHocAccess not found");
		}
		return new CategoryController(session).deleteMember(categoryId, objectType, objectId);
	}
}

