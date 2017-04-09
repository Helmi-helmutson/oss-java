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

@Path("system")
@Api(value = "system")
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
    @ApiOperation(value = "Gets the whole system configuration.")
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Server broken, please contact administrator")
    })
    @RolesAllowed("sysadmins")
    List<Map<String, String>>  getConfig(
    		@ApiParam(hidden = true) @Auth Session session
    		);

    @GET
    @Path("configuration/{key}")
    @Produces("text/plain")
    @ApiOperation(value = "Gets a system configuration value.")
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
    @ApiOperation(value = "Sets a system configuration.")
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Server broken, please contact administrator")
    })
    @RolesAllowed("sysadmins")
    Response setConfig(
    		@ApiParam(hidden = true) @Auth Session session,
            @PathParam("key") String key,
            @PathParam("value") String value
    );
    
    // Firewall configuration
    @GET
    @Path("firewall/incommingRules")
    @Produces(JSON_UTF8)
    @ApiOperation(value = "Gets the incomming firewall rules.")
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Server broken, please contact administrator")
    })
    @RolesAllowed("sysadmins")
    Map<String, String>  getFirewallIncommingRules(
    		@ApiParam(hidden = true) @Auth Session session
    		);

    @POST
    @Path("firewall/incommingRules")
    @Produces(JSON_UTF8)
    @ApiOperation(value = "Gets the incomming firewall rules.")
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Server broken, please contact administrator")
    })
    @RolesAllowed("sysadmins")
    Response  setFirewallIncommingRules(
    		@ApiParam(hidden = true) @Auth Session session,
    		Map<String, String> incommingRules
    		);

    @GET
    @Path("firewall/outgoingRules")
    @Produces(JSON_UTF8)
    @ApiOperation(value = "Gets the incomming firewall rules.")
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Server broken, please contact administrator")
    })
    @RolesAllowed("sysadmins")
    List<Map<String, String>>  getFirewallOutgoingRules(
    		@ApiParam(hidden = true) @Auth Session session
    		);

    @POST
    @Path("firewall/outgoingRules")
    @Produces(JSON_UTF8)
    @ApiOperation(value = "Gets the incomming firewall rules.")
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Server broken, please contact administrator")
    })
    @RolesAllowed("sysadmins")
    Response  setFirewallOutgoingRules(
    		@ApiParam(hidden = true) @Auth Session session,
    		List<Map<String, String>> incommingRules
    		);

    @GET
    @Path("firewall/remoteAccessRules")
    @Produces(JSON_UTF8)
    @ApiOperation(value = "Gets the incomming firewall rules.")
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Server broken, please contact administrator")
    })
    @RolesAllowed("sysadmins")
    List<Map<String, String>>  getFirewallRemoteAccessRules(
    		@ApiParam(hidden = true) @Auth Session session
    		);

    @POST
    @Path("firewall/remoteAccessRules")
    @Produces(JSON_UTF8)
    @ApiOperation(value = "Gets the incomming firewall rules.")
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Server broken, please contact administrator")
    })
    @RolesAllowed("sysadmins")
    Response  setFirewallRemoteAccessRules(
    		@ApiParam(hidden = true) @Auth Session session,
    		List<Map<String, String>> incommingRules
    		);

}
