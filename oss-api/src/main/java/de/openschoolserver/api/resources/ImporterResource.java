/* (c) 2017 EXTIS GmbH (www.extis.de) - all rights reserved */
package de.openschoolserver.api.resources;

import static de.openschoolserver.api.resources.Resource.JSON_UTF8;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import javax.ws.rs.core.MediaType;

import de.claxss.importlib.ImporterDescription;
import de.claxss.importlib.ImportOrder;
import de.openschoolserver.dao.Session;
import io.dropwizard.auth.Auth;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import java.io.InputStream;

@Path("importer")
@Api(value = "importer")
public interface ImporterResource {

	/*
	 * GET groups/<groupId>/members
	 */
	@GET
	@Path("{objecttype}/availableimporters")
	@Produces(JSON_UTF8)
	@ApiOperation(value = "Get the list of available import descriptions for an object type like PERSON, SCHOOLCLASS, GROUP, SUBJECT, ROOM, PLAN, SCHOOL")
	@ApiResponses(value = { @ApiResponse(code = 404, message = "Group not found"),
			@ApiResponse(code = 500, message = "Server broken, please contact adminstrator") })
	@RolesAllowed("device.manage")
	List<ImporterDescription> getAvailableImporters(@ApiParam(hidden = true) @Auth Session session,
			@PathParam("objecttype") String objecttype);

	@POST
	@Path("prepareImport")
	@Produces(JSON_UTF8)
	@ApiOperation(value = "prepare the import ")
	@ApiResponses(value = { @ApiResponse(code = 409, message = "An import is actually in progress"),
			@ApiResponse(code = 500, message = "Server broken, please contact administrator") })
	@RolesAllowed("device.manage")
	ImportOrder prepareImport(@ApiParam(hidden = true) @Auth Session session, ImportOrder importOrder);

	@POST
	@Path("uploadImport")
	@Produces(JSON_UTF8)
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@ApiOperation(value = "imports data upload")
	@ApiResponses(value = {

			@ApiResponse(code = 500, message = "Server broken, please contact administrator") })
	String uploadImport(@ApiParam(hidden = true) @Auth Session session,

			@FormDataParam("file") final InputStream fileInputStream,
			@FormDataParam("file") final FormDataContentDisposition contentDispositionHeader);

	@POST
	@Path("processImport")
	@Produces(JSON_UTF8)
	@ApiOperation(value = "process the import user data, can be called multiple times with changed ImportOrder parameters, to optimize processing")
	@ApiResponses(value = { @ApiResponse(code = 500, message = "Server broken, please contact administrator") })
	@RolesAllowed("device.manage")
	ImportOrder processImport(@ApiParam(hidden = true) @Auth Session session, ImportOrder importOrder);

	@POST
	@Path("cancelImport")
	@Produces(JSON_UTF8)
	@ApiOperation(value = "cancel the import")
	@ApiResponses(value = { @ApiResponse(code = 500, message = "Server broken, please contact administrator") })
	@RolesAllowed("device.manage")
	ImportOrder cancelImport(@ApiParam(hidden = true) @Auth Session session, ImportOrder importOrder);

	@POST
	@Path("getImportStatus")
	@Produces(JSON_UTF8)
	@ApiOperation(value = "get the status of the last import")
	@ApiResponses(value = { @ApiResponse(code = 500, message = "Server broken, please contact administrator") })
	@RolesAllowed("device.manage")
	ImportOrder getImportStatus(@ApiParam(hidden = true) @Auth Session session, ImportOrder importOrder);
}
