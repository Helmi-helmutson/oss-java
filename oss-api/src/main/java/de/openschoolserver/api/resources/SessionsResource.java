/* (c) 2017 Peter Varkoly <peter@varkoly.de> - all rights reserved */
/* (c) 2016 EXTIS GmbH - all rights reserved */
package de.openschoolserver.api.resources;


import static de.openschoolserver.api.resources.Resource.*;

import io.dropwizard.auth.Auth;
import io.swagger.annotations.*;
import javax.annotation.security.PermitAll;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;
import de.openschoolserver.dao.Session;
import java.util.List;

@Path("sessions")
@Api(value = "sessions")
@SwaggerDefinition(securityDefinition = @SecurityDefinition(apiKeyAuthDefinitions = {@ApiKeyAuthDefinition(
	       key = "apiKeyAuth", name = "Authorization", in = ApiKeyAuthDefinition.ApiKeyLocation.HEADER)
		}))
public interface SessionsResource {

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(JSON_UTF8)
    @ApiOperation(value = "create a new session")
    @ApiResponses(value = {
            @ApiResponse(code = 401, message = "Login is incorrect"),
            @ApiResponse(code = 500, message = "Server broken, please contact administrator")
    })
    Session createSession(
            @Context UriInfo ui,
            @FormParam("username") String username,
            @FormParam("password") String password,
            @FormParam("device") String device,
            @Context HttpServletRequest req
    );

    @POST
    @Path("login")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(TEXT)
    @ApiOperation(value = "create a new session")
    @ApiResponses(value = {
            @ApiResponse(code = 401, message = "Login is incorrect"),
            @ApiResponse(code = 500, message = "Server broken, please contact administrator")
    })
    String createToken(
            @Context UriInfo ui,
            @FormParam("username") String username,
            @FormParam("password") String password,
            @FormParam("device") String device,
            @Context HttpServletRequest req
    );

    @GET
    @Produces(JSON_UTF8)
    @ApiOperation(value = "get session status")
    @ApiResponses(value = {
            @ApiResponse(code = 401, message = "Token is not valid or no token given"),
            @ApiResponse(code = 500, message = "Server broken, please contact administrator")
    })
    @PermitAll
    Session getStatus(
            @ApiParam(hidden = true) @Auth Session session
    );

    @DELETE
    @Path("{token}")
    @Produces(JSON_UTF8)
    @ApiOperation(value = "delete session")
    @ApiResponses(value = {
            @ApiResponse(code = 401, message = "Token is not valid or no token given"),
            @ApiResponse(code = 500, message = "Server broken, please contact administrator")
    })
    @PermitAll
    void deleteSession(
            @ApiParam(hidden = true) @Auth Session session,
            @PathParam("token") String token
    );

    @GET
    @Path("{key}")
    @Produces(TEXT)
    @ApiOperation(value = "Get some session values. Available keys are: defaultPrinter, availablePrinters, dnsName, domainName.")
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Server broken, please contact administrator")
    })
    @PermitAll
    String getSessionValue(
            @ApiParam(hidden = true) @Auth Session session,
            @PathParam("key") String key
    );
    
    @GET
    @Path("allowedModules")
    @Produces(JSON_UTF8)
    @ApiOperation(value = "Get some session values. Available keys are: defaultPrinter, availablePrinters, dnsName, domainName.")
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Server broken, please contact administrator")
    })
    @PermitAll
   List<String> allowedModules(
            @ApiParam(hidden = true) @Auth Session session
    );
}
