/* (c) 2017 Péter Varkoly <peter@varkoly.de> - all rights reserved */
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
		return new Response(this.getSession(),"OK","Group was modified");
	}
	
	public Response delete(long groupId){
		EntityManager em = getEntityManager();
		em.getTransaction().begin();
		Group group = this.getById(groupId);
		String groupName = group.getName();

		// Remove group from GroupMember of table
		Query query = em.createQuery("DELETE FROM GroupMember WHERE group_id = :groupId");
		query.setParameter("groupId", groupId);
		query.executeUpdate();
		// Let's remove the group
		em.remove(group);
		em.getTransaction().commit();
		
		//TODO find and remove files
		//TODO remove group from AD
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
}
