/* (c) 2017 Péter Varkoly <peter@varkoly.de> - all rights reserved  */
package de.openschoolserver.dao.controller;

import java.util.ArrayList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.File;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.ws.rs.WebApplicationException;

import org.jdom.*;
import org.jdom.input.SAXBuilder;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.openschoolserver.dao.*;
import de.openschoolserver.dao.tools.OSSShellTools;

@SuppressWarnings( "unchecked" )
public class SoftwareController extends Controller {
	
	Logger logger           = LoggerFactory.getLogger(SoftwareController.class);
	private static String SALT_PACKAGE_DIR = "/srv/salt/packages/";

	public SoftwareController(Session session) {
		super(session);
	}
	
	/*
	 * Functions to create and modify softwares
	 */
	public Software getById(long softwareId) {
		EntityManager em = getEntityManager();
		try {
			return em.find(Software.class, softwareId);
		} catch (Exception e) {
			logger.error(e.getMessage());
			return null;
		} finally {
			em.close();
		}
	}

	public List<Software> search(String search) {
		EntityManager em = getEntityManager();
		try {
			Query query = em.createNamedQuery("SoftwareStatus.search").setParameter("search",search);
			return query.getResultList();
		} catch (Exception e) {
			logger.error(e.getMessage());
			return null;
		} finally {
			em.close();
		}
	}

	public OssResponse add(Software software) {
		EntityManager em = getEntityManager();
		Software oldSoftware = this.getByName(software.getName());
		SoftwareVersion softwareVersion = software.getSoftwareVersions().get(0);	
		if( oldSoftware != null ) {
			try {
				oldSoftware.getSoftwareVersions().add(softwareVersion);
				softwareVersion.setSoftware(oldSoftware);
				em.getTransaction().begin();
				em.persist(softwareVersion);
				em.merge(oldSoftware);
				em.getTransaction().commit();
			} catch (Exception e) {
				logger.error(e.getMessage());
				em.close();
				return new OssResponse(this.getSession(),"ERROR",e.getMessage());
			}
			return new OssResponse(this.getSession(),"OK","Software was created succesfully",software.getId());
		}
		software.addSoftwareVersion(softwareVersion);
		softwareVersion.setSoftware(software);
		software.setCreator(this.session.getUser());
		try {
			em.getTransaction().begin();
			em.persist(software);
			em.persist(softwareVersion);
			em.getTransaction().commit();
		} catch (Exception e) {
			logger.error(e.getMessage());
			return new OssResponse(this.getSession(),"ERROR",e.getMessage());
		} finally {
			em.close();
		}
		return new OssResponse(this.getSession(),"OK","Software was created succesfully");
	}
	
	public OssResponse delete(Long softwareId) {
		EntityManager em = getEntityManager();
		Software software = this.getById(softwareId);
		if( !this.mayModify(software) ) {
        	return new OssResponse(this.getSession(),"ERROR","You must not delete this software.");
        }
		try {
			em.getTransaction().begin();
			if( !em.contains(software)) {
				software = em.merge(software);
			}
			em.remove(software);
			em.getTransaction().commit();
			em.getEntityManagerFactory().getCache().evictAll();
		} catch (Exception e) {
			logger.error(e.getMessage());
			return new OssResponse(this.getSession(),"ERROR",e.getMessage());
		} finally {
			em.close();
		}
		return new OssResponse(this.getSession(),"OK","Software was deleted succesfully");
	}

	public OssResponse modify(Software software) {
		EntityManager em = getEntityManager();
		//TODO it may be too simple
		try {
			em.getTransaction().begin();
			em.merge(software);
			em.getTransaction().commit();
		} catch (Exception e) {
			logger.error(e.getMessage());
			return new OssResponse(this.getSession(),"ERROR",e.getMessage());
		} finally {
			em.close();
		}
		return new OssResponse(this.getSession(),"OK","Software was created succesfully");
	}

	public List<Software> getAll() {
		EntityManager em = getEntityManager();
		Query query = em.createNamedQuery("Software.findAll");
		return (List<Software>)query.getResultList();
	}

	public Software getByName(String name) {
		EntityManager em = getEntityManager();
		Query query = em.createNamedQuery("Software.getByName").setParameter("name", name);
		if( query.getResultList().isEmpty() ) {
			return null;
		}
		return (Software) query.getResultList().get(0);
	}
	
	public List<SoftwareVersion> getAllVersion() {
		EntityManager em = getEntityManager();
		Query query = em.createNamedQuery("SoftwareVersion.findAll");
		return (List<SoftwareVersion>)query.getResultList();
	}
	
	
	/*
	 * Functions to interact with the CEPHALIX repository.
	 */

	public List<Map<String, String>> listDownloadedSoftware() {
		Map<String,String>        software;
		List<Map<String, String>> softwares = new ArrayList<>();
		Map<String,String>        updates   = new HashMap<String,String>();
		Map<String,String>        updatesDescription  = new HashMap<String,String>();
		String[] program    = new String[9];
		StringBuffer reply  = new StringBuffer();
		StringBuffer stderr = new StringBuffer();
		program[0]= "/usr/bin/zypper";
		program[1]= "-nx";
		program[2]= "-D";
		program[3] = "/srv/salt/repos.d/";
		program[4] = "lu";
		program[5] = "-t";
		program[6] = "package";
		program[7] = "-r";
		program[8] = "salt-packages";
		OSSShellTools.exec(program, reply, stderr, null);
		try {
			Document doc = new SAXBuilder().build( reply.toString() );
			Element rootNode = doc.getRootElement();
			for( Element node : (List<Element>) rootNode.getChild("update-list").getChildren("update") ) {
				updates.put(node.getAttributeValue("name").substring(8),node.getAttributeValue("edition"));
				updatesDescription.put(node.getAttributeValue("name").substring(8), node.getChildText("description"));
			}
		} catch(IOException e ) { 
			logger.error(e.getMessage());
			throw new WebApplicationException(500);
		} catch(JDOMException e)  {
			logger.error(e.getMessage());
			throw new WebApplicationException(500);
		}
		program    = new String[8];
		program[0] = "/usr/bin/zypper";
		program[1] = "-nx";
		program[2] = "-D";
		program[3] = "/srv/salt/repos.d/";
		program[4] = "se";
		program[5] = "-si";
		program[6] = "-r";
		program[7] = "salt-packages";
		OSSShellTools.exec(program, reply, stderr, null);
		try {
			Document doc = new SAXBuilder().build( reply.toString() );
			Element rootNode = doc.getRootElement();
			for( Element node : (List<Element>) rootNode.getChild("search-result").getChild("solvable-list").getChildren("solvable") ) {
				software = new HashMap<String,String>();
				software.put("name", node.getAttributeValue("name").substring(8));
				software.put("description", node.getAttributeValue("kind"));
				software.put("version", node.getAttributeValue("edition"));
				if( updates.containsKey(node.getAttributeValue("name").substring(8)) ) {
					software.put("update",updates.get(node.getAttributeValue("name").substring(8)));
					software.put("updateDescription",updatesDescription.get(node.getAttributeValue("name").substring(8)));
				}
				softwares.add(software);
			}
		} catch(IOException e ) { 
			logger.error(e.getMessage());
			throw new WebApplicationException(500);
		} catch(JDOMException e)  {
			logger.error(e.getMessage());
			throw new WebApplicationException(500);
		}
		return softwares;
	}

	public List<Map<String, String>> getAvailableSoftware() {
		Map<String,String>        software;
		List<Map<String, String>> softwares = new ArrayList<>();
		String[] program    = new String[6];
		StringBuffer reply  = new StringBuffer();
		StringBuffer stderr = new StringBuffer();
		program[0] = "/usr/bin/zypper";
		program[1] = "-nxD";
		program[2] = "/srv/salt/repos.d/";
		program[3] = "se";
		program[4] = "-sur";
		program[5] = "salt-packages";
		OSSShellTools.exec(program, reply, stderr, null);
		try {
			Document doc = new SAXBuilder().build( new StringReader(reply.toString()) );
			Element rootNode = doc.getRootElement();
			List<Element> elements = rootNode.getChild("search-result").getChild("solvable-list").getChildren("solvable");
			for( Element node : elements ) {
				software = new HashMap<String,String>();
				software.put("name", node.getAttributeValue("name").substring(8));
				/*software.put("description", node.getAttributeValue("kind"));*/
				software.put("version", node.getAttributeValue("edition"));
				softwares.add(software);
			}
		} catch(IOException e ) { 
			logger.error(e.getMessage());
			throw new WebApplicationException(500);
		} catch(JDOMException e)  {
			logger.error(reply.toString());
			logger.error(stderr.toString());
			logger.error(e.getMessage());
			throw new WebApplicationException(500);
		}
		return softwares;
	}
	

	public OssResponse downloadSoftwares(List<String> softwares) {
		String[] program    = new String[1+ softwares.size()];
		StringBuffer reply  = new StringBuffer();
		StringBuffer stderr = new StringBuffer();
		program[0] = "/usr/sbin/oss_download_packages";
		for(int i = 0; i < softwares.size(); i++) {
			program[1+i] = "oss-pkg-" + softwares.get(i);
		}
		OSSShellTools.exec(program, reply, stderr, null);
		return new OssResponse(this.getSession(),"OK","Download of the softwares was started succesfully");
	}
	
	public OssResponse removeSoftwares(List<String> softwares) {
		String[] program    = new String[7+ softwares.size()];
		StringBuffer reply  = new StringBuffer();
		StringBuffer stderr = new StringBuffer();
		program[0] = "/usr/bin/zypper";
		program[1] = "-nx";
		program[2] = "-D";
		program[3] = "/srv/salt/repos.d/";
		program[4] = "install";
		program[5] = "-r";
		program[6] = "salt-packages";
		for(int i = 0; i < softwares.size(); i++) {
			program[8+i] = softwares.get(i);
		}
		OSSShellTools.exec(program, reply, stderr, null);
		return new OssResponse(this.getSession(),"OK","Softwares were removed succesfully");		
	}

	/*
	 * Functions to deliver installation status
	 */
	public Map<String, String> statistic() {
		Map<String,String> statusMap = new HashMap<>();
		statusMap.put("name","software");
		EntityManager em = getEntityManager();
        Query query;
        Integer count;

        query = em.createNamedQuery("SoftwareStatus.findByStatus").setParameter("STATUS","I");
        count = query.getResultList().size();
        statusMap.put("Installed", count.toString());

        query = em.createNamedQuery("SoftwareStatus.findByStatus").setParameter("STATUS","IS");
        count = query.getResultList().size();
        statusMap.put("Installation scheduled", count.toString());
        
        query = em.createNamedQuery("SoftwareStatus.findByStatus").setParameter("STATUS","IF");
        count = query.getResultList().size();
        statusMap.put("Installation failed", count.toString());
        
        query = em.createNamedQuery("SoftwareStatus.findByStatus").setParameter("STATUS","MI");
        count = query.getResultList().size();
        statusMap.put("Installed manually", count.toString());
        
        query = em.createNamedQuery("SoftwareStatus.findByStatus").setParameter("STATUS","DM");
        count = query.getResultList().size();
        statusMap.put("Deinstalled manually", count.toString());
        
        return statusMap;
	}

	public List<SoftwareStatus> getAllStatus(String installationStatus) {
		EntityManager em = getEntityManager();
        Query query = em.createNamedQuery("SoftwareStatus.findByStatus").setParameter("STATUS",installationStatus);
        return (List<SoftwareStatus>) query.getResultList();
	}

	/*
	 * This is the first step to start the installation. An installation category will be created
	 * 
	 *  @param		category A category object containing the name and description of the category
	 *  @return		Returns the id of the created new category
	 */
	public Long createInstallationCategory(Category category) {
		CategoryController categoryController = new CategoryController(this.session);
		category.setCategoryType("installation");
		categoryController.add(category);
		Category newCategory = categoryController.getByName(category.getName());
		if( newCategory != null ) {
			return newCategory.getId();
		}
		return null;
	}

	public OssResponse addSoftwareToCategory(Long softwareId,Long categoryId){
		EntityManager em = getEntityManager();
		try {
			Software s = em.find(Software.class, softwareId);
			Category c = em.find(Category.class, categoryId);
			s.getCategories().add(c);
			c.getSoftwares().add(s);
			s.getRemovedFromCategories().remove(c);
			c.getRemovedSoftwares().remove(s);
			em.getTransaction().begin();
			em.merge(s);
			em.merge(c);
			em.getTransaction().commit();
		} catch (Exception e) {
			logger.error(e.getMessage());;
			return new OssResponse(this.getSession(),"ERROR",e.getMessage());
		} finally {
			em.close();
		}
		return new OssResponse(this.getSession(),"OK","SoftwareState was added to category succesfully");
	}
	
	public OssResponse deleteSoftwareFromCategory(Long softwareId,Long categoryId){
		EntityManager em = getEntityManager();
		try {
			Software s = em.find(Software.class, softwareId);
			Category c = em.find(Category.class, categoryId);
			s.getCategories().remove(c);
			c.getSoftwares().remove(s);
			s.getRemovedFromCategories().add(c);
			c.getRemovedSoftwares().add(s);
			em.getTransaction().begin();
			em.merge(s);
			em.merge(c);
			em.getTransaction().commit();
			for(Room r : c.getRooms() ) {
				for( Device d : r.getDevices() ) {
					this.modifySoftwareStatusOnDevice(d,s,"","deinstallation_scheduled");
				}
			}
		} catch (Exception e) {
			return new OssResponse(this.getSession(),"ERROR",e.getMessage());
		} finally {
			em.close();
		}
		return new OssResponse(this.getSession(),"OK","SoftwareState was added to category succesfully");
	}
	
	/*
	 * Add a license to a software
	 */
	public OssResponse addLicenseToSoftware(SoftwareLicense softwareLicense, Long softwareId,
			InputStream fileInputStream, 
			FormDataContentDisposition contentDispositionHeader
			) 
	{
		EntityManager em = getEntityManager();
		Software software = this.getById(softwareId);
		softwareLicense.setCreator(this.session.getUser());
		if(softwareLicense.getLicenseType().equals('F')) {
			try {
				em.getTransaction().begin();
				softwareLicense.setSoftware(software);
				em.persist(softwareLicense);
				em.getTransaction().commit();
			} catch (Exception e) {
				return new OssResponse(this.getSession(),"ERROR",e.getMessage());
			} finally {
				em.close();
			}
			return this.uploadLicenseFile(softwareLicense.getId(), fileInputStream, contentDispositionHeader);
		}
		if(softwareLicense.getLicenseType().equals('C') && fileInputStream == null) {
			try {
				em.getTransaction().begin();
				softwareLicense.setSoftware(software);
				em.persist(softwareLicense);
				em.getTransaction().commit();
			} catch (Exception e) {
				return new OssResponse(this.getSession(),"ERROR",e.getMessage());
			} finally {
				em.close();
			}
		} else {
			File file = null;
			try {
				file = File.createTempFile("oss_uploadFile", ".ossb", new File("/opt/oss-java/tmp/"));
				Files.copy(fileInputStream, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
				em.getTransaction().begin();
				for( String line : Files.readAllLines(file.toPath())) {
					SoftwareLicense sl = new SoftwareLicense();
					String[] lic = line.split(";");
					sl.setLicenseType('C');
					sl.setValue(lic[0]);
					sl.setSoftware(software);
					if( lic.length == 1 ) {
						sl.setCount(1);
					} else {
						sl.setCount(Integer.parseInt(lic[1]));
					}
					em.persist(sl);
				}
				em.getTransaction().commit();
				Files.delete(file.toPath());
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
				return new OssResponse(this.getSession(),"ERROR", e.getMessage());
			} finally {
				em.close();
			}
		}
		return new OssResponse(this.getSession(),"OK","License was added to the software succesfully");
	}
	
	public OssResponse uploadLicenseFile(Long licenseId, InputStream fileInputStream, 
			FormDataContentDisposition contentDispositionHeader)
	{
		EntityManager em = getEntityManager();
		SoftwareLicense softwareLicense = em.find(SoftwareLicense.class, licenseId);
		try {
			String fileName = contentDispositionHeader.getFileName();
			StringBuilder newFileName = new StringBuilder(SALT_PACKAGE_DIR);
			newFileName.append(softwareLicense.getSoftware().getName());
			File newFile = new File( newFileName.toString());
			Files.createDirectories(newFile.toPath());
			newFileName.append("/").append(fileName);
			newFile = new File(newFileName.toString());
			Files.copy(fileInputStream, newFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
			softwareLicense.setValue(fileName);
			em.getTransaction().begin();
			em.merge(softwareLicense);
			em.getTransaction().commit();
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			throw new WebApplicationException(500);
		} finally {
			em.close();
		}
		return new OssResponse(this.getSession(),"OK","Software License File was uploaded succesfully");
	}

	/*
	 *  Add Licenses to a HWConf 
	 */
	public OssResponse addSoftwareLicenseToHWConf(Software software, HWConf hwconf) {
		return this.addSoftwareLicenseToDevices(software, hwconf.getDevices());
	}
	
	/*
	 *  Add Licenses to a Room
	 */
	public OssResponse addSoftwareLicenseToRoom(Software software, Room room) {
		return this.addSoftwareLicenseToDevices(software, room.getDevices());
	}
	
	/*
	 * Add Licenses to devices
	 */
	public OssResponse addSoftwareLicenseToDevices(Software software, List<Device> devices ){
		EntityManager em = getEntityManager();
		SoftwareLicense softwareLicense;
 		List<String> failedDevices = new ArrayList<String>();
 		for( Device device : devices ) {
 			for( SoftwareLicense myLicense : device.getSoftwareLicences() ) {
 				if( myLicense.getSoftware().equals(software) ){
 					continue;
 				}
 			}
 			softwareLicense = this.getNextFreeLicenseId(software); 
 			if( softwareLicense == null) {
 				failedDevices.add(device.getName());
 			} else {
 				try {
 					em.getTransaction().begin();
 					device.getSoftwareLicences().add(softwareLicense);
 					softwareLicense.getDevices().add(device);
 					em.getTransaction().commit();
 				} catch (Exception e) {
 					logger.error(e.getMessage());
 					em.close();
 					return new OssResponse(this.getSession(),"ERROR",e.getMessage());
 				}
 			}
 		}
 		em.close();
 		if(failedDevices.isEmpty() ) {
 			return new OssResponse(this.getSession(),"OK","License was added to the devices succesfully");
 		}
 		return new OssResponse(this.getSession(),"ERROR","License could not be added to the following devices" + String.join(", ", failedDevices));
	}
	
	/*
	 * Return the next free license
	 */
	private  SoftwareLicense getNextFreeLicenseId(Software software) {
		for( SoftwareLicense softwareLicense : software.getSoftwareLicenses() ) {
			if( softwareLicense.getCount() > softwareLicense.getDevices().size() ) {
				return softwareLicense;
			}
		}
		return null;
	}

	/*
	 * Sets the software status on a device to a given version and remove the other status.
	 */
	public void setSoftwareStatusOnDevice(Device d, Software s,  String version, String status) {
		EntityManager em = getEntityManager();
		List<SoftwareVersion> lsv;
		Query query= em.createNamedQuery("SoftwareStatus.get")
				.setParameter("SOFTWARE", s.getId())
				.setParameter("DEVICE", d.getId());
		
		List<SoftwareStatus> lss = query.getResultList();
		try {
			em.getTransaction().begin();
			if( !lss.isEmpty()) {
				for( SoftwareStatus ss : lss ) {
					if( ss.getSoftwareVersion().getVersion().equals(version) ) {
						ss.setStatus(status);
						em.merge(ss);
					} else {
						em.remove(ss);
					}
				}
			} else {
				query = em.createNamedQuery("SoftwareVersion.getBySDoftware")
						.setParameter("SOFTWARE", s.getId());
				lsv = query.getResultList();
				if( lsv.isEmpty() ) {
					SoftwareVersion sv = new SoftwareVersion(s,"0.0");
					em.persist(sv);
					SoftwareStatus ss = new SoftwareStatus(d,sv,status);
					em.persist(ss);
				} else {
					for( SoftwareVersion sv : lsv ) {
						SoftwareStatus ss = new SoftwareStatus(d,sv,status);
						em.persist(ss);
					}
				}
			}
			em.getTransaction().commit();
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			em.close();
		}
	}

	/*
	 * Modify the software status on a device. If there is no status nothing will be happenend.
	 */
	public void modifySoftwareStatusOnDevice(Device d, Software s,  String version, String status) {
		EntityManager em = getEntityManager();
		Query query;
		if( version.isEmpty() ) {
			query = em.createNamedQuery("SoftwareStatus.get")
					.setParameter("SOFTWARE", s.getId())
					.setParameter("DEVICE", d.getId());
		} else {
			query = em.createNamedQuery("SoftwareStatus.get")
					.setParameter("SOFTWARE", s.getId())
					.setParameter("DEVICE", d.getId())
					.setParameter("VERSION", version);
		}
		List<SoftwareStatus> lss = query.getResultList();
		try {
			em.getTransaction().begin();
			for( SoftwareStatus ss : lss ) {
					ss.setStatus(status);
					em.merge(ss);
			}
			em.getTransaction().commit();
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			em.close();
		}
	}

	/*
	 * Remove the software status from a device.
	 */
	public void removeSoftwareStatusOnDevice(Device d, Software s,  String version) {
		EntityManager em = getEntityManager();
		Query query;
		if( version.isEmpty() ) {
			query = em.createNamedQuery("SoftwareStatus.get")
					.setParameter("SOFTWARE", s.getId())
					.setParameter("DEVICE", d.getId());
		} else {
			query = em.createNamedQuery("SoftwareStatus.get")
					.setParameter("SOFTWARE", s.getId())
					.setParameter("DEVICE", d.getId())
					.setParameter("VERSION", version);
		}
		List<SoftwareStatus> lss = query.getResultList();
		try {
			em.getTransaction().begin();
			for( SoftwareStatus ss : lss ) {
					em.remove(ss);
			}
			em.getTransaction().commit();
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			em.close();
		}
	}

	/*
	 * Reads the software status to a given version of a software on a device.
	 */
	public String getSoftwareStatusOnDevice(Device d, Software s,  String version) {
		EntityManager em = getEntityManager();
		Query query;
		if( version.isEmpty() ) {
			query = em.createNamedQuery("SoftwareStatus.get")
					.setParameter("SOFTWARE", s.getId())
					.setParameter("DEVICE", d.getId());
		} else {
			query = em.createNamedQuery("SoftwareStatus.get")
					.setParameter("SOFTWARE", s.getId())
					.setParameter("DEVICE", d.getId())
					.setParameter("VERSION", version);
		}
		List<SoftwareStatus> lss = query.getResultList();
		em.close();
		if( lss.isEmpty() ) {
			return "";
		}
		return lss.get(0).getStatus();
	}

	/*
	 * Checks if there is a software status to a given version of a software on a device.
	 */
	public boolean checkSoftwareStatusOnDevice(Device d, Software s,  String version) {
		EntityManager em = getEntityManager();
		Query query = em.createNamedQuery("SoftwareStatus.get")
					.setParameter("SOFTWARE", s.getId())
					.setParameter("DEVICE", d.getId())
					.setParameter("VERSION", version);
		return ! query.getResultList().isEmpty();
	}

	/*
	 * Save the software status what shall be installed to host sls files.
	 */
	public OssResponse applySoftwareStateToHosts(){
		EntityManager em = getEntityManager();
		RoomController   roomController   = new RoomController(this.session);
		DeviceController deviceController = new DeviceController(this.session);
		Map<Device,List<String>>   softwaresToInstall = new HashMap<>();
		Map<Device,List<Software>> softwaresToRemove  = new HashMap<>();
		String key;
		final String domainName = this.getConfigValue("DOMAIN_NAME");

		List<String>   topSls = new ArrayList<String>();
		Path SALT_TOP_TEMPL   = Paths.get("/usr/share/oss/templates/top.sls");
		if( Files.exists(SALT_TOP_TEMPL) ) {
			try {
				topSls = Files.readAllLines(SALT_TOP_TEMPL);
			} catch (java.nio.file.NoSuchFileException e) {
				logger.error(e.getMessage());
			} catch( IOException e ) {
				logger.error(e.getMessage());
			}
		} else {
			topSls.add("base:");
		}

		//Evaluate device categories
		for( Device device : deviceController.getAll() ) {
			List<String>   toInstall = new ArrayList<String>();
			List<Software> toRemove  = new ArrayList<Software>();
			for( Category category : device.getCategories() ) {
				if( !category.getCategoryType().equals("installation")) {
					continue;
				}
				for( Software software : category.getRemovedSoftwares() ) {
					toRemove.add(software);
				}
			}
			for( Category category : device.getCategories() ) {
				if( !category.getCategoryType().equals("installation")) {
					continue;
				}
				for( Software software : category.getSoftwares() ) {
					toRemove.remove(software);
					toInstall.add(String.format("%04d-%s",software.getWeight(),software.getName()));
				}
			}
			softwaresToInstall.put(device, toInstall);
			softwaresToRemove.put(device, toRemove);
		}

		//Evaluate room categories
		for( Room room : roomController.getAll() ) {
			List<Software> toRemove  = new ArrayList<Software>();
			for( Category category : room.getCategories() ) {
				if( !category.getCategoryType().equals("installation")) {
					continue;
				}
				for( Software software : category.getRemovedSoftwares() ) {
					toRemove.add(software);
				}
			}
			for( Category category : room.getCategories() ) {
				if( !category.getCategoryType().equals("installation")) {
					continue;
				}
				for( Software software : category.getSoftwares() ) {
					toRemove.remove(software);
					key = String.format("%04d-%s",software.getWeight(),software.getName());
					for( Device device : room.getDevices() ) {
						if( ! softwaresToInstall.get(device).contains(key) ) {
							softwaresToInstall.get(device).add(key);
						}
					}
				}
			}
			for( Device device : room.getDevices() ) {
				for( Software software : toRemove ) {
					if( ! softwaresToRemove.get(device).contains(software) ) {
						softwaresToRemove.get(device).add(software);
					}
				}
			}
		}

		//Evaluate hwconf categories
		Query query = em.createNamedQuery("HWConf.findAll");
		for( HWConf hwconf : (List<HWConf>)  query.getResultList() ) {
			//List of software to be removed
			List<Software> toRemove  = new ArrayList<Software>();
			for( Category category : hwconf.getCategories() ) {
				if( !category.getCategoryType().equals("installation")) {
					continue;
				}
				for( Software software : category.getRemovedSoftwares() ) {
					toRemove.add(software);
				}
			}
			for( Category category : hwconf.getCategories() ) {
				if( !category.getCategoryType().equals("installation")) {
					continue;
				}
				for( Software software : category.getSoftwares() ) {
					toRemove.remove(software);
					key = String.format("%04d-%s",software.getWeight(),software.getName());
					for( Device device : hwconf.getDevices() ) {
						if( ! softwaresToInstall.get(device).contains(key) ) {
							softwaresToInstall.get(device).add(key);
						}
					}
				}
			}
			for( Device device : hwconf.getDevices() ) {
				for( Software software : toRemove ) {
					if( ! softwaresToRemove.get(device).contains(software) ) {
						softwaresToRemove.get(device).add(software);
					}
				}
			}
		}

		//Write the hosts sls files
		for( Device device : deviceController.getAll() ) {
			List<String> deviceSls = new ArrayList<String>();
			//Remove first the softwares.
			for( Software software : softwaresToRemove.get(device) ) {
				deviceSls.add(software.getName()+":");
				deviceSls.add("  - pkg:");
				deviceSls.add("    - removed:");
				if( this.checkSoftwareStatusOnDevice(device, software, "I") ){
					this.setSoftwareStatusOnDevice(device, software, "", "DS");
				}
			}
			softwaresToInstall.get(device).sort((String s1, String s2) -> { return s2.compareTo(s1); });
			for( String softwareKey :  softwaresToInstall.get(device) ) {			
				String softwareName = softwareKey.substring(4);
				StringBuilder filePath = new StringBuilder(SALT_PACKAGE_DIR);
				filePath.append(softwareName).append(".sls");
				File file = new File(filePath.toString());
				if( file.exists() ) {
					Software software = this.getByName(softwareName);
					for( SoftwareLicense sl : device.getSoftwareLicences() ) {
						if( sl.getSoftware().equals(software) ) {
							deviceSls.add(softwareName + "_KEY");
							deviceSls.add("  grains.present:");
							deviceSls.add("    - value: " + sl.getValue());
						}
						deviceSls.add("include:");
						deviceSls.add(" - " + softwareName);
					}
				} else {
					deviceSls.add(softwareName+":");
					deviceSls.add("  - pkg:");
					deviceSls.add("    - installed:");					
				}
				Software software = this.getByName(softwareName);
				if(! this.checkSoftwareStatusOnDevice(device, software, "I")){
					this.setSoftwareStatusOnDevice(device, software, "", "IS");
				}
			}
			if( deviceSls.size() > 0) {
				StringBuilder hostname = new StringBuilder();
				hostname.append(device.getName()).append(".").append(domainName);

				topSls.add("  - " + hostname.toString() + ":");
				topSls.add("    - oss_device_" + hostname.toString());

				Path SALT_DEV   = Paths.get("/srv/salt/oss_device_" + hostname.append(".sls").toString());
				try {
					Files.write(SALT_DEV, deviceSls );
				} catch( IOException e ) { 
					logger.error(e.getMessage());
				}
			}
		}
		if( topSls.size() > 0 ) {
			Path SALT_TOP   = Paths.get("/srv/salt/top.sls");
			try {
				Files.write(SALT_TOP, topSls );
			} catch( IOException e ) { 
				e.printStackTrace();
			}
			this.systemctl("restart", "salt-master");
		}
		return new OssResponse(this.getSession(),"OK","Software State was saved succesfully"); 
	}

	/*
	 * Sets the software installation status on a device
	 * 
	 * @param	device			The corresponding device object
	 * @param	softwareName	Name of the corresponding software package
	 * @param	version			The version of the corresponding software
	 * @param	status			The state to be set
	 * @return					An OssResponse object will be responsed
	 */
	public OssResponse setSoftwareStatusOnDevice(Device device, String softwareName, String version, String status) {
		SoftwareStatus  softwareStatus  = null;
		Software        software        = this.getByName(softwareName);
		SoftwareVersion softwareVersion = null;
		EntityManager em = getEntityManager();
		if( software == null ) {
			// Software does not exist. It is a manually installed software.
			software = new Software();
			software.setName(softwareName);
			software.setManually(true);
			software.setDescription(softwareName);
			try {
				em.getTransaction().begin();
				em.persist(software);
				em.getTransaction().commit();
			} catch (Exception e) {
				logger.error(e.getMessage());
				em.close();
				return new OssResponse(this.getSession(),"ERROR",e.getMessage());
			}		
		}
		for( SoftwareVersion sv :  software.getSoftwareVersions() ) {
			if( sv.getVersion().equals(version)) {
				softwareVersion = sv;
				break;
			}
		}
		if( softwareVersion == null ) {
			softwareVersion = new SoftwareVersion();
			softwareVersion.setVersion(version);
			softwareVersion.setSoftware(software);
			try {
				em.getTransaction().begin();
				em.merge(software);
				em.persist(softwareVersion);
				em.getTransaction().commit();
			} catch (Exception e) {
				logger.error(e.getMessage());
				em.close();
				return new OssResponse(this.getSession(),"ERROR",e.getMessage());
			}
		}
		for( SoftwareStatus st : device.getSofwareStatus() ) {
			if( st.getSoftwareVersion().equals(softwareVersion) ) {
				softwareStatus = st;
				break;
			}
		}
		if( softwareStatus == null ) {
			softwareStatus = new SoftwareStatus(device,softwareVersion,status);
			try {
				em.getTransaction().begin();
				em.persist(softwareStatus);
				em.getTransaction().commit();
			} catch (Exception e) {
				logger.error(e.getMessage());
				em.close();
				return new OssResponse(this.getSession(),"ERROR",e.getMessage());
			}
		} else {
			softwareStatus.setStatus(status);
			try {
				em.getTransaction().begin();
				em.merge(softwareStatus);
				em.getTransaction().commit();
			} catch (Exception e) {
				logger.error(e.getMessage());
				em.close();
				return new OssResponse(this.getSession(),"ERROR",e.getMessage());
			}
		}
		em.close();
		return new OssResponse(this.getSession(),"OK","Software State was saved succesfully");
	}

	public OssResponse setSoftwareStatusOnDeviceById(Long deviceId, String softwareName, String version, String status) {
		DeviceController deviceController = new DeviceController(this.session);
		Device          device          =  deviceController.getById(deviceId);
		return this.setSoftwareStatusOnDevice(device, softwareName, version, status);
	}
	
	public OssResponse setSoftwareStatusOnDeviceByName(String deviceName, String softwareName, String version, String status) {
		DeviceController deviceController = new DeviceController(this.session);
		Device          device            =  deviceController.getByName(deviceName);
		return this.setSoftwareStatusOnDevice(device, softwareName, version, status);
	}

	/*
	 * Delete Software Status
	 * 
	 * 	 * @param	device			The corresponding device object
	 * @param	softwareName	Name of the corresponding software package
	 * @param	version			The version of the corresponding software
	 * @param	status			The state to be set
	 * @return					An OssResponse object will be responsed
	 * @param 	Device device The corresponding device object
	 * @param 
	 */
	public OssResponse deleteSoftwareStatusFromDevice(Device device, String softwareName, String version ) {
		EntityManager em = getEntityManager();
		for(SoftwareStatus st : device.getSofwareStatus() ) {
			if( st.getSoftwareVersion().getVersion().equals(version) && st.getSoftwareVersion().getSoftware().getName().equals(softwareName) ) {
				try {
					em.getTransaction().begin();
					em.merge(st);
					em.remove(st);
					em.getTransaction().commit();
					return new OssResponse(this.getSession(),"OK","Software State was removed succesfully");
				} catch (Exception e) {
					logger.error(e.getMessage());
					return new OssResponse(this.getSession(),"ERROR",e.getMessage());
				} finally {
					em.close();
				}
			}
		}
		return new OssResponse(this.getSession(),"OK","No Software State exists for this software version on this device.");
	}

	public OssResponse deleteSoftwareStatusFromDeviceByName(String deviceName, String softwareName, String version) {
		Device device = new DeviceController(this.session).getByName(deviceName);
		return this.deleteSoftwareStatusFromDevice(device, softwareName, version);
	}

	public OssResponse deleteSoftwareStatusFromDeviceById(Long deviceId, String softwareName, String version) {
		Device device = new DeviceController(this.session).getById(deviceId);
		return this.deleteSoftwareStatusFromDevice(device, softwareName, version);
	}

	public String getSoftwareStatusOnDeviceByName(String deviceName, String softwareName,
			String version) {
		Device device = new DeviceController(this.session).getByName(deviceName);
		Software software = this.getByName(softwareName);
		return this.getSoftwareStatusOnDevice(device, software, version);
	}

	public String getSoftwareStatusOnDeviceById(Long deviceId, String softwareName, String version) {
		Device device = new DeviceController(this.session).getById(deviceId);
		Software software = this.getByName(softwareName);
		return this.getSoftwareStatusOnDevice(device, software, version);
	}

	public List<SoftwareStatus> getSoftwareStatusOnDevice(Device device, String softwareName) {
		List<SoftwareStatus> softwareStatus = new ArrayList<SoftwareStatus>();
		for( SoftwareStatus st : device.getSofwareStatus() ) {
			st.setSoftwareName(st.getSoftwareVersion().getSoftware().getName());
			st.setDeviceName(device.getName());
			if( softwareName.equals("*") || st.getSoftwareName().equals(softwareName) ) {
				st.setVersion(st.getSoftwareVersion().getVersion());
				st.setManually(st.getSoftwareVersion().getSoftware().getManually());
				softwareStatus.add(st);
			}
		}
		return softwareStatus;
	}

	public List<SoftwareStatus> getSoftwareStatusOnDeviceByName(String deviceName, String softwareName) {
		Device device = new DeviceController(this.session).getByName(deviceName);
		return this.getSoftwareStatusOnDevice(device, softwareName);
	}

	public List<SoftwareStatus> getSoftwareStatusOnDeviceById(Long deviceId, String softwareName) {
		Device  device = new DeviceController(this.session).getById(deviceId);
		return this.getSoftwareStatusOnDevice(device, softwareName);
	}

	public String getSoftwareLicencesOnDevice(String deviceName) {

		Device        device    =  new DeviceController(this.session).getByName(deviceName);
		StringBuilder softwares = new StringBuilder();
		for( SoftwareLicense license : device.getSoftwareLicences() ) {
			softwares.append("'LIC_");
			softwares.append(license.getSoftware().getName());
			softwares.append("' '");
			softwares.append(license.getValue());
			softwares.append("'\\n");
		}
		return softwares.toString();
	}
}
