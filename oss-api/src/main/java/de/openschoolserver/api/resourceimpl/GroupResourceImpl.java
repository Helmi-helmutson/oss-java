package de.openschoolserver.api.resourceimpl;

import java.util.List;

import de.openschoolserver.api.resources.GroupResource;
import de.openschoolserver.dao.Group;
import de.openschoolserver.dao.Session;

public class GroupResourceImpl implements GroupResource {

	@Override
	public Group getById(Session session, long groupId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Group> getAvailableMembers(Session session, long groupId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Group> getByType(Session session, String type) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Group> getAll(Session session) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Group> search(Session session, String search) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean add(Session session, Group group) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean modify(Session session, Group group) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean delete(Session session, long groupId) {
		// TODO Auto-generated method stub
		return false;
	}

}
