package de.openschoolserver.api.resourceimpl;

import java.util.List;


import de.openschoolserver.api.resources.CategoryResource;
import de.openschoolserver.dao.Category;
import de.openschoolserver.dao.Response;
import de.openschoolserver.dao.Session;
import de.openschoolserver.dao.controler.CategoryControler;

public class CategoryResourceImpl implements CategoryResource {

	public CategoryResourceImpl() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public List<Category> getAll(Session session) {
		final CategoryControler categoryControler = new CategoryControler(session);
		return categoryControler.getAll();
	}

	@Override
	public Category getById(Session session, long categoryId) {
		final CategoryControler categoryControler = new CategoryControler(session);
		return categoryControler.getById(categoryId);
	}

	@Override
	public List<Long> getMember(Session session, long categoryId, String memberType) {
		final CategoryControler categoryControler = new CategoryControler(session);
		return categoryControler.getMembers(categoryId,memberType);
	}

	@Override
	public List<Long> getAvailableMember(Session session, long categoryId, String memberType) {
		final CategoryControler categoryControler = new CategoryControler(session);
		return categoryControler.getAvailableMembers(categoryId,memberType);
	}

	@Override
	public List<Category> search(Session session, String search) {
		final CategoryControler categoryControler = new CategoryControler(session);
		return categoryControler.search(search);
	}

	@Override
	public Response add(Session session, Category category) {
		final CategoryControler categoryControler = new CategoryControler(session);
		return categoryControler.add(category);
	}

	@Override
	public Response addMember(Session session, long categoryId, String memberType, long memberId) {
		final CategoryControler categoryControler = new CategoryControler(session);
		return categoryControler.addMember(categoryId, memberType, memberId);
	}
	
	@Override
	public Response removeMember(Session session, long categoryId, String memberType, long memberId) {
		final CategoryControler categoryControler = new CategoryControler(session);
		return categoryControler.deleteMember(memberId, memberType, memberId);
	}

	@Override
	public Response modify(Session session, Category category) {
		final CategoryControler categoryControler = new CategoryControler(session);
		return categoryControler.modify(category);
	}

	@Override
	public Response delete(Session session, long categoryId) {
		final CategoryControler categoryControler = new CategoryControler(session);
		return categoryControler.delete(categoryId);
	}

	@Override
	public List<Category> getCategories(Session session, List<Long> categoryIds) {
		final CategoryControler categoryControler = new CategoryControler(session);
		return categoryControler.getCategories(categoryIds);
	}

}
