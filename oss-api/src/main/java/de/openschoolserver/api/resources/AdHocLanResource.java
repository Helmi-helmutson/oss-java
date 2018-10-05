/* (c) 2017 Péter Varkoly <peter@varkoly.de> - all rights reserved */
package de.openschoolserver.api.resources;

import static de.openschoolserver.api.resources.Resource.JSON_UTF8;


import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.POST;
import javax.ws.rs.DELETE;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import io.dropwizard.auth.Auth;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import de.openschoolserver.dao.Device;
import de.openschoolserver.dao.Group;
import de.openschoolserver.dao.OssResponse;
import de.openschoolserver.dao.Room;
import de.openschoolserver.dao.Session;
import de.openschoolserver.dao.User;

import java.util.List;

@Path("adhoclan")
@Api(value = "adhoclan")
public interface AdHocLanResource {

	/*
	 * Get adhoclan/rooms/{roomId}/{objectType}
	 */
	@GET
	@Path("rooms/{roomId}/users")
	@Produces(JSON_UTF8)
	@ApiOperation(value = "Gets all defined groups or users or devices in a giwen AdHocLan room. Object types can be Group or User")
	@ApiResponses(value = {
			@ApiResponse(code = 404, message = "No category was found"),
			@ApiResponse(code = 500, message = "Server broken, please contact adminstrator")})
	@RolesAllowed("adhoclan.manage")
	List<User> getUsersOfRoom(
			@ApiParam(hidden = true) @Auth Session session,
			@PathParam("roomId")     Long roomId
			);

	@GET
	@Path("rooms/{roomId}/groups")
	@Produces(JSON_UTF8)
	@ApiOperation(value = "Gets all defined groups or users or devices in a giwen AdHocLan room. Object types can be Group or User")
	@ApiResponses(value = {
			@ApiResponse(code = 404, message = "No category was found"),
			@ApiResponse(code = 500, message = "Server broken, please contact adminstrator")})
	@RolesAllowed("adhoclan.manage")
	List<Group> getGroupsOfRoom(
			@ApiParam(hidden = true) @Auth Session session,
			@PathParam("roomId")     Long roomId
			);

	/*
	 * GET categories/<roomId>/available/<memeberType>
	 */
	@GET
	@Path("rooms/{roomId}/available/users")
	@Produces(JSON_UTF8)
	@ApiOperation(value = "Get the non member users of an AdHocLan room.")
	@ApiResponses(value = {
			@ApiResponse(code = 404, message = "Category not found"),
			@ApiResponse(code = 500, message = "Server broken, please contact adminstrator")})
	@RolesAllowed("adhoclan.manage")
	List<User> getAvailableUser(
			@ApiParam(hidden = true) @Auth Session session,
			@PathParam("roomId") long roomId
			);

	@GET
	@Path("rooms/{roomId}/available/groups")
	@Produces(JSON_UTF8)
	@ApiOperation(value = "Get the non member groups of an AdHocLan room.")
	@ApiResponses(value = {
			@ApiResponse(code = 404, message = "Category not found"),
			@ApiResponse(code = 500, message = "Server broken, please contact adminstrator")})
	@RolesAllowed("adhoclan.manage")
	List<Group> getAvailableGroups(
			@ApiParam(hidden = true) @Auth Session session,
			@PathParam("roomId") long roomId
			);
	
	/*
	 * Get adhoclan/users
	 */
	@GET
	@Path("users")
	@Produces(JSON_UTF8)
	@ApiOperation(value = "Gets all users which may use AdHocLan Devices.")
	@ApiResponses(value = {
			@ApiResponse(code = 404, message = "No category was found"),
			@ApiResponse(code = 500, message = "Server broken, please contact adminstrator")})
	@RolesAllowed("adhoclan.manage")
	List<User> getUsers(
			@ApiParam(hidden = true) @Auth Session session
			);
	
	/*
	 * Get adhoclan/groups
	 */
	@GET
	@Path("groups")
	@Produces(JSON_UTF8)
	@ApiOperation(value = "Gets all Groups which have AdHocLan access.")
	@ApiResponses(value = {
			@ApiResponse(code = 404, message = "No category was found"),
			@ApiResponse(code = 500, message = "Server broken, please contact adminstrator")})
	@RolesAllowed("adhoclan.manage")
	List<Group> getGroups(
			@ApiParam(hidden = true) @Auth Session session
			);
	
    /*
     * POST addhoclan/rooms { hash }
     */
    @POST
    @Path("rooms")
    @Produces(JSON_UTF8)
    @ApiOperation(value = "Create new AddHocLan room")
    @ApiResponses(value = {
            // TODO so oder anders? @ApiResponse(code = 404, message = "At least one room was not found"),
            @ApiResponse(code = 500, message = "Server broken, please contact administrator")
    })
    @RolesAllowed("adhoclan.manage")
    OssResponse add(
            @ApiParam(hidden = true) @Auth Session session,
            Room room
    );

    /*
     * PUT addhoclan/rooms/{roomId}/{objectType}/{objectId}
     */
    @PUT
	@Path("rooms/{roomId}")
	@Produces(JSON_UTF8)
	@ApiOperation(value = "Define a room as AdHocLan room")
	@ApiResponses(value = {
			@ApiResponse(code = 404, message = "No category was found"),
			@ApiResponse(code = 500, message = "Server broken, please contact adminstrator")})
	@RolesAllowed("adhoclan.manage")
	OssResponse turnOn(
			@ApiParam(hidden = true) @Auth Session session,
			@PathParam("roomId")		Long roomId
			);

    /*
     * POST addhoclan/rooms/{roomId}
     */
    @GET
	@Path("rooms/{roomId}")
	@Produces(JSON_UTF8)
	@ApiOperation(value = "Gets an AdHocLan room.")
	@ApiResponses(value = {
			@ApiResponse(code = 404, message = "No category was found"),
			@ApiResponse(code = 500, message = "Server broken, please contact adminstrator")})
	@RolesAllowed("adhoclan.manage")
	Room getRoomById(
			@ApiParam(hidden = true) @Auth Session session,
			@PathParam("roomId")		Long roomId
			);

    /*
     * POST addhoclan/rooms/{roomId}
     */
    @POST
	@Path("rooms/{roomId}")
	@Produces(JSON_UTF8)
	@ApiOperation(value = "Modify an AdHocLan room")
	@ApiResponses(value = {
			@ApiResponse(code = 404, message = "No category was found"),
			@ApiResponse(code = 500, message = "Server broken, please contact adminstrator")})
	@RolesAllowed("adhoclan.manage")
	OssResponse modify(
			@ApiParam(hidden = true) @Auth Session session,
			@PathParam("roomId")		Long roomId,
			Room room
			);

    /*
     * Get addhoclan/rooms/{roomId}/studentsOnly
     */
    @GET
	@Path("rooms/{roomId}/studentsOnly")
	@Produces(JSON_UTF8)
	@ApiOperation(value = "Gets an AdHocLan room.")
	@ApiResponses(value = {
			@ApiResponse(code = 404, message = "No category was found"),
			@ApiResponse(code = 500, message = "Server broken, please contact adminstrator")})
	@RolesAllowed("adhoclan.manage")
	boolean getStudentsOnly(
			@ApiParam(hidden = true) @Auth Session session,
			@PathParam("roomId")		Long roomId
			);

    /*
     * Get addhoclan/rooms/{roomId}/studentsOnly
     */
    @POST
	@Path("rooms/{roomId}/studentsOnly")
	@Produces(JSON_UTF8)
	@ApiOperation(value = "Gets an AdHocLan room.")
	@ApiResponses(value = {
			@ApiResponse(code = 404, message = "No category was found"),
			@ApiResponse(code = 500, message = "Server broken, please contact adminstrator")})
	@RolesAllowed("adhoclan.manage")
    OssResponse setStudentsOnly(
			@ApiParam(hidden = true) @Auth Session session,
			@PathParam("roomId")		Long roomId,
			boolean studentsOnly
			);


    /*
     * PUT addhoclan/rooms/{roomId}/{objectType}/{objectId}
     */
    @PUT
	@Path("rooms/{roomId}/{objectType}/{objectId}")
	@Produces(JSON_UTF8)
	@ApiOperation(value = "Add a new group or user to a giwen AdHocLan room")
	@ApiResponses(value = {
			@ApiResponse(code = 404, message = "No category was found"),
			@ApiResponse(code = 500, message = "Server broken, please contact adminstrator")})
	@RolesAllowed("adhoclan.manage")
	OssResponse putObjectIntoRoom(
			@ApiParam(hidden = true) @Auth Session session,
			@PathParam("roomId")		Long roomId,
			@PathParam("objectType")	String onjectType,
			@PathParam("objectId")		Long objectId
			);

    /*
     * PUT addhoclan/rooms/{roomId}/{objectType}/{objectId}
     */
    @DELETE
	@Path("rooms/{roomId}/{objectType}/{objectId}")
	@Produces(JSON_UTF8)
	@ApiOperation(value = "Removes a group or user from an AdHocLan room")
	@ApiResponses(value = {
			@ApiResponse(code = 404, message = "No category was found"),
			@ApiResponse(code = 500, message = "Server broken, please contact adminstrator")})
	@RolesAllowed("adhoclan.manage")
	OssResponse deleteObjectIntoRoom(
			@ApiParam(hidden = true) @Auth Session session,
			@PathParam("roomId")		Long roomId,
			@PathParam("objectType")	String onjectType,
			@PathParam("objectId")		Long objectId
			);

    /*
     * Functions for normal user which are allowed to register the own devices.
     */
	/*
	 * Get adhoclan/rooms
	 */
	@GET
	@Path("rooms")
	@Produces(JSON_UTF8)
	@ApiOperation(value = "Gets all defined AdHocLan Rooms which a user may use. Superuser get the list of all AdHocLan rooms.")
	@ApiResponses(value = {
			@ApiResponse(code = 404, message = "No room was found"),
			@ApiResponse(code = 500, message = "Server broken, please contact adminstrator")})
	@PermitAll
	List<Room> getRooms(
			@ApiParam(hidden = true) @Auth Session session
			);

    /*
	 * Get adhoclan/devices
	 */
	@GET
	@Path("devices")
	@Produces(JSON_UTF8)
	@ApiOperation(value = "Gets all owned AdHocLan Devices of a user.")
	@ApiResponses(value = {
			@ApiResponse(code = 404, message = "No category was found"),
			@ApiResponse(code = 500, message = "Server broken, please contact adminstrator")})
	@PermitAll
	List<Device> getDevices(
			@ApiParam(hidden = true) @Auth Session session
			);

	/*
	 * Get adhoclan/devices/{deviceId}
	 */
	@DELETE
	@Path("devices/{deviceId}")
	@Produces(JSON_UTF8)
	@ApiOperation(value = "Delets an owned AdHocLan Devices of a user.")
	@ApiResponses(value = {
			@ApiResponse(code = 404, message = "No category was found"),
			@ApiResponse(code = 500, message = "Server broken, please contact adminstrator")})
	@PermitAll
	OssResponse deleteDevice(
			@ApiParam(hidden = true) @Auth Session session,
			@PathParam("deviceId")         Long    deviceId
			);

	/*
	 * Get adhoclan/devices/{deviceId}
	 */
	@POST
	@Path("devices/{deviceId}")
	@Produces(JSON_UTF8)
	@ApiOperation(value = "Modify an owned AdHocLan Devices of a user.")
	@ApiResponses(value = {
			@ApiResponse(code = 404, message = "No category was found"),
			@ApiResponse(code = 500, message = "Server broken, please contact adminstrator")})
	@PermitAll
	OssResponse modifyDevice(
			@ApiParam(hidden = true) @Auth Session session,
			@PathParam("deviceId")         Long    deviceId,
			Device device
			);

    @PUT
    @Path("rooms/{roomId}/device/{MAC}/{name}")
    @Produces(JSON_UTF8)
    @ApiOperation(value = "Create a new device. This api call can be used only for registering own devices.")
    @ApiResponses(value = {
            // TODO so oder anders? @ApiResponse(code = 404, message = "At least one device was not found"),
            @ApiResponse(code = 500, message = "Server broken, please contact administrator")
    })
    @PermitAll
    OssResponse addDevice(
            @ApiParam(hidden = true) @Auth Session session,
            @PathParam("roomId")		long roomId,
            @PathParam("MAC")			String macAddress,
            @PathParam("name")			String name
    );
}
