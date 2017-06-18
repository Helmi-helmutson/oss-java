package de.openschoolserver.api.resourceimpl;

import java.util.List;


import de.claxss.importlib.ImporterDescription;
import de.claxss.importlib.ImporterFactory;
import de.claxss.importlib.ImporterObject;
import de.openschoolserver.api.resources.ImporterResource;
import de.openschoolserver.dao.Session;

public class ImporterResourceImpl implements ImporterResource {

	@Override
	public List<ImporterDescription> getAvailableImporters(Session session, String objecttype) {
		ImporterFactory f = new ImporterFactory();
		List<ImporterDescription> importers = f.getImporterDescriptions(ImporterObject.ImportObjectType.valueOf(objecttype));
		return importers;
	}

}
