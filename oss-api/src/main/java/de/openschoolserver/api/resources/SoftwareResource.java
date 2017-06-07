/* (c) 2017 Peter Varkoly <peter@varkoly.de> - all rights reserved */
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
import de.openschoolserver.dao.Software;
import de.openschoolserver.dao.Response;
import de.openschoolserver.dao.Session;

import java.util.List;

@Path("softwares")
@Api(value = "softwares")
public interface SoftwareResource {
	/*
	 * Get softwares/all
	 */
	@GET
	@Path("all")
	@Produces(JSON_UTF8)
	@ApiOperation(value = "Gets all Softwares.")
	@ApiResponses(value = {
			@ApiResponse(code = 404, message = "No category was found"),
			@ApiResponse(code = 500, message = "Server broken, please contact adminstrator")})
	@PermitAll
	List<Software> getAll(
			@ApiParam(hidden = true) @Auth Session session
			);

	/*
	 * GET softwares/<softwareId>
	 */
	@GET
	@Path("{softwareId}")
	@Produces(JSON_UTF8)
	@ApiOperation(value = "Get software by id")
	@ApiResponses(value = {
			@ApiResponse(code = 404, message = "Software not found"),
			@ApiResponse(code = 500, message = "Server broken, please contact adminstrator")})
	@RolesAllowed("software.search")
	Software getById(
			@ApiParam(hidden = true) @Auth Session session,
			@PathParam("softwareId") long softwareId
			);
	
	/*
	 * GET softwares/search/{search}
	 */
	@GET
	@Path("search/{search}")
	@Produces(JSON_UTF8)
	@ApiOperation(value = "Search for software by name and description.")
	@ApiResponses(value = {
			// TODO so oder anders? @ApiResponse(code = 404, message = "At least one user was not found"),
			@ApiResponse(code = 500, message = "Server broken, please contact administrator")
	})
	//@PermitAll
	@RolesAllowed("software.search")
	List<Software> search(
			@ApiParam(hidden = true) @Auth Session session,
			@PathParam("search") String search
			);

	/*
	 * POST softwares/add { hash }
	 */
	@POST
	@Path("add")
	@Produces(JSON_UTF8)
	@ApiOperation(value = "Create new software")
	@ApiResponses(value = {
			// TODO so oder anders? @ApiResponse(code = 404, message = "At least one user was not found"),
			@ApiResponse(code = 500, message = "Server broken, please contact administrator")
	})
	//@PermitAll
	@RolesAllowed("software.add")
	Response add(
			@ApiParam(hidden = true) @Auth Session session,
			Software Software
			);
	
	/*
	 * POST softwares/modify { hash }
	 */
	@POST
	@Path("{softwareId}")
	@Produces(JSON_UTF8)
	@ApiOperation(value = "Modify a software")
	@ApiResponses(value = {
			// TODO so oder anders? @ApiResponse(code = 404, message = "At least one user was not found"),
			@ApiResponse(code = 500, message = "Server broken, please contact administrator")
	})
	//@PermitAll
	@RolesAllowed("software.modify")
	Response modify(
			@ApiParam(hidden = true) @Auth Session session,
			Software software
			);
	
	/*
	 * DELETE softwares/<softwareId>
	 */
	@DELETE
	@Path("{softwareId}")
	@Produces(JSON_UTF8)
	@ApiOperation(value = "Delets a software defined by id.")
	@ApiResponses(value = {
			@ApiResponse(code = 404, message = "Software not found"),
			@ApiResponse(code = 500, message = "Server broken, please contact adminstrator")})
	@RolesAllowed("software.delete")
	Response delete(
			@ApiParam(hidden = true) @Auth Session session,
			@PathParam("softwareId") long softwareId
			);
	
	/*
	 * PUT 
	 */
	@PUT
	@Path("saveState")
	@Produces(JSON_UTF8)
	@ApiOperation(value = "Save the actuall software configuration.")
	@ApiResponses(value = {
			@ApiResponse(code = 404, message = "Software not found"),
			@ApiResponse(code = 500, message = "Server broken, please contact adminstrator")})
	@RolesAllowed("software.modify")
	Response saveState(
			@ApiParam(hidden = true) @Auth Session session
			);
	
}
