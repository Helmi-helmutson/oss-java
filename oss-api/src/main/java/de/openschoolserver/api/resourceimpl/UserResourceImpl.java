package de.openschoolserver.api.resourceimpl;

import java.util.List;

import de.openschoolserver.api.resources.UserResource;
import de.openschoolserver.dao.Session;
import de.openschoolserver.dao.User;

public class UserResourceImpl implements UserResource {

	@Override
	public User getById(Session session, int userId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<User> getAll(Session session) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean add(Session session, User user) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean delete(Session session, int userId) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<String> getAvailableIPAddresses(Session session, int userId) {
		// TODO Auto-generated method stub
		return null;
	}

}
