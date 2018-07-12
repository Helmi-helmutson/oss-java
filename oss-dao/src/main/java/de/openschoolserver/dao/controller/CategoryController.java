/* (c) 2017 Péter Varkoly <peter@varkoly.de> - all rights reserved  */
package de.openschoolserver.dao.controller;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.ValidatorFactory;

import java.util.List;
import java.util.ArrayList;
import de.openschoolserver.dao.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings( "unchecked" )
public class CategoryController extends Controller {

	Logger logger = LoggerFactory.getLogger(CategoryController.class);

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
		List<Category> categories = new ArrayList<Category>();
		try {
			Query query = em.createNamedQuery("Category.getByType").setParameter("type", search);
			for( Category c :  (List<Category>) query.getResultList() ) {
				c.setIds();
				categories.add(c);
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			em.close();
		}
		return categories;
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
		//Check category parameter
		StringBuilder errorMessage = new StringBuilder();
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		for (ConstraintViolation<Category> violation : factory.getValidator().validate(category) ) {
			errorMessage.append(violation.getMessage()).append(getNl());
		}
		if( errorMessage.length() > 0 ) {
			return new OssResponse(this.getSession(),"ERROR", "Validation Error" + errorMessage.toString());
		}
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
			logger.debug("Created Category:" + category );
		} catch (Exception e) {
			logger.error(e.getMessage());
			return new OssResponse(this.getSession(),"ERROR",e.getMessage());
		}
		return new OssResponse(this.getSession(),"OK","Category was created",category.getId());
	}

	public OssResponse modify(Category category){
		//Check category parameter
		StringBuilder errorMessage = new StringBuilder();
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		for (ConstraintViolation<Category> violation : factory.getValidator().validate(category) ) {
			errorMessage.append(violation.getMessage()).append(getNl());
		}
		if( errorMessage.length() > 0 ) {
			return new OssResponse(this.getSession(),"ERROR", errorMessage.toString());
		}
		EntityManager em = getEntityManager();
		Category oldCategory = this.getById(category.getId());
		oldCategory.setDescription(category.getDescription());
		oldCategory.setName(category.getName());
		try {
			em.getTransaction().begin();
			em.merge(oldCategory);
			em.getTransaction().commit();
		} catch (Exception e) {
			logger.error(e.getMessage());
			return new OssResponse(this.getSession(),"ERROR",e.getMessage());
		}
		return new OssResponse(this.getSession(),"OK","Category was modified");
	}

	public OssResponse delete(Long categoryId){
		return this.delete(this.getById(categoryId));
	}

	public OssResponse delete(Category category) {
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
		if( c == null ) {
			return objectIds;
		}
		EntityManager em = getEntityManager();
		Query query = em.createNamedQuery(objectName + ".findAllId");
		for(Long l : (List<Long>) query.getResultList() ) {
			objectIds.add(l);
		}
		switch(objectName.toLowerCase()){
		case("device"):
			for(Device d : c.getDevices()) {
				objectIds.remove(d.getId());
			}
		break;
		case("group"):
			for(Group g : c.getGroups()) {
				objectIds.remove(g.getId());
			}
		break;
		case("hwconf"):
			for(HWConf h : c.getHWConfs()) {
				objectIds.remove(h.getId());
			}
		break;
		case("room"):
			for(Room r: c.getRooms()) {
				objectIds.remove(r.getId());
			}
		break;
		case("software"):
			for(Software s: c.getSoftwares()) {
				objectIds.remove(s.getId());
			}
		break;
		case("user"):
			for(User u: c.getUsers()) {
				objectIds.remove(u.getId());
			}
		break;
		case("faq"):
			for(FAQ f: c.getFaqs() ) {
				objectIds.remove(f.getId());
			}
		break;
		case("announcement"):
			for(Announcement a: c.getAnnouncements() ) {
				objectIds.remove(a.getId());
			}
		break;
		case("contact"):
			for(Contact cont: c.getContacts()) {
				objectIds.remove(cont.getId());
			}
		}
		return objectIds;
	}

	public List<Long> getMembers(Long categoryId, String objectName ) {
		Category c = this.getById(categoryId);
		List<Long> objectIds = new ArrayList<Long>();
		if( c == null ) {
			return objectIds;
		}
		switch(objectName.toLowerCase()){
		case("device"):
			for(Device d : c.getDevices()) {
				objectIds.add(d.getId());
			}
		break;
		case("group"):
			for(Group g : c.getGroups()) {
				objectIds.add(g.getId());
			}
		break;
		case("hwconf"):
			for(HWConf h : c.getHWConfs()) {
				objectIds.add(h.getId());
			}
		break;
		case("room"):
			for(Room r: c.getRooms()) {
				objectIds.add(r.getId());
			}
		break;
		case("software"):
			for(Software s: c.getSoftwares()) {
				objectIds.add(s.getId());
			}
		break;
		case("user"):
			for(User u: c.getUsers()) {
				objectIds.add(u.getId());
			}
		break;
		case("faq"):
			for(FAQ f: c.getFaqs() ) {
				objectIds.add(f.getId());
			}
		break;
		case("announcement"):
			for(Announcement a: c.getAnnouncements() ) {
				objectIds.add(a.getId());
			}
		break;
		case("contact"):
			for(Contact cont: c.getContacts()) {
				objectIds.add(cont.getId());
			}
		}
		return objectIds;
	}

	public OssResponse addMember(Long categoryId, String objectName,Long objectId ) {
		EntityManager em = getEntityManager();
		Category category = this.getById(categoryId);
		boolean changes = false;
		try {
			em.getTransaction().begin();
			switch(objectName.toLowerCase()){
			case("device"):
				Device device = new DeviceController(this.session).getById(objectId);
				if(!category.getDevices().contains(device)) {
					category.getDevices().add(device);
					device.getCategories().add(category);
					em.merge(device);
					changes = true;
				}
			break;
			case("group"):
				Group group = new GroupController(this.session).getById(objectId);
				if(!category.getGroups().contains(group)) {
					category.getGroups().add(group);
					group.getCategories().add(category);
					em.merge(group);
					changes = true;
				}
			break;
			case("hwconf"):
				HWConf hwconf = new CloneToolController(this.session).getById(objectId);
				if(!category.getHWConfs().contains(hwconf)) {
					category.getHWConfs().add(hwconf);
					hwconf.getCategories().add(category);
					em.merge(hwconf);
					changes = true;
				}
			break;
			case("room"):
				Room room = new RoomController(this.session).getById(objectId);
				if(!category.getRooms().contains(room)) {
					category.getRooms().add(room);
					room.getCategories().add(category);
					em.merge(room);
					changes = true;
				}
			break;
			case("software"):
				Software software = new SoftwareController(this.session).getById(objectId);
				if(!category.getSoftwares().contains(software)) {
					category.getSoftwares().add(software);
					software.getCategories().add(category);
					em.merge(software);
					changes = true;
				}
			break;
			case("user"):
				User user = new UserController(this.session).getById(objectId);
				if(!category.getUsers().contains(user)) {
					category.getUsers().add(user);
					user.getCategories().add(category);
					em.merge(user);
					changes = true;
				}
			break;
			case("faq"):
				FAQ faq = new InformationController(this.session).getFAQById(objectId);
				if(!category.getFaqs().contains(faq)) {
					category.getFaqs().add(faq);
					faq.getCategories().add(category);
					em.merge(faq);
					changes = true;
				}
			break;
			case("announcement"):
				Announcement info = new InformationController(this.session).getAnnouncementById(objectId);
				if(!category.getAnnouncements().contains(info)) {
					category.getAnnouncements().add(info);
					info.getCategories().add(category);
					em.merge(info);
					changes = true;
				}
			break;
			case("contact"):
				Contact contact = new InformationController(this.session).getContactById(objectId);
				if(!category.getContacts().contains(contact)) {
					category.getContacts().add(contact);
					contact.getCategories().add(category);
					em.merge(contact);
					changes = true;
				}
			break;
			}
			if( changes ) {
				em.merge(category);
				em.getTransaction().commit();
			}
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
				if(category.getDevices().contains(device)) {
					category.getDevices().remove(device);
					device.getCategories().remove(category);
					em.merge(device);
				}
			break;
			case("Group"):
				Group group = new GroupController(this.session).getById(objectId);
				if(category.getGroups().contains(group)) {
					category.getGroups().remove(group);
					group.getCategories().remove(category);
					em.merge(group);
				}
			break;
			case("HWConf"):
				HWConf hwconf = new CloneToolController(this.session).getById(objectId);
				if(category.getHWConfs().contains(hwconf)) {
					category.getHWConfs().remove(hwconf);
					hwconf.getCategories().remove(category);
					em.merge(hwconf);
				}
			break;
			case("Room"):
				Room room = new RoomController(this.session).getById(objectId);
				if(category.getRooms().contains(room)) {
					category.getRooms().remove(room);
					room.getCategories().remove(category);
					em.merge(room);
				}
			break;
			case("Software"):
				Software software = new SoftwareController(this.session).getById(objectId);
				if( category.getSoftwares().contains(software) ) {
					category.getSoftwares().remove(software);
					category.getRemovedSoftwares().add(software);
					software.getCategories().remove(category);
					software.getRemovedFromCategories().add(category);
					em.merge(software);
				}
			break;
			case("User"):
				User user = new UserController(this.session).getById(objectId);
				if( category.getUsers().contains(user)) {
					category.getUsers().remove(user);
					user.getCategories().remove(category);
					em.merge(user);
				}
			break;
			case("FAQ"):
				FAQ faq = new InformationController(this.session).getFAQById(objectId);
				if(category.getFaqs().contains(faq) ) {
					category.getFaqs().remove(faq);
					faq.getCategories().remove(category);
					em.merge(faq);
				}
			break;
			case("Announcement"):
				Announcement info = new InformationController(this.session).getAnnouncementById(objectId);
				if( category.getAnnouncements().contains(info)) {
					category.getAnnouncements().remove(info);
					info.getCategories().remove(category);
					em.merge(info);
				}
			break;
			case("Contact"):
				Contact contact = new InformationController(this.session).getContactById(objectId);
				if( category.getContacts().contains(contact)) {
					category.getContacts().remove(contact);
					contact.getCategories().remove(category);
					em.merge(contact);
				}
			break;
			}
			em.merge(category);
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
