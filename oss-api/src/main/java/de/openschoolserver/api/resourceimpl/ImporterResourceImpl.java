/* (c) 2017 EXTIS GmbH (www.extis.de) - all rights reserved */
package de.openschoolserver.api.resourceimpl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;

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
import de.claxss.importlib.common.ImporterUtil;
import de.openschoolserver.api.resources.ImporterResource;
import de.openschoolserver.dao.Group;
import de.openschoolserver.dao.Session;
import de.openschoolserver.dao.User;
import de.openschoolserver.dao.controller.GroupController;
import de.openschoolserver.dao.controller.UserController;

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
					handleObjects(session, importer, o);
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
				LOG.debug("found object: " + object.getClass());
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

	protected boolean doCompareAndImportUser(Session session, Person person, ImportOrder o) {
		final UserController userController = new UserController(session);
		final GroupController groupController = new GroupController(session);
		Person existingUser = null;

		List<Person> oldUserList = buildOldUserlist(userController);
		List<Person> handledUsers = new ArrayList<Person>();
		existingUser = ImporterUtil.findUserByUid(oldUserList, person);
		if (existingUser==null) {
			LOG.error("user not found by uid: " + person.getFirstname() + " " + person.getName() + " " + person.getLoginId() + " " + person.getBirthday());
		}
		if (existingUser == null) {
			existingUser = ImporterUtil.findUser(o, oldUserList, person);
		}
		if (existingUser==null) {
			LOG.error("user not found: " + person.getFirstname() + " " + person.getName() + " " + person.getLoginId() + " " + person.getBirthday());
		}
		/* ========== second step: create or update the user ============ */
		if (existingUser != null) {
			handledUsers.add(existingUser);
			User ossUser = (User) existingUser.getData();
			// update the user
			boolean change = false;
			if (person.getLoginId() != null && !person.getLoginId().equals(ossUser.getUid())) {
				ossUser.setUid(person.getLoginId());
				change = true;
			}
			if (person.getFirstname() != null && !person.getFirstname().equals(ossUser.getGivenName())) {
				ossUser.setGivenName(person.getFirstname());
				change = true;
			}
			if (person.getName() != null && !person.getName().equals(ossUser.getSureName())) {
				ossUser.setSureName(person.getName());
				change = true;
			}
			if (person.getBirthday() != null && !person.getBirthday().equals(ossUser.getBirthDay())) {
				ossUser.setBirthDay(person.getBirthday());
				change = true;
			}
			if (change && !o.isTestOnly()) {
				userController.modify(ossUser);
			}
			if (person.getSchoolClasses() != null && person.getSchoolClasses().size() > 0) {
				// use new classes
				List<SchoolClass> removeClasses = new ArrayList<SchoolClass>();
				List<SchoolClass> newClasses = new ArrayList<SchoolClass>();
				List<SchoolClass> keepClasses = new ArrayList<SchoolClass>();
				ImporterUtil.handleSchoolClassesDiff(existingUser, person, removeClasses, newClasses, keepClasses);

				if (!o.isTestOnly()) {
					for (SchoolClass schoolClass : removeClasses) {
						groupController.removeMember(((Group) schoolClass.getData()).getId(), ossUser.getId());
					}
					for (SchoolClass schoolClass : newClasses) {

						Group group = groupController.getByName(schoolClass.getNormalizedName());
						if (group != null) {

							groupController.addMember(group.getId(), ossUser.getId());
						}
					}
				}

			}
			if (change && !o.isTestOnly()) {
				userController.modify(ossUser);
			}
		} else {
			// create the user
			User newUser = new User();
			newUser.setUid(person.getLoginId());
			newUser.setGivenName(person.getFirstname());
			newUser.setSureName(person.getName());
			newUser.setRole(o.getRequestedUserRole() != null ? o.getRequestedUserRole() : getOSSRole(person));
			newUser.setBirthDay(person.getBirthday());
			if (!o.isTestOnly()) {
				userController.add(newUser);
				newUser = userController.getByUid(person.getLoginId());
			}
			if (newUser != null) {
				if (person.getSchoolClasses() != null) {
					for (SchoolClass schoolClass : person.getSchoolClasses()) {
						Group group = groupController.getByName(schoolClass.getNormalizedName());
						if (group != null) {

							groupController.addMember(group.getId(), newUser.getId());
						} else {
							LOG.error("Group not found: " + schoolClass.getNormalizedName());
						}
					}
				}
			}

		}
		// TODO handle old users
		return true;
	}

	private List<Person> buildOldUserlist(final UserController userController) {
		/* get old list */
		List<User> oldUserlist = null;
		List<Person> oldUserlistIL = new ArrayList<Person>();

		oldUserlist = userController.getAll();

		for (User user : oldUserlist) {
			Person p = new Person();
			p.setBirthday(user.getBirthDay());
			if ("student".equals(user.getRole())) {
				p.addRole(Person.PersonType.STUDENT);
			} else if ("teacher".equals(user.getRole())) {
				p.addRole(Person.PersonType.TEACHER);
			}
			p.setFirstname(user.getGivenName());
			p.setName(user.getSureName());
			p.setLoginId(user.getUid());
			p.setData(user);
			for (Group group : user.getGroups()) {
				if ("class".equals(group.getGroupType())) {
					SchoolClass sc = new SchoolClass(group.getName());
					sc.setNormalizedName(group.getName());
					sc.setLongName(group.getDescription());
					sc.setData(group);
					p.addSchoolClass(sc);
				}
			}
			oldUserlistIL.add(p);
		}
		return oldUserlistIL;
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
		LOG.error("importing group: " + schoolClass.getNormalizedName());
		if (schoolClass != null && schoolClass.getNormalizedName() != null) {
			final GroupController groupController = new GroupController(session);
			final Group existingClass = groupController.getByName(schoolClass.getNormalizedName());

			if (existingClass == null) {
				Group newClass = new Group();
				newClass.setName(schoolClass.getNormalizedName());
				newClass.setDescription(schoolClass.getLongName() != null ? schoolClass.getLongName()
						: schoolClass.getNormalizedName());
				newClass.setGroupType("class");
				if (!o.isTestOnly()) {
					groupController.add(newClass);
				}

			}
		}

		return true;
	}
}
