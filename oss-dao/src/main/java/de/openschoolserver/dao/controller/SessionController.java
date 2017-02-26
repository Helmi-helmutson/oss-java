/* (c) 2017 EXTIS GmbH - all rights reserved */
package de.openschoolserver.dao.controller;



import javax.persistence.EntityManager;


import javax.persistence.Query;

import de.openschoolserver.dao.Session;
import de.openschoolserver.dao.SessionToken;
import de.openschoolserver.dao.User;
import de.openschoolserver.dao.Room;
import de.openschoolserver.dao.Device;
import de.openschoolserver.dao.Group;
import de.openschoolserver.dao.Acl;
import de.openschoolserver.dao.controller.UserController;
import de.openschoolserver.dao.controller.DeviceController;


import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SessionController extends Controller {

   

    public SessionController(Session session) {
    	super(session);
    }

    public SessionController() {
    	super(null);
    }

   public static Map<String, Session> sessions = new ConcurrentHashMap<String, Session>();

    public void removeAllSessionsFromCache() {
        sessions.clear();
    }

    public void deleteSession(Session session) {
        if (session != null) {
            sessions.remove(session.getToken());
            remove(session); // delete from database
        }
    }

    public Session createSessionWithUser(String username, String password, String deviceType) {
    	UserController userController = new UserController(this.session);
    	DeviceController deviceController = new DeviceController(this.session);
    	Room room = null;
    	//TODO check the password
    	//TODO what to do with deviceType
    	User user = userController.getByUid(username);
    	if( user == null )
    		return null;
  
    	Device device = deviceController.getByIP(this.getSession().getIP());
    	if( device != null )
    		room = device.getRoom();
    
        if (user != null && user.getId() > 0) {
            final String token = SessionToken.createSessionToken("dummy");
            this.session.setToken(token);
            this.session.setUserId(user.getId());
            if( room != null )
               this.session.setRoomId(room.getId());
            if( device != null )
               this.session.setDeviceId(device.getId());
            sessions.put(token, this.session);
            save(session);
            return this.session;
        } else {
            return null;
        }
    }

    private void save(Session obj) {
        EntityManager em = getEntityManager();
        if (em != null) {
            try {
                em.getTransaction().begin();
                if (obj.getId() > 0) {
                    em.merge(obj);
                } else {
                    em.persist(obj);
                }
                em.flush();
                em.refresh(obj);
                em.getTransaction().commit();
            } catch (Exception e) {
                //TODO PGR LOG.error("create failed: " + e.getMessage());
            	System.err.println(e.getMessage());
            } finally {
                em.close();
            }
        }
    }

    private Session find(int id) {
        Session data = null;
        EntityManager em = getEntityManager();
        if (em != null) {
            try {
                em.getTransaction().begin();
                data = em.find(Session.class, id);
                em.getTransaction().commit();
            } finally {
                if ((em != null) && (em.isOpen()))
                    em.close();
            }
        }
        return data;
    }

    private void remove(Session session) {
        EntityManager em = getEntityManager();
        if (em != null) {

            try {

                em.getTransaction().begin();
                Session foundSession = em.find(Session.class, session.getId());
                if (foundSession != null) {
                    em.remove(foundSession);
                }
                em.getTransaction().commit();
            } finally {
                if ((em != null) && (em.isOpen()))
                    em.close();
            }
        }
    }

    public Session getByToken(String token) {
        EntityManager em = getEntityManager();
        Session data = null;
        if (em != null) {
            try {

                em.getTransaction().begin();
                Query q = em.createNamedQuery("Session.getByToken").setParameter("token", token).setMaxResults(1);
                @SuppressWarnings("unchecked")
                List<Session> sessions = q.getResultList();

                if ((sessions != null) && (sessions.size() > 0)) {
                    data = sessions.get(0);
                }

                em.getTransaction().commit();
            } finally {
                if ((em != null) && (em.isOpen()))
                    em.close();
            }
        }
        return data;
    }

    public Session validateToken(String token) {
        Session session = sessions.get(token);
        if (session == null) {
            session = getByToken(token);
            if (session != null) {
                sessions.put(token, session);
            }
        }
        return session; // todo handle correct validation
    }
    
    public boolean authorize(Session session, String requiredRole){
    	EntityManager em = getEntityManager();
    	if (em != null) {
    		try {
    			Query q = em.createNamedQuery("Acl.checkByRole").setParameter("role", session.getUser().getRole()).setParameter("acl",requiredRole).setMaxResults(1);
    			@SuppressWarnings("unchecked")
                List<Acl> acls = q.getResultList();
    			//If there is one result this is allowed by role.
    			if( ! acls.isEmpty() )
    				return true;
    			//Is it allowed by the user
    			for( Acl acl : session.getUser().getAcls() ){
    				if( acl.getAcl().equals(requiredRole))
    					return true;
    			}
    			//Is it allowed by one of the groups of the user
    			for( Group group : session.getUser().getGroups() ) {
    				for( Acl acl : group.getAcls() ) {
    					if( acl.getAcl().equals(requiredRole))
        					return true;
    				}
    			}
    		} finally {
    			if ((em != null) && (em.isOpen()))
                    em.close();
    		}
    	}
    	return false;
    }

   
}
