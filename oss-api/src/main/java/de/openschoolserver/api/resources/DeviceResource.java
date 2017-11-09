/* (c) 2017 Péter Varkoly <peter@varkoly.de> - all rights reserved */
package de.openschoolserver.api.resources;


import io.dropwizard.auth.Auth;




import io.swagger.annotations.*;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;

import de.openschoolserver.dao.Device;
import de.openschoolserver.dao.Session;
import de.openschoolserver.dao.OssResponse;

import java.util.List;

import static de.openschoolserver.api.resources.Resource.JSON_UTF8;

@Path("devices")
@Api(value = "devices")
public interface DeviceResource {

	/*
	 * GET devices/<deviceId>
	 */
    @GET
    @Path("{deviceId}")
    @Produces(JSON_UTF8)
    @ApiOperation(value = "Get device by id")
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "Device not found"),
            @ApiResponse(code = 500, message = "Server broken, please contact adminstrator")})
    @RolesAllowed("device.manage")
    Device getById(
            @ApiParam(hidden = true) @Auth Session session,
            @PathParam("deviceId") long deviceId
    );

    /*
	 * GET devices/byType/<deviceTyp>
	 */
    @GET
    @Path("byType/{deviceType}")
    @Produces(JSON_UTF8)
    @ApiOperation(value = "Get device by type, this can be printer, mobileDvice, ...")
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "Device not found"),
            @ApiResponse(code = 500, message = "Server broken, please contact adminstrator")})
    @RolesAllowed("device.manage")
    List<Device> getByType(
            @ApiParam(hidden = true) @Auth Session session,
            @PathParam("deviceType") String type
    );
    
    /*
	 * GET devices/byType/<deviceTyp>
	 */
    @GET
    @Path("byHWConf/{hwconfId}")
    @Produces(JSON_UTF8)
    @ApiOperation(value = "Get device by hwconfId.")
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "Device not found"),
            @ApiResponse(code = 500, message = "Server broken, please contact adminstrator")})
    @RolesAllowed("device.manage")
    List<Device> getByHWConf(
            @ApiParam(hidden = true) @Auth Session session,
            @PathParam("hwconfId") Long id
    );
    
    /*
     * GET devices/getAll
     */
    @GET
    @Path("all")
    @Produces(JSON_UTF8)
    @ApiOperation(value = "Get all devices")
    @ApiResponses(value = {
            // TODO so oder anders? @ApiResponse(code = 404, message = "At least one device was not found"),
            @ApiResponse(code = 500, message = "Server broken, please contact administrator")
    })
    @RolesAllowed("device.manage")
    List<Device> getAll(
            @ApiParam(hidden = true) @Auth Session session
    );
    
    /*
     * GET search/{search}
     */
    @GET
    @Path("search/{search}")
    @Produces(JSON_UTF8)
    @ApiOperation(value = "Search for device by name or IP or MAC address by substring.")
    @ApiResponses(value = {
            // TODO so oder anders? @ApiResponse(code = 404, message = "At least one user was not found"),
            @ApiResponse(code = 500, message = "Server broken, please contact administrator")
    })
    @RolesAllowed("device.search")
    List<Device> search(
            @ApiParam(hidden = true) @Auth Session session,
            @PathParam("search") String search
    );
    
    /*
   	 * POST devices/getDevices
   	 */
       @POST
       @Path("getDevices")
       @Produces(JSON_UTF8)
       @ApiOperation(value = "Gets a list of device objects to the list of deviceIds.")
       @ApiResponses(value = {
               @ApiResponse(code = 500, message = "Server broken, please contact adminstrator")})
       @PermitAll
       List<Device> getDevices(
               @ApiParam(hidden = true) @Auth Session session,
               List<Long> deviceIds
       );
    
    /*
     * GET devices/byIP/<IPAddress>
     */
    @GET
    @Path("byIP/{IP}")
    @Produces(JSON_UTF8)
    @ApiOperation(value = "Get device by MAC address")
        @ApiResponses(value = {
        @ApiResponse(code = 500, message = "Server broken, please contact administrator")
    })
    @RolesAllowed("device.search")
    Device getByIP(
            @ApiParam(hidden = true) @Auth Session session,
            @PathParam("IP") String IP
    );

    /*
     * GET devices/byMAC/<MACAddress>
     */
    @GET
    @Path("byMAC/{MAC}")
    @Produces(JSON_UTF8)
    @ApiOperation(value = "Get device by MAC address")
        @ApiResponses(value = {
        @ApiResponse(code = 500, message = "Server broken, please contact administrator")
    })
    @RolesAllowed("device.search")
    Device getByMAC(
            @ApiParam(hidden = true) @Auth Session session,
            @PathParam("MAC") String MAC
    );

    /*
     * GET devices/byName/<Name>
     */
    @GET
    @Path("byName/{Name}")
    @Produces(JSON_UTF8)
    @ApiOperation(value = "Get device by Name")
        @ApiResponses(value = {
        @ApiResponse(code = 500, message = "Server broken, please contact administrator")
    })
    @RolesAllowed("device.search")
    Device getByName(
            @ApiParam(hidden = true) @Auth Session session,
            @PathParam("Name") String Name
    );

    /*
     * GET devices/{deviceId}/defaultPrinter
     */
    @GET
    @Path("{deviceId}/defaultPrinter")
    @Produces(JSON_UTF8)
    @ApiOperation(value = "Get default printer Name")
        @ApiResponses(value = {
        @ApiResponse(code = 500, message = "Server broken, please contact administrator")
    })
    @PermitAll
    String getDefaultPrinter(
            @ApiParam(hidden = true) @Auth Session session,
            @PathParam("deviceId") long deviceId
    );
    
    /*
     * PUT devices/{deviceId}/defaultPrinter/{defaultPrinterId}
     */
    @PUT
    @Path("{deviceId}/defaultPrinter/{defaultPrinterId}")
    @Produces(JSON_UTF8)
    @ApiOperation(value = "Set default printer Name")
        @ApiResponses(value = {
        @ApiResponse(code = 500, message = "Server broken, please contact administrator")
    })
    @RolesAllowed("device.manage")
    OssResponse setDefaultPrinter(
            @ApiParam(hidden = true) @Auth Session session,
            @PathParam("deviceId") long deviceId,
            @PathParam("defaulPrinterId") long defaultPrinterId
    );
    
    /*
     * GET devices/{deviceId}/availablePrinters
     */
    @GET
    @Path("{deviceId}/availablePrinters")
    @Produces(JSON_UTF8)
    @ApiOperation(value = "Get the list of name of the available printers")
        @ApiResponses(value = {
        @ApiResponse(code = 500, message = "Server broken, please contact administrator")
    })
    @PermitAll
    List<String> getAvailablePrinters(
            @ApiParam(hidden = true) @Auth Session session,
            @PathParam("deviceId") long deviceId
    );
    
    /*
     * PUT devices/{deviceId}/availablePrinters
     */
    @PUT
    @Path("{deviceId}/availablePrinters")
    @Produces(JSON_UTF8)
    @ApiOperation(value = "Set the list of name of the available printers")
        @ApiResponses(value = {
        @ApiResponse(code = 500, message = "Server broken, please contact administrator")
    })
    @RolesAllowed("device.manage")
    OssResponse setAvailablePrinters(
            @ApiParam(hidden = true) @Auth Session session,
            @PathParam("deviceId") long deviceId,
            List<Long> availablePrinters
    );
    
    /*
     * GET devices/loggedInUsers/{IP-Address}
     */
    @GET
    @Path("loggedInUsers/{IP}")
    @Produces(JSON_UTF8)
    @ApiOperation(value = "Get the logged on users on a device defined by IP.")
        @ApiResponses(value = {
        @ApiResponse(code = 500, message = "Server broken, please contact administrator")
    })
    @RolesAllowed("room.manage")
    List<String> getLoggedInUsers(
            @ApiParam(hidden = true) @Auth Session session,
            @PathParam("IP") String IP
    );
    
    /*
     * PUT devices/loggedInUsers/{IP-Address}/{userName}
     */
    @PUT
    @Path("loggedInUsers/{IP}/{userName}")
    @Produces(JSON_UTF8)
    @ApiOperation(value = "Set the logged on users on a device defined by IP.")
        @ApiResponses(value = {
        @ApiResponse(code = 500, message = "Server broken, please contact administrator")
    })
    @RolesAllowed("device.manage")
    OssResponse addLoggedInUser(
            @ApiParam(hidden = true) @Auth Session session,
            @PathParam("IP") String IP,
            @PathParam("userName") String userName
    );
    
    /*
     * DELETE devices/loggedInUsers/{IP-Address}/{userName}
     */
    @DELETE
    @Path("loggedInUsers/{IP}/{userName}")
    @Produces(JSON_UTF8)
    @ApiOperation(value = "Get the logged on users on a device defined by IP.")
        @ApiResponses(value = {
        @ApiResponse(code = 500, message = "Server broken, please contact administrator")
    })
    @RolesAllowed("device.manage")
    OssResponse removeLoggedInUser(
            @ApiParam(hidden = true) @Auth Session session,
            @PathParam("IP") String IP,
            @PathParam("userName") String userName
    );
    
    /*
     * GET devices/{deviceId}/loggedInUsers
     */
    @GET
    @Path("{deviceId}/loggedInUsers")
    @Produces(JSON_UTF8)
    @ApiOperation(value = "Get the logged on users on a device defined by the deviceId.")
        @ApiResponses(value = {
        @ApiResponse(code = 500, message = "Server broken, please contact administrator")
    })
    @RolesAllowed("device.manage")
    List<String> getLoggedInUsers(
            @ApiParam(hidden = true) @Auth Session session,
            @PathParam("deviceId") long deviceId
    );
    
    /*
     * GET devices/refreshConfig
     */
    @PUT
    @Path("refreshConfig")
    @Produces(JSON_UTF8)
    @ApiOperation(value = "Refresh the DHCP DNS and SALT Configuration.")
    @ApiResponses(value = {
        @ApiResponse(code = 500, message = "Server broken, please contact administrator")
    })
    @RolesAllowed("device.add")
    void refreshConfig(
            @ApiParam(hidden = true) @Auth Session session
    );
    
    /*
     * PUSH devices/modify
     */
    @POST
    @Path("modify")
    @Produces(JSON_UTF8)
    @ApiOperation(value = "Modify the configuration of one device.")
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Server broken, please contact administrator")
    })
    @RolesAllowed("device.modify")
    OssResponse modify(
    		@ApiParam(hidden = true) @Auth Session session,
            Device device
    );
    
    /*
     * DELETE 
     */
    @DELETE
    @Path("{deviceId}")
    @Produces(JSON_UTF8)
    @ApiOperation(value = "Removes a device.")
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Server broken, please contact administrator")
    })
    @RolesAllowed("device.modify")
    OssResponse delete(
    		@ApiParam(hidden = true) @Auth Session session,
    		@PathParam("deviceId") long deviceId
    );
    
    
}
