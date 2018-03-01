/* (c) 2017 Péter Varkoly <peter@varkoly.de> - all rights reserved */
package de.openschoolserver.dao.controller;

import java.util.List;
import java.util.ArrayList;

import javax.persistence.EntityManager;
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
		try {
			em.getTransaction().begin();
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
		try {
			em.getTransaction().begin();
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
		try {
			em.getTransaction().begin();
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
		Announcement announcement = this.getAnnouncementById(announcementId);
		if( !this.mayModify(announcement) )
		{
			return new OssResponse(this.getSession(),"ERROR", "You have no rights to delete this Announcement");
		}
		try {
			em.getTransaction().begin();
			em.merge(announcement);
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
		Contact contact = this.getContactById(contactId);
		if( !this.mayModify(contact) )
		{
			return new OssResponse(this.getSession(),"ERROR", "You have no rights to delete this contact");
		}
		try {
			em.getTransaction().begin();
			em.merge(contact);
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
		FAQ faq = this.getFAQById(faqId);
		if( !this.mayModify(faq) )
		{
			return new OssResponse(this.getSession(),"ERROR", "You have no rights to delete this FAQ");
		}
		try {
			em.getTransaction().begin();
			em.merge(faq);
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


	public List<Category> getInfoCategories(String search) {
		if( this.isSuperuser() ) {
			CategoryController categoryController = new CategoryController(this.session);
			return categoryController.getByType(search);
		}
		List<Category> categories = new ArrayList<Category>();
		for(Category category : this.session.getUser().getCategories() ) {
			if(category.getCategoryType().equals(search)) {
				categories.add(category);
			}
		}
		return categories;
	}

}
