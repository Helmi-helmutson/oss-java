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

	private EntityManager em;

	public InformationResourceImpl() {
		super();
		em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
	}

	protected void finalize()
	{
	   em.close();
	}

	@Override
	public OssResponse addAnnouncement(Session session, Announcement announcement) {
		InformationController infoController = new InformationController(session,em);
		return infoController.addAnnouncement(announcement);
	}

	@Override
	public OssResponse addContact(Session session, Contact contact) {
		InformationController infoController = new InformationController(session,em);
		return infoController.addContact(contact);
	}

	@Override
	public OssResponse addFAQ(Session session, FAQ faq) {
		InformationController infoController = new InformationController(session,em);
		return infoController.addFAQ(faq);
	}

	@Override
	public List<Announcement> getAnnouncements(Session session) {
		InformationController infoController = new InformationController(session,em);
		return infoController.getAnnouncements();
	}

	@Override
	public List<Announcement> getNewAnnouncements(Session session) {
		return new InformationController(session,em).getNewAnnouncements();
	}

	@Override
	public OssResponse setAnnouncementHaveSeen(Session session, Long announcementId) {
		return new InformationController(session,em).setAnnouncementHaveSeen(announcementId);
	}

	@Override
	public List<Contact> getContacts(Session session) {
		InformationController infoController = new InformationController(session,em);
		return infoController.getContacts();
	}

	@Override
	public List<FAQ> getFAQs(Session session) {
		InformationController infoController = new InformationController(session,em);
		return infoController.getFAQs();
	}

	@Override
	public OssResponse deleteAnnouncement(Session session, Long announcementId) {
		InformationController infoController = new InformationController(session,em);
		return infoController.deleteAnnouncement(announcementId);
	}

	@Override
	public OssResponse deleteContact(Session session, Long contactId) {
		InformationController infoController = new InformationController(session,em);
		return infoController.deleteContact(contactId);
	}

	@Override
	public OssResponse deleteFAQ(Session session, Long faqId) {
		InformationController infoController = new InformationController(session,em);
		return infoController.deleteFAQ(faqId);
	}

	@Override
	public OssResponse modifyAnnouncement(Session session, Long announcementId, Announcement announcement) {
		InformationController infoController = new InformationController(session,em);
		announcement.setId(announcementId);
		return infoController.modifyAnnouncement(announcement);
	}

	@Override
	public OssResponse modifyContact(Session session, Long contactId, Contact contact) {
		InformationController infoController = new InformationController(session,em);
		contact.setId(contactId);
		return infoController.modifyContact(contact);
	}

	@Override
	public OssResponse modifyFAQ(Session session, Long faqId, FAQ faq) {
		InformationController infoController = new InformationController(session,em);
		faq.setId(faqId);
		return infoController.modifyFAQ(faq);
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
		return new InformationController(session,em).getInfoCategories();
	}

	@Override
	public Announcement getAnnouncement(Session session, Long announcementId) {
		return new InformationController(session,em).getAnnouncementById(announcementId);
	}

	@Override
	public Contact getContact(Session session, Long contactId) {
		return new InformationController(session,em).getContactById(contactId);
	}

	@Override
	public FAQ getFAQ(Session session, Long faqId) {
		return new InformationController(session,em).getFAQById(faqId);
	}
}
