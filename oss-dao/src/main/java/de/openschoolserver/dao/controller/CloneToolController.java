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
import de.openschoolserver.dao.tools.*;

public class CloneToolController extends Controller {

        public CloneToolController(Session session) {
                super(session);
        }

	public Long getHWConf(){
		DeviceController devController = new DeviceController(this.getSession);
		Device device = devController.getByIP(session.IP);
		return device.getHWConf().getId();
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
		for( Partition part : this.getById(hwconf).partitions ){
			partitions.add(part.getName();	
		}
		return partitions.join(" ");
	}

	public String getConfigurationValue(Long hwconfId, String partition, String key ) {
		EntityManager em = getEntityManager();
		try {
                        Query query = em.createNamedQuery("Partition.getPartitionByName");
                        query.setParameter("hwconfId", hwconfId).setParameter("name",partition);
                        return query.getSingleResult();
                } catch (Exception e) {
                        //logger.error(e.getMessage());
                        System.err.println(e.getMessage()); //TODO
                        return new ArrayList<>();
                } finally {
                        em.close();
                }
	}
}
