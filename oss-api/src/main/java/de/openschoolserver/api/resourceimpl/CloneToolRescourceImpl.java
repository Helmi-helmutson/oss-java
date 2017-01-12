/* (c) 2017 Péter Varkoly <peter@varkoly.de> - all rights reserved */
package de.openschoolserver.api.resourceimpl;

import de.openschoolserver.dao.HWConf;

import de.openschoolserver.dao.Partition;
import de.openschoolserver.dao.Session;
import de.openschoolserver.dao.controller.CloneToolController;
import de.openschoolserver.api.resources.CloneToolResource;


import javax.ws.rs.WebApplicationException;
import java.util.List;
import java.util.Map;

public class CloneToolRescourceImpl implements CloneToolResource {


	@Override
	public Long getHWConf(Session session) {
		final CloneToolController cloneToolController = new CloneToolController(session);
		final Long  hwconf = cloneToolController.getHWConf();
		if (hwconf == null) {
			throw new WebApplicationException(404);
		}
		return hwconf;
	}

	@Override
	public HWConf getById(Session session, Long hwconfId) {
		final CloneToolController cloneToolController = new CloneToolController(session);
		final HWConf hwconf = cloneToolController.getById(hwconfId);
		if (hwconf == null) {
			throw new WebApplicationException(404);
		}
		return hwconf;
	}

	@Override
	public String getPartitions(Session session, Long hwconfId) {
		final CloneToolController cloneToolController = new CloneToolController(session);
		return cloneToolController.getPartitions(hwconfId);
	}

	@Override
	public Partition getPartition(Session session, Long hwconfId, String partition) {
		final CloneToolController cloneToolController = new CloneToolController(session);
		return cloneToolController.getPartition(hwconfId, partition);
	}

	@Override
	public String getConfigurationValue(Session session, Long hwconfId, String partition, String key) {
		final CloneToolController cloneToolController = new CloneToolController(session);
		return cloneToolController.getConfigurationValue(hwconfId,partition,key);
	}

	@Override
	public boolean addHWConf(Session session, HWConf hwconf) {
		final CloneToolController cloneToolController = new CloneToolController(session);
		return cloneToolController.addHWConf(hwconf);
	}

	@Override
	public boolean modifyHWConf(Session session, Long hwconfId, HWConf hwconf) {
		final CloneToolController cloneToolController = new CloneToolController(session);
		return cloneToolController.modifyHWConf(hwconfId, hwconf);
	}

	@Override
	public boolean addPartition(Session session, Long hwconfId, String partitionName, Partition partition) {
		// TODO Auto-generated method stub
		final CloneToolController cloneToolController = new CloneToolController(session);
		partition.setName(partitionName);
		return cloneToolController.addPartitionToHWConf(hwconfId, partition);
	}

	@Override
	public boolean addPartition(Session session, Long hwconfId, String partitionName) {
		// TODO Auto-generated method stub
		final CloneToolController cloneToolController = new CloneToolController(session);
		return cloneToolController.addPartitionToHWConf(hwconfId, partitionName );
	}
	
	@Override
	public boolean setConfigurationValue(Session session, Long hwconfId, String partition, String key, String value) {
		final CloneToolController cloneToolController = new CloneToolController(session);
		return cloneToolController.setConfigurationValue(hwconfId,partition,key,value);
	}

	@Override
	public boolean delete(Session session, Long hwconfId) {
		// TODO Auto-generated method stub
		final CloneToolController cloneToolController = new CloneToolController(session);
		return cloneToolController.delete(hwconfId);
	}
}
