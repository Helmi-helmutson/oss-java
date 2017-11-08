/* (c) 2017 Péter Varkoly <peter@varkoly.de> - all rights reserved */
package de.openschoolserver.api.resources;


import io.dropwizard.auth.Auth;


import io.swagger.annotations.*;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;

import de.openschoolserver.dao.User;
import de.openschoolserver.dao.Group;
import de.openschoolserver.dao.Session;
import de.openschoolserver.dao.OssResponse;

import java.util.List;

import static de.openschoolserver.api.resources.Resource.JSON_UTF8;

@Path("groups")
@Api(value = "groups")
public interface GroupResource {

	/*
	 * GET groups/<groupId>
	 */
    @GET
    @Path("{groupId}")
    @Produces(JSON_UTF8)
    @ApiOperation(value = "Get group by id")
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "Group not found"),
            @ApiResponse(code = 500, message = "Server broken, please contact adminstrator")})
    @RolesAllowed("group.search")
    Group getById(
            @ApiParam(hidden = true) @Auth Session session,
            @PathParam("groupId") long groupId
    );

    /*
   	 * GET groups/<groupId>/members
   	 */
       @GET
       @Path("{groupId}/members")
       @Produces(JSON_UTF8)
       @ApiOperation(value = "Get users which are member in this group.")
       @ApiResponses(value = {
               @ApiResponse(code = 404, message = "Group not found"),
               @ApiResponse(code = 500, message = "Server broken, please contact adminstrator")})
       @RolesAllowed("group.manage")
       List<User> getMembers(
               @ApiParam(hidden = true) @Auth Session session,
               @PathParam("groupId") long groupId
       );

    /*
	* GET groups/<groupId>/availableMembers
	*/
    @GET
    @Path("{groupId}/availableMembers")
    @Produces(JSON_UTF8)
    @ApiOperation(value = "Get users which are not member in this group.")
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "Group not found"),
            @ApiResponse(code = 500, message = "Server broken, please contact adminstrator")})
    @RolesAllowed("group.manage")
    List<User> getAvailableMembers(
            @ApiParam(hidden = true) @Auth Session session,
            @PathParam("groupId") long groupId
    );

    /*
     * GET groups/byType/{type}
     */
    @GET
    @Path("byType/{type}")
    @Produces(JSON_UTF8)
    @ApiOperation(value = "Get groups from a type")
        @ApiResponses(value = {
        @ApiResponse(code = 500, message = "Server broken, please contact administrator")
    })
    @RolesAllowed("group.search")
    List<Group> getByType(
            @ApiParam(hidden = true) @Auth Session session,
            @PathParam("type") String type
    );

    /*
     * GET groups/all
     */
    @GET
    @Path("all")
    @Produces(JSON_UTF8)
    @ApiOperation(value = "Get all groups")
    @ApiResponses(value = {
            // TODO so oder anders? @ApiResponse(code = 404, message = "At least one group was not found"),
            @ApiResponse(code = 500, message = "Server broken, please contact administrator")
    })
    @RolesAllowed("group.search")
    List<Group> getAll(
            @ApiParam(hidden = true) @Auth Session session
    );

    /*
     * GET groups/search/{search}
     */
    @GET
    @Path("search/{search}")
    @Produces(JSON_UTF8)
    @ApiOperation(value = "Search for group by name and description.")
    @ApiResponses(value = {
            // TODO so oder anders? @ApiResponse(code = 404, message = "At least one group was not found"),
            @ApiResponse(code = 500, message = "Server broken, please contact administrator")
    })
    @RolesAllowed("group.search")
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
    @ApiOperation(value = "Create new group")
    @ApiResponses(value = {
            // TODO so oder anders? @ApiResponse(code = 404, message = "At least one group was not found"),
            @ApiResponse(code = 500, message = "Server broken, please contact administrator")
    })
    @RolesAllowed("group.add")
    OssResponse add(
            @ApiParam(hidden = true) @Auth Session session,
            Group group
    );
    
    /*
     * POST groups/modify { hash }
     */
    @POST
    @Path("modify")
    @Produces(JSON_UTF8)
    @ApiOperation(value = "Modify an existing group")
    @ApiResponses(value = {
            // TODO so oder anders? @ApiResponse(code = 404, message = "At least one group was not found"),
            @ApiResponse(code = 500, message = "Server broken, please contact administrator")
    })
    @RolesAllowed("group.modify")
    OssResponse modify(
            @ApiParam(hidden = true) @Auth Session session,
            Group group
    );
    
    /*
   	 * POST groups/getGroups
   	 */
       @POST
       @Path("getGroups")
       @Produces(JSON_UTF8)
       @ApiOperation(value = "Gets a list of group objects to the list of groupIds.")
       @ApiResponses(value = {
               @ApiResponse(code = 404, message = "Group not found"),
               @ApiResponse(code = 500, message = "Server broken, please contact adminstrator")})
       @RolesAllowed("group.manage")
       List<Group> getGroups(
               @ApiParam(hidden = true) @Auth Session session,
               List<Long> groupIds
       );
        
    /*
     * DELETE groups/<groupId>
     */
    @DELETE
    @Path("{groupId}")
    @Produces(JSON_UTF8)
    @ApiOperation(value = "Deletes group by id")
    @ApiResponses(value = {
        @ApiResponse(code = 404, message = "Group not found"),
        @ApiResponse(code = 500, message = "Server broken, please contact adminstrator")})
    @RolesAllowed("group.delete")
    OssResponse delete(
            @ApiParam(hidden = true) @Auth Session session,
            @PathParam("groupId") long groupId
    );
    
    //Manipulation of memebers
    
    /*
   	 * POST groups/<groupId>/members
   	 */
       @POST
       @Path("{groupId}/members")
       @Produces(JSON_UTF8)
       @ApiOperation(value = "Sets the member of this group.")
       @ApiResponses(value = {
               @ApiResponse(code = 404, message = "Group not found"),
               @ApiResponse(code = 500, message = "Server broken, please contact adminstrator")})
       @RolesAllowed("group.manage")
       OssResponse setMembers(
               @ApiParam(hidden = true) @Auth Session session,
               @PathParam("groupId") long groupId,
               List<Long> users
       );
       
       /*
        * 
        */
 
       /*
        * DELETE groups/<groupId>/<userId>
        */
       @DELETE
       @Path("{groupId}/{userId}")
       @Produces(JSON_UTF8)
       @ApiOperation(value = "Deletes a member of a group by userId.")
       @ApiResponses(value = {
           @ApiResponse(code = 500, message = "Server broken, please contact adminstrator")})
       @RolesAllowed("group.search")
       OssResponse removeMember(
               @ApiParam(hidden = true) @Auth Session session,
               @PathParam("groupId") long groupId,
               @PathParam("userId") long userId
       );
       
       /*
        * PUT groups/<groupId>/<userId>
        */
       @PUT
       @Path("{groupId}/{userId}")
       @Produces(JSON_UTF8)
       @ApiOperation(value = "Add a member to a group by userId.")
       @ApiResponses(value = {
           @ApiResponse(code = 404, message = "Group not found"),
           @ApiResponse(code = 500, message = "Server broken, please contact adminstrator")})
       @RolesAllowed("group.manage")
       OssResponse addMember(
               @ApiParam(hidden = true) @Auth Session session,
               @PathParam("groupId") long groupId,
               @PathParam("userId") long userId
       );


}
