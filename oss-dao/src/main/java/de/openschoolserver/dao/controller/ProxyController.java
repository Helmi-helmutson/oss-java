/* (c) 2017 Péter Varkoly <peter@varkoly.de> - all rights reserved */
package de.openschoolserver.dao.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.openschoolserver.dao.Device;
import de.openschoolserver.dao.OssResponse;
import de.openschoolserver.dao.PositiveList;
import de.openschoolserver.dao.ProxyRule;
import de.openschoolserver.dao.Room;
import de.openschoolserver.dao.Session;
import de.openschoolserver.dao.User;
import de.openschoolserver.dao.tools.OSSShellTools;

@SuppressWarnings( "unchecked" )
public class ProxyController extends Controller {

	Logger logger = LoggerFactory.getLogger(ProxyController.class); 

	public ProxyController(Session session) {
		super(session);
	}
	
	/*
	 * Reads the default proxy acl setting
	 * @return The default proxy acl setting
	 */
	public List<ProxyRule> readDefaults(String role) {
		List<ProxyRule> acl = new ArrayList<ProxyRule>();
		String[] program   = new String[2];
		program[0] = "/usr/share/oss/tools/squidGuard.pl";
		program[1] = "read";
		StringBuffer reply = new StringBuffer();
		StringBuffer error = new StringBuffer();
		OSSShellTools.exec(program, reply, error, "");
		for( String line : reply.toString().split("\\n") ) {
			String[] values = line.split(" ");
			if( role.equals(values[0])) {
				for(int i=1; i < values.length; i++ ) {
					ProxyRule proxyRule = new ProxyRule(
							values[i].split(":")[0],
							values[i].split(":")[1].equals("true")
							);
					acl.add(proxyRule);
				}
			}
		}
		return acl;
	}

	/*
	 * Writes the default proxy setting
	 * @param acls The list of the default acl setting
	 * @return An OssResponse object with the result
	 */
	public OssResponse setDefaults(String role, List<ProxyRule> acl) {
		StringBuilder output = new StringBuilder();
		for(ProxyRule proxyRule : acl ) {
			output.append(role).append(":").append(proxyRule.getName()).append(":");
			if( proxyRule.isEnabled() ) {
				output.append("true");
			} else {
				output.append("false");
			}
			output.append(this.getNl());
		}
		String[] program   = new String[2];
		program[0] = "/usr/share/oss/tools/squidGuard.pl";
		program[1] = "write";
		StringBuffer reply = new StringBuffer();
		StringBuffer error = new StringBuffer();
		logger.debug(output.toString());
		OSSShellTools.exec(program, reply, error, output.toString());
		return new OssResponse(this.session,"OK","Proxy Setting was saved succesfully.");
	}
	
	public PositiveList getPositiveListById( Long positiveListId ) {
		EntityManager em = getEntityManager();
		try {
			return em.find(PositiveList.class, positiveListId);
		} catch (Exception e) {
			logger.debug("PositiveList:" + positiveListId + " " + e.getMessage(),e);
			return null;
		} finally {
			em.close();
		}
	}

	public PositiveList getPositiveListByName( String name ) {
		EntityManager em = getEntityManager();
		try {
			Query query = em.createNamedQuery("PostiveList.byName").setParameter("name", name);
			return (PositiveList) query.getResultList().get(0);
		} catch (Exception e) {
			return null;
		} finally {
			em.close();
		}
	}

	/*
	 * Creates or modify a positive list
	 * @param positiveList The positive list to be saved
	 * @return An OssResponse object with the result
	 */
	public OssResponse editPositiveList(PositiveList positiveList) {
		EntityManager em = getEntityManager();
		logger.debug(positiveList.toString());
		PositiveList oldPositiveList = this.getPositiveListById(positiveList.getId());
		try {
			positiveList.setOwner(session.getUser());
			em.getTransaction().begin();
			if( oldPositiveList == null ) {
				User user = this.session.getUser();
				int count = user.getOwnedPositiveLists().size();
				positiveList.setName(user.getUid() + String.valueOf(count));
				positiveList.setOwner(user);
				em.persist(positiveList);
				user.getOwnedPositiveLists().add(positiveList);
				em.merge(user);
			} else {
				oldPositiveList.setDescription(positiveList.getDescription());
				oldPositiveList.setSubject(positiveList.getSubject());
				em.merge(oldPositiveList);
			}
			em.getTransaction().commit();
			String[] program   = new String[3];
			program[0] = "/usr/share/oss/tools/squidGuard.pl";
			program[1] = "writePositiveList";
			program[2] = positiveList.getName();
			StringBuffer reply = new StringBuffer();
			StringBuffer error = new StringBuffer();
			OSSShellTools.exec(program, reply, error, positiveList.getDomains());
			return new OssResponse(this.getSession(),"OK", "Postive list was created/modified succesfully.",positiveList.getId());
		} catch (Exception e) {
			logger.error("add " + e.getMessage(),e);
			return new OssResponse(this.getSession(),"ERROR", e.getMessage());
		} finally {
			em.close();
		}
	}

	public OssResponse deletePositiveList(Long positiveListId) {
		EntityManager em = getEntityManager();
		PositiveList positiveList = this.getPositiveListById(positiveListId);
		try {
			Files.deleteIfExists(Paths.get("/var/lib/squidGuard/db/PL/" + positiveList.getName() + "/domains"));
			em.getTransaction().begin();
			em.remove(positiveList);
			em.getTransaction().commit();
		}  catch (Exception e) {
			logger.error("delete " + e.getMessage(),e);
			return new OssResponse(this.getSession(),"ERROR", e.getMessage());
		} finally {
			em.close();
		}
		return new OssResponse(this.getSession(),"OK", "Postive list was deleted succesfully.");
	}

	/*
	 * Reads a positive list
	 * @param name The name of the positive list
	 * @return The domain list of the positive list
	 */
	public PositiveList getPositiveList(Long id) {
		PositiveList positiveList = this.getPositiveListById(id);
		try {
			positiveList.setDomains(
					String.join(
							this.getNl(),
							Files.readAllLines(Paths.get("/var/lib/squidGuard/db/PL/" + positiveList.getName() + "/domains"))
							)
					);
		}
		catch( IOException e ) { 
			e.printStackTrace();
		}
		return positiveList;
	}
	
	/*
	 * Reads the available positive lists
	 * @return The list of positive lists the user can use
	 */
	public List<PositiveList> getAllPositiveLists() {
		EntityManager em = getEntityManager();
		try {
			Query query = em.createNamedQuery("PositiveList.findAll"); 
			return query.getResultList();
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			return new ArrayList<>();
		} finally {
			em.close();
		}
	}

	/*
	 * Reads the available positive lists
	 * @return The list of positive lists the user can use
	 */
	public List<PositiveList> getMyPositiveLists() {
		return this.session.getUser().getOwnedPositiveLists();
	}

	public List<PositiveList> getPositiveListsInRoom(Long roomId) {
		List<PositiveList> positiveLists = new ArrayList<PositiveList>();
		Room room  = new RoomController(session).getById(roomId);
		String[] program   = new String[3];
		program[0] = "/usr/share/oss/tools/squidGuard.pl";
		program[1] = "readRoom";
		program[2] = room.getName();
		StringBuffer reply = new StringBuffer();
		StringBuffer error = new StringBuffer();
		StringBuilder acls = new StringBuilder();
		OSSShellTools.exec(program, reply, error, acls.toString());
		for( String roomName : reply.toString().split(" ") ) {
			PositiveList positiveList = this.getPositiveListByName(roomName);
			if( positiveList != null ) {
				positiveLists.add(positiveList);
			}
		}
		return positiveLists;
	}
	/*
	 * Sets positive lists in a room
	 * @param roomId The room id.
	 * @param positiveListIds list of positiveList ids which have to be set in this room
	 * @return An OssResponse object with the result
	 */
	public OssResponse setAclsInRoom(Long roomId, List<Long> positiveListIds) {

		DeviceController deviceController = new DeviceController(session);
		Room room         = new RoomController(session).getById(roomId);
		StringBuilder ips = new StringBuilder();
		for(List<Long> loggedOn : new EducationController(session).getRoom(roomId)) {
			Device device = deviceController.getById(loggedOn.get(1));
			if( device.getIp() != null && !device.getIp().isEmpty() ) {
				ips.append(device.getIp()).append(this.getNl());
			}
			if( device.getWlanIp() != null && !device.getWlanIp().isEmpty() ) {
				ips.append(device.getWlanIp()).append(this.getNl());
			}
		}
		String[] program  = new String[3];
		program[0] = "/usr/share/oss/tools/squidGuard.pl";
		program[1] = "writeIpSource";
		program[2] = room.getName();
		StringBuffer reply = new StringBuffer();
		StringBuffer error = new StringBuffer();
		OSSShellTools.exec(program, reply, error, ips.toString());
		StringBuilder acls = new StringBuilder();
		for( Long id : positiveListIds ) {
			PositiveList positiveList = this.getPositiveList(id);
			acls.append(room.getName()).append(":").append(positiveList.getName()).append(":true\n");
		}
		acls.append(room.getName()).append(":all:false\n");
		program  = new String[2];
		program[0] = "/usr/share/oss/tools/squidGuard.pl";
		program[1] = "write";
		OSSShellTools.exec(program, reply, error, acls.toString());
		return new OssResponse(this.session,"OK","Proxy Setting was saved succesfully in your room.");	
	}

	/*
	 * Removes all positive list in a room and the default settings occurs
	 * @param roomId The room id
	 */
	public OssResponse deleteAclsInRoom(Long roomId) {
		String[] program   = new String[2];
		program[0] = "/usr/share/oss/tools/squidGuard.pl";
		program[1] = "write";
		StringBuffer reply = new StringBuffer();
		StringBuffer error = new StringBuffer();
		Room room  = new RoomController(session).getById(roomId);
		String acls = room.getName() + ":remove-this-list:true\n";
		OSSShellTools.exec(program, reply, error, acls);
		return new OssResponse(this.session,"OK","Positive lists was succesfully deactivated in your room.");
	}
}
