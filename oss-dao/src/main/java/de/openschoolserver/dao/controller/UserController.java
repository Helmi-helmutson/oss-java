/* (c) 2017 Péter Varkoly <peter@varkoly.de> - all rights reserved */
package de.openschoolserver.dao.controller;

import java.util.ArrayList;


import java.util.Date;
import java.util.List;
import java.lang.Integer;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import de.extis.core.util.UserUtil;

import de.openschoolserver.dao.Device;
import de.openschoolserver.dao.User;
import de.openschoolserver.dao.Group;
import de.openschoolserver.dao.Session;
import de.openschoolserver.dao.Response;


public class UserController extends Controller {
	
	public UserController(Session session) {
		super(session);
	}
	
	public User getById(long userId) {
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

	public Response add(User user){
		EntityManager em = getEntityManager();
		// Create uid if not given
		if( user.getUid() == "") {
			String userId = UserUtil.createUserId( user.getGivenName(),
												   user.getSureName(),
												   user.getBirthDay(),
												   true,
												   this.getConfigValue("SCHOOL_STRING_CONVERT_TYPE") == "telex", 
												   this.getConfigValue("SCHOOL_LOGIN_SCHEME")
												   );
			user.setUid( this.getConfigValue("SCHOOL_LOGIN_PREFIX") + userId );
			Integer i = 1;
			while( !this.isNameUnique(user.getUid()) ) {
				user.setUid( this.getConfigValue("SCHOOL_LOGIN_PREFIX") + userId + i );
			}
		}
		else
		{
		// First we check if the parameter are unique.
				if( ! this.isNameUnique(user.getUid())){
					return new Response(this.getSession(),"ERROR", "User name is not unique.");
				}
				// Check if uid contains non allowed characters
				if( this.checkNonASCII(user.getUid()) ) {
					return new Response(this.getSession(),"ERROR", "Uid contains not allowed characters.");
				}
		}
		// Check the user password
		if( user.getPassword() == "" ) {
			user.setPassword(UserUtil.createRandomPassword(8));
		}
		else
		{
			if( user.getPassword().length() < Integer.parseInt(this.getConfigValue("SCHOOL_MINIMAL_PASSWORD_LENGTH")) ) {
				return new Response(this.getSession(),"ERROR", "User password is to short.");
			}
			if( user.getPassword().length() > Integer.parseInt(this.getConfigValue("SCHOOL_MAXIMAL_PASSWORD_LENGTH")) ) {
				return new Response(this.getSession(),"ERROR", "User password is to long.");
			}
			if(  this.getConfigValue("SCHOOL_CHECK_PASSWORD_QUALITY") == "yes" ) {
				//return new Response(this.getSession(),"ERROR", "User password is to simple.");
			}
		}
		try {
			em.getTransaction().begin();
			em.persist(user);
			em.getTransaction().commit();
		} catch (Exception e) {
			System.err.println(e.getMessage());
			return new Response(this.getSession(),"ERROR", e.getMessage());
		} finally {
			em.close();
		}
		this.startPlugin("add_user",user);
		return new Response(this.getSession(),"OK", user.getUid() + " (" + user.getGivenName() + " " + user.getSureName() + ") was created.");
	}

	public List<Response> add(List<User> users){
		List<Response> results = new ArrayList<Response>();
		for( User user : users ) {
			results.add(this.add(user));
		}
		return results;
	}
	
	public boolean modify(User user){
		//TODO make some checks!!
		EntityManager em = getEntityManager();

		// First we have to check some parameter
		try {
			em.getTransaction().begin();
			em.merge(user);
			em.getTransaction().commit();
		} catch (Exception e) {
			System.err.println(e.getMessage());
			return false;
		} finally {
			em.close();
		}
		this.startPlugin("modify_user",user);
		return true;
	}
	
	public boolean delete(long userId){
		
		EntityManager em = getEntityManager();
		em.getTransaction().begin();
		User user = this.getById(userId);

		this.startPlugin("delete_user",user);

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
		em.close();
		//TODO find and remove files
		return true;
	}
	
	public List<Group> getAvailableGroups(long userId){
		EntityManager em = getEntityManager();
		User user = this.getById(userId);
		Query query = em.createNamedQuery("Group.findAll");
		List<Group> allGroups = query.getResultList();
		allGroups.removeAll(user.getGroups());
		em.close();
		return allGroups;
	}

	public List<Group> getGroups(long userId) {
		User user = this.getById(userId);
		return user.getGroups();
	}
}
