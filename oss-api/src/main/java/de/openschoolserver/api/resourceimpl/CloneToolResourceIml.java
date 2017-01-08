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
    public Long getHWConf(Session sessiond) {
       final CloneToolController cloneToolController = new CloneToolController(session);
       final Long  hwconf = cloneToolController.getHWConf();
        if (hwconf == null) {
            throw new WebApplicationException(404);
        }
        return hwconf;
    }

    @Override
    public HWConf getById(Session sessiond, Long hwconfId) {
	final CloneToolController cloneToolController = new CloneToolController(session);
	final HWConf hwconf = cloneToolController.getById(hwconfId);
        if (hwconf == null) {
            throw new WebApplicationException(404);
        }
        return hwconf;
    }

    @Override
    public String getPartitions(Session sessiond, Long hwconfId) {
    	final CloneToolController cloneToolController = new CloneToolController(session);
	return cloneToolController.getPartitionsOfHWConf(hwconfId);
    }

    @Override
    public Partition getPartition(Session sessiond, Long hwconfId, String partition) {
    	final CloneToolController cloneToolController = new CloneToolController(session);
	return cloneToolController.getPartition(hwconfId, partition);
    }

    @Override
    public String getConfigurationValue(Session sessiond, Long hwconfId, String partition, String key) {
    	final CloneToolController cloneToolController = new CloneToolController(session);
	return cloneToolController.getConfigurationValue(hwconfId,partition,key);
    }

    @Override
    public Long createHWConf(Session sessiond, HWConf hwconf) {
       final CloneToolController cloneToolController = new CloneToolController(session);
       final Long  hwconf = cloneToolController.createHWConf(hwconf);
        if (hwconf == null) {
            throw new WebApplicationException(404);
        }
        return hwconf;
    }

    @Override
    public Boolean setHWConf(Session sessiond, HWConf hwconf) {
       final CloneToolController cloneToolController = new CloneToolController(session);
       return cloneToolController.setHWConf(hwconf);
    }

    @Override
    public boolean setPartition(Session sessiond, Long hwconfId, Partition partition) {
    	final CloneToolController cloneToolController = new CloneToolController(session);
	return cloneToolController.setPartition(hwconfId, partition);
    }

    @Override
    public String getPartitions(Session sessiond, Long hwconfId, String partitions) {
    	final CloneToolController cloneToolController = new CloneToolController(session);
	return cloneToolController.setPartitionsOfHWConf(hwconfId,partitions);
    }

    @Override
    public String setConfigurationValue(Session sessiond, Long hwconfId, String partition, String key, String value) {
    	final CloneToolController cloneToolController = new CloneToolController(session);
	return cloneToolController.setConfigurationValue(hwconfId,partition,key,value);
    }
}
