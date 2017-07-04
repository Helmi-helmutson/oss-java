package de.openschoolserver.api.resourceimpl;

import java.util.List;



import de.openschoolserver.api.resources.GroupResource;
import de.openschoolserver.dao.Group;
import de.openschoolserver.dao.User;
import de.openschoolserver.dao.controler.GroupControler;
import de.openschoolserver.dao.Session;
import de.openschoolserver.dao.Response;

public class GroupResourceImpl implements GroupResource {

	@Override
	public Group getById(Session session, long groupId) {
	    final GroupControler groupControler = new GroupControler(session);
		return groupControler.getById(groupId);
	}

	@Override
	public List<User> getAvailableMembers(Session session, long groupId) {
		final GroupControler groupControler = new GroupControler(session);
		return groupControler.getAvailableMember(groupId);
	}

	@Override
	public List<User> getMembers(Session session, long groupId) {
		final GroupControler groupControler = new GroupControler(session);
		return groupControler.getMembers(groupId);
	}
	
	@Override
	public List<Group> getByType(Session session, String type) {
		final GroupControler groupControler = new GroupControler(session);
		return groupControler.getByType(type);
	}

	@Override
	public List<Group> getAll(Session session) {
		final GroupControler groupControler = new GroupControler(session);
		return groupControler.getAll();
	}

	@Override
	public List<Group> search(Session session, String search) {
		final GroupControler groupControler = new GroupControler(session);
		return groupControler.search(search);
	}

	@Override
	public Response add(Session session, Group group) {
		final GroupControler groupControler = new GroupControler(session);
		return groupControler.add(group);
	}

	@Override
	public Response modify(Session session, Group group) {
		final GroupControler groupControler = new GroupControler(session);
		return groupControler.modify(group);
	}

	@Override
	public Response delete(Session session, long groupId) {
		final GroupControler groupControler = new GroupControler(session);
		return groupControler.delete(groupId);
	}

	@Override
	public Response setMembers(Session session, long groupId, List<Long> users) {
		final GroupControler groupControler = new GroupControler(session);
		return groupControler.setMembers(groupId,users);
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
	public List<Group> getGroups(Session session, List<Long> groupIds) {
		final GroupControler groupControler = new GroupControler(session);
		return groupControler.getGroups(groupIds);
	}

}
