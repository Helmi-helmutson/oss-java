/* (c) 2017 Péter Varkoly <peter@varkoly.de> - all rights reserved */
package de.openschoolserver.dao.controller;
import java.util.ArrayList;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import de.openschoolserver.dao.Device;
import de.openschoolserver.dao.HWConf;
import de.openschoolserver.dao.Partition;
import de.openschoolserver.dao.Response;
import de.openschoolserver.dao.Session;

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

	public String getDescription(Long hwconfId ) {
		return this.getById(hwconfId).getName();
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
			return part.getTool();
		case "Join" :
			return part.getJoinType();
		case "Name" :
			return part.getName();
		case "OS" :
			return part.getOs();
		}
		return "";
	}

	public Response addHWConf(HWConf hwconf){
		EntityManager em = getEntityManager();
		// First we check if the parameter are unique.
		if( ! this.isNameUnique(hwconf.getName())){
			return new Response(this.getSession(),"ERROR", "Configuration name is not unique.");
		}
		try {
			em.getTransaction().begin();
			em.persist(hwconf);
			em.getTransaction().commit();
		} catch (Exception e) {
			System.err.println(e.getMessage());
			return new Response(this.getSession(),"ERROR", e.getMessage());
		} finally {
			em.close();
		}
		return new Response(this.getSession(),"OK", hwconf.getName() + " (" + hwconf.getDeviceType() + ") was created.");
	}

	public Response modifyHWConf(Long hwconfId, HWConf hwconf){
		//TODO make some checks!!
		EntityManager em = getEntityManager();
		hwconf.setId(hwconfId);
		// First we check if the parameter are unique.
		if( ! this.isNameUnique(hwconf.getName())){
			return new Response(this.getSession(),"ERROR", "Configuration name is not unique.");
		}
		try {
			em.getTransaction().begin();
			em.merge(hwconf);
			em.getTransaction().commit();
		} catch (Exception e) {
			System.err.println(e.getMessage());
			return new Response(this.getSession(),"ERROR", e.getMessage());
		} finally {
			em.close();
		}
		return new Response(this.getSession(),"OK", hwconf.getName() + " (" + hwconf.getDeviceType() + ") was modified.");
	}

	public Response addPartitionToHWConf(Long hwconfId, String name ) {
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
			return new Response(this.getSession(),"ERROR", e.getMessage());
		} finally {
			em.close();
		}
		return new Response(this.getSession(),"OK", "Partition: " + name + "was created in" + hwconf.getName() + " (" + hwconf.getDeviceType() + ")");
	}
	
	public Response addPartitionToHWConf(Long hwconfId, Partition partition ) {
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
			return new Response(this.getSession(),"ERROR", e.getMessage());
		} finally {
			em.close();
		}
		return new Response(this.getSession(),"OK", "Partition: " + partition.getName() + " was created in " + hwconf.getName() + " (" + hwconf.getDeviceType() + ")");
	}


	public Response setConfigurationValue(Long hwconfId, String partitionName, String key, String value) {
		// TODO Auto-generated method stub
		Partition partition = this.getPartition(hwconfId, partitionName);
		switch (key) {
		case "Description" :
			partition.setDescription(value);
			break;
		case "Format" :
			partition.setFormat(value);
			break;
		case "ITool" :
			partition.setTool(value);
			break;
		case "Join" :
			partition.setJoinType(value);
			break;
		case "Name" :
			partition.setName(value);
			break;
		case "OS" :
			partition.setOs(value);
		}
		EntityManager em = getEntityManager();
		try {
			em.getTransaction().begin();
			em.merge(partition);
			em.getTransaction().commit();			
		} catch (Exception e) {
			System.err.println(e.getMessage());
			return new Response(this.getSession(),"ERROR", e.getMessage());
		} finally {
			em.close();
		}
		return new Response(this.getSession(),"OK", "Partitions key: " +  key + " was set to " + value );
	}
	
	public Response delete(Long hwconfId){
		EntityManager em = getEntityManager();
		try {
			em.getTransaction().begin();
			HWConf hwconf = this.getById(hwconfId);
			em.remove(hwconf);
			em.getTransaction().commit();
		} catch (Exception e) {
			System.err.println(e.getMessage());
			return new Response(this.getSession(),"ERROR", e.getMessage());
		} finally {
			em.close();
		}
		return new Response(this.getSession(),"OK", "Hardware configuration was deleted succesfully.");
	}

	public Response deletePartition(Long hwconfId, String partitionName) {
		// TODO Auto-generated method stub
		HWConf hwconf = this.getById(hwconfId);
		Partition partition = this.getPartition(hwconfId, partitionName);
		hwconf.removePartition(partition);
		EntityManager em = getEntityManager();
		try {
			em.getTransaction().begin();
			em.merge(hwconf);
			em.getTransaction().commit();
		} catch (Exception e) {
			System.err.println(e.getMessage());
			return new Response(this.getSession(),"ERROR", e.getMessage());
		} finally {
			em.close();
		}
		return new Response(this.getSession(),"OK", "Partition: " + partitionName + " was deleted from " + hwconf.getName() + " (" + hwconf.getDeviceType() + ")");
	}

	public Response deleteConfigurationValue(Long hwconfId, String partitionName, String key) {
		// TODO Auto-generated method stub
		Partition partition = this.getPartition(hwconfId, partitionName);
		switch (key) {
		case "Description" :
			partition.setDescription("");
			break;
		case "Format" :
			partition.setFormat("");
			break;
		case "ITool" :
			partition.setTool("");
			break;
		case "Join" :
			partition.setJoinType("");
			break;
		case "Name" :
			partition.setName("");
			break;
		case "OS" :
			partition.setOs("");
			break;
		}
		EntityManager em = getEntityManager();
		try {
			em.getTransaction().begin();
			em.merge(partition);
			em.getTransaction().commit();
		} catch (Exception e) {
			System.err.println(e.getMessage());
			return new Response(this.getSession(),"ERROR", e.getMessage());
		} finally {
			em.close();
		}
		return new Response(this.getSession(),"OK", "Partitions key: " +  key + " was deleted" );
	}

	public List<HWConf> getAllHWConf() {
		EntityManager em = getEntityManager();
		try {
			Query query = em.createNamedQuery("HWConf.findAll");
			return (List<HWConf>) query.getResultList();
		} catch (Exception e) {
			System.err.println(e.getMessage()); //TODO
			return null;
		} finally {
			em.close();
		}
	}
}
