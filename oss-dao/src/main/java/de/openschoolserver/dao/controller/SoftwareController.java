 /* (c) 2017 Péter Varkoly <peter@varkoly.de> - all rights reserved  */
package de.openschoolserver.dao.controller;

import java.util.ArrayList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
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
import static de.openschoolserver.dao.internal.OSSConstatns.*;

import com.fasterxml.jackson.databind.ObjectMapper;

@SuppressWarnings( "unchecked" )
public class SoftwareController extends Controller {
	
	Logger logger           = LoggerFactory.getLogger(SoftwareController.class);
	private static String SALT_PACKAGE_DIR = "/srv/salt/packages/";
	private static String SALT_SOURCE_DIR  = "/srv/salt/win/repo-ng/";

	public SoftwareController(Session session) {
		super(session);
	}
	
	/*
	 * Functions to create and modify softwares
	 */
	public Software getById(long softwareId) {
		EntityManager em = getEntityManager();
		try {
			Software software =  em.find(Software.class, softwareId);
			File f = new File(SALT_SOURCE_DIR + software.getName() );
			if( f.exists() && f.list().length > 1 ) {
				software.setSourceAvailable(true);
			} else {
				software.setSourceAvailable(false);
			}
			return software;
		} catch (Exception e) {
			logger.error(e.getMessage());
			return null;
		} finally {
			em.close();
		}
	}

	public SoftwareVersion getSoftwareVersionById(long id) {
		EntityManager em = getEntityManager();
		try {
			return em.find(SoftwareVersion.class, id);
		} catch (Exception e) {
			logger.error(e.getMessage());
			return null;
		} finally {
			em.close();
		}
	}

	public SoftwareStatus getSoftwareStatusById(long id) {
		EntityManager em = getEntityManager();
		try {
			return em.find(SoftwareStatus.class, id);
		} catch (Exception e) {
			logger.error(e.getMessage());
			return null;
		} finally {
			em.close();
		}
	}

	public SoftwareLicense getSoftwareLicenseById(long id) {
		EntityManager em = getEntityManager();
		try {
			return em.find(SoftwareLicense.class, id);
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

	public OssResponse add(Software software, Boolean replace) {
		EntityManager em = getEntityManager();
		Software oldSoftware = this.getByName(software.getName());
		SoftwareVersion softwareVersion = software.getSoftwareVersions().get(0);	
		if( oldSoftware != null ) {
			try {
				if( replace ) {
					for( SoftwareVersion sv : oldSoftware.getSoftwareVersions() ) {
						sv.setStatus("R");
					}
					softwareVersion.setStatus("C");
				}
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
		if( replace ) {
			softwareVersion.setStatus("C");
		}
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
		return new OssResponse(this.getSession(),"OK","Software was created succesfully",software.getId());
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
		List<Software> softwares = new ArrayList<Software>();
		for( Software software : (List<Software>)query.getResultList() ) {
			if( ! software.getManually() ) {
				File f = new File(SALT_SOURCE_DIR + software.getName() );
				if( f.exists() ) {
					int count = 0; 
					for( String fileName : f.list() ) {
						if( fileName.equals("init.sls") || fileName.equals("install.xml") ) {
							continue;
						}
						count++;
					}
					software.setSourceAvailable( count > 0);
				} else {
					software.setSourceAvailable(false);
				}
				softwares.add(software);
			}
		}
		return softwares;
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
			Document doc = new SAXBuilder().build( new StringReader(reply.toString()) );
			Element rootNode = doc.getRootElement();
			if(!rootNode.getChildren("update-list").isEmpty()) {
				if( !rootNode.getChild("update-list").getChildren("update").isEmpty() ) { 
					for( Element node : (List<Element>) rootNode.getChild("update-list").getChildren("update") ) {
						updates.put(node.getAttributeValue("name").substring(8),node.getAttributeValue("edition"));
						updatesDescription.put(node.getAttributeValue("name").substring(8), node.getChildText("description"));
					}
				}
			}
		} catch(IOException e ) { 
			logger.error("1" + e.getMessage());
			//throw new WebApplicationException(500);
		} catch(JDOMException e)  {
			logger.error("2" + e.getMessage());
			throw new WebApplicationException(500);
		}
		program    = new String[5];
		program[0] = "rpm";
		program[1] = "-qa";
		program[2] = "oss-pkg-*";
		program[3] = "--qf";
		program[4] = "%{NAME}##%{SUMMARY}##%{VERSION}\\n";;
		reply  = new StringBuffer();
		stderr = new StringBuffer();
		OSSShellTools.exec(program, reply, stderr, null);
		logger.debug("Reply" + reply.toString());
		for( String line : reply.toString().split("\\n") ) {
			String[] values = line.split("##");
			String name     = values[0].substring(8);
			software = new HashMap<String,String>();
			software.put("name",  name);
			software.put("description", values[1]);
			software.put("version",values[2]);
			if( updates.containsKey(name) ) {
				software.put("update",updates.get(name));
				software.put("updateDescription",updatesDescription.get(name));
			}
			softwares.add(software);
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
			logger.debug(reply.toString());
			Element rootNode = doc.getRootElement();
			if( rootNode.getChild("search-result") == null ) {
				throw new WebApplicationException(600);
			}
			if( rootNode.getChild("search-result").getChild("solvable-list").getChildren().isEmpty() ||
				rootNode.getChild("search-result").getChild("solvable-list").getChildren("solvable").isEmpty()	) {
				throw new WebApplicationException(600);
			}
			List<Element> elements = rootNode.getChild("search-result").getChild("solvable-list").getChildren("solvable");
			for( Element node : elements ) {
				software = new HashMap<String,String>();
				software.put("name", node.getAttributeValue("name").substring(8));
				/*software.put("description", node.getAttributeValue("kind"));*/
				software.put("version", node.getAttributeValue("edition"));
				softwares.add(software);
			}
		} catch(IOException e ) {
			logger.error("1 " + reply.toString());
			logger.error("1 " + stderr.toString());
			logger.error("1 " + e.getMessage());
			throw new WebApplicationException(500);
		} catch(JDOMException e)  {
			logger.error("2 " + reply.toString());
			logger.error("2 " + stderr.toString());
			logger.error("2 " + e.getMessage());
			throw new WebApplicationException(500);
		}
		return softwares;
	}
	

	public OssResponse downloadSoftwares(List<String> softwares) {
		File file = null;
		try {
			file = File.createTempFile("oss_download_job", ".ossb", new File("/opt/oss-java/tmp/"));
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			return new OssResponse(this.getSession(),"ERROR", e.getMessage());
		}
		StringBuilder command = new StringBuilder();
		command.append("/usr/sbin/oss_download_packages ");
		for(int i = 0; i < softwares.size(); i++) {
			command.append("oss-pkg-").append(softwares.get(i)).append(" ");
		}
		try(  PrintWriter out = new PrintWriter( file.toPath().toString() )  ){
		    out.println( command.toString() );
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}

		String[] program    = new String[4];
		StringBuffer reply  = new StringBuffer();
		StringBuffer stderr = new StringBuffer();
		program[0]   = "at";
		program[1]   = "-f";
		program[2]   = file.toPath().toString();
		program[3]   = "now";
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
        
        query = em.createNamedQuery("SoftwareStatus.findByStatus").setParameter("STATUS","LM");
        count = query.getResultList().size();
        statusMap.put("License missing", count.toString());

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
	public OssResponse createInstallationCategory(Category category) {
		CategoryController categoryController = new CategoryController(this.session);
		category.setCategoryType("installation");
		return categoryController.add(category);
	}

	public OssResponse addSoftwareToCategory(Long softwareId,Long categoryId){
		EntityManager em = getEntityManager();
		try {
			Software s = em.find(Software.class, softwareId);
			Category c = em.find(Category.class, categoryId);
			if(c.getSoftwares().contains(s) ) {
				return new OssResponse(this.getSession(),"OK","Software was already added to the installation.");
			}
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
		return new OssResponse(this.getSession(),"OK","Software was added to the installation succesfully.");
	}
	
	public OssResponse deleteSoftwareFromCategory(Long softwareId,Long categoryId){
		EntityManager em = getEntityManager();
		try {
			Software s = em.find(Software.class, softwareId);
			Category c = em.find(Category.class, categoryId);
			if(!c.getSoftwares().contains(s) ) {
				return new OssResponse(this.getSession(),"OK","Software is not member of the installation.");
			}
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
	public OssResponse addLicenseToSoftware(SoftwareLicense softwareLicense,
			Long softwareId,
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
			return this.uploadLicenseFile(softwareLicense, fileInputStream, contentDispositionHeader);
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
	
	/*
	 * Modify an existing license
	 */
	public OssResponse modifySoftwareLIcense(
			SoftwareLicense softwareLicense,
			InputStream fileInputStream,
			FormDataContentDisposition contentDispositionHeader
			) {
		SoftwareLicense oldLicense = this.getSoftwareLicenseById(softwareLicense.getId());
		if( oldLicense == null ) {
			throw new WebApplicationException(404);
		}
		oldLicense.setCount(softwareLicense.getCount());
		oldLicense.setValue(softwareLicense.getValue());
		EntityManager em = getEntityManager();
		try {
			em.getTransaction().begin();
			em.merge(oldLicense);
			em.getTransaction().commit();
		} catch (Exception e) {
			return new OssResponse(this.getSession(),"ERROR",e.getMessage());
		} finally {
			em.close();
		}
		if( softwareLicense.getLicenseType().equals('F') && fileInputStream != null ) {
			return this.uploadLicenseFile(softwareLicense, fileInputStream, contentDispositionHeader);
		}
		return new OssResponse(this.getSession(),"OK","License was modified succesfully");
	}

	/*
	 * Upload a license file to an existing license.
	 */
	public OssResponse uploadLicenseFile(
			SoftwareLicense softwareLicense,
			InputStream fileInputStream, 
			FormDataContentDisposition contentDispositionHeader)
	{
		EntityManager em = getEntityManager();
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
	 * Add Licenses to a device
	 */
	public OssResponse addSoftwareLicenseToDevices(Software software, Device device ){

			for( SoftwareLicense myLicense : device.getSoftwareLicenses() ) {
				if( myLicense.getSoftware().equals(software) ){
					return new OssResponse(this.getSession(),"OK","License was already added to the device.");
				}
			}
			EntityManager em = getEntityManager();
			SoftwareLicense softwareLicense = this.getNextFreeLicenseId(software);
			if( softwareLicense == null) {
				return new OssResponse(this.getSession(),"ERROR","There is not enough licences.");
			} else {
				try {
					em.getTransaction().begin();
					device.getSoftwareLicenses().add(softwareLicense);
					softwareLicense.getDevices().add(device);
					em.getTransaction().commit();
				} catch (Exception e) {
					logger.error(e.getMessage());
					em.close();
					return new OssResponse(this.getSession(),"ERROR",e.getMessage());
				} finally {
					em.close();
				}
			}
			return new OssResponse(this.getSession(),"OK","License was added to the device succesfully.");
	}

	 /*
	  *  Delete Licenses from a device
	 */
	public OssResponse deleteSoftwareLicenseFromDevice(Software software, Device device ){

			for( SoftwareLicense myLicense : device.getSoftwareLicenses() ) {
				if( myLicense.getSoftware().equals(software) ){
					EntityManager em = getEntityManager();
					try {
						em.getTransaction().begin();
						device.getSoftwareLicenses().remove(myLicense);
						myLicense.getDevices().remove(device);
						em.merge(device);
						em.merge(myLicense);
						em.getTransaction().commit();
					} catch (Exception e) {
						logger.error(e.getMessage());
						em.close();
						return new OssResponse(this.getSession(),"ERROR",e.getMessage());
					} finally {
						em.close();
					}
					return new OssResponse(this.getSession(),"OK","License was removed from device.");
				}
			}
			return new OssResponse(this.getSession(),"OK","No license on thise device.");
	 }
	/*
	 * Sets the software status on a device to a given version and remove the other status.
	 */

	/*
	 * Sets the software status on a device to a given version and remove the other status.
	 */
	public void setSoftwareStatusOnDevice(Device d, Software s,  String version, String status) {
		EntityManager em = getEntityManager();
		List<SoftwareVersion> lsv;
		
		List<SoftwareStatus> lss = new ArrayList<SoftwareStatus>();
		for(SoftwareStatus ss : d.getSoftwareStatus() ) {
			if( ss.getSoftwareVersion().getSoftware().equals(s)) {
				lss.add(ss);
			}
		}
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
				lsv = s.getSoftwareVersions();
				if( lsv.isEmpty() ) {
					SoftwareVersion sv = new SoftwareVersion(s,version,"U");
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
	 * 
	 * @param   d     The concerning device
	 * @param   s     The software
	 * @param   state The status we are looking for
	 */
	public boolean checkSoftwareStatusOnDevice(Device d, Software s,  String state) {
		for( SoftwareStatus ss : d.getSoftwareStatus() ) {
			if( ss.getStatus().equals(state) && 
				ss.getSoftwareVersion().getSoftware().equals(s)) {
				return true;
			}
		}
		return false;
	}

	/*
	 * Checks if there is a software is installed on a device.
	 * 
	 * @param   d     The concerning device
	 * @param   s     The software
	 */
	public boolean isSoftwareInstalledOnDevice(Device d, Software s) {
		for( SoftwareStatus ss : d.getSoftwareStatus() ) {
			if( ( ss.getStatus().equals("I") || ss.getStatus().equals("IM") )&& 
				ss.getSoftwareVersion().getSoftware().equals(s)) {
				return true;
			}
		}
		return false;
	}

	/*
	 * Checks if there is a software version status to a given version of a software on a device.
	 * 
	 * @param   d     The concerning device
	 * @param   sv    The software version
	 * @param   state The status we are looking for
	 */
	public boolean checkSoftwareVersionStatusOnDevice(Device d, SoftwareVersion sv, String state) {
		for( SoftwareStatus ss : d.getSoftwareStatus() ) {
			if( ss.getStatus().equals(state) && 
				ss.getSoftwareVersion().equals(sv)) {
				return true;
			}
		}
		return false;
	}

	/*
	 * Checks if there is a software status to a given version of a software on a device.
	 */
	public boolean getSoftwareVersionOnDevice(Device d, Software s) {
		//TODO Does not works.
		EntityManager em = getEntityManager();
		Query query = em.createNamedQuery("SoftwareStatus.getForOne")
					.setParameter("SOFTWARE", s.getId())
					.setParameter("DEVICE", d.getId());
		return ! query.getResultList().isEmpty();
	}

	/*
	 * Set the software status on a device to a defined software version
	 */
	public void setInstallUpdateOnDevice(Software software, SoftwareVersion softwareVersion, Device device) {
		EntityManager em = getEntityManager();
		try {
			boolean update    = false;
			boolean installed = false;
			em.getTransaction().begin();
			for(SoftwareStatus ss : device.getSoftwareStatus() ) {
				if( ss.getSoftwareVersion().getSoftware().equals(software)) {
					if( !ss.getSoftwareVersion().equals(softwareVersion) ) {
						ss.setStatus("US");
						em.merge(ss);
						update = true;
					} else {
						installed = true;
					}
				}
			}
			if( !update && !installed ) {
				SoftwareStatus softwareStatus = new SoftwareStatus(device,softwareVersion,"IS");
				device.getSoftwareStatus().add(softwareStatus);
				em.persist(softwareStatus);
				em.merge(device);
			}
			em.getTransaction().commit();
		} catch (Exception e) {
			logger.error("Error in setInstallUpdateOnDevice" + e.getMessage());
		} finally {
			em.close();
		}
	}

	/*
	 * Save the software status what shall be installed to host sls files.
	 */
	public OssResponse applySoftwareStateToHosts(){
		RoomController   roomController   = new RoomController(this.session);
		DeviceController deviceController = new DeviceController(this.session);
		Map<String,List<String>>   softwaresToInstall = new HashMap<>();
		Map<String,List<Software>> softwaresToRemove  = new HashMap<>();
		List<String>   toInstall    = new ArrayList<String>();
		List<Software> toRemove     = new ArrayList<Software>();
		final String domainName     = this.getConfigValue("DOMAIN");
		StringBuilder errorMessages = new StringBuilder();
		List<String>   topSls       = new ArrayList<String>();
		Path SALT_TOP_TEMPL         = Paths.get("/usr/share/oss/templates/top.sls");
		String registerPassword     = this.getProperty("de.openschoolserver.dao.User.Register.Password");
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
			if( device.getHwconf() == null || ! device.getHwconf().getDeviceType().equals("FatClient") ) {
				continue;
			}
			toInstall = new ArrayList<String>();
			toRemove  = new ArrayList<Software>();
			for( Category category : device.getCategories() ) {
				if( category.getCategoryType().equals("installation")) {
					for( Software software : category.getRemovedSoftwares() ) {
						toRemove.add(software);
					}
				}
			}
			for( Category category : device.getCategories() ) {
				if( category.getCategoryType().equals("installation")) {
					for( Software software : category.getSoftwares() ) {
						toRemove.remove(software);
						for(Software requirements : software.getSoftwareRequirements() ) {
							toRemove.remove(requirements);
							toInstall.add(String.format("%04d-%s",requirements.getWeight(),requirements.getName()));
						}
						toInstall.add(String.format("%04d-%s",software.getWeight(),software.getName()));
					}
				}
			}
			softwaresToInstall.put(device.getName(), toInstall);
			softwaresToRemove.put(device.getName(), toRemove);
		}
		try {
			logger.debug("Software map after devices:" + softwaresToInstall);
		} catch (Exception e) {
			logger.error(e.getMessage());
			return null;
		}

		//Evaluate room categories
		for( Room room : roomController.getAll() ) {
			toRemove  = new ArrayList<Software>();
			toInstall = new ArrayList<String>();
			for( Category category : room.getCategories() ) {
				if( category.getCategoryType().equals("installation")) {
					for( Software software : category.getRemovedSoftwares() ) {
						toRemove.add(software);
					}
				}
			}
			for( Category category : room.getCategories() ) {
				if( category.getCategoryType().equals("installation")) {
					for( Software software : category.getSoftwares() ) {
						toRemove.remove(software);
						for(Software requirements : software.getSoftwareRequirements() ) {
							toRemove.remove(requirements);
							toInstall.add(String.format("%04d-%s",requirements.getWeight(),requirements.getName()));
						}
						toInstall.add(String.format("%04d-%s",software.getWeight(),software.getName()));
					}

				}
			}
			for( Device device : room.getDevices() ) {
				if( device.getHwconf() == null || ! device.getHwconf().getDeviceType().equals("FatClient") ) {
					continue;
				}
				softwaresToInstall.get(device.getName()).addAll(toInstall);
				softwaresToRemove.get(device.getName()).addAll(toRemove);
			}
		}

		//Evaluate hwconf categories
		for( HWConf hwconf : new CloneToolController(this.session).getAllHWConf() ) {
			logger.debug("HWConfs: " + hwconf.getName() + " " + hwconf.getDeviceType());
			if( !hwconf.getDeviceType().equals("FatClient")) {
				continue;
			}
			//List of software to be removed
			toRemove  = new ArrayList<Software>();
			toInstall = new ArrayList<String>();
			for( Category category : hwconf.getCategories() ) {
				if( category.getCategoryType().equals("installation")) {
					for( Software software : category.getRemovedSoftwares() ) {
						toRemove.add(software);
					}
				}
			}
			for( Category category : hwconf.getCategories() ) {
				logger.debug("HWConfs Categories: " + category.getName() + " " + category.getCategoryType());
				if( category.getCategoryType().equals("installation")) {
					for( Software software : category.getSoftwares() ) {
						toRemove.remove(software);
						for(Software requirements : software.getSoftwareRequirements() ) {
							toRemove.remove(requirements);
							toInstall.add(String.format("%04d-%s",requirements.getWeight(),requirements.getName()));
						}
						toInstall.add(String.format("%04d-%s",software.getWeight(),software.getName()));
					}
				}
			}
			for( Device device : hwconf.getDevices() ) {
				softwaresToInstall.get(device.getName()).addAll(toInstall);
				softwaresToRemove.get(device.getName()).addAll(toRemove);
			}
		}
		try {
			logger.debug("Software map:" + softwaresToInstall);
		} catch (Exception e) {
			logger.error(e.getMessage());
			return null;
		}

		//Write the hosts sls files
		for( Device device : deviceController.getAll() ) {
			if( device.getHwconf() == null || ! device.getHwconf().getDeviceType().equals("FatClient") ) {
				continue;
			}
			List<String> deviceRemove  = new ArrayList<String>();
			List<String> deviceGrains  = new ArrayList<String>();
			List<String> deviceInstall = new ArrayList<String>();
			List<String> deviceOssInst = new ArrayList<String>();
			deviceRemove.add("packages.toremove:");
			deviceRemove.add("  pkg.removed:");
			deviceRemove.add("    - pkgs:");

			//Remove first the softwares.
			if( softwaresToRemove.containsKey(device.getName()) ) {
				for( Software software : softwaresToRemove.get(device.getName()) ) {
					if( this.isSoftwareInstalledOnDevice(device, software) ){
						this.setSoftwareStatusOnDevice(device, software, "", "DS");
						deviceRemove.add("       - " + software.getName());
					}
					this.deleteSoftwareLicenseFromDevice(software,device);
				}
			}
			if( softwaresToInstall.containsKey(device.getName()) ) {
				softwaresToInstall.get(device.getName()).sort((String s1, String s2) -> { return s2.compareTo(s1); });
			}
			//Add packages to install
			List<String> normalizeSoftware = new ArrayList<String>();
			for( String softwareKey :  softwaresToInstall.get(device.getName()) ) {
				String softwareName = softwareKey.substring(5);
				//Take care to install software only once
				if( normalizeSoftware.contains(softwareName)) {
					continue;
				} else {
					normalizeSoftware.add(softwareName);
				}
				Software software               = this.getByName(softwareName);
				SoftwareVersion softwareVersion = null;
				for( SoftwareVersion sv : software.getSoftwareVersions() ) {
					if( sv.getStatus().equals("C") ) {
						softwareVersion = sv;
						break;
					}
				}
				//Allocate license to device
				if( ! software.getSoftwareLicenses().isEmpty() ) {
					if( ! this.addSoftwareLicenseToDevices(software,device).getCode().equals("OK") ) {
						//There is no license we can not install this.
						errorMessages.append("No license for ").append(softwareName).append(" on ").append(device.getName()).append(this.getNl());
						this.setSoftwareStatusOnDevice(device, software, softwareVersion.getVersion(), "LM");
						continue;
					}
				}
				// Set the software version status on device if not the actual version is already installed
				// The version status can be US or IS 
				if( !this.checkSoftwareVersionStatusOnDevice(device, softwareVersion, "I")) {
					this.setInstallUpdateOnDevice(software, softwareVersion, device);
				}
				//create the software package sls file name
				StringBuilder filePath = new StringBuilder(SALT_PACKAGE_DIR);
				filePath.append(softwareName).append(".sls");
				File file = new File(filePath.toString());
				if( file.exists() ) {
					for( SoftwareLicense sl : device.getSoftwareLicenses() ) {
						if( sl.getSoftware().equals(software) ) {
							deviceGrains.add(softwareName + "_KEY");
							deviceGrains.add("  grains.present:");
							deviceGrains.add("    - value: " + sl.getValue());
						}
					}
					/*
					 * TODO to implement frozen versions.
					 */
					deviceOssInst.add("  - " + softwareName);
				} else {
					deviceInstall.add(softwareName+":");
					deviceInstall.add("  - pkg:");
					deviceInstall.add("    - installed");
				}
			}
			List<String> deviceSls = new ArrayList<String>();
			deviceSls.add(device.getName() + ":");
			deviceSls.add("  system.computer_name: []");
			for( Partition partition : device.getHwconf().getPartitions() ) {
				if( partition.getJoinType().equals("Domain") || partition.getJoinType().equals("Simple") ) {
					deviceSls.add(domainName + ":");
					deviceSls.add("  system.join_domain:");
					deviceSls.add("    - username: register");
					deviceSls.add("    - password: " + registerPassword);
					deviceSls.add("    - restart: True");
					break;
				}
			}
			if( deviceRemove.size() > 3 ) {
				deviceSls.addAll(deviceRemove);
			}
			deviceSls.addAll(deviceInstall);
			deviceSls.addAll(deviceGrains);
			if( deviceOssInst.size() > 0 ) {
				deviceSls.add("include:");
				deviceSls.addAll(deviceOssInst);
			}

			if( deviceSls.size() > 0) {
				StringBuilder firstLine = new StringBuilder();
				firstLine.append("  ").append(device.getName()).append(".").append(domainName).append(":");

				topSls.add(firstLine.toString());
				topSls.add("    - oss_device_" + device.getName() );

				Path SALT_DEV   = Paths.get("/srv/salt/oss_device_" + device.getName() + ".sls");
				try {
					Files.write(SALT_DEV, deviceSls);
					Files.setPosixFilePermissions(SALT_DEV, groupReadDirPermission);
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
			this.systemctl("restart", "oss_salt_event_watcher");
		}
		//TO SET THE RIGHTS
		if( errorMessages.length() > 0 ) {
			return new OssResponse(this.getSession(),"ERROR",errorMessages.toString());
		}
		return new OssResponse(this.getSession(),"OK","Software State was saved succesfully"); 
	}

	/*
	 * Sets the software installation status on a device and remove the status of older version if the status is installed.
	 * 
	 * @param	device			The corresponding device object
	 * @param	softwareName	Name of the corresponding software package
	 * @param	version			The version of the corresponding software
	 * @param	status			The state to be set
	 * @return					An OssResponse object will be responded
	 */
	public OssResponse setSoftwareStatusOnDevice(Device device, String softwareName, String version, String status) {
		SoftwareStatus  softwareStatus  = null;
		Software        software        = this.getByName(softwareName);
		SoftwareVersion softwareVersion = null;
		EntityManager em = getEntityManager();
		logger.debug("setSoftwareStatusOnDevice called: " + softwareName + " ## " + version +" ## " + status);
		if( software == null ) {
			// Software does not exist. It is a manually installed software.
			logger.debug("Create new software:" + softwareName);
			software = new Software();
			software.setName(softwareName);
			software.setManually(true);
			software.setDescription(softwareName);
			try {
				em.getTransaction().begin();
				em.persist(software);
				em.getTransaction().commit();
			} catch (Exception e) {
				logger.error("Can not create software: " + e.getMessage());
				em.close();
				return new OssResponse(this.getSession(),"ERROR","Can not create software: " +e.getMessage());
			}		
		}

		//Search for the real software version
		try {
			logger.debug("Software versions:" + new ObjectMapper().writeValueAsString(software.getSoftwareVersions()));
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		for( SoftwareVersion sv :  software.getSoftwareVersions() ) {
			if( sv.getVersion().equals(version)) {
				softwareVersion = this.getSoftwareVersionById(sv.getId());
				break;
			}
		}

		if( softwareVersion == null ) {
			//This software version does not exists. We have create it
			softwareVersion = new SoftwareVersion(software,version,"U");
			logger.debug("Create new software version:" + softwareName + " ## " + version);
			try {
				em.getTransaction().begin();
				em.persist(softwareVersion);
				software.addSoftwareVersion(softwareVersion);
				em.merge(software);
				em.getTransaction().commit();
			} catch (Exception e) {
				logger.error("Can not create software version: " + e.getMessage());
				em.close();
				return new OssResponse(this.getSession(),"ERROR","Can not create software version " + e.getMessage());
			}
		}

		//We are searching for the status of this version of the software on the device.
		List<SoftwareStatus> softwareStatusToRemove = new ArrayList<SoftwareStatus>();
		try {
			//logger.debug("Software Status on Device:" + new ObjectMapper().writeValueAsString(device.getSoftwareStatus()));
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		for( SoftwareStatus st : device.getSoftwareStatus() ) {
			//logger.debug("Software Status of " + st.getSoftwareVersion().getSoftware().getName());
			if( st.getSoftwareVersion().equals(softwareVersion) ) {
				logger.debug("equal:" +st.getSoftwareVersion().getVersion());
				softwareStatus = st;
			} else if( status == "I" && st.getSoftwareVersion().getSoftware().equals(software)) {
				//Remove the other versions of the software if this is installed.
				softwareStatusToRemove.add(st);
			}
		}

		if( softwareStatus == null ) {
			//This software version has no status on this device. Let's create it.
			logger.debug("Create new software status:" + softwareName + " ## " + version + " ## " + status);
			softwareStatus = new SoftwareStatus(device,softwareVersion,status);
			try {
				em.getTransaction().begin();
				em.persist(softwareStatus);
				device.getSoftwareStatus().add(softwareStatus);
				softwareVersion.getSoftwareStatuses().add(softwareStatus);
				em.merge(device);
				em.merge(softwareVersion);
				em.getTransaction().commit();
			} catch (Exception e) {
				logger.error("Can not create software status:" + e.getMessage());
				em.close();
				return new OssResponse(this.getSession(),"ERROR","Can not create software status:" + e.getMessage());
			}
		} else {
			softwareStatus.setStatus(status);
			try {
				em.getTransaction().begin();
				em.merge(softwareStatus);		
				em.getTransaction().commit();
			} catch (Exception e) {
				logger.error("Can not modify software status:" + e.getMessage());
				em.close();
				return new OssResponse(this.getSession(),"ERROR","Can not modify software status:" + e.getMessage());
			}
		}
		
		//Remove the old software statuses
		try {
			try {
				logger.debug("Software status to remove:" + new ObjectMapper().writeValueAsString(softwareStatusToRemove));
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
			for( SoftwareStatus st : softwareStatusToRemove ) {
				em.getTransaction().begin();
				softwareStatus = em.merge(st);
				em.remove(softwareStatus);
				em.getTransaction().commit();
			}
		} catch (Exception e) {
			logger.error("Can not remove software status:" + e.getMessage());
			em.close();
			return new OssResponse(this.getSession(),"ERROR","Can not remove software status:" + e.getMessage());
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

	public OssResponse cleunUpSoftwareStatusOnDevice(Device device, Software software) {
		EntityManager em = getEntityManager();
		try {
			em.getTransaction().begin();
			for(SoftwareStatus st : device.getSoftwareStatus() ) {
				if( st.getSoftwareVersion().getSoftware().equals(software) ) {
					em.merge(st);
					em.remove(st);
				}
			}
			em.getTransaction().commit();
		} catch (Exception e) {
			logger.error(e.getMessage());
			return new OssResponse(this.getSession(),"ERROR",e.getMessage());
		} finally {
			em.close();
		}
		return new OssResponse(this.getSession(),"OK","All software states was removed from device.");
	}
	/*
	 * Delete Software Status
	 * 
	 *	 * @param	device			The corresponding device object
	 * @param	softwareName	Name of the corresponding software package
	 * @param	version			The version of the corresponding software
	 * @param	status			The state to be set
	 * @return					An OssResponse object will be responsed
	 * @param	Device device The corresponding device object
	 * @param 
	 */
	public OssResponse deleteSoftwareStatusFromDevice(Device device, String softwareName, String version ) {
		EntityManager em = getEntityManager();
		for(SoftwareStatus st : device.getSoftwareStatus() ) {
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

	public List<SoftwareStatus> getSoftwareStatusOnDevice(Device device, Long softwareId) {
		List<SoftwareStatus> softwareStatus = new ArrayList<SoftwareStatus>();
		for( SoftwareStatus st : device.getSoftwareStatus() ) {
			st.setSoftwareName(st.getSoftwareVersion().getSoftware().getName());
			st.setDeviceName(device.getName());
			if( softwareId < 1 || st.getSoftwareVersion().getSoftware().getId() == softwareId ) {
				st.setVersion(st.getSoftwareVersion().getVersion());
				st.setManually(st.getSoftwareVersion().getSoftware().getManually());
				softwareStatus.add(st);
			}
		}
		return softwareStatus;
	}

	public List<SoftwareStatus> getSoftwareStatusOnDeviceById(Long deviceId, Long softwareId) {
		Device  device = new DeviceController(this.session).getById(deviceId);
		return this.getSoftwareStatusOnDevice(device, softwareId);
	}

	public String getSoftwareLicencesOnDevice(String deviceName) {

		Device        device    =  new DeviceController(this.session).getByName(deviceName);
		StringBuilder softwares = new StringBuilder();
		for( SoftwareLicense license : device.getSoftwareLicenses() ) {
			softwares.append("'LIC_");
			softwares.append(license.getSoftware().getName());
			softwares.append("' '");
			softwares.append(license.getValue());
			softwares.append("'\\n");
		}
		return softwares.toString();
	}

	public List<Software> getSoftwareStatusById(List<Long> softwareIds) {
		List<Software> softwares = new ArrayList<Software>();
		for( Long i : softwareIds) {
			softwares.add(getById(i));
		}
		return softwares;
	}

	public OssResponse addRequirements(Software software, Software requirement) {
		EntityManager em = getEntityManager();
		try {
			em.getTransaction().begin();
			software.getSoftwareRequirements().add(requirement);
			software.getRequiredBy().add(software);
			em.merge(software);
			em.merge(requirement);
			em.getTransaction().commit();
		} catch (Exception e) {
			logger.error(e.getMessage());
			return new OssResponse(this.getSession(),"ERROR",e.getMessage());
		} finally {
			em.close();
		}
		return new OssResponse(this.getSession(),"OK","Software requirement was added successfully");
	}

	public OssResponse addRequirements(List<String> requirement) {
		return this.addRequirements(this.getByName(requirement.get(0)), this.getByName(requirement.get(1)));
	}

	public OssResponse addRequirements(long softwareId, long requirementId) {
		return this.addRequirements(this.getById(softwareId),this.getById(requirementId));
	}

	public OssResponse deleteRequirements(long softwareId, long requirementId) {
		// TODO Auto-generated method stub
		return null;
	}
}
