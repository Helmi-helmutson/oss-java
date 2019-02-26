/* (c) 2017 Péter Varkoly <peter@varkoly.de> - all rights reserved */
package de.openschoolserver.api.resourceimpl;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import de.openschoolserver.api.resources.InformationResource;
import de.openschoolserver.dao.Announcement;
import de.openschoolserver.dao.Category;
import de.openschoolserver.dao.Contact;
import de.openschoolserver.dao.FAQ;
import de.openschoolserver.dao.OssResponse;
import de.openschoolserver.dao.Session;
import de.openschoolserver.dao.controller.InformationController;
import de.openschoolserver.dao.internal.CommonEntityManagerFactory;

public class InformationResourceImpl implements InformationResource {

	public InformationResourceImpl() {
	}

	@Override
	public OssResponse addAnnouncement(Session session, Announcement announcement) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		InformationController infoController = new InformationController(session,em);
		OssResponse resp = infoController.addAnnouncement(announcement);
		em.close();
		return resp;
	}

	@Override
	public OssResponse addContact(Session session, Contact contact) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		InformationController infoController = new InformationController(session,em);
		OssResponse resp = infoController.addContact(contact);
		em.close();
		return resp;
	}

	@Override
	public OssResponse addFAQ(Session session, FAQ faq) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		InformationController infoController = new InformationController(session,em);
		OssResponse resp = infoController.addFAQ(faq);
		em.close();
		return resp;
	}

	@Override
	public List<Announcement> getAnnouncements(Session session) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		InformationController infoController = new InformationController(session,em);
		List<Announcement> resp = infoController.getAnnouncements();
		em.close();
		return resp;
	}

	@Override
	public List<Announcement> getNewAnnouncements(Session session) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		List<Announcement> resp = new InformationController(session,em).getNewAnnouncements();
		em.close();
		return resp;
	}

	@Override
	public OssResponse setAnnouncementHaveSeen(Session session, Long announcementId) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		OssResponse resp = new InformationController(session,em).setAnnouncementHaveSeen(announcementId);
		em.close();
		return resp;
	}

	@Override
	public List<Contact> getContacts(Session session) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		InformationController infoController = new InformationController(session,em);
		List<Contact> resp = infoController.getContacts();
		em.close();
		return resp;
	}

	@Override
	public List<FAQ> getFAQs(Session session) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		InformationController infoController = new InformationController(session,em);
		List<FAQ> resp = infoController.getFAQs();
		em.close();
		return resp;
	}

	@Override
	public OssResponse deleteAnnouncement(Session session, Long announcementId) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		InformationController infoController = new InformationController(session,em);
		OssResponse resp = infoController.deleteAnnouncement(announcementId);
		em.close();
		return resp;
	}

	@Override
	public OssResponse deleteContact(Session session, Long contactId) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		InformationController infoController = new InformationController(session,em);
		OssResponse resp = infoController.deleteContact(contactId);
		em.close();
		return resp;
	}

	@Override
	public OssResponse deleteFAQ(Session session, Long faqId) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		InformationController infoController = new InformationController(session,em);
		OssResponse resp = infoController.deleteFAQ(faqId);
		em.close();
		return resp;
	}

	@Override
	public OssResponse modifyAnnouncement(Session session, Long announcementId, Announcement announcement) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		InformationController infoController = new InformationController(session,em);
		announcement.setId(announcementId);
		OssResponse resp = infoController.modifyAnnouncement(announcement);
		em.close();
		return resp;
	}

	@Override
	public OssResponse modifyContact(Session session, Long contactId, Contact contact) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		InformationController infoController = new InformationController(session,em);
		contact.setId(contactId);
		OssResponse resp = infoController.modifyContact(contact);
		em.close();
		return resp;
	}

	@Override
	public OssResponse modifyFAQ(Session session, Long faqId, FAQ faq) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		InformationController infoController = new InformationController(session,em);
		faq.setId(faqId);
		OssResponse resp = infoController.modifyFAQ(faq);
		em.close();
		return resp;
	}

	@Override
	public List<Announcement> getMyAnnouncements(Session session) {
		List<Announcement> announcements = new ArrayList<Announcement>();
		for( Announcement a :  session.getUser().getMyAnnouncements() ) {
			a.setText("");
			announcements.add(a);
		}
		return announcements;
	}

	@Override
	public List<Contact> getMyContacts(Session session) {
		return session.getUser().getMyContacts();
	}

	@Override
	public List<FAQ> getMyFAQs(Session session) {
		List<FAQ> faqs = new ArrayList<FAQ>();
		for( FAQ faq :  session.getUser().getMyFAQs() ) {
			faq.setText("");
			faqs.add(faq);
		}
		return faqs;
	}

	@Override
	public List<Category> getInformationCategories(Session session) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		List<Category> resp = new InformationController(session,em).getInfoCategories();
		em.close();
		return resp;
	}

	@Override
	public Announcement getAnnouncement(Session session, Long announcementId) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		Announcement resp = new InformationController(session,em).getAnnouncementById(announcementId);
		em.close();
		return resp;
	}

	@Override
	public Contact getContact(Session session, Long contactId) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		Contact resp = new InformationController(session,em).getContactById(contactId);
		em.close();
		return resp;
	}

	@Override
	public FAQ getFAQ(Session session, Long faqId) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		FAQ resp = new InformationController(session,em).getFAQById(faqId);
		em.close();
		return resp;
	}
}
