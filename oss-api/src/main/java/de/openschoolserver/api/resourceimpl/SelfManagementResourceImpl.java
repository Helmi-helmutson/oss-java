package de.openschoolserver.api.resourceimpl;

import java.io.File;

import javax.persistence.EntityManager;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.openschoolserver.api.resources.SelfManagementResource;
import de.openschoolserver.dao.Group;
import de.openschoolserver.dao.OssResponse;
import de.openschoolserver.dao.Session;
import de.openschoolserver.dao.User;
import de.openschoolserver.dao.controller.Config;
import de.openschoolserver.dao.controller.UserController;
import de.openschoolserver.dao.internal.CommonEntityManagerFactory;
import de.openschoolserver.dao.tools.OSSShellTools;

public class SelfManagementResourceImpl implements SelfManagementResource {

	Logger logger = LoggerFactory.getLogger(SelfManagementResource.class);

	public SelfManagementResourceImpl() {
		super();
	}

	@Override
	public User getBySession(Session session) {
		return session.getUser();
	}

	@Override
	public OssResponse modifyMySelf(Session session, User user) {
		EntityManager em = CommonEntityManagerFactory.instance("dummy").getEntityManagerFactory().createEntityManager();
		UserController userController = new UserController(session,em);
		User oldUser = session.getUser();
		OssResponse  ossResponse = null;
		logger.debug("modifyMySelf" + user);
		if( userController.isAllowed("myself.manage") ) {
			if( user.getPassword() != null && !user.getPassword().isEmpty() ) {
				ossResponse = userController.checkPassword(user.getPassword());
				logger.debug("Check-Password:" + ossResponse );
				if( ossResponse != null  && ossResponse.getCode().equals("ERROR")) {
					return ossResponse;
				}
				oldUser.setPassword(user.getPassword());
			}
			oldUser.setGivenName(user.getGivenName());
			oldUser.setSurName(user.getSurName());
			oldUser.setBirthDay(user.getBirthDay());
			oldUser.setFsQuota(user.getFsQuota());
			oldUser.setMsQuota(user.getMsQuota());
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
				em.close();
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

	@Override
	public Boolean haveVpn(Session session) {
		File vpn = new File("/usr/share/oss/tools/vpn");
		if( vpn == null || !vpn.exists() ) {
			return false;
		}
		for( Group g : session.getUser().getGroups() ) {
			if( g.getName().equals("VPNUSERS")) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Response getConfig(Session session, String OS) {
		if( ! haveVpn(session)) {
			throw new WebApplicationException(401);
		}
		Config config   = new Config("/etc/sysconfig/oss-vpn","");
		String vpnId    = config.getConfigValue("VPND_ID");
		File configFile = null;
		switch(OS) {
		case "Win7":
		case "Win10":
			configFile = new File("/var/adm/oss/vpn/" + vpnId + "-" + session.getUser().getUid() + ".exe");
			break;
		case "Mac":
			configFile = new File("/var/adm/oss/vpn/" + vpnId + "-" + session.getUser().getUid() + ".tar.gz");
			break;
		case "Linux":
			configFile = new File("/var/adm/oss/vpn/" + vpnId + "-" + session.getUser().getUid() + ".tgz");
			break;
		}
		if( ! configFile.exists() ) {
			StringBuffer reply = new StringBuffer();
			StringBuffer error = new StringBuffer();
			String[]   program = new String[2];
			program[0] = "/usr/share/oss/tools/vnp/creat-config.sh";
			program[1] = session.getUser().getUid();
			OSSShellTools.exec(program, reply, error, null);
		}
		ResponseBuilder response = Response.ok((Object) configFile);
		response.header("Content-Disposition","attachment; filename=\""+ configFile.getName() + "\"");
		return response.build();
	}

	@Override
	public Response getInstaller(Session session, String OS) {
		if( ! haveVpn(session)) {
			throw new WebApplicationException(401);
		}
		File configFile = null;
		switch(OS) {
		case "Win7":
			configFile = new File("/srv/www/admin/vpn-clients/openvpn-install-Win7.exe");
			break;
		case "Win10":
			configFile = new File("/srv/www/admin/vpn-clients/openvpn-install-Win10.exe");
			break;
		case "Mac":
			configFile = new File("/srv/www/admin/vpn-clients/Tunnelblick.dmg");
			break;
		}
		ResponseBuilder response = Response.ok((Object) configFile);
		response.header("Content-Disposition","attachment; filename=\""+ configFile.getName() + "\"");
		return response.build();
	}

}
