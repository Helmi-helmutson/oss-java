/* (c) 2017 Péter Varkoly <peter@varkoly.de> - all rights reserved 
 * (c) 2017 EXTIS GmbH www.extis.de - all rights reserved */
package de.openschoolserver.dao.controller;

import java.util.ArrayList;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import de.openschoolserver.dao.User;
import de.openschoolserver.dao.Group;
import de.openschoolserver.dao.Session;
import de.openschoolserver.dao.Response;

@SuppressWarnings( "unchecked" )
public class GroupController extends Controller {

	Logger logger = LoggerFactory.getLogger(GroupController.class);

	public GroupController(Session session) {
		super(session);
	}

	public Group getById(long groupId) {
		EntityManager em = getEntityManager();	
		try {
			return em.find(Group.class, groupId);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			return null;
		} finally {
			em.close();
		}
	}

	public List<Group> getByType(String groupType) {
		EntityManager em = getEntityManager();
		try {
			Query query = em.createNamedQuery("Group.getByType");
			query.setParameter("groupType", groupType);
			return query.getResultList();
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			return new ArrayList<>();
		} finally {
			em.close();
		}
	}

	public Group getByName(String name) {
		EntityManager em = getEntityManager();
		try {
			Query query = em.createNamedQuery("Group.getByName");
			query.setParameter("name", name);
			List<Group> result = query.getResultList();
			if (result!=null && result.size()>0) {
			return result .get(0);
			} else {
				return null;
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			return null;
		} finally {
			em.close();
		}
	}
	
	public List<Group> search(String search) {
		EntityManager em = getEntityManager();
		try {
			Query query = em.createNamedQuery("Group.search");
			query.setParameter("search", search + "%");
			return query.getResultList();
		} catch (Exception e) {
			logger.error(e.getMessage());
			return new ArrayList<>();
		} finally {
			em.close();
		}
	}

	public List<Group> getAll() {
		EntityManager em = getEntityManager();
		try {
			Query query = em.createNamedQuery("Group.findAll"); 
			return query.getResultList();
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			return new ArrayList<>();
		} finally {
			em.close();
		}
	}

	public Response add(Group group){
		EntityManager em = getEntityManager();
		// First we check if the parameter are unique.
		if( ! this.isNameUnique(group.getName())){
			return new Response(this.getSession(),"ERROR","Group name is not unique.");
		}
		try {
			em.getTransaction().begin();
			em.persist(group);
			em.getTransaction().commit();
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			return new Response(this.getSession(),"ERROR",e.getMessage());
		}
		this.startPlugin("add_group", group);
		return new Response(this.getSession(),"OK","Group was created",group.getId());
	}

	public Response modify(Group group){
		Group oldGroup = this.getById(group.getId());
		oldGroup.setDescription(group.getDescription());
		EntityManager em = getEntityManager();
		try {
			em.getTransaction().begin();
			em.merge(oldGroup);
			em.getTransaction().commit();
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			return new Response(this.getSession(),"ERROR",e.getMessage());
		}
		this.startPlugin("modify_group", oldGroup);
		return new Response(this.getSession(),"OK","Group was modified");
	}

	public Response delete(long groupId){
		Group group = this.getById(groupId);
		if( this.isProtected(group)) {
			return new Response(this.getSession(),"ERROR","This group must not be deleted.");
		}
		
		this.startPlugin("delete_group", group);

		// Remove group from GroupMember of table
		EntityManager em = getEntityManager();
		try {
			em.getTransaction().begin();
			if( !em.contains(group)) {
				group = em.merge(group);
			}
			em.remove(group);
			em.getTransaction().commit();
			em.getEntityManagerFactory().getCache().evictAll();
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			return new Response(this.getSession(),"ERROR",e.getMessage());
		} finally {
			em.close();
		}
		return new Response(this.getSession(),"OK","Group was deleted");
	}

	public List<User> getAvailableMember(long groupId){
		EntityManager em = getEntityManager();
		Group group = this.getById(groupId);
		Query query = em.createNamedQuery("User.findAll");
		List<User> allUsers = query.getResultList();
		allUsers.removeAll(group.getUsers());
		return allUsers;
	}

	public List<User> getMembers(long groupId) {
		Group group = this.getById(groupId);
		return group.getUsers();
	}

	public Response setMembers(long groupId, List<Long> userIds) {
		EntityManager em = getEntityManager();
		List<User> usersToRemove = new ArrayList<User>();
		List<User> usersToAdd    = new ArrayList<User>();
		List<User> users = new ArrayList<User>();
		for( Long userId : userIds ) {
			users.add(em.find(User.class, userId));
		}
		Group group = this.getById(groupId);
		for( User user : users ){
			if(! group.getUsers().contains(user)){
				usersToAdd.add(user);
			}
		}
		for( User user : group.getUsers() ) {
			if(! users.contains(user)) {
				usersToRemove.add(user);
			}
		}
		try {
			em.getTransaction().begin();
			for( User user : usersToAdd) {
				group.getUsers().add(user);
				user.getGroups().add(group);
				em.merge(user);
			}
			for( User user : usersToRemove ) {
				group.getUsers().remove(user);
				user.getGroups().remove(group);
				em.merge(user);
			}
			em.merge(group);
			em.getTransaction().commit();
		}catch (Exception e) {
			return new Response(this.getSession(),"ERROR",e.getMessage());
		} finally {
			em.close();
		}
		this.changeMemberPlugin("addmembers", group, usersToAdd);
		this.changeMemberPlugin("removemembers", group, usersToRemove);
		return new Response(this.getSession(),"OK","The members of group was set.");
	}


	public Response addMember(Group group, User user) {
		EntityManager em = getEntityManager();
		group.getUsers().add(user);
		if (user.getGroups()==null) {
			user.setGroups(new ArrayList<Group>());
		}
		user.getGroups().add(group);
		try {
			em.getTransaction().begin();
			em.merge(user);
			em.merge(group);
			em.getTransaction().commit();
		} catch (Exception e) {
			return new Response(this.getSession(),"ERROR",e.getMessage());
		} finally {
			em.close();
		}
		this.changeMemberPlugin("addmembers", group, user);
		return new Response(this.getSession(),"OK","User " + user.getUid() + " was added to group " + group.getName() );
	}

	public Response addMember(long groupId, long userId) {
		EntityManager em = getEntityManager();
		Group group = em.find(Group.class, groupId);
		User  user  = em.find(User.class, userId);
		return this.addMember(group, user);
	}

	public Response removeMember(long groupId, long userId) {
		EntityManager em = getEntityManager();
		Group group = em.find(Group.class, groupId);
		User  user  = em.find(User.class, userId);
		group.getUsers().remove(user);
		user.getGroups().remove(group);
		try {
			em.getTransaction().begin();
			em.merge(user);
			em.merge(group);
			em.getTransaction().commit();
		} catch (Exception e) {
			return new Response(this.getSession(),"ERROR",e.getMessage());
		} finally {
			em.close();
		}
		this.changeMemberPlugin("removemembers", group, user);
		return new Response(this.getSession(),"OK","User " + user.getUid() + " was removed from group " + group.getName() );
	}

	public List<Group> getGroups(List<Long> groupIds) {
 		List<Group> groups = new ArrayList<Group>();
 		for( Long id : groupIds){
 			groups.add(this.getById(id));
 		}
		return groups;
	}
}
