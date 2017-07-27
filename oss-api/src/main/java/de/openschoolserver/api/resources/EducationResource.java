/* (c) 2017 Péter Varkoly <peter@varkoly.de> - all rights reserved  */
package de.openschoolserver.api.resources;

import io.dropwizard.auth.Auth;


import io.swagger.annotations.*;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import static de.openschoolserver.api.resources.Resource.JSON_UTF8;
import de.openschoolserver.dao.Response;
import de.openschoolserver.dao.Session;
import de.openschoolserver.dao.Group;
import de.openschoolserver.dao.Category;

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
    @ApiOperation(value = "Create a new smart room. A smart Room is a category with CategoryType smart room. " +
    					  "The map can contains a description attribute. " +
    					  "The map must contains either users or groups or devices. ")
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Server broken, please contact administrator")
    })
    @RolesAllowed({"sysadmins","teachers"})
    Response  createSmartRoom(
    		@ApiParam(hidden = true) @Auth Session session,
    		Category smartRoom
    		);

    /*
	 * POST education/rooms/{roomId}
	 */
    @POST
    @Path("rooms/{roomId}")
    @Produces(JSON_UTF8)
    @ApiOperation(value = "Modfy a smart room. Only name and description can be modified here. To modify the member there are some PUT and DELETE calls.")
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Server broken, please contact administrator")
    })
    @RolesAllowed({"sysadmins","teachers"})
    Response  modifySmartRoom(
    		@ApiParam(hidden = true) @Auth Session session,
    		@PathParam("roomId") long roomId,
    		Category smartRoom
    		);
    
    /*
     *  PUT education/rooms/{roomId}/users/{userId}
     */
    @PUT
    @Path("rooms/{roomId}/users/{userId}")
    @Produces(JSON_UTF8)
    @ApiOperation(value = "Add a user to a smart room." )
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Server broken, please contact administrator")
    })
    @RolesAllowed({"sysadmins","teachers"})
    Response addUser(
    		@ApiParam(hidden = true) @Auth Session session,
    		@PathParam("roomId") long roomId,
    		@PathParam("userId") long userId
    		);

    /*
     *  PUT education/rooms/{roomId}/devices/{deviceId}
     */
    @PUT
    @Path("rooms/{roomId}/devices/{deviceId}")
    @Produces(JSON_UTF8)
    @ApiOperation(value = "Add a device to a smart room." )
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Server broken, please contact administrator")
    })
    @RolesAllowed({"sysadmins","teachers"})
    Response addDevice(
    		@ApiParam(hidden = true) @Auth Session session,
    		@PathParam("roomId")   long roomId,
    		@PathParam("deviceId") long deviceId
    		);

    /*
     *  PUT education/rooms/{roomId}/groups/{groupId}
     */
    @PUT
    @Path("rooms/{roomId}/groups/{groupId}")
    @Produces(JSON_UTF8)
    @ApiOperation(value = "Add a group to a smart room." )
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Server broken, please contact administrator")
    })
    @RolesAllowed({"sysadmins","teachers"})
    Response addGroup(
    		@ApiParam(hidden = true) @Auth Session session,
    		@PathParam("roomId") long roomId,
    		@PathParam("groupId") long roupId
    		);

   
    /*
     *  DELETE education/rooms/{roomId}/users/{userId}
     */
    @DELETE
    @Path("rooms/{roomId}/users/{userId}")
    @Produces(JSON_UTF8)
    @ApiOperation(value = "Delete a user from a smart room." )
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Server broken, please contact administrator")
    })
    @RolesAllowed({"sysadmins","teachers"})
    Response deleteUser(
    		@ApiParam(hidden = true) @Auth Session session,
    		@PathParam("roomId") long roomId,
    		@PathParam("userId") long userId
    		);

    /*
     *  DELETE education/rooms/{roomId}/    devices/{deviceId}
     */
    @DELETE
    @Path("rooms/{roomId}/devices/{deviceId}")
    @Produces(JSON_UTF8)
    @ApiOperation(value = "Delete a device from a smart room." )
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Server broken, please contact administrator")
    })
    @RolesAllowed({"sysadmins","teachers"})
    Response deleteDevice(
    		@ApiParam(hidden = true) @Auth Session session,
    		@PathParam("roomId")   long roomId,
    		@PathParam("deviceId") long deviceId
    		);

    /*
     *  DELETE education/rooms/{roomId}/groups/{groupId}
     */
    @DELETE
    @Path("rooms/{roomId}/groups/{groupId}")
    @Produces(JSON_UTF8)
    @ApiOperation(value = "Delete a device from a smart room." )
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Server broken, please contact administrator")
    })
    @RolesAllowed({"sysadmins","teachers"})
    Response deleteGroup(
    		@ApiParam(hidden = true) @Auth Session session,
    		@PathParam("roomId")  long roomId,
    		@PathParam("groupId") long groupId
    		);

    /*
	 * DELETE education/rooms/{roomId}
	 */
    @DELETE
    @Path("rooms/{roomId}")
    @Produces(JSON_UTF8)
    @ApiOperation(value = "Deletes a new smart room.")
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Server broken, please contact administrator")
    })
    @RolesAllowed({"sysadmins","teachers"})
    Response  deleteSmartRoom(
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
    @ApiOperation(value = "Gets the state of a smart room. This call delivers a list of map with the logged in users. " +
    					  "A logged in user map has the format: { deviceId => <deviceId> , userId => <userId> } " +
    					  "The response contains a list of maps with userId and deviceId: " +
    					  "[ { userId => UID1, deviceId => DID1 } ,  { userId => UID2, deviceId => DID2 } ]"
    			)
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Server broken, please contact administrator")
    })
    @RolesAllowed({"sysadmins","teachers"})
	List<List<Long>>  getRoom(
    		@ApiParam(hidden = true) @Auth Session session,
    		@PathParam("roomId") long roomId
    		);

	/*
	 * GET education/rooms/{roomId}/control/minutes
	 */
	@GET
	@Path("rooms/{roomId}/control/{minutes}")
    @Produces(JSON_UTF8)
    @ApiOperation(value = "Get the control for a room for an amount of time."
    			)
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Server broken, please contact administrator")
    })
    @RolesAllowed({"sysadmins","teachers"})
	Response  getRoomControl(
    		@ApiParam(hidden = true) @Auth Session session,
    		@PathParam("roomId")  long roomId,
    		@PathParam("minutes") long minutes
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
            @PathParam("roomId") long roomId
    );

    /*
     * POST education/rooms/{roomId}/{action}
     */
    @PUT
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

    @POST
    @Path("rooms/{roomId}/upload")
    @Produces(JSON_UTF8)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @ApiOperation( value = "Puts data to te member of the smart rooms" )
    @ApiResponses(value = {
	            @ApiResponse(code = 500, message = "Server broken, please contact administrator")
	})
    Response uploadFileToRoom(@ApiParam(hidden = true) @Auth Session session,
    		@PathParam("roomId") long roomId,
            @FormDataParam("file") final InputStream fileInputStream,
            @FormDataParam("file") final FormDataContentDisposition contentDispositionHeader
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
     
     @POST
     @Path("education/groups/{groupId}/upload")
     @Produces(JSON_UTF8)
     @Consumes(MediaType.MULTIPART_FORM_DATA)
     @ApiOperation( value = "Puts data to te member of the smart rooms" )
     @ApiResponses(value = {
 	            @ApiResponse(code = 500, message = "Server broken, please contact administrator")
 	})
     Response uploadFileToGroup(@ApiParam(hidden = true) @Auth Session session,
     		 @PathParam("groupId")  long  groupId,
             @FormDataParam("file") final InputStream fileInputStream,
             @FormDataParam("file") final FormDataContentDisposition contentDispositionHeader
             );
  

    /************************************************************/
    /* Actions on logged in users and smart rooms and groups. */
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
     * GET education/groups/{groupId}/actions
     */
    @GET
    @Path("groups/{groupId}/actions")
    @Produces(JSON_UTF8)
    @ApiOperation(value = "Delivers a list of available actions for a user.")
    @ApiResponses(value = {
            // TODO so oder anders? @ApiResponse(code = 404, message = "At least one room was not found"),
            @ApiResponse(code = 500, message = "Server broken, please contact administrator")
    })
    @RolesAllowed({"sysadmins","teachers"})
    List<String> getAvailableGroupActions(
            @ApiParam(hidden = true) @Auth Session session,
            @PathParam("groupId") long groupId
    );

    /*
     * POST education/users/{userId}/{deviceId}/{action}
     */
    @PUT
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

    @POST
    @Path("education/users/{userId}/upload")
    @Produces(JSON_UTF8)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @ApiOperation( value = "Puts data to te member of the smart rooms" )
    @ApiResponses(value = {
	            @ApiResponse(code = 500, message = "Server broken, please contact administrator")
	})
    Response uploadFileToUser(@ApiParam(hidden = true) @Auth Session session,
    		@PathParam("userId")   long  userId,
            @FormDataParam("file") final InputStream fileInputStream,
            @FormDataParam("file") final FormDataContentDisposition contentDispositionHeader
            );
 
    /************************************************************/
    /* Actions on logged in users and smart rooms and groups. */
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
            @PathParam("deviceId") long deviceId
    );

    /*
     * PUT education/devices/{deviceId}/{action}
     */
    @PUT
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
    
    @POST
    @Path("education/devices/{deviceId}/upload")
    @Produces(JSON_UTF8)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @ApiOperation( value = "Puts data to te member of the smart rooms" )
    @ApiResponses(value = {
	            @ApiResponse(code = 500, message = "Server broken, please contact administrator")
	})
    Response uploadFileToDevice(@ApiParam(hidden = true) @Auth Session session,
    		@PathParam("deviceId") long deviceId,
            @FormDataParam("file") final InputStream fileInputStream,
            @FormDataParam("file") final FormDataContentDisposition contentDispositionHeader
            );
 
}
