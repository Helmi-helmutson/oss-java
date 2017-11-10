/* (c) 2017 EXTIS GmbH (www.extis.de) - all rights reserved */
package de.openschoolserver.api.resourceimpl;

import java.io.File;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import javax.ws.rs.WebApplicationException;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.slf4j.LoggerFactory;

import de.claxss.importlib.ImportOrder;
import de.claxss.importlib.Importer;
import de.claxss.importlib.ImporterDescription;
import de.claxss.importlib.ImporterFactory;
import de.claxss.importlib.ImporterObject;
import de.openschoolserver.api.resources.ImporterResource;
import de.openschoolserver.dao.Session;
import de.openschoolserver.dao.internal.ImportHandler;

public class ImporterResourceImpl implements ImporterResource {
	private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(ImporterResourceImpl.class);

	@Override
	public List<ImporterDescription> getAvailableImporters(Session session, String objecttype) {
		ImporterFactory f = new ImporterFactory();
		List<ImporterDescription> importers = f
				.getImporterDescriptions(ImporterObject.ImportObjectType.valueOf(objecttype));
		return importers;
	}

	@Override
	public ImportOrder processImport(Session session, ImportOrder importOrder) {

		if (session.getTemporaryUploadData() != null && session.getTemporaryUploadData() instanceof ImportOrder) {
			// TODO handle cached file
			ImportOrder o = (ImportOrder) session.getTemporaryUploadData();
			if ((o != null) && (o.getImportJobId().equals(importOrder.getImportJobId()))) {
				ImporterFactory f = new ImporterFactory();

				Importer importer = f.getImporterInstance(importOrder.getImporterId());
				if (importer.startImport(o)) {
					ImportHandler h = new ImportHandler();
					String reslog = h.handleObjects(session, importer, o);
					importOrder.setImportResult(reslog);
					o.setPercentCompleted(50);
					importer.reset();
					o.setPercentCompleted(100); // TODO change with real
												// implementation
				}
			} else {
				throw new WebApplicationException(409);
			}
			/*
			 * result = (List<Employee>) session.getTemporaryUploadData();
			 * EmployeeController ec = new EmployeeController(); result =
			 * ec.importEmployees(result, session);
			 */

		}
		return importOrder;
	}

	@Override
	public String uploadImport(Session session, InputStream inputStream,
			FormDataContentDisposition contentDispositionHeader) {

		ImportOrder o = (ImportOrder) session.getTemporaryUploadData();
		LOG.info("uploadImport file size:" + contentDispositionHeader.getSize() + "import order job: "
				+ (o == null ? "no order" : o.getImportJobId()));
		if (o != null) {
			try {
				File file = File.createTempFile("oss_", ".ossb", new File("/opt/oss-java/tmp/"));
				Files.copy(inputStream, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
				o.setImportData(file);
			} catch (IOException e) {
				LOG.error(e.getMessage(), e);
				throw new WebApplicationException(500);
			}

		}
		return "upload done";
	}

	@Override
	public ImportOrder prepareImport(Session session, ImportOrder importOrder) {
		ImportOrder oldOrder = (ImportOrder) session.getTemporaryUploadData();
		if (oldOrder != null && oldOrder.getPercentCompleted() < 100) {
			throw new WebApplicationException(409);
		}
		Random r = new Random();
		int id = r.nextInt();
		importOrder.setImportJobId(String.valueOf(id));
		session.setTemporaryUploadData(importOrder);
		LOG.info("prepareImport order job: " + importOrder.getImportJobId());

		return importOrder;
	}

	@Override
	public ImportOrder getImportStatus(Session session, ImportOrder importOrder) {

		return (ImportOrder) session.getTemporaryUploadData();
	}

	@Override
	public ImportOrder cancelImport(Session session, ImportOrder importOrder) {
		ImportOrder o = (ImportOrder) session.getTemporaryUploadData();
		if ((o != null) && (o.getImportJobId().equals(importOrder.getImportJobId()))) {
			session.setTemporaryUploadData(null);
			// TODO stop processing
		}
		return importOrder;
	}

	
}
