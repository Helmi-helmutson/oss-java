/* (c) 2017 Péter Varkoly <peter@varkoly.de> - all rights reserved */
package de.openschoolserver.api.resourceimpl;

import java.util.List;


import de.openschoolserver.api.resources.InformationResource;
import de.openschoolserver.dao.Announcement;
import de.openschoolserver.dao.Category;
import de.openschoolserver.dao.Contact;
import de.openschoolserver.dao.FAQ;
import de.openschoolserver.dao.OssResponse;
import de.openschoolserver.dao.Session;
import de.openschoolserver.dao.controller.InformationController;

public class InformationResourceImpl implements InformationResource {

	public InformationResourceImpl() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public OssResponse addAnnouncement(Session session, Announcement announcement) {
		InformationController infoController = new InformationController(session);
		return infoController.addAnnouncement(announcement);
	}

	@Override
	public OssResponse addContact(Session session, Contact contact) {
		InformationController infoController = new InformationController(session);
		return infoController.addContact(contact);
	}

	@Override
	public OssResponse addFAQ(Session session, FAQ faq) {
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
	public OssResponse deleteAnnouncement(Session session, Long announcementId) {
		InformationController infoController = new InformationController(session);
		return infoController.deleteAnnouncement(announcementId);
	}

	@Override
	public OssResponse deleteContact(Session session, Long contactId) {
		InformationController infoController = new InformationController(session);
		return infoController.deleteContact(contactId);
	}

	@Override
	public OssResponse deleteFAQ(Session session, Long faqId) {
		InformationController infoController = new InformationController(session);
		return infoController.deleteFAQ(faqId);
	}

	@Override
	public List<Category> getAnnouncementCategories(Session session) {
		InformationController infoController = new InformationController(session);
		return infoController.getInfoCategories("announcements");
	}

	@Override
	public List<Category> getContactsCategories(Session session) {
		InformationController infoController = new InformationController(session);
		return infoController.getInfoCategories("contacts");
	}

	@Override
	public List<Category> getFAQCategories(Session session) {
		InformationController infoController = new InformationController(session);
		return infoController.getInfoCategories("faqs");
	}

	@Override
	public OssResponse modifyAnnouncement(Session session, Long announcementId, Announcement announcement) {
		InformationController infoController = new InformationController(session);
		announcement.setId(announcementId);
		return infoController.modifyAnnouncement(announcement);
	}

	@Override
	public OssResponse modifyContact(Session session, Long contactId, Contact contact) {
		InformationController infoController = new InformationController(session);
		contact.setId(contactId);
		return infoController.modifyContact(contact);
	}

	@Override
	public OssResponse modifyFAQ(Session session, Long faqId, FAQ faq) {
		InformationController infoController = new InformationController(session);
		faq.setId(faqId);
		return infoController.modifyFAQ(faq);
	}

}
