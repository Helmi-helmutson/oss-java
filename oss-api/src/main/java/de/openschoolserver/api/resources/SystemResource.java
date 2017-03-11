/* (c) 2017 P��ter Varkoly <peter@varkoly.de> - all rights reserved */
/* (c) 2016 EXTIS GmbH - all rights reserved */
package de.openschoolserver.api.resources;

import io.dropwizard.auth.Auth;

import io.swagger.annotations.*;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;

import java.util.List;
import java.util.Map;

import static de.openschoolserver.api.resources.Resource.JSON_UTF8;
import de.openschoolserver.dao.Response;
import de.openschoolserver.dao.Session;

@Path("sessions")
@Api(value = "sessions")
public interface SystemResource {

    @GET
    @Path("status")
    @Produces(JSON_UTF8)
    @ApiOperation(value = "Gets the system status.")
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Server broken, please contact administrator")
    })
    @RolesAllowed("sysadmins")
    List<Map<String, String>> getStatus(
    		@ApiParam(hidden = true) @Auth Session session
    		);
    
    //Handling of enumerates

    @GET
    @Path("enumerates/{type}")
    @Produces(JSON_UTF8)
    @ApiOperation(value = "get session status")
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Server broken, please contact administrator")
    })
    @PermitAll
    List<String> getEnumerates(
    		@ApiParam(hidden = true) @Auth Session session,
            @PathParam("type") String type
    );

    @PUT
    @Path("enumerates/{type}/{value}")
    @Produces(JSON_UTF8)
    @ApiOperation(value = "Creates a new enumerate")
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Server broken, please contact administrator")
    })
    @RolesAllowed("sysadmins")
    Response addEnumerate(
    		@ApiParam(hidden = true) @Auth Session session,
            @PathParam("type") String type,
            @PathParam("value") String value
    );

    @DELETE
    @Path("enumerates/{type}/{value}")
    @Produces(JSON_UTF8)
    @ApiOperation(value = "Deletes an enumerate")
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Server broken, please contact administrator")
    })
    @RolesAllowed("sysadmins")
    Response removeEnumerate(
    		@ApiParam(hidden = true) @Auth Session session,
            @PathParam("type") String type,
            @PathParam("value") String value
    );
    
    // Global Configuration
    
    @GET
    @Path("configuration")
    @Produces(JSON_UTF8)
    @ApiOperation(value = "get session status")
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Server broken, please contact administrator")
    })
    @RolesAllowed("sysadmins")
    List<Map<String, String>>  getConfig(
    		@ApiParam(hidden = true) @Auth Session session
    		);

    @GET
    @Path("configuration/{key}")
    @Produces(JSON_UTF8)
    @ApiOperation(value = "get session status")
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Server broken, please contact administrator")
    })
    @PermitAll
    String getConfig(
    		@ApiParam(hidden = true) @Auth Session session,
    		@PathParam("key") String key
    		);

    @PUT
    @Path("configuration/{key}/{value}")
    @Produces(JSON_UTF8)
    @ApiOperation(value = "Creates a new enumerate")
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Server broken, please contact administrator")
    })
    @RolesAllowed("sysadmins")
    Response setConfig(
    		@ApiParam(hidden = true) @Auth Session session,
            @PathParam("key") String key,
            @PathParam("value") String value
    );

}
