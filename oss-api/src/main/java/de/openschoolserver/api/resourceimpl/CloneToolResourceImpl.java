/* (c) 2017 Péter Varkoly <peter@varkoly.de> - all rights reserved */
package de.openschoolserver.api.resourceimpl;

import de.openschoolserver.dao.HWConf;

import de.openschoolserver.dao.Partition;
import de.openschoolserver.dao.Response;
import de.openschoolserver.dao.Session;
import de.openschoolserver.dao.controler.CloneToolControler;
import de.openschoolserver.dao.DeviceConfig;
import de.openschoolserver.api.resources.CloneToolResource;


import javax.ws.rs.WebApplicationException;
import java.util.List;

public class CloneToolResourceImpl implements CloneToolResource {

	@Override
	public Long getHWConf(Session session) {
		final CloneToolControler cloneToolControler = new CloneToolControler(session);
		final Long  hwconf = cloneToolControler.getHWConf();
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
		final CloneToolControler cloneToolControler = new CloneToolControler(session);
		final HWConf hwconf = cloneToolControler.getById(hwconfId);
		if (hwconf == null) {
			throw new WebApplicationException(404);
		}
		return hwconf;
	}

	@Override
	public String getPartitions(Session session, Long hwconfId) {
		final CloneToolControler cloneToolControler = new CloneToolControler(session);
		return cloneToolControler.getPartitions(hwconfId);
	}
	
	@Override
	public String getDescription(Session session, Long hwconfId) {
		final CloneToolControler cloneToolControler = new CloneToolControler(session);
		return cloneToolControler.getDescription(hwconfId);
	}

	@Override
	public Partition getPartition(Session session, Long hwconfId, String partition) {
		final CloneToolControler cloneToolControler = new CloneToolControler(session);
		return cloneToolControler.getPartition(hwconfId, partition);
	}

	@Override
	public String getConfigurationValue(Session session, Long hwconfId, String partition, String key) {
		final CloneToolControler cloneToolControler = new CloneToolControler(session);
		return cloneToolControler.getConfigurationValue(hwconfId,partition,key);
	}

	@Override
	public Response addHWConf(Session session, HWConf hwconf) {
		final CloneToolControler cloneToolControler = new CloneToolControler(session);
		return cloneToolControler.addHWConf(hwconf);
	}

	@Override
	public Response modifyHWConf(Session session, Long hwconfId, HWConf hwconf) {
		final CloneToolControler cloneToolControler = new CloneToolControler(session);
		return cloneToolControler.modifyHWConf(hwconfId, hwconf);
	}

	@Override
	public Response addPartition(Session session, Long hwconfId, Partition partition) {
		// TODO Auto-generated method stub
		final CloneToolControler cloneToolControler = new CloneToolControler(session);
		return cloneToolControler.addPartitionToHWConf(hwconfId, partition);
	}

	@Override
	public Response addPartition(Session session, Long hwconfId, String partitionName) {
		// TODO Auto-generated method stub
		final CloneToolControler cloneToolControler = new CloneToolControler(session);
		return cloneToolControler.addPartitionToHWConf(hwconfId, partitionName );
	}
	
	@Override
	public Response setConfigurationValue(Session session, Long hwconfId, String partitionName, String key, String value) {
		final CloneToolControler cloneToolControler = new CloneToolControler(session);
		return cloneToolControler.setConfigurationValue(hwconfId,partitionName,key,value);
	}

	@Override
	public Response delete(Session session, Long hwconfId) {
		// TODO Auto-generated method stub
		final CloneToolControler cloneToolControler = new CloneToolControler(session);
		return cloneToolControler.delete(hwconfId);
	}

	@Override
	public Response deletePartition(Session session, Long hwconfId, String partitionName) {
		final CloneToolControler cloneToolControler = new CloneToolControler(session);
		return cloneToolControler.deletePartition(hwconfId,partitionName);
	}

	@Override
	public Response deleteConfigurationValue(Session session, Long hwconfId, String partitionName, String key) {
		final CloneToolControler cloneToolControler = new CloneToolControler(session);
		return cloneToolControler.deleteConfigurationValue(hwconfId,partitionName,key);
	}

	@Override
	public List<HWConf> getAllHWConf(Session session) {
		// TODO Auto-generated method stub
		final CloneToolControler cloneToolControler = new CloneToolControler(session);
		return cloneToolControler.getAllHWConf();
	}
}
