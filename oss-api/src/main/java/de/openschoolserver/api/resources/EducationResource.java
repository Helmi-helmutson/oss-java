package de.openschoolserver.api.resources;

import io.dropwizard.auth.Auth;

import io.swagger.annotations.*;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;

import java.util.List;
import java.util.Map;

import static de.openschoolserver.api.resources.Resource.JSON_UTF8;
import de.openschoolserver.dao.Response;
import de.openschoolserver.dao.Session;
import de.openschoolserver.dao.Group;

@Path("education")
@Api(value = "education")
public interface EducationResource {

	/******************************/
	/* Functions to handle rooms  */
	/******************************/
	
    /*
	 * POST education/rooms
	 */
    @POST
    @Path("rooms")
    @Produces(JSON_UTF8)
    @ApiOperation(value = "Create a new virtual room. The map must contains the name attribute. " +
    					  "The map can contains a description attribute. " +
    					  "The map must contains either a users or a groups or a devices attribute. " +
    					  "This provides a comma separated list of object ids. " +
    					  "{ name => 'MyVirtualRoom' , description => 'My virtual room', users => '12,45,22,34,23'")
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Server broken, please contact administrator")
    })
    @RolesAllowed({"sysadmins","teachers"})
    Response  createVirtaulRoom(
    		@ApiParam(hidden = true) @Auth Session session,
    		Map<String, String> virtualRoom
    		);

    /*
	 * POST education/rooms/{roomId}
	 */
    @POST
    @Path("rooms/{roomId}")
    @Produces(JSON_UTF8)
    @ApiOperation(value = "Midfy a virtual room. The map must contains the name attribute. " +
    					  "The map can contains a description attribute. " +
    					  "The map must contains either a users or a groups or a devices attribute. " +
    					  "This provides a comma separated list of object ids. " +
    					  "{ name => 'MyVirtualRoom' , description => 'My virtual room', users => '12,45,22,34,23'")
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Server broken, please contact administrator")
    })
    @RolesAllowed({"sysadmins","teachers"})
    Response  modifyVirtaulRoom(
    		@ApiParam(hidden = true) @Auth Session session,
    		Map<String, String> virtualRoom
    		);

    /*
	 * DELETE education/rooms/{roomId}
	 */
    @DELETE
    @Path("rooms/{roomId}")
    @Produces(JSON_UTF8)
    @ApiOperation(value = "Deletes a new virtual room.")
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Server broken, please contact administrator")
    })
    @RolesAllowed({"sysadmins","teachers"})
    Response  deleteVirtaulRoom(
    		@ApiParam(hidden = true) @Auth Session session,
    		@PathParam("roomId") long roomId
    		);

    
    /*
	 *  GET education/rooms
	 */
	@GET
	@Path("rooms")
	@Produces(JSON_UTF8)
	@ApiOperation(value = "Gets the list of ids of the rooms the session user may control.")
	@ApiResponses(value = {
	            @ApiResponse(code = 500, message = "Server broken, please contact administrator")
	})
	@RolesAllowed({"sysadmins","teachers"})
	List<Long> getMyRooms(
			@ApiParam(hidden = true) @Auth Session session
	);
	
	/*
	 * GET education/rooms/{roomId}
	 */
	@GET
	@Path("rooms/{roomId}")
    @Produces(JSON_UTF8)
    @ApiOperation(value = "Gets the state of a virtual room. This call delivers a list of map with the logged in users. " +
    					  "A logged in user map has the format: { deviceId => <deviceId> , userId => <userId> } " +
    					  "The response contains a list of maps with userId and deviceId: " +
    					  "[ { userId => UID1, deviceId => DID1 } ,  { userId => UID2, deviceId => DID2 } ]"
    			)
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Server broken, please contact administrator")
    })
    @RolesAllowed({"sysadmins","teachers"})
	List<Map<String, String>>  getRoom(
    		@ApiParam(hidden = true) @Auth Session session,
    		@PathParam("roomId") long roomId
    		);

	/*
     * GET education/rooms/{roomId}/actions
     */
    @GET
    @Path("rooms/{roomId}/actions")
    @Produces(JSON_UTF8)
    @ApiOperation(value = "Delivers a list of available actions for a room.")
    @ApiResponses(value = {
            // TODO so oder anders? @ApiResponse(code = 404, message = "At least one room was not found"),
            @ApiResponse(code = 500, message = "Server broken, please contact administrator")
    })
    @RolesAllowed({"sysadmins","teachers"})
    List<String> getAvailableRoomActions(
            @ApiParam(hidden = true) @Auth Session session,
            @PathParam("roomId") long roomId,
            @PathParam("action") String action
    );

    /*
     * PUT education/rooms/{roomId}/{action}
     */
    @POST
    @Path("rooms/{roomId}/{action}")
    @Produces(JSON_UTF8)
    @ApiOperation(value = "Manage a room. Valid actions are open, close, reboot, shutdown, wol, logout, openProxy, closeProxy, .")
    @ApiResponses(value = {
            // TODO so oder anders? @ApiResponse(code = 404, message = "At least one room was not found"),
            @ApiResponse(code = 500, message = "Server broken, please contact administrator")
    })
    @RolesAllowed({"sysadmins","teachers"})
    Response manageRoom(
            @ApiParam(hidden = true) @Auth Session session,
            @PathParam("roomId") long roomId,
            @PathParam("action") String action,
        	Map<String, String> actionContent
    );

	
	/******************************/
	/* Functions to handle groups */
	/******************************/
	
    /*
	 * POST education/groups
	 */
    @POST
    @Path("groups")
    @Produces(JSON_UTF8)
    @ApiOperation(value = "Create a new workgroup.")
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Server broken, please contact administrator")
    })
    @RolesAllowed({"sysadmins","teachers"})
    Response  createGroup(
    		@ApiParam(hidden = true) @Auth Session session,
       		Group group
    		);

    /*
	 * POST education/groups/{groupId}
	 */
    @POST
    @Path("groups/{groupId}")
    @Produces(JSON_UTF8)
    @ApiOperation(value = "Modify a workgroup.")
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Server broken, please contact administrator")
    })
    @RolesAllowed({"sysadmins","teachers"})
    Response  modifyGroup(
    		@ApiParam(hidden = true) @Auth Session session,
    		@PathParam("groupId") long groupId,
    		Group group
    		);
    
    /*
 	 * DELETE education/groups/{groupId}
 	 */
     @DELETE
     @Path("groups/{groupId}")
     @Produces(JSON_UTF8)
     @ApiOperation(value = "Modify a workgroup.")
     @ApiResponses(value = {
             @ApiResponse(code = 500, message = "Server broken, please contact administrator")
     })
     @RolesAllowed({"sysadmins","teachers"})
     Response  removeGroup(
     		@ApiParam(hidden = true) @Auth Session session,
     		@PathParam("groupId") long groupId
     	   );

    /************************************************************/
    /* Actions on logged in users and virtual rooms and groups. */
    /************************************************************/
    /*
     * DELETE education/users/{userId}/{deviceId}
     */
    @DELETE
    @Path("users/{userId}/{deviceId}")
    @Produces(JSON_UTF8)
    @ApiOperation(value = "Log out a user from a device. If device is -1 user will be logged out from all devices." )
    @ApiResponses(value = {
            // TODO so oder anders? @ApiResponse(code = 404, message = "At least one room was not found"),
            @ApiResponse(code = 500, message = "Server broken, please contact administrator")
    })
    @RolesAllowed({"sysadmins","teachers"})
    Response logOut(
    		@ApiParam(hidden = true) @Auth Session session,
    		@PathParam("userId") long userId,
    		@PathParam("deviceId") long deviceId
    		);
    
    /*
     * PUT education/users/{userId}/{deviceId}
     */
    @PUT
    @Path("users/{userId}/{deviceId}")
    @Produces(JSON_UTF8)
    @ApiOperation(value = "Log in a user to a device." )
    @ApiResponses(value = {
            // TODO so oder anders? @ApiResponse(code = 404, message = "At least one room was not found"),
            @ApiResponse(code = 500, message = "Server broken, please contact administrator")
    })
    @RolesAllowed({"sysadmins","teachers"})
    Response logIn(
    		@ApiParam(hidden = true) @Auth Session session,
    		@PathParam("userId") long userId,
    		@PathParam("deviceId") long roomId
    		);

    /*
     * GET education/users/{userId}/actions
     */
    @GET
    @Path("users/{userId}/actions")
    @Produces(JSON_UTF8)
    @ApiOperation(value = "Delivers a list of available actions for a user.")
    @ApiResponses(value = {
            // TODO so oder anders? @ApiResponse(code = 404, message = "At least one room was not found"),
            @ApiResponse(code = 500, message = "Server broken, please contact administrator")
    })
    @RolesAllowed({"sysadmins","teachers"})
    List<String> getAvailableUserActions(
            @ApiParam(hidden = true) @Auth Session session,
            @PathParam("userId") long userId
    );

    /*
     * POST education/users/{userId}/{deviceId}/{action}
     */
    @POST
    @Path("users/{userId}/{deviceId}/{action}")
    @Produces(JSON_UTF8)
    @ApiOperation(value = "Send a action to a user to a device. If the device is -1 the user gets this action on all devices. " +
    					  "Depending on the action an arbitary map can be sent in the body.")
    @ApiResponses(value = {
            // TODO so oder anders? @ApiResponse(code = 404, message = "At least one room was not found"),
            @ApiResponse(code = 500, message = "Server broken, please contact administrator")
    })
    @RolesAllowed({"sysadmins","teachers"})
    Response manageUSer(
    		@ApiParam(hidden = true) @Auth Session session,
    		@PathParam("userId") long userId,
    		@PathParam("deviceId") long deviceId,
    		@PathParam("action") String action,
    		Map<String, String> actionContent
    		);

    /************************************************************/
    /* Actions on logged in users and virtual rooms and groups. */
    /************************************************************/
     /*
     * GET education/devices/{deviceId}/actions
     */
    @GET
    @Path("devices/{deviceId}/actions")
    @Produces(JSON_UTF8)
    @ApiOperation(value = "Delivers a list of available actions for a device.")
    @ApiResponses(value = {
            // TODO so oder anders? @ApiResponse(code = 404, message = "At least one room was not found"),
            @ApiResponse(code = 500, message = "Server broken, please contact administrator")
    })
    @RolesAllowed({"sysadmins","teachers"})
    List<String> getAvailableDeviceActions(
            @ApiParam(hidden = true) @Auth Session session,
            @PathParam("deviceId") long deviceId,
            @PathParam("action") String action
    );

    /*
     * PUT education/devices/{deviceId}/{action}
     */
    @POST
    @Path("devices/{deviceId}/{action}")
    @Produces(JSON_UTF8)
    @ApiOperation(value = "Manage a room. Valid actions are open, close, reboot, shutdown, wol, logout, openProxy, closeProxy, .")
    @ApiResponses(value = {
            // TODO so oder anders? @ApiResponse(code = 404, message = "At least one room was not found"),
            @ApiResponse(code = 500, message = "Server broken, please contact administrator")
    })
    @RolesAllowed({"sysadmins","teachers"})
    Response manageDevice(
            @ApiParam(hidden = true) @Auth Session session,
            @PathParam("deviceId") long deviceId,
            @PathParam("action") String action,
            Map<String, String> actionContent
    );
 
}
