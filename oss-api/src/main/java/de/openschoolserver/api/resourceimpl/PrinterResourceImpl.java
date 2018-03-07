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
import java.util.regex.*;

import javax.ws.rs.WebApplicationException;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.openschoolserver.api.resources.PrinterResource;
import de.openschoolserver.dao.OssResponse;
import de.openschoolserver.dao.Printer;
import de.openschoolserver.dao.Device;
import de.openschoolserver.dao.HWConf;
import de.openschoolserver.dao.Session;
import de.openschoolserver.dao.controller.*;
import de.openschoolserver.dao.tools.OSSShellTools;

public class PrinterResourceImpl implements PrinterResource {
	
	Logger logger = LoggerFactory.getLogger(PrinterResourceImpl.class);
	private Path DRIVERS   = Paths.get("/usr/share/oss/templates/drivers.txt");
	private Path PRINTERS  = Paths.get("/usr/share/oss/templates/printers.txt");

	public PrinterResourceImpl() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public List<Printer> getPrinters(Session session) {
		List<Printer> printers = new ArrayList<Printer>();
		DeviceController deviceController = new DeviceController(session);
		String[] program = new String[3];
		StringBuffer reply  = new StringBuffer();
		StringBuffer stderr = new StringBuffer();
		program[0] = "/usr/bin/ipptool";
		program[1] = "ipp://localhost/printers/";
		program[2] = "get-printers.test";
		OSSShellTools.exec(program, reply, stderr, null);
		String[] lines = reply.toString().split(deviceController.getNl());
		Pattern pattern = Pattern.compile("\\S+");
		for( int i=2 ; i < lines.length; i ++) {
			logger.debug("printer:" + lines[i]);
			Matcher matcher = pattern.matcher(lines[i]);
			if( matcher.find()) {
				String  name    = matcher.group(0);
				Printer printer = new Printer(name,deviceController);
				if( matcher.find() ) {
					printer.setState(matcher.group(0));
				}
				if( matcher.find() ) {
					printer.setAcceptingJobs(matcher.group(0).equals("true"));
				}
				if( matcher.find() ) {
					printer.setActiveJobs(Integer.getInteger(matcher.group(0)));
				}
				// Test if the windows driver was activated
				File file = new File("/var/lib/printserver/drivers/x64/3/"+name+".ppd");
				if( file.exists() ) {
					printer.setWindowsDriver(true);
				}
				printers.add(printer);
			}
		}
		return printers;
	}

	@Override
	public OssResponse deletePrinter(Session session, Long printerId) {
		DeviceController devcieController = new DeviceController(session);
		Device printer = devcieController.getById(printerId);
		if( printer == null ) {
			throw new WebApplicationException(404);
		}
		if( printer.getHwconf().getDeviceType().equals("Printer")) {
			throw new WebApplicationException(405);
		}
		String[] program    = new String[3];
		StringBuffer reply  = new StringBuffer();
		StringBuffer stderr = new StringBuffer();
		program[0] = "/usr/sbin/lpadmin";
		program[1] = "-x";
		program[2] = printer.getName();
		OSSShellTools.exec(program, reply, stderr, null);
		return devcieController.delete(printerId, true);
	}

	@Override
	public OssResponse deletePrinter(Session session, String printerName) {
		Device printer = new DeviceController(session).getByName(printerName);
		if( printer == null ) {
			throw new WebApplicationException(404);
		}
		if( printer.getHwconf().getDeviceType().equals("Printer")) {
			throw new WebApplicationException(405);
		}
		return deletePrinter(session, printer.getId());
	}

	@Override
	public OssResponse resetPrinter(Session session, Long printerId) {
		Device printer = new DeviceController(session).getById(printerId);
		if( printer == null ) {
			throw new WebApplicationException(404);
		}
		if( printer.getHwconf().getDeviceType().equals("Printer")) {
			throw new WebApplicationException(405);
		}
		return resetPrinter(session, printer.getName());
	}

	@Override
	public OssResponse resetPrinter(Session session, String printerName) {
		String[] program = new String[2];
		StringBuffer reply  = new StringBuffer();
		StringBuffer stderr = new StringBuffer();
		program[0] = "/usr/bin/lprm";
		program[1] = "-P";
		program[2] = printerName;
		program[3] = "-";
		OSSShellTools.exec(program, reply, stderr, null);
		return new OssResponse(session,"OK","Printer was reseted succesfully.");
	}

	@Override
	public OssResponse enablePrinter(Session session, Long printerId) {
		Device printer = new DeviceController(session).getById(printerId);
		if( printer == null ) {
			throw new WebApplicationException(404);
		}
		if( printer.getHwconf().getDeviceType().equals("Printer")) {
			throw new WebApplicationException(405);
		}
		return enablePrinter(session, printer.getName());
	}

	@Override
	public OssResponse enablePrinter(Session session, String printerName) {
		String[] program = new String[2];
		StringBuffer reply  = new StringBuffer();
		StringBuffer stderr = new StringBuffer();
		program[0] = "/usr/sbin/cupsenable";
		program[1] = printerName;
		OSSShellTools.exec(program, reply, stderr, null);
		return new OssResponse(session,"OK","Printer was enabled succesfully.");
	}

	@Override
	public OssResponse disablePrinter(Session session, Long printerId) {
		Device printer = new DeviceController(session).getById(printerId);
		if( printer == null ) {
			throw new WebApplicationException(404);
		}
		if( printer.getHwconf().getDeviceType().equals("Printer")) {
			throw new WebApplicationException(405);
		}
		return disablePrinter(session, printer.getName());
	}

	@Override
	public OssResponse disablePrinter(Session session, String printerName) {
		String[] program = new String[2];
		StringBuffer reply  = new StringBuffer();
		StringBuffer stderr = new StringBuffer();
		program[0] = "/usr/sbin/cupsdisable";
		program[1] = printerName;
		OSSShellTools.exec(program, reply, stderr, null);
		return new OssResponse(session,"OK","Printer was disabled succesfully.");
	}
	
	@Override
	public OssResponse activateWindowsDriver(Session session, Long printerId) {
		Device printer = new DeviceController(session).getById(printerId);
		if( printer == null ) {
			throw new WebApplicationException(404);
		}
		if( printer.getHwconf().getDeviceType().equals("Printer")) {
			throw new WebApplicationException(405);
		}
		return activateWindowsDriver( session, printer.getName() );
	}

	@Override
	public OssResponse activateWindowsDriver(Session session, String printerName) {
		//Create the windows driver
		String[] program     = new String[6];
		StringBuffer reply   = new StringBuffer();
		StringBuffer stderr  = new StringBuffer();
		program[0] = "/usr/sbin/cupsaddsmb";
		program[1] = "-H";
		program[2] = "printserver";
		program[3] = "-U";
		program[4] = "cephalix%" + new Controller(session).getProperty("de.openschoolserver.dao.User.Cephalix.Password");
		program[5] = printerName;
		OSSShellTools.exec(program, reply, stderr, null);
		if( stderr.length() > 0 ) {
			return new OssResponse(session,"ERROR", stderr.toString());
		}
		return new OssResponse(session,"OK","Windows driver was activated.");
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
		
		
		//First we create a device object
		RoomController roomController = new RoomController(session);
		HWConf hwconf = new CloneToolController(session).getByName("Printer");
		Device device = new Device();
		device.setMac(mac);
		device.setName(name);
		device.setHwconf(hwconf);
		List<Device> devices = new ArrayList<Device>();
		devices.add(device);
		
		//Persist the device object
		OssResponse ossResponse = roomController.addDevices(roomId, devices);
		if( ossResponse.getCode().equals("ERROR")) {
			return ossResponse;
		}
		
		//Create the printer in CUPS
		String driverFile = "";
		if( fileInputStream != null ) {
			File file = null;
			try {
				file = File.createTempFile("oss_driverFile", name, new File("/opt/oss-java/tmp/"));
				Files.copy(fileInputStream, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
				return new OssResponse(session,"ERROR", e.getMessage());
			}
			driverFile = file.toPath().toString();
		} else {
			try {
				for( String line : Files.readAllLines(DRIVERS) ) {
					String[] fields = line.split("###");
					if( fields.length == 2 && fields[0].equals(model) ) {
						driverFile = fields[1];
						break;
					}
				}
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
				return new OssResponse(session,"ERROR", e.getMessage());
			}
		}
		String[] program = new String[11];
		StringBuffer reply  = new StringBuffer();
		StringBuffer stderr = new StringBuffer();
		program[0] = "/usr/sbin/lpadmin";
		program[1] = "-p";
		program[2] = name;
		program[3] = "-P";
		program[4] = driverFile;
		program[5] = "-o";
		program[6] = "printer-error-policy=abort-job";
		program[7] = "-o";
		program[8] = "PageSize=A4";
		program[9] = "-v";
		program[10]= "socket://"+ name;
				
		OSSShellTools.exec(program, reply, stderr, null);
		
		if(windowsDriver) {
			ossResponse = activateWindowsDriver(session,name);

			if( ossResponse.getCode().equals("ERROR")) { 
				return ossResponse;
			}
		}

		return new OssResponse(session,"OK", "Printer was created succesfully.");
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

}
