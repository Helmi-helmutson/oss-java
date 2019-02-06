/* (c) 2017 Péter Varkoly <peter@varkoly.de> - all rights reserved */
package de.openschoolserver.api.resourceimpl;

import java.util.List;

import javax.persistence.EntityManager;

import de.openschoolserver.api.resources.CategoryResource;
import de.openschoolserver.dao.Category;
import de.openschoolserver.dao.OssResponse;
import de.openschoolserver.dao.Session;
import de.openschoolserver.dao.controller.CategoryController;
import de.openschoolserver.dao.internal.CommonEntityManagerFactory;

public class CategoryResourceImpl implements CategoryResource {

	public CategoryResourceImpl() {
	}

	@Override
	public List<Category> getAll(Session session) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		final CategoryController categoryController = new CategoryController(session,em);
		return categoryController.getAll();
	}

	@Override
	public Category getById(Session session, long categoryId) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		final CategoryController categoryController = new CategoryController(session,em);
		return categoryController.getById(categoryId);
	}

	@Override
	public List<Long> getMember(Session session, long categoryId, String memberType) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		final CategoryController categoryController = new CategoryController(session,em);
		return categoryController.getMembers(categoryId,memberType);
	}

	@Override
	public List<Long> getAvailableMember(Session session, long categoryId, String memberType) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		final CategoryController categoryController = new CategoryController(session,em);
		return categoryController.getAvailableMembers(categoryId,memberType);
	}

	@Override
	public List<Category> search(Session session, String search) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		final CategoryController categoryController = new CategoryController(session,em);
		return categoryController.search(search);
	}

	@Override
	public OssResponse add(Session session, Category category) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		final CategoryController categoryController = new CategoryController(session,em);
		return categoryController.add(category);
	}

	@Override
	public OssResponse addMember(Session session, long categoryId, String memberType, long memberId) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		final CategoryController categoryController = new CategoryController(session,em);
		return categoryController.addMember(categoryId, memberType, memberId);
	}

	@Override
	public OssResponse removeMember(Session session, long categoryId, String memberType, long memberId) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		final CategoryController categoryController = new CategoryController(session,em);
		return categoryController.deleteMember(categoryId, memberType, memberId);
	}

	@Override
	public OssResponse modify(Session session, Category category) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		final CategoryController categoryController = new CategoryController(session,em);
		return categoryController.modify(category);
	}

	@Override
	public OssResponse delete(Session session, long categoryId) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		final CategoryController categoryController = new CategoryController(session,em);
		return categoryController.delete(categoryId);
	}

	@Override
	public List<Category> getCategories(Session session, List<Long> categoryIds) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		final CategoryController categoryController = new CategoryController(session,em);
		return categoryController.getCategories(categoryIds);
	}

}
