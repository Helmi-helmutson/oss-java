/* (c) 2017 Peter Varkoly <peter@varkoly.de> - all rights reserved */
package de.openschoolserver.api.resourceimpl;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

import de.openschoolserver.api.resources.SoftwareResource;
import de.openschoolserver.dao.Category;
import de.openschoolserver.dao.Device;
import de.openschoolserver.dao.HWConf;
import de.openschoolserver.dao.OssBaseObject;
import de.openschoolserver.dao.OssResponse;
import de.openschoolserver.dao.Room;
import de.openschoolserver.dao.Session;
import de.openschoolserver.dao.Software;
import de.openschoolserver.dao.SoftwareLicense;
import de.openschoolserver.dao.SoftwareStatus;
import de.openschoolserver.dao.SoftwareVersion;
import de.openschoolserver.dao.controller.SoftwareController;
import de.openschoolserver.dao.controller.CategoryController;
import de.openschoolserver.dao.controller.CloneToolController;
import de.openschoolserver.dao.controller.DeviceController;
import de.openschoolserver.dao.controller.RoomController;

public class SoftwareResourceImpl implements SoftwareResource {
	Logger logger           = LoggerFactory.getLogger(SoftwareResource.class);
	public SoftwareResourceImpl() {
	}

	@Override
	public List<Software> getAll(Session session) {
		SoftwareController softwareController = new SoftwareController(session);
		return softwareController.getAll();
	}

	@Override
	public List<Software> getAllInstallable(Session session) {
		SoftwareController softwareController = new SoftwareController(session);
		return softwareController.getAllInstallable();
	}

	@Override
	public Software getById(Session session, long softwareId) {
		SoftwareController softwareController = new SoftwareController(session);
		return softwareController.getById(softwareId);
	}

	@Override
	public List<Software> search(Session session, String search) {
		SoftwareController softwareController = new SoftwareController(session);
		return softwareController.search(search);
	}

	@Override
	public OssResponse add(Session session, Software software) {
		SoftwareController softwareController = new SoftwareController(session);
		return softwareController.add(software,true);
	}

	@Override
	public OssResponse modify(Session session, Software software) {
		SoftwareController softwareController = new SoftwareController(session);
		return softwareController.modify(software);
	}

	@Override
	public OssResponse delete(Session session, long softwareId) {
		SoftwareController softwareController = new SoftwareController(session);
		return softwareController.delete(softwareId);
	}

	@Override
	public OssResponse apply(Session session) {
		SoftwareController softwareController = new SoftwareController(session);
		return softwareController.applySoftwareStateToHosts();
	}


	@Override
	public OssResponse createInstallation(Session session, Category category) {
		return new SoftwareController(session).createInstallationCategory(category);
	}

	@Override
	public OssResponse addSoftwareToInstalation(Session session, long installationId, long softwareId) {
		SoftwareController softwareController = new SoftwareController(session);
		return softwareController.addSoftwareToCategory(softwareId,installationId);
	}

	@Override
	public OssResponse addDeviceToInstalation(Session session, long installationId, long deviceId) {
		CategoryController categoryController = new CategoryController(session);
		return categoryController.addMember(installationId, "Device", deviceId);
	}

	@Override
	public OssResponse addRoomToInstalation(Session session, long installationId, long roomId) {
		CategoryController categoryController = new CategoryController(session);
		return categoryController.addMember(installationId, "Room", roomId);
	}

	@Override
	public OssResponse addHWConfToInstalation(Session session, long installationId, long hwconfId) {
		CategoryController categoryController = new CategoryController(session);
		return categoryController.addMember(installationId, "HWConf", hwconfId);
	}

	@Override
	public OssResponse deleteInstalation(Session session, long installationId) {
		CategoryController categoryController = new CategoryController(session);
		OssResponse ossResponse = categoryController.delete(installationId);
		if( ossResponse.getCode().equals("OK") ) {
			ossResponse = this.apply(session);
		}
		return ossResponse;
	}

	@Override
	public OssResponse deleteSoftwareFromInstalation(Session session, long installationId, long softwareId) {
		SoftwareController softwareController = new SoftwareController(session);
		return softwareController.deleteSoftwareFromCategory(softwareId,installationId);
	}

	@Override
	public OssResponse deleteDeviceFromInstalation(Session session, long installationId, long deviceId) {
		CategoryController categoryController = new CategoryController(session);
		return categoryController.deleteMember(installationId, "Device", deviceId);
	}

	@Override
	public OssResponse deleteRoomFromInstalation(Session session, long installationId, long roomId) {
		CategoryController categoryController = new CategoryController(session);
		return categoryController.deleteMember(installationId, "Room", roomId);
	}

	@Override
	public OssResponse deleteHWConfFromInstalation(Session session, long installationId, long hwconfId) {
		CategoryController categoryController = new CategoryController(session);
		return categoryController.deleteMember(installationId, "HWConf", hwconfId);
	}

	@Override
	public List<OssBaseObject> getSoftwares(Session session, long installationId) {
		Category category = new CategoryController(session).getById(installationId);
		List<OssBaseObject> objects = new ArrayList<OssBaseObject>();
		for( Software object : category.getSoftwares() ) {
			objects.add(new OssBaseObject(object.getId(),object.getName()));
		}
		return objects;
	}

	@Override
	public List<OssBaseObject> getDevices(Session session, long installationId) {
		Category category = new CategoryController(session).getById(installationId);
		List<OssBaseObject> objects = new ArrayList<OssBaseObject>();
		for( Device object : category.getDevices() ) {
			objects.add(new OssBaseObject(object.getId(),object.getName()));
		}
		return objects;
	}

	@Override
	public List<OssBaseObject> getRooms(Session session, long installationId) {
		Category category = new CategoryController(session).getById(installationId);
		List<OssBaseObject> objects = new ArrayList<OssBaseObject>();
		for( Room object : category.getRooms() ) {
			objects.add(new OssBaseObject(object.getId(),object.getName()));
		}
		return objects;
	}

	@Override
	public List<OssBaseObject> getHWConfs(Session session, long installationId) {
		Category category = new CategoryController(session).getById(installationId);
		List<OssBaseObject> objects = new ArrayList<OssBaseObject>();
		for( HWConf object : category.getHWConfs() ) {
			objects.add(new OssBaseObject(object.getId(),object.getName()));
		}
		return objects;
	}

	@Override
	public List<Map<String, String>> getAvailable(Session session) {
		SoftwareController softwareController = new SoftwareController(session);
		return softwareController.getAvailableSoftware();
	}

	@Override
	public OssResponse download(Session session, List<String> softwares) {
		SoftwareController softwareController = new SoftwareController(session);
		return softwareController.downloadSoftwares(softwares);
	}
	@Override
	public OssResponse downloadOne(Session session, String softwareName) {
		List<String> softwares = new ArrayList<String>();
		softwares.add(softwareName);
		SoftwareController softwareController = new SoftwareController(session);
		return softwareController.downloadSoftwares(softwares);
	}

	@Override
	public List<Map<String, String>> listDownloadedSoftware(Session session) {
		return new SoftwareController(session).listDownloadedSoftware();
	}

	@Override
	public OssResponse setSoftwareInstalledOnDevice(Session session, String deviceName, String softwareName,
			String version) {
		return new SoftwareController(session).setSoftwareStatusOnDeviceByName(deviceName, softwareName, version, "I");
	}

	@Override
	public String getSoftwareLicencesOnDevice(Session session, String deviceName) {
		SoftwareController softwareController = new SoftwareController(session);
		return softwareController.getSoftwareLicencesOnDevice(deviceName);
	}

	@Override
	public List<Category> getInstallations(Session session) {
		return new CategoryController(session).getByType("installation");
	}

	@Override
	public List<Software> getSoftwares(Session session, List<Long> softwareIds) {
		return new SoftwareController(session).getSoftwareStatusById(softwareIds);
	}

	@Override
	public OssResponse addLicenseToSoftware(
			Session     session,
			long        softwareId,
			Character   licenseType,
    		Integer     count,
    		String      value,
			InputStream fileInputStream,
			FormDataContentDisposition contentDispositionHeader) {

		SoftwareLicense softwareLicense = new SoftwareLicense();
		softwareLicense.setValue(value);
		softwareLicense.setCount(count);
		softwareLicense.setLicenseType(licenseType);
		return new SoftwareController(session).addLicenseToSoftware(
				softwareLicense,
				softwareId,
				fileInputStream,
				contentDispositionHeader);
	}

	@Override
	public OssResponse modifyLicense(
			Session     session,
			long        licenseId,
			Character   licenseType,
    		Integer     count,
    		String      value,
			InputStream fileInputStream,
			FormDataContentDisposition contentDispositionHeader
		) {
		SoftwareController  softwareController = new SoftwareController(session);
		SoftwareLicense softwareLicense = softwareController.getSoftwareLicenseById(licenseId);
		softwareLicense.setCount(count);
		softwareLicense.setValue(value);
		softwareLicense.setLicenseType(licenseType);
		return softwareController.modifySoftwareLIcense(
				softwareLicense,fileInputStream,contentDispositionHeader);
	}

	@Override
	public OssResponse deleteLicense(Session session, long licenseId) {
		return new SoftwareController(session).deleteLicence(licenseId);
	}

	@Override
	public List<SoftwareStatus> getSoftwareStatusOnDevice(Session session, Long deviceId, Long softwareId) {
		if( softwareId == null) {
			return this.getAllSoftwareStatusOnDevice(session, deviceId);
		}
		return new SoftwareController(session).getSoftwareStatusOnDeviceById(deviceId, softwareId);
	}

	@Override
	public List<SoftwareStatus> getAllSoftwareStatusOnDevice(Session session, Long deviceId) {
		return new SoftwareController(session).getAllSoftwareStatusOnDeviceById(deviceId);
	}

	@Override
	public List<SoftwareStatus> getSoftwareStatus(Session session, Long softwareId) {
		SoftwareController softwareController = new SoftwareController(session);
		List<SoftwareStatus> softwareStatus = new ArrayList<SoftwareStatus>();
		Software software = softwareController.getById(softwareId);
		for( SoftwareVersion sv : software.getSoftwareVersions() ) {
			for( SoftwareStatus st : sv.getSoftwareStatuses() ) {
				st.setSoftwareName(software.getName());
				st.setDeviceName(st.getDevice().getName());
				st.setVersion(sv.getVersion());
				st.setManually(software.getManually());
				softwareStatus.add(st);
			}
		}
		return softwareStatus;
	}

	@Override
	public List<SoftwareLicense> getSoftwareLicenses(Session session, long softwareId) {
		List<SoftwareLicense> licenses = new ArrayList<SoftwareLicense>();
		for( SoftwareLicense license : new SoftwareController(session).getById(softwareId).getSoftwareLicenses() ) {
			license.setUsed(license.getDevices().size());
			licenses.add(license);
		}
		return licenses;
	}

	@Override
	public OssResponse addRequirements(Session session, List<String> requirement) {
		return new SoftwareController(session).addRequirements(requirement);
	}

	@Override
	public OssResponse addRequirements(Session session, long softwareId, long requirementId) {
		return new SoftwareController(session).addRequirements(softwareId,requirementId);
	}

	@Override
	public OssResponse deleteRequirements(Session session, long softwareId, long requirementId) {
		return new SoftwareController(session).deleteRequirements(softwareId,requirementId);
	}

	@Override
	public String downloadStatus(Session session) {
		try {
			return	String.join(" ", Files.readAllLines(Paths.get("/run/lock/oss-api/oss_download_packages")));
		} catch( IOException e ) {
			return "";
		}
	}

	@Override
	public List<SoftwareStatus> getRoomsStatus(Session session, Long roomId) {
		List<SoftwareStatus> ss = new ArrayList<SoftwareStatus>();
		Room room = new RoomController(session).getById(roomId);
		SoftwareController sc = new SoftwareController(session);
		for( Device device : room.getDevices() ) {
			ss.addAll(sc.getAllSoftwareStatusOnDevice(device));
		}
		return ss;
	}

	@Override
	public List<SoftwareStatus> getHWConsStatus(Session session, Long hwconfId) {
		List<SoftwareStatus> ss = new ArrayList<SoftwareStatus>();
		SoftwareController sc = new SoftwareController(session);
		HWConf hwconf = new CloneToolController(session).getById(hwconfId);
		for( Device device : hwconf.getDevices() ) {
			ss.addAll(sc.getAllSoftwareStatusOnDevice(device));
		}
		return ss;
	}

	@Override
	public OssResponse applyState(Session session) {
		DeviceController deviceController = new DeviceController(session);
		for( Device device : deviceController.getAll() ) {
			deviceController.manageDevice(device, "applyState", null);
		}
		return new OssResponse(session,"OK","Salt High State was applied on all minions.");
	}

	@Override
	public Category getInstallation(Session session, long installationId) {
		return new CategoryController(session).getById(installationId);
	}

	@Override
	public List<Map<String, String>> listUpdatesForSoftwarePackages(Session session) {
		return new SoftwareController(session).listUpdatesForSoftwarePackages();
	}

	@Override
	public OssResponse updatesSoftwares(Session session, List<String> softwares) {
		return new SoftwareController(session).updatesSoftwares(softwares);
	}

	@Override
	public OssResponse deleteDownloadedSoftwares(Session session, List<String> softwares) {
		return new SoftwareController(session).deleteDownloadedSoftwares(softwares);
	}

	@Override
	public List<OssBaseObject> getAvailableSoftwares(Session session, long installationId) {
		SoftwareController sc = new SoftwareController(session);
		List<OssBaseObject> objects = new ArrayList<OssBaseObject>();
		Category installationSet = new CategoryController(session).getById(installationId);
		for( Software software : sc.getAllInstallable() ) {
			if( !installationSet.getSoftwares().contains(software) ) {
				objects.add(new OssBaseObject(software.getId(),software.getName()));
			}
		}
		return objects;
	}

	@Override
	public List<OssBaseObject> getAvailableDevices(Session session, long installationId) {
		List<OssBaseObject> objects = new ArrayList<OssBaseObject>();
		DeviceController dc = new DeviceController(session);
		for( Long deviceId : new CategoryController(session).getAvailableMembers(installationId, "Device") ) {
			Device device = dc.getById(deviceId);
			if( device != null  &&  device.getHwconf() != null && device.getHwconf().getDeviceType().equals("FatClient") ) {
				objects.add(new OssBaseObject(device.getId(),device.getName()));
			}
		}
		return objects;
	}

	@Override
	public List<OssBaseObject> getAvailableRooms(Session session, long installationId) {
		List<OssBaseObject> objects = new ArrayList<OssBaseObject>();
		RoomController rc = new RoomController(session);
		for( Long roomId : new CategoryController(session).getAvailableMembers(installationId, "Room") ) {
			Room room = rc.getById(roomId);
			if( room != null  &&  room.getHwconf() != null && room.getHwconf().getDeviceType().equals("FatClient") ) {
				objects.add(new OssBaseObject(room.getId(),room.getName()));
			}
		}
		return objects;
	}

	@Override
	public List<OssBaseObject> getAvailableHWConfs(Session session, long installationId) {
		List<OssBaseObject> objects = new ArrayList<OssBaseObject>();
		CloneToolController cc = new CloneToolController(session);
		for( Long hwconfId : new CategoryController(session).getAvailableMembers(installationId, "HWConf") ) {
			HWConf hwconf = cc.getById(hwconfId);
			if( hwconf != null && hwconf.getDeviceType().equals("FatClient") ) {
				objects.add(new OssBaseObject(hwconf.getId(),hwconf.getName()));
			}
		}
		return objects;
	}
}
