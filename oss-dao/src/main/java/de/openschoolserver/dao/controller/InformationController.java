/**
 * 
 */
package de.openschoolserver.dao.controller;

import java.util.List;
import java.util.ArrayList;
import java.util.Date;

import javax.persistence.EntityManager;

import de.openschoolserver.dao.Announcement;
import de.openschoolserver.dao.Category;
import de.openschoolserver.dao.Contact;
import de.openschoolserver.dao.FAQ;
import de.openschoolserver.dao.Group;
import de.openschoolserver.dao.Response;
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

	public Response addAnnouncement(Announcement announcement) {
		EntityManager em = getEntityManager();
		announcement.setOwner(this.session.getUser());
		try {
			em.getTransaction().begin();
			em.persist(announcement);
			em.getTransaction().commit();
			return new Response(this.getSession(),"OK", "Announcement was created succesfully.");
		} catch (Exception e) {
			logger.error("add " + e.getMessage(),e);
			return new Response(this.getSession(),"ERROR", e.getMessage());
		} finally {
			em.close();
		}
	}

	public Response addContact(Contact contact) {
		EntityManager em = getEntityManager();
		contact.setOwner(this.session.getUser());
		try {
			em.getTransaction().begin();
			em.persist(contact);
			em.getTransaction().commit();
			return new Response(this.getSession(),"OK", "Contact was created succesfully.");
		} catch (Exception e) {
			logger.error("add " + e.getMessage(),e);
			return new Response(this.getSession(),"ERROR", e.getMessage());
		} finally {
			em.close();
		}
	}

	public Response addFAQ(FAQ faq) {
		EntityManager em = getEntityManager();
		faq.setOwner(this.session.getUser());
		try {
			em.getTransaction().begin();
			em.persist(faq);
			em.getTransaction().commit();
			return new Response(this.getSession(),"OK", "FAQ was created succesfully.");
		} catch (Exception e) {
			logger.error("add " + e.getMessage(),e);
			return new Response(this.getSession(),"ERROR", e.getMessage());
		} finally {
			em.close();
		}
	}

	public List<Announcement> getAnnouncements() {
		List<Announcement> announcements = new ArrayList<Announcement>();
		Date now = new Date(System.currentTimeMillis());
		User user = this.session.getUser();
		for(Group group : user.getGroups() ) {
			for(Category category : group.getCategories() ) {
				for(Announcement announcement : category.getAnnouncements() ) {
					if( announcement.getValidFrom().after(now) &&
						announcement.getValidUntil().before(now) &&
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

	public Response modifyAnnouncement(Announcement announcement) {
		EntityManager em = getEntityManager();
		if( !this.isSuperuser() && !announcement.getOwner().equals(this.session.getUser()) )
		{
			return new Response(this.getSession(),"ERROR", "You have no rights to modify this Announcement");
		}
		try {
			em.getTransaction().begin();
			em.merge(announcement);
			em.getTransaction().commit();
			return new Response(this.getSession(),"OK", "Announcement was modified succesfully.");
		} catch (Exception e) {
			logger.error("add " + e.getMessage(),e);
			return new Response(this.getSession(),"ERROR", e.getMessage());
		} finally {
			em.close();
		}
	}
	
	public Response modifyContact(Contact contact) {
		EntityManager em = getEntityManager();
		if( !this.isSuperuser() && !contact.getOwner().equals(this.session.getUser()) )
		{
			return new Response(this.getSession(),"ERROR", "You have no rights to modify this contact");
		}
		try {
			em.getTransaction().begin();
			em.merge(contact);
			em.getTransaction().commit();
			return new Response(this.getSession(),"OK", "Contact was modified succesfully.");
		} catch (Exception e) {
			logger.error("add " + e.getMessage(),e);
			return new Response(this.getSession(),"ERROR", e.getMessage());
		} finally {
			em.close();
		}
	}

	public Response modifyFAQ(FAQ faq) {
		EntityManager em = getEntityManager();
		if( !this.isSuperuser() && !faq.getOwner().equals(this.session.getUser()) )
		{
			return new Response(this.getSession(),"ERROR", "You have no rights to modify this FAQ ");
		}
		try {
			em.getTransaction().begin();
			em.merge(faq);
			em.getTransaction().commit();
			return new Response(this.getSession(),"OK", "FAQ was modified succesfully.");
		} catch (Exception e) {
			logger.error("add " + e.getMessage(),e);
			return new Response(this.getSession(),"ERROR", e.getMessage());
		} finally {
			em.close();
		}
	}

	public Response deleteAnnouncement(Long announcementId) {
		EntityManager em = getEntityManager();
		Announcement announcement = this.getAnnouncementById(announcementId);
		if( !this.isSuperuser() && !announcement.getOwner().equals(this.session.getUser()) )
		{
			return new Response(this.getSession(),"ERROR", "You have no rights to delete this Announcement");
		}
		try {
			em.getTransaction().begin();
			em.merge(announcement);
			em.remove(announcement);
			em.getTransaction().commit();
			return new Response(this.getSession(),"OK", "Announcement was deleted succesfully.");
		} catch (Exception e) {
			logger.error("add " + e.getMessage(),e);
			return new Response(this.getSession(),"ERROR", e.getMessage());
		} finally {
			em.close();
		}
	}

	public Response deleteContact(Long contactId) {
		EntityManager em = getEntityManager();
		Contact contact = this.getContactById(contactId);
		if( !this.isSuperuser() && !contact.getOwner().equals(this.session.getUser()) )
		{
			return new Response(this.getSession(),"ERROR", "You have no rights to delete this contact");
		}
		try {
			em.getTransaction().begin();
			em.merge(contact);
			em.remove(contact);
			em.getTransaction().commit();
			return new Response(this.getSession(),"OK", "Contact was deleted succesfully.");
		} catch (Exception e) {
			logger.error("add " + e.getMessage(),e);
			return new Response(this.getSession(),"ERROR", e.getMessage());
		} finally {
			em.close();
		}
	}

	public Response deleteFAQ(Long faqId) {
		EntityManager em = getEntityManager();
		FAQ faq = this.getFAQById(faqId);
		if( !this.isSuperuser() && !faq.getOwner().equals(this.session.getUser()) )
		{
			return new Response(this.getSession(),"ERROR", "You have no rights to delete this FAQ");
		}
		try {
			em.getTransaction().begin();
			em.merge(faq);
			em.remove(faq);
			em.getTransaction().commit();
			return new Response(this.getSession(),"OK", "FAQ was deleted succesfully.");
		} catch (Exception e) {
			logger.error("add " + e.getMessage(),e);
			return new Response(this.getSession(),"ERROR", e.getMessage());
		} finally {
			em.close();
		}
	}

}
