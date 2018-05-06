/* (c) 2017 Péter Varkoly <peter@varkoly.de> - all rights reserved 
 * (c) 2017 EXTIS GmbH www.extis.de - all rights reserved */
package de.openschoolserver.dao.controller;

import java.util.ArrayList;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.ValidatorFactory;

import de.openschoolserver.dao.User;
import de.openschoolserver.dao.Category;
import de.openschoolserver.dao.Group;
import de.openschoolserver.dao.Session;
import de.openschoolserver.dao.OssResponse;

@SuppressWarnings( "unchecked" )
public class GroupController extends Controller {

	Logger logger = LoggerFactory.getLogger(GroupController.class);
	private List<String> parameters;

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

	public OssResponse add(Group group){
		//Check group parameter
		StringBuilder errorMessage = new StringBuilder();
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		for (ConstraintViolation<Group> violation : factory.getValidator().validate(group) ) {
			errorMessage.append(violation.getMessage()).append(getNl());
		}
		if( errorMessage.length() > 0 ) {
			return new OssResponse(this.getSession(),"ERROR", errorMessage.toString());
		}
		// Group names will be converted upper case
		group.setName(group.getName().toUpperCase());
		// First we check if the parameter are unique.
		if( ! this.isNameUnique(group.getName())){
			return new OssResponse(this.getSession(),"ERROR","Group name is not unique.");
		}
		EntityManager em = getEntityManager();
		group.setOwner(this.session.getUser());
		try {
			em.getTransaction().begin();
			em.persist(group);
			em.getTransaction().commit();
			logger.debug("Created Group:" + group);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			return new OssResponse(this.getSession(),"ERROR",e.getMessage());
		} finally {
			em.close();
		}
		this.startPlugin("add_group", group);
		return new OssResponse(this.getSession(),"OK","Group was created.",group.getId());
	}

	public OssResponse modify(Group group){
		Group oldGroup = this.getById(group.getId());
		if( !this.mayModify(oldGroup) ) {
        	return new OssResponse(this.getSession(),"ERROR","You must not modify this group.");
        }
		oldGroup.setDescription(group.getDescription());
		//Check group parameter
		StringBuilder errorMessage = new StringBuilder();
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		for (ConstraintViolation<Group> violation : factory.getValidator().validate(group) ) {
			errorMessage.append(violation.getMessage()).append(getNl());
		}
		if( errorMessage.length() > 0 ) {
				return new OssResponse(this.getSession(),"ERROR", errorMessage.toString());
		}
		EntityManager em = getEntityManager();
		try {
			em.getTransaction().begin();
			em.merge(oldGroup);
			em.getTransaction().commit();
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			return new OssResponse(this.getSession(),"ERROR",e.getMessage());
		}  finally {
			em.close();
		}
		this.startPlugin("modify_group", oldGroup);
		return new OssResponse(this.getSession(),"OK","Group was modified.");
	}

	public OssResponse delete(Group group){
		// Remove group from GroupMember of table
		EntityManager em = getEntityManager();
		group = this.getById(group.getId());
		if( this.isProtected(group)) {
			return new OssResponse(this.getSession(),"ERROR","This group must not be deleted.");
		}
		if( !this.mayModify(group) ) {
        	return new OssResponse(this.getSession(),"ERROR","You must not delete this group.");
        }
		//Primary group must not be deleted if there are member
		if( group.getGroupType().equals("primary")) {
			if( group.getUsers() != null  && !group.getUsers().isEmpty() ) {
				return new OssResponse(this.getSession(),"ERROR","You must not delete this primary group because this still contains member(s).");
			}
		}
		//Start the plugin
		this.startPlugin("delete_group", group);
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
			return new OssResponse(this.getSession(),"ERROR",e.getMessage());
		} finally {
			em.close();
		}
		return new OssResponse(this.getSession(),"OK","Group was deleted.");
	}

	public OssResponse delete(long groupId){
		Group group = this.getById(groupId);
		return delete(group);
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

	public OssResponse setMembers(long groupId, List<Long> userIds) {
		Group group = this.getById(groupId);
		if( !this.mayModify(group) ) {
        	return new OssResponse(this.getSession(),"ERROR","You must not modify this group.");
        }
		EntityManager em = getEntityManager();
		List<User> usersToRemove = new ArrayList<User>();
		List<User> usersToAdd    = new ArrayList<User>();
		List<User> users = new ArrayList<User>();
		for( Long userId : userIds ) {
			users.add(em.find(User.class, userId));
		}
		for( User user : users ){
			if(! group.getUsers().contains(user)){
				usersToAdd.add(user);
			}
		}
		for( User user : group.getUsers() ) {
			if(! user.getRole().equals(group.getName()) ) {
				//User must not be removed from it's primary group.
				continue;
			}
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
			return new OssResponse(this.getSession(),"ERROR",e.getMessage());
		} finally {
			em.close();
		}
		this.changeMemberPlugin("addmembers", group, usersToAdd);
		this.changeMemberPlugin("removemembers", group, usersToRemove);
		return new OssResponse(this.getSession(),"OK","The members of group was set.");
	}


	public OssResponse addMember(Group group, User user) {
		EntityManager em = getEntityManager();
		if( !this.mayModify(group) ) {
        	return new OssResponse(this.getSession(),"ERROR","You must not modify this group.");
        }
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
			return new OssResponse(this.getSession(),"ERROR",e.getMessage());
		} finally {
			em.close();
		}
		this.changeMemberPlugin("addmembers", group, user);
		parameters = new ArrayList<String>();
		parameters.add(user.getUid());
		parameters.add(group.getName());
		return new OssResponse(this.getSession(),"OK","User %s was added to group %s.",null,parameters );
	}

	public OssResponse addMember(long groupId, long userId) {
		EntityManager em = getEntityManager();
		Group group = em.find(Group.class, groupId);
		User  user  = em.find(User.class, userId);
		return this.addMember(group, user);
	}

	public OssResponse removeMember(long groupId, long userId) {
		EntityManager em = getEntityManager();
		Group group = em.find(Group.class, groupId);
		if( !this.mayModify(group) ) {
        	return new OssResponse(this.getSession(),"ERROR","You must not modify this group.");
        }
		User  user  = em.find(User.class, userId);
		if( user.getRole().equals(group.getName()) ) {
			return new OssResponse(this.getSession(),"ERROR","User must not be removed from it's primary group.");
		}
		group.getUsers().remove(user);
		user.getGroups().remove(group);
		try {
			em.getTransaction().begin();
			em.merge(user);
			em.merge(group);
			em.getTransaction().commit();
		} catch (Exception e) {
			return new OssResponse(this.getSession(),"ERROR",e.getMessage());
		} finally {
			em.close();
		}
		this.changeMemberPlugin("removemembers", group, user);
		parameters = new ArrayList<String>();
		parameters.add(user.getUid());
		parameters.add(group.getName());
		return new OssResponse(this.getSession(),"OK","User %s was removed from group %s.",null,parameters );
	}

	public List<Group> getGroups(List<Long> groupIds) {
 		List<Group> groups = new ArrayList<Group>();
 		for( Long id : groupIds){
 			groups.add(this.getById(id));
 		}
		return groups;
	}
}
