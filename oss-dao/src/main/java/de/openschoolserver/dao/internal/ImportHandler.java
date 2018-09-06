/* (c) 2017 EXTIS GmbH - all rights reserved  */

package de.openschoolserver.dao.internal;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.slf4j.LoggerFactory;

import de.claxss.importlib.ImportOrder;
import de.claxss.importlib.Importer;
import de.claxss.importlib.ImporterObject;
import de.claxss.importlib.Person;
import de.claxss.importlib.SchoolClass;
import de.claxss.importlib.common.ImporterUtil;
import de.openschoolserver.dao.Group;
import de.openschoolserver.dao.OssResponse;
import de.openschoolserver.dao.Session;
import de.openschoolserver.dao.User;
import de.openschoolserver.dao.controller.GroupController;
import de.openschoolserver.dao.controller.UserController;

public class ImportHandler extends Thread {
	private Session session;
	private Importer importer;
	private ImportOrder order;
	StringBuilder responseString;
	boolean done = false;

	private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(ImportHandler.class);

	public ImportHandler(Session session, Importer importer, ImportOrder o) {
		this.session = session;
		this.importer = importer;
		this.order = o;
		responseString = new StringBuilder();
	}

	public String getResponseString() {
		return responseString.toString();
	}

	public void handleObjects() {
		start();
		// doHandleObjects();

		// return responseString.toString();
	}

	private void doHandleObjects() {
		ImporterObject object;
		int ctr = 0;
		try {
			do {

				// handle data
				object = importer.getNextObject();

				if (object != null) {
					String objectMsg = object.getObjectMessage();
					order.setPercentCompleted((100 / importer.getNumberOfObjects()) * ctr);
					ctr++;
					LOG.debug("found object: " + object.getClass());
					if (object instanceof de.claxss.importlib.SchoolClass) {
						if (!doCompareAndImportSchoolClass(session, (de.claxss.importlib.SchoolClass) object, order,
								responseString)) {
							appendLog(importer, order, "Import " + object.getObjectMessage() + ": FAILED");

						} else {

							appendLog(importer, order, "Import " + object.getObjectMessage() + ": OK");
						}
					} else if (object instanceof de.claxss.importlib.Person) {

						if (!doCompareAndImportUser(session, (de.claxss.importlib.Person) object, order, importer,
								responseString)) {

							appendLog(importer, order, "Import " + objectMsg + ": FAILED");
						} else {

							appendLog(importer, order, "Import " + objectMsg + ": OK");
						}
					}
					order.setImportResult(responseString.toString());
					// LOG.debug("Setting result string: " +
					// order.getImportResult());
					// try {
					// LOG.debug("Waiting for debugging....");
					// Thread.sleep(10000); // TODO remove me
					// } catch (InterruptedException e) {
					// LOG.error(e.getMessage(), e);
					// e.printStackTrace();
					// }
				}
			} while (object != null);
		} finally {
			closeLogfiles();
		}
		order.setPercentCompleted(100);
		done = true;
	}

	private OutputStream logfile = null;
	private OutputStream useraddLogfile = null;
	private String CSVSEP = ";";
	private String LINESEP = "\n";
	private String CLASSESSEP = ",";

	private void createLogfiles(Importer i, ImportOrder o) {

		if (logfile == null || useraddLogfile == null) {
			SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd.HH-mm-ss");
			String now = fmt.format(new Date());
			String filepath;
			String os = System.getProperty("os.name", "generic").toLowerCase(Locale.ENGLISH);
			if ((os.indexOf("mac") >= 0) || (os.indexOf("darwin") >= 0)) {
				filepath = "/tmp/userimport." + now;
			} else {
				filepath = "/home/groups/SYSADMINS/userimport." + now;
			}

			File dir = new File(filepath);
			dir.mkdir();
			File lf = new File(filepath + "/import.log");
			File uaf = new File(filepath + "/userlist.csv");
			try {
				if (logfile == null) {
					logfile = new FileOutputStream(lf);
					StringBuilder b = new StringBuilder();
					b.append(now).append(" Import: ").append(i.getImporterDescription().getName()).append(" Modus: ")
							.append(o.isTestOnly() ? "Test Import" : "Tatsächlicher Import").append(LINESEP);
					try {
						logfile.write(b.toString().getBytes("UTF-8"));
					} catch (UnsupportedEncodingException e) {
						LOG.error("createLogfiles: " + e.getMessage());
					} catch (IOException e) {
						LOG.error("createLogfiles: " + e.getMessage());
					}
				}
				if (useraddLogfile == null) {
					useraddLogfile = new FileOutputStream(uaf);
					StringBuilder b = new StringBuilder();
					b.append("LOGIN").append(CSVSEP).append("NACHNAME").append(CSVSEP).append("VORNAME").append(CSVSEP)
							.append("GEBURTSTAG").append(CSVSEP).append("KLASSE").append(CSVSEP).append("PASSWORT")
							.append(LINESEP);
					try {
						useraddLogfile.write(b.toString().getBytes("UTF-8"));
					} catch (UnsupportedEncodingException e) {
						LOG.error("createLogfiles: " + e.getMessage());
					} catch (IOException e) {
						LOG.error("createLogfiles: " + e.getMessage());
					}
				}
			} catch (FileNotFoundException e) {
				LOG.error("createLogfiles: " + e.getMessage());
			}
		}
		// less userlist.6B.txt
		// schooladmin.extis.test1:/home/groups/SYSADMINS/userimport.2016-09-12.10-52-03
		// BENUTZERK334RZEL:NACHNAME:VORNAME:GEBURTSTAG:KLASSE:LOGIN:PASSWORT
		// Abed Aziz:Jusuf:2016-08-10:6B:jusuabed02:

	}

	private void closeLogfiles() {
		if (logfile != null) {
			try {
				logfile.close();
			} catch (IOException e) {
				LOG.error("closeLogfiles logfile:" + e.getMessage());
			}
			logfile = null;
		}
		if (useraddLogfile != null) {
			try {
				useraddLogfile.close();
			} catch (IOException e) {
				LOG.error("closeLogfiles useraddLogfile:" + e.getMessage());
			}
			useraddLogfile = null;
		}
	}

	private void appendLog(Importer i, ImportOrder o, String msg) {
		createLogfiles(i, o);
		try {
			logfile.write(msg.getBytes("UTF-8"));
			logfile.write(LINESEP.getBytes());
		} catch (UnsupportedEncodingException e) {
			LOG.error("appendLog:" + e.getMessage());
		} catch (IOException e) {
			LOG.error("appendLog:" + e.getMessage());
		}
	}

	private String normalizeValue(String value) {
		if (value == null) {
			return "";
		} else if (value.contains(CSVSEP)) {
			if (value.contains("\"")) {
				value = value.replaceAll("\"", "\\\"");
			}
			return "\"" + value + "\"";
		}
		return value;
	}

	private String getCSVClasses(User user) {
		StringBuilder b = new StringBuilder();
		if (user.getGroups() != null) {
			if (user.getGroups().size() == 1) {
				return user.getGroups().get(0).getName();
			}
			int i = 0;
			for (Group group : user.getGroups()) {
				if ("class".equals(group.getGroupType())) {
					if (i > 0) {
						b.append(CLASSESSEP);
					}
					b.append(group.getName());
					i++;
				}
			}
			return b.toString();
		}
		return "";
	}

	private String extractPW(OssResponse res) {
		if (res.getParameters() != null && res.getParameters().size() >= 4) {
			return res.getParameters().get(3);
		}
		return "";
	}

	private void appendUserAddLog(Importer i, ImportOrder o, OssResponse res, User newUser, boolean create) {
		createLogfiles(i, o);
		try {
			StringBuilder buf = new StringBuilder();
			SimpleDateFormat fmt = new SimpleDateFormat("dd.MM.yyyy");
			String birthday = newUser.getBirthDay() != null ? fmt.format(newUser.getBirthDay()) : "";
			String classes = getCSVClasses(newUser);
			buf.append(newUser.getUid()).append(CSVSEP).append(normalizeValue(newUser.getSurName())).append(CSVSEP)
					.append(normalizeValue(newUser.getGivenName())).append(CSVSEP).append(birthday).append(CSVSEP)
					.append(normalizeValue(classes)).append(CSVSEP).append(res != null ? extractPW(res) : "")
					.append(LINESEP);
			useraddLogfile.write(buf.toString().getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			LOG.error("appendUserAddLog:" + e.getMessage());
		} catch (IOException e) {
			LOG.error("appendUserAddLog:" + e.getMessage());
		}

	}

	private boolean doCompareAndImportUser(Session session, Person person, ImportOrder o, Importer importer,
			StringBuilder responseString) {
		final UserController userController = new UserController(session);
		final GroupController groupController = new GroupController(session);
		Person existingUser = null;

		List<Person> oldUserList = buildOldUserlist(userController);
		List<Person> handledUsers = new ArrayList<Person>();
		
		if (person.getPersonNumber()!=null && person.getPersonNumber().length()>0) {
			// try to find per uuid
			for (Person person2 : oldUserList) {
				if (person.getPersonNumber().equals(person2.getPersonNumber())) {
					existingUser = person2;
					LOG.info("user  found by uuid: " + person.getPersonNumber() + " " + person.getFirstname() + " " + person.getName() + " "
							+ person.getLoginId() + " " + person.getBirthday());
				}
			}
		}
		if (existingUser!=null ) {
		  existingUser = ImporterUtil.findUserByUid(oldUserList, person);
		}
		if (existingUser == null) {
			LOG.info("user not found by uid: " + person.getFirstname() + " " + person.getName() + " "
					+ person.getLoginId() + " " + person.getBirthday());
		}
		if (existingUser == null) {
			if (o.getRequestedUserRole() != null && o.getRequestedUserRole().length() > 0
					&& o.getRequestedUserRole().equals("students")) {
				person.setPersonType(Person.PersonType.STUDENT);
				;
			}

			existingUser = ImporterUtil.findUser(o, oldUserList, person); // TODO
																			// handle
																			// whether
																			// to
																			// ignore
																			// birthday
																			// or
																			// not
																			// (additional
																			// boolean
																			// parameter
																			// provided
																			// by
																			// lib)
		}
		if (existingUser == null) {
			LOG.info("user not found: " + person.getFirstname() + " " + person.getName() + " " + person.getLoginId()
					+ " " + person.getBirthday());
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
			if (person.getName() != null && !person.getName().equals(ossUser.getSurName())) {
				ossUser.setSurName(person.getName());
				change = true;
			}
			if (person.getBirthday() != null && !person.getBirthday().equals(ossUser.getBirthDay())) {
				ossUser.setBirthDay(person.getBirthday());
				change = true;
			}
			if (change && !o.isTestOnly()) {
				OssResponse res = userController.modify(ossUser);
				appendUserAddLog(importer, o, res, ossUser, false);
			} else if (!o.isTestOnly()) {
				appendUserAddLog(importer, o, null, ossUser, false);
			}
			if (person.getSchoolClasses() != null && person.getSchoolClasses().size() > 0) {
				// use new classes
				List<SchoolClass> removeClasses = new ArrayList<SchoolClass>();
				List<SchoolClass> newClasses = new ArrayList<SchoolClass>();
				List<SchoolClass> keepClasses = new ArrayList<SchoolClass>();
				ImporterUtil.handleSchoolClassesDiff(existingUser, person, removeClasses, newClasses, keepClasses);
				StringBuilder bclasses = new StringBuilder();
				if (!o.isTestOnly()) {
					for (SchoolClass schoolClass : removeClasses) {
						groupController.removeMember(((Group) schoolClass.getData()).getId(), ossUser.getId());
					}
					for (SchoolClass schoolClass : newClasses) {

						Group group = groupController.getByName(schoolClass.getNormalizedName());
						if (group != null) {
							bclasses.append(group.getName()).append(" ");
							groupController.addMember(group.getId(), ossUser.getId());
						}
					}
				}
				responseString.append("Benutzer wird aktualisiert: ").append(existingUser.getName()).append(", ")
						.append(existingUser.getFirstname()).append(" ").append(bclasses.toString()).append(LINESEP);
			}
			if (change && !o.isTestOnly()) {
				userController.modify(ossUser);
			}
		} else {
			// create the user
			User newUser = new User();
			newUser.setUid(person.getLoginId());
			if (person.getPersonNumber()!=null && person.getPersonNumber().length()>0) {
			  newUser.setUuid(person.getPersonNumber());
			}
			newUser.setGivenName(person.getFirstname());
			newUser.setSurName(person.getName());
			newUser.setRole((o.getRequestedUserRole() != null && o.getRequestedUserRole().length()>0) ? o.getRequestedUserRole() : getOSSRole(person));
			if (person.getBirthday()!=null) {
			newUser.setBirthDay(person.getBirthday());
			} else {
				newUser.setBirthDay(new Date());
			}
			
			if (person.getPassword() != null && person.getPassword().length() > 0) {
				newUser.setPassword(person.getPassword());
			} else if (o.getNewUserPassword()!=null && o.getNewUserPassword().length()>0) {
				// handling of given new user password
				if (o.getNewUserPassword().equals("[teachers:random,students:birthday]")) {
					
					if ("students".equals(newUser.getRole())) {
						SimpleDateFormat fmt = new SimpleDateFormat("dd.MM.yyyy");
						newUser.setPassword(fmt.format(newUser.getBirthDay()));
			
					} else {
						// no password set -> random
			
					}
				} else {
					newUser.setPassword(o.getNewUserPassword());
				}
			}
			
			if (newUser.getUuid()!=null) {
				responseString.append(" uuid: ").append(newUser.getUuid());
			}
		
			
			OssResponse useraddRes = null;
			if (!o.isTestOnly()) {
				useraddRes = userController.add(newUser);
				if (useraddRes != null && useraddRes.getObjectId() != null) {
					newUser = userController.getById(useraddRes.getObjectId());
				} else {
					if (useraddRes == null) {
						LOG.error("userController.add returns no response");
						responseString.append("Benutzer kann nicht angelegt werden: ").append(newUser.getSurName())
								.append(", ").append(newUser.getGivenName()).append(LINESEP);
					} else {
						LOG.error("userController.add returns null as objectid in response: " + useraddRes.getCode()
								+ " " + useraddRes.getValue());
						responseString.append("Benutzer kann nicht angelegt werden: ").append(newUser.getSurName())
								.append(", ").append(newUser.getGivenName()).append(". Grund: ")
								.append(useraddRes.getValue()).append(LINESEP);
					}
					return false;
				}
				// appendUserAddLog(importer, o, res, newUser, true);
			} else {
				// appendUserAddLog(importer, o, null, newUser, true);
			}
			
			StringBuilder newUserClassesBuilder = new StringBuilder();
			if (newUser != null && newUser.getId() != null && !o.isTestOnly()) {
				if (person.getSchoolClasses() != null) {
					for (SchoolClass schoolClass : person.getSchoolClasses()) {
						Group group = groupController.getByName(schoolClass.getNormalizedName());
						if (group != null && group.getId() != null) {
							LOG.debug("Add user to classes" + newUser.getUid() + " " + group.getName());
							newUserClassesBuilder.append(group.getName()).append(" ");
							groupController.addMember(group.getId(), newUser.getId());
						} else {
							LOG.info("Group not found: " + schoolClass.getNormalizedName() + " " + (group != null
									? group.getName() : ""));
						}
					}
				}
			}
			responseString.append("Benutzer wird neu angelegt: ").append(newUser.getSurName()).append(", ")
			.append(newUser.getGivenName()).append(" ").append(newUserClassesBuilder);
			if (newUser.getUuid()!=null) {
				responseString.append(" uuid: ").append(newUser.getUuid());
			}
			responseString.append(LINESEP);
			if (!o.isTestOnly()) {
				// done here to get the classnames of the user
				newUser = userController.getById(useraddRes.getObjectId());
				appendUserAddLog(importer, o, useraddRes, newUser, true);
			} else {
				appendUserAddLog(importer, o, null, newUser, true);
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
			if ("students".equals(user.getRole())) {
				p.addRole(Person.PersonType.STUDENT);
			} else if ("teachers".equals(user.getRole())) {
				p.addRole(Person.PersonType.TEACHER);
			}
			p.setFirstname(user.getGivenName());
			p.setName(user.getSurName());
			p.setLoginId(user.getUid());
			p.setData(user);
			p.setPersonNumber(user.getUuid());
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
			return "students";

		case TEACHER:
			return "teachers";
		case PERSON:
			return "administration";
		}
		return "unknown";
	}

	private boolean doCompareAndImportSchoolClass(Session session, de.claxss.importlib.SchoolClass schoolClass,
			ImportOrder o, StringBuilder responseString) {
		// LOG.error("importing group: " + schoolClass.getNormalizedName());
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
				responseString.append("Neue Gruppe wird angelegt: ").append(newClass.getName()).append(LINESEP);
			}
		}

		return true;
	}

	@Override
	public void run() {
		doHandleObjects();
	}
}
