/* (c) 2017 Péter Varkoly <peter@varkoly.de> - all rights reserved */
package de.openschoolserver.api.resourceimpl;

import de.openschoolserver.dao.HWConf;


import de.openschoolserver.dao.Clone;
import de.openschoolserver.dao.Device;
import de.openschoolserver.dao.Partition;
import de.openschoolserver.dao.OssResponse;
import de.openschoolserver.dao.Session;
import de.openschoolserver.dao.Room;
import de.openschoolserver.dao.controller.CloneToolController;
import de.openschoolserver.dao.controller.Config;
import de.openschoolserver.dao.controller.RoomController;
import de.openschoolserver.dao.controller.SessionController;
import de.openschoolserver.dao.internal.CommonEntityManagerFactory;
import de.openschoolserver.dao.controller.DeviceController;
import de.openschoolserver.api.resources.HwconfResource;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.UriInfo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class HwconfResourceImpl implements HwconfResource {

	public HwconfResourceImpl() {
	}

	@Override
	public Long getMaster(Session session, Long hwconfId) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		CloneToolController cloneToolController = new CloneToolController(session,em);
		HWConf hwconf = cloneToolController.getById(hwconfId);
		Long resp = null;
		if( hwconf != null ) {
			for( Device device : hwconf.getDevices() ) {
				if( cloneToolController.checkConfig(device, "isMaster") ) {
					resp = device.getId();
					break;
				}
			}
		}
		em.close();
		return resp;
	}

	@Override
	public OssResponse add(Session session, HWConf hwconf) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		OssResponse resp = new CloneToolController(session,em).addHWConf(hwconf);
		em.close();
		return resp;
	}

	@Override
	public OssResponse modifyHWConf(Session session, Long hwconfId, HWConf hwconf) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		OssResponse resp = new CloneToolController(session,em).modifyHWConf(hwconfId, hwconf);
		em.close();
		return resp;
	}

	@Override
	public HWConf getById(Session session, Long hwconfId) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		final HWConf hwconf = new CloneToolController(session,em).getById(hwconfId);
		em.close();
		if (hwconf == null) {
			throw new WebApplicationException(404);
		}
		return hwconf;
	}

	@Override
	public OssResponse addPartition(Session session, Long hwconfId, Partition partition) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		OssResponse resp = new CloneToolController(session,em).addPartitionToHWConf(hwconfId, partition);
		em.close();
		return resp;
	}

	@Override
	public OssResponse delete(Session session, Long hwconfId) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		OssResponse resp = new CloneToolController(session,em).delete(hwconfId);
		em.close();
		return resp;
	}

	@Override
	public OssResponse deletePartition(Session session, Long hwconfId, String partitionName) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		OssResponse resp = new CloneToolController(session,em).deletePartition(hwconfId,partitionName);
		em.close();
		return resp;
	}

	@Override
	public List<HWConf> getAllHWConf(Session session) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		List<HWConf> resp = new CloneToolController(session,em).getAllHWConf();
		em.close();
		return resp;
	}

	@Override
	public OssResponse startRecover(Session session, Long hwconfId, Clone parameters) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		OssResponse resp = new CloneToolController(session,em).startCloning(hwconfId,parameters);
		em.close();
		return resp;
	}

	@Override
	public OssResponse startRecover(Session session, Long hwconfId, int multiCast) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		return new CloneToolController(session,em).startCloning("hwconf", hwconfId, multiCast);
	}

	@Override
	public OssResponse stopRecover(Session session, Long hwconfId) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		OssResponse resp = new CloneToolController(session,em).stopCloning("hwconf",hwconfId);
		em.close();
		return resp;
	}

	@Override
	public OssResponse startMulticast(Session session, Long partitionId, String networkDevice) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		OssResponse resp = new CloneToolController(session,em).startMulticast(partitionId,networkDevice);
		em.close();
		return resp;
	}

	@Override
	public OssResponse modifyPartition(Session session, Long partitionId, Partition partition) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		OssResponse resp = new CloneToolController(session,em).modifyPartition(partitionId, partition);
		em.close();
		return resp;
	}

	@Override
	public OssResponse importHWConfs(Session session, List<HWConf> hwconfs) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		CloneToolController  cloneToolController = new CloneToolController(session,em);
		OssResponse ossResponse = null;
		for( HWConf hwconf : hwconfs ) {
			ossResponse = cloneToolController.addHWConf(hwconf);
			if( ossResponse.getCode().equals("ERROR")) {
				break;
			}
		}
		em.close();
		return ossResponse;
	}
}
