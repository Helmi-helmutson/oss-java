package de.openschoolserver.dao.controller;

import de.openschoolserver.dao.Session;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import de.openschoolserver.dao.Enumerate;
import de.openschoolserver.dao.Response;
import java.util.List;

public class SystemController extends Controller {
	
	public SystemController(Session session) {
		super(session);
	}
	
	public List<String> getEnumerates(String type ) {
		EntityManager em = getEntityManager();
		try {
			Query query = em.createNamedQuery("Enumerate.getByType").setParameter("tye", type);
			return query.getResultList();
		} catch (Exception e) {
			//logger.error(e.getMessage());
			System.err.println(e.getMessage());
			return null;
		} finally {
			em.close();
		}
	}
	
	public Response addEnumerate(String type, String value) {
		EntityManager em = getEntityManager();
		Enumerate en = new Enumerate();
		en.setName(type);
		en.setValue(value);
		try {
			em.persist(en);
		} catch (Exception e) {
			//logger.error(e.getMessage());
			System.err.println(e.getMessage());
			return new Response(this.getSession(),"ERROR", e.getMessage());
		} finally {
			em.close();
		}
		return new Response(this.getSession(),"OK","Enumerate was created succesfully.");
	}
	
	public Response removeEnumerate(String type, String value) {
		EntityManager em = getEntityManager();
		Query query = em.createNamedQuery("Enumerate.getByType").setParameter("type", type).setParameter("value", value);
		try {
			Enumerate en = (Enumerate) query.getSingleResult();
			em.remove(en);
		} catch (Exception e) {
			//logger.error(e.getMessage());
			System.err.println(e.getMessage());
			return new Response(this.getSession(),"ERROR", e.getMessage());
		} finally {
			em.close();
		}
		return new Response(this.getSession(),"OK","Enumerate was removed succesfully.");
	}
	

}
