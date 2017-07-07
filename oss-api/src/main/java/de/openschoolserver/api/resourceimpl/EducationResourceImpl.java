package de.openschoolserver.api.resourceimpl;

import java.util.List;


import java.util.Map;

import de.openschoolserver.api.resources.EducationResource;
import de.openschoolserver.api.resources.Resource;
import de.openschoolserver.dao.Category;
import de.openschoolserver.dao.Group;
import de.openschoolserver.dao.Response;
import de.openschoolserver.dao.Session;
import de.openschoolserver.dao.controler.*;

public class EducationResourceImpl implements Resource, EducationResource {

	public EducationResourceImpl() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public Response createVirtaulRoom(Session session, Category virtualRoom) {
		EducationControler educationControler = new EducationControler(session);
		return educationControler.createVirtualRoom(virtualRoom);
	}

	@Override
	public Response modifyVirtaulRoom(Session session, long roomId, Category virtualRoom) {
		EducationControler educationControler = new EducationControler(session);
		return educationControler.modifyVirtualRoom(roomId, virtualRoom);
	}

	@Override
	public Response deleteVirtaulRoom(Session session, long roomId) {
		EducationControler educationControler = new EducationControler(session);
		return educationControler.deleteVirtualRoom(roomId);
	}

	@Override
	public List<Long> getMyRooms(Session session) {
		EducationControler educationControler = new EducationControler(session);
		return educationControler.getMyRooms();
	}

	@Override
	public List<List<Long>> getRoom(Session session, long roomId) {
		EducationControler educationControler = new EducationControler(session);
		return educationControler.getRoom(roomId);
	}

	@Override
	public List<String> getAvailableRoomActions(Session session, long roomId, String action) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Response manageRoom(Session session, long roomId, String action, Map<String, String> actionContent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Response createGroup(Session session, Group group) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Response modifyGroup(Session session, long groupId, Group group) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Response removeGroup(Session session, long groupId) {
		// TODO Auto-generated method stub
		return null;
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Response manageUSer(Session session, long userId, long deviceId, String action,
			Map<String, String> actionContent) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public List<String> getAvailableDeviceActions(Session session, long deviceId, String action) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Response manageDevice(Session session, long deviceId, String action, Map<String, String> actionContent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Response addUser(Session session, long roomId, long userId) {
		EducationControler educationControler = new EducationControler(session);
		CategoryControler categoryControler = new CategoryControler(session);
		return categoryControler.addMember(educationControler.getCategoryToRoom(roomId), "user", userId);
	}

	@Override
	public Response addDevice(Session session, long roomId, long deviceId) {
		EducationControler educationControler = new EducationControler(session);
		CategoryControler categoryControler = new CategoryControler(session);
		return categoryControler.addMember(educationControler.getCategoryToRoom(roomId),"device", deviceId);
	}

	@Override
	public Response deleteUser(Session session, long roomId, long userId) {
		EducationControler educationControler = new EducationControler(session);
		CategoryControler categoryControler = new CategoryControler(session);
		return categoryControler.deleteMember(educationControler.getCategoryToRoom(roomId), "user", userId);	
	}

	@Override
	public Response deleteDevice(Session session, long roomId, long deviceId) {
		EducationControler educationControler = new EducationControler(session);
		CategoryControler categoryControler = new CategoryControler(session);
		return categoryControler.deleteMember(educationControler.getCategoryToRoom(roomId),"device", deviceId);
	}

	@Override
	public Response addgroup(Session session, long roomId, long groupId) {
		EducationControler educationControler = new EducationControler(session);
		CategoryControler categoryControler = new CategoryControler(session);
		return categoryControler.addMember(educationControler.getCategoryToRoom(roomId),"group",groupId);
	}

	@Override
	public Response deleteGroup(Session session, long roomId, long groupId) {
		EducationControler educationControler = new EducationControler(session);
		CategoryControler categoryControler = new CategoryControler(session);
		return categoryControler.deleteMember(educationControler.getCategoryToRoom(roomId),"group",groupId);
	}

}
