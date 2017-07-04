package de.openschoolserver.api.resourceimpl;

import java.util.List;

import java.util.Map;

import de.openschoolserver.api.resources.EducationResource;
import de.openschoolserver.api.resources.Resource;
import de.openschoolserver.dao.Group;
import de.openschoolserver.dao.Response;
import de.openschoolserver.dao.Session;
import de.openschoolserver.dao.controler.*;

public class EducationResourceImpl implements Resource, EducationResource {

	public EducationResourceImpl() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public Response createVirtaulRoom(Session session, Map<String, String> virtualRoom) {
		EducationControler educationControler = new EducationControler(session);
		return educationControler.createVirtualRoom(virtualRoom);
	}

	@Override
	public Response modifyVirtaulRoom(Session session, Map<String, String> virtualRoom) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Response deleteVirtaulRoom(Session session, long roomId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Long> getMyRooms(Session session) {
		EducationControler educationControler = new EducationControler(session);
		return educationControler.getMyRooms();
	}

	@Override
	public List<Map<String, String>> getRoom(Session session, long roomId) {
		// TODO Auto-generated method stub
		return null;
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

}
