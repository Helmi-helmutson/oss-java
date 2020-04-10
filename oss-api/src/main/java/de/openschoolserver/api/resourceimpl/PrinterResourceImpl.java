package de.openschoolserver.api.resourceimpl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.ws.rs.WebApplicationException;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.openschoolserver.api.resources.PrinterResource;
import de.openschoolserver.dao.Device;
import de.openschoolserver.dao.OssResponse;
import de.openschoolserver.dao.Printer;
import de.openschoolserver.dao.PrintersOfManufacturer;
import de.openschoolserver.dao.Session;
import de.openschoolserver.dao.controller.*;
import de.openschoolserver.dao.internal.CommonEntityManagerFactory;
import de.openschoolserver.dao.tools.OSSShellTools;
import static de.openschoolserver.dao.internal.OSSConstants.*;

public class PrinterResourceImpl implements PrinterResource {

	Logger logger = LoggerFactory.getLogger(PrinterResourceImpl.class);

	private Path PRINTERS  = Paths.get(cranixPrinters);

	public PrinterResourceImpl() {
	}

	@Override
	public List<Printer> getPrinters(Session session) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		List<Printer> resp = new PrinterController(session,em).getPrinters();
		em.close();
		return resp;
	}

	@Override
	public OssResponse deletePrinter(Session session, Long printerId) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		OssResponse resp = new PrinterController(session,em).deletePrinter(printerId);
		em.close();
		return resp;
	}

	@Override
	public OssResponse deletePrinter(Session session, String printerName) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		PrinterController pc = new PrinterController(session,em);
		Printer printer = pc.getByName(printerName);
		if( printer == null ) {
			em.close();
			throw new WebApplicationException(404);
		}
		OssResponse resp = pc.deletePrinter(printer.getId());
		em.close();
		return resp;
	}

	@Override
	public OssResponse resetPrinter(Session session, Long printerId) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		Printer printer = new PrinterController(session,em).getById(printerId);
		if( printer == null ) {
			em.close();
			throw new WebApplicationException(404);
		}
		OssResponse resp = resetPrinter(session, printer.getName());
		em.close();
		return resp;
	}

	@Override
	public OssResponse resetPrinter(Session session, String printerName) {
		String[] program = new String[4];
		StringBuffer reply  = new StringBuffer();
		StringBuffer stderr = new StringBuffer();
		program[0] = "/usr/bin/lprm";
		program[1] = "-P";
		program[2] = printerName;
		program[3] = "-";
		OSSShellTools.exec(program, reply, stderr, null);
		this.enablePrinter(session, printerName);
		return new OssResponse(session,"OK","Printer was reseted succesfully.");
	}

	@Override
	public OssResponse enablePrinter(Session session, Long printerId) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		PrinterController pc = new PrinterController(session,em);
		Printer printer = pc.getById(printerId);
		if( printer == null ) {
			em.close();
			throw new WebApplicationException(404);
		}
		OssResponse resp = pc.enablePrinter(printer.getName());
		em.close();
		return resp;
	}

	@Override
	public OssResponse enablePrinter(Session session, String printerName) {
		OssResponse resp = new PrinterController(session,null).enablePrinter(printerName);
		return resp;
	}

	@Override
	public OssResponse disablePrinter(Session session, Long printerId) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		PrinterController pc = new PrinterController(session,em);
		Printer printer = pc.getById(printerId);
		if( printer == null ) {
			throw new WebApplicationException(404);
		}
		OssResponse resp = pc.disablePrinter(printer.getName());
		em.close();
		return resp;
	}

	@Override
	public OssResponse disablePrinter(Session session, String printerName) {
		return new PrinterController(session,null).disablePrinter(printerName);
	}

	@Override
	public OssResponse activateWindowsDriver(Session session, Long printerId) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		Printer printer = new PrinterController(session,em).getById(printerId);
		em.close();
		if( printer == null ) {
			throw new WebApplicationException(404);
		}
		return activateWindowsDriver( session, printer.getName() );
	}

	@Override
	public OssResponse activateWindowsDriver(Session session, String printerName) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		OssResponse resp = new PrinterController(session,em).activateWindowsDriver(printerName);
		em.close();
		return resp;
	}

	@Override
	public OssResponse addPrinter(Session session,
			String name,
			String mac,
			Long roomId,
			String model,
			boolean windowsDriver,
			InputStream fileInputStream,
			FormDataContentDisposition contentDispositionHeader) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		OssResponse resp = new PrinterController(session,em).addPrinter(name.toLowerCase().trim(),mac,roomId,model,windowsDriver,fileInputStream,contentDispositionHeader);
		em.close();
		return resp;
	}

	@Override
	public Map<String,String[]> getAvailableDrivers(Session session) {
		Map<String,String[]> drivers = new HashMap<String,String[]>();
		try {
			for( String line : Files.readAllLines(PRINTERS) ) {
				String[] fields = line.split("###");
				if( fields.length == 2 ) {
					drivers.put(fields[0], fields[1].split("%%"));
				}
			}
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
		return drivers;
	}

	@Override
	public List<PrintersOfManufacturer> getDrivers(Session session) {
		List<PrintersOfManufacturer> printers = new ArrayList<PrintersOfManufacturer>();
		try {
			for( String line : Files.readAllLines(PRINTERS) ) {
				PrintersOfManufacturer printersOfManufacturer = new PrintersOfManufacturer();
				String[] fields = line.split("###");
				if( fields.length == 2 ) {
					printersOfManufacturer.setName(fields[0]);
					printersOfManufacturer.setPrinters(fields[1].split("%%"));
					printers.add(printersOfManufacturer);
				}
			}
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
		return printers;
	}

	@Override
	public OssResponse setDriver(Session session,
			Long printerId, InputStream fileInputStream,
			FormDataContentDisposition contentDispositionHeader) {

		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		Printer printer = new PrinterController(session,em).getById(printerId);
		em.close();
		if( printer == null ) {
			throw new WebApplicationException(404);
		}
		File file = null;
		try {
			file = File.createTempFile("oss_driverFile", printer.getName(), new File(cranixTmpDir));
			Files.copy(fileInputStream, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			return new OssResponse(session,"ERROR", e.getMessage());
		}
		String driverFile = file.toPath().toString();
		String[] program = new String[11];
		StringBuffer reply  = new StringBuffer();
		StringBuffer stderr = new StringBuffer();
		program[0] = "/usr/sbin/lpadmin";
		program[1] = "-p";
		program[2] = printer.getName();
		program[3] = "-P";
		program[4] = driverFile;
		program[5] = "-o";
		program[6] = "printer-error-policy=abort-job";
		program[7] = "-o";
		program[8] = "PageSize=A4";
		program[9] = "-v";
		program[10]= "socket://"+ printer.getName();

		OSSShellTools.exec(program, reply, stderr, null);
		logger.debug("activateWindowsDriver error" + stderr.toString());
		logger.debug("activateWindowsDriver reply" + reply.toString());
		//TODO check output
		return new OssResponse(session,"OK", "Printer driver was set succesfully.");
	}

	@Override
	public OssResponse addPrinterQueue(Session session, String name, Long deviceId, String model, boolean windowsDriver,
			InputStream fileInputStream, FormDataContentDisposition contentDispositionHeader) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		OssResponse resp = new PrinterController(session,em).addPrinterQueue(session,name.toLowerCase().trim(),deviceId,model,windowsDriver,fileInputStream,contentDispositionHeader);
		em.close();
		return resp;
	}

	@Override
	public List<Device> getPrinterDevices(Session session) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		List<Device> resp = new CloneToolController(session,em).getByName("Printer").getDevices();
		em.close();
		return resp;
	}

	@Override
	public Printer getPrinterById(Session session, Long printerId) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		Printer resp = new PrinterController(session,em).getById(printerId);
		em.close();
		return resp;
	}
}
