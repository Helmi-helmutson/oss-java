/* (c) 2017 Péter Varkoly <peter@varkoly.de> - all rights reserved */
package de.openschoolserver.api.resourceimpl;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

import de.openschoolserver.api.resources.GroupResource;
import de.openschoolserver.dao.Group;
import de.openschoolserver.dao.User;
import de.openschoolserver.dao.controller.GroupController;
import de.openschoolserver.dao.internal.CommonEntityManagerFactory;
import de.openschoolserver.dao.Session;
import de.openschoolserver.dao.OssResponse;

public class GroupResourceImpl implements GroupResource {

	private EntityManager em;

	public GroupResourceImpl() {
		super();
		this.em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
	}

	protected void finalize()
	{
	   this.em.close();
	}

	@Override
	public Group getById(Session session, long groupId) {
		return new GroupController(session,this.em).getById(groupId);
	}

	@Override
	public List<User> getAvailableMembers(Session session, long groupId) {
		return new GroupController(session,this.em).getAvailableMember(groupId);
	}

	@Override
	public List<User> getMembers(Session session, long groupId) {
		return new GroupController(session,this.em).getMembers(groupId);
	}

	@Override
	public List<Group> getByType(Session session, String type) {
		return new GroupController(session,this.em).getByType(type);
	}

	@Override
	public List<Group> getAll(Session session) {
		return new GroupController(session,this.em).getAll();
	}

	@Override
	public List<Group> search(Session session, String search) {
		return new GroupController(session,this.em).search(search);
	}

	@Override
	public OssResponse add(Session session, Group group) {
		return new GroupController(session,this.em).add(group);
	}

	@Override
	public OssResponse modify(Session session, Group group) {
		return new GroupController(session,this.em).modify(group);
	}

	@Override
	public OssResponse delete(Session session, long groupId) {
		return new GroupController(session,this.em).delete(groupId);
	}

	@Override
	public OssResponse setMembers(Session session, long groupId, List<Long> users) {
		return new GroupController(session,this.em).setMembers(groupId,users);
	}

	@Override
	public OssResponse removeMember(Session session, long groupId, long userId) {
		return new GroupController(session,this.em).removeMember(groupId,userId);
	}

	@Override
	public OssResponse addMember(Session session, long groupId, long userId) {
		return new GroupController(session,this.em).addMember(groupId,userId);
	}

	@Override
	public List<Group> getGroups(Session session, List<Long> groupIds) {
		return new GroupController(session,this.em).getGroups(groupIds);
	}

	@Override
	public String getMembersText(Session session, String groupName) {
		List<String> member = new ArrayList<String>();
		final GroupController gc = new GroupController(session,this.em);
		Group group = gc.getByName(groupName);
		for(User user : group.getUsers() ) {
			member.add(user.getUid());
		}
		return String.join(gc.getNl(),member);
	}

	@Override
	public String getByTypeText(Session session, String type) {
		List<String> groups = new ArrayList<String>();
		final GroupController gc = new GroupController(session,this.em);
		for( Group group : gc.getByType(type)) {
			groups.add(group.getName());
		}
		return String.join(gc.getNl(),groups);
	}

	@Override
	public String delete(Session session, String groupName) {
		return new GroupController(session,this.em).delete(groupName).getCode();
	}

	@Override
	public OssResponse cleanUpDirectory(Session session, long groupId) {
		GroupController gc = new GroupController(session,this.em);
		Group group = gc.getById(groupId);
		return gc.cleanGrupDirectory(group);
	}

	@Override
	public OssResponse importGroups(Session session, InputStream fileInputStream,
			FormDataContentDisposition contentDispositionHeader) {
		return new GroupController(session,this.em).importGroups(fileInputStream, contentDispositionHeader);
	}
}
