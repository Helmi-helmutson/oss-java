/* (c) 2017 Péter Varkoly <peter@varkoly.de> - all rights reserved  */
package de.openschoolserver.api.resourceimpl;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

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

	Logger logger = LoggerFactory.getLogger(EducationResourceImpl.class);

	public EducationResourceImpl() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public OssResponse createSmartRoom(Session session, Category smartRoom) {
		return new EducationController(session).createSmartRoom(smartRoom);
	}

	@Override
	public OssResponse modifySmartRoom(Session session, Long roomId, Category smartRoom) {
		return new EducationController(session).modifySmartRoom(roomId, smartRoom);
	}

	@Override
	public OssResponse deleteSmartRoom(Session session, Long roomId) {
		return new EducationController(session).deleteSmartRoom(roomId);
	}

	@Override
	public List<Long> getMyRooms(Session session) {
		return new EducationController(session).getMyRooms();
	}

	@Override
	public List<List<Long>> getRoom(Session session, Long roomId) {
		return new EducationController(session).getRoom(roomId);
	}

	@Override
	public List<String> getAvailableRoomActions(Session session, Long roomId) {
		return new EducationController(session).getAvailableRoomActions(roomId);
	}

	@Override
	public OssResponse manageRoom(Session session, Long roomId, String action) {
		try {
			logger.debug("EducationResourceImpl.manageRoom:" + roomId + " action:" + action);
		}  catch (Exception e) {
			logger.error("EducationResourceImpl.manageRoom error:" + e.getMessage());
		}
		return new EducationController(session).manageRoom(roomId,action, null);
	}
	@Override

	public OssResponse manageRoom(Session session, Long roomId, String action, Map<String, String> actionContent) {
		try {
			logger.debug("EducationResourceImpl.manageRoom:" + roomId + " action:" + action);
		}  catch (Exception e) {
			logger.error("EducationResourceImpl.manageRoom error:" + e.getMessage());
		}
		return new EducationController(session).manageRoom(roomId,action, actionContent);
	}

	@Override
	public OssResponse createGroup(Session session, Group group) {
		return new EducationController(session).createGroup(group);
	}

	@Override
	public OssResponse modifyGroup(Session session, Long groupId, Group group) {
		return new EducationController(session).modifyGroup(groupId, group);
	}

	@Override
	public OssResponse removeGroup(Session session, Long groupId) {
		return new EducationController(session).deleteGroup(groupId);
	}

	@Override
	public OssResponse logOut(Session session, Long userId, Long deviceId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OssResponse logIn(Session session, Long userId, Long roomId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> getAvailableUserActions(Session session, Long userId) {
		return new EducationController(session).getAvailableUserActions(userId);
	}

	@Override
	public OssResponse manageUSer(Session session, Long userId, Long deviceId, String action,
			Map<String, String> actionContent) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public List<String> getAvailableDeviceActions(Session session, Long deviceId) {
		return new EducationController(session).getAvailableDeviceActions(deviceId);
	}

	@Override
	public OssResponse manageDevice(Session session, Long deviceId, String action, Map<String, String> actionContent) {
		return new DeviceController(session).manageDevice(deviceId,action,actionContent);
	}

	@Override
	public OssResponse addUser(Session session, Long roomId, Long userId) {
		EducationController educationController = new EducationController(session);
		return new CategoryController(session).addMember(educationController.getCategoryToRoom(roomId).getId(), "user", userId);
	}

	@Override
	public OssResponse addDevice(Session session, Long roomId, Long deviceId) {
		EducationController educationController = new EducationController(session);
		return new CategoryController(session).addMember(educationController.getCategoryToRoom(roomId).getId(),"device", deviceId);
	}

	@Override
	public OssResponse deleteUser(Session session, Long roomId, Long userId) {
		EducationController educationController = new EducationController(session);
		return new CategoryController(session).deleteMember(educationController.getCategoryToRoom(roomId).getId(), "user", userId);
	}

	@Override
	public OssResponse deleteDevice(Session session, Long roomId, Long deviceId) {
		EducationController educationController = new EducationController(session);
		return new CategoryController(session).deleteMember(educationController.getCategoryToRoom(roomId).getId(),"device", deviceId);
	}

	@Override
	public OssResponse addGroup(Session session, Long roomId, Long groupId) {
		EducationController educationController = new EducationController(session);
		return new CategoryController(session).addMember(educationController.getCategoryToRoom(roomId).getId(),"group",groupId);
	}

	@Override
	public OssResponse deleteGroup(Session session, Long roomId, Long groupId) {
		EducationController educationController = new EducationController(session);
		return new CategoryController(session).deleteMember(educationController.getCategoryToRoom(roomId).getId(),"group",groupId);
	}

	@Override
	public OssResponse uploadFileToRoom(Session session, Long roomId, InputStream fileInputStream,
			FormDataContentDisposition contentDispositionHeader) {
		return new EducationController(session).uploadFileTo("room",roomId,fileInputStream,contentDispositionHeader);
	}

	@Override
	public OssResponse uploadFileToUser(Session session, Long userId, InputStream fileInputStream,
			FormDataContentDisposition contentDispositionHeader) {
		return new EducationController(session).uploadFileTo("user",userId,fileInputStream,contentDispositionHeader);
	}

	@Override
	public OssResponse uploadFileToDevice(Session session, Long deviceId, InputStream fileInputStream,
			FormDataContentDisposition contentDispositionHeader) {
		return new EducationController(session).uploadFileTo("device",deviceId,fileInputStream,contentDispositionHeader);

	}

	@Override
	public OssResponse uploadFileToGroup(Session session, Long groupId, InputStream fileInputStream,
			FormDataContentDisposition contentDispositionHeader) {
		return new EducationController(session).uploadFileTo("group",groupId,fileInputStream,contentDispositionHeader);
	}

	@Override
	public OssResponse getRoomControl(Session session, Long roomId, Long minutes) {
		return new EducationController(session).getRoomControl(roomId,minutes);
	}

	@Override
	public List<String> getAvailableGroupActions(Session session, Long groupId) {
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
	public OssResponse deletePositiveListById(Session session, Long positiveListId) {
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
	public List<Long> getUserMember(Session session, Long roomId) {
		List<Long> memberIds = new ArrayList<Long>();
		for ( User member : new EducationController(session).getCategoryToRoom(roomId).getUsers() ) {
			memberIds.add(member.getId());
		}
		return memberIds;
	}

	@Override
	public List<Long> getGroupMember(Session session, Long roomId) {
		List<Long> memberIds = new ArrayList<Long>();
		for ( Group member : new EducationController(session).getCategoryToRoom(roomId).getGroups() ) {
			memberIds.add(member.getId());
		}
		return memberIds;
	}

	@Override
	public List<Long> getDeviceMember(Session session, Long roomId) {
		List<Long> memberIds = new ArrayList<Long>();
		for ( Device member : new EducationController(session).getCategoryToRoom(roomId).getDevices() ) {
			memberIds.add(member.getId());
		}
		return memberIds;
	}

	@Override
	public OssResponse collectFileFromDevice(Session session, Long deviceId, String projectName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OssResponse collectFileFromRoom(Session session, Long roomId, String projectName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OssResponse collectFileFromStudentsOfGroup(Session session, Long groupId, String projectName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OssResponse collectFileFromMembersOfGroup(Session session, Long groupId, String projectName) {
		// TODO Auto-generated method stub
		return null;
	}

}
