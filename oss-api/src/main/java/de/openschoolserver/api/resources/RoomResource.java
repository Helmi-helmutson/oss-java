/* (c) 2017 Péter Varkoly <peter@varkoly.de> - all rights reserved */
package de.openschoolserver.api.resources;


import io.dropwizard.auth.Auth;


import io.swagger.annotations.*;

import javax.annotation.security.PermitAll;
import javax.ws.rs.*;

import de.openschoolserver.dao.Room;
import de.openschoolserver.dao.AccessInRoom;
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
    @ApiOperation(value = "get room by id")
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
    @Path("getAll")
    @Produces(JSON_UTF8)
    @ApiOperation(value = "get all rooms")
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
    @ApiOperation(value = "create new room")
    @ApiResponses(value = {
            // TODO so oder anders? @ApiResponse(code = 404, message = "At least one room was not found"),
            @ApiResponse(code = 500, message = "Server broken, please contact administrator")
    })
    @PermitAll
    boolean add(
            @ApiParam(hidden = true) @Auth Session session,
            Room room
    );
    
    /*
     * DELETE rooms/{roomId}
     */
    @DELETE
    @Path("{roomId}")
    @Produces(JSON_UTF8)
    @ApiOperation(value = "delete room by id")
    @ApiResponses(value = {
        @ApiResponse(code = 404, message = "Room not found"),
        @ApiResponse(code = 500, message = "Server broken, please contact adminstrator")})
    @PermitAll
    boolean delete(
            @ApiParam(hidden = true) @Auth Session session,
            @PathParam("roomId") long roomId
    );

    
    /*
     * GET rooms/{roomId}/getAvailableIPAddresses
     */
    @GET
    @Path("{roomId}/getAvailableIPAddresses")
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
    @Produces(JSON_UTF8)
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
     * GET rooms/{roomId}/getLoggedInUsers
     */
    @GET
    @Path("{roomId}/getLoggedInUsers")
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
     * GET rooms/{roomId}/getAccessList
     */
    @GET
    @Path("{roomId}/getAccessList")
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
     * POST rooms/{roomId}/getAccessList { List<Hash> }
     */
    @POST
    @Path("{roomId}/setAccessList")
    @Produces(JSON_UTF8)
    @ApiOperation(value = "Sets the access list in a room")
    @ApiResponses(value = {
            // TODO so oder anders? @ApiResponse(code = 404, message = "At least one room was not found"),
            @ApiResponse(code = 500, message = "Server broken, please contact administrator")
    })
    @PermitAll
    boolean setAccessList(
            @ApiParam(hidden = true) @Auth Session session,
            @PathParam("roomId") long roomId,
            List<AccessInRoom>   accessList
    );
    
    /*
     * GET rooms/setScheduledAccess
     */
    @GET
    @Path("setScheduledAccess")
    @Produces(JSON_UTF8)
    @ApiOperation(value = "Sets access in all rooms corresponding to the access lists and the actual time.")
    @ApiResponses(value = {
            // TODO so oder anders? @ApiResponse(code = 404, message = "At least one room was not found"),
            @ApiResponse(code = 500, message = "Server broken, please contact administrator")
    })
    @PermitAll
    boolean setScheduledAccess(
    		@ApiParam(hidden = true) @Auth Session session
    );
    
    /*
     * GET rooms/getAccessStatus
     */
    @GET
    @Path("getAccessStatus")
    @Produces(JSON_UTF8)
    @ApiOperation(value = "Gets the actual access.")
    @ApiResponses(value = {
            // TODO so oder anders? @ApiResponse(code = 404, message = "At least one room was not found"),
            @ApiResponse(code = 500, message = "Server broken, please contact administrator")
    })
    @PermitAll
    List<AccessInRoom> getAccessStatus(
    		@ApiParam(hidden = true) @Auth Session session
    );

    /*
     * GET rooms/{roomId}/getAccessStatus
     */
    @GET
    @Path("{roomId}/getAccessStatus")
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
     * POST rooms/{roomId}/setAccessStatus
     */
    @POST
    @Path("{roomId}/setAccessStatus")
    @Produces(JSON_UTF8)
    @ApiOperation(value = "Sets the actual access in a room")
    @ApiResponses(value = {
            // TODO so oder anders? @ApiResponse(code = 404, message = "At least one room was not found"),
            @ApiResponse(code = 500, message = "Server broken, please contact administrator")
    })
    @PermitAll
    boolean setAccessStatus(
            @ApiParam(hidden = true) @Auth Session session,
            @PathParam("roomId") long roomId,
            AccessInRoom access
    );
}
