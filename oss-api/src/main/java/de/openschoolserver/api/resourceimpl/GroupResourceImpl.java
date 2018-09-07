/* (c) 2017 Péter Varkoly <peter@varkoly.de> - all rights reserved */
package de.openschoolserver.api.resourceimpl;

import java.util.ArrayList;
import java.util.List;



import de.openschoolserver.api.resources.GroupResource;
import de.openschoolserver.dao.Group;
import de.openschoolserver.dao.User;
import de.openschoolserver.dao.controller.GroupController;
import de.openschoolserver.dao.Session;
import de.openschoolserver.dao.OssResponse;

public class GroupResourceImpl implements GroupResource {

	@Override
	public Group getById(Session session, long groupId) {
		return new GroupController(session).getById(groupId);
	}

	@Override
	public List<User> getAvailableMembers(Session session, long groupId) {
		return new GroupController(session).getAvailableMember(groupId);
	}

	@Override
	public List<User> getMembers(Session session, long groupId) {
		return new GroupController(session).getMembers(groupId);
	}
	
	@Override
	public List<Group> getByType(Session session, String type) {
		return new GroupController(session).getByType(type);
	}

	@Override
	public List<Group> getAll(Session session) {
		return new GroupController(session).getAll();
	}

	@Override
	public List<Group> search(Session session, String search) {
		return new GroupController(session).search(search);
	}

	@Override
	public OssResponse add(Session session, Group group) {
		return new GroupController(session).add(group);
	}

	@Override
	public OssResponse modify(Session session, Group group) {
		return new GroupController(session).modify(group);
	}

	@Override
	public OssResponse delete(Session session, long groupId) {
		return new GroupController(session).delete(groupId);
	}

	@Override
	public OssResponse setMembers(Session session, long groupId, List<Long> users) {
		return new GroupController(session).setMembers(groupId,users);
	}

	@Override
	public OssResponse removeMember(Session session, long groupId, long userId) {
		return new GroupController(session).removeMember(groupId,userId);	
	}

	@Override
	public OssResponse addMember(Session session, long groupId, long userId) {
		return new GroupController(session).addMember(groupId,userId);
	}

	@Override
	public List<Group> getGroups(Session session, List<Long> groupIds) {
		return new GroupController(session).getGroups(groupIds);
	}

	@Override
	public String getMembersText(Session session, String groupName) {
		List<String> member = new ArrayList<String>();
		Group group = new GroupController(session).getByName(groupName);
		for(User user : group.getUsers() ) {
			member.add(user.getUid());
		}
		return String.join("\n",member);
	}

	@Override
	public String getByTypeText(Session session, String type) {
		List<String> groups = new ArrayList<String>();
		for( Group group : new GroupController(session).getByType(type)) {
			groups.add(group.getName());
		}
		return String.join("\n",groups);
	}

	@Override
	public String delete(Session session, String groupName) {
		return new GroupController(session).delete(groupName).getCode();
	}

	@Override
	public OssResponse cleanUpDirectory(Session session, long groupId) {
		GroupController gc = new GroupController(session);
		Group group = gc.getById(groupId);
		return gc.cleanGrupDirectory(group);
		
	}

}
