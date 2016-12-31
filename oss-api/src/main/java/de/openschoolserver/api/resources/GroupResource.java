/* (c) 2017 Péter Varkoly <peter@varkoly.de> - all rights reserved */
package de.openschoolserver.api.resources;


import io.dropwizard.auth.Auth;

import io.swagger.annotations.*;

import javax.annotation.security.PermitAll;
import javax.ws.rs.*;

import de.openschoolserver.dao.User;
import de.openschoolserver.dao.Group;
import de.openschoolserver.dao.Session;

import java.util.List;

import static de.openschoolserver.api.resources.Resource.JSON_UTF8;

@Path("groups")
@Api(value = "groups")
public interface GroupResource {

	/*
	 * GET groups/<groupID>
	 */
    @GET
    @Path("{groupID}")
    @Produces(JSON_UTF8)
    @ApiOperation(value = "get group by id")
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "Group not found"),
            @ApiResponse(code = 500, message = "Server broken, please contact adminstrator")})
    @PermitAll
    Group getByID(
            @ApiParam(hidden = true) @Auth Session session,
            @PathParam("groupID") long groupID
    );

    /*
	 * GET groups/<groupID>/availableMembers
	 */
    @GET
    @Path("{groupID}/availableMembers")
    @Produces(JSON_UTF8)
    @ApiOperation(value = "get users which are not member in this group.")
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "Group not found"),
            @ApiResponse(code = 500, message = "Server broken, please contact adminstrator")})
    @PermitAll
    List<Group> getAvailableMembers(
            @ApiParam(hidden = true) @Auth Session session,
            @PathParam("groupID") long groupID
    );

    /*
     * GET groups/getByType/{type}
     */
    @GET
    @Path("groups/getByType/{type}")
    @Produces(JSON_UTF8)
    @ApiOperation(value = "get groups from a type")
        @ApiResponses(value = {
        @ApiResponse(code = 500, message = "Server broken, please contact administrator")
    })
    @PermitAll
    List<Group> getByType(
            @ApiParam(hidden = true) @Auth Session session,
            @PathParam("type") String type
    );

    /*
     * GET groups/getall
     */
    @GET
    @Path("getall")
    @Produces(JSON_UTF8)
    @ApiOperation(value = "get all groups")
    @ApiResponses(value = {
            // TODO so oder anders? @ApiResponse(code = 404, message = "At least one group was not found"),
            @ApiResponse(code = 500, message = "Server broken, please contact administrator")
    })
    @PermitAll
    List<Group> getAll(
            @ApiParam(hidden = true) @Auth Session session
    );

    /*
     * GET groups/search/{search}
     */
    @GET
    @Path("search/{search}")
    @Produces(JSON_UTF8)
    @ApiOperation(value = "Search for group by uid and name.")
    @ApiResponses(value = {
            // TODO so oder anders? @ApiResponse(code = 404, message = "At least one group was not found"),
            @ApiResponse(code = 500, message = "Server broken, please contact administrator")
    })
    @PermitAll
    List<Group> search(
            @ApiParam(hidden = true) @Auth Session session,
            @PathParam("search") String search
    );

    /*
     * POST groups/add { hash }
     */
    @POST
    @Path("add")
    @Produces(JSON_UTF8)
    @ApiOperation(value = "create new group")
    @ApiResponses(value = {
            // TODO so oder anders? @ApiResponse(code = 404, message = "At least one group was not found"),
            @ApiResponse(code = 500, message = "Server broken, please contact administrator")
    })
    @PermitAll
    boolean add(
            @ApiParam(hidden = true) @Auth Session session,
            Group group
    );
    
    /*
     * POST groups/modify { hash }
     */
    @POST
    @Path("modify")
    @Produces(JSON_UTF8)
    @ApiOperation(value = "modify an existing group")
    @ApiResponses(value = {
            // TODO so oder anders? @ApiResponse(code = 404, message = "At least one group was not found"),
            @ApiResponse(code = 500, message = "Server broken, please contact administrator")
    })
    @PermitAll
    boolean modify(
            @ApiParam(hidden = true) @Auth Session session,
            Group group
    );
    
    /*
     * GET groups/<groupID>/delete
     */
    @GET
    @Path("{groupID}/delete")
    @Produces(JSON_UTF8)
    @ApiOperation(value = "delete group by id")
    @ApiResponses(value = {
        @ApiResponse(code = 404, message = "Group not found"),
        @ApiResponse(code = 500, message = "Server broken, please contact adminstrator")})
    @PermitAll
    boolean delete(
            @ApiParam(hidden = true) @Auth Session session,
            @PathParam("groupID") long groupID
    );

}
