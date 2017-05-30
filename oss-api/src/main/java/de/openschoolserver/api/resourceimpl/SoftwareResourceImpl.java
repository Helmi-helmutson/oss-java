package de.openschoolserver.api.resourceimpl;

import java.util.List;
import de.openschoolserver.api.resources.SoftwareResource;
import de.openschoolserver.dao.Response;
import de.openschoolserver.dao.Session;
import de.openschoolserver.dao.Software;
import de.openschoolserver.dao.controller.SoftwareController;

public class SoftwareResourceImpl implements SoftwareResource {

	public SoftwareResourceImpl() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public List<Software> getAll(Session session) {
		SoftwareController softwareController = new SoftwareController(session);
		return softwareController.getAll();
	}

	@Override
	public Software getById(Session session, long softwareId) {
		SoftwareController softwareController = new SoftwareController(session);
		return softwareController.getById(softwareId);
	}

	@Override
	public List<Software> search(Session session, String search) {
		SoftwareController softwareController = new SoftwareController(session);
		return softwareController.search(search);
	}

	@Override
	public Response add(Session session, Software software) {
		SoftwareController softwareController = new SoftwareController(session);
		return softwareController.add(software);
	}

	@Override
	public Response modify(Session session, Software software) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Response delete(Session session, long softwareId) {
		// TODO Auto-generated method stub
		return null;
	}

}
