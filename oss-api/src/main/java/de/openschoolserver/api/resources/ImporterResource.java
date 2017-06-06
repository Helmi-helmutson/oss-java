package de.openschoolserver.api.resources;

import static de.openschoolserver.api.resources.Resource.JSON_UTF8;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import de.claxss.importlib.ImporterDescription;
import de.openschoolserver.dao.Session;
import io.dropwizard.auth.Auth;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

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
       @ApiResponses(value = {
               @ApiResponse(code = 404, message = "Group not found"),
               @ApiResponse(code = 500, message = "Server broken, please contact adminstrator")})
       @RolesAllowed("device.manage")
       List<ImporterDescription> getAvailableImporters(
               @ApiParam(hidden = true) @Auth Session session,
               @PathParam("objecttype") String objecttype
       );
}
