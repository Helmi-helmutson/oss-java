/* (c) 2017 Péter Varkoly <peter@varkoly.de> - all rights reserved */
package de.openschoolserver.api.resourceimpl;

import de.openschoolserver.dao.HWConf;

import de.openschoolserver.dao.Partition;
import de.openschoolserver.dao.Response;
import de.openschoolserver.dao.Session;
import de.openschoolserver.dao.controller.CloneToolController;
import de.openschoolserver.dao.DeviceConfig;
import de.openschoolserver.api.resources.CloneToolResource;


import javax.ws.rs.WebApplicationException;
import java.util.List;

public class CloneToolResourceImpl implements CloneToolResource {

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
	public String isMaster(Session session) {
		if( session.getDevice() == null )
			return "";
		for( DeviceConfig dc : session.getDevice().getDeviceConfigs() )
		{
			if( dc.getKeyword().equals("isMaster") && dc.getValue().equals("Y") )
				return "true";
		}
		return "";
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
	public String getDescription(Session session, Long hwconfId) {
		final CloneToolController cloneToolController = new CloneToolController(session);
		return cloneToolController.getDescription(hwconfId);
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
	public Response addHWConf(Session session, HWConf hwconf) {
		final CloneToolController cloneToolController = new CloneToolController(session);
		return cloneToolController.addHWConf(hwconf);
	}

	@Override
	public Response modifyHWConf(Session session, Long hwconfId, HWConf hwconf) {
		final CloneToolController cloneToolController = new CloneToolController(session);
		return cloneToolController.modifyHWConf(hwconfId, hwconf);
	}

	@Override
	public Response addPartition(Session session, Long hwconfId, Partition partition) {
		// TODO Auto-generated method stub
		final CloneToolController cloneToolController = new CloneToolController(session);
		return cloneToolController.addPartitionToHWConf(hwconfId, partition);
	}

	@Override
	public Response addPartition(Session session, Long hwconfId, String partitionName) {
		// TODO Auto-generated method stub
		final CloneToolController cloneToolController = new CloneToolController(session);
		return cloneToolController.addPartitionToHWConf(hwconfId, partitionName );
	}
	
	@Override
	public Response setConfigurationValue(Session session, Long hwconfId, String partitionName, String key, String value) {
		final CloneToolController cloneToolController = new CloneToolController(session);
		return cloneToolController.setConfigurationValue(hwconfId,partitionName,key,value);
	}

	@Override
	public Response delete(Session session, Long hwconfId) {
		// TODO Auto-generated method stub
		final CloneToolController cloneToolController = new CloneToolController(session);
		return cloneToolController.delete(hwconfId);
	}

	@Override
	public Response deletePartition(Session session, Long hwconfId, String partitionName) {
		final CloneToolController cloneToolController = new CloneToolController(session);
		return cloneToolController.deletePartition(hwconfId,partitionName);
	}

	@Override
	public Response deleteConfigurationValue(Session session, Long hwconfId, String partitionName, String key) {
		final CloneToolController cloneToolController = new CloneToolController(session);
		return cloneToolController.deleteConfigurationValue(hwconfId,partitionName,key);
	}

	@Override
	public List<HWConf> getAllHWConf(Session session) {
		// TODO Auto-generated method stub
		final CloneToolController cloneToolController = new CloneToolController(session);
		return cloneToolController.getAllHWConf();
	}
}
