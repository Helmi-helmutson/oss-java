/* (c) 2017 Péter Varkoly <peter@varkoly.de> - all rights reserved */
package de.openschoolserver.api.resourceimpl;

import java.util.List;


import de.openschoolserver.api.resources.CategoryResource;
import de.openschoolserver.dao.Category;
import de.openschoolserver.dao.OssResponse;
import de.openschoolserver.dao.Session;
import de.openschoolserver.dao.controller.CategoryController;

public class CategoryResourceImpl implements CategoryResource {

	public CategoryResourceImpl() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public List<Category> getAll(Session session) {
		final CategoryController categoryController = new CategoryController(session);
		return categoryController.getAll();
	}

	@Override
	public Category getById(Session session, long categoryId) {
		final CategoryController categoryController = new CategoryController(session);
		return categoryController.getById(categoryId);
	}

	@Override
	public List<Long> getMember(Session session, long categoryId, String memberType) {
		final CategoryController categoryController = new CategoryController(session);
		return categoryController.getMembers(categoryId,memberType);
	}

	@Override
	public List<Long> getAvailableMember(Session session, long categoryId, String memberType) {
		final CategoryController categoryController = new CategoryController(session);
		return categoryController.getAvailableMembers(categoryId,memberType);
	}

	@Override
	public List<Category> search(Session session, String search) {
		final CategoryController categoryController = new CategoryController(session);
		return categoryController.search(search);
	}

	@Override
	public OssResponse add(Session session, Category category) {
		final CategoryController categoryController = new CategoryController(session);
		return categoryController.add(category);
	}

	@Override
	public OssResponse addMember(Session session, long categoryId, String memberType, long memberId) {
		final CategoryController categoryController = new CategoryController(session);
		return categoryController.addMember(categoryId, memberType, memberId);
	}
	
	@Override
	public OssResponse removeMember(Session session, long categoryId, String memberType, long memberId) {
		final CategoryController categoryController = new CategoryController(session);
		return categoryController.deleteMember(categoryId, memberType, memberId);
	}

	@Override
	public OssResponse modify(Session session, Category category) {
		final CategoryController categoryController = new CategoryController(session);
		return categoryController.modify(category);
	}

	@Override
	public OssResponse delete(Session session, long categoryId) {
		final CategoryController categoryController = new CategoryController(session);
		return categoryController.delete(categoryId);
	}

	@Override
	public List<Category> getCategories(Session session, List<Long> categoryIds) {
		final CategoryController categoryController = new CategoryController(session);
		return categoryController.getCategories(categoryIds);
	}

}
