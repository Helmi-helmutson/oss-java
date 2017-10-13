/* (c) 2017 Péter Varkoly <peter@varkoly.de> - all rights reserved */
package de.openschoolserver.api.resourceimpl;

import de.openschoolserver.dao.HWConf;


import de.openschoolserver.dao.Clone;
import de.openschoolserver.dao.Partition;
import de.openschoolserver.dao.OssResponse;
import de.openschoolserver.dao.Session;
import de.openschoolserver.dao.controller.CloneToolController;
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
		if( session.getDevice() == null ) {
			return "";
		}
		final CloneToolController cloneToolController = new CloneToolController(session);
		if( cloneToolController.checkConfig(session.getDevice(),"isMaster" ) ) {
			return "true";
		}
		return "";
	}

	@Override
	public HWConf getById(Session session, Long hwconfId) {
		final HWConf hwconf = new CloneToolController(session).getById(hwconfId);
		if (hwconf == null) {
			throw new WebApplicationException(404);
		}
		return hwconf;
	}

	@Override
	public String getPartitions(Session session, Long hwconfId) {
		return new CloneToolController(session).getPartitions(hwconfId);
	}
	
	@Override
	public String getDescription(Session session, Long hwconfId) {
		return new CloneToolController(session).getDescription(hwconfId);
	}

	@Override
	public Partition getPartition(Session session, Long hwconfId, String partition) {
		return new CloneToolController(session).getPartition(hwconfId, partition);
	}

	@Override
	public String getConfigurationValue(Session session, Long hwconfId, String partition, String key) {
		return new CloneToolController(session).getConfigurationValue(hwconfId,partition,key);
	}

	@Override
	public OssResponse addHWConf(Session session, HWConf hwconf) {
		return new CloneToolController(session).addHWConf(hwconf);
	}

	@Override
	public OssResponse modifyHWConf(Session session, Long hwconfId, HWConf hwconf) {
		return new CloneToolController(session).modifyHWConf(hwconfId, hwconf);
	}

	@Override
	public OssResponse addPartition(Session session, Long hwconfId, Partition partition) {
		return new CloneToolController(session).addPartitionToHWConf(hwconfId, partition);
	}

	@Override
	public OssResponse addPartition(Session session, Long hwconfId, String partitionName) {
		return new CloneToolController(session).addPartitionToHWConf(hwconfId, partitionName );
	}
	
	@Override
	public OssResponse setConfigurationValue(Session session, Long hwconfId, String partitionName, String key, String value) {
		return new CloneToolController(session).setConfigurationValue(hwconfId,partitionName,key,value);
	}

	@Override
	public OssResponse delete(Session session, Long hwconfId) {
		return new CloneToolController(session).delete(hwconfId);
	}

	@Override
	public OssResponse deletePartition(Session session, Long hwconfId, String partitionName) {
		return new CloneToolController(session).deletePartition(hwconfId,partitionName);
	}

	@Override
	public OssResponse deleteConfigurationValue(Session session, Long hwconfId, String partitionName, String key) {
		return new CloneToolController(session).deleteConfigurationValue(hwconfId,partitionName,key);
	}

	@Override
	public List<HWConf> getAllHWConf(Session session) {
		return new CloneToolController(session).getAllHWConf();
	}

	@Override
	public OssResponse startCloning(Session session, Long hwconfId, Clone parameters) {
		return new CloneToolController(session).startCloning(hwconfId,parameters);
	}

	@Override
	public OssResponse stopCloning(Session session, Long hwconfId) {
		return new CloneToolController(session).stopCloning(hwconfId);
	}
}
