package de.openschoolserver.api.resources;


import io.dropwizard.auth.Auth;
import io.swagger.annotations.*;

import javax.annotation.security.PermitAll;
import javax.ws.rs.*;

import de.openschoolserver.dao.User;
import de.openschoolserver.dao.Session;

import java.util.List;

import static de.openschoolserver.api.resources.Resource.JSON_UTF8;

@Path("users")
@Api(value = "users")
public interface UserResource {

	/*
	 * GET users/<userId>
	 */
    @GET
    @Path("{userId}")
    @Produces(JSON_UTF8)
    @ApiOperation(value = "get user by id")
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "User not found"),
            @ApiResponse(code = 500, message = "Server broken, please contact adminstrator")})
    @PermitAll
    User getById(
            @ApiParam(hidden = true) @Auth Session session,
            @PathParam("userId") int userId
    );

    /*
     * GET users/getall
     */
    @GET
    @Path("getall")
    @Produces(JSON_UTF8)
    @ApiOperation(value = "get all users")
    @ApiResponses(value = {
            // TODO so oder anders? @ApiResponse(code = 404, message = "At least one user was not found"),
            @ApiResponse(code = 500, message = "Server broken, please contact administrator")
    })
    @PermitAll
    List<User> getAll(
            @ApiParam(hidden = true) @Auth Session session
    );

    /*
     * POST users/add { hash }
     */
    @POST
    @Path("add")
    @Produces(JSON_UTF8)
    @ApiOperation(value = "create new user")
    @ApiResponses(value = {
            // TODO so oder anders? @ApiResponse(code = 404, message = "At least one user was not found"),
            @ApiResponse(code = 500, message = "Server broken, please contact administrator")
    })
    @PermitAll
    boolean add(
            @ApiParam(hidden = true) @Auth Session session,
            User user
    );
    
    /*
     * DELETE users/<userId>
     */
    @DELETE
    @Path("{userId}")
    @Produces(JSON_UTF8)
    @ApiOperation(value = "delete user by id")
    @ApiResponses(value = {
        @ApiResponse(code = 404, message = "User not found"),
        @ApiResponse(code = 500, message = "Server broken, please contact adminstrator")})
    @PermitAll
    boolean delete(
            @ApiParam(hidden = true) @Auth Session session,
            @PathParam("userId") int userId
    );

    
    /*
     * GET users/getall
     */
    @GET
    @Path("{userId}/getAvailableIPAddresses")
    @Produces(JSON_UTF8)
    @ApiOperation(value = "get all available ip-adresses of the user")
        @ApiResponses(value = {
        @ApiResponse(code = 500, message = "Server broken, please contact administrator")
    })
    @PermitAll
    List<String> getAvailableIPAddresses(
            @ApiParam(hidden = true) @Auth Session session,
            @PathParam("userId") int userId
    );

}
