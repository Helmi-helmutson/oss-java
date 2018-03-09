/* (c) 2017 Péter Varkoly <peter@varkoly.de> - all rights reserved */
package de.openschoolserver.api.resourceimpl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.ws.rs.WebApplicationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.openschoolserver.api.resources.UserResource;
import de.openschoolserver.dao.Category;
import de.openschoolserver.dao.Group;
import de.openschoolserver.dao.Session;
import de.openschoolserver.dao.User;
import de.openschoolserver.dao.controller.CategoryController;
import de.openschoolserver.dao.controller.GroupController;
import de.openschoolserver.dao.controller.UserController;
import de.openschoolserver.dao.OssResponse;

public class UserResourceImpl implements UserResource {
	
	Logger logger           = LoggerFactory.getLogger(UserResourceImpl.class);

	@Override
	public User getById(Session session, long userId) {
		final UserController userController = new UserController(session);
		final User user = userController.getById(userId);
		 if (user == null) {
	            throw new WebApplicationException(404);
	    }
		return user;
	}

	@Override
	public List<User> getByRole(Session session, String role) {
		final UserController userController = new UserController(session);
		final List<User> users = userController.getByRole(role);
		if (users == null) {
            throw new WebApplicationException(404);
		}
		return users;
	}
	
	@Override
	public List<User> getAll(Session session) {
		final UserController userController = new UserController(session);
		final List<User> users = userController.getAll();
		if (users == null) {
            throw new WebApplicationException(404);
		}
		return users;
	}

	@Override
	public OssResponse add(Session session, User user) {
		final UserController userController = new UserController(session);
		return userController.add(user);
	}

	@Override
	public List<OssResponse> add(Session session, List<User> users) {
		final UserController userController = new UserController(session);
		return userController.add(users);
	}

	@Override
	public OssResponse delete(Session session, long userId) {
		final UserController userController = new UserController(session);
		return userController.delete(userId);
	}

	@Override
	public OssResponse modify(Session session, User user) {
		final UserController userController = new UserController(session);
		return userController.modify(user);
	}

	@Override
	public List<User> search(Session session, String search) {
		final UserController userController = new UserController(session);
		final List<User> users = userController.search(search);
		if (users == null) {
            throw new WebApplicationException(404);
		}
		return users;
	}

	@Override
	public List<Group> getAvailableGroups(Session session, long userId) {
		final UserController userController = new UserController(session);
		final List<Group> groups = userController.getAvailableGroups(userId);
		if (groups == null) {
            throw new WebApplicationException(404);
		}
		return groups;
	}

	@Override
	public List<Group> groups(Session session, long userId) {
		final UserController userController = new UserController(session);
		final List<Group> groups =  userController.getGroups(userId);
		if (groups == null) {
            throw new WebApplicationException(404);
		}
		return groups;
	}
	
	@Override
	public OssResponse setMembers(Session session, long userId, List<Long> groupIds) {
		final UserController userController = new UserController(session);
		return userController.setGroups(userId,groupIds);
	}
	
	@Override
	public OssResponse removeMember(Session session, long groupId, long userId) {
		final GroupController groupController = new GroupController(session);
		return groupController.removeMember(groupId,userId);	
	}

	@Override
	public OssResponse addMember(Session session, long groupId, long userId) {
		final GroupController groupController = new GroupController(session);
		return groupController.addMember(groupId,userId);
	}

	@Override
	public OssResponse syncFsQuotas(Session session, List<List<String>> Quotas) {
		final UserController userController = new UserController(session);
		return userController.syncFsQuotas(Quotas);
	}

	@Override
	public List<User> getUsers(Session session, List<Long> userIds) {
		final UserController userController = new UserController(session);
		return userController.getUsers(userIds);
	}

	@Override
	public String getUserAttribute(Session session, String uid, String attribute) {
		final UserController userController = new UserController(session);
		User user = userController.getByUid(uid);
		if( user == null) {
			return "";
		}
		switch(attribute.toLowerCase()) {
		case "role":
			return user.getRole();
		case "uuid":
			return user.getUuid();
		case "givenname":
			return user.getGivenName();
		case "surname":
			return user.getSurName();
		case "groups":
			List<String> groups = new ArrayList<String>();
			for( Group group : user.getGroups() ) {
				groups.add(group.getName());
			}
			return String.join(" ", groups);
		default:
			//This is a config or mconfig. We have to merge it from the groups from actual room and from the user
			List<String> configs = new ArrayList<String>();
			//Group configs
			for( Group group : user.getGroups() ) {
				if( userController.getConfig(group, attribute) != null ) {
					configs.add(userController.getConfig(group, attribute));
				}
				for( String config : userController.getMConfigs(group, attribute) ) {
					if( config != null ) {
						configs.add(config);
					}
				}
			}
			//Room configs.
			if( session.getRoom() != null ) {
				if( userController.getConfig(session.getRoom(), attribute) != null ) {
					configs.add(userController.getConfig(session.getRoom(), attribute));
				}
				for( String config : userController.getMConfigs(session.getRoom(), attribute) ) {
					if( config != null ) {
						configs.add(config);
					}
				}	
			}
			if( userController.getConfig(user, attribute) != null ) {
				configs.add(userController.getConfig(user, attribute));
			}
			for( String config : userController.getMConfigs(user, attribute) ) {
				if( config != null ) {
					configs.add(config);
				}
			}
			return String.join(userController.getNl(), configs);
		}
	}

	@Override
	public List<Category> getGuestUsers(Session session) {
		final CategoryController categoryController= new CategoryController(session);
		if( categoryController.isSuperuser() ) {
			return categoryController.getByType("guestUsers");
		}
		List<Category> categories = new ArrayList<Category>();
		for( Category category : categoryController.getByType("guestUsers") ) {
			if( category.getOwner().equals(session.getUser()) ) {
				categories.add(category);
			}
		}
		return categories;
	}

	@Override
	public Category getGuersUsersCategory(Session session, Long guestUsersId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OssResponse deleteGuestUsers(Session session, Long guestUsersId) {
		final UserController     userController    = new UserController(session);
		final CategoryController categoryController= new CategoryController(session);
		final GroupController    groupController   = new GroupController(session);
		Category category = categoryController.getById(guestUsersId);
		//Have to implement.
		OssResponse ossResponse = new OssResponse();
		return ossResponse;
	}

	@Override
	public OssResponse addGuestUsers(Session session, String name, String description, Long roomId, 
			int count, Date validUntil) {
		final UserController     userController    = new UserController(session);
		final CategoryController categoryController= new CategoryController(session);
		final GroupController    groupController   = new GroupController(session);
		Category category = new Category();
		category.setCategoryType("guestUsers");
		category.setName(name);
		category.setDescription(description);
		category.setValidFrom(categoryController.now());
		category.setValidUntil(validUntil);
		OssResponse ossResponse =  categoryController.add(category);
		if( ossResponse.getCode().equals("ERROR")) {
			return ossResponse;
		}
		category = categoryController.getById(ossResponse.getObjectId());
		
		Group group = new Group();
		group.setGroupType("guest");
		group.setName(name);
		group.setDescription(description);
		ossResponse = groupController.add(group);
		if( ossResponse.getCode().equals("ERROR")) {
			categoryController.delete(category.getId());
			return ossResponse;
		}

		group = groupController.getById(ossResponse.getObjectId());
		EntityManager em = groupController.getEntityManager();
		try {
			em.getTransaction().begin();
			category.setGroups(new ArrayList<Group>());
			category.getGroups().add(group);
			group.setCategories(new ArrayList<Category>());
			group.getCategories().add(category);
			em.merge(category);
			em.merge(group);
			em.getTransaction().commit();
		} catch (Exception e) {
			logger.error(e.getMessage());
			return null;
		}
		for( int i = 1; i < count +1 ;  i++) {
			String userName = String.format("%s%02d", name, i);
			User user = new User();
			user.setUid(userName);
			user.setGivenName(userName);
			user.setGivenName("GuestUser");
			user.setRole("guest");
			ossResponse = userController.add(user);
			user = userController.getById(ossResponse.getId());
			groupController.addMember(group, user);
			categoryController.addMember(category.getId(), "user", user.getId());
		}
		ossResponse.setObjectId(category.getId());
		ossResponse.setValue("Guest Users were created succesfully");
		ossResponse.setCode("OK");
		return ossResponse; 
	}
}
