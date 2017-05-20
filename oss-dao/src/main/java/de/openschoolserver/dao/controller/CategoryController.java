package de.openschoolserver.dao.controller;

import javax.persistence.EntityManager;

import de.openschoolserver.dao.*;

public class CategoryController extends Controller {

	public CategoryController(Session session) {
		super(session);
	}
	
	public Category getById(long categoryId) {
		EntityManager em = getEntityManager();

		try {
			return em.find(Category.class, categoryId);
		} catch (Exception e) {
			logger.error(e.getMessage());
			return null;
		} finally {
			em.close();
		}
	}

}
