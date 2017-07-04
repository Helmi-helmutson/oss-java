/* (c) 2017 Péter Varkoly <peter@varkoly.de> - all rights reserved */
package de.openschoolserver.api.resourceimpl;

import java.util.List;

import javax.ws.rs.WebApplicationException;

import de.openschoolserver.api.resources.UserResource;
import de.openschoolserver.dao.Group;
import de.openschoolserver.dao.Session;
import de.openschoolserver.dao.User;
import de.openschoolserver.dao.controler.GroupControler;
import de.openschoolserver.dao.controler.UserControler;
import de.openschoolserver.dao.Response;

public class UserResourceImpl implements UserResource {

	@Override
	public User getById(Session session, long userId) {
		final UserControler userControler = new UserControler(session);
		final User user = userControler.getById(userId);
		 if (user == null) {
	            throw new WebApplicationException(404);
	    }
		return user;
	}

	@Override
	public List<User> getByRole(Session session, String role) {
		final UserControler userControler = new UserControler(session);
		final List<User> users = userControler.getByRole(role);
		if (users == null) {
            throw new WebApplicationException(404);
		}
		return users;
	}
	
	@Override
	public List<User> getAll(Session session) {
		final UserControler userControler = new UserControler(session);
		final List<User> users = userControler.getAll();
		if (users == null) {
            throw new WebApplicationException(404);
		}
		return users;
	}

	@Override
	public Response add(Session session, User user) {
		final UserControler userControler = new UserControler(session);
		return userControler.add(user);
	}

	@Override
	public List<Response> add(Session session, List<User> users) {
		final UserControler userControler = new UserControler(session);
		return userControler.add(users);
	}

	@Override
	public Response delete(Session session, long userId) {
		final UserControler userControler = new UserControler(session);
		return userControler.delete(userId);
	}

	@Override
	public Response modify(Session session, User user) {
		final UserControler userControler = new UserControler(session);
		return userControler.modify(user);
	}

	@Override
	public List<User> search(Session session, String search) {
		final UserControler userControler = new UserControler(session);
		final List<User> users = userControler.search(search);
		if (users == null) {
            throw new WebApplicationException(404);
		}
		return users;
	}

	@Override
	public List<Group> getAvailableGroups(Session session, long userId) {
		final UserControler userControler = new UserControler(session);
		final List<Group> groups = userControler.getAvailableGroups(userId);
		if (groups == null) {
            throw new WebApplicationException(404);
		}
		return groups;
	}

	@Override
	public List<Group> groups(Session session, long userId) {
		final UserControler userControler = new UserControler(session);
		final List<Group> groups =  userControler.getGroups(userId);
		if (groups == null) {
            throw new WebApplicationException(404);
		}
		return groups;
	}
	
	@Override
	public Response setMembers(Session session, long userId, List<Long> groupIds) {
		final UserControler userControler = new UserControler(session);
		return userControler.setGroups(userId,groupIds);
	}
	
	@Override
	public Response removeMember(Session session, long groupId, long userId) {
		final GroupControler groupControler = new GroupControler(session);
		return groupControler.removeMember(groupId,userId);	
	}

	@Override
	public Response addMember(Session session, long groupId, long userId) {
		final GroupControler groupControler = new GroupControler(session);
		return groupControler.addMember(groupId,userId);
	}

	@Override
	public Response syncFsQuotas(Session session, List<List<String>> Quotas) {
		final UserControler userControler = new UserControler(session);
		return userControler.syncFsQuotas(Quotas);
	}

	@Override
	public List<User> getUsers(Session session, List<Long> userIds) {
		final UserControler userControler = new UserControler(session);
		return userControler.getUsers(userIds);
	}

	
}
