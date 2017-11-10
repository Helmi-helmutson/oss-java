/* (c) 2017 Péter Varkoly <peter@varkoly.de> - all rights reserved */
package de.openschoolserver.dao.controller;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import de.openschoolserver.dao.Clone;
import de.openschoolserver.dao.HWConf;
import de.openschoolserver.dao.Partition;
import de.openschoolserver.dao.OssResponse;
import de.openschoolserver.dao.Session;

@SuppressWarnings( "unchecked" )
public class CloneToolController extends Controller {

	Logger logger = LoggerFactory.getLogger(CloneToolController.class);

	public CloneToolController(Session session) {
		super(session);
	}

	public Long getHWConf(){
		if( this.session.getDevice() != null ) {
			return this.session.getDevice().getHwconfId();
		} else {
			return null;
		}
	}

	public HWConf getById(Long hwconfId ) {
		EntityManager em = getEntityManager();

		try {
			return em.find(HWConf.class, hwconfId);
		} catch (Exception e) {
			logger.error(e.getMessage());
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
			logger.error(e.getMessage());
			return null;
		} finally {
			em.close();
		}
	}

	public String getConfigurationValue(Long hwconfId, String partition, String key ) {
		Partition part = this.getPartition(hwconfId, partition);
		switch (key) {
		case "DESC" :
			return part.getDescription();
		case "FORMAT" :
			return part.getFormat();
		case "ITOOL" :
			return part.getTool();
		case "JOIN" :
			return part.getJoinType();
		case "NAME" :
			return part.getName();
		case "OS" :
			return part.getOs();
		}
		return "";
	}

	public OssResponse addHWConf(HWConf hwconf){
		EntityManager em = getEntityManager();
		// First we check if the parameter are unique.
		if( ! this.isNameUnique(hwconf.getName())){
			return new OssResponse(this.getSession(),"ERROR", "Configuration name is not unique.");
		}
		try {
			hwconf.setCreator(this.session.getUser());
			em.getTransaction().begin();
			em.persist(hwconf);
			em.getTransaction().commit();
			logger.debug("Created HWConf:" + new ObjectMapper().writeValueAsString(hwconf));
		} catch (Exception e) {
			logger.error(e.getMessage());
			return new OssResponse(this.getSession(),"ERROR", e.getMessage());
		} finally {
			em.close();
		}
		return new OssResponse(this.getSession(),"OK", hwconf.getName() + " (" + hwconf.getDeviceType() + ") was created.",hwconf.getId());
	}

	public OssResponse modifyHWConf(Long hwconfId, HWConf hwconf){
		//TODO make some checks!!
		//If the name will be modified then some files must be moved too!!! TODO
		EntityManager em = getEntityManager();
		hwconf.setId(hwconfId);
		// First we check if the parameter are unique.
		if( ! this.isNameUnique(hwconf.getName())){
			return new OssResponse(this.getSession(),"ERROR", "Configuration name is not unique.");
		}
		try {
			em.getTransaction().begin();
			em.merge(hwconf);
			em.getTransaction().commit();
		} catch (Exception e) {
			logger.error(e.getMessage());
			return new OssResponse(this.getSession(),"ERROR", e.getMessage());
		} finally {
			em.close();
		}
		return new OssResponse(this.getSession(),"OK", hwconf.getName() + " (" + hwconf.getDeviceType() + ") was modified.");
	}

	public OssResponse addPartitionToHWConf(Long hwconfId, String name ) {
		EntityManager em = getEntityManager();
		HWConf hwconf = this.getById(hwconfId);
		Partition partition = new Partition();
		partition.setName(name);
		partition.setCreator(this.session.getUser());
		hwconf.addPartition(partition);
		partition.setHwconf(hwconf);
		try {
			em.getTransaction().begin();
			em.persist(partition);
			em.merge(hwconf);
			em.getTransaction().commit();			
		} catch (Exception e) {
			logger.error(e.getMessage());
			return new OssResponse(this.getSession(),"ERROR", e.getMessage());
		} finally {
			em.close();
		}
		return new OssResponse(this.getSession(),"OK", "Partition: " + name + "was created in" + hwconf.getName() + " (" + hwconf.getDeviceType() + ")");
	}
	
	public OssResponse addPartitionToHWConf(Long hwconfId, Partition partition ) {
		EntityManager em = getEntityManager();
		HWConf hwconf = this.getById(hwconfId);
		partition.setCreator(this.session.getUser());
		hwconf.addPartition(partition);
		// First we check if the parameter are unique.
		try {
			em.getTransaction().begin();
			em.merge(hwconf);
			em.getTransaction().commit();			
		} catch (Exception e) {
			logger.error(e.getMessage());
			return new OssResponse(this.getSession(),"ERROR", e.getMessage());
		} finally {
			em.close();
		}
		return new OssResponse(this.getSession(),"OK", "Partition: " + partition.getName() + " was created in " + hwconf.getName() + " (" + hwconf.getDeviceType() + ")");
	}


	public OssResponse setConfigurationValue(Long hwconfId, String partitionName, String key, String value) {
		Partition partition = this.getPartition(hwconfId, partitionName);
		if(partition == null ) {
			this.addPartitionToHWConf(hwconfId, partitionName);
			partition = this.getPartition(hwconfId, partitionName);
			if( partition == null ) {
				return new OssResponse(this.getSession(),"ERROR", "Can not create partition in HWConf");
			}
			logger.debug("Creating partition '" + partitionName + "' in hwconf #" +hwconfId );
		}
		switch (key) {
		case "DESC" :
			partition.setDescription(value);
			break;
		case "FORMAT" :
			partition.setFormat(value);
			break;
		case "ITOOL" :
			partition.setTool(value);
			break;
		case "JOIN" :
			partition.setJoinType(value);
			break;
		case "NAME" :
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
			logger.error(e.getMessage());
			return new OssResponse(this.getSession(),"ERROR", e.getMessage());
		} finally {
			em.close();
		}
		return new OssResponse(this.getSession(),"OK", "Partitions key: " +  key + " was set to " + value );
	}
	
	public OssResponse delete(Long hwconfId){
		EntityManager em = getEntityManager();
		try {
			em.getTransaction().begin();
			HWConf hwconf = this.getById(hwconfId);
	        if( this.isProtected(hwconf)) {
	            return new OssResponse(this.getSession(),"ERROR","This hardware configuration must not be deleted.");
	        }
	        if( !this.mayModify(hwconf) ) {
	        	return new OssResponse(this.getSession(),"ERROR","You must not delete this hardware configuration.");
	        }
			if( ! em.contains(hwconf)) {
				hwconf = em.merge(hwconf);
			}
			em.remove(hwconf);
			em.getTransaction().commit();
			em.getEntityManagerFactory().getCache().evictAll();
		} catch (Exception e) {
			logger.error(e.getMessage());
			return new OssResponse(this.getSession(),"ERROR", e.getMessage());
		} finally {
			em.close();
		}
		return new OssResponse(this.getSession(),"OK", "Hardware configuration was deleted succesfully.");
	}

	public OssResponse deletePartition(Long hwconfId, String partitionName) {
		HWConf hwconf = this.getById(hwconfId);
		Partition partition = this.getPartition(hwconfId, partitionName);
		if( !this.mayModify(partition) ) {
        	return new OssResponse(this.getSession(),"ERROR","You must not delete this partition.");
        }
		hwconf.removePartition(partition);
		EntityManager em = getEntityManager();
		try {
			em.getTransaction().begin();
			em.merge(hwconf);
			em.getTransaction().commit();
			em.getEntityManagerFactory().getCache().evict(hwconf.getClass());
		} catch (Exception e) {
			logger.error(e.getMessage());
			return new OssResponse(this.getSession(),"ERROR", e.getMessage());
		} finally {
			em.close();
		}
		return new OssResponse(this.getSession(),"OK", "Partition: " + partitionName + " was deleted from " + hwconf.getName() + " (" + hwconf.getDeviceType() + ")");
	}

	public OssResponse deleteConfigurationValue(Long hwconfId, String partitionName, String key) {
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
			logger.error(e.getMessage());
			return new OssResponse(this.getSession(),"ERROR", e.getMessage());
		} finally {
			em.close();
		}
		return new OssResponse(this.getSession(),"OK", "Partitions key: " +  key + " was deleted" );
	}

	public List<HWConf> getAllHWConf() {
		EntityManager em = getEntityManager();
		try {
			Query query = em.createNamedQuery("HWConf.findAll");
			return (List<HWConf>) query.getResultList();
		} catch (Exception e) {
			logger.error(e.getMessage());
			return null;
		} finally {
			em.close();
		}
	}

	public OssResponse startCloning(Long hwconfId, Clone parameters) {
		List<String> partitions = new ArrayList<String>();
		for( Long partitionIds : parameters.getPartitionIds() ) {
			
		}
		String parts = String.join(",",partitions);
		
		for( Long deviceId : parameters.getDeviceIds() ) {
			
		}
		return null;
	}

	public OssResponse stopCloning(Long hwconfId) {
		// TODO Auto-generated method stub
		return null;
	}
}
