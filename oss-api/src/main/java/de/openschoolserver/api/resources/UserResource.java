/* (c) 2017 Péter Varkoly <peter@varkoly.de> - all rights reserved */
package de.openschoolserver.api.resources;


import io.dropwizard.auth.Auth;

import io.swagger.annotations.*;

import javax.annotation.security.RolesAllowed;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.ws.rs.*;

import de.openschoolserver.dao.User;
import de.openschoolserver.dao.Group;
import de.openschoolserver.dao.OssResponse;
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
    @ApiOperation(value = "Get user by id")
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "User not found"),
            @ApiResponse(code = 500, message = "Server broken, please contact adminstrator")})
    @RolesAllowed("user.search")
    User getById(
            @ApiParam(hidden = true) @Auth Session session,
            @PathParam("userId") long userId
    );

    /*
	 * GET users/<userId>
	 */
    @GET
    @Path("{userId}/groups")
    @Produces(JSON_UTF8)
    @ApiOperation(value = "Get groups the user is member in it.")
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "User not found"),
            @ApiResponse(code = 500, message = "Server broken, please contact adminstrator")})
    @RolesAllowed("user.search")
    List<Group> groups(
            @ApiParam(hidden = true) @Auth Session session,
            @PathParam("userId") long userId
    );

    /*
	 * GET users/<userId>
	 */
    @GET
    @Path("{userId}/availableGroups")
    @Produces(JSON_UTF8)
    @ApiOperation(value = "Get groups the user is not member in it.")
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "User not found"),
            @ApiResponse(code = 500, message = "Server broken, please contact adminstrator")})
    @RolesAllowed("user.search")
    List<Group> getAvailableGroups(
            @ApiParam(hidden = true) @Auth Session session,
            @PathParam("userId") long userId
    );

    /*
     * GET users/byRole/<role>
     */
    @GET
    @Path("byRole/{role}")
    @Produces(JSON_UTF8)
    @ApiOperation(value = "Get users from a rolle")
        @ApiResponses(value = {
        @ApiResponse(code = 500, message = "Server broken, please contact administrator")
    })
    @RolesAllowed("user.search")
    List<User> getByRole(
            @ApiParam(hidden = true) @Auth Session session,
            @PathParam("role") String role
    );

    /*
     * GET users/getAll
     */
    @GET
    @Path("all")
    @Produces(JSON_UTF8)
    @ApiOperation(value = "Get all users")
    @ApiResponses(value = {
            // TODO so oder anders? @ApiResponse(code = 404, message = "At least one user was not found"),
            @ApiResponse(code = 500, message = "Server broken, please contact administrator")
    })
    @RolesAllowed("user.search")
    @JsonIgnoreProperties({"groups"})
    List<User> getAll(
            @ApiParam(hidden = true) @Auth Session session
    );

    /*
     * GET search/{search}
     */
    @GET
    @Path("search/{search}")
    @Produces(JSON_UTF8)
    @ApiOperation(value = "Search for user by uid and name.")
    @ApiResponses(value = {
            // TODO so oder anders? @ApiResponse(code = 404, message = "At least one user was not found"),
            @ApiResponse(code = 500, message = "Server broken, please contact administrator")
    })
    //@PermitAll
    @RolesAllowed("user.search")
    List<User> search(
            @ApiParam(hidden = true) @Auth Session session,
            @PathParam("search") String search
    );
    
    /*
   	 * POST user/getUsers
   	 */
       @POST
       @Path("getUsers")
       @Produces(JSON_UTF8)
       @ApiOperation(value = "Gets a list of user objects to the list of userIds.")
       @ApiResponses(value = {
               @ApiResponse(code = 500, message = "Server broken, please contact adminstrator")})
       @RolesAllowed("user.search")
       List<User> getUsers(
               @ApiParam(hidden = true) @Auth Session session,
               List<Long> userIds
       );

    /*
     * POST users/add { hash }
     */
    @POST
    @Path("add")
    @Produces(JSON_UTF8)
    @ApiOperation(value = "Create new user")
    @ApiResponses(value = {
            // TODO so oder anders? @ApiResponse(code = 404, message = "At least one user was not found"),
            @ApiResponse(code = 500, message = "Server broken, please contact administrator")
    })
    //@PermitAll
    @RolesAllowed("user.add")
    OssResponse add(
            @ApiParam(hidden = true) @Auth Session session,
            User user
    );
    
    /*
     * POST users/add [ { hash }, { user } ]
     */
    @POST
    @Path("addList")
    @Produces(JSON_UTF8)
    @ApiOperation(value = "Create new users")
    @ApiResponses(value = {
            // TODO so oder anders? @ApiResponse(code = 404, message = "At least one user was not found"),
            @ApiResponse(code = 500, message = "Server broken, please contact administrator")
    })
    //@PermitAll
    @RolesAllowed("user.add")
    List<OssResponse> add(
            @ApiParam(hidden = true) @Auth Session session,
            List<User> users
    );
    
    /*
     * POST users/modify { hash }
     */
    @POST
    @Path("modify")
    @Produces(JSON_UTF8)
    @ApiOperation(value = "modify an existing user")
    @ApiResponses(value = {
            // TODO so oder anders? @ApiResponse(code = 404, message = "At least one user was not found"),
            @ApiResponse(code = 500, message = "Server broken, please contact administrator")
    })
    //@PermitAll
    @RolesAllowed("user.modify")
    OssResponse modify(
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
    @RolesAllowed("user.delete")
    OssResponse delete(
            @ApiParam(hidden = true) @Auth Session session,
            @PathParam("userId") long userId
    );
    
    /*
   	 * POST users/<userId>/groups
   	 */
       @POST
       @Path("{userId}/groups")
       @Produces(JSON_UTF8)
       @ApiOperation(value = "Put the user to this groups as member.")
       @ApiResponses(value = {
               @ApiResponse(code = 404, message = "Group not found"),
               @ApiResponse(code = 500, message = "Server broken, please contact adminstrator")})
       @RolesAllowed("user.manage")
       OssResponse setMembers(
               @ApiParam(hidden = true) @Auth Session session,
               @PathParam("userId") long userId,
               List<Long> groups
       );
       
    
    /*
     * DELETE users/<userId>/<groupId>
     */
    @DELETE
    @Path("{userId}/{groupId}")
    @Produces(JSON_UTF8)
    @ApiOperation(value = "Removes the user from a group.")
    @ApiResponses(value = {
        @ApiResponse(code = 404, message = "User not found"),
        @ApiResponse(code = 500, message = "Server broken, please contact adminstrator")})
    @RolesAllowed("user.manage")
    OssResponse removeMember(
            @ApiParam(hidden = true) @Auth Session session,
            @PathParam("groupId") long groupId,
            @PathParam("userId") long userId
    );
    
    /*
     * PUT users/<groupId>/<userId>
     */
    @PUT
    @Path("{userId}/{groupId}")
    @Produces(JSON_UTF8)
    @ApiOperation(value = "Add user to a group.")
    @ApiResponses(value = {
        @ApiResponse(code = 404, message = "User not found"),
        @ApiResponse(code = 500, message = "Server broken, please contact adminstrator")})
    @RolesAllowed("user.manage")
    OssResponse addMember(
            @ApiParam(hidden = true) @Auth Session session,
            @PathParam("groupId") long groupId,
            @PathParam("userId") long userId
    );
    
    /*
     * POST syncFsQuotas
     */
    @POST
    @Path("syncFsQuotas")
    @Produces(JSON_UTF8)
    @ApiOperation(value = "Synchronize the file system quota values into the JPA")
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "User not found"),
            @ApiResponse(code = 500, message = "Server broken, please contact adminstrator")})
    @RolesAllowed("user.manage")
    OssResponse syncFsQuotas(
                @ApiParam(hidden = true) @Auth Session session,
                List<List<String>> Quotas
    );
    
    /*
     * GET users/byUid/{uid}/{attribute}
     * Get's an attribute from a user 
     */
    @GET
    @Path("byUid/{uid}/{attribute}")
    @Produces("text/plain")
    @ApiOperation(value = "Reads some attributes from a user. Available attributes are: role uuid givenname surname groups.")
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "User not found"),
            @ApiResponse(code = 500, message = "Server broken, please contact adminstrator")})
     @RolesAllowed("user.search")
     String getUserAttribute(
    		@ApiParam(hidden = true) @Auth Session session,
            @PathParam("uid")  String uid,
            @PathParam("attribute") String attribute
    		);
     
}
