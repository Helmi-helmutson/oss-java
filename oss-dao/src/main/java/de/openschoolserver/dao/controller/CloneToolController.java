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
		EntityManager em = getEntityManager();
		HWConf hwconf = this.getById(hwconfId);
		Partition partition = new Partition();
		partition.setName(name);
		hwconf.addPartition(partition);
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
	
	public boolean addPartitionToHWConf(Long hwconfId, Partition partition ) {
		EntityManager em = getEntityManager();
		HWConf hwconf = this.getById(hwconfId);
		hwconf.addPartition(partition);
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


	public boolean setConfigurationValue(Long hwconfId, String partitionName, String key, String value) {
		// TODO Auto-generated method stub
		Partition partition = this.getPartition(hwconfId, partitionName);
		switch (key) {
		case "Description" :
			partition.setDescription(value);
		case "Format" :
			partition.setFormat(value);
		case "ITool" :
			partition.setOs(value);
		case "Join" :
			partition.setFormat(value);
		case "Name" :
			partition.setFormat(value);
		case "OS" :
			partition.setOs(value);
		}
		EntityManager em = getEntityManager();
		em.getTransaction().begin();
		em.merge(partition);
		em.getTransaction().commit();
		return true;
	}
	
	public boolean delete(Long hwconfId){
		EntityManager em = getEntityManager();
		em.getTransaction().begin();
		HWConf hwconf = this.getById(hwconfId);
		em.remove(hwconf);
		em.getTransaction().commit();
		return true;
	}

	public boolean deletePartition(Long hwconfId, String partitionName) {
		// TODO Auto-generated method stub
		Partition partition = this.getPartition(hwconfId, partitionName);
		EntityManager em = getEntityManager();
		em.getTransaction().begin();
		em.remove(partition);
		em.getTransaction().commit();
		return true;
	}

	public boolean deleteConfigurationValue(Long hwconfId, String partitionName, String key) {
		// TODO Auto-generated method stub
		Partition partition = this.getPartition(hwconfId, partitionName);
		switch (key) {
		case "Description" :
			partition.setDescription("");
		case "Format" :
			partition.setFormat("");
		case "ITool" :
			partition.setOs("");
		case "Join" :
			partition.setFormat("");
		case "Name" :
			partition.setFormat("");
		case "OS" :
			partition.setOs("");
		}
		EntityManager em = getEntityManager();
		em.getTransaction().begin();
		em.merge(partition);
		em.getTransaction().commit();
		return true;
	}
	
	
}
