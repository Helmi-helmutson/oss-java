/* (c) 2017 Péter Varkoly <peter@varkoly.de> - all rights reserved */
package de.openschoolserver.api.resourceimpl;

import java.util.List;

import javax.ws.rs.WebApplicationException;

import de.openschoolserver.api.resources.UserResource;
import de.openschoolserver.dao.Group;
import de.openschoolserver.dao.Session;
import de.openschoolserver.dao.User;
import de.openschoolserver.dao.Response;
import de.openschoolserver.dao.controller.GroupController;
import de.openschoolserver.dao.controller.UserController;

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
	public Response add(Session session, User user) {
		final UserController userController = new UserController(session);
		return userController.add(user);
	}

	@Override
	public List<Response> add(Session session, List<User> users) {
		final UserController userController = new UserController(session);
		return userController.add(users);
	}

	@Override
	public Response delete(Session session, long userId) {
		final UserController userController = new UserController(session);
		return userController.delete(userId);
	}

	@Override
	public Response modify(Session session, User user) {
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
	public Response setMembers(Session session, long userId, List<Long> groupIds) {
		final UserController userController = new UserController(session);
		return userController.setGroups(userId,groupIds);
	}
	
	@Override
	public Response removeMember(Session session, long groupId, long userId) {
		final GroupController groupController = new GroupController(session);
		return groupController.removeMember(groupId,userId);	
	}

	@Override
	public Response addMember(Session session, long groupId, long userId) {
		final GroupController groupController = new GroupController(session);
		return groupController.addMember(groupId,userId);
	}

	@Override
	public Response syncFsQuotas(Session session, List<List<String>> Quotas) {
		final UserController userController = new UserController(session);
		return userController.syncFsQuotas(Quotas);
	}

	
}
