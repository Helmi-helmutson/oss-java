package de.openschoolserver.api.resourceimpl;

import java.util.List;


import de.openschoolserver.api.resources.GroupResource;
import de.openschoolserver.dao.Group;
import de.openschoolserver.dao.User;
import de.openschoolserver.dao.Session;
import de.openschoolserver.dao.Response;
import de.openschoolserver.dao.controller.GroupController;

public class GroupResourceImpl implements GroupResource {

	@Override
	public Group getById(Session session, long groupId) {
		// TODO Auto-generated method stub
	    final GroupController groupController = new GroupController(session);
		return groupController.getById(groupId);
	}

	@Override
	public List<User> getAvailableMembers(Session session, long groupId) {
		// TODO Auto-generated method stub
		final GroupController groupController = new GroupController(session);
		return groupController.getAvailableMember(groupId);
	}

	@Override
	public List<User> getMembers(Session session, long groupId) {
		final GroupController groupController = new GroupController(session);
		return groupController.getMember(groupId);
	}
	
	@Override
	public List<Group> getByType(Session session, String type) {
		// TODO Auto-generated method stub
		final GroupController groupController = new GroupController(session);
		return groupController.getByType(type);
	}

	@Override
	public List<Group> getAll(Session session) {
		// TODO Auto-generated method stub
		final GroupController groupController = new GroupController(session);
		return groupController.getAll();
	}

	@Override
	public List<Group> search(Session session, String search) {
		// TODO Auto-generated method stub
		final GroupController groupController = new GroupController(session);
		return groupController.search(search);
	}

	@Override
	public Response add(Session session, Group group) {
		// TODO Auto-generated method stub
		final GroupController groupController = new GroupController(session);
		return groupController.add(group);
	}

	@Override
	public Response modify(Session session, Group group) {
		// TODO Auto-generated method stub
		final GroupController groupController = new GroupController(session);
		return groupController.modify(group);
	}

	@Override
	public Response delete(Session session, long groupId) {
		// TODO Auto-generated method stub
		final GroupController groupController = new GroupController(session);
		return groupController.delete(groupId);
	}

	

}
