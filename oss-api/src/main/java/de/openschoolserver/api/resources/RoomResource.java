/* (c) 2017 Peter Varkoly <peter@varkoly.de> - all rights reserved */
package de.openschoolserver.api.resources;


import io.dropwizard.auth.Auth;




import io.swagger.annotations.*;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;

import de.openschoolserver.dao.Room;
import de.openschoolserver.dao.AccessInRoom;
import de.openschoolserver.dao.Device;
import de.openschoolserver.dao.HWConf;
import de.openschoolserver.dao.OssResponse;
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
    @RolesAllowed("room.search")
    Room getById(
            @ApiParam(hidden = true) @Auth Session session,
            @PathParam("roomId") long roomId
    );

    /*
     * GET rooms/all
     */
    @GET
    @Path("all")
    @Produces(JSON_UTF8)
    @ApiOperation(value = "Get all rooms")
    @ApiResponses(value = {
            // TODO so oder anders? @ApiResponse(code = 404, message = "At least one room was not found"),
            @ApiResponse(code = 500, message = "Server broken, please contact administrator")
    })
    @RolesAllowed("room.search")
    List<Room> getAll(
            @ApiParam(hidden = true) @Auth Session session
    );

    /*
     * GET rooms/toRegister
     */
    @GET
    @Path("toRegister")
    @Produces(JSON_UTF8)
    @ApiOperation(value = "Get all rooms where devices can be registered")
    @ApiResponses(value = {
            // TODO so oder anders? @ApiResponse(code = 404, message = "At least one room was not found"),
            @ApiResponse(code = 500, message = "Server broken, please contact administrator")
    })
    @RolesAllowed("room.search")
    List<Room> getRoomsToRegister(
            @ApiParam(hidden = true) @Auth Session session
    );

    /*
     * GET rooms/search/{search}
     */
    @GET
    @Path("search/{search}")
    @Produces(JSON_UTF8)
    @ApiOperation(value = "Search for room by name and descripton and type with substring.")
    @ApiResponses(value = {
            // TODO so oder anders? @ApiResponse(code = 404, message = "At least one user was not found"),
            @ApiResponse(code = 500, message = "Server broken, please contact administrator")
    })
    //@PermitAll
    @RolesAllowed("room.search")
    List<Room> search(
            @ApiParam(hidden = true) @Auth Session session,
            @PathParam("search") String search
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
    @RolesAllowed("room.add")
    OssResponse add(
            @ApiParam(hidden = true) @Auth Session session,
            Room room
    );
    
    /*
     * POST rooms/add { hash }
     */
    @POST
    @Path("modify")
    @Produces(JSON_UTF8)
    @ApiOperation(value = "Modify a room")
    @ApiResponses(value = {
            // TODO so oder anders? @ApiResponse(code = 404, message = "At least one room was not found"),
            @ApiResponse(code = 500, message = "Server broken, please contact administrator")
    })
    @RolesAllowed("room.add")
    OssResponse modify(
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
    @RolesAllowed("room.delete")
    OssResponse delete(
            @ApiParam(hidden = true) @Auth Session session,
            @PathParam("roomId") long roomId
    );

    /*
     * GET rooms/{roomId}/hwConf
     */
    @GET
    @Path("{roomId}/hwConf")
    @Produces(JSON_UTF8)
    @ApiOperation(value = "Get hardware configuration of the room")
        @ApiResponses(value = {
        	@ApiResponse(code = 404, message = "There is no more IP address in this room."),
            @ApiResponse(code = 500, message = "Server broken, please contact administrator")
    })
    @RolesAllowed("room.search")
    HWConf getHwConf(
            @ApiParam(hidden = true) @Auth Session session,
            @PathParam("roomId") long roomId
    );
    
    /*
     * SET rooms/{roomId}/hwConf
     */
    @PUT
    @Path("{roomId}/{hwConfId}")
    @Produces(JSON_UTF8)
    @ApiOperation(value = "Set hardware configuration of the room")
        @ApiResponses(value = {
        	@ApiResponse(code = 404, message = "There is no more IP address in this room."),
            @ApiResponse(code = 500, message = "Server broken, please contact administrator")
    })
    @RolesAllowed("room.modify")
    OssResponse setHwConf(
            @ApiParam(hidden = true) @Auth Session session,
            @PathParam("roomId")   long roomId,
            @PathParam("hwConfId") long hwConfId
    );

    /*
     * GET rooms/{roomId}/availableIPAddresses
     */
    @GET
    @Path("{roomId}/availableIPAddresses")
    @Produces(JSON_UTF8)
    @ApiOperation(value = "get all available ip-adresses of the room")
        @ApiResponses(value = {
        	@ApiResponse(code = 404, message = "There is no more IP address in this room."),
            @ApiResponse(code = 500, message = "Server broken, please contact administrator")
    })
    @RolesAllowed("device.add")
    List<String> getAvailableIPAddresses(
            @ApiParam(hidden = true) @Auth Session session,
            @PathParam("roomId") long roomId
    );

    /*
     * GET rooms/{roomId}/getAvailableIPAddresses
     */
    @GET
    @Path("{roomId}/availableIPAddresses/{count}")
    @Produces(JSON_UTF8)
    @ApiOperation(value = "Get count available ip-adresses of the room. The string list will contains the proposed name too: 'IP-Addres Proposed-Name'")
        @ApiResponses(value = {
        @ApiResponse(code = 404, message = "There is no more IP address in this room."),
        @ApiResponse(code = 500, message = "Server broken, please contact administrator")
    })
    @RolesAllowed("device.add")
    List<String> getAvailableIPAddresses(
            @ApiParam(hidden = true) @Auth Session session,
            @PathParam("roomId") long roomId,
            @PathParam("count") long count
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
    @RolesAllowed("room.add")
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
    @RolesAllowed("room.manage")
    List<Map<String, String>> getLoggedInUsers(
            @ApiParam(hidden = true) @Auth Session session,
            @PathParam("roomId") long roomId
    );
    
    /*
   	 * POST room/getRooms
   	 */
       @POST
       @Path("getRooms")
       @Produces(JSON_UTF8)
       @ApiOperation(value = "Gets a list of room objects to the list of roomIds.")
       @ApiResponses(value = {
               @ApiResponse(code = 404, message = "Group not found"),
               @ApiResponse(code = 500, message = "Server broken, please contact adminstrator")})
       @RolesAllowed("room.search")
       List<Room> getRooms(
               @ApiParam(hidden = true) @Auth Session session,
               List<Long> roomIds
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
    @RolesAllowed("room.add")
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
    @RolesAllowed("room.add")
    OssResponse setAccessList(
            @ApiParam(hidden = true) @Auth Session session,
            @PathParam("roomId") long roomId,
            List<AccessInRoom>   accessList
    );
    
    /*
     * PUT rooms/scheduledAccess
     */
    @PUT
    @Path("setScheduledAccess")
    @Produces(JSON_UTF8)
    @ApiOperation(value = "Sets access in all rooms corresponding to the access lists and the actual time.")
    @ApiResponses(value = {
            // TODO so oder anders? @ApiResponse(code = 404, message = "At least one room was not found"),
            @ApiResponse(code = 500, message = "Server broken, please contact administrator")
    })
    @RolesAllowed("room.add")
    OssResponse setScheduledAccess(
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
    @RolesAllowed("room.add")
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
    @RolesAllowed("room.manage")
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
    @RolesAllowed("room.manage")
    OssResponse setAccessStatus(
            @ApiParam(hidden = true) @Auth Session session,
            @PathParam("roomId") long roomId,
            AccessInRoom access
    );
    
    // Functions to manage Devices in Rooms
    /*
     * POST rooms/{roomId}/devices { hash }
     */
    @POST
    @Path("{roomId}/devices")
    @Produces(JSON_UTF8)
    @ApiOperation(value = "Create new devices")
    @ApiResponses(value = {
            // TODO so oder anders? @ApiResponse(code = 404, message = "At least one device was not found"),
            @ApiResponse(code = 500, message = "Server broken, please contact administrator")
    })
    @RolesAllowed("device.add")
    OssResponse addDevices(
            @ApiParam(hidden = true) @Auth Session session,
            @PathParam("roomId") long roomId,
            List<Device> devices
    );
    
    /*
     * PUT rooms/{roomId}/device/{macAddress}/{name}
     */
    @PUT
    @Path("{roomId}/device/{macAddress}/{name}")
    @Produces(JSON_UTF8)
    @ApiOperation(value = "Create a new device. This api call can be used only for registering own devices.")
    @ApiResponses(value = {
            // TODO so oder anders? @ApiResponse(code = 404, message = "At least one device was not found"),
            @ApiResponse(code = 500, message = "Server broken, please contact administrator")
    })
    @PermitAll
    OssResponse addDevice(
            @ApiParam(hidden = true) @Auth Session session,
            @PathParam("roomId") long roomId,
            @PathParam("macAddress") String macAddress,
            @PathParam("name") String name
    );

    /*
     * GET rooms/{roomId}/getDevices
     */
    @GET
    @Path("{roomId}/devices")
    @Produces(JSON_UTF8)
    @ApiOperation(value = "Gets a list of the devices in room.")
    @ApiResponses(value = {
            // TODO so oder anders? @ApiResponse(code = 404, message = "At least one device was not found"),
            @ApiResponse(code = 500, message = "Server broken, please contact administrator")
    })
    @RolesAllowed("room.manage")
    List<Device> getDevices(
            @ApiParam(hidden = true) @Auth Session session,
            @PathParam("roomId") long roomId
    );
    
    /*
     * POST  rooms/{roomId}/deleteDevices [ deviceId, deviceId]
     */
    @POST
    @Path("{roomId}/deleteDevices")
    @Produces(JSON_UTF8)
    @ApiOperation(value = "delete devices by id")
    @ApiResponses(value = {
        @ApiResponse(code = 404, message = "Device not found"),
        @ApiResponse(code = 500, message = "Server broken, please contact adminstrator")})
    @RolesAllowed("device.delete")
    OssResponse deleteDevices(
            @ApiParam(hidden = true) @Auth Session session,
            @PathParam("roomId") long roomId,
            List<Long> deviceId
    );
    
    /*
     * DELETE {roomId}/deleteDevice/{deviceId}
     */
    @DELETE
    @Path("{roomId}/device/{deviceId}")
    @Produces(JSON_UTF8)
    @ApiOperation(value = "Delete a device defined by deviceId")
    @ApiResponses(value = {
        @ApiResponse(code = 404, message = "Device not found"),
        @ApiResponse(code = 500, message = "Server broken, please contact adminstrator")})
    @RolesAllowed("device.delete")
    OssResponse deleteDevice(
            @ApiParam(hidden = true) @Auth Session session,
            @PathParam("roomId") long roomId,
            @PathParam("deviceId") Long deviceId
    );
}
