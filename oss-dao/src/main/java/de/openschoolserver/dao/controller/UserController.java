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
import de.openschoolserver.dao.tools.*;

public class UserController extends Controller {
	
	public UserController(Session session) {
		super(session);
	}
	
	public User getById(int userId) {
		EntityManager em = getEntityManager();	
		try {
			return em.find(User.class, userId);
		} catch (Exception e) {
			// logger.error(e.getMessage());
			System.err.println(e.getMessage()); //TODO
			return null;
		} finally {
			em.close();
		}
	}
	
	public List<User> getByRole(String role) {
		EntityManager em = getEntityManager();
		try {
			Query query = em.createNamedQuery("User.getByRole");
			query.setParameter("role", role);
			return query.getResultList();
		} catch (Exception e) {
			//logger.error(e.getMessage());
			System.err.println(e.getMessage()); //TODO
			return new ArrayList<>();
		} finally {
			em.close();
		}
	}

	public List<User> search(String search) {
		EntityManager em = getEntityManager();
		try {
			Query query = em.createNamedQuery("User.search");
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

	public List<User> getAll() {
		EntityManager em = getEntityManager();
		try {
			Query query = em.createNamedQuery("User.findAll"); 
			return query.getResultList();
		} catch (Exception e) {
			//logger.error(e.getMessage());
			System.err.println(e.getMessage()); //TODO
			return new ArrayList<>();
		} finally {
			em.close();
		}
	}

	public boolean add(User user){
		EntityManager em = getEntityManager();
		// First we check if the parameter are unique.
		if( ! this.isNameUnique(user.getUid())){
			return false;
		}
		try {
			em.getTransaction().begin();
			em.persist(user);
			em.getTransaction().commit();
		} catch (Exception e) {
			System.err.println(e.getMessage());
			return false;
		}
		return true;
	}

	public boolean modify(User user){
		//TODO make some checks!!
		EntityManager em = getEntityManager();
		// First we check if the parameter are unique.
		if( ! this.isNameUnique(user.getUid())){
			return false;
		}
		try {
			em.getTransaction().begin();
			em.merge(user);
			em.getTransaction().commit();
		} catch (Exception e) {
			System.err.println(e.getMessage());
			return false;
		}
		return true;
	}
	
	public boolean delete(int userId){
		EntityManager em = getEntityManager();
		em.getTransaction().begin();
		User user = this.getById(userId);
		List<Device> devices = user.getOwnedDevices();
		// Remove user from logged on table
		Query query = em.createQuery("DELETE FROM LoggedOn WHERE user_id = :userId");
		query.setParameter("userId", userId);
		query.executeUpdate();
		// Remove user from GroupMember of table
		query = em.createQuery("DELETE FROM GroupMember WHERE user_id = :userId");
		query.setParameter("userId", userId);
		query.executeUpdate();
		// Let's remove the user
		em.remove(user);
		em.getTransaction().commit();
		if( !devices.isEmpty() ) {
			//TODO restart dhcp and dns
			
		}
		//TODO find and remove files
		return true;
	}


}
