 /* (c) 2017 Peter Varkoly <peter@varkoly.de> - all rights reserved */
/* (c) 2017 EXTIS GmbH - all rights reserved */
package de.openschoolserver.dao.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import de.openschoolserver.dao.Session;
import de.openschoolserver.dao.SessionToken;
import de.openschoolserver.dao.User;
import de.openschoolserver.dao.controller.DeviceController;
import de.openschoolserver.dao.controller.UserController;
import de.openschoolserver.dao.Room;
import de.openschoolserver.dao.Device;
import de.openschoolserver.dao.Group;
import de.openschoolserver.dao.Acl;
import de.openschoolserver.dao.tools.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import io.dropwizard.auth.AuthenticationException;

@SuppressWarnings("unchecked")
public class SessionController extends Controller {

	Logger logger = LoggerFactory.getLogger(SessionController.class); 

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
		String[]   program = new String[5];
		StringBuffer reply = new StringBuffer();
		StringBuffer error = new StringBuffer();
		program[0] = "/usr/bin/smbclient";
		program[1] = "-L";
		program[2] = "admin";
		program[3] = "-A";
		try {
			File file = File.createTempFile("login", ".cred", new File("/opt/oss-java/tmp/"));
			List<String> credentials = new ArrayList<String>();
			credentials.add("username=" + username);
			credentials.add("password=" + password);
			credentials.add("domain=" + this.getConfigValue("WORKGROUP"));
			Files.write(file.toPath(), credentials);
			program[4] = file.getAbsolutePath();
			OSSShellTools.exec(program, reply, error, null);
			Files.delete(file.toPath());
			if( reply.toString().contains("NT_STATUS_")) {
				return null;
			}
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			return null;
		}
		
		//TODO what to do with deviceType
		User user = userController.getByUid(username);
		if( user == null ) {
			return null;
		}

		String IP = this.getSession().getIP();
		Device device = deviceController.getByIP(IP);
		if( device != null ) {
			room = device.getRoom();
		}

		final String token = SessionToken.createSessionToken("dummy");
		this.session.setToken(token);
		this.session.setUserId(user.getId());
		this.session.setRole(user.getRole());
		this.session.setUser(user);
		if( room != null ) {
			this.session.setRoomId(room.getId());
			this.session.setRoom(room);
		}
		if( device != null ) {
			this.session.setDeviceId(device.getId());
			this.session.setMac(device.getMac());
			this.session.setIP(device.getIp());
			this.session.setDNSName(device.getName());
			this.session.setDevice(device);
		} else {
			//Evaluate the MAC Address
			if( !IP.contains("127.0.0.1") && IPv4.validateIPAddress(IP) && room == null ) {
				reply = new StringBuffer();
				error = new StringBuffer();
				program = new String[3];
				program[0] = "arp";
				program[1] = "-n";
				program[2] = IP;
				OSSShellTools.exec(program, reply, error, null);
				String[] lines = reply.toString().split("\\n");
				if( lines.length >1 ) {
					String[] fields = lines[1].split("\\s+");
					if( fields.length > 2 ) {
						this.session.setMac(fields[2]);
					}
				}
			}
		}
		List<String> modules = new ArrayList<String>();
		//Modules with right permit all is allowed for all authorized users.
		modules.add("permitall");
		//Is it allowed by the groups.
		for( Group group : user.getGroups() ) {
			for( Acl acl : group.getAcls() ) {
				if( acl.getAllowed() ) {
					modules.add(acl.getAcl());
				}
			}
		}
		//Is it allowed by the user
		for( Acl acl : user.getAcls() ){
			if( acl.getAllowed() && !modules.contains(acl.getAcl())) {
				modules.add(acl.getAcl());
			} else if( modules.contains(acl.getAcl()) ) {
				//It is forbidden by the user
				modules.remove(acl.getAcl());
			}
		}
		if( !this.isSuperuser() ) {
			RoomController  roomController = new RoomController(session);
			if( ! roomController.getAllToRegister().isEmpty() ) {
				modules.add("adhoclan.mydevices");
			}
		}
		session.setCommonName(user.getGivenName() + " " + user.getSurName());
		session.setAcls(modules);
		session.setPassword(password);
		sessions.put(token, this.session);
		save(session);
		return this.session;
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
				Device device = obj.getDevice();
				if( device != null ) {
					User user = obj.getUser();
					if( ! user.getLoggedOn().contains(device) ) {
						user.getLoggedOn().add(device);
						device.getLoggedIn().add(user);
						em.merge(device);
						em.merge(user);
					}
				}
				em.flush();
				em.refresh(obj);
				em.getTransaction().commit();
			} catch (Exception e) {
				logger.error(e.getMessage());
			} finally {
				em.close();
			}
		}
	}

	private Session find(Long id) {
		Session data = null;
		EntityManager em = getEntityManager();
		if (em != null) {
			try {
				em.getTransaction().begin();
				data = em.find(Session.class, id);
				em.getTransaction().commit();
			} catch (Exception e) {
				logger.error(e.getMessage());
			} finally {
				if ((em != null) && (em.isOpen())) {
					em.close();
				}
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
					Device device = foundSession.getDevice();
					if( device != null ) {
						User user = foundSession.getUser();
						user.getLoggedOn().remove(device);
						device.getLoggedIn().remove(user);
						em.merge(device);
						em.merge(user);
					}   
					em.remove(foundSession);
				}
				em.getTransaction().commit();
			} catch (Exception e) {
				logger.error(e.getMessage());
			} finally {
				if ((em != null) && (em.isOpen())) {
					em.close();
				}
			}
		}
	}

	public Session getByToken(String token) {
		EntityManager em = getEntityManager();
		Session data = null;
		if (em != null) {
			try {
				Query q = em.createNamedQuery("Session.getByToken").setParameter("token", token).setMaxResults(1);
				List<Session> sessions = q.getResultList();
				if ((sessions != null) && (sessions.size() > 0)) {
					data = sessions.get(0);
				}
			} catch (Exception e) {
				logger.error(e.getMessage());
			} finally {
				if ((em != null) && (em.isOpen())) {
					em.close();
				}
			}
		}
		return data;
	}

	public Session validateToken(String token) throws AuthenticationException {
		Session session = sessions.get(token);
		if (session == null) {
			session = getByToken(token);
			if (session != null) {
				sessions.put(token, session);
			} else {
				return null;
			}
		}
		if( !isSuperuser(session)) {
			Long timeout = 90L;
			try {
				timeout = Long.valueOf(this.getConfigValue("SESSION_TIMEOUT"));
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
			timeout = timeout * 60000;
			if( (now().getTime() - session.getCreateDate().getTime())  > timeout ) {
				logger.info("Session was timed out." + session);
				deleteSession(session);
				//throw new AuthenticationException("Session expired.");
				return null;
			} else {
				updateSession(session);
			}
		}
		return session; // todo handle correct validation
	}

	public void updateSession(Session session) {
		EntityManager em = getEntityManager();
		try {
			em.getTransaction().begin();
			session.setCreateDate(now());
			em.merge(session);
			em.getTransaction().commit();
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			em.close();
		}
	}

	public boolean authorize(Session session, String requiredRole){

		//The simply way
		if( session.getUser().getRole().contains(requiredRole)) {
			return true;
		}
		//Is it allowed by the user
		for( Acl acl : session.getUser().getAcls() ){
			if( acl.getAcl().equals(requiredRole)) {
				return acl.getAllowed();
			}
		}
		//Is it allowed by one of the groups of the user
		for( Group group : session.getUser().getGroups() ) {
			for( Acl acl : group.getAcls() ) {
				if( acl.getAcl().equals(requiredRole)) {
					return acl.getAllowed();
				}
			}
		}
		return false;
	}
	
	public Session getLocalhostSession() {
		String token = this.getProperty("de.openschoolserver.api.auth.localhost");
		if( token != null ) {
			return this.getByToken(token);
		}
		return null;
	}
}
