/* (c) 2017 Péter Varkoly <peter@varkoly.de> - all rights reserved */
package de.openschoolserver.dao.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import de.openschoolserver.dao.Device;
import de.openschoolserver.dao.OssResponse;
import de.openschoolserver.dao.PositiveList;
import de.openschoolserver.dao.Session;
import de.openschoolserver.dao.tools.OSSShellTools;

@SuppressWarnings( "unchecked" )
public class ProxyController extends Controller {

	public ProxyController(Session session) {
		super(session);
	}
	
	/*
	 * Reads the default proxy acl setting
	 * @return The default proxy acl setting
	 */
	public Map<String,List<String[]>> readDefaults() {
		Map<String,List<String[]>> acls = new HashMap<>();
		String[] program   = new String[2];
		program[0] = "/usr/share/oss/tools/squidGuard.pl";
		program[1] = "read";
		StringBuffer reply = new StringBuffer();
		StringBuffer error = new StringBuffer();
		OSSShellTools.exec(program, reply, error, "");
		for( String line : reply.toString().split("\\n") ) {
			String[] values = line.split(" ");
			List<String[]> settings = new ArrayList<String[]>();
			for(int i=1; i < values.length; i++ ) {
				settings.add(values[i].split(":"));
			}
			acls.put(values[0], settings);
		}
		return acls;
	}

	/*
	 * Writes the default proxy setting
	 * @param acls The list of the default acl setting
	 * @return An OssResponse object with the result
	 */
	public OssResponse setDefaults(Map<String,List<String[]>> acls) {
		StringBuilder output = new StringBuilder();
		for(String group : acls.keySet() ) {
			for( String[] acl : acls.get(group) ) {
				output.append(group).append(":").append(acl[0]).append(":").append(acl[1]);
			}
		}
		String[] program   = new String[2];
		program[0] = "/usr/share/oss/tools/squidGuard.pl";
		program[1] = "write";
		StringBuffer reply = new StringBuffer();
		StringBuffer error = new StringBuffer();
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

	/*
	 * Creates or modify a positive list
	 * @param positiveList The positive list to be saved
	 * @return An OssResponse object with the result
	 */
	public OssResponse editPositiveList(PositiveList positiveList) {
		EntityManager em = getEntityManager();
		PositiveList oldPositiveList= this.getPositiveListById(positiveList.getId());
		
		try {
			positiveList.setOwner(session.getUser());
			em.getTransaction().begin();
			if( oldPositiveList == null ) {
				em.persist(positiveList);
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

	/*
	 * Reads a positive list
	 * @param name The name of the positive list
	 * @return The domain list of the positive list
	 */
	public List<String> getPositiveList(String name) {
		List<String> domains = new ArrayList<String>();
		//TODO
		return domains;
	}
	
	/*
	 * Reads the available positive lists
	 * @return The list of positive lists the user can use
	 */
	public List<PositiveList> getAllPositiveList() {
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
	 * Sets the acls in a room
	 * @param roomId The room id.
	 * @param acls The acls which have to be set in this room
	 * @return An OssResponse object with the result
	 */
	public OssResponse setAclsInRoom(Long roomId,List<String[]> acls) {
		//TODO
		return new OssResponse(this.session,"OK","Proxy Setting was saved succesfully in your room.");	
	}

}
