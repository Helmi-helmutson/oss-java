/* (c) 2017 Péter Varkoly <peter@varkoly.de> - all rights reserved */
package de.openschoolserver.api.resources;


import io.dropwizard.auth.Auth;

import io.swagger.annotations.*;

import javax.annotation.security.PermitAll;
import javax.ws.rs.*;

import de.openschoolserver.dao.Room;
import de.openschoolserver.dao.AccessInRoom;
import de.openschoolserver.dao.Device;
import de.openschoolserver.dao.Response;
import de.openschoolserver.dao.Session;

import java.util.List;
import java.util.Map;

import static de.openschoolserver.api.resources.Resource.JSON_UTF8;

@Path("rooms")
@Api(value = "rooms")
public interface RoomResource {

	/*
	 * GET rooms/<roomId>
	 */
    @GET
    @Path("{roomId}")
    @Produces(JSON_UTF8)
    @ApiOperation(value = "Get a room by id")
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "Room not found"),
            @ApiResponse(code = 500, message = "Server broken, please contact adminstrator")})
    @PermitAll
    Room getById(
            @ApiParam(hidden = true) @Auth Session session,
            @PathParam("roomId") long roomId
    );

    /*
     * GET rooms/getAll
     */
    @GET
    @Path("all")
    @Produces(JSON_UTF8)
    @ApiOperation(value = "Get all rooms")
    @ApiResponses(value = {
            // TODO so oder anders? @ApiResponse(code = 404, message = "At least one room was not found"),
            @ApiResponse(code = 500, message = "Server broken, please contact administrator")
    })
    @PermitAll
    List<Room> getAll(
            @ApiParam(hidden = true) @Auth Session session
    );

    /*
     * POST rooms/add { hash }
     */
    @POST
    @Path("add")
    @Produces(JSON_UTF8)
    @ApiOperation(value = "Create new room")
    @ApiResponses(value = {
            // TODO so oder anders? @ApiResponse(code = 404, message = "At least one room was not found"),
            @ApiResponse(code = 500, message = "Server broken, please contact administrator")
    })
    @PermitAll
    Response add(
            @ApiParam(hidden = true) @Auth Session session,
            Room room
    );
    
    /*
     * DELETE rooms/{roomId}
     */
    @DELETE
    @Path("{roomId}")
    @Produces(JSON_UTF8)
    @ApiOperation(value = "Delete room by id")
    @ApiResponses(value = {
        @ApiResponse(code = 404, message = "Room not found"),
        @ApiResponse(code = 500, message = "Server broken, please contact adminstrator")})
    @PermitAll
    Response delete(
            @ApiParam(hidden = true) @Auth Session session,
            @PathParam("roomId") long roomId
    );

    
    /*
     * GET rooms/{roomId}/getAvailableIPAddresses
     */
    @GET
    @Path("{roomId}/availableIPAddresses")
    @Produces(JSON_UTF8)
    @ApiOperation(value = "get all available ip-adresses of the room")
        @ApiResponses(value = {
        @ApiResponse(code = 500, message = "Server broken, please contact administrator")
    })
    @PermitAll
    List<String> getAvailableIPAddresses(
            @ApiParam(hidden = true) @Auth Session session,
            @PathParam("roomId") long roomId
    );

    /*
     * PUT rooms/getNextRoomIP/ { netMask : 26, netWork : "10.12.0.0/16" }
     */
    @PUT
    @Path("getNextRoomIP")
    @Produces("text/plain")
    @ApiOperation(value = "Delivers the next free ip address for a room.")
    @ApiResponses(value = {
            // TODO so oder anders? @ApiResponse(code = 404, message = "At least one room was not found"),
            @ApiResponse(code = 500, message = "Server broken, please contact administrator")
    })
    @PermitAll
    String getNextRoomIP(
            @ApiParam(hidden = true) @Auth Session session,
            @FormParam("netWork") String netWork,
            @FormParam("netMask") int netMask
    );
    
    /*
     * GET rooms/{roomId}/loggedInUsers
     */
    @GET
    @Path("{roomId}/loggedInUsers")
    @Produces(JSON_UTF8)
    @ApiOperation(value = "Delivers the list of the users which are logged in in a room.")
    @ApiResponses(value = {
            // TODO so oder anders? @ApiResponse(code = 404, message = "At least one room was not found"),
            @ApiResponse(code = 500, message = "Server broken, please contact administrator")
    })
    @PermitAll
    List<Map<String, String>> getLoggedInUsers(
            @ApiParam(hidden = true) @Auth Session session,
            @PathParam("roomId") long roomId
    );
    
    /*
     * GET rooms/{roomId}/accessList
     */
    @GET
    @Path("{roomId}/accessList")
    @Produces(JSON_UTF8)
    @ApiOperation(value = "Gets the access list in a room")
    @ApiResponses(value = {
            // TODO so oder anders? @ApiResponse(code = 404, message = "At least one room was not found"),
            @ApiResponse(code = 500, message = "Server broken, please contact administrator")
    })
    @PermitAll
    List<AccessInRoom> getAccessList(
            @ApiParam(hidden = true) @Auth Session session,
            @PathParam("roomId") long roomId
    );
    
    /*
     * POST rooms/{roomId}/accessList { List<Hash> }
     */
    @POST
    @Path("{roomId}/accessList")
    @Produces(JSON_UTF8)
    @ApiOperation(value = "Sets the access list in a room")
    @ApiResponses(value = {
            // TODO so oder anders? @ApiResponse(code = 404, message = "At least one room was not found"),
            @ApiResponse(code = 500, message = "Server broken, please contact administrator")
    })
    @PermitAll
    Response setAccessList(
            @ApiParam(hidden = true) @Auth Session session,
            @PathParam("roomId") long roomId,
            List<AccessInRoom>   accessList
    );
    
    /*
     * PUT rooms/scheduledAccess
     */
    @PUT
    @Path("scheduledAccess")
    @Produces(JSON_UTF8)
    @ApiOperation(value = "Sets access in all rooms corresponding to the access lists and the actual time.")
    @ApiResponses(value = {
            // TODO so oder anders? @ApiResponse(code = 404, message = "At least one room was not found"),
            @ApiResponse(code = 500, message = "Server broken, please contact administrator")
    })
    @PermitAll
    Response setScheduledAccess(
    		@ApiParam(hidden = true) @Auth Session session
    );
    
    /*
     * GET rooms/accessStatus
     */
    @GET
    @Path("accessStatus")
    @Produces(JSON_UTF8)
    @ApiOperation(value = "Gets the actual access status in all rooms.")
    @ApiResponses(value = {
            // TODO so oder anders? @ApiResponse(code = 404, message = "At least one room was not found"),
            @ApiResponse(code = 500, message = "Server broken, please contact administrator")
    })
    @PermitAll
    List<AccessInRoom> getAccessStatus(
    		@ApiParam(hidden = true) @Auth Session session
    );

    /*
     * GET rooms/{roomId}/accessStatus
     */
    @GET
    @Path("{roomId}/accessStatus")
    @Produces(JSON_UTF8)
    @ApiOperation(value = "Gets the actual access in a room")
    @ApiResponses(value = {
            // TODO so oder anders? @ApiResponse(code = 404, message = "At least one room was not found"),
            @ApiResponse(code = 500, message = "Server broken, please contact administrator")
    })
    @PermitAll
    AccessInRoom getAccessStatus(
    		@ApiParam(hidden = true) @Auth Session session,
            @PathParam("roomId") long roomId
    );
    
    /*
     * POST rooms/{roomId}/accessStatus
     */
    @POST
    @Path("{roomId}/accessStatus")
    @Produces(JSON_UTF8)
    @ApiOperation(value = "Sets the actual access in a room")
    @ApiResponses(value = {
            // TODO so oder anders? @ApiResponse(code = 404, message = "At least one room was not found"),
            @ApiResponse(code = 500, message = "Server broken, please contact administrator")
    })
    @PermitAll
    Response setAccessStatus(
            @ApiParam(hidden = true) @Auth Session session,
            @PathParam("roomId") long roomId,
            AccessInRoom access
    );
    
    // Functions to manage Devices in Rooms
    /*
     * POST devices/add { hash }
     */
    @POST
    @Path("{roomId}/addDevices")
    @Produces(JSON_UTF8)
    @ApiOperation(value = "Create new devices")
    @ApiResponses(value = {
            // TODO so oder anders? @ApiResponse(code = 404, message = "At least one device was not found"),
            @ApiResponse(code = 500, message = "Server broken, please contact administrator")
    })
    @PermitAll
    Response addDevices(
            @ApiParam(hidden = true) @Auth Session session,
            @PathParam("roomId") long roomId,
            List<Device> devices
    );
    
    /*
     * POST  {roomId}/deleteDevices [ deviceId, deviceId]
     */
    @POST
    @Path("{roomId}/deleteDevices")
    @Produces(JSON_UTF8)
    @ApiOperation(value = "delete devices by id")
    @ApiResponses(value = {
        @ApiResponse(code = 404, message = "Device not found"),
        @ApiResponse(code = 500, message = "Server broken, please contact adminstrator")})
    @PermitAll
    Response deleteDevices(
            @ApiParam(hidden = true) @Auth Session session,
            @PathParam("roomId") long roomId,
            List<Long> deviceId
    );
    
    /*
     * DELETE {roomId}/deleteDevice/{deviceId}
     */
    @DELETE
    @Path("{roomId}/deleteDevice/{deviceId}")
    @Produces(JSON_UTF8)
    @ApiOperation(value = "Delete a device defined by deviceId")
    @ApiResponses(value = {
        @ApiResponse(code = 404, message = "Device not found"),
        @ApiResponse(code = 500, message = "Server broken, please contact adminstrator")})
    @PermitAll
    Response deleteDevice(
            @ApiParam(hidden = true) @Auth Session session,
            @PathParam("roomId") long roomId,
            @PathParam("deviceId") Long deviceId
    );
}
