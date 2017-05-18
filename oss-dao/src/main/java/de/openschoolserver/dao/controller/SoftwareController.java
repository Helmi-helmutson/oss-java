package de.openschoolserver.dao.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.openschoolserver.dao.*;

public class SoftwareController extends Controller {
	
	Logger logger = LoggerFactory.getLogger(CloneToolController.class);

	public SoftwareController(Session session) {
		super(session);
	}
	
	public Map<String, String> statistic() {
		Map<String,String> statusMap = new HashMap<>();
		statusMap.put("name","software");
		EntityManager em = getEntityManager();
        Query query;
        Integer count;

        query = em.createNamedQuery("SoftwareStatus.findByStatus").setParameter("STATUS","installed");
        count = query.getResultList().size();
        statusMap.put("installed", count.toString());

        query = em.createNamedQuery("SoftwareStatus.findByStatus").setParameter("STATUS","installation_scheduled");
        count = query.getResultList().size();
        statusMap.put("installation_scheduled", count.toString());
        
        query = em.createNamedQuery("SoftwareStatus.findByStatus").setParameter("STATUS","installation_failed");
        count = query.getResultList().size();
        statusMap.put("installation_failed", count.toString());
        
        query = em.createNamedQuery("SoftwareStatus.findByStatus").setParameter("STATUS","installed_manuell");
        count = query.getResultList().size();
        statusMap.put("installed_manuell", count.toString());
        
        query = em.createNamedQuery("SoftwareStatus.findByStatus").setParameter("STATUS","deinstalled_manuell");
        count = query.getResultList().size();
        statusMap.put("deinstalled_manuell", count.toString());
        
        return statusMap;
	}

	public List<Software> getAll() {
		EntityManager em = getEntityManager();
		Query query = em.createNamedQuery("Software.findAll");
		return (List<Software>)query.getResultList();
	}

	public List<SoftwareVersion> getAllVersion() {
		EntityManager em = getEntityManager();
		Query query = em.createNamedQuery("SoftwareVersion.findAll");
		return (List<SoftwareVersion>)query.getResultList();
	}
	
	public List<SoftwareStatus> getAllStatus(String installationStatus) {
		EntityManager em = getEntityManager();
        Query query = em.createNamedQuery("SoftwareStatus.findByStatus").setParameter("STATUS",installationStatus);
        return (List<SoftwareStatus>) query.getResultList();
	}
}
