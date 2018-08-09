/* (c) 2018 EXTIS GmbH - all rights reserved */
package de.openschoolserver.api.resources;

import static de.openschoolserver.api.resources.Resource.*;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import de.openschoolserver.dao.OssResponse;
import de.openschoolserver.dao.Session;
import de.openschoolserver.dao.SupportRequest;
import io.dropwizard.auth.Auth;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Path("support")
@Api(value = "support")
public interface SupportResource {
	@POST
	@Path("create")
	@Produces(JSON_UTF8)
	@ApiOperation(value = "create a support request ")
	@ApiResponses(value = { @ApiResponse(code = 400, message = "Missing data for request"),
			@ApiResponse(code = 500, message = "Server broken, please contact administrator") })
	@RolesAllowed("device.manage")
	OssResponse create(@ApiParam(hidden = true) @Auth Session session, SupportRequest supportRequest);

}
