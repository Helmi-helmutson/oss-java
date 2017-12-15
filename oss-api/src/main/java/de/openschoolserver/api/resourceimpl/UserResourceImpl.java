/* (c) 2017 Péter Varkoly <peter@varkoly.de> - all rights reserved */
package de.openschoolserver.api.resourceimpl;

import java.util.List;

import javax.ws.rs.WebApplicationException;

import de.openschoolserver.api.resources.UserResource;
import de.openschoolserver.dao.Group;
import de.openschoolserver.dao.Session;
import de.openschoolserver.dao.User;
import de.openschoolserver.dao.controller.GroupController;
import de.openschoolserver.dao.controller.UserController;
import de.openschoolserver.dao.OssResponse;

public class UserResourceImpl implements UserResource {

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
		User user = new UserController(session).getByUid(uid);
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
		}
		return "";
	}

	
}
