package de.openschoolserver.dao.controller;

import javax.persistence.EntityManager;

import javax.persistence.Query;

import java.util.List;
import java.util.ArrayList;

import de.openschoolserver.dao.*;

@SuppressWarnings( "unchecked" )
public class CategoryController extends Controller {
	
	static String[] categoriesIn = { 
			"DeviceInCategories",
			"GroupInCategories",
			"HWConfInCategories",
			"RoomInCategories",
			"SoftwareInCategories",
			"SoftwareRemovedFromCategories",
			"UserInCategories"
			};

	public CategoryController(Session session) {
		super(session);
	}
	
	public List<Category> getAll() {
		EntityManager em = getEntityManager();
		try {
			Query query = em.createNamedQuery("Category.findAll"); 
			return query.getResultList();
		} catch (Exception e) {
			logger.error(e.getMessage());
			return null;
		} finally {
			em.close();
		}
	}
	
	public Category getById(long categoryId) {
		EntityManager em = getEntityManager();
		try {
			return em.find(Category.class, categoryId);
		} catch (Exception e) {
			logger.error(e.getMessage());
			return null;
		} finally {
			em.close();
		}
	}
	
	public List<Category> search(String search) {
		EntityManager em = getEntityManager();
			try {
				Query query = em.createNamedQuery("Category.search");
				query.setParameter("search", search + "%");
				return query.getResultList();
			} catch (Exception e) {
				logger.error(e.getMessage());
				return new ArrayList<>();
			} finally {
				em.close();
			}
		}

	public Response add(Category category){
		EntityManager em = getEntityManager();
		
		try {
			// First we check if the parameter are unique.
			Query query = em.createNamedQuery("Category.getByName").setParameter("name",category.getName());
			if( !query.getResultList().isEmpty() ){
				return new Response(this.getSession(),"ERROR","Category name is not unique.");
			}
			if( !category.getDescription().isEmpty() ) {
				query = em.createNamedQuery("Category.getByDescription").setParameter("description",category.getDescription());
				if( !query.getResultList().isEmpty() ){
					return new Response(this.getSession(),"ERROR","Category description is not unique.");
				}
			}
			em.getTransaction().begin();
			em.persist(category);
			em.getTransaction().commit();
		} catch (Exception e) {
			logger.error(e.getMessage());
			return new Response(this.getSession(),"ERROR",e.getMessage());
		}
		return new Response(this.getSession(),"OK","Category was created");
	}
	
	public Response modify(Category category){
		EntityManager em = getEntityManager();
		try {
			em.getTransaction().begin();
			em.merge(category);
			em.getTransaction().commit();
		} catch (Exception e) {
			logger.error(e.getMessage());
			return new Response(this.getSession(),"ERROR",e.getMessage());
		}
		return new Response(this.getSession(),"OK","Category was modified");
	}
	
	public Response delete(Long categoryId){
		Category category = this.getById(categoryId);
		if( this.isProtected(category)) {
			return new Response(this.getSession(),"ERROR","This category must not be deleted.");
		}
		// Remove group from GroupMember of table
		EntityManager em = getEntityManager();
		try {
			em.getTransaction().begin();
			for( String ci : categoriesIn ) {
				Query query = em.createQuery("DELETE FROM "+ci+" WHERE category_id = :groupId");
				query.setParameter("categoryId", category.getId());
				query.executeUpdate();
				// Let's remove the group
			}
			if( !em.contains(category)) {
				category = em.merge(category);
			}
			em.remove(category);
			em.getTransaction().commit();
		} catch (Exception e) {
			logger.error(e.getMessage());
			return new Response(this.getSession(),"ERROR",e.getMessage());
		} finally {
			em.close();
		}
		return new Response(this.getSession(),"OK","Category was deleted");
	}
	
	public List<Long> getAvailableMembers(Long categoryId, String objectName ) {
		Category c = this.getById(categoryId);
		List<Long> objectIds = new ArrayList<Long>();
		EntityManager em = getEntityManager();
		Query query = em.createNamedQuery(objectName + ".findAllId");
		for(Long l : (List<Long>) query.getResultList() ) {
			objectIds.add(l);
		}
		switch(objectName){
			case("Device"):
				for(Device d : c.getDevices()) {
					objectIds.remove(d.getId());
				}
			break;
			case("Group"):
				for(Group g : c.getGroups()) {
					objectIds.remove(g.getId());
				}
			break;
			case("HWConf"):
				for(HWConf h : c.getHWConfs()) {
					objectIds.remove(h.getId());
				}
			break;
			case("Room"):
				for(Room r: c.getRooms()) {
					objectIds.remove(r.getId());
				}
			break;
			case("Software"):
				for(Software s: c.getSoftwares()) {
					objectIds.remove(s.getId());
				}
			break;
			case("User"):
				for(User u: c.getUsers()) {
					objectIds.remove(u.getId());
				}
			break;
		}
		return objectIds;
	}
	
	public List<Long> getMembers(Long categoryId, String objectName ) {
		Category c = this.getById(categoryId);
		List<Long> objectIds = new ArrayList<Long>();
		switch(objectName){
		case("Device"):
			for(Device d : c.getDevices()) {
				objectIds.add(d.getId());
			}
		break;
		case("Group"):
			for(Group g : c.getGroups()) {
				objectIds.add(g.getId());
			}
		break;
		case("HWConf"):
			for(HWConf h : c.getHWConfs()) {
				objectIds.add(h.getId());
			}
		break;
		case("Room"):
			for(Room r: c.getRooms()) {
				objectIds.add(r.getId());
			}
		break;
		case("Software"):
			for(Software s: c.getSoftwares()) {
				objectIds.add(s.getId());
			}
		break;
		case("User"):
			for(User u: c.getUsers()) {
				objectIds.add(u.getId());
			}
		break;
		}
		return objectIds;
	}

	public Response addMember(Long categoryId, String objectName,Long objectId ) {
		String table = objectName + "InCategory";
		EntityManager em = getEntityManager();
		try {
			em.getTransaction().begin();
			Query query = em.createQuery("INSERT INTO "+table+" VALUES("+objectId +","+categoryId+ ")");
			query.executeUpdate();		
			em.getTransaction().commit();
		} catch (Exception e) {
			logger.error(e.getMessage());
			return new Response(this.getSession(),"ERROR",e.getMessage());
		} finally {
			em.close();
		}
		return new Response(this.getSession(),"OK","Category was modified");
	}
	
	public Response removeMember(Long categoryId, String objectName, Long objectId ) {
		String table = objectName + "InCategory";
		String idName = objectName.toLowerCase()+"_id";
		EntityManager em = getEntityManager();
		try {
			em.getTransaction().begin();
			Query query = em.createQuery("DELETE FROM "+table+" WHERE " + idName + " = " + objectId + " AND category_id = " + categoryId);
			query.executeUpdate();
			em.getTransaction().commit();
			if( objectName.equals("Software") ) {
				em.getTransaction().begin();
				query = em.createQuery("INSER INTO SoftwareRemovedFromCategories Values(" + categoryId + "," + objectId + ")");
				query.executeUpdate();
				em.getTransaction().commit();
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			return new Response(this.getSession(),"ERROR",e.getMessage());
		} finally {
			em.close();
		}
		return new Response(this.getSession(),"OK","Category was modified");
	}
}
