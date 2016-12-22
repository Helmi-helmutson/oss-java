package de.openschoolserver.api.resources;


import io.dropwizard.auth.Auth;
import io.swagger.annotations.*;

import javax.annotation.security.PermitAll;
import javax.ws.rs.*;

import de.openschoolserver.dao.Device;
import de.openschoolserver.dao.Session;

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
    @ApiOperation(value = "get device by id")
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "Device not found"),
            @ApiResponse(code = 500, message = "Server broken, please contact adminstrator")})
    @PermitAll
    Device getById(
            @ApiParam(hidden = true) @Auth Session session,
            @PathParam("deviceId") int deviceId
    );

    /*
	 * GET devices/byType/<deviceTyp>
	 */
    @GET
    @Path("byType/{deviceType}")
    @Produces(JSON_UTF8)
    @ApiOperation(value = "get device by type, this can be printer, mobileDvice, ...")
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "Device not found"),
            @ApiResponse(code = 500, message = "Server broken, please contact adminstrator")})
    @PermitAll
    List<Device> getByType(
            @ApiParam(hidden = true) @Auth Session session,
            @PathParam("deviceType") String type
    );
    
    /*
     * GET devices/getall
     */
    @GET
    @Path("getall")
    @Produces(JSON_UTF8)
    @ApiOperation(value = "get all devices")
    @ApiResponses(value = {
            // TODO so oder anders? @ApiResponse(code = 404, message = "At least one device was not found"),
            @ApiResponse(code = 500, message = "Server broken, please contact administrator")
    })
    @PermitAll
    List<Device> getAll(
            @ApiParam(hidden = true) @Auth Session session
    );

    /*
     * POST devices/add { hash }
     */
    @POST
    @Path("add")
    @Produces(JSON_UTF8)
    @ApiOperation(value = "create new device")
    @ApiResponses(value = {
            // TODO so oder anders? @ApiResponse(code = 404, message = "At least one device was not found"),
            @ApiResponse(code = 500, message = "Server broken, please contact administrator")
    })
    @PermitAll
    boolean add(
            @ApiParam(hidden = true) @Auth Session session,
            Device device
    );
    
    /*
     * DELETE devices/<deviceId>
     */
    @DELETE
    @Path("{deviceId}")
    @Produces(JSON_UTF8)
    @ApiOperation(value = "delete device by id")
    @ApiResponses(value = {
        @ApiResponse(code = 404, message = "Device not found"),
        @ApiResponse(code = 500, message = "Server broken, please contact adminstrator")})
    @PermitAll
    boolean delete(
            @ApiParam(hidden = true) @Auth Session session,
            @PathParam("deviceId") int deviceId
    );

    
    /*
     * GET devices/getbyIP/<IPAddress>
     */
    @GET
    @Path("getbyIP/{IP}")
    @Produces(JSON_UTF8)
    @ApiOperation(value = "get device by MAC address")
        @ApiResponses(value = {
        @ApiResponse(code = 500, message = "Server broken, please contact administrator")
    })
    @PermitAll
    Device getByIP(
            @ApiParam(hidden = true) @Auth Session session,
            @PathParam("IP") String IP
    );

    /*
     * GET devices/getbyMAC/<MACAddress>
     */
    @GET
    @Path("getbyMAC/{MAC}")
    @Produces(JSON_UTF8)
    @ApiOperation(value = "get device by MAC address")
        @ApiResponses(value = {
        @ApiResponse(code = 500, message = "Server broken, please contact administrator")
    })
    @PermitAll
    Device getByMAC(
            @ApiParam(hidden = true) @Auth Session session,
            @PathParam("MAC") String MAC
    );

    /*
     * GET devices/getbyName/<Name>
     */
    @GET
    @Path("getbyName/{Name}")
    @Produces(JSON_UTF8)
    @ApiOperation(value = "get device by Name")
        @ApiResponses(value = {
        @ApiResponse(code = 500, message = "Server broken, please contact administrator")
    })
    @PermitAll
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
    @ApiOperation(value = "get default printer Name")
        @ApiResponses(value = {
        @ApiResponse(code = 500, message = "Server broken, please contact administrator")
    })
    @PermitAll
    String getDefaultPrinter(
            @ApiParam(hidden = true) @Auth Session session,
            @PathParam("deviceId") int deviceId
    );
    
    /*
     * GET devices/{deviceId}/availablePrinters
     */
    @GET
    @Path("{deviceId}/availablePrinters")
    @Produces(JSON_UTF8)
    @ApiOperation(value = "get the list of name of the available printers")
        @ApiResponses(value = {
        @ApiResponse(code = 500, message = "Server broken, please contact administrator")
    })
    @PermitAll
    List<String> getAvailablePrinters(
            @ApiParam(hidden = true) @Auth Session session,
            @PathParam("deviceId") int deviceId
    );    
}
