package de.openschoolserver.api.resources;

import static de.openschoolserver.api.resources.Resource.JSON_UTF8;

import java.util.List;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import de.openschoolserver.dao.OssResponse;
import de.openschoolserver.dao.Session;
import de.openschoolserver.dao.User;
import io.dropwizard.auth.Auth;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Path("selfmanagement")
@Api(value = "selfmanagement")
public interface SelfManagementResource {


	/*
	 * GET selfmanagement/me
	 */
    @GET
    @Path("me")
    @Produces(JSON_UTF8)
    @ApiOperation(value = "Get my own datas")
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "User not found"),
            @ApiResponse(code = 500, message = "Server broken, please contact adminstrator")})
    @RolesAllowed("myself.search")
    User getBySession(
            @ApiParam(hidden = true) @Auth Session session
    );

    /*
     * POST users/modify { hash }
     */
    @POST
    @Path("modify")
    @Produces(JSON_UTF8)
    @ApiOperation(value = "Modify my own datas")
    @ApiResponses(value = {
            // TODO so oder anders? @ApiResponse(code = 404, message = "At least one user was not found"),
            @ApiResponse(code = 500, message = "Server broken, please contact administrator")
    })
    @PermitAll
    OssResponse modifyMySelf(
            @ApiParam(hidden = true) @Auth Session session,
            User user
    );

    /*
     * VPN Management
     */

    /**
     * Checks if a user is allowed to use vpn connection to the school
     * @param session
     * @return true/false
     */
    @GET
    @Path("vpn/have")
    @Produces(JSON_UTF8)
    @ApiOperation(value = "Checks if a user is allowed to use vpn connection to the school")
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "User not found"),
            @ApiResponse(code = 500, message = "Server broken, please contact adminstrator")})
    @RolesAllowed("myself.search")
    Boolean haveVpn(
            @ApiParam(hidden = true) @Auth Session session
    );


    /**
     * Delivers the list of supported clients OS for the VPN.
     * @param session
     * @return List of the supported OS
     */
    @GET
    @Path("vpn/OS")
    @Produces(JSON_UTF8)
    @ApiOperation(value = "Delivers the list of supported clients OS for the VPN.")
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "User not found"),
            @ApiResponse(code = 500, message = "Server broken, please contact adminstrator")})
    @RolesAllowed("myself.search")
    String[] vpnOS(
            @ApiParam(hidden = true) @Auth Session session
    );

    /**
     * Delivers the configuration for a given operating system.
     * @param OS The operating system: Win, Mac or Linux
     * @return The configuration as an installer or tar archive.
     */
    @GET
    @Path("vpn/config/{OS}")
    @Produces("*/*")
    @ApiOperation(value = "Delivers the configuration for a given operating system.",
	notes = "OS The operating system: the list of the supported os will be delivered by GET selfmanagement/vpn/OS")
    @ApiResponses(value = {
	@ApiResponse(code = 401, message = "You are not allowed to use VPN."),
            @ApiResponse(code = 404, message = "User not found"),
            @ApiResponse(code = 500, message = "Server broken, please contact adminstrator."),
            @ApiResponse(code = 501, message = "Can not create your configuration. Please contact adminstrator.")})
    @RolesAllowed("myself.search")
    Response getConfig(
            @ApiParam(hidden = true) @Auth Session session,
            @PathParam("OS") String OS
    );

    /**
     * Delivers the configuration for a given operating system.
     * @param OS The operating system: Win, Mac or Linux
     * @return The configuration as an installer or tar archive.
     */
    @GET
    @Path("vpn/installer/{OS}")
    @Produces("*/*")
    @ApiOperation(value = "Delivers the installer for a given operating system.",
	notes = "OS The operating system: the list of the supported os will be delivered by GET selfmanagement/vpn/OS")
    @ApiResponses(value = {
	@ApiResponse(code = 401, message = "You are not allowed to use VPN."),
            @ApiResponse(code = 404, message = "User not found"),
            @ApiResponse(code = 500, message = "Server broken, please contact adminstrator")})
    @RolesAllowed("myself.search")
    Response getInstaller(
            @ApiParam(hidden = true) @Auth Session session,
            @PathParam("OS") String OS
    );
}
