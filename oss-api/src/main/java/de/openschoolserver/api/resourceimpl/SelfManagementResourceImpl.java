package de.openschoolserver.api.resourceimpl;

import javax.persistence.EntityManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.openschoolserver.api.resources.SelfManagementResource;
import de.openschoolserver.dao.OssResponse;
import de.openschoolserver.dao.Session;
import de.openschoolserver.dao.User;
import de.openschoolserver.dao.controller.UserController;
import de.openschoolserver.dao.tools.OSSShellTools;

public class SelfManagementResourceImpl implements SelfManagementResource {

	Logger logger = LoggerFactory.getLogger(SelfManagementResource.class);

	public SelfManagementResourceImpl() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public User getBySession(Session session) {
		return session.getUser();
	}

	@Override
	public OssResponse modifyMySelf(Session session, User user) {
		UserController userController = new UserController(session);
		User oldUser = session.getUser();
		OssResponse  ossResponse = null;
		logger.debug("modifyMySelf" + user);
		if( userController.isAllowed("myself.manage") ) {
			if( user.getPassword() != null && !user.getPassword().isEmpty() ) {
				ossResponse = userController.checkPassword(user.getPassword());
				if( ossResponse.getCode().equals("ERROR")) {
					return ossResponse;
				}
			}
			oldUser.setGivenName(user.getGivenName());
			oldUser.setSurName(user.getSurName());
			oldUser.setBirthDay(user.getBirthDay());
			oldUser.setFsQuota(user.getFsQuota());
			oldUser.setMsQuota(user.getMsQuota());
			EntityManager em = userController.getEntityManager(); 
			try {
				em.getTransaction().begin();
				em.merge(oldUser);
				em.getTransaction().commit();
				userController.startPlugin("modify_user", oldUser);
			} catch (Exception e) {
				return null;
			} finally {
				em.close();
			}
			ossResponse = new OssResponse(session,"OK","User parameters were set successfully.");
		} else {
			if( user.getPassword() != null && !user.getPassword().isEmpty() ) {
				ossResponse = userController.checkPassword(user.getPassword());
				if( ossResponse != null  && ossResponse.getCode().equals("ERROR")) {
					logger.debug("checkPassword:" + ossResponse);
					return ossResponse;
				}
				StringBuffer reply = new StringBuffer();
				StringBuffer error = new StringBuffer();
				String[]   program = new String[5];
				program[0] = "/usr/bin/samba-tool";
				program[1] = "user";
				program[2] = "setpassword";
				program[3] = session.getUser().getUid();
				program[4] = "--newpassword=" + user.getPassword();
				OSSShellTools.exec(program, reply, error, null);
				logger.debug("sambatool:" + reply.toString() + " Error" + error.toString() );
				ossResponse = new OssResponse(session,"OK","User parameters were set successfully.");
			}
		}
		return ossResponse;
	}

}
