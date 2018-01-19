/* (c) 2017 Péter Varkoly <peter@varkoly.de> - all rights reserved  */
package de.openschoolserver.api.resourceimpl;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import de.openschoolserver.api.resources.EducationResource;
import de.openschoolserver.api.resources.Resource;
import de.openschoolserver.dao.Category;
import de.openschoolserver.dao.Device;
import de.openschoolserver.dao.Group;
import de.openschoolserver.dao.OssResponse;
import de.openschoolserver.dao.PositiveList;
import de.openschoolserver.dao.Session;
import de.openschoolserver.dao.User;
import de.openschoolserver.dao.controller.*;

public class EducationResourceImpl implements Resource, EducationResource {

	public EducationResourceImpl() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public OssResponse createSmartRoom(Session session, Category smartRoom) {
		return new EducationController(session).createSmartRoom(smartRoom);
	}

	@Override
	public OssResponse modifySmartRoom(Session session, long roomId, Category smartRoom) {
		return new EducationController(session).modifySmartRoom(roomId, smartRoom);
	}

	@Override
	public OssResponse deleteSmartRoom(Session session, long roomId) {
		return new EducationController(session).deleteSmartRoom(roomId);
	}

	@Override
	public List<Long> getMyRooms(Session session) {
		return new EducationController(session).getMyRooms();
	}

	@Override
	public List<List<Long>> getRoom(Session session, long roomId) {
		return new EducationController(session).getRoom(roomId);
	}

	@Override
	public List<String> getAvailableRoomActions(Session session, long roomId) {
		return new EducationController(session).getAvailableRoomActions(roomId);
	}

	@Override
	public OssResponse manageRoom(Session session, long roomId, String action, Map<String, String> actionContent) {
		return new EducationController(session).manageRoom(roomId,action,actionContent);
	}

	@Override
	public OssResponse createGroup(Session session, Group group) {
		return new EducationController(session).createGroup(group);
	}

	@Override
	public OssResponse modifyGroup(Session session, long groupId, Group group) {
		return new EducationController(session).modifyGroup(groupId, group);
	}

	@Override
	public OssResponse removeGroup(Session session, long groupId) {
		return new EducationController(session).deleteGroup(groupId);
	}

	@Override
	public OssResponse logOut(Session session, long userId, long deviceId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OssResponse logIn(Session session, long userId, long roomId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> getAvailableUserActions(Session session, long userId) {
		return new EducationController(session).getAvailableUserActions(userId);
	}

	@Override
	public OssResponse manageUSer(Session session, long userId, long deviceId, String action,
			Map<String, String> actionContent) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public List<String> getAvailableDeviceActions(Session session, long deviceId) {
		return new EducationController(session).getAvailableDeviceActions(deviceId);
	}

	@Override
	public OssResponse manageDevice(Session session, long deviceId, String action, Map<String, String> actionContent) {
		return new DeviceController(session).manageDevice(deviceId,action,actionContent);
	}

	@Override
	public OssResponse addUser(Session session, long roomId, long userId) {
		EducationController educationController = new EducationController(session);
		return new CategoryController(session).addMember(educationController.getCategoryToRoom(roomId).getId(), "user", userId);
	}

	@Override
	public OssResponse addDevice(Session session, long roomId, long deviceId) {
		EducationController educationController = new EducationController(session);
		return new CategoryController(session).addMember(educationController.getCategoryToRoom(roomId).getId(),"device", deviceId);
	}

	@Override
	public OssResponse deleteUser(Session session, long roomId, long userId) {
		EducationController educationController = new EducationController(session);
		return new CategoryController(session).deleteMember(educationController.getCategoryToRoom(roomId).getId(), "user", userId);
	}

	@Override
	public OssResponse deleteDevice(Session session, long roomId, long deviceId) {
		EducationController educationController = new EducationController(session);
		return new CategoryController(session).deleteMember(educationController.getCategoryToRoom(roomId).getId(),"device", deviceId);
	}

	@Override
	public OssResponse addGroup(Session session, long roomId, long groupId) {
		EducationController educationController = new EducationController(session);
		return new CategoryController(session).addMember(educationController.getCategoryToRoom(roomId).getId(),"group",groupId);
	}

	@Override
	public OssResponse deleteGroup(Session session, long roomId, long groupId) {
		EducationController educationController = new EducationController(session);
		return new CategoryController(session).deleteMember(educationController.getCategoryToRoom(roomId).getId(),"group",groupId);
	}

	@Override
	public OssResponse uploadFileToRoom(Session session, long roomId, InputStream fileInputStream,
			FormDataContentDisposition contentDispositionHeader) {
		return new EducationController(session).uploadFileTo("room",roomId,fileInputStream,contentDispositionHeader);
	}

	@Override
	public OssResponse uploadFileToUser(Session session, long userId, InputStream fileInputStream,
			FormDataContentDisposition contentDispositionHeader) {
		return new EducationController(session).uploadFileTo("user",userId,fileInputStream,contentDispositionHeader);
	}

	@Override
	public OssResponse uploadFileToDevice(Session session, long deviceId, InputStream fileInputStream,
			FormDataContentDisposition contentDispositionHeader) {
		return new EducationController(session).uploadFileTo("device",deviceId,fileInputStream,contentDispositionHeader);

	}

	@Override
	public OssResponse uploadFileToGroup(Session session, long groupId, InputStream fileInputStream,
			FormDataContentDisposition contentDispositionHeader) {
		return new EducationController(session).uploadFileTo("group",groupId,fileInputStream,contentDispositionHeader);
	}

	@Override
	public OssResponse getRoomControl(Session session, long roomId, long minutes) {
		return new EducationController(session).getRoomControl(roomId,minutes);
	}

	@Override
	public List<String> getAvailableGroupActions(Session session, long groupId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OssResponse collectFileFromDevice(Session session, long deviceId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<PositiveList> getPositiveLists(Session session) {
		return new ProxyController(session).getAllPositiveList();
	}

	@Override
	public List<PositiveList> getMyPositiveLists(Session session) {
		return session.getUser().getOwnedPositiveLists();
	}

	@Override
	public OssResponse addPositiveList(Session session, PositiveList positiveList) {
		return new ProxyController(session).editPositiveList(positiveList);
	}

	@Override
	public PositiveList getPositiveListById(Session session, Long positiveListId) {
		return new ProxyController(session).getPositiveList(positiveListId);
	}

	@Override
	public PositiveList deletePositiveListById(Session session, Long positiveListId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OssResponse activatePositiveListsInRoom(Session session, Long roomId, List<Long> positiveListIds) {
		return new ProxyController(session).setAclsInRoom(roomId, positiveListIds);
	}

	@Override
	public OssResponse deActivatePositiveListsInRoom(Session session, Long roomId) {
		return new ProxyController(session).deleteAclsInRoom(roomId);
	}

	@Override
	public Device getDefaultPrinter(Session session, Long roomId) {
		return new RoomController(session).getById(roomId).getDefaultPrinter();
	}

	@Override
	public List<Device> getAvailablePrinters(Session session, Long roomId) {
		return new RoomController(session).getById(roomId).getAvailablePrinters();
	}

	@Override
	public List<Long> getUserMember(Session session, long roomId) {
		List<Long> memberIds = new ArrayList<Long>();
		for ( User member : new EducationController(session).getCategoryToRoom(roomId).getUsers() ) {
			memberIds.add(member.getId());
		}
		return memberIds;
	}

	@Override
	public List<Long> getGroupMember(Session session, long roomId) {
		List<Long> memberIds = new ArrayList<Long>();
		for ( Group member : new EducationController(session).getCategoryToRoom(roomId).getGroups() ) {
			memberIds.add(member.getId());
		}
		return memberIds;
	}

	@Override
	public List<Long> getDeviceMember(Session session, long roomId) {
		List<Long> memberIds = new ArrayList<Long>();
		for ( Device member : new EducationController(session).getCategoryToRoom(roomId).getDevices() ) {
			memberIds.add(member.getId());
		}
		return memberIds;
	}

}
