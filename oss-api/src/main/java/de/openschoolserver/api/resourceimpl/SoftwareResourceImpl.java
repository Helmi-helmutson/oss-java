/* (c) 2017 Peter Varkoly <peter@varkoly.de> - all rights reserved */
package de.openschoolserver.api.resourceimpl;

import java.util.List;
import de.openschoolserver.api.resources.SoftwareResource;
import de.openschoolserver.dao.Response;
import de.openschoolserver.dao.Session;
import de.openschoolserver.dao.Software;
import de.openschoolserver.dao.controler.SoftwareControler;

public class SoftwareResourceImpl implements SoftwareResource {

	public SoftwareResourceImpl() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public List<Software> getAll(Session session) {
		SoftwareControler softwareControler = new SoftwareControler(session);
		return softwareControler.getAll();
	}

	@Override
	public Software getById(Session session, long softwareId) {
		SoftwareControler softwareControler = new SoftwareControler(session);
		return softwareControler.getById(softwareId);
	}

	@Override
	public List<Software> search(Session session, String search) {
		SoftwareControler softwareControler = new SoftwareControler(session);
		return softwareControler.search(search);
	}

	@Override
	public Response add(Session session, Software software) {
		SoftwareControler softwareControler = new SoftwareControler(session);
		return softwareControler.add(software);
	}

	@Override
	public Response modify(Session session, Software software) {
		SoftwareControler softwareControler = new SoftwareControler(session);
		return softwareControler.modify(software);
	}

	@Override
	public Response delete(Session session, long softwareId) {
		SoftwareControler softwareControler = new SoftwareControler(session);
		return softwareControler.delete(softwareId);
	}

	@Override
	public Response apply(Session session) {
		SoftwareControler softwareControler = new SoftwareControler(session);
		return softwareControler.applySoftwareStateToHosts();
	}

}
