/* (c) 2017 Péter Varkoly <peter@varkoly.de> - all rights reserved */
package de.openschoolserver.api.resourceimpl;

import java.util.List;

import de.openschoolserver.api.resources.UserResource;
import de.openschoolserver.dao.Group;
import de.openschoolserver.dao.Session;
import de.openschoolserver.dao.User;
import de.openschoolserver.dao.controller.UserController;

public class UserResourceImpl implements UserResource {

	@Override
	public User getById(Session session, long userId) {
		// TODO Auto-generated method stub
		final UserController userController = new UserController(session);
		final User user = userController.getById(userId);
		return user;
	}

	@Override
	public List<User> getByRole(Session session, String role) {
		// TODO Auto-generated method stub
		final UserController userController = new UserController(session);
		final List<User> users = userController.getByRole(role);
		return users;
	}
	
	@Override
	public List<User> getAll(Session session) {
		// TODO Auto-generated method stub
		final UserController userController = new UserController(session);
		final List<User> users = userController.getAll();
		return users;
	}

	@Override
	public String add(Session session, User user) {
		// TODO Auto-generated method stub
		final UserController userController = new UserController(session);
		return userController.add(user);
	}

	@Override
	public List<String> add(Session session, List<User> users) {
		// TODO Auto-generated method stub
		final UserController userController = new UserController(session);
		return userController.add(users);
	}

	@Override
	public boolean delete(Session session, long userId) {
		// TODO Auto-generated method stub
		final UserController userController = new UserController(session);
		return userController.delete(userId);
	}

	@Override
	public boolean modify(Session session, User user) {
		// TODO Auto-generated method stub
		final UserController userController = new UserController(session);
		return userController.modify(user);
	}

	@Override
	public List<User> search(Session session, String search) {
		// TODO Auto-generated method stub
		final UserController userController = new UserController(session);
		return userController.search(search);
	}

	@Override
	public List<Group> getAvailableGroups(Session session, long userId) {
		// TODO Auto-generated method stub
		final UserController userController = new UserController(session);
		final List<Group> groups = userController.getAvailableGroups(userId);
		return groups;
	}
}
