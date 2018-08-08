package de.openschoolserver.api.resourceimpl;

import java.util.Locale;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.client.ClientProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.openschoolserver.api.resources.SupportResource;
import de.openschoolserver.dao.Session;
import de.openschoolserver.dao.SupportRequest;
import de.openschoolserver.dao.controller.SystemController;
import de.openschoolserver.dao.tools.OSSShellTools;

public class SupportResourceImpl implements SupportResource {
	Logger logger = LoggerFactory.getLogger(SupportResourceImpl.class);

	private Client jerseyClient;
	private String supportUrl;
	private String supportEmail;
	private String supportEmailFrom;
	private boolean isLinux = true;

	public SupportResourceImpl(final Client jerseyClient) {
		this.jerseyClient = jerseyClient;
		String os = System.getProperty("os.name", "generic").toLowerCase(Locale.ENGLISH);
		isLinux = !((os.indexOf("mac") >= 0) || (os.indexOf("darwin") >= 0));

	}

	private void loadConf(Session session) {
		// Session session = new SessionController().getLocalhostSession();
		SystemController sc = new SystemController(session);
		supportUrl = sc.getConfigValue("SUPPORT_URL");
		if (supportUrl != null) {
			supportUrl = supportUrl.trim();
		}
		if (supportUrl != null && supportUrl.equalsIgnoreCase("MAIL")) {
			supportEmail = sc.getConfigValue("SUPPORT_MAIL_ADDRESS");
			supportEmailFrom = sc.getConfigValue("SUPPORT_MAIL_FROM");
			if (supportEmailFrom!=null) {
				supportEmailFrom = supportEmailFrom.trim();
				if (supportEmailFrom.length()==0) {
					supportEmailFrom = null;
				}
			}
			supportUrl = null;
		} else if (supportUrl != null && supportUrl.length() > 0) {
			supportEmail = null;
			supportEmailFrom = null;
		} else {
			// not configured therefore use default
			supportUrl = "https://support.extis.de/support";
			supportEmail = null;
			supportEmailFrom = null;
		}
	}

	@Override
	public SupportRequest create(Session session, SupportRequest supportRequest) {
		loadConf(session);
		logger.debug("URL: " + supportUrl);
		if (supportUrl != null && supportUrl.length() > 0) {
			// use oss support rest services
			final WebTarget webTarget = jerseyClient.target(supportUrl);

			Response response = webTarget.request().property(ClientProperties.READ_TIMEOUT, 10000)
					.post(Entity.json(supportRequest));
			if (response.getStatus() != 200 && response.getStatus() != 204) {
				logger.error("error from support system at " + supportUrl + ": " + response.getStatus());
				throw new WebApplicationException(response.getStatus());
			}
			return response.readEntity(SupportRequest.class);
		} else {
			// use classic email
			StringBuilder request = new StringBuilder();
			request.append(supportRequest.getDescription()).append("\n\nRegcode: ").append(supportRequest.getRegcode());
			request.append("\nProduct: ").append(supportRequest.getProduct());

			request.append("\nVon: ").append(supportRequest.getFirstname()).append(" ")
					.append(supportRequest.getLastname());
			if (supportRequest.getCompany() != null && supportRequest.getCompany().trim().length() > 0) {
				request.append(" ").append(supportRequest.getCompany());
			}
			request.append(" ").append(supportEmail);
			request.append("\nArt: ").append(supportRequest.getSupporttype());
			request.append("\n."); // Important: end of message

			int args = 4 + (isLinux?1:0) + (supportEmailFrom!=null?(isLinux?1:0):0);
			String[] program = new String[args];
			StringBuffer reply = new StringBuffer();
			StringBuffer error = new StringBuffer();
			int i = 0;
			program[i++] = "/usr/bin/mail";
			if (isLinux) {
				program[i++] = "-R " + supportRequest.getEmail();
			}
			program[i++] = "-s " + supportRequest.getSubject();
			if (isLinux && supportEmailFrom != null) {
				program[i++] = "-r " + supportEmailFrom;
			}
			program[i++] = "-c " + supportRequest.getEmail();// cc address
			
			program[i++] = supportEmail;
			
			int result = OSSShellTools.exec(program, reply, error, request.toString());
			if (result == 0) {
				supportRequest.setTicketno("EMAIL");
			} else {
				logger.error("Error sending support mail: " + error.toString());
				supportRequest.setTicketno("ERROR: " + error.toString());
			}
			return supportRequest;
		}
	}

}
