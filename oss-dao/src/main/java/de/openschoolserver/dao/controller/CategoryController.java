/* (c) 2017 Péter Varkoly <peter@varkoly.de> - all rights reserved  */
package de.openschoolserver.dao.controller;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;
import java.util.ArrayList;
import de.openschoolserver.dao.*;

import com.fasterxml.jackson.databind.ObjectMapper;

@SuppressWarnings( "unchecked" )
public class CategoryController extends Controller {

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

	public List<Category> getByType(String search) {
		EntityManager em = getEntityManager();
		try {
			Query query = em.createNamedQuery("Category.getByType").setParameter("type", search);
			return query.getResultList();
		} catch (Exception e) {
			logger.error(e.getMessage());
			return new ArrayList<>();
		} finally {
			em.close();
		}
	}

	public Category getByName(String name) {
		EntityManager em = getEntityManager();
		try {
			Query query = em.createNamedQuery("Category.getByName").setParameter("name", name);
			return (Category) query.getSingleResult();
		} catch (Exception e) {
			logger.debug(e.getMessage());
			return null;
		} finally {
			em.close();
		}
	}

	public OssResponse add(Category category){
		EntityManager em = getEntityManager();

		try {
			// First we check if the parameter are unique.
			Query query = em.createNamedQuery("Category.getByName").setParameter("name",category.getName());
			if( !query.getResultList().isEmpty() ){
				return new OssResponse(this.getSession(),"ERROR","Category name is not unique.");
			}
			if( !category.getDescription().isEmpty() ) {
				query = em.createNamedQuery("Category.getByDescription").setParameter("description",category.getDescription());
				if( !query.getResultList().isEmpty() ){
					return new OssResponse(this.getSession(),"ERROR","Category description is not unique.");
				}
			}
			category.setOwner(this.session.getUser());
			em.getTransaction().begin();
			em.persist(category);
			em.getTransaction().commit();
			logger.debug("Created Category:" + new ObjectMapper().writeValueAsString(category));
		} catch (Exception e) {
			logger.error(e.getMessage());
			return new OssResponse(this.getSession(),"ERROR",e.getMessage());
		}
		return new OssResponse(this.getSession(),"OK","Category was created",category.getId());
	}

	public OssResponse modify(Category category){
		EntityManager em = getEntityManager();
		try {
			em.getTransaction().begin();
			em.merge(category);
			em.getTransaction().commit();
		} catch (Exception e) {
			logger.error(e.getMessage());
			return new OssResponse(this.getSession(),"ERROR",e.getMessage());
		}
		return new OssResponse(this.getSession(),"OK","Category was modified");
	}

	public OssResponse delete(Long categoryId){
		Category category = this.getById(categoryId);
		if( this.isProtected(category)) {
			return new OssResponse(this.getSession(),"ERROR","This category must not be deleted.");
		}
		// Remove group from GroupMember of table
		EntityManager em = getEntityManager();
		try {
			em.getTransaction().begin();
			if( !em.contains(category)) {
				category = em.merge(category);
			}
			em.remove(category);
			em.getTransaction().commit();
			em.getEntityManagerFactory().getCache().evictAll();
		} catch (Exception e) {
			logger.error(e.getMessage());
			return new OssResponse(this.getSession(),"ERROR",e.getMessage());
		} finally {

			em.close();
		}
		return new OssResponse(this.getSession(),"OK","Category was deleted");
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
		case("FAQ"):
			for(FAQ f: c.getFaqs() ) {
				objectIds.remove(f.getId());
			}
		break;
		case("Announcement"):
			for(Announcement a: c.getAnnouncements() ) {
				objectIds.remove(a.getId());
			}
		break;
		case("Contact"):
			for(Contact cont: c.getContacts()) {
				objectIds.remove(cont.getId());
			}
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
		case("FAQ"):
			for(FAQ f: c.getFaqs() ) {
				objectIds.add(f.getId());
			}
		break;
		case("Announcement"):
			for(Announcement a: c.getAnnouncements() ) {
				objectIds.add(a.getId());
			}
		break;
		case("Contact"):
			for(Contact cont: c.getContacts()) {
				objectIds.add(cont.getId());
			}
		}
		return objectIds;
	}

	public OssResponse addMember(Long categoryId, String objectName,Long objectId ) {
		EntityManager em = getEntityManager();
		Category category = this.getById(categoryId);
		try {
			em.getTransaction().begin();
			switch(objectName){
			case("Device"):
				Device device = new DeviceController(this.session).getById(objectId);
				category.getDevices().add(device);
				device.getCategories().add(category);
			break;
			case("Group"):
				Group group = new GroupController(this.session).getById(objectId);
				category.getGroups().add(group);
				group.getCategories().add(category);
			break;
			case("HWConf"):
				HWConf hwconf = new CloneToolController(this.session).getById(objectId);
				category.getHWConfs().add(hwconf);
				hwconf.getCategories().add(category);
			break;
			case("Room"):
				Room room = new RoomController(this.session).getById(objectId);
				category.getRooms().add(room);
				room.getCategories().add(category);
			break;
			case("Software"):
				Software software = new SoftwareController(this.session).getById(objectId);
				category.getSoftwares().add(software);
				software.getCategories().add(category);
			break;
			case("User"):
				User user = new UserController(this.session).getById(objectId);
				category.getUsers().add(user);
				user.getCategories().add(category);
			break;
			case("FAQ"):
				FAQ faq = new InformationController(this.session).getFAQById(objectId);
				category.getFaqs().add(faq);
				faq.getCategories().add(category);
			break;
			case("Announcement"):
				Announcement info = new InformationController(this.session).getAnnouncementById(objectId);
				category.getAnnouncements().add(info);
				info.getCategories().add(category);
			break;
			case("Contact"):
				Contact contact = new InformationController(this.session).getContactById(objectId);
				category.getContacts().add(contact);
				contact.getCategories().add(category);
			break;
			}
			em.getTransaction().commit();
		} catch (Exception e) {
			logger.error(e.getMessage());
			return new OssResponse(this.getSession(),"ERROR",e.getMessage());
		} finally {
			em.close();
		}
		return new OssResponse(this.getSession(),"OK","Category was modified");
	}

	public OssResponse deleteMember(Long categoryId, String objectName, Long objectId ) {
		Category category = this.getById(categoryId);
		EntityManager em = getEntityManager();
		try {
			em.getTransaction().begin();
			switch(objectName){
			case("Device"):
				Device device = new DeviceController(this.session).getById(objectId);
				category.getDevices().remove(device);
				device.getCategories().remove(category);
			break;
			case("Group"):
				Group group = new GroupController(this.session).getById(objectId);
				category.getGroups().remove(group);
				group.getCategories().remove(category);
			break;
			case("HWConf"):
				HWConf hwconf = new CloneToolController(this.session).getById(objectId);
				category.getHWConfs().remove(hwconf);
				hwconf.getCategories().remove(category);
			break;
			case("Room"):
				Room room = new RoomController(this.session).getById(objectId);
				category.getRooms().remove(room);
				room.getCategories().remove(category);
			break;
			case("Software"):
				Software software = new SoftwareController(this.session).getById(objectId);
				category.getSoftwares().remove(software);
				category.getRemovedSoftwares().add(software);
				software.getCategories().remove(category);
				software.getRemovedFromCategories().add(category);
			break;
			case("User"):
				User user = new UserController(this.session).getById(objectId);
				category.getUsers().remove(user);
				user.getCategories().remove(category);
			break;
			case("FAQ"):
				FAQ faq = new InformationController(this.session).getFAQById(objectId);
				category.getFaqs().remove(faq);
				faq.getCategories().remove(category);
			break;
			case("Announcement"):
				Announcement info = new InformationController(this.session).getAnnouncementById(objectId);
				category.getAnnouncements().remove(info);
				info.getCategories().remove(category);
			break;
			case("Contact"):
				Contact contact = new InformationController(this.session).getContactById(objectId);
				category.getContacts().remove(contact);
				contact.getCategories().remove(category);
			break;
			}
			em.getTransaction().commit();
		} catch (Exception e) {
			logger.error(e.getMessage());
			return new OssResponse(this.getSession(),"ERROR",e.getMessage());
		} finally {
			em.close();
		}
		return new OssResponse(this.getSession(),"OK","Category was modified");
	}

	public List<Category> getCategories(List<Long> categoryIds) {
		List<Category> categories = new ArrayList<Category>();
		for( Long id : categoryIds ) {
			categories.add(this.getById(id));
		}
		return categories;
	}
}
