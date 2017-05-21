package de.openschoolserver.dao.controller;

import javax.persistence.EntityManager;
import javax.persistence.Query;

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
	
	public Response add(Category category){
		EntityManager em = getEntityManager();
		
		try {
			// First we check if the parameter are unique.
			Query query = em.createNamedQuery("Category.getByName").setParameter("name",category.getName());
			if( !query.getResultList().isEmpty() ){
				return new Response(this.getSession(),"ERROR","Category name is not unique.");
			}
			if( !category.getDescription().isEmpty() ) {
				query = em.createNamedQuery("Category.getByDescription").setParameter("description",category.getDescription());
				if( !query.getResultList().isEmpty() ){
					return new Response(this.getSession(),"ERROR","Category description is not unique.");
				}
			}
			em.getTransaction().begin();
			em.persist(category);
			em.getTransaction().commit();
		} catch (Exception e) {
			logger.error(e.getMessage());
			return new Response(this.getSession(),"ERROR",e.getMessage());
		}
		return new Response(this.getSession(),"OK","Category was created");
	}

}
