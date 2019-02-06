/* (c) 2017 Péter Varkoly <peter@varkoly.de> - all rights reserved  */
package de.openschoolserver.api.resourceimpl;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
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
import de.openschoolserver.dao.Printer;
import de.openschoolserver.dao.Room;
import de.openschoolserver.dao.Session;
import de.openschoolserver.dao.SmartRoom;
import de.openschoolserver.dao.User;
import de.openschoolserver.dao.controller.*;
import de.openschoolserver.dao.internal.CommonEntityManagerFactory;

public class EducationResourceImpl implements Resource, EducationResource {

	Logger logger = LoggerFactory.getLogger(EducationResourceImpl.class);

	private EntityManager em;

	protected void finalize()
	{
	   this.em.close();
	}

	public EducationResourceImpl() {
		super();
		this.em  = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
	}

	@Override
	public OssResponse createSmartRoom(Session session, Category smartRoom) {
		return new EducationController(session,this.em).createSmartRoom(smartRoom);
	}

	@Override
	public OssResponse modifySmartRoom(Session session, Long roomId, Category smartRoom) {
		return new EducationController(session,this.em).modifySmartRoom(roomId, smartRoom);
	}

	@Override
	public OssResponse deleteSmartRoom(Session session, Long roomId) {
		return new EducationController(session,this.em).deleteSmartRoom(roomId);
	}

	@Override
	public List<Room> getMySmartRooms(Session session) {
		return new EducationController(session,this.em).getMySmartRooms();

	}

	@Override
	public List<Room> getMyRooms(Session session) {
		return new EducationController(session,this.em).getMyRooms();
	}

	@Override
	public List<List<Long>> getRoom(Session session, Long roomId) {
		return new EducationController(session,this.em).getRoom(roomId);
	}

	@Override
	public List<String> getAvailableRoomActions(Session session, Long roomId) {
		return new EducationController(session,this.em).getAvailableRoomActions(roomId);
	}

	@Override
	public OssResponse manageRoom(Session session, Long roomId, String action) {
		try {
			logger.debug("EducationResourceImpl.manageRoom:" + roomId + " action:" + action);
		}  catch (Exception e) {
			logger.error("EducationResourceImpl.manageRoom error:" + e.getMessage());
		}
		return new EducationController(session,this.em).manageRoom(roomId,action, null);
	}

	@Override
	public OssResponse manageRoom(Session session, Long roomId, String action, Map<String, String> actionContent) {
		try {
			logger.debug("EducationResourceImpl.manageRoom:" + roomId + " action:" + action);
		}  catch (Exception e) {
			logger.error("EducationResourceImpl.manageRoom error:" + e.getMessage());
		}
		return new EducationController(session,this.em).manageRoom(roomId,action, actionContent);
	}

	@Override
	public OssResponse createGroup(Session session, Group group) {
		return new EducationController(session,this.em).createGroup(group);
	}

	@Override
	public OssResponse modifyGroup(Session session, Long groupId, Group group) {
		return new EducationController(session,this.em).modifyGroup(groupId, group);
	}

	@Override
	public OssResponse deleteGroup(Session session, Long groupId) {
		return new EducationController(session,this.em).deleteGroup(groupId);
	}

	@Override
	public OssResponse logOut(Session session, Long userId, Long deviceId) {
		return new DeviceController(session,this.em).removeLoggedInUser(deviceId, userId);
	}

	@Override
	public OssResponse logIn(Session session, Long userId, Long deviceId) {
		return new DeviceController(session,this.em).addLoggedInUser(deviceId, userId);
	}

	@Override
	public List<String> getAvailableUserActions(Session session, Long userId) {
		return new EducationController(session,this.em).getAvailableUserActions(userId);
	}

	@Override
	public List<String> getAvailableDeviceActions(Session session, Long deviceId) {
		return new EducationController(session,this.em).getAvailableDeviceActions(deviceId);
	}

	@Override
	public OssResponse manageDevice(Session session, Long deviceId, String action) {
		return new DeviceController(session,this.em).manageDevice(deviceId,action,null);
	}

	@Override
	public OssResponse manageDevice(Session session, Long deviceId, String action, Map<String, String> actionContent) {
		return new DeviceController(session,this.em).manageDevice(deviceId,action,actionContent);
	}

	@Override
	public OssResponse addUser(Session session, Long roomId, Long userId) {
		EducationController educationController = new EducationController(session,this.em);
		return new CategoryController(session,this.em).addMember(educationController.getCategoryToRoom(roomId).getId(), "user", userId);
	}

	@Override
	public OssResponse addDevice(Session session, Long roomId, Long deviceId) {
		EducationController educationController = new EducationController(session,this.em);
		return new CategoryController(session,this.em).addMember(educationController.getCategoryToRoom(roomId).getId(),"device", deviceId);
	}

	@Override
	public OssResponse deleteUser(Session session, Long roomId, Long userId) {
		EducationController educationController = new EducationController(session,this.em);
		return new CategoryController(session,this.em).deleteMember(educationController.getCategoryToRoom(roomId).getId(), "user", userId);
	}

	@Override
	public OssResponse deleteDevice(Session session, Long roomId, Long deviceId) {
		EducationController educationController = new EducationController(session,this.em);
		return new CategoryController(session,this.em).deleteMember(educationController.getCategoryToRoom(roomId).getId(),"device", deviceId);
	}

	@Override
	public OssResponse addGroup(Session session, Long roomId, Long groupId) {
		EducationController educationController = new EducationController(session,this.em);
		return new CategoryController(session,this.em).addMember(educationController.getCategoryToRoom(roomId).getId(),"group",groupId);
	}

	@Override
	public OssResponse deleteGroup(Session session, Long roomId, Long groupId) {
		EducationController educationController = new EducationController(session,this.em);
		return new CategoryController(session,this.em).deleteMember(educationController.getCategoryToRoom(roomId).getId(),"group",groupId);
	}

	@Override
	public List<OssResponse> uploadFileToRoom(Session session, Long roomId, InputStream fileInputStream,
			FormDataContentDisposition contentDispositionHeader) {
		return new EducationController(session,this.em).uploadFileTo("room",roomId,null,fileInputStream,contentDispositionHeader,false);
	}

	@Override
	public OssResponse uploadFileToUser(Session session, Long userId, InputStream fileInputStream,
			FormDataContentDisposition contentDispositionHeader) {
		return new EducationController(session,this.em).uploadFileTo("user",userId,null,fileInputStream,contentDispositionHeader,false).get(0);
	}

	@Override
	public OssResponse uploadFileToDevice(Session session, Long deviceId, InputStream fileInputStream,
			FormDataContentDisposition contentDispositionHeader) {
		return new EducationController(session,this.em).uploadFileTo("device",deviceId,null,fileInputStream,contentDispositionHeader,false).get(0);
	}

	@Override
	public List<OssResponse> uploadFileToGroup(Session session,
			Long groupId,
			boolean studentsOnly,
			InputStream fileInputStream,
			FormDataContentDisposition contentDispositionHeader) {
		return new EducationController(session,this.em).uploadFileTo("group",groupId,null,fileInputStream,contentDispositionHeader,studentsOnly);
	}

	@Override
	public List<OssResponse> uploadFileToGroups(Session session,
			String groupIds,
			boolean studentsOnly,
			InputStream fileInputStream,
			FormDataContentDisposition contentDispositionHeader) {
		List<OssResponse> responses = new ArrayList<OssResponse>();
		EducationController ec = new EducationController(session,this.em);
		for(String sgroupId : groupIds.split(",")) {
			Long groupId = Long.valueOf(sgroupId);
			if( groupId != null ) {
				responses.addAll(ec.uploadFileTo("group",groupId,null,fileInputStream,contentDispositionHeader,studentsOnly));
			}
		}
		return responses;
	}

	@Override
	public List<OssResponse> uploadFileToUsers(Session session, InputStream fileInputStream,
			FormDataContentDisposition contentDispositionHeader, String sUserIds) {
		List<Long> userIds = new ArrayList<Long>();
		for( String id : sUserIds.split(",")) {
			userIds.add(Long.valueOf(id));
		}
		logger.debug("uploadFileToUsers: " + sUserIds + " " + userIds);
		return new EducationController(session,this.em).uploadFileTo("users",0l,userIds,fileInputStream,contentDispositionHeader,false);
	}

	@Override
	public List<OssResponse> collectFileFromUsers(Session session, String projectName, boolean sortInDirs,
			boolean cleanUpExport, String userIds) {
		List<OssResponse> responses = new ArrayList<OssResponse>();
		UserController userController = new UserController(session,this.em);
		for( String id : userIds.split(",")) {
			User user = userController.getById(Long.valueOf(id));
			if( user != null ) {
				responses.add(userController.collectFileFromUser(user, projectName,  sortInDirs, cleanUpExport));
			}
		}
		return responses;
	}

	@Override
	public OssResponse getRoomControl(Session session, Long roomId, Long minutes) {
		return new EducationController(session,this.em).getRoomControl(roomId,minutes);
	}

	@Override
	public List<String> getAvailableGroupActions(Session session, Long groupId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<PositiveList> getPositiveLists(Session session) {
		return new ProxyController(session,this.em).getAllPositiveLists();
	}

	@Override
	public List<PositiveList> getMyPositiveLists(Session session) {
		return session.getUser().getOwnedPositiveLists();
	}

	@Override
	public OssResponse addPositiveList(Session session, PositiveList positiveList) {
		return new ProxyController(session,this.em).editPositiveList(positiveList);
	}

	@Override
	public PositiveList getPositiveListById(Session session, Long positiveListId) {
		return new ProxyController(session,this.em).getPositiveList(positiveListId);
	}

	@Override
	public OssResponse deletePositiveListById(Session session, Long positiveListId) {
		return new ProxyController(session,this.em).deletePositiveList(positiveListId);
	}

	@Override
	public OssResponse activatePositiveListsInRoom(Session session, Long roomId, List<Long> positiveListIds) {
		return new ProxyController(session,this.em).setAclsInRoom(roomId, positiveListIds);
	}

	@Override
	public OssResponse deActivatePositiveListsInRoom(Session session, Long roomId) {
		return new ProxyController(session,this.em).deleteAclsInRoom(roomId);
	}

	@Override
	public List<PositiveList> getPositiveListsInRoom(Session session, Long roomId) {
		return new ProxyController(session,this.em).getPositiveListsInRoom(roomId);
	}

	@Override
	public Printer getDefaultPrinter(Session session, Long roomId) {
		return new RoomController(session,this.em).getById(roomId).getDefaultPrinter();
	}

	@Override
	public List<Printer> getAvailablePrinters(Session session, Long roomId) {
		return new RoomController(session,this.em).getById(roomId).getAvailablePrinters();
	}

	@Override
	public List<User> getUserMember(Session session, Long roomId) {
		List<User> users = new ArrayList<User>();
		Category category = new EducationController(session,this.em).getCategoryToRoom(roomId);
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
		Category category = new EducationController(session,this.em).getCategoryToRoom(roomId);
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
		Category category  = new EducationController(session,this.em).getCategoryToRoom(roomId);
		if( category != null ) {
			for ( Device member : category.getDevices() ) {
				devices.add(member);
			}
		} else {
			RoomController roomController = new RoomController(session,this.em);
			return roomController.getDevices(roomId);
		}
		return devices;
	}

	@Override
	public OssResponse collectFileFromDevice(Session session, Long deviceId, String projectName) {
		Device device = new DeviceController(session,this.em).getById(deviceId);
		return new UserController(session,this.em).collectFile(device.getLoggedIn(), projectName);
	}

	@Override
	public List<OssResponse> collectFileFromRoom(Session session, Long roomId, String projectName, boolean sortInDirs, boolean cleanUpExport) {
		UserController userController     = new UserController(session,this.em);
		DeviceController deviceController = new DeviceController(session,this.em);
		List<OssResponse> responses       = new ArrayList<OssResponse>();
		for( List<Long> logged : new EducationController(session,this.em).getRoom(roomId) ) {
			User   user   = userController.getById(logged.get(0));
			Device device =  deviceController.getById(logged.get(1));
			if( user == null ) {
				user = userController.getByUid(device.getName());
			}
			if( user != null ) {
				responses.add(userController.collectFileFromUser(user,projectName,sortInDirs,cleanUpExport));
			}
		}
		return responses;
	}

	@Override
	public List<OssResponse> collectFileFromGroup(Session session,
			Long groupId,
			String projectName,
			boolean sortInDirs,
			boolean cleanUpExport,
			boolean studentsOnly
			) {
		UserController userController = new UserController(session,this.em);
		Group          group          = new GroupController(session,this.em).getById(groupId);
		List<OssResponse> responses   = new ArrayList<OssResponse>();
		for( User user : group.getUsers() ) {
			if( !studentsOnly ||  user.getRole().equals(roleStudent) || user.getRole().equals(roleGuest)) {
				if( user.getRole().equals(roleTeacher) ) {
					responses.add(userController.collectFileFromUser(user, projectName, sortInDirs, false));
				} else {
					responses.add(userController.collectFileFromUser(user, projectName, sortInDirs, cleanUpExport));
				}
			}
		}
		return responses;
	}

	@Override
	public List<OssResponse> collectFileFromGroups(Session session,
			String groupIds,
			String projectName,
			boolean sortInDirs,
			boolean cleanUpExport,
			boolean studentsOnly
			) {
		List<OssResponse> responses   = new ArrayList<OssResponse>();
		for(String sgroupId : groupIds.split(",")) {
			Long groupId = Long.valueOf(sgroupId);
			if(groupId != null ) {
				responses.addAll(collectFileFromGroup(session,Long.valueOf(sgroupId),projectName,sortInDirs,cleanUpExport,studentsOnly));
			}
		}
		return responses;
	}

	@Override
	public List<OssResponse> applyAction(Session session, OssActionMap ossActionMap) {
		List<OssResponse> responses = new ArrayList<OssResponse>();
		UserController userController = new UserController(session,this.em);
		logger.debug(ossActionMap.toString());
		switch(ossActionMap.getName()) {
		case "setPassword":
			return  userController.resetUserPassword(
					ossActionMap.getUserIds(),
					ossActionMap.getStringValue(),
					ossActionMap.isBooleanValue());
		case "setFilesystemQuota":
			return  userController.setFsQuota(
					ossActionMap.getUserIds(),
					ossActionMap.getLongValue());
		case "setMailsystemQuota":
			return  userController.setMsQuota(
					ossActionMap.getUserIds(),
					ossActionMap.getLongValue());
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
		case "deleteUser":
			SessionController sessionController = new SessionController(session,this.em);
			if( sessionController.authorize(session,"user.delete") || sessionController.authorize(session,"student.delete") ) {
				return  userController.deleteStudents(ossActionMap.getUserIds());
			} else {
				responses.add(new OssResponse(session,"ERROR","You have no right to execute this action."));
				return responses;
			}
		}
		responses.add(new OssResponse(session,"ERROR","Unknown action"));
		return responses;
	}

	@Override
	public List<Category> getGuestUsers(Session session) {
		return new UserController(session,this.em).getGuestUsers();
	}

	@Override
	public Category getGuestUsersCategory(Session session, Long guestUsersId) {
		return new UserController(session,this.em).getGuestUsersCategory(guestUsersId);
	}

	@Override
	public OssResponse deleteGuestUsers(Session session, Long guestUsersId) {
		return new UserController(session,this.em).deleteGuestUsers(guestUsersId);
	}

	@Override
	public OssResponse addGuestUsers(Session session, String name, String description, Long roomId, Long count,
			Date validUntil) {
		return new UserController(session,this.em).addGuestUsers(name, description, roomId, count, validUntil);
	}

	@Override
	public List<Room> getGuestRooms(Session session) {
		return new RoomController(session,this.em).getAllWithTeacherControl();
	}

	@Override
	public List<User> getUsersById(Session session, List<Long> userIds) {
		return new UserController(session,this.em).getUsers(userIds);
	}

	@Override
	public Group getGroup(Session session, Long groupId) {
		return new GroupController(session,this.em).getById(groupId);
	}

	@Override
	public List<Group> getMyGroups(Session session) {
		List<Group> groups = new ArrayList<Group>();
		for( Group group : session.getUser().getGroups() ) {
			if( !group.getGroupType().equals("primary") ) {
				groups.add(group);
			}
		}
		return groups;
	}

	@Override
	public List<Group> getMyAvailableClasses(Session session) {
		List<Group> groups = new ArrayList<Group>();
		for( Group group : new GroupController(session,this.em).getByType("class") ) {
			if( !session.getUser().getGroups().contains(group)) {
				groups.add(group);
			}
		}
		return groups;
	}

	@Override
	public List<User> getAvailableMembers(Session session, long groupId) {
		return new GroupController(session,this.em).getAvailableMember(groupId);
	}

	@Override
	public List<User> getMembers(Session session, long groupId) {
		List<User> users = new ArrayList<User>();
		Group group = new GroupController(session,this.em).getById(groupId);
		Boolean myGroup = group.getOwner().equals(session.getUser());
		for( User user :  new GroupController(session,this.em).getMembers(groupId) ) {
			if( myGroup || user.getRole().equals(roleStudent) || user.getRole().equals(roleGuest) ) {
				users.add(user);
			}
		}
		return users;
	}

	@Override
	public OssResponse deleteMember(Session session, long groupId, long userId) {
		return new GroupController(session,this.em).removeMember(groupId, userId);
	}

	@Override
	public OssResponse addMember(Session session, long groupId, long userId) {
		return new GroupController(session,this.em).addMember(groupId, userId);
	}

	@Override
	public AccessInRoom getAccessStatus(Session session, long roomId) {
		return new RoomController(session,this.em).getAccessStatus(roomId);
	}

	@Override
	public OssResponse setAccessStatus(Session session, long roomId, AccessInRoom access) {
		return new RoomController(session,this.em).setAccessStatus(roomId, access);
	}

	@Override
	public List<Device> getDevicesById(Session session, List<Long> deviceIds) {
		return new DeviceController(session,this.em).getDevices(deviceIds);
	}

	@Override
	public User getUserById(Session session, Long userId) {
		return new UserController(session,this.em).getById(userId);
	}

	@Override
	public Device getDeviceById(Session session, Long deviceId) {
		return new DeviceController(session,this.em).getById(deviceId);
	}

	@Override
	public List<User> getAvailableUserMember(Session session, Long roomId) {
		List<User> members = this.getUserMember(session, roomId);
		List<User> availableMembers = new ArrayList<User>();
		for( User user : new UserController(session,this.em).getAll() ) {
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
		for( Group group : new GroupController(session,this.em).getAll() ) {
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
		for( Device device : new DeviceController(session,this.em).getAll() ) {
			if( !members.contains(device)) {
				availableMembers.add(device);
			}
		}
		return availableMembers;
	}

	@Override
	public OssResponse modifyDevice(Session session, Long deviceId, Device device) {
		DeviceController deviceConrtoller = new DeviceController(session,this.em);
		Device oldDevice = deviceConrtoller.getById(deviceId);
		oldDevice.setRow(device.getRow());
		oldDevice.setPlace(device.getPlace());
		if( deviceConrtoller.getDevicesOnMyPlace(oldDevice).size() > 0 ) {
			return new OssResponse(session,"ERROR","Place is already occupied.");
		}
		try {
			this.em.getTransaction().begin();
			this.em.merge(oldDevice);
			this.em.getTransaction().commit();
		} catch (Exception e) {
			return new OssResponse(session,"ERROR", e.getMessage());
		}
		return new OssResponse(session,"OK","Device was repositioned.");
	}

	@Override
	public OssResponse modifyDeviceOfRoom(Session session, Long roomId, Long deviceId, Device device) {
		Room room = new RoomController(session,this.em).getById(roomId);
		if( (room.getCategories() != null) && (room.getCategories().size() > 0 ) && room.getCategories().get(0).getCategoryType().equals("smartRoom") ) {
			DeviceController deviceConrtoller = new DeviceController(session,this.em);
			Device oldDevice = deviceConrtoller.getById(deviceId);
			return deviceConrtoller.setConfig(oldDevice, "smartRoom-" + roomId + "-coordinates", String.format("%d,%d", device.getRow(),device.getPlace()));
		} else {
			return modifyDevice(session, deviceId, device);
		}
	}

	@Override
	public SmartRoom getRoomDetails(Session session, Long roomId) {
		return new SmartRoom(session,this.em,roomId);
	}

	@Override
	public OssResponse manageGroup(Session session, Long groupId, String action) {
		GroupController gc = new GroupController(session,this.em);
		Group group = gc.getById(groupId);
		switch(action.toLowerCase()) {
		case "turnsmartroom":
			return gc.createSmartRoomForGroup(group, true, true);
		}
		return null;
	}

	@Override
	public OssResponse manageGroup(Session session, Long groupId, String action, Map<String, String> actionContent) {
		// TODO Auto-generated method stub
		return null;
	}
}
