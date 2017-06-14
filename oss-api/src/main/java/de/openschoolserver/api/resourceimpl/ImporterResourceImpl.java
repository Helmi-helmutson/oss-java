package de.openschoolserver.api.resourceimpl;

import java.io.InputStream;
import java.util.List;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

import de.claxss.importlib.ImporterDescription;
import de.claxss.importlib.ImporterFactory;
import de.claxss.importlib.ImporterObject;
import de.openschoolserver.api.resources.ImporterResource;
import de.openschoolserver.dao.Session;
import de.openschoolserver.dao.User;

public class ImporterResourceImpl implements ImporterResource {

	@Override
	public List<ImporterDescription> getAvailableImporters(Session session, String objecttype) {
		ImporterFactory f = new ImporterFactory();
		List<ImporterDescription> importers = f.getImporterDescriptions(ImporterObject.ImportObjectType.valueOf(objecttype));
		return importers;
	}

	@Override
	public List<User> processImportUser(Session session) {
		List<User> result = null;
		if (session.getTemporaryUploadData() != null && session.getTemporaryUploadData() instanceof List<?>) {
			/*result = (List<Employee>) session.getTemporaryUploadData();
			EmployeeController ec = new EmployeeController();
			result = ec.importEmployees(result, session);*/
			//TODO

		}
		return result;
	}

	@Override
	public String uploadImport(Session session, InputStream fileInputStream,
			FormDataContentDisposition contentDispositionHeader) {
	

			// TODO
			session.setTemporaryUploadData(null);

			return "found records: 0 " ; //TODO
		
	}

}
