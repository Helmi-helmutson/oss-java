/* (c) 2017 Péter Varkoly <peter@varkoly.de> - all rights reserved */
package de.openschoolserver.dao.controller;
import java.util.ArrayList;


import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import de.openschoolserver.dao.Device;
import de.openschoolserver.dao.HWConf;
import de.openschoolserver.dao.Session;
import de.openschoolserver.dao.User;
import de.openschoolserver.dao.Partition;
import de.openschoolserver.dao.tools.*;

public class CloneToolController extends Controller {

	public CloneToolController(Session session) {
		super(session);
	}

	public Long getHWConf(){
		DeviceController devController = new DeviceController(this.getSession());
		Device device = devController.getByIP(this.getSession().getIP());
		return device.getHwconf().getId();
	}

	public HWConf getById(Long hwconfId ) {
		EntityManager em = getEntityManager();

		try {
			return em.find(HWConf.class, hwconfId);
		} catch (Exception e) {
			// logger.error(e.getMessage());
			System.err.println(e.getMessage()); //TODO
			return null;
		} finally {
			em.close();
		}
	}

	public String getPartitions(Long hwconfId ) {
		List<String> partitions = new ArrayList<String>();
		for( Partition part : this.getById(hwconfId).getPartitions() ) {
			partitions.add(part.getName());	
		}
		return String.join(" ", partitions );
	}

	public Partition getPartition(Long hwconfId, String partition) {
		EntityManager em = getEntityManager();
		try {
			Query query = em.createNamedQuery("Partition.getPartitionByName");
			query.setParameter("hwconfId", hwconfId).setParameter("name",partition);
			return (Partition) query.getSingleResult();
		} catch (Exception e) {
			//logger.error(e.getMessage());
			System.err.println(e.getMessage()); //TODO
			return null;
		} finally {
			em.close();
		}
	}

	public String getConfigurationValue(Long hwconfId, String partition, String key ) {
		Partition part = this.getPartition(hwconfId, partition);
		switch (key) {
		case "Description" :
			return part.getDescription();
		case "Format" :
			return part.getFormat();
		case "ITool" :
			return part.getOs();
		case "Join" :
			return part.getFormat();
		case "Name" :
			return part.getFormat();
		case "OS" :
			return part.getOs();
		}
		return "";
	}

	public boolean addHWConf(HWConf hwconf){
		EntityManager em = getEntityManager();
		// First we check if the parameter are unique.
		if( ! this.isNameUnique(hwconf.getName())){
			return false;
		}
		try {
			em.getTransaction().begin();
			em.persist(hwconf);
			em.getTransaction().commit();
		} catch (Exception e) {
			System.err.println(e.getMessage());
			return false;
		}
		return true;
	}

	public boolean modifyHWConf(Long hwconfId, HWConf hwconf){
		//TODO make some checks!!
		EntityManager em = getEntityManager();
		hwconf.setId(hwconfId);
		// First we check if the parameter are unique.
		try {
			em.getTransaction().begin();
			em.merge(hwconf);
			em.getTransaction().commit();
		} catch (Exception e) {
			System.err.println(e.getMessage());
			return false;
		}
		return true;
	}

	public boolean addPartitionToHWConf(Long hwconfId, String name ) {
		Partition partition = new Partition(name);
		partition.setHwconf(this.getById(hwconfId));
		EntityManager em = getEntityManager();
		// First we check if the parameter are unique.
		try {
			em.getTransaction().begin();
			em.persist(partition);
			em.getTransaction().commit();
		} catch (Exception e) {
			System.err.println(e.getMessage());
			return false;
		}
		return true;
	}
	
	public boolean addPartitionToHWConf(Long hwconfId, Partition partition ) {
		partition.setHwconf(this.getById(hwconfId));
		EntityManager em = getEntityManager();
		// First we check if the parameter are unique.
		try {
			em.getTransaction().begin();
			em.persist(partition);
			em.getTransaction().commit();
		} catch (Exception e) {
			System.err.println(e.getMessage());
			return false;
		}
		return true;
	}


	public boolean setConfigurationValue(Long hwconfId, String partition, String key, String value) {
		// TODO Auto-generated method stub
		Partition part = this.getPartition(hwconfId, partition);
		switch (key) {
		case "Description" :
			part.setDescription(value);
		case "Format" :
			part.setFormat(value);
		case "ITool" :
			part.setOs(value);
		case "Join" :
			part.setFormat(value);
		case "Name" :
			part.setFormat(value);
		case "OS" :
			part.setOs(value);
		}
		EntityManager em = getEntityManager();
		em.getTransaction().begin();
		em.merge(partition);
		em.getTransaction().commit();
		return false;
	}
}
