/* (c) 2017 EXTIS GmbH - all rights reserved */
package de.openschoolserver.dao.controller;



import javax.persistence.EntityManager;
import javax.persistence.Query;

import de.openschoolserver.dao.Session;
import de.openschoolserver.dao.SessionToken;
import de.openschoolserver.dao.User;


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

   

    public Session createSessionWithUser(User user, String password, String ip) {
       

        if (user != null && user.getId() > 0) {
            final String token = SessionToken.createSessionToken("dummy");
            Session session = new Session(token, user.getId(), password, ip);
            sessions.put(token, session);
            super.session=session; //note: controller was instanciated without a session
            save(session);
            return session;
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

   
}
