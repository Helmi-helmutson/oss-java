package de.openschoolserver.dao.controller;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import de.openschoolserver.dao.Room;
import de.openschoolserver.dao.Session;

public class RoomController extends Controller {
	
	public RoomController(Session session) {
		super(session);
	}
	

	 
	 public Room getById(int roomId) {
	        EntityManager em = getEntityManager();
	        try {
	            return em.find(Room.class, roomId);
	        } catch (Exception e) {
	           // logger.error(e.getMessage());
	        	System.err.println(e.getMessage()); //TODO
	            return null;
	        } finally {
	            em.close();
	        }
	    }

	    public List<Room> getByIds(List<Integer> roomIds) {
	        EntityManager em = getEntityManager();
	        try {
	            Query query = em.createNamedQuery("Room.findAll"); //TODO select only the given list
	            //query.setParameter("ids", roomIds);
	            return query.getResultList();
	        } catch (Exception e) {
	            //logger.error(e.getMessage());
	        	System.err.println(e.getMessage()); //TODO
	            return new ArrayList<>();
	        } finally {
	            em.close();
	        }
	    }

}
