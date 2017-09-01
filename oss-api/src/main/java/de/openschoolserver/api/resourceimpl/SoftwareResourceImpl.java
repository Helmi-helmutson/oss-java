/* (c) 2017 Peter Varkoly <peter@varkoly.de> - all rights reserved */
package de.openschoolserver.api.resourceimpl;

import java.io.InputStream;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

import de.openschoolserver.api.resources.SoftwareResource;
import de.openschoolserver.dao.Category;
import de.openschoolserver.dao.Response;
import de.openschoolserver.dao.Session;
import de.openschoolserver.dao.Software;
import de.openschoolserver.dao.SoftwareLicense;
import de.openschoolserver.dao.SoftwareStatus;
import de.openschoolserver.dao.controller.SoftwareController;
import de.openschoolserver.dao.controller.CategoryController;

public class SoftwareResourceImpl implements SoftwareResource {

	public SoftwareResourceImpl() {
	}

	@Override
	public List<Software> getAll(Session session) {
		SoftwareController softwareController = new SoftwareController(session);
		return softwareController.getAll();
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
	public Response add(Session session, Software software) {
		SoftwareController softwareController = new SoftwareController(session);
		return softwareController.add(software);
	}

	@Override
	public Response modify(Session session, Software software) {
		SoftwareController softwareController = new SoftwareController(session);
		return softwareController.modify(software);
	}

	@Override
	public Response delete(Session session, long softwareId) {
		SoftwareController softwareController = new SoftwareController(session);
		return softwareController.delete(softwareId);
	}

	@Override
	public Response apply(Session session) {
		SoftwareController softwareController = new SoftwareController(session);
		return softwareController.applySoftwareStateToHosts();
	}

	@Override
	public Response addLicenseToSoftware(Session session, long softwareId, SoftwareLicense softwareLicense,
			InputStream fileInputStream, FormDataContentDisposition contentDispositionHeader) {
		SoftwareController softwareController = new SoftwareController(session);
		return softwareController.addLicenseToSoftware(softwareLicense, softwareId, fileInputStream, contentDispositionHeader);
	}

	@Override
	public Response createInstallation(Session session, Category category) {
		SoftwareController softwareController = new SoftwareController(session);
		return softwareController.createInstallationCategory(category);
	}

	@Override
	public Response addSoftwareToInstalation(Session session, long installationId, long softwareId) {
		SoftwareController softwareController = new SoftwareController(session);
		return softwareController.addSoftwareToCategory(softwareId,installationId);
	}

	@Override
	public Response addDeviceToInstalation(Session session, long installationId, long deviceId) {
		CategoryController categoryController = new CategoryController(session);
		return categoryController.addMember(installationId, "Device", deviceId);
	}

	@Override
	public Response addRoomToInstalation(Session session, long installationId, long roomId) {
		CategoryController categoryController = new CategoryController(session);
		return categoryController.addMember(installationId, "Room", roomId);
	}

	@Override
	public Response addHWConfToInstalation(Session session, long installationId, long hwconfId) {
		CategoryController categoryController = new CategoryController(session);
		return categoryController.addMember(installationId, "HWConf", hwconfId);
	}

	@Override
	public Response deleteInstalation(Session session, long installationId) {
		CategoryController categoryController = new CategoryController(session);
		Response response = categoryController.delete(installationId);
		if( response.getCode().equals("OK") ) {
			response = this.apply(session);
		}
		return response;
	}

	@Override
	public Response deleteSoftwareFromInstalation(Session session, long installationId, long softwareId) {
		SoftwareController softwareController = new SoftwareController(session);
		return softwareController.deleteSoftwareFromCategory(softwareId,installationId);
	}

	@Override
	public Response deleteDeviceFromInstalation(Session session, long installationId, long deviceId) {
		CategoryController categoryController = new CategoryController(session);
		return categoryController.deleteMember(installationId, "Device", deviceId);
	}

	@Override
	public Response deleteRoomFromInstalation(Session session, long installationId, long roomId) {
		CategoryController categoryController = new CategoryController(session);
		return categoryController.deleteMember(installationId, "Room", roomId);
	}

	@Override
	public Response deleteHWConfFromInstalation(Session session, long installationId, long hwconfId) {
		CategoryController categoryController = new CategoryController(session);
		return categoryController.addMember(installationId, "HWCconf", hwconfId);
	}

	@Override
	public List<Long> getSoftwares(Session session, long installationId) {
		CategoryController categoryController = new CategoryController(session);
		return categoryController.getMembers(installationId, "Software");
	}

	@Override
	public List<Long> getDevices(Session session, long installationId) {
		CategoryController categoryController = new CategoryController(session);
		return categoryController.getMembers(installationId, "Device");
	}

	@Override
	public List<Long> getRooms(Session session, long installationId) {
		CategoryController categoryController = new CategoryController(session);
		return categoryController.getMembers(installationId, "Rooms");
	}

	@Override
	public List<Long> getHWConfs(Session session, long installationId) {
		CategoryController categoryController = new CategoryController(session);
		return categoryController.getMembers(installationId, "HWConf");
	}

	@Override
	public List<Map<String, String>> getAvailable(Session session) {
		SoftwareController softwareController = new SoftwareController(session);
		return softwareController.getAvailableSoftware();
	}

	@Override
	public Response download(Session session, List<String> softwares) {
		SoftwareController softwareController = new SoftwareController(session);
		return softwareController.downloadSoftwares(softwares);
	}
	@Override
	public Response downloadOne(Session session, String softwareName) {
		List<String> softwares = new ArrayList<String>();
		softwares.add(softwareName);
		SoftwareController softwareController = new SoftwareController(session);
		return softwareController.downloadSoftwares(softwares);
	}
	
	@Override
	public Response removeSoftwares(Session session, List<String> softwares) {
		SoftwareController softwareController = new SoftwareController(session);
		return softwareController.removeSoftwares(softwares);
	}

	@Override
	public List<Map<String, String>> listDownloadedSoftware(Session session) {
		SoftwareController softwareController = new SoftwareController(session);
		return softwareController.listDownloadedSoftware();
	}

	@Override
	public Response setSoftwareInstalledOnDevice(Session session, String deviceName, String softwareName,
			String version) {
		SoftwareController softwareController = new SoftwareController(session);
		return softwareController.setSoftwareStatusOnDeviceByName(deviceName, softwareName, version, "I");
	}

	@Override
	public Response setSoftwareInstalledOnDeviceById(Session session, Long deviceId, String softwareName,
			String version) {
		SoftwareController softwareController = new SoftwareController(session);
		return softwareController.setSoftwareStatusOnDeviceById(deviceId, softwareName, version, "I");
	}

	@Override
	public Response deleteSoftwareStatusFromDevice(Session session, String deviceName, String softwareName,
			String version) {
		SoftwareController softwareController = new SoftwareController(session);
		return softwareController.deleteSoftwareStatusFromDeviceByName(deviceName, softwareName, version);
	}

	@Override
	public Response deleteSoftwareStatusFromDeviceById(Session session, Long deviceId, String softwareName,
			String version) {
		SoftwareController softwareController = new SoftwareController(session);
		return softwareController.deleteSoftwareStatusFromDeviceById(deviceId, softwareName, version);
	}

	@Override
	public List<SoftwareStatus> getSoftwareStatusOnDevice(Session session, String deviceName, String softwareName) {
		SoftwareController softwareController = new SoftwareController(session);
		return softwareController.getSoftwareStatusOnDeviceByName(deviceName, softwareName);
	}

	@Override
	public List<SoftwareStatus> getSoftwareStatusOnDeviceById(Session session, Long deviceId, String softwareName) {
		SoftwareController softwareController = new SoftwareController(session);
		return softwareController.getSoftwareStatusOnDeviceById(deviceId, softwareName);
	}

	@Override
	public String getSoftwareStatusOnDeviceByName(Session session, String DeviceName, String softwareName,
			String version) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getSoftwareStatusOnDeviceById(Session session, Long deviceId, String softwareName, 
			String version) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
