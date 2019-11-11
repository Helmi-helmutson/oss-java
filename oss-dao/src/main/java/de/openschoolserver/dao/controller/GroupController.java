/* (c) 2017 Péter Varkoly <peter@varkoly.de> - all rights reserved
 * (c) 2017 EXTIS GmbH www.extis.de - all rights reserved */
package de.openschoolserver.dao.controller;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Comparator;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.ValidatorFactory;

import de.openschoolserver.dao.User;
import de.openschoolserver.dao.tools.OSSShellTools;
import de.openschoolserver.dao.Category;
import de.openschoolserver.dao.Enumerate;
import de.openschoolserver.dao.Group;
import de.openschoolserver.dao.Session;
import de.openschoolserver.dao.OssResponse;
import de.openschoolserver.dao.Room;

@SuppressWarnings( "unchecked" )
public class GroupController extends Controller {

	Logger logger = LoggerFactory.getLogger(GroupController.class);
	private List<String> parameters;

	public GroupController(Session session,EntityManager em) {
		super(session,em);
	}

	public Group getById(long groupId) {
		try {
			return this.em.find(Group.class, groupId);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			return null;
		} finally {
		}
	}

	public List<Group> getByType(String groupType) {
		List<Group> groups = new ArrayList<>();
		try {
			Query query = this.em.createNamedQuery("Group.getByType");
			query.setParameter("groupType", groupType);
			groups = query.getResultList();
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		} finally {
		}
		groups.sort(Comparator.comparing(Group::getName));
		return groups;
	}

	public Group getByName(String name) {
		try {
			Query query = this.em.createNamedQuery("Group.getByName");
			query.setParameter("name", name);
			List<Group> result = query.getResultList();
			if (result!=null && result.size()>0) {
			return result .get(0);
			} else {
				return null;
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			return null;
		} finally {
		}
	}
	/** getByExactName compares only the name and not the description. used for import of user data */
	public Group getByExactName(String name) {
		try {
			Query query = this.em.createNamedQuery("Group.getByExactName");
			query.setParameter("name", name);
			List<Group> result = query.getResultList();
			if (result!=null && result.size()>0) {
			return result .get(0);
			} else {
				return null;
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			return null;
		} finally {
		}
	}

	public List<Group> search(String search) {
		List<Group> groups = new ArrayList<>();
		try {
			Query query = this.em.createNamedQuery("Group.search");
			query.setParameter("search", search + "%");
			groups = query.getResultList();
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
		}
		groups.sort(Comparator.comparing(Group::getName));
		return groups;
	}

	public List<Group> getAll() {
		List<Group> groups = new ArrayList<>();
		try {
			Query query = this.em.createNamedQuery("Group.findAll");
			return query.getResultList();
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		} finally {
		}
		groups.sort(Comparator.comparing(Group::getName));
		return groups;
	}

	public OssResponse add(Group group){
		//Check group parameter
		StringBuilder errorMessage = new StringBuilder();
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		for (ConstraintViolation<Group> violation : factory.getValidator().validate(group) ) {
			errorMessage.append(violation.getMessage()).append(getNl());
		}
		if( errorMessage.length() > 0 ) {
			return new OssResponse(this.getSession(),"ERROR", errorMessage.toString());
		}
		// Group names will be converted upper case
		group.setName(group.getName().toUpperCase());
		// First we check if the parameter are unique.
		if( ! this.isNameUnique(group.getName())){
			return new OssResponse(this.getSession(),"ERROR","Group name is not unique.");
		}
		group.setOwner(this.session.getUser());
		try {
			this.em.getTransaction().begin();
			if( group.getGroupType().equals("primary")) {
				Enumerate enumerate = new Enumerate("role",group.getName());
				this.em.persist(enumerate);
			}
			this.em.persist(group);
			if( group.getGroupType().equals("workgroup") || group.getGroupType().equals("guest")) {
				this.session.getUser().getOwnedGroups().add(group);
				User user = this.em.find(User.class, this.session.getUser().getId());
				group.setOwnerId(user.getId());
				user.getOwnedGroups().add(group);
			}
			this.em.getTransaction().commit();
			logger.debug("Created Group:" + group);
		} catch (Exception e) {
			logger.error("Error crating a group" + e.getMessage(),e);
			return new OssResponse(this.getSession(),"ERROR",e.getMessage());
		} finally {
		}
		this.startPlugin("add_group", group);
		if( group.getGroupType().equals("workgroup") ) {
			logger.debug("Add creator to member");
			createSmartRoomForGroup(group,true,false);
			addMember(group,this.session.getUser());
		}
		if(group.getGroupType().equals("class")) {
			this.createCategoryForGroup(group, true, true, "informations");
		}
		return new OssResponse(this.getSession(),"OK","Group was created.",group.getId());
	}

	public OssResponse modify(Group group){
		Group oldGroup = this.getById(group.getId());
		if( !this.mayModify(oldGroup) ) {
       return new OssResponse(this.getSession(),"ERROR","You must not modify this group.");
        }
		oldGroup.setDescription(group.getDescription());
		//Check group parameter
		StringBuilder errorMessage = new StringBuilder();
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		for (ConstraintViolation<Group> violation : factory.getValidator().validate(group) ) {
			errorMessage.append(violation.getMessage()).append(getNl());
		}
		if( errorMessage.length() > 0 ) {
				return new OssResponse(this.getSession(),"ERROR", errorMessage.toString());
		}
		try {
			this.em.getTransaction().begin();
			this.em.merge(oldGroup);
			this.em.getTransaction().commit();
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			return new OssResponse(this.getSession(),"ERROR",e.getMessage());
		}  finally {
		}
		this.startPlugin("modify_group", oldGroup);
		return new OssResponse(this.getSession(),"OK","Group was modified.");
	}

	public OssResponse delete(Group group){
		// Remove group from GroupMember of table
		group =  this.em.find(Group.class, group.getId());
		if( this.isProtected(group)) {
			return new OssResponse(this.getSession(),"ERROR","This group must not be deleted.");
		}
		if( !this.mayModify(group) ) {
			return new OssResponse(this.getSession(),"ERROR","You must not delete this group.");
        }
		//Primary group must not be deleted if there are member
		if( group.getGroupType().equals("primary")) {
			if( group.getUsers() != null  && !group.getUsers().isEmpty() ) {
				return new OssResponse(this.getSession(),"ERROR","You must not delete this primary group because this still contains member(s).");
			}
		}
		//Start the plugin
		this.startPlugin("delete_group", group);
		try {
			this.em.getTransaction().begin();
			if( !em.contains(group)) {
				group = this.em.merge(group);
			}
			for ( Category category : group.getCategories() ) {
				if( category.getCategoryType().equals("smartRoom") && category.getName().equals(group.getName()) ) {
					for( Room room : category.getRooms() ) {
						if( room.getRoomType().equals("smartRoom") && room.getName().equals(group.getName())) {
							this.em.remove(room);
						}
					}
					User owner = category.getOwner();
					if( owner != null ) {
						owner.getCategories().remove(category);
						this.em.merge(owner);
					}
					this.em.remove(category);
				}
			}
			for( User user : group.getUsers() ) {
				user.getGroups().remove(group);
				this.em.merge(user);
			}
			this.deletAllConfigs(group);
			this.em.remove(group);
			this.em.getTransaction().commit();
			SessionController.removeAllSessionsFromCache();
			//em.getEntityManagerFactory().getCache().evictAll();
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			return new OssResponse(this.getSession(),"ERROR",e.getMessage());
		} finally {
		}
		return new OssResponse(this.getSession(),"OK","Group was deleted.");
	}

	/**
	 * Import groups from a CSV file. This MUST have following format:
	 * Separator: semicolon
	 * No header
	 * All fields are mandatory
	 * No header
	 * Fields: name;description;group type;member
	 * Member: space separated list of user names (uid)
	 * Group Type: can be class, primary or workgroup
	 * @param fileInputStream
	 * @param contentDispositionHeader
	 * @return
	 */
	public OssResponse importGroups(InputStream fileInputStream,
			FormDataContentDisposition contentDispositionHeader) {
		File file = null;
		List<String> importFile;
		OssResponse ossResponse;
		try {
			file = File.createTempFile("oss_uploadFile", ".ossb", new File("/opt/oss-java/tmp/"));
			Files.copy(fileInputStream, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
			importFile = Files.readAllLines(file.toPath());
		} catch (IOException e) {
			logger.error("File error:" + e.getMessage(), e);
			return new OssResponse(this.getSession(),"ERROR", e.getMessage());
		}
		int count = 0;
		for(String line : importFile ) {
			String[] values = line.split(";");
			count++;
			if( values.length < 3 ) {
				logger.error("importGroups bad line: " + count + ":" + line);
				continue;
			}
			Group group = this.getByName(values[0]);
			if( group == null ) {
				group = new Group(values[0],values[1],values[2]);
				ossResponse = this.add(group);
				if( ossResponse.getCode().equals("ERROR") ) {
					logger.error("importGroups. Error in line: " + count + ": " + ossResponse.getValue() );
					continue;
				}
				group = this.getById(ossResponse.getObjectId());
			}
			if( values.length > 3 ) {
				for( String uid : values[3].split(" ") ) {
					this.addMember(values[0], uid);
				}
			}
		}
		return new OssResponse(this.getSession(),"OK","Groups were imported.");
	}

	public OssResponse delete(long groupId){
		Group group = this.getById(groupId);
		if( group == null ) {
			return new OssResponse(this.getSession(),"ERROR", "Can not find the group with id %s.",null,String.valueOf(groupId));
		}
		return delete(group);
	}

	public OssResponse delete(String groupName){
		Group group = this.getByName(groupName);
		if( group == null ) {
			return new OssResponse(this.getSession(),"ERROR", "Can not find the group with id %s.",null,groupName);
		}
		return delete(group);
	}

	public List<User> getAvailableMember(long groupId){
		Group group = this.getById(groupId);
		Query query = this.em.createNamedQuery("User.findAll");
		List<User> allUsers = query.getResultList();
		allUsers.removeAll(group.getUsers());
		return allUsers;
	}

	public List<User> getMembers(long groupId) {
		Group group = this.getById(groupId);
		return group.getUsers();
	}

	public OssResponse setMembers(long groupId, List<Long> userIds) {
		Group group = this.getById(groupId);
		if( !this.mayModify(group) ) {
       return new OssResponse(this.getSession(),"ERROR","You must not modify this group.");
        }
		List<User> usersToRemove = new ArrayList<User>();
		List<User> usersToAdd    = new ArrayList<User>();
		List<User> users = new ArrayList<User>();
		for( Long userId : userIds ) {
			users.add(em.find(User.class, userId));
		}
		for( User user : users ){
			if(! group.getUsers().contains(user)){
				usersToAdd.add(user);
			}
		}
		for( User user : group.getUsers() ) {
			if(! user.getRole().equals(group.getName()) ) {
				//User must not be removed from it's primary group.
				continue;
			}
			if(! users.contains(user)) {
				usersToRemove.add(user);
			}
		}
		try {
			this.em.getTransaction().begin();
			for( User user : usersToAdd) {
				group.getUsers().add(user);
				user.getGroups().add(group);
				this.em.merge(user);
			}
			for( User user : usersToRemove ) {
				group.getUsers().remove(user);
				user.getGroups().remove(group);
				this.em.merge(user);
			}
			this.em.merge(group);
			this.em.getTransaction().commit();
		}catch (Exception e) {
			return new OssResponse(this.getSession(),"ERROR",e.getMessage());
		} finally {
		}
		this.changeMemberPlugin("addmembers", group, usersToAdd);
		this.changeMemberPlugin("removemembers", group, usersToRemove);
		return new OssResponse(this.getSession(),"OK","The members of group was set.");
	}


	public OssResponse addMember(Group group, User user) {
		if( !this.mayModify(group) ) {
			return new OssResponse(this.getSession(),"ERROR","You must not modify this group.");
        }
		parameters = new ArrayList<String>();
		parameters.add(user.getUid());
		parameters.add(group.getName());
		if( user.getGroups().contains(group)) {
			return new OssResponse(this.getSession(),"OK","User %s is already member of group %s.",null,parameters );
		}
		group.getUsers().add(user);
		if (user.getGroups()==null) {
			user.setGroups(new ArrayList<Group>());
		}
		user.getGroups().add(group);
		try {
			this.em.getTransaction().begin();
			this.em.merge(user);
			this.em.merge(group);
			this.em.getTransaction().commit();
		} catch (Exception e) {
			return new OssResponse(this.getSession(),"ERROR",e.getMessage());
		} finally {
		}
		this.changeMemberPlugin("addmembers", group, user);
		return new OssResponse(this.getSession(),"OK","User %s was added to group %s.",null,parameters );
	}

	public OssResponse addMembers(Group group, List<User> users) {
		if( !this.mayModify(group) ) {
			return new OssResponse(this.getSession(),"ERROR","You must not modify this group.");
        }
		try {
			for( User user: users ) {
				if(! user.getGroups().contains(group) ) {
					group.getUsers().add(user);
					user.getGroups().add(group);
					this.em.getTransaction().begin();
					this.em.merge(user);
					this.em.merge(group);
					this.em.getTransaction().commit();
				}
			}
		} catch (Exception e) {
			return new OssResponse(this.getSession(),"ERROR",e.getMessage());
		} finally {
		}
		this.changeMemberPlugin("addmembers", group, users);
		return new OssResponse(this.getSession(),"OK","User %s was added to group %s.",null,parameters );
	}
	public OssResponse addMember(long groupId, long userId) {
		Group group = this.em.find(Group.class, groupId);
		User  user  = this.em.find(User.class, userId);
		if( group == null ) {
			return new OssResponse(this.getSession(),"ERROR","Group %s was not found.",null,String.valueOf(groupId));
		}
		if( user == null ) {
			return new OssResponse(this.getSession(),"ERROR","User %s was not found.",null,String.valueOf(userId));
		}
		return this.addMember(group, user);
	}

	public OssResponse addMember(String groupName, String uid) {
		Group group = this.getByName(groupName);
		User  user  = new UserController(this.session,this.em).getByUid(uid);
		if( group == null ) {
			return new OssResponse(this.getSession(),"ERROR","Group %s was not found.",null,groupName);
		}
		if( user == null ) {
			return new OssResponse(this.getSession(),"ERROR","User %s was not found.",null,uid);
		}
		group = this.em.find(Group.class, group.getId());
		user  = this.em.find(User.class,  user.getId());
		return this.addMember(group, user);
	}

	public OssResponse removeMember(String groupName, String uid) {
		Long groupId = this.getByName(groupName).getId();
		Long userId  = new UserController(this.session,this.em).getByUid(uid).getId();
		return this.removeMember(groupId, userId);
	}

	public OssResponse removeMember(long groupId, long userId) {
		Group group = this.em.find(Group.class, groupId);
		if( !this.mayModify(group) ) {
       return new OssResponse(this.getSession(),"ERROR","You must not modify this group.");
        }
		User  user  = this.em.find(User.class, userId);
		if( user.getRole().equals(group.getName()) ) {
			return new OssResponse(this.getSession(),"ERROR","User must not be removed from it's primary group.");
		}
		if( group.getOwner().equals(user) ) {
			return new OssResponse(this.getSession(),"ERROR","You must not remove yourself from your owned groups.");
		}
		group.getUsers().remove(user);
		user.getGroups().remove(group);
		try {
			this.em.getTransaction().begin();
			this.em.merge(user);
			this.em.merge(group);
			this.em.getTransaction().commit();
		} catch (Exception e) {
			return new OssResponse(this.getSession(),"ERROR",e.getMessage());
		} finally {
		}
		this.changeMemberPlugin("removemembers", group, user);
		parameters = new ArrayList<String>();
		parameters.add(user.getUid());
		parameters.add(group.getName());
		return new OssResponse(this.getSession(),"OK","User %s was removed from group %s.",null,parameters );
	}

	public List<Group> getGroups(List<Long> groupIds) {
		List<Group> groups = new ArrayList<Group>();
		for( Long id : groupIds){
			groups.add(this.getById(id));
		}
		return groups;
	}

	public OssResponse setOwner(String groupName, String userName) {
		Long groupId = this.getByName(groupName).getId();
		User user    = new UserController(this.session,this.em).getByUid(userName);
		Group group = this.em.find(Group.class, groupId);
		group.setOwner(user);
		user.getOwnedGroups().add(group);
		try {
			this.em.getTransaction().begin();
			this.em.merge(user);
			this.em.merge(group);
			this.em.getTransaction().commit();
		} catch (Exception e) {
			return new OssResponse(this.getSession(),"ERROR",e.getMessage());
		} finally {
		}
		return new OssResponse(this.getSession(),"OK","Group owner was changed.");
	}

	public OssResponse cleanGrupDirectory(Group group) {
		StringBuffer reply = new StringBuffer();
		StringBuffer error = new StringBuffer();
		String[] program = new String[2];
		program[0] = "/usr/sbin/oss_clean_group_directory.sh";
		program[1] = group.getName();
		OSSShellTools.exec(program, reply, error, null);
		if( error.length() > 0 ) {
			parameters = new ArrayList<String>();
			parameters.add(group.getName());
			parameters.add(error.toString());
			return new OssResponse(this.getSession(),"ERROR","Class directory %s can not be cleaned. Reason %s",null,parameters);
		}
		return new OssResponse(this.getSession(),"OK","Group directory was cleaned.");
	}

	public OssResponse cleanClassDirectories() {
		for( Group group : this.getByType("class")) {
			if( cleanGrupDirectory(group).getCode().equals("ERROR") ) {
				return new OssResponse(this.getSession(),"ERROR","Class directory %s can not be cleaned.",null,group.getName());
			}
		}
		return new OssResponse(this.getSession(),"OK","Class directories was cleaned.");
	}

	public OssResponse createCategoryForGroup(Long groupId, boolean studentsOnly, boolean publicAccess, String type) {
		return createCategoryForGroup(this.getById(groupId),studentsOnly,publicAccess, type);
	}

	public OssResponse createCategoryForGroup(Group group, boolean studentsOnly, boolean publicAccess, String type) {
		for ( Category cat : group.getCategories() ) {
			if( cat.getCategoryType().equals(type) && cat.getName().equals(group.getName()) ) {
				return new OssResponse(this.getSession(),"OK","Smart room is for this group already created.");
			}
		}
		Category category = new Category();
		category.setName(group.getName());
		category.setDescription(group.getDescription());
		category.setCategoryType(type);
		category.setStudentsOnly(studentsOnly);
		category.setPublicAccess(publicAccess);
		category.setGroupIds(new ArrayList<Long>());
		category.getGroupIds().add(group.getId());
		return new CategoryController(this.session,this.em).add(category);
	}

	public OssResponse createSmartRoomForGroup(Long groupId, boolean studentsOnly, boolean publicAccess) {
		Group group = this.getById(groupId);
		return createSmartRoomForGroup(group,studentsOnly,publicAccess);
	}

	public OssResponse createSmartRoomForGroup(Group group, boolean studentsOnly, boolean publicAccess) {
		for ( Category cat : group.getCategories() ) {
			if( cat.getCategoryType().equals("smartRoom") && cat.getName().equals(group.getName()) ) {
				return new OssResponse(this.getSession(),"OK","Smart room is for this group already created.");
			}
		}
		Category category = new Category();
		category.setName(group.getName());
		category.setDescription(group.getDescription());
		category.setCategoryType("smartRoom");
		category.setStudentsOnly(studentsOnly);
		category.setPublicAccess(publicAccess);
		category.setGroupIds(new ArrayList<Long>());
		category.getGroupIds().add(group.getId());
		return new EducationController(this.session,this.em).createSmartRoom(category);
	}
}
