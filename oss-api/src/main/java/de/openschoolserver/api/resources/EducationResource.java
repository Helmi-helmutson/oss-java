/* (c) 2017 Péter Varkoly <peter@varkoly.de> - all rights reserved  */
package de.openschoolserver.api.resources;


import static de.openschoolserver.api.resources.Resource.*;

import io.dropwizard.auth.Auth;
import io.swagger.annotations.*;
import javax.annotation.security.RolesAllowed;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import de.openschoolserver.dao.OssResponse;
import de.openschoolserver.dao.Group;
import de.openschoolserver.dao.Category;
import de.openschoolserver.dao.PositiveList;
import de.openschoolserver.dao.Session;
import de.openschoolserver.dao.Device;

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
    @RolesAllowed("education.rooms")
    OssResponse  createSmartRoom(
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
    @RolesAllowed("education.rooms")
    OssResponse  modifySmartRoom(
            @ApiParam(hidden = true) @Auth Session session,
            @PathParam("roomId") Long roomId,
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
    @RolesAllowed("education.rooms")
    OssResponse addUser(
            @ApiParam(hidden = true) @Auth Session session,
            @PathParam("roomId") Long roomId,
            @PathParam("userId") Long userId
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
    @RolesAllowed("education.rooms")
    OssResponse addDevice(
            @ApiParam(hidden = true) @Auth Session session,
            @PathParam("roomId")   Long roomId,
            @PathParam("deviceId") Long deviceId
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
    @RolesAllowed("education.rooms")
    OssResponse addGroup(
            @ApiParam(hidden = true) @Auth Session session,
            @PathParam("roomId") Long roomId,
            @PathParam("groupId") Long roupId
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
    @RolesAllowed("education.rooms")
    OssResponse deleteUser(
            @ApiParam(hidden = true) @Auth Session session,
            @PathParam("roomId") Long roomId,
            @PathParam("userId") Long userId
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
    @RolesAllowed("education.rooms")
    OssResponse deleteDevice(
            @ApiParam(hidden = true) @Auth Session session,
            @PathParam("roomId")   Long roomId,
            @PathParam("deviceId") Long deviceId
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
    @RolesAllowed("education.rooms")
    OssResponse deleteGroup(
            @ApiParam(hidden = true) @Auth Session session,
            @PathParam("roomId")  Long roomId,
            @PathParam("groupId") Long groupId
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
    @RolesAllowed("education.rooms")
    OssResponse  deleteSmartRoom(
            @ApiParam(hidden = true) @Auth Session session,
            @PathParam("roomId") Long roomId
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
    @RolesAllowed("education.rooms")
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
    @RolesAllowed("education.rooms")
    List<List<Long>>  getRoom(
            @ApiParam(hidden = true) @Auth Session session,
            @PathParam("roomId") Long roomId
            );

    /*
     * GET education/rooms/{roomId}/users
     * Delivers the list of the member users of a smart room
     */
    @GET
    @Path("rooms/{roomId}/users")
    @Produces(JSON_UTF8)
    @ApiOperation(value = "Delivers the ids of the user members in a smart room" )
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Server broken, please contact administrator")
    })
    @RolesAllowed("education.rooms")
    List<Long>  getUserMember(
            @ApiParam(hidden = true) @Auth Session session,
            @PathParam("roomId") Long roomId
            );
 
    /*
     * GET education/rooms/{roomId}/groups
     */
    @GET
    @Path("rooms/{roomId}/groups")
    @Produces(JSON_UTF8)
    @ApiOperation(value = "Delivers the ids of the group members in a smart room" )
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Server broken, please contact administrator")
    })
    @RolesAllowed("education.rooms")
    List<Long>  getGroupMember(
            @ApiParam(hidden = true) @Auth Session session,
            @PathParam("roomId") Long roomId
            );

    /*
     * GET education/rooms/{roomId}/devices
     */
    @GET
    @Path("rooms/{roomId}/devices")
    @Produces(JSON_UTF8)
    @ApiOperation(value = "Delivers the ids of the group members in a smart room" )
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Server broken, please contact administrator")
    })
    @RolesAllowed("education.rooms")
    List<Long>  getDeviceMember(
            @ApiParam(hidden = true) @Auth Session session,
            @PathParam("roomId") Long roomId
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
    @RolesAllowed("education.rooms")
    OssResponse  getRoomControl(
            @ApiParam(hidden = true) @Auth Session session,
            @PathParam("roomId")  Long roomId,
            @PathParam("minutes") Long minutes
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
    @RolesAllowed("education.rooms")
    List<String> getAvailableRoomActions(
            @ApiParam(hidden = true) @Auth Session session,
            @PathParam("roomId") Long roomId
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
    @RolesAllowed("education.rooms")
    OssResponse manageRoom(
            @ApiParam(hidden = true) @Auth Session session,
            @PathParam("roomId") Long roomId,
            @PathParam("action") String action
    );

    /*
     * POST education/rooms/{roomId}/{action}
     */
    @POST
    @Path("rooms/{roomId}/actionWithMap/{action}")
    @Produces(JSON_UTF8)
    @ApiOperation(value = "Manage a room. Valid actions are open, close, reboot, shutdown, wol, logout, openProxy, closeProxy."
    		+ "This version of call allows to send a map with some parametrs:"
    		+ "graceTime : seconds to wait befor execute action."
    		+ "message : the message to shown befor/during execute the action.")
    @ApiResponses(value = {
            // TODO so oder anders? @ApiResponse(code = 404, message = "At least one room was not found"),
            @ApiResponse(code = 500, message = "Server broken, please contact administrator")
    })
    @RolesAllowed("education.rooms")
    OssResponse manageRoom(
            @ApiParam(hidden = true) @Auth Session session,
            @PathParam("roomId") Long roomId,
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
    OssResponse uploadFileToRoom(@ApiParam(hidden = true) @Auth Session session,
            @PathParam("roomId") Long roomId,
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
    @RolesAllowed("education.groups")
    OssResponse  createGroup(
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
    @RolesAllowed("education.groups")
    OssResponse  modifyGroup(
            @ApiParam(hidden = true) @Auth Session session,
            @PathParam("groupId") Long groupId,
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
     @RolesAllowed("education.groups")
     OssResponse  removeGroup(
             @ApiParam(hidden = true) @Auth Session session,
             @PathParam("groupId") Long groupId
            );
     
     @POST
     @Path("groups/{groupId}/upload")
     @Produces(JSON_UTF8)
     @Consumes(MediaType.MULTIPART_FORM_DATA)
     @ApiOperation( value = "Puts data to te member of the smart rooms" )
     @ApiResponses(value = {
                 @ApiResponse(code = 500, message = "Server broken, please contact administrator")
     })
     OssResponse uploadFileToGroup(@ApiParam(hidden = true) @Auth Session session,
              @PathParam("groupId")  Long  groupId,
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
    @RolesAllowed("education.users")
    OssResponse logOut(
            @ApiParam(hidden = true) @Auth Session session,
            @PathParam("userId") Long userId,
            @PathParam("deviceId") Long deviceId
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
    @RolesAllowed("education.users")
    OssResponse logIn(
            @ApiParam(hidden = true) @Auth Session session,
            @PathParam("userId") Long userId,
            @PathParam("deviceId") Long roomId
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
    @RolesAllowed("education.users")
    List<String> getAvailableUserActions(
            @ApiParam(hidden = true) @Auth Session session,
            @PathParam("userId") Long userId
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
    @RolesAllowed("education.groups")
    List<String> getAvailableGroupActions(
            @ApiParam(hidden = true) @Auth Session session,
            @PathParam("groupId") Long groupId
    );

    /*
     * PUT education/users/{userId}/{deviceId}/{action}
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
    @RolesAllowed("education.users")
    OssResponse manageUSer(
            @ApiParam(hidden = true) @Auth Session session,
            @PathParam("userId") Long userId,
            @PathParam("deviceId") Long deviceId,
            @PathParam("action") String action,
            Map<String, String> actionContent
            );

    @POST
    @Path("users/{userId}/upload")
    @Produces(JSON_UTF8)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @ApiOperation( value = "Puts data to te member of the smart rooms" )
    @ApiResponses(value = {
                @ApiResponse(code = 500, message = "Server broken, please contact administrator")
    })
    OssResponse uploadFileToUser(@ApiParam(hidden = true) @Auth Session session,
            @PathParam("userId")   Long  userId,
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
    @RolesAllowed("education.rooms")
    List<String> getAvailableDeviceActions(
            @ApiParam(hidden = true) @Auth Session session,
            @PathParam("deviceId") Long deviceId
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
    @RolesAllowed("education.rooms")
    OssResponse manageDevice(
            @ApiParam(hidden = true) @Auth Session session,
            @PathParam("deviceId") Long deviceId,
            @PathParam("action") String action,
            Map<String, String> actionContent
    );
    
    @POST
    @Path("devices/{deviceId}/upload")
    @Produces(JSON_UTF8)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @ApiOperation( value = "Puts data to te member of the smart rooms" )
    @ApiResponses(value = {
                @ApiResponse(code = 500, message = "Server broken, please contact administrator")
    })
    @RolesAllowed("education.rooms")
    OssResponse uploadFileToDevice(@ApiParam(hidden = true) @Auth Session session,
            @PathParam("deviceId") Long deviceId,
            @FormDataParam("file") final InputStream fileInputStream,
            @FormDataParam("file") final FormDataContentDisposition contentDispositionHeader
    );
    
    @GET
    @Path("devices/{deviceId}/collect/{projectName}")
    @Produces(JSON_UTF8)
    @ApiOperation( value = "Collects data from a user logged on the corresponding device." )
    @ApiResponses(value = {
                @ApiResponse(code = 500, message = "Server broken, please contact administrator")
    })
    @RolesAllowed("education.rooms")
    OssResponse collectFileFromDevice(
        @ApiParam(hidden = true) @Auth Session session,
        @PathParam("deviceId") Long deviceId,
        @PathParam("projectName") String projectName
    );

    @GET
    @Path("rooms/{roomId}/collect/{projectName}")
    @Produces(JSON_UTF8)
    @ApiOperation( value = "Collects data from a users logged on the corresponding room." )
    @ApiResponses(value = {
                @ApiResponse(code = 500, message = "Server broken, please contact administrator")
    })
    @RolesAllowed("education.rooms")
    OssResponse collectFileFromRoom(
        @ApiParam(hidden = true) @Auth Session session,
        @PathParam("roomId") Long roomId,
        @PathParam("projectName") String projectName
    );

    @GET
    @Path("groups/{groupId}/collect/{projectName}/")
    @Produces(JSON_UTF8)
    @ApiOperation( value = "Collects data from the students member of the corresponding group." )
    @ApiResponses(value = {
                @ApiResponse(code = 500, message = "Server broken, please contact administrator")
    })
    @RolesAllowed("education.rooms")
    OssResponse collectFileFromStudentsOfGroup(
        @ApiParam(hidden = true) @Auth Session session,
        @PathParam("groupId")     Long groupId,
        @PathParam("projectName") String projectName
    );

    @GET
    @Path("groups/{groupId}/collect/{projectName}/all")
    @Produces(JSON_UTF8)
    @ApiOperation( value = "Collects data from the students member of the corresponding group." )
    @ApiResponses(value = {
                @ApiResponse(code = 500, message = "Server broken, please contact administrator")
    })
    @RolesAllowed("education.rooms")
    OssResponse collectFileFromMembersOfGroup(
        @ApiParam(hidden = true) @Auth Session session,
        @PathParam("groupId")     Long groupId,
        @PathParam("projectName") String projectName
    );

    /*
     * Get informations from the printers in the room
     */
    @GET
    @Path("rooms/{roomId}/defaultPrinter")
    @Produces(JSON_UTF8)
    @ApiOperation( value = "Gets the default printer in the room." )
    @ApiResponses(value = {
                @ApiResponse(code = 500, message = "Server broken, please contact administrator")
    })
    @RolesAllowed("education.rooms")
    Device getDefaultPrinter(
        @ApiParam(hidden = true) @Auth Session session,
        @PathParam("roomId") Long roomId
    );

    /*
     * Get informations from the printers in the room
     */
    @GET
    @Path("rooms/{roomId}/availablePrinters")
    @Produces(JSON_UTF8)
    @ApiOperation( value = "Gets the list fo the available printers in the room." )
    @ApiResponses(value = {
                @ApiResponse(code = 500, message = "Server broken, please contact administrator")
    })
    @RolesAllowed("education.rooms")
    List<Device> getAvailablePrinters(
        @ApiParam(hidden = true) @Auth Session session,
        @PathParam("roomId") Long roomId
    );
    
    
    /*******************************************/
    /* Functions to handle proxy settings.     */
    /*******************************************/
    @GET
    @Path("proxy/positiveLists")
    @Produces(JSON_UTF8)
    @ApiOperation( value = "Gets all positive lists." )
    @ApiResponses(value = {
                @ApiResponse(code = 500, message = "Server broken, please contact administrator")
    })
    @RolesAllowed("education.proxy")
    List<PositiveList> getPositiveLists(
        @ApiParam(hidden = true) @Auth Session session
    );
    
    @GET
    @Path("proxy/myPositiveLists")
    @Produces(JSON_UTF8)
    @ApiOperation( value = "Gets owned positive lists." )
    @ApiResponses(value = {
                @ApiResponse(code = 500, message = "Server broken, please contact administrator")
    })
    @RolesAllowed("education.proxy")
    List<PositiveList> getMyPositiveLists(
        @ApiParam(hidden = true) @Auth Session session
    );
    
    @POST
    @Path("proxy/positiveLists")
    @Produces(JSON_UTF8)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @ApiOperation( value = "Creates a new positive list." )
    @ApiResponses(value = {
                @ApiResponse(code = 500, message = "Server broken, please contact administrator")
    })
    @RolesAllowed("education.proxy")
    OssResponse addPositiveList(
        @ApiParam(hidden = true) @Auth Session session,
        PositiveList positiveList
    );
    
    @GET
    @Path("proxy/positiveLists/{positiveListId}")
    @Produces(JSON_UTF8)
    @ApiOperation( value = "Gets the content of a positive list." )
    @ApiResponses(value = {
                @ApiResponse(code = 500, message = "Server broken, please contact administrator")
    })
    @RolesAllowed("education.proxy")
    PositiveList getPositiveListById(
        @ApiParam(hidden = true) @Auth Session session,
        @PathParam("positiveListId") Long positiveListId
    );
    
    @DELETE
    @Path("proxy/positiveLists/{positiveListId}")
    @Produces(JSON_UTF8)
    @ApiOperation( value = "Deletes a positive list." )
    @ApiResponses(value = {
                @ApiResponse(code = 500, message = "Server broken, please contact administrator")
    })
    @RolesAllowed("education.proxy")
    OssResponse deletePositiveListById(
        @ApiParam(hidden = true) @Auth Session session,
        @PathParam("positiveListId") Long positiveListId
    );

    @POST
    @Path("proxy/rooms/{roomId}")
    @Produces(JSON_UTF8)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @ApiOperation( value = "Activates positive lists in a room." )
    @ApiResponses(value = {
                @ApiResponse(code = 500, message = "Server broken, please contact administrator")
    })
    @RolesAllowed("education.proxy")
    OssResponse activatePositiveListsInRoom(
        @ApiParam(hidden = true) @Auth Session session,
        @PathParam("roomId") Long roomId,
        List<Long> postiveListIds
    );
    
    @DELETE
    @Path("proxy/rooms/{roomId}")
    @Produces(JSON_UTF8)
    @ApiOperation( value = "Deactivates positive lists in a room." )
    @ApiResponses(value = {
                @ApiResponse(code = 500, message = "Server broken, please contact administrator")
    })
    @RolesAllowed("education.proxy")
    OssResponse deActivatePositiveListsInRoom(
        @ApiParam(hidden = true) @Auth Session session,
        @PathParam("roomId") Long roomId
    );
}
