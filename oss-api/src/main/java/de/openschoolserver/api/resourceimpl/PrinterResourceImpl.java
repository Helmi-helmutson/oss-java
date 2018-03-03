package de.openschoolserver.api.resourceimpl;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.*;

import javax.ws.rs.WebApplicationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.openschoolserver.api.resources.PrinterResource;
import de.openschoolserver.dao.OssResponse;
import de.openschoolserver.dao.Printer;
import de.openschoolserver.dao.Device;
import de.openschoolserver.dao.Session;
import de.openschoolserver.dao.controller.DeviceController;
import de.openschoolserver.dao.tools.OSSShellTools;

public class PrinterResourceImpl implements PrinterResource {
	
	Logger logger = LoggerFactory.getLogger(PrinterResourceImpl.class);

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
				program[2] = "get-jobs.test";
				OSSShellTools.exec(program, reply, stderr, null);
				String jobs[] = reply.toString().split(deviceController.getNl());
				printer.setActiveJobs(jobs.length-2);
				printers.add(printer);
			}
		}
		return printers;
	}

	@Override
	public OssResponse deletePrinter(Session session, Long printerId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OssResponse resetPrinter(Session session, Long printerId) {
		// TODO Auto-generated method stub
		return null;
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
		return null;
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
		return null;
	}
	
	@Override
	public OssResponse activateWindowsDriver(Session session, Long printerId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OssResponse deletePrinter(Session session, String printerName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OssResponse resetPrinter(Session session, String printerName) {
		// TODO Auto-generated method stub
		return null;
	}




	@Override
	public OssResponse activateWindowsDriver(Session session, String printerName) {
		// TODO Auto-generated method stub
		return null;
	}

}
