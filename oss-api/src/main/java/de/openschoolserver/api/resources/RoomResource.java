package de.openschoolserver.api.resources;


import io.dropwizard.auth.Auth;
import io.swagger.annotations.*;

import javax.annotation.security.PermitAll;
import javax.ws.rs.*;

import de.openschoolserver.dao.Room;
import de.openschoolserver.dao.Session;

import java.util.List;

import static de.openschoolserver.api.resources.Resource.JSON_UTF8;

@Path("rooms")
@Api(value = "rooms")
public interface RoomResource {

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
            @PathParam("roomId") int roomId
    );

    @GET
    @Produces(JSON_UTF8)
    @ApiOperation(value = "get rooms by ids")
    @ApiResponses(value = {
            // TODO so oder anders? @ApiResponse(code = 404, message = "At least one room was not found"),
            @ApiResponse(code = 500, message = "Server broken, please contact administrator")
    })
    @PermitAll
    List<Room> getByIds(
            @ApiParam(hidden = true) @Auth Session session,
            @QueryParam("ids") List<Integer> roomIds
    );

}
