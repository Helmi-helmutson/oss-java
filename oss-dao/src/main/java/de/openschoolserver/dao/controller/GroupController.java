/* (c) 2017 P��ter Varkoly <peter@varkoly.de> - all rights reserved */
package de.openschoolserver.dao.controller;

import java.util.ArrayList;


import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import de.openschoolserver.dao.Device;
import de.openschoolserver.dao.User;
import de.openschoolserver.dao.Group;
import de.openschoolserver.dao.Room;
import de.openschoolserver.dao.Session;
import de.openschoolserver.dao.Response;
import de.openschoolserver.dao.tools.*;

public class GroupController extends Controller {

	public GroupController(Session session) {
		super(session);
	}

	public Group getById(long groupId) {
		EntityManager em = getEntityManager();	
		try {
			return em.find(Group.class, groupId);
		} catch (Exception e) {
			// logger.error(e.getMessage());
			System.err.println(e.getMessage()); //TODO
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
			//logger.error(e.getMessage());
			System.err.println(e.getMessage()); //TODO
			return new ArrayList<>();
		} finally {
			em.close();
		}
	}

	public List<Group> search(String search) {
		EntityManager em = getEntityManager();
		try {
			Query query = em.createNamedQuery("Group.search");
			query.setParameter("search", search);
			return query.getResultList();
		} catch (Exception e) {
			//logger.error(e.getMessage());
			System.err.println(e.getMessage()); //TODO
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
			//logger.error(e.getMessage());
			System.err.println(e.getMessage()); //TODO
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
			System.err.println(e.getMessage());
			return new Response(this.getSession(),"ERROR",e.getMessage());
		}
		this.startPlugin("add_group", group);
		return new Response(this.getSession(),"OK","Group was created");
	}

	public Response modify(Group group){
		//TODO make some checks!!
		EntityManager em = getEntityManager();
		try {
			em.getTransaction().begin();
			em.merge(group);
			em.getTransaction().commit();
		} catch (Exception e) {
			System.err.println(e.getMessage());
			return new Response(this.getSession(),"ERROR",e.getMessage());
		}
		this.startPlugin("modify_group", group);
		return new Response(this.getSession(),"OK","Group was modified");
	}

	public Response delete(long groupId){
		Group group = this.getById(groupId);
		this.startPlugin("delete_group", group);

		// Remove group from GroupMember of table
		EntityManager em = getEntityManager();
		try {
			em.getTransaction().begin();
			Query query = em.createQuery("DELETE FROM GroupMember WHERE group_id = :groupId");
			query.setParameter("groupId", groupId);
			query.executeUpdate();
			// Let's remove the group
			em.remove(group);
			em.getTransaction().commit();
		} catch (Exception e) {
			System.err.println(e.getMessage());
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

	public Response addMember(long groupId, long userId) {
		EntityManager em = getEntityManager();
		Group group = em.find(Group.class, groupId);
		User  user  = em.find(User.class, userId);
		group.getUsers().add(user);
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
}
