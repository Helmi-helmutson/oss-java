/* (c) 2017 Péter Varkoly <peter@varkoly.de> - all rights reserved  */
package de.openschoolserver.api.resourceimpl;

import java.io.InputStream;



import java.util.List;
import java.util.ArrayList;
import java.util.Map;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import de.openschoolserver.api.resources.EducationResource;
import de.openschoolserver.api.resources.Resource;
import de.openschoolserver.dao.Category;
import de.openschoolserver.dao.Group;
import de.openschoolserver.dao.Response;
import de.openschoolserver.dao.Session;
import de.openschoolserver.dao.controller.*;

public class EducationResourceImpl implements Resource, EducationResource {

	public EducationResourceImpl() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public Response createSmartRoom(Session session, Category smartRoom) {
		EducationController educationController = new EducationController(session);
		return educationController.createSmartRoom(smartRoom);
	}

	@Override
	public Response modifySmartRoom(Session session, long roomId, Category smartRoom) {
		EducationController educationController = new EducationController(session);
		return educationController.modifySmartRoom(roomId, smartRoom);
	}

	@Override
	public Response deleteSmartRoom(Session session, long roomId) {
		EducationController educationController = new EducationController(session);
		return educationController.deleteSmartRoom(roomId);
	}

	@Override
	public List<Long> getMyRooms(Session session) {
		EducationController educationController = new EducationController(session);
		return educationController.getMyRooms();
	}

	@Override
	public List<List<Long>> getRoom(Session session, long roomId) {
		EducationController educationController = new EducationController(session);
		return educationController.getRoom(roomId);
	}

	@Override
	public List<String> getAvailableRoomActions(Session session, long roomId) {
		EducationController educationController = new EducationController(session);
		return educationController.getAvailableRoomActions(roomId);
	}

	@Override
	public Response manageRoom(Session session, long roomId, String action, Map<String, String> actionContent) {
		EducationController educationController = new EducationController(session);
		return educationController.manageRoom(roomId,action,actionContent);
	}

	@Override
	public Response createGroup(Session session, Group group) {
		EducationController educationController = new EducationController(session);
		return educationController.createGroup(group);
	}

	@Override
	public Response modifyGroup(Session session, long groupId, Group group) {
		EducationController educationController = new EducationController(session);
		return educationController.modifyGroup(groupId, group);
	}

	@Override
	public Response removeGroup(Session session, long groupId) {
		EducationController educationController = new EducationController(session);
		return educationController.deleteGroup(groupId);
	}

	@Override
	public Response logOut(Session session, long userId, long deviceId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Response logIn(Session session, long userId, long roomId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> getAvailableUserActions(Session session, long userId) {
		EducationController educationController = new EducationController(session);
		return educationController.getAvailableUserActions(userId);
	}

	@Override
	public Response manageUSer(Session session, long userId, long deviceId, String action,
			Map<String, String> actionContent) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public List<String> getAvailableDeviceActions(Session session, long deviceId) {
		EducationController educationController = new EducationController(session);
		return educationController.getAvailableDeviceActions(deviceId);
	}

	@Override
	public Response manageDevice(Session session, long deviceId, String action, Map<String, String> actionContent) {
		EducationController educationController = new EducationController(session);
		return educationController.manageDevice(deviceId,action,actionContent);
	}

	@Override
	public Response addUser(Session session, long roomId, long userId) {
		EducationController educationController = new EducationController(session);
		CategoryController categoryController = new CategoryController(session);
		return categoryController.addMember(educationController.getCategoryToRoom(roomId), "user", userId);
	}

	@Override
	public Response addDevice(Session session, long roomId, long deviceId) {
		EducationController educationController = new EducationController(session);
		CategoryController categoryController = new CategoryController(session);
		return categoryController.addMember(educationController.getCategoryToRoom(roomId),"device", deviceId);
	}

	@Override
	public Response deleteUser(Session session, long roomId, long userId) {
		EducationController educationController = new EducationController(session);
		CategoryController categoryController = new CategoryController(session);
		return categoryController.deleteMember(educationController.getCategoryToRoom(roomId), "user", userId);	
	}

	@Override
	public Response deleteDevice(Session session, long roomId, long deviceId) {
		EducationController educationController = new EducationController(session);
		CategoryController categoryController = new CategoryController(session);
		return categoryController.deleteMember(educationController.getCategoryToRoom(roomId),"device", deviceId);
	}

	@Override
	public Response addGroup(Session session, long roomId, long groupId) {
		EducationController educationController = new EducationController(session);
		CategoryController categoryController = new CategoryController(session);
		return categoryController.addMember(educationController.getCategoryToRoom(roomId),"group",groupId);
	}

	@Override
	public Response deleteGroup(Session session, long roomId, long groupId) {
		EducationController educationController = new EducationController(session);
		CategoryController categoryController = new CategoryController(session);
		return categoryController.deleteMember(educationController.getCategoryToRoom(roomId),"group",groupId);
	}

	@Override
	public Response uploadFileToRoom(Session session, long roomId, InputStream fileInputStream,
			FormDataContentDisposition contentDispositionHeader) {
		EducationController educationController = new EducationController(session);
		return educationController.uploadFileTo("room",roomId,fileInputStream,contentDispositionHeader);
	}

	@Override
	public Response uploadFileToUser(Session session, long userId, InputStream fileInputStream,
			FormDataContentDisposition contentDispositionHeader) {
		EducationController educationController = new EducationController(session);
		return educationController.uploadFileTo("user",userId,fileInputStream,contentDispositionHeader);
	}

	@Override
	public Response uploadFileToDevice(Session session, long deviceId, InputStream fileInputStream,
			FormDataContentDisposition contentDispositionHeader) {
		EducationController educationController = new EducationController(session);
		return educationController.uploadFileTo("device",deviceId,fileInputStream,contentDispositionHeader);

	}

	@Override
	public Response uploadFileToGroup(Session session, long groupId, InputStream fileInputStream,
			FormDataContentDisposition contentDispositionHeader) {
		EducationController educationController = new EducationController(session);
		return educationController.uploadFileTo("group",groupId,fileInputStream,contentDispositionHeader);
	}

}
