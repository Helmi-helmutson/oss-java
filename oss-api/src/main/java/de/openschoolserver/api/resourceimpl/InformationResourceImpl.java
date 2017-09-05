package de.openschoolserver.api.resourceimpl;

import java.util.List;


import de.openschoolserver.api.resources.InformationResource;
import de.openschoolserver.dao.Announcement;
import de.openschoolserver.dao.Category;
import de.openschoolserver.dao.Contact;
import de.openschoolserver.dao.FAQ;
import de.openschoolserver.dao.Response;
import de.openschoolserver.dao.Session;
import de.openschoolserver.dao.controller.InformationController;

public class InformationResourceImpl implements InformationResource {

	public InformationResourceImpl() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public Response addAnnouncement(Session session, Announcement announcement) {
		InformationController infoController = new InformationController(session);
		return infoController.addAnnouncement(announcement);
	}

	@Override
	public Response addContact(Session session, Contact contact) {
		InformationController infoController = new InformationController(session);
		return infoController.addContact(contact);
	}

	@Override
	public Response addFAQ(Session session, FAQ faq) {
		InformationController infoController = new InformationController(session);
		return infoController.addFAQ(faq);
	}

	@Override
	public List<Announcement> getAnnouncements(Session session) {
		InformationController infoController = new InformationController(session);
		return infoController.getAnnouncements();
	}

	@Override
	public List<Contact> getContacts(Session session) {
		InformationController infoController = new InformationController(session);
		return infoController.getContacts();
	}

	@Override
	public List<FAQ> getFAQs(Session session) {
		InformationController infoController = new InformationController(session);
		return infoController.getFAQs();
	}

	@Override
	public Response deleteAnnouncement(Session session, Long announcementId) {
		InformationController infoController = new InformationController(session);
		return infoController.deleteAnnouncement(announcementId);
	}

	@Override
	public Response deleteContact(Session session, Long contactId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Response deleteFAQ(Session session, Long faqId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Category> getAnnouncementCategories(Session session) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Category> getContactsCategories(Session session) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Category> getFAQCategories(Session session) {
		// TODO Auto-generated method stub
		return null;
	}

}
