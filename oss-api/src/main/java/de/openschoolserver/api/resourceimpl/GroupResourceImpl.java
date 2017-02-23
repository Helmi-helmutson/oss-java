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
	    final GroupController groupController = new GroupController(session);
		return groupController.getById(groupId);
	}

	@Override
	public List<User> getAvailableMembers(Session session, long groupId) {
		final GroupController groupController = new GroupController(session);
		return groupController.getAvailableMember(groupId);
	}

	@Override
	public List<User> getMembers(Session session, long groupId) {
		final GroupController groupController = new GroupController(session);
		return groupController.getMembers(groupId);
	}
	
	@Override
	public List<Group> getByType(Session session, String type) {
		final GroupController groupController = new GroupController(session);
		return groupController.getByType(type);
	}

	@Override
	public List<Group> getAll(Session session) {
		final GroupController groupController = new GroupController(session);
		return groupController.getAll();
	}

	@Override
	public List<Group> search(Session session, String search) {
		final GroupController groupController = new GroupController(session);
		return groupController.search(search);
	}

	@Override
	public Response add(Session session, Group group) {
		final GroupController groupController = new GroupController(session);
		return groupController.add(group);
	}

	@Override
	public Response modify(Session session, Group group) {
		final GroupController groupController = new GroupController(session);
		return groupController.modify(group);
	}

	@Override
	public Response delete(Session session, long groupId) {
		final GroupController groupController = new GroupController(session);
		return groupController.delete(groupId);
	}

	@Override
	public Response setMembers(Session session, long groupId, List<Long> users) {
		final GroupController groupController = new GroupController(session);
		return groupController.setMembers(groupId,users);
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

}
