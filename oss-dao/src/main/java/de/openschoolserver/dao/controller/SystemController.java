package de.openschoolserver.dao.controller;

import de.openschoolserver.dao.Session;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import de.openschoolserver.dao.Enumerate;
import de.openschoolserver.dao.Response;
import de.openschoolserver.dao.User;
import java.util.*;

public class SystemController extends Controller {
	
	public SystemController(Session session) {
		super(session);
	}
	
	
	public List<Map<String, String>> getStatus() {
		//Initialize of sovme variable
		List<Map<String, String>> statusList = new ArrayList<>();
		EntityManager em = getEntityManager();
		Map<String,String> statusMap;
		Query query;
		Integer count;
		
		//Groups;
		statusMap = new HashMap<>();
		statusMap.put("name","groups");
		for( String groupType : this.getEnumerates("groupType")) {
			query = em.createNamedQuery("Group.getByType").setParameter("groupType",groupType);
			count = query.getResultList().size();
			statusMap.put(groupType,count.toString());
		}
		statusList.add(statusMap);
		
		//Users
		statusMap = new HashMap<>();
		statusMap.put("name","users");
		for( String role : this.getEnumerates("role")) {
			query = em.createNamedQuery("User.getByRole").setParameter("role",role);
			count = query.getResultList().size();
			statusMap.put(role,count.toString());
			Integer loggedOn = 0;
			for( User u : (List<User>) query.getResultList() ) {
				loggedOn += u.getLoggedOn().size();
			}
			statusMap.put(role + "-loggedOn", loggedOn.toString());
		}
		statusList.add(statusMap);
		
		//Rooms
		statusMap = new HashMap<>();
		statusMap.put("name","rooms");
		
		
		return statusList;
	}
	
	public List<String> getEnumerates(String type ) {
		EntityManager em = getEntityManager();
		try {
			Query query = em.createNamedQuery("Enumerate.getByType").setParameter("type", type);
			List<String> results = new ArrayList<String>();
			for( Enumerate e :  (List<Enumerate>) query.getResultList() ) {
				results.add(e.getValue());
			}
			return results;
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
