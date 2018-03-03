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

import io.swagger.annotations.Api;
import io.dropwizard.auth.Auth;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import de.openschoolserver.dao.OssResponse;
import de.openschoolserver.dao.Room;
import de.openschoolserver.dao.Printer;
import de.openschoolserver.dao.Session;

import java.util.List;

@Path("printers")
@Api(value = "printers")
public interface PrinterResource {
	
	/*
	 * Get adhoclan/users
	 */
	@GET
	@Path("all")
	@Produces(JSON_UTF8)
	@ApiOperation(value = "Gets thes lis of printers.")
	@ApiResponses(value = {
			@ApiResponse(code = 404, message = "No device was found"),
			@ApiResponse(code = 500, message = "Server broken, please contact adminstrator")})
	@RolesAllowed("adhoclan.search")
	List<Printer> getPrinters(
			@ApiParam(hidden = true) @Auth Session session
			);

	@DELETE
	@Path("{printerId}")
	@Produces(JSON_UTF8)
	@ApiOperation(value = "Deletes a printer")
	@ApiResponses(value = {
			@ApiResponse(code = 404, message = "No device was found"),
			@ApiResponse(code = 405, message = "Device is not a Printer."),
			@ApiResponse(code = 500, message = "Server broken, please contact adminstrator")})
	@RolesAllowed("adhoclan.search")
	OssResponse deletePrinter(
			@ApiParam(hidden = true) @Auth Session session,
			@PathParam("printerId")		Long printerId
			);
	
	@PUT
	@Path("{printerId}/reset")
	@Produces(JSON_UTF8)
	@ApiOperation(value = "Resets a printer")
	@ApiResponses(value = {
			@ApiResponse(code = 404, message = "No device was found"),
			@ApiResponse(code = 405, message = "Device is not a Printer."),
			@ApiResponse(code = 500, message = "Server broken, please contact adminstrator")})
	@RolesAllowed("adhoclan.search")
	OssResponse resetPrinter(
			@ApiParam(hidden = true) @Auth Session session,
			@PathParam("printerId")		Long printerId
			);

	@PUT
	@Path("{printerId}/enable")
	@Produces(JSON_UTF8)
	@ApiOperation(value = "Enable a printer")
	@ApiResponses(value = {
			@ApiResponse(code = 404, message = "No device was found"),
			@ApiResponse(code = 405, message = "Device is not a Printer."),
			@ApiResponse(code = 500, message = "Server broken, please contact adminstrator")})
	@RolesAllowed("adhoclan.search")
	OssResponse enablePrinter(
			@ApiParam(hidden = true) @Auth Session session,
			@PathParam("printerId")		Long printerId
			);

	@PUT
	@Path("{printerId}/disable")
	@Produces(JSON_UTF8)
	@ApiOperation(value = "Disable a printer")
	@ApiResponses(value = {
			@ApiResponse(code = 404, message = "No device was found"),
			@ApiResponse(code = 405, message = "Device is not a Printer."),
			@ApiResponse(code = 500, message = "Server broken, please contact adminstrator")})
	@RolesAllowed("adhoclan.search")
	OssResponse disablePrinter(
			@ApiParam(hidden = true) @Auth Session session,
			@PathParam("printerId")		Long printerId
			);

	@PUT
	@Path("{printerId}/activateWindowsDriver")
	@Produces(JSON_UTF8)
	@ApiOperation(value = "Add a new group or user to a giwen AdHocLan room")
	@ApiResponses(value = {
			@ApiResponse(code = 404, message = "No device was found"),
			@ApiResponse(code = 405, message = "Device is not a Printer."),
			@ApiResponse(code = 500, message = "Server broken, please contact adminstrator")})
	@RolesAllowed("adhoclan.search")
	OssResponse activateWindowsDriver(
			@ApiParam(hidden = true) @Auth Session session,
			@PathParam("printerId")		Long printerId
			);

	@DELETE
	@Path("byName/{printerName}")
	@Produces(JSON_UTF8)
	@ApiOperation(value = "Deletes a printer")
	@ApiResponses(value = {
			@ApiResponse(code = 404, message = "No device was found"),
			@ApiResponse(code = 405, message = "Device is not a Printer."),
			@ApiResponse(code = 500, message = "Server broken, please contact adminstrator")})
	@RolesAllowed("adhoclan.search")
	OssResponse deletePrinter(
			@ApiParam(hidden = true) @Auth Session session,
			@PathParam("printerName")		String printerName
			);
	
	@PUT
	@Path("byName/{printerName}/reset")
	@Produces(JSON_UTF8)
	@ApiOperation(value = "Resets a printer")
	@ApiResponses(value = {
			@ApiResponse(code = 404, message = "No device was found"),
			@ApiResponse(code = 405, message = "Device is not a Printer."),
			@ApiResponse(code = 500, message = "Server broken, please contact adminstrator")})
	@RolesAllowed("adhoclan.search")
	OssResponse resetPrinter(
			@ApiParam(hidden = true) @Auth Session session,
			@PathParam("printerName")		String printerName
			);

	@PUT
	@Path("byName/{printerName}/enable")
	@Produces(JSON_UTF8)
	@ApiOperation(value = "Enable a printer")
	@ApiResponses(value = {
			@ApiResponse(code = 404, message = "No device was found"),
			@ApiResponse(code = 405, message = "Device is not a Printer."),
			@ApiResponse(code = 500, message = "Server broken, please contact adminstrator")})
	@RolesAllowed("adhoclan.search")
	OssResponse enablePrinter(
			@ApiParam(hidden = true) @Auth Session session,
			@PathParam("printerName")		String printerName
			);

	@PUT
	@Path("byName/{printerName}/disable")
	@Produces(JSON_UTF8)
	@ApiOperation(value = "Disable a printer")
	@ApiResponses(value = {
			@ApiResponse(code = 404, message = "No device was found"),
			@ApiResponse(code = 405, message = "Device is not a Printer."),
			@ApiResponse(code = 500, message = "Server broken, please contact adminstrator")})
	@RolesAllowed("adhoclan.search")
	OssResponse disablePrinter(
			@ApiParam(hidden = true) @Auth Session session,
			@PathParam("printerName")		String printerName
			);

	@PUT
	@Path("byName/{printerName}/activateWindowsDriver")
	@Produces(JSON_UTF8)
	@ApiOperation(value = "Add a new group or user to a giwen AdHocLan room")
	@ApiResponses(value = {
			@ApiResponse(code = 404, message = "No device was found"),
			@ApiResponse(code = 405, message = "Device is not a Printer."),
			@ApiResponse(code = 500, message = "Server broken, please contact adminstrator")})
	@RolesAllowed("adhoclan.search")
	OssResponse activateWindowsDriver(
			@ApiParam(hidden = true) @Auth Session session,
			@PathParam("printerName")		String printerName
			);

}
