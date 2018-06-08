/* (c) 2017 Péter Varkoly <peter@varkoly.de> - all rights reserved */
package de.openschoolserver.dao.controller;

import java.util.List;
import java.util.ArrayList;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.ValidatorFactory;

import de.openschoolserver.dao.Announcement;
import de.openschoolserver.dao.Category;
import de.openschoolserver.dao.Contact;
import de.openschoolserver.dao.FAQ;
import de.openschoolserver.dao.Group;
import de.openschoolserver.dao.OssResponse;
import de.openschoolserver.dao.Session;
import de.openschoolserver.dao.User;
/**
 * @author varkoly
 *
 */
public class InformationController extends Controller {

	/**
	 * @param session
	 */
	public InformationController(Session session) {
		super(session);
	}

	public OssResponse addAnnouncement(Announcement announcement) {
		//Check parameters
		StringBuilder errorMessage = new StringBuilder();
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		for (ConstraintViolation<Announcement> violation : factory.getValidator().validate(announcement) ) {
			errorMessage.append(violation.getMessage()).append(getNl());
		}
		if( errorMessage.length() > 0 ) {
			return new OssResponse(this.getSession(),"ERROR", errorMessage.toString());
		}
		EntityManager em = getEntityManager();
		announcement.setOwner(this.session.getUser());
		announcement.setCategories( new ArrayList<Category>() );
		Category category;
		try {
			em.getTransaction().begin();
			for( Long categoryId : announcement.getCategoryIds() ) {
				try {
					category = em.find(Category.class, categoryId);
					category.getAnnouncements().add(announcement);
					announcement.getCategories().add(category);
					em.merge(category);
				} catch (Exception e) {
					logger.error(e.getMessage());
				}
			}
			em.persist(announcement);
			em.getTransaction().commit();
			logger.debug("Created Announcement:" + announcement);
			return new OssResponse(this.getSession(),"OK", "Announcement was created succesfully.",announcement.getId());
		} catch (Exception e) {
			logger.error("add " + e.getMessage(),e);
			return new OssResponse(this.getSession(),"ERROR", e.getMessage());
		} finally {
			em.close();
		}
	}

	public OssResponse addContact(Contact contact) {
		//Check parameters
		StringBuilder errorMessage = new StringBuilder();
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		for (ConstraintViolation<Contact> violation : factory.getValidator().validate(contact) ) {
			errorMessage.append(violation.getMessage()).append(getNl());
		}
		if( errorMessage.length() > 0 ) {
			return new OssResponse(this.getSession(),"ERROR", errorMessage.toString());
		}
		EntityManager em = getEntityManager();
		contact.setOwner(this.session.getUser());
		Category category;
		try {
			em.getTransaction().begin();
			for( Long categoryId : contact.getCategoryIds() ) {
				try {
					category = em.find(Category.class, categoryId);
					category.getContacts().add(contact);
					contact.getCategories().add(category);
					em.merge(category);
				} catch (Exception e) {
					logger.error(e.getMessage());
				}
			}
			em.persist(contact);
			em.getTransaction().commit();
			logger.debug("Created Contact:" + contact);
			return new OssResponse(this.getSession(),"OK", "Contact was created succesfully.",contact.getId());
		} catch (Exception e) {
			logger.error("add " + e.getMessage(),e);
			return new OssResponse(this.getSession(),"ERROR", e.getMessage());
		} finally {
			em.close();
		}
	}

	public OssResponse addFAQ(FAQ faq) {
		//Check parameters
		StringBuilder errorMessage = new StringBuilder();
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		for (ConstraintViolation<FAQ> violation : factory.getValidator().validate(faq) ) {
			errorMessage.append(violation.getMessage()).append(getNl());
		}
		if( errorMessage.length() > 0 ) {
			return new OssResponse(this.getSession(),"ERROR", errorMessage.toString());
		}
		EntityManager em = getEntityManager();
		faq.setOwner(this.session.getUser());
		Category category;
		try {
			em.getTransaction().begin();
			for( Long categoryId : faq.getCategoryIds() ) {
				try {
					category = em.find(Category.class, categoryId);
					category.getFaqs().add(faq);
					faq.getCategories().add(category);
					em.merge(category);
				} catch (Exception e) {
					logger.error(e.getMessage());
				}
			}
			em.persist(faq);
			em.getTransaction().commit();
			logger.debug("Created FAQ:" + faq);
			return new OssResponse(this.getSession(),"OK", "FAQ was created succesfully.",faq.getId());
		} catch (Exception e) {
			logger.error("add " + e.getMessage(),e);
			return new OssResponse(this.getSession(),"ERROR", e.getMessage());
		} finally {
			em.close();
		}
	}

	public List<Announcement> getAnnouncements() {
		List<Announcement> announcements = new ArrayList<Announcement>();
		User user = this.session.getUser();
		for(Group group : user.getGroups() ) {
			for(Category category : group.getCategories() ) {
				for(Announcement announcement : category.getAnnouncements() ) {
					if( announcement.getValidFrom().before(this.now()) &&
					    announcement.getValidUntil().after(this.now())
					)
					{
						announcements.add(announcement);
					}
				}
			}
		}
		return announcements;
	}

	public List<Announcement> getNewAnnouncements() {
		List<Announcement> announcements = new ArrayList<Announcement>();
		User user = this.session.getUser();
		for(Group group : user.getGroups() ) {
			for(Category category : group.getCategories() ) {
				for(Announcement announcement : category.getAnnouncements() ) {
					if( announcement.getValidFrom().after(this.now()) &&
						announcement.getValidUntil().before(this.now()) &&
						! user.getReadAnnouncements().contains(announcement) )
					{
						announcements.add(announcement);
					}
				}
			}
		}
		return announcements;
	}

	public OssResponse setAnnouncementHaveSeen(Long announcementId) {
		EntityManager em = getEntityManager();
		try {
			Announcement announcement = em.find(Announcement.class, announcementId);
			User user = this.session.getUser();
			announcement.getHaveSeenUsers().add(user);
			user.getReadAnnouncements().add(announcement);
			em.getTransaction().begin();
			em.merge(user);
			em.merge(announcement);
			em.getTransaction().commit();
		}catch (Exception e) {
			logger.error("setAnnouncementHaveSeen:" + this.getSession().getUserId() + " " + e.getMessage(),e);
			return new OssResponse(this.getSession(),"ERROR","Annoncement could not be set as seen.");
		} finally {
			em.close();
		}
		return new OssResponse(this.getSession(),"OK","Annoncement was set as seen.");
	}

	public List<FAQ> getFAQs() {
		List<FAQ> faqs = new ArrayList<FAQ>();
		User user = this.session.getUser();
		for(Group group : user.getGroups() ) {
			for(Category category : group.getCategories() ) {
				for(FAQ faq : category.getFaqs() ) {
						faqs.add(faq);
				}
			}
		}
		return faqs;
	}

	public List<Contact> getContacts() {
		List<Contact> contacts = new ArrayList<Contact>();
		User user = this.session.getUser();
		for(Group group : user.getGroups() ) {
			for(Category category : group.getCategories() ) {
				for(Contact contact : category.getContacts() ) {
						contacts.add(contact);
				}
			}
		}
		return contacts;
	}

	public Announcement getAnnouncementById(Long AnnouncementId) {
		EntityManager em = getEntityManager();
		try {
			return em.find(Announcement.class, AnnouncementId);
		} catch (Exception e) {
			logger.error("add " + e.getMessage(),e);
			return null;
		} finally {
			em.close();
		}
	}

	public Contact getContactById(Long ContactId) {
		EntityManager em = getEntityManager();
		try {
			return em.find(Contact.class, ContactId);
		} catch (Exception e) {
			logger.error("add " + e.getMessage(),e);
			return null;
		} finally {
			em.close();
		}
	}

	public FAQ getFAQById(Long FAQId) {
		EntityManager em = getEntityManager();
		try {
			return em.find(FAQ.class, FAQId);
		} catch (Exception e) {
			logger.error("add " + e.getMessage(),e);
			return null;
		} finally {
			em.close();
		}
	}

	public OssResponse modifyAnnouncement(Announcement announcement) {
		//Check parameters
		StringBuilder errorMessage = new StringBuilder();
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		for (ConstraintViolation<Announcement> violation : factory.getValidator().validate(announcement) ) {
			errorMessage.append(violation.getMessage()).append(getNl());
		}
		if( errorMessage.length() > 0 ) {
			return new OssResponse(this.getSession(),"ERROR", errorMessage.toString());
		}
		EntityManager em = getEntityManager();
		if( !this.mayModify(announcement) )
		{
			return new OssResponse(this.getSession(),"ERROR", "You have no rights to modify this Announcement");
		}
		try {
			em.getTransaction().begin();
			em.merge(announcement);
			em.getTransaction().commit();
			return new OssResponse(this.getSession(),"OK", "Announcement was modified succesfully.");
		} catch (Exception e) {
			logger.error("add " + e.getMessage(),e);
			return new OssResponse(this.getSession(),"ERROR", e.getMessage());
		} finally {
			em.close();
		}
	}

	public OssResponse modifyContact(Contact contact) {
		//Check parameters
		StringBuilder errorMessage = new StringBuilder();
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		for (ConstraintViolation<Contact> violation : factory.getValidator().validate(contact) ) {
			errorMessage.append(violation.getMessage()).append(getNl());
		}
		if( errorMessage.length() > 0 ) {
			return new OssResponse(this.getSession(),"ERROR", errorMessage.toString());
		}
		EntityManager em = getEntityManager();
		if( !this.mayModify(contact) )
		{
			return new OssResponse(this.getSession(),"ERROR", "You have no rights to modify this contact");
		}
		try {
			em.getTransaction().begin();
			em.merge(contact);
			em.getTransaction().commit();
			return new OssResponse(this.getSession(),"OK", "Contact was modified succesfully.");
		} catch (Exception e) {
			logger.error("add " + e.getMessage(),e);
			return new OssResponse(this.getSession(),"ERROR", e.getMessage());
		} finally {
			em.close();
		}
	}

	public OssResponse modifyFAQ(FAQ faq) {
		//Check parameters
		StringBuilder errorMessage = new StringBuilder();
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		for (ConstraintViolation<FAQ> violation : factory.getValidator().validate(faq) ) {
			errorMessage.append(violation.getMessage()).append(getNl());
		}
		if( errorMessage.length() > 0 ) {
			return new OssResponse(this.getSession(),"ERROR", errorMessage.toString());
		}
		EntityManager em = getEntityManager();
		if( !this.mayModify(faq) )
		{
			return new OssResponse(this.getSession(),"ERROR", "You have no rights to modify this FAQ ");
		}
		try {
			em.getTransaction().begin();
			em.merge(faq);
			em.getTransaction().commit();
			return new OssResponse(this.getSession(),"OK", "FAQ was modified succesfully.");
		} catch (Exception e) {
			logger.error("add " + e.getMessage(),e);
			return new OssResponse(this.getSession(),"ERROR", e.getMessage());
		} finally {
			em.close();
		}
	}

	public OssResponse deleteAnnouncement(Long announcementId) {
		EntityManager em = getEntityManager();
		Announcement announcement;
		try {
			announcement = em.find(Announcement.class, announcementId);
		} catch (Exception e) {
			logger.error("add " + e.getMessage(),e);
			return null;
		}
		if( !this.mayModify(announcement) )
		{
			return new OssResponse(this.getSession(),"ERROR", "You have no rights to delete this Announcement");
		}
		try {
			em.getTransaction().begin();
			em.merge(announcement);
			for( Category category : announcement.getCategories() ) {
				category.getAnnouncements().remove(announcement);
				em.merge(category);
			}
			em.remove(announcement);
			em.getTransaction().commit();
			return new OssResponse(this.getSession(),"OK", "Announcement was deleted succesfully.");
		} catch (Exception e) {
			logger.error("add " + e.getMessage(),e);
			return new OssResponse(this.getSession(),"ERROR", e.getMessage());
		} finally {
			em.close();
		}
	}

	public OssResponse deleteContact(Long contactId) {
		EntityManager em = getEntityManager();
		Contact contact;
		try {
			contact = em.find(Contact.class, contactId);
		} catch (Exception e) {
			logger.error("add " + e.getMessage(),e);
			return null;
		}
		if( !this.mayModify(contact) )
		{
			return new OssResponse(this.getSession(),"ERROR", "You have no rights to delete this contact");
		}
		try {
			em.getTransaction().begin();
			em.merge(contact);
			for( Category category : contact.getCategories() ) {
				category.getContacts().remove(contact);
				em.merge(category);
			}
			em.remove(contact);
			em.getTransaction().commit();
			return new OssResponse(this.getSession(),"OK", "Contact was deleted succesfully.");
		} catch (Exception e) {
			logger.error("add " + e.getMessage(),e);
			return new OssResponse(this.getSession(),"ERROR", e.getMessage());
		} finally {
			em.close();
		}
	}

	public OssResponse deleteFAQ(Long faqId) {
		EntityManager em = getEntityManager();
		FAQ faq;
		try {
			faq = em.find(FAQ.class, faqId);
		} catch (Exception e) {
			logger.error("add " + e.getMessage(),e);
			return null;
		}
		if( !this.mayModify(faq) )
		{
			return new OssResponse(this.getSession(),"ERROR", "You have no rights to delete this FAQ");
		}
		try {
			em.getTransaction().begin();
			em.merge(faq);
			for( Category category : faq.getCategories() ) {
				category.getFaqs().remove(faq);
				em.merge(category);
			}
			em.remove(faq);
			em.getTransaction().commit();
			return new OssResponse(this.getSession(),"OK", "FAQ was deleted succesfully.");
		} catch (Exception e) {
			logger.error("add " + e.getMessage(),e);
			return new OssResponse(this.getSession(),"ERROR", e.getMessage());
		} finally {
			em.close();
		}
	}


	public List<Category> getInfoCategories() {
		CategoryController categoryController = new CategoryController(this.session);
		if( this.isSuperuser() ) {
			return categoryController.getByType("informations");
		}
		List<Category> categories = this.session.getUser().getCategories();
		for(Category category : categoryController.getByType("informations") ) {
			if( category.getOwner().getId() == 1L ) {
				categories.add(category);
			}
		}
		return categories;
	}

}
