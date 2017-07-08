/* (c) 2017 EXTIS GmbH (www.extis.de) - all rights reserved */
package de.openschoolserver.api.resourceimpl;

import java.io.ByteArrayInputStream;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.text.TabExpander;
import javax.ws.rs.WebApplicationException;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.slf4j.LoggerFactory;

import de.claxss.importlib.ImportOrder;
import de.claxss.importlib.Importer;
import de.claxss.importlib.ImporterDescription;
import de.claxss.importlib.ImporterFactory;
import de.claxss.importlib.ImporterObject;
import de.claxss.importlib.Person;
import de.claxss.importlib.SchoolClass;
import de.openschoolserver.api.resources.ImporterResource;
import de.openschoolserver.dao.Group;
import de.openschoolserver.dao.Session;
import de.openschoolserver.dao.User;
import de.openschoolserver.dao.controller.GroupController;
import de.openschoolserver.dao.controller.UserController;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

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
		List<User> result = null;
		if (session.getTemporaryUploadData() != null && session.getTemporaryUploadData() instanceof ImportOrder) {
			// TODO handle cached file
			ImportOrder o = (ImportOrder) session.getTemporaryUploadData();
			if ((o != null) && (o.getImportJobId().equals(importOrder.getImportJobId()))) {
				ImporterFactory f = new ImporterFactory();

				Importer importer = f.getImporterInstance(importOrder.getImporterId());
				if (importer.startImport(o)) {
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

	private void handleObjects(Session session, Importer importer, ImportOrder o) {
		ImporterObject object;
		int ctr = 0;
		do {

			// handle data
			object = importer.getNextObject();
			if (object != null) {
				String objectMsg = object.getObjectMessage();
				o.setPercentCompleted((100 / importer.getNumberOfObjects()) * ctr);
				ctr++;

				if (object instanceof de.claxss.importlib.SchoolClass) {
					if (!doCompareAndImportSchoolClass(session, (de.claxss.importlib.SchoolClass) object, o)) {
						// appendLog("Import " + object.getObjectMessage() + ":
						// FAILED");

					} else {

						// appendLog("Import " + object.getObjectMessage() + ":
						// OK");
					}
				} else if (object instanceof de.claxss.importlib.Person) {
					if (!doCompareAndImportUser(session, (de.claxss.importlib.Person) object, o)) {

						// appendLog("Import " + objectMsg + ": FAILED");
					} else {

						// appendLog("Import " + objectMsg + ": OK");
					}
				}
			}
		} while (object != null);

	}

	protected boolean doCompareAndImportUser(Session session, de.claxss.importlib.Person person, ImportOrder o) {
		final UserController userController = new UserController(session);
		User existingUser = null;
		/* ========== first step: try to find the user ============ */
		if (person.getLoginId() != null && person.getLoginId().length() > 0) {
			// first try to find via uid
			existingUser = userController.getByUid(person.getLoginId());
		}
		if (existingUser == null) {
			// user not found via uid, try to find via name
			List<User> possibleUsers;
			if (o.getRequestedUserRole() != null && o.getRequestedUserRole().length() > 0) {
				possibleUsers = userController.findByNameAndRole(person.getFirstname(), person.getName(),
						o.getRequestedUserRole());
				// try to find a user with a given role via First and LastName
			} else {
				// try to find via firstname and lastname
				possibleUsers = userController.findByName(person.getFirstname(), person.getName());
			}
			if (possibleUsers != null && possibleUsers.size() > 0) {
				if (possibleUsers.size() == 1) {
					if ("student".equals(o.getRequestedUserRole())) { // birthday
																		// has
																		// to
																		// mach
						if (possibleUsers.get(0).getBirthDay().equals(person.getBirthday())) {
							existingUser = possibleUsers.get(0);
						}
					} else {
						existingUser = possibleUsers.get(0);
					}
				} else {
					// more than one found via name and role is unclear ->
					// birthday has to mach
					for (User user : possibleUsers) {
						if (user.getBirthDay().equals(person.getBirthday())) {
							existingUser = user;
							break;
						}
					}
				}

			}
		}
		/* ========== second step: create or update the user ============ */
		if (existingUser != null) {
			// update the user
			boolean change = false;
			if (person.getLoginId() != null && !person.getLoginId().equals(existingUser.getUid())) {
				existingUser.setUid(person.getLoginId());
				change = true;
			}
			if (person.getFirstname() != null && !person.getFirstname().equals(existingUser.getGivenName())) {
				existingUser.setGivenName(person.getFirstname());
				change = true;
			}
			if (person.getName() != null && !person.getName().equals(existingUser.getSureName())) {
				existingUser.setSureName(person.getName());
				change = true;
			}
			if (person.getBirthday() != null && !person.getBirthday().equals(existingUser.getBirthDay())) {
				existingUser.setBirthDay(person.getBirthday());
				change = true;
			}
			if (person.getSchoolClasses() != null && person.getSchoolClasses().size() > 0) {
				// use new classes
				List<Group> oldClasses  = new ArrayList<Group>();
				List<Group> newClasses  = new ArrayList<Group>();
				List<Group> keepClasses = new ArrayList<Group>();
				collectClassesOfUser(existingUser, oldClasses);
				for (SchoolClass schoolclass : person.getSchoolClasses()) {
					Group existing = findClassInList(oldClasses, schoolclass.getNormalizedName());
					if (existing == null) {
						Group newClass = createClassFromPerson(newClasses, schoolclass);
						newClasses.add(newClass);
					} else {
						oldClasses.remove(existing);
						keepClasses.add(existing);
					}
				}
				if (!o.isTestOnly()) {
					existingUser.getGroups().removeAll(oldClasses); // remove
																	// classes
																	// not found
																	// in the
																	// import
				}
				existingUser.getGroups().addAll(keepClasses); // return the
																// classes
																// temporarily
																// removed from
																// the list
				if (!o.isTestOnly()) {
					existingUser.getGroups().addAll(newClasses); // add the
																	// newly
																	// found
																	// classes
				}
			}
			if (change && !o.isTestOnly()) {
				userController.modify(existingUser);
			}
		} else {
			// create the user
			User newUser = new User();
			newUser.setUid(person.getLoginId());
			newUser.setGivenName(person.getFirstname());
			newUser.setSureName(person.getName());
			newUser.setRole(o.getRequestedUserRole() != null ? o.getRequestedUserRole() : getOSSRole(person));
			newUser.setBirthDay(person.getBirthday());
			List<Group> newClasses = new ArrayList<Group>();
			for (SchoolClass schoolclass : person.getSchoolClasses()) {
				Group newClass = createClassFromPerson(newClasses, schoolclass);
				newClasses.add(newClass);
			}
			newUser.setGroups(newClasses);
			if (!o.isTestOnly()) {
				userController.add(newUser);
			}
		}
		//TODO handle old users
		return true;
	}

	private Group createClassFromPerson(List<Group> newClasses, SchoolClass schoolclass) {
		Group newClass = new Group();
		newClass.setGroupType("class");
		newClass.setName(schoolclass.getNormalizedName());
		newClass.setDescription(schoolclass.getLongName());
		return newClass;
	}

	private Group findClassInList(List<Group> oldClasses, String name) {
		for (Group group : oldClasses) {
			if (group.getName().equalsIgnoreCase(name)) {
				return group;
			}
		}
		return null;
	}

	private void collectClassesOfUser(User existingUser, List<Group> oldClasses) {
		if (existingUser.getGroups() != null) {
			for (Group group : existingUser.getGroups()) {
				if ("class".equals(group.getGroupType())) {
					oldClasses.add(group);
				}
			}
		}
	}

	private String getOSSRole(Person p) {
		switch (p.getPersonType()) {
		case STUDENT:
			return "student";

		case TEACHER:
			return "teacher";
		case PERSON:
			return "administration";
		}
		return "unknown";
	}

	protected boolean doCompareAndImportSchoolClass(Session session, de.claxss.importlib.SchoolClass schoolClass,
			ImportOrder o) {
		if (schoolClass != null && schoolClass.getNormalizedName() != null) {
			final GroupController groupController = new GroupController(session);
			final Group existingClass = groupController.getByName(schoolClass.getNormalizedName());

			if (existingClass == null) {
				Group newClass = new Group();
				newClass.setName(schoolClass.getNormalizedName());
				newClass.setDescription(schoolClass.getLongName());
				newClass.setGroupType("class");
				if (!o.isTestOnly()) {
					groupController.add(newClass);
				}

			}
		}

		return true;
	}
}
