/* (c) 2017 Péter Varkoly <peter@varkoly.de> - all rights reserved  */
package de.openschoolserver.dao.controller;

import java.io.File;


import static de.openschoolserver.dao.internal.OSSConstatns.*;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.FileSystems;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.nio.file.attribute.UserPrincipal;
import java.nio.file.attribute.UserPrincipalLookupService;
import java.util.*;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.openschoolserver.dao.*;
import de.openschoolserver.dao.tools.OSSShellTools;

public class EducationController extends Controller {

	Logger logger = LoggerFactory.getLogger(EducationController.class);
    static FileAttribute<Set<PosixFilePermission>> privatDirAttribute  = PosixFilePermissions.asFileAttribute( PosixFilePermissions.fromString("rwx------"));
    static FileAttribute<Set<PosixFilePermission>> privatFileAttribute = PosixFilePermissions.asFileAttribute( PosixFilePermissions.fromString("rw-------"));

	public EducationController(Session session) {
		super(session);
	}

	/*
	 * Return the Category to a smart room
	 */
	
	public Category getCategoryToRoom(Long roomId){
		EntityManager   em = getEntityManager();
		Room room;
		try {
			room = em.find(Room.class, roomId);
		} catch (Exception e) {
			logger.error(e.getMessage());
			return null;
		} finally {
			 
			em.close();
		}
		for( Category category : room.getCategories() ) {
			if( room.getName().equals(category.getName()) && category.getCategoryType().equals("smartRoom")) {
				return category;
			}
		}
		return null;
	}

	/*
	 * Return the list of ids of rooms in which a user may actually control the access.
	 */
	public List<Long> getMyRooms() {
		List<Long> rooms = new ArrayList<Long>();
		if( this.session.getRoom() == null || this.session.getRoom().getRoomControl().equals("no")){
			for( Room room : new RoomController(this.session).getAll() ) {
				switch(room.getRoomControl()) {
				case "no":
				case "inRoom":
					break;
				case "allTeachers":
					rooms.add(room.getId());
					break;
				case "teachers":
					if( this.checkMConfig(room, "teachers", Long.toString((this.session.getUserId())))) {
						rooms.add(room.getId());
					}
				}
			}
		} else {
			rooms.add(this.session.getRoomId());
		}
		for( Category category : this.session.getUser().getCategories() ) {
			for( Room room : category.getRooms() ) {
				if( room.getRoomType().equals("smartRoom")) {
					rooms.add(room.getId());
				}
			}
		}
		return rooms;
	}

	/*
	 * Create the a new smart room from a hash:
	 * {
	 *     "name"  : <Smart room name>,
	 *     "description : <Descripton of the room>,
	 *     "studentsOnly : true/false
	 * }
	 */
	public OssResponse createSmartRoom(Category smartRoom) {
		EntityManager   em = getEntityManager();
		User   owner       = this.session.getUser();
		/* Define the room */
		Room     room      = new Room();
		room.setName(smartRoom.getName());
		room.setDescription(smartRoom.getDescription());
		room.setRoomType("smartRoom");
		room.getCategories().add(smartRoom);
		smartRoom.setRooms(new ArrayList<Room>());
		smartRoom.getRooms().add(room);
		smartRoom.setOwner(owner);
		smartRoom.setCategoryType("smartRoom");
		owner.getCategories().add(smartRoom);

		try {
			em.getTransaction().begin();
			em.persist(room);
			em.persist(smartRoom);
			em.merge(owner);
			em.getTransaction().commit();
		} catch (Exception e) {
			logger.error(e.getMessage());
			em.close();
			return new OssResponse(this.getSession(),"ERROR", e.getMessage());
		}
		try {
			em.getTransaction().begin();
			/*
			 * Add groups to the smart room
			 */
			GroupController groupController = new GroupController(this.session);
			for( Long id : smartRoom.getGroupIds()) {
				Group group = groupController.getById(id);
				smartRoom.getGroups().add(group);
				group.getCategories().add(smartRoom);
				em.merge(room);
				em.merge(smartRoom);
			}
			/*
			 * Add users to the smart room
			 */
			UserController  userController  = new UserController(this.session);
			for( Long id : smartRoom.getUserIds()) {
				User user = userController.getById(Long.valueOf(id));
				if(smartRoom.getStudentsOnly() && ! user.getRole().equals(roleStudent)){
					continue;
				}
				smartRoom.getUsers().add(user);
				user.getCategories().add(smartRoom);
				em.merge(user);
				em.merge(smartRoom);
			}
			/*
			 * Add devices to the smart room
			 */
			DeviceController deviceController = new DeviceController(this.session);
			for( Long id: smartRoom.getDeviceIds()) {
				Device device = deviceController.getById(Long.valueOf(id));
				smartRoom.getDevices().add(device);
				device.getCategories().add(smartRoom);
				em.merge(device);
				em.merge(smartRoom);
			}
			em.getTransaction().commit();
		} catch (Exception e) {
			logger.error(e.getMessage());
			return new OssResponse(this.getSession(),"ERROR", e.getMessage());
		} finally {
			em.close();
		}
		return new OssResponse(this.getSession(),"OK","Smart Room was created succesfully."); 
	}

	public OssResponse modifySmartRoom(long roomId, Category smartRoom) {
		EntityManager   em = getEntityManager();
		try {
			em.getTransaction().begin();
			Room room = smartRoom.getRooms().get(0);
			room.setName(smartRoom.getName());
			room.setDescription(smartRoom.getDescription());
			em.merge(smartRoom);
			em.merge(room);
			em.getTransaction().commit();
		} catch (Exception e) {
			logger.error(e.getMessage());
			return new OssResponse(this.getSession(),"ERROR", e.getMessage());
		} finally {
			em.close();
		}
		return new OssResponse(this.getSession(),"OK","Smart Room was modified succesfully.");
	}
	
	public OssResponse deleteSmartRoom(Long roomId) {
		EntityManager   em = getEntityManager();
		try {
			em.getTransaction().begin();
			Room room         = new RoomController(this.session).getById(roomId);
			Category category = room.getCategories().get(0);
			em.merge(room);
			em.remove(room);
			em.merge(category);
			em.remove(category);
			em.getTransaction().commit();
		} catch (Exception e) {
			logger.error(e.getMessage());
			return new OssResponse(this.getSession(),"ERROR", e.getMessage());
		} finally {
			em.close();
		}
		return new OssResponse(this.getSession(),"OK","Smart Room was deleted succesfully.");
	}

	
	/*
	 * Get the list of users which are logged in a room or smart room
	 * If a user of a smart room is not logged on the device id is 0L;
	 * @param  roomId The id of the wanted room.
	 * @return The id list of the logged on users: [ [ <userId>,<deviceId> ], [ <userId>, <deviceId] ... ]
	 */
	public List<List<Long>> getRoom(long roomId) {
		List<List<Long>> loggedOns = new ArrayList<List<Long>>();
		List<Long> loggedOn;
		RoomController roomController = new RoomController(this.session);
		Room room = roomController.getById(roomId);
		try {
			logger.debug("EducationController.getRoom:" + room);
		}  catch (Exception e) {
			logger.error("EducationController.getRoom error:" + e.getMessage());
		}
		User me   = this.session.getUser();
		if( room.getRoomType().equals("smartRoom")) {
			Category category = room.getCategories().get(0);
			for( Group group : category.getGroups() ) {
				for( User user : group.getUsers() ) {
					if(	category.getStudentsOnly() && ! user.getRole().equals(roleStudent) ||
							user.equals(me)	){
						continue;
					}
					if( user.getLoggedOn().isEmpty() ) {
						loggedOn = new ArrayList<Long>();
						loggedOn.add(user.getId());
						loggedOn.add(0L);
						loggedOns.add(loggedOn);
					} else {
						for( Device device : user.getLoggedOn() ) {
							loggedOn = new ArrayList<Long>();
							loggedOn.add(user.getId());
							loggedOn.add(device.getId());
							loggedOns.add(loggedOn);
						}
					}
				}
			}
			for( User user : category.getUsers() ) {
				if( user.equals(me) ) {
					continue;
				}
				if( user.getLoggedOn().isEmpty() ) {
					loggedOn = new ArrayList<Long>();
					loggedOn.add(user.getId());
					loggedOn.add(0L);
					loggedOns.add(loggedOn);
				} else {
					for( Device device : user.getLoggedOn() ) {
						loggedOn = new ArrayList<Long>();
						loggedOn.add(user.getId());
						loggedOn.add(device.getId());
						loggedOns.add(loggedOn);
					}
				}
			}
			for( Device device : category.getDevices() ) {
				/*
				 * If nobody is logged in set user id to 0L
				 */
				if( device.getLoggedIn().isEmpty() ) {
					loggedOn = new ArrayList<Long>();
					loggedOn.add(0L);
					loggedOn.add(device.getId());
					loggedOns.add(loggedOn);
				} else {
					for( User user : device.getLoggedIn() ) {
						if( ! user.equals(me) ) {
							loggedOn = new ArrayList<Long>();
							loggedOn.add(user.getId());
							loggedOn.add(device.getId());
							loggedOns.add(loggedOn);
						}
					}
				}
			}
		} else {
			for( Device device : room.getDevices() ) {
				try {
					logger.debug("EducationController.getRoom:" + device );
				}  catch (Exception e) {
					logger.error("EducationController.getRoom error:" + e.getMessage());
				}
				/*
				 * If nobody is logged in set user id to 0L
				 */
				if( device.getLoggedIn().isEmpty() ) {
					loggedOn = new ArrayList<Long>();
					loggedOn.add(0L);
					loggedOn.add(device.getId());
					loggedOns.add(loggedOn);
				} else {
					for( User user : device.getLoggedIn() ) {
						if( ! user.equals(me) ) {
							loggedOn = new ArrayList<Long>();
							loggedOn.add(user.getId());
							loggedOn.add(device.getId());
							loggedOns.add(loggedOn);
						}
					}
				}
			}
		}
		return loggedOns;
	}

	public OssResponse uploadFileTo(String what, long objectId, InputStream fileInputStream,
			FormDataContentDisposition contentDispositionHeader) {
		String fileName = contentDispositionHeader.getFileName();
		File file = null;
		try {
			file = File.createTempFile("oss_uploadFile", ".ossb", new File("/opt/oss-java/tmp/"));
			Files.copy(fileInputStream, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			return new OssResponse(this.getSession(),"ERROR", e.getMessage());
		}
		StringBuilder error = new StringBuilder();
		switch(what) {
		case "user":
			User user = new UserController(this.session).getById(objectId);
			error.append(this.saveFileToUserImport(user, file, fileName));
			break;
		case "group":
			Group group = new GroupController(this.session).getById(objectId);
			for( User myUser : group.getUsers() ) {
				error.append(this.saveFileToUserImport(myUser, file, fileName));
			}
			break;
		case "device":
			Device device = new DeviceController(this.session).getById(objectId);
			for( User myUser : device.getLoggedIn() ) {
				error.append(this.saveFileToUserImport(myUser, file, fileName));
			}
			break;
		case "room":
			UserController userController = new UserController(this.session);
			for( List<Long> loggedOn : this.getRoom(objectId) ) {
				User myUser = userController.getById(loggedOn.get(0));
				error.append(this.saveFileToUserImport(myUser, file, fileName));
			}
		}
		file.delete();
		return new OssResponse(this.getSession(),"OK", "File was copied succesfully.");
	}
	
	public String saveFileToUserImport(User user, File file, String fileName) {
		UserPrincipalLookupService lookupService = FileSystems.getDefault().getUserPrincipalLookupService();
		try {
			StringBuilder newFileName = new StringBuilder(this.getConfigValue("HOME_BASE"));
			newFileName.append("/").append(user.getUid()).append("/") .append("Import").append("/");
			// Create the directory first.
			File importDir = new File( newFileName.toString() );
			Files.createDirectories(importDir.toPath(), privatDirAttribute );

			// Copy temp file to the rigth place
			newFileName.append(fileName);
			File newFile = new File( newFileName.toString() );
			Files.copy(file.toPath(), newFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
			
			// Set owner
			UserPrincipal owner = lookupService.lookupPrincipalByName(user.getUid());
			Files.setOwner(importDir.toPath(), owner);
			Files.setOwner(newFile.toPath(), owner);
		} catch (IOException e) {
			logger.error(e.getMessage());
			return e.getMessage() + System.lineSeparator();
		}
		return "";
	}

	public OssResponse collectFileFromUser(User user,String project, boolean cleanUpExport, boolean sortInDirs) {
		String[] program = new String[11];
		StringBuffer reply  = new StringBuffer();
		StringBuffer stderr = new StringBuffer();
		program[0] = "/usr/sbin/oss_collect_files.sh";
		program[1] = "-t";
		program[2] = this.session.getUser().getUid();
		program[3] = "-f";
		program[4] = user.getUid();
		program[5] = "-p";
		program[6] = project;
		program[7] = "-c";
		if( cleanUpExport ) {
			program[8] = "y";
		} else {
			program[8] = "n";
		}
		program[9] = "-d";
		if( sortInDirs ) {
			program[10] = "y";
		} else {
			program[10] = "n";
		}
		OSSShellTools.exec(program, reply, stderr, null);
		if( stderr.toString().isEmpty() ) {
			return new OssResponse(this.getSession(),"OK", "File was collected from:",null,user.getUid() );
		}
		return new OssResponse(this.getSession(),"ERROR", stderr.toString());
	}

	public OssResponse createGroup(Group group) {
		GroupController groupController = new GroupController(this.session);
		group.setGroupType("workgroup");
		group.setOwner(session.getUser());
		return groupController.add(group);
	}

	public OssResponse modifyGroup(long groupId, Group group) {
		GroupController groupController = new GroupController(this.session);
		Group emGroup = groupController.getById(groupId);
		if( this.session.getUser().equals(emGroup.getOwner())) {
			return groupController.modify(group);
		} else {
			return new OssResponse(this.getSession(),"ERROR", "You are not the owner of this group.");
		}
	}

	public OssResponse deleteGroup(long groupId) {
		GroupController groupController = new GroupController(this.session);
		Group emGroup = groupController.getById(groupId);
		if( this.session.getUser().equals(emGroup.getOwner())) {
			return groupController.delete(groupId);
		} else {
			return new OssResponse(this.getSession(),"ERROR", "You are not the owner of this group.");
		}
	}

	public List<String> getAvailableRoomActions(long roomId) {
		Room room = new RoomController(this.session).getById(roomId);
		List<String> actions = new ArrayList<String>();
		for( String action : this.getProperty("de.openschoolserver.dao.EducationController.RoomActions").split(",") ) {
			if(! this.checkMConfig(room, "disabledActions", action )) {
				actions.add(action);
			}
		}
		return actions;
	}

	public List<String> getAvailableUserActions(long userId) {
		User user = new UserController(this.session).getById(userId);
		List<String> actions = new ArrayList<String>();
		for( String action : this.getProperty("de.openschoolserver.dao.EducationController.UserActions").split(",") ) {
			if(! this.checkMConfig(user, "disabledActions", action )) {
				actions.add(action);
			}
		}
		return actions;
	}

	public List<String> getAvailableDeviceActions(long deviceId) {
		Device device = new DeviceController(this.session).getById(deviceId);
		List<String> actions = new ArrayList<String>();
		for( String action : this.getProperty("de.openschoolserver.dao.EducationController.DeviceActions").split(",") ) {
			if(! this.checkMConfig(device, "disabledActions", action )) {
				actions.add(action);
			}
		}
		return actions;
	}


	public OssResponse manageRoom(long roomId, String action, Map<String, String> actionContent) {
		OssResponse ossResponse = null;
		List<String> errors = new ArrayList<String>();
		DeviceController dc = new DeviceController(this.session);
		logger.debug("manageRoom called " + roomId + " action:");
		for( List<Long> loggedOn : this.getRoom(roomId) ) {
			logger.debug("manageRoom " + roomId + " user:" + loggedOn.get(0) + " device:" +  loggedOn.get(1));
			//Do not control the own workstation
			if( this.session.getDevice().getId().equals(loggedOn.get(1)) ||
					this.session.getUser().getId().equals(loggedOn.get(0)) ) {
				continue;
			}
			ossResponse = dc.manageDevice(loggedOn.get(1), action, actionContent);
			if( ossResponse.getCode().equals("ERROR")) {
				errors.add(ossResponse.getValue());
			}
		}
		if( errors.isEmpty() ) {
			new OssResponse(this.getSession(),"OK", "Device control was applied.");
		} else {
			return new OssResponse(this.getSession(),"ERROR",String.join("<br>", errors));
		}
		return null;
	}

	public Long getRoomActualController(long roomId) {
		EntityManager em = getEntityManager();
		try {
			Query query = em.createNamedQuery("SmartControl.getAllActiveInRoom");
			query.setParameter("roomId", roomId);
			if( query.getResultList().isEmpty() ) {
				//The room is free
				return null;
			}
			RoomSmartControl rsc = (RoomSmartControl) query.getResultList().get(0);
			return rsc.getOwner().getId();
		} catch (Exception e) {
		  logger.error(e.getMessage());
		  return null;
		} finally {
			em.close();
		}
	}

	/*
	 * getRoomControl
	 * @param roomId	The roomId which should be controlled
	 * @param minutes   How long do you want to control the room
	 * @return          An OssResponse object
	 */
	public OssResponse getRoomControl(long roomId, long minutes) {

		//Create the list of the controllers
		StringBuilder controllers = new StringBuilder();
		controllers.append(this.getConfigValue("SERVER")).append(";").append(this.session.getDevice().getIp());
		if( this.session.getDevice().getWlanIp() != null ) {
			controllers.append(";").append(this.session.getDevice().getWlanIp());
		}

		// Get the list of the devices
		DeviceController dc = new DeviceController(this.session);
		List<String>  devices = new ArrayList<String>();
		String domain         = "." + this.getConfigValue("DOMAIN");
		for( List<Long> loggedOn : this.getRoom(roomId) ) {
			//Do not control the own workstation
			if( this.session.getDevice().getId().equals(loggedOn.get(1))) {
				continue;
			}
			devices.add(dc.getById(loggedOn.get(1)).getName() + domain );
		}
		String[] program    = new String[5];
		StringBuffer reply  = new StringBuffer();
		StringBuffer stderr = new StringBuffer();
		program[0] = "/usr/bin/salt";
		program[1] = "-L";
		program[2] = String.join(",", devices);
		program[3] = "oss_client.set_controller_ip";
		program[4] = controllers.toString();
		OSSShellTools.exec(program, reply, stderr, null);

		RoomSmartControl roomSmartControl = new RoomSmartControl(roomId,this.session.getUserId(),minutes);
		EntityManager em = getEntityManager();
		try {
			em.getTransaction().begin();
			em.persist(roomSmartControl);
			em.getTransaction().commit();
		} catch (Exception e) {
			logger.error(e.getMessage());
			return new OssResponse(this.getSession(),"ERROR", e.getMessage());
		} finally {
			em.close();
		}	
		return new OssResponse(this.getSession(),"OK", "Now you have the control for the selected room.");

	}
}
