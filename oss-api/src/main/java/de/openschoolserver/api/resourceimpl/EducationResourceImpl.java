/* (c) 2017 Péter Varkoly <peter@varkoly.de> - all rights reserved  */
package de.openschoolserver.api.resourceimpl;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static de.openschoolserver.dao.internal.OSSConstants.*;
import de.openschoolserver.api.resources.EducationResource;
import de.openschoolserver.api.resources.Resource;
import de.openschoolserver.dao.AccessInRoom;
import de.openschoolserver.dao.Category;
import de.openschoolserver.dao.Device;
import de.openschoolserver.dao.Group;
import de.openschoolserver.dao.OssActionMap;
import de.openschoolserver.dao.OssResponse;
import de.openschoolserver.dao.PositiveList;
import de.openschoolserver.dao.Room;
import de.openschoolserver.dao.Session;
import de.openschoolserver.dao.SmartRoom;
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
	public List<Room> getMySamrtRooms(Session session) {
		List<Room> smartRooms = new ArrayList<Room>();
		for( Category category : session.getUser().getCategories() ) {
			if(category.getCategoryType().equals("smartRoom")) {
				smartRooms.add(category.getRooms().get(0));
			}
		}
		return smartRooms;
	}

	@Override
	public List<Room> getMyRooms(Session session) {
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
	public OssResponse downloadFilesFromRoom(Session session, Long roomId, String projectName, boolean sortInDirs,
			boolean cleanUpExport) {
		Map<String, String> actionContent = new HashMap<String,String>();
		RoomController roomController = new RoomController(session);
		Room room = roomController.getById(roomId);
		actionContent.put("sortInDirs", "true");
		actionContent.put("cleanUpExport", "true");
		if( projectName == null ) {
			actionContent.put("projectName", roomController.nowString() + "." + room.getName() );
		} else {
			actionContent.put("projectName",projectName);
		}
		if( !sortInDirs) {
			actionContent.put("sortInDirs", "false");
		}
		if( !cleanUpExport) {
			actionContent.put("cleanUpExport", "false");
		}
		return this.manageRoom(session, roomId, "download", actionContent);
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
	public OssResponse deleteGroup(Session session, Long groupId) {
		return new EducationController(session).deleteGroup(groupId);
	}

	@Override
	public OssResponse logOut(Session session, Long userId, Long deviceId) {
		return new DeviceController(session).removeLoggedInUser(deviceId, userId);
	}

	@Override
	public OssResponse logIn(Session session, Long userId, Long deviceId) {
		return new DeviceController(session).addLoggedInUser(deviceId, userId);
	}

	@Override
	public List<String> getAvailableUserActions(Session session, Long userId) {
		return new EducationController(session).getAvailableUserActions(userId);
	}

	@Override
	public List<String> getAvailableDeviceActions(Session session, Long deviceId) {
		return new EducationController(session).getAvailableDeviceActions(deviceId);
	}

	@Override
	public OssResponse manageDevice(Session session, Long deviceId, String action) {
		return new DeviceController(session).manageDevice(deviceId,action,null);
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
		return new ProxyController(session).getAllPositiveLists();
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
		return new ProxyController(session).deletePositiveList(positiveListId);
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
	public List<PositiveList> getPositiveListsInRoom(Session session, Long roomId) {
		return new ProxyController(session).getPositiveListsInRoom(roomId);
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
	public List<User> getUserMember(Session session, Long roomId) {
		List<User> users = new ArrayList<User>();
		Category category = new EducationController(session).getCategoryToRoom(roomId);
		if( category != null ) {
			for ( User member : category.getUsers() ) {
				users.add(member);
			}
		}
		return users;
	}

	@Override
	public List<Group> getGroupMember(Session session, Long roomId) {
		List<Group> groups = new ArrayList<Group>();
		Category category = new EducationController(session).getCategoryToRoom(roomId);
		if( category != null ) {
			for ( Group member : category.getGroups() ) {
				groups.add(member);
			}
		}
		return groups;
	}

	@Override
	public List<Device> getDeviceMember(Session session, Long roomId) {
		List<Device> devices = new ArrayList<Device>();
		Category category  = new EducationController(session).getCategoryToRoom(roomId);
		if( category != null ) {
			for ( Device member : category.getDevices() ) {
				devices.add(member);
			}
		} else {
			RoomController roomController = new RoomController(session);
			return roomController.getDevices(roomId);
		}
		return devices;
	}

	@Override
	public OssResponse collectFileFromDevice(Session session, Long deviceId, String projectName) {
		Device device = new DeviceController(session).getById(deviceId);
		return new UserController(session).collectFile(device.getLoggedIn(), projectName);
	}

	@Override
	public OssResponse collectFileFromRoom(Session session, Long roomId, String projectName) {
		UserController userController = new UserController(session);
		List<User> users = new ArrayList<User>();
		for( List<Long> logged : new EducationController(session).getRoom(roomId) ) {
				users.add(userController.getById(logged.get(0)));
		}
		return new UserController(session).collectFile(users, projectName);
	}

	@Override
	public OssResponse collectFileFromStudentsOfGroup(Session session, Long groupId, String projectName) {
		UserController userController = new UserController(session);
		List<User> users = new ArrayList<User>();
		Group group        = new GroupController(session).getById(groupId);
		for( User user : group.getUsers() ) {
			if( user.getRole().equals(roleStudent)) {
				users.add(user);
			}
		}
		return userController.collectFile(users, projectName);
	}

	@Override
	public OssResponse collectFileFromMembersOfGroup(Session session, Long groupId, String projectName) {
		UserController userController = new UserController(session);
		List<User> users = new ArrayList<User>();
		Group group        = new GroupController(session).getById(groupId);
		for( User user : group.getUsers() ) {
			users.add(user);
		}
		return userController.collectFile(users, projectName);
	}

	@Override
	public OssResponse applyAction(Session session, OssActionMap ossActionMap) {
		UserController userController = new UserController(session);
		logger.debug(ossActionMap.toString());
		switch(ossActionMap.getName()) {
		case "setPassword":
			return  userController.resetUserPassword(
					ossActionMap.getUserIds(),
					ossActionMap.getStringValue(),
					ossActionMap.isBooleanValue());
		case "setFilesystemQuota":
			break;
		case "setMailsystemQuota":
			break;
		case "disableLogin":
			return  userController.disableLogin(
					ossActionMap.getUserIds(),
					ossActionMap.isBooleanValue());
		case "disableInternet":
			return  userController.disableInternet(
					ossActionMap.getUserIds(),
					ossActionMap.isBooleanValue());
		case "copyTemplate":
			return  userController.copyTemplate(
					ossActionMap.getUserIds(),
					ossActionMap.getStringValue());
		case "removeProfiles":
			return  userController.removeProfile(ossActionMap.getUserIds());
		}
		return new OssResponse(session,"ERROR","Unknown action");
	}

	@Override
	public List<Category> getGuestUsers(Session session) {
		return new UserController(session).getGuestUsers();
	}

	@Override
	public Category getGuestUsersCategory(Session session, Long guestUsersId) {
		return new UserController(session).getGuestUsersCategory(guestUsersId);
	}

	@Override
	public OssResponse deleteGuestUsers(Session session, Long guestUsersId) {
		return new UserController(session).deleteGuestUsers(guestUsersId);
	}

	@Override
	public OssResponse addGuestUsers(Session session, String name, String description, Long roomId, int count,
			Date validUntil) {
		return new UserController(session).addGuestUsers(name, description, roomId, count, validUntil);
	}

	@Override
	public List<User> getUsersById(Session session, List<Long> userIds) {
		return new UserController(session).getUsers(userIds);
	}

	@Override
	public Group getGroup(Session session, Long groupId) {
		return new GroupController(session).getById(groupId);
	}

	@Override
	public List<Group> getMyGroups(Session session) {
		List<Group> groups = new ArrayList<Group>();
		for( Group group : new GroupController(session).getAll() ) {
			if(group.getOwner().equals(session.getUser()) || group.getGroupType().equals("class") ) {
				groups.add(group);
			}
		}
		return groups;
	}

	@Override
	public List<User> getAvailableMembers(Session session, long groupId) {
		return new GroupController(session).getAvailableMember(groupId);
	}

	@Override
	public List<User> getMembers(Session session, long groupId) {
		List<User> users = new ArrayList<User>();
		for( User user :  new GroupController(session).getMembers(groupId) ) {
			if( user.getRole().equals("students")) {
				users.add(user);
			}
		}
		return users;
	}

	@Override
	public OssResponse removeMember(Session session, long groupId, long userId) {
		return new GroupController(session).removeMember(groupId, userId);
	}

	@Override
	public OssResponse addMember(Session session, long groupId, long userId) {
		return new GroupController(session).addMember(groupId, userId);
	}

	@Override
	public AccessInRoom getAccessStatus(Session session, long roomId) {
		return new RoomController(session).getAccessStatus(roomId);
	}

	@Override
	public OssResponse setAccessStatus(Session session, long roomId, AccessInRoom access) {
		return new RoomController(session).setAccessStatus(roomId, access);
	}

	@Override
	public List<Device> getDevicesById(Session session, List<Long> deviceIds) {
		return new DeviceController(session).getDevices(deviceIds);
	}

	@Override
	public User getUserById(Session session, Long userId) {
		return new UserController(session).getById(userId);
	}

	@Override
	public Device getDeviceById(Session session, Long deviceId) {
		return new DeviceController(session).getById(deviceId);
	}

	@Override
	public List<User> getAvailableUserMember(Session session, Long roomId) {
		List<User> members = this.getUserMember(session, roomId);
		List<User> availableMembers = new ArrayList<User>();
		for( User user : new UserController(session).getAll() ) {
			if(!members.contains(user)) {
				availableMembers.add(user);
			}
		}
		return availableMembers;
	}

	@Override
	public List<Group> getAvailableGroupMember(Session session, Long roomId) {
		List<Group> members = this.getGroupMember(session, roomId);
		List<Group> availableMembers = new ArrayList<Group>();
		for( Group group : new GroupController(session).getAll() ) {
			if( !members.contains(group)) {
				availableMembers.add(group);
			}
		}
		return availableMembers;
	}

	@Override
	public List<Device> getAvailableDeviceMember(Session session, Long roomId) {
		List<Device> members = this.getDeviceMember(session, roomId);
		List<Device> availableMembers = new ArrayList<Device>();
		for( Device device : new DeviceController(session).getAll() ) {
			if( !members.contains(device)) {
				availableMembers.add(device);
			}
		}
		return availableMembers;
	}

	@Override
	public OssResponse modifyDevice(Session session, Long deviceId, Device device) {
		DeviceController deviceConrtoller = new DeviceController(session);
		Device oldDevice = deviceConrtoller.getById(deviceId);
		oldDevice.setRow(device.getRow());
		oldDevice.setPlace(device.getPlace());
		if( deviceConrtoller.getDevicesOnMyPlace(oldDevice).size() > 0 ) {
			return new OssResponse(session,"ERROR","Place is already occupied.");
		}
		EntityManager em = deviceConrtoller.getEntityManager();
		try {
			em.getTransaction().begin();
			em.merge(oldDevice);
			em.getTransaction().commit();
		} catch (Exception e) {
			return new OssResponse(session,"ERROR", e.getMessage());
		} finally {
			em.close();
		}
		return new OssResponse(session,"OK","Device was repositioned.");
	}

	@Override
	public OssResponse modifyDeviceOfRoom(Session session, Long roomId, Long deviceId, Device device) {
		Room room = new RoomController(session).getById(roomId);
		if( (room.getCategories() != null) && (room.getCategories().size() > 0 ) && room.getCategories().get(0).getCategoryType().equals("smartRoom") ) {
			DeviceController deviceConrtoller = new DeviceController(session);
			Device oldDevice = deviceConrtoller.getById(deviceId);
			return deviceConrtoller.setConfig(oldDevice, "smartRoom-" + roomId + "-coordinates", String.format("%d,%d", device.getRow(),device.getPlace()));
		} else {
			return modifyDevice(session, deviceId, device);
		}
	}

	@Override
	public SmartRoom getRoomDetails(Session session, Long roomId) {
		return new SmartRoom(session,roomId);
	}
}
