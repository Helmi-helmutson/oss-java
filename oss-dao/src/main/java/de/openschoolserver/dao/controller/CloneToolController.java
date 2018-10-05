/* (c) 2017 Péter Varkoly <peter@varkoly.de> - all rights reserved */
package de.openschoolserver.dao.controller;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.sql.Timestamp;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import de.openschoolserver.dao.Clone;
import de.openschoolserver.dao.Device;
import de.openschoolserver.dao.HWConf;
import de.openschoolserver.dao.Partition;
import de.openschoolserver.dao.Room;
import de.openschoolserver.dao.OssResponse;
import de.openschoolserver.dao.Session;
import de.openschoolserver.dao.SoftwareStatus;
import de.openschoolserver.dao.tools.OSSShellTools;

@SuppressWarnings( "unchecked" )
public class CloneToolController extends Controller {

	Logger logger = LoggerFactory.getLogger(CloneToolController.class);
	private List<String> parameters = new ArrayList<String>();
	
	protected Path PXE_BOOT   = Paths.get("/usr/share/oss/templates/pxeboot");
	protected Path ELILO_BOOT = Paths.get("/usr/share/oss/templates/eliloboot");
	protected String images   = "/srv/itool/images/";

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
			HWConf hwconf = em.find(HWConf.class, hwconfId);
			for( Partition partition : hwconf.getPartitions() ) {
				File f = new File(images + hwconfId + "/" + partition.getName() + ".img");
				if( f.exists() ) {
					partition.setLastCloned(new Timestamp(f.lastModified()));
				}
			}
			return hwconf;
		} catch (Exception e) {
			logger.error(e.getMessage());
			return null;
		} finally {
			em.close();
		}
	}

	public HWConf getByName(String name) {
		EntityManager em = getEntityManager();
		try {
			Query query = em.createNamedQuery("HWConf.getByName").setParameter("name",name);
			return (HWConf) query.getSingleResult();
		} catch (Exception e) {
			logger.error(e.getMessage());
			return null;
		} finally {
			em.close();
		}
	}

	public List<HWConf> getByType(String deviceType) {
		EntityManager em = getEntityManager();
		try {
			Query query = em.createNamedQuery("HWConf.getByType").setParameter("deviceType",deviceType);
			return (List<HWConf>) query.getResultList();
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

	public Partition getPartitionById(Long partitionId) {
		EntityManager em = getEntityManager();
		try {
			return em.find(Partition.class, partitionId);
		} catch (Exception e) {
			logger.error(e.getMessage());
			return null;
		} finally {
			em.close();
		}
	}

	public String getConfigurationValue(Long hwconfId, String partition, String key ) {
		Partition part = this.getPartition(hwconfId, partition);
		if( part == null) {
			return "";
		}
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
		if( this.getByName(hwconf.getName()) != null){
			return new OssResponse(this.getSession(),"ERROR", "Configuration name is not unique.");
		}
		try {
			hwconf.setCreator(this.session.getUser());
			if( hwconf.getDeviceType() == null ||  hwconf.getDeviceType().isEmpty() ) {
				hwconf.setDeviceType("FatClient");
			}
			em.getTransaction().begin();
			em.persist(hwconf);
			em.getTransaction().commit();
			logger.debug("Created HWConf:" + hwconf );
		} catch (Exception e) {
			logger.error(e.getMessage());
			return new OssResponse(this.getSession(),"ERROR", e.getMessage());
		} finally {
			em.close();
		}
		return new OssResponse(this.getSession(),"OK","Hardware configuration was created.",hwconf.getId());
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
		return new OssResponse(this.getSession(),"OK", "Hardware configuration was modified.");
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
		parameters.add(name);
		parameters.add(hwconf.getName());
		parameters.add(hwconf.getDeviceType());
		return new OssResponse(this.getSession(),"OK", "Partition: %s was created in %s (%s)",null,parameters);
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
		parameters.add(partition.getName());
		parameters.add(hwconf.getName());
		parameters.add(hwconf.getDeviceType());
		return new OssResponse(this.getSession(),"OK", "Partition: %s was created in %s (%s)",null,parameters);
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
		parameters.add(key);
		parameters.add(value);
		return new OssResponse(this.getSession(),"OK", "Partitions key: %s was set to %s.",partition.getId(),parameters);
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
			for( Device o : hwconf.getDevices() ) {
				o.setHwconf(null);
				em.merge(o);
			}
			for( Room o : hwconf.getRooms() ) {
				o.setHwconf(null);
				em.merge(o);
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
			em.remove(partition);
			em.merge(hwconf);
			em.getTransaction().commit();
			em.getEntityManagerFactory().getCache().evict(hwconf.getClass());
		} catch (Exception e) {
			logger.error(e.getMessage());
			return new OssResponse(this.getSession(),"ERROR", e.getMessage());
		} finally {
			em.close();
		}
		parameters.add(partitionName);
		parameters.add(hwconf.getName());
		parameters.add(hwconf.getDeviceType());
		return new OssResponse(this.getSession(),"OK", "Partition: %s was deleted from %s (%s)",hwconfId,parameters);
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
		parameters.add(key);
		return new OssResponse(this.getSession(),"OK", "Partitions key: %s was deleted",null,parameters );
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

	public OssResponse startCloning(String type, Long id, int multiCast) {
		List<String> pxeBoot;
		List<String> eliloBoot;
		StringBuilder ERROR = new StringBuilder();
		try {
			pxeBoot   = Files.readAllLines(PXE_BOOT);
			eliloBoot = Files.readAllLines(PXE_BOOT);
		}
		catch( IOException e ) { 
			e.printStackTrace();
			return new OssResponse(this.getSession(),"ERROR", e.getMessage());
		}
		for(int i = 0; i < pxeBoot.size(); i++) {
			String temp = pxeBoot.get(i);
			temp = temp.replaceAll("#PARTITIONS#", "all");
			if( multiCast != 1) {
				temp = temp.replaceAll("MULTICAST=1", "");
			}
			pxeBoot.set(i, temp);
		}
		for(int i = 0; i < eliloBoot.size(); i++) {
			String temp = eliloBoot.get(i);
			temp = temp.replaceAll("#PARTITIONS#", "all");
			if( multiCast != 1) {
				temp = temp.replaceAll("MULTICAST=1", "");
			}
			eliloBoot.set(i, temp);
		}
		
		List<Device> devices = new ArrayList<Device>();
		switch(type) {
			case "device":
				devices.add(new DeviceController(this.session).getById(id));
				break;
			case "hwconf":
				devices = new DeviceController(this.session).getByHWConf(id);
				break;
			case "room":
				devices = new RoomController(this.session).getById(id).getDevices();
				
		}
		
		for( Device device : devices ) {
			String pathPxe  = String.format("/srv/tftp/pxelinux.cfg/01-%s", device.getMac().toLowerCase().replace(":", "-"));
			String pathElilo= String.format("/srv/tftp/%s.conf", device.getMac().toUpperCase().replace(":", "-"));
			try {
				Files.write(Paths.get(pathPxe), pxeBoot);
				Files.write(Paths.get(pathElilo), eliloBoot);
			}catch( IOException e ) { 
				e.printStackTrace();
				ERROR.append(e.getMessage());
			}
		}
		if( ERROR.length() == 0 ) {
			return new OssResponse(this.getSession(),"OK", "Boot configuration was saved succesfully." );
		}
		parameters.add(ERROR.toString());
		return new OssResponse(this.getSession(),"ERROR","Error(s) accoured during saving the boot configuration: %s",null,parameters);
	}
	
	public OssResponse startCloning(Long hwconfId, Clone parameters) {
		List<String> partitions = new ArrayList<String>();
		List<String> pxeBoot;
		List<String> eliloBoot;
		List<String> responseParameters = new ArrayList<String>();
		StringBuilder ERROR = new StringBuilder();
		try {
			pxeBoot   = Files.readAllLines(PXE_BOOT);
			eliloBoot = Files.readAllLines(PXE_BOOT);
		}
		catch( IOException e ) { 
			e.printStackTrace();
			return new OssResponse(this.getSession(),"ERROR", e.getMessage());
		}
		for( Long partitionId : parameters.getPartitionIds() ) {
			Partition partition = this.getPartitionById(partitionId);
			if( partition == null ) {
				responseParameters.add(String.valueOf(partitionId));
				return new OssResponse(this.getSession(),"ERROR", "Can not find partition with id: %s",hwconfId,responseParameters);
			}
			partitions.add(partition.getName());
		}
		String parts = String.join(",",partitions);
		for(int i = 0; i < pxeBoot.size(); i++) {
			String temp = pxeBoot.get(i);
			temp = temp.replaceAll("#PARTITIONS#", parts.toString());
			if(!parameters.isMultiCast()) {
				temp = temp.replaceAll("MULTICAST=1", "");
			}
			pxeBoot.set(i, temp);
		}
		for(int i = 0; i < eliloBoot.size(); i++) {
			String temp = eliloBoot.get(i);
			temp = temp.replaceAll("#PARTITIONS#", parts.toString());
			if(!parameters.isMultiCast()) {
				temp = temp.replaceAll("MULTICAST=1", "");
			}
			eliloBoot.set(i, temp);
		}
		DeviceController dc = new DeviceController(this.session);
		for( Long deviceId : parameters.getDeviceIds() ) {
			Device device   = dc.getById(deviceId);
			String pathPxe  = String.format("/srv/tftp/pxelinux.cfg/01-%s", device.getMac().toLowerCase().replace(":", "-"));
			String pathElilo= String.format("/srv/tftp/%s.conf", device.getMac().toUpperCase().replace(":", "-"));
			try {
				Files.write(Paths.get(pathPxe), pxeBoot);
				Files.write(Paths.get(pathElilo), eliloBoot);
			}catch( IOException e ) { 
				e.printStackTrace();
				ERROR.append(e.getMessage());
			}
		}
		if( ERROR.length() == 0 ) {
			return new OssResponse(this.getSession(),"OK", "Boot configuration was saved succesfully." );
		}
		responseParameters.add(ERROR.toString());
		return new OssResponse(this.getSession(),"ERROR","Error(s) accoured during saving the boot configuration: %s",hwconfId,responseParameters);
	}

	public OssResponse stopCloning(String type, Long id) {
		StringBuilder ERROR = new StringBuilder();
		List<Device> devices = new ArrayList<Device>();
		switch(type) {
			case "device":
				devices.add(new DeviceController(this.session).getById(id));
				break;
			case "hwconf":
				devices = new DeviceController(this.session).getByHWConf(id);
				break;
			case "room":
				devices = new RoomController(this.session).getById(id).getDevices();
				
		}
		for( Device device : devices ) {
			String pathPxe  = String.format("/srv/tftp/pxelinux.cfg/01-%s", device.getMac().toLowerCase().replace(":", "-"));
			String pathElilo= String.format("/srv/tftp/%s.conf", device.getMac().toUpperCase().replace(":", "-"));
			try {
				Files.deleteIfExists(Paths.get(pathPxe));
				Files.deleteIfExists(Paths.get(pathElilo));
			}catch( IOException e ) { 
				e.printStackTrace();
				ERROR.append(e.getMessage());
			}
		}
		if( ERROR.length() == 0 ) {
			return new OssResponse(this.getSession(),"OK", "Boot configuration was removed succesfully." );
		}
		parameters.add(ERROR.toString());
		return new OssResponse(this.getSession(),"ERROR","Error(s) accoured during removing the boot configuration: %s",null,parameters);
	}

	public String resetMinion(Long deviceId) {
		EntityManager em = getEntityManager();
		try {
			Device device = em.find(Device.class, deviceId);
			if( device == null ) {
				return "ERROR Can not find the device.";
			}
			em.getTransaction().begin();
			for ( SoftwareStatus st : device.getSoftwareStatus() ) {
				em.remove(st);
			}
			device.setSoftwareStatus(null);
			em.merge(device);
			em.getTransaction().commit();
			String deviceName  = device.getName();
			String[] program   = new String[4];
			StringBuffer reply = new StringBuffer();
			StringBuffer error = new StringBuffer();
			program[0] = "/usr/bin/samba-tool";
			program[1] = "user";
			program[2] = "delete";
			program[3] = deviceName + "$";
			OSSShellTools.exec(program, reply, error, null);
			StringBuilder path = new StringBuilder("/etc/salt/pki/master/minions/");
			path.append(deviceName).append(".").append(this.getConfigValue("DOMAIN"));
			Files.deleteIfExists(Paths.get(path.toString()));
			path = new StringBuilder("/etc/salt/pki/master/minions_denied/");
			path.append(deviceName).append(".").append(this.getConfigValue("DOMAIN"));
			Files.deleteIfExists(Paths.get(path.toString()));
			path = new StringBuilder("/etc/salt/pki/master/minions_rejected/");
			path.append(deviceName).append(".").append(this.getConfigValue("DOMAIN"));
			Files.deleteIfExists(Paths.get(path.toString()));
			this.systemctl("try-restart", "salt-master");
			this.systemctl("try-restart", "oss_salt_event_watcher");
		} catch ( IOException e ) {
			logger.error(e.getMessage());
			return "ERROR "+e.getMessage();
		}
		return "OK";
	}

	public OssResponse startMulticast(Long partitionId, String networkDevice) {
		EntityManager em = getEntityManager();
		Partition partition;
		try {
			partition = em.find(Partition.class, partitionId);
			Long hwconfId = partition.getHwconf().getId();
			String[] program   = new String[8];
			StringBuffer reply = new StringBuffer();
			StringBuffer error = new StringBuffer();
			program[0] = "/usr/sbin/udp-sender";
			program[1] = "--nokbd";
			program[2] = "--interface";
			program[3] = networkDevice;
			program[4] = "--file";
			program[5] = images + hwconfId + "/" + partition.getName() + ".img";
			program[6] = "--autostart";
			program[7] = "&";
			OSSShellTools.exec(program, reply, error, null);
		} catch (Exception e) {
			logger.error(e.getMessage());
			return null;
		} finally {
			em.close();
		}
		return new OssResponse(this.getSession(),"OK","Multicast imaging was started succesfully.");
	}

	public OssResponse modifyPartition(Long partitionId, Partition partition) {
		EntityManager em = getEntityManager();
		try {
			if( partition.getId() != partitionId ) {
				return new OssResponse(this.getSession(),"ERROR","Partition id mismatch.");
			}
			Partition oldPartition = em.find(Partition.class, partitionId);
			if( oldPartition == null ) {
				return new OssResponse(this.getSession(),"ERROR","Cannot find partition.");
			}
			em.getTransaction().begin();
			oldPartition.setDescription(partition.getDescription());
			oldPartition.setOs(partition.getOs());
			oldPartition.setFormat(partition.getFormat());
			oldPartition.setJoinType(partition.getJoinType());
			em.merge(oldPartition);
			em.getTransaction().commit();
		} catch (Exception e) {
			logger.error("modifyPartition:" + e.getMessage());
			return new OssResponse(this.getSession(),"ERROR", e.getMessage());
		} finally {
			em.close();
		}
		return new OssResponse(this.getSession(),"OK","Multicast imaging was started succesfully.");
	}

}
