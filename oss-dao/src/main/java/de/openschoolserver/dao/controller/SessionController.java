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
import static de.openschoolserver.dao.internal.OSSConstants.*;

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

	public SessionController(Session session,EntityManager em) {
		super(session,em);
	}

	public SessionController(EntityManager em) {
		super(null,em);
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
		UserController userController = new UserController(this.session,this.em);
		DeviceController deviceController = new DeviceController(this.session,this.em);
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
			if( ! logger.isDebugEnabled() ) {
				Files.delete(file.toPath());
			}
			logger.debug("Login reply:" + reply.toString());
			logger.debug("Login error:" + error.toString());
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
			RoomController  roomController = new RoomController(this.session,this.em);;
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
		if (em != null) {
			try {
				this.em.getTransaction().begin();
				if (obj.getId() > 0) {
					this.em.merge(obj);
				} else {
					this.em.persist(obj);
				}
				Device device = obj.getDevice();
				if( device != null ) {
					User user = obj.getUser();
					if( ! user.getLoggedOn().contains(device) ) {
						user.getLoggedOn().add(device);
						device.getLoggedIn().add(user);
						this.em.merge(device);
						this.em.merge(user);
					}
				}
				this.em.flush();
				this.em.refresh(obj);
				this.em.getTransaction().commit();
			} catch (Exception e) {
				logger.error(e.getMessage());
			} finally {
			}
		}
	}

	private Session find(Long id) {
		Session data = null;
		if (em != null) {
			try {
				this.em.getTransaction().begin();
				data = this.em.find(Session.class, id);
				this.em.getTransaction().commit();
			} catch (Exception e) {
				logger.error(e.getMessage());
			} finally {
				if ((em != null) && (em.isOpen())) {
				}
			}
		}
		return data;
	}

	private void remove(Session session) {
		if (em != null) {
			try {
				this.em.getTransaction().begin();
				Session foundSession = this.em.find(Session.class, session.getId());
				if (foundSession != null) {
					Device device = foundSession.getDevice();
					if( device != null ) {
						User user = foundSession.getUser();
						user.getLoggedOn().remove(device);
						device.getLoggedIn().remove(user);
						this.em.merge(device);
						this.em.merge(user);
					}
					this.em.remove(foundSession);
				}
				this.em.getTransaction().commit();
			} catch (Exception e) {
				logger.error(e.getMessage());
			} finally {
				if ((em != null) && (em.isOpen())) {
				}
			}
		}
	}

	public Session getByToken(String token) {
		Session data = null;
		if (em != null) {
			try {
				Query q = this.em.createNamedQuery("Session.getByToken").setParameter("token", token).setMaxResults(1);
				List<Session> sessions = q.getResultList();
				if ((sessions != null) && (sessions.size() > 0)) {
					data = sessions.get(0);
				}
			} catch (Exception e) {
				logger.error(e.getMessage());
			} finally {
				if ((em != null) && (em.isOpen())) {
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
		try {
			this.em.getTransaction().begin();
			session.setCreateDate(now());
			this.em.merge(session);
			this.em.getTransaction().commit();
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
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

	public String logonScript(String OS) {
		//TODO make logon server configurable
		String[]   program = new String[6];
		StringBuffer reply = new StringBuffer();
		StringBuffer error = new StringBuffer();
		List<String> batFile = new ArrayList<String>();
		batFile.add("net use z: \\\\admin\\"+ this.session.getUser().getUid()
					+ " /persisten:no /user:"
					+ this.getConfigValue("WORKGROUP")+"\\"
					+ this.session.getUser().getUid() + " \""
					+ this.session.getPassword() + "\"");
		program[0] = "/usr/share/oss/plugins/shares/netlogon/open/100-create-logon-script.sh";
		program[1] = this.session.getUser().getUid();
		program[2] = this.session.getIP();
		program[3] = OS;
		if( this.session.getDevice() != null ) {
			program[4] = this.session.getDevice().getName();
		} else {
			program[4] = "dummy";
		}
		program[5] = this.getConfigValue("DOMAIN");
		OSSShellTools.exec(program, reply, error, null);
		File file = new File("/var/lib/samba/sysvol/" + this.getConfigValue("DOMAIN") + "/scripts/" +  this.session.getUser().getUid() + ".bat");
		if( file.exists() ) {
			try {
				String tmp = System.getProperty("line.separator");
				System.setProperty("line.separator", winLineSeparator);
				for(String line : Files.readAllLines(file.toPath()) ) {
					if( line.startsWith("net use z:") ||
						line.contains("netlogon")) {
						continue;
					}
					if( line.startsWith("net use") || line.startsWith("rundll32 printui.dll")) {
						batFile.add(line);
					}
				}
				System.setProperty("line.separator", tmp);
			} catch (Exception e) {
				logger.error("logonScript" + e.getMessage());
			}
		}
		return String.join(winLineSeparator,batFile);
	}
}
