/* (c) 2017 Peter Varkoly <peter@varkoly.de> - all rights reserved */
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
import de.openschoolserver.dao.MissedTranslation;
import de.openschoolserver.dao.Translation;


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
    Response deleteEnumerate(
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
    @Path("firewall/incomingRules")
    @Produces(JSON_UTF8)
    @ApiOperation(value = "Gets the incoming firewall rules.")
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Server broken, please contact administrator")
    })
    @RolesAllowed("sysadmins")
    Map<String, String>  getFirewallIncomingRules(
    		@ApiParam(hidden = true) @Auth Session session
    		);

    @POST
    @Path("firewall/incomingRules")
    @Produces(JSON_UTF8)
    @ApiOperation(value = "Sets the incoming firewall rules.")
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Server broken, please contact administrator")
    })
    @RolesAllowed("sysadmins")
    Response  setFirewallIncomingRules(
    		@ApiParam(hidden = true) @Auth Session session,
    		Map<String, String> incomingRules
    		);

    @GET
    @Path("firewall/outgoingRules")
    @Produces(JSON_UTF8)
    @ApiOperation(value = "Gets the incoming firewall rules.")
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
    @ApiOperation(value = "Sets the incoming firewall rules.")
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Server broken, please contact administrator")
    })
    @RolesAllowed("sysadmins")
    Response  setFirewallOutgoingRules(
    		@ApiParam(hidden = true) @Auth Session session,
    		List<Map<String, String>> incomingRules
    		);

    @GET
    @Path("firewall/remoteAccessRules")
    @Produces(JSON_UTF8)
    @ApiOperation(value = "Gets the incoming firewall rules.")
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
    @ApiOperation(value = "Sets the incoming firewall rules.")
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Server broken, please contact administrator")
    })
    @RolesAllowed("sysadmins")
    Response  setFirewallRemoteAccessRules(
    		@ApiParam(hidden = true) @Auth Session session,
    		List<Map<String, String>> incomingRules
    		);
    
    
    /*
     * Translations stuff
     */
    @GET
    @Path("translations")
    @Produces("text/plain")
    @ApiOperation(value = "Translate a text into a given language")
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Server broken, please contact administrator")
    })
    @PermitAll
    String translate(
    		@ApiParam(hidden = true) @Auth Session session,
    		MissedTranslation missedTranslataion
    );
    
    @POST
    @Path("translations")
    @Produces(JSON_UTF8)
    @ApiOperation(value = "Add or updates a translation.")
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Server broken, please contact administrator")
    })
    @RolesAllowed("sysadmins")
    Response addTranslation(
    		@ApiParam(hidden = true) @Auth Session session,
    		Translation	translation
    );
    
    @GET
    @Path("missedTranslations/{lang}")
    @Produces(JSON_UTF8)
    @ApiOperation(value = "Get the list of the missed translations to a language")
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Server broken, please contact administrator")
    })
    @RolesAllowed("sysadmins")
    List<String> getMissedTranslations(
    		@ApiParam(hidden = true) @Auth Session session,
    		String lang
    );
    
    
    
    
}
