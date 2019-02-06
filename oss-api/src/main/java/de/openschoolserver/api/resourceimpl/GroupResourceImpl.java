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

		public GroupResourceImpl() {
	}

	@Override
	public Group getById(Session session, long groupId) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		Group group =  new GroupController(session,em).getById(groupId);
		em.close();
		return group;
	}

	@Override
	public List<User> getAvailableMembers(Session session, long groupId) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		List<User> users = new GroupController(session,em).getAvailableMember(groupId);
		em.close();
		return users;
	}

	@Override
	public List<User> getMembers(Session session, long groupId) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		return new GroupController(session,em).getMembers(groupId);
	}

	@Override
	public List<Group> getByType(Session session, String type) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		return new GroupController(session,em).getByType(type);
	}

	@Override
	public List<Group> getAll(Session session) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		return new GroupController(session,em).getAll();
	}

	@Override
	public List<Group> search(Session session, String search) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		return new GroupController(session,em).search(search);
	}

	@Override
	public OssResponse add(Session session, Group group) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		return new GroupController(session,em).add(group);
	}

	@Override
	public OssResponse modify(Session session, Group group) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		return new GroupController(session,em).modify(group);
	}

	@Override
	public OssResponse delete(Session session, long groupId) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		return new GroupController(session,em).delete(groupId);
	}

	@Override
	public OssResponse setMembers(Session session, long groupId, List<Long> users) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		return new GroupController(session,em).setMembers(groupId,users);
	}

	@Override
	public OssResponse removeMember(Session session, long groupId, long userId) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		return new GroupController(session,em).removeMember(groupId,userId);
	}

	@Override
	public OssResponse addMember(Session session, long groupId, long userId) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		return new GroupController(session,em).addMember(groupId,userId);
	}

	@Override
	public List<Group> getGroups(Session session, List<Long> groupIds) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		return new GroupController(session,em).getGroups(groupIds);
	}

	@Override
	public String getMembersText(Session session, String groupName) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		List<String> member = new ArrayList<String>();
		final GroupController gc = new GroupController(session,em);
		Group group = gc.getByName(groupName);
		for(User user : group.getUsers() ) {
			member.add(user.getUid());
		}
		return String.join(gc.getNl(),member);
	}

	@Override
	public String getByTypeText(Session session, String type) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		List<String> groups = new ArrayList<String>();
		final GroupController gc = new GroupController(session,em);
		for( Group group : gc.getByType(type)) {
			groups.add(group.getName());
		}
		return String.join(gc.getNl(),groups);
	}

	@Override
	public String delete(Session session, String groupName) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		return new GroupController(session,em).delete(groupName).getCode();
	}

	@Override
	public OssResponse cleanUpDirectory(Session session, long groupId) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		GroupController gc = new GroupController(session,em);
		Group group = gc.getById(groupId);
		return gc.cleanGrupDirectory(group);
	}

	@Override
	public OssResponse importGroups(Session session, InputStream fileInputStream,
			FormDataContentDisposition contentDispositionHeader) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		return new GroupController(session,em).importGroups(fileInputStream, contentDispositionHeader);
	}
}
