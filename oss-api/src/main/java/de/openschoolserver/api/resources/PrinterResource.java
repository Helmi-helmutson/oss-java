/* (c) 2018 Péter Varkoly <peter@varkoly.de> - all rights reserved */
package de.openschoolserver.api.resources;

import static de.openschoolserver.api.resources.Resource.JSON_UTF8;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.POST;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import io.swagger.annotations.Api;
import io.dropwizard.auth.Auth;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import de.openschoolserver.dao.OssResponse;
import de.openschoolserver.dao.Printer;
import de.openschoolserver.dao.PrintersOfManufacturer;
import de.openschoolserver.dao.Session;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

@Path("printers")
@Api(value = "printers")
public interface PrinterResource {
	
	
	@POST
	@Path("add")
	@Produces(JSON_UTF8)
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@ApiOperation(value = "Creates a new printer.")
	@ApiResponses(value = {
			@ApiResponse(code = 500, message = "Server broken, please contact adminstrator")})
	@RolesAllowed("printers.add")
	OssResponse addPrinter(
			@ApiParam(hidden = true) @Auth Session session,
			@FormDataParam("name")          String  name,
			@FormDataParam("mac")      		String  mac,
			@FormDataParam("roomId")   		Long    roomId,
			@FormDataParam("model")   		String  model,
			@FormDataParam("windowsDriver") boolean windowsDriver,
            @FormDataParam("file") final InputStream fileInputStream,
            @FormDataParam("file") final FormDataContentDisposition contentDispositionHeader
			);

	@POST
	@Path("{printerId}/setDriver")
	@Produces(JSON_UTF8)
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@ApiOperation(value = "Creates a new printer.")
	@ApiResponses(value = {
			@ApiResponse(code = 500, message = "Server broken, please contact adminstrator")})
	@RolesAllowed("printers.add")
	OssResponse setDriver(
			@ApiParam(hidden = true) @Auth Session session,
			@PathParam("printerId")	Long printerId,
            @FormDataParam("file") final InputStream fileInputStream,
            @FormDataParam("file") final FormDataContentDisposition contentDispositionHeader
			);
	
	@GET
	@Path("all")
	@Produces(JSON_UTF8)
	@ApiOperation(value = "Gets thes lis of printers.")
	@ApiResponses(value = {
			@ApiResponse(code = 404, message = "No device was found"),
			@ApiResponse(code = 500, message = "Server broken, please contact adminstrator")})
	@RolesAllowed("printers.manage")
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
	@RolesAllowed("printers.add")
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
	@RolesAllowed("printers.manage")
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
	@RolesAllowed("printers.manage")
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
	@RolesAllowed("printers.manage")
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
	@RolesAllowed("printers.add")
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
	@RolesAllowed("printers.add")
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
	@RolesAllowed("printers.manage")
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
	@RolesAllowed("printers.manage")
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
	@RolesAllowed("printers.manage")
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
	@RolesAllowed("printers.add")
	OssResponse activateWindowsDriver(
			@ApiParam(hidden = true) @Auth Session session,
			@PathParam("printerName")		String printerName
			);
	
	/*
	 *
	 */
	@GET
	@Path("availableDrivers")
	@Produces(JSON_UTF8)
	@ApiOperation(value = "Get the list of the available drivers sorted by printer manufacturer",
			notes = "The result is a hashmap of arrays:"
			+ "{<br>"
			+ "&nbsp;&nbsp;&nbsp; manufacturer1: [ Model1, Model2, Model3 ],<br>"
			+ "&nbsp;&nbsp;&nbsp; manufacturer2: [ Model4, Model5, Model6 ]<br>"
			+ "}<br>"
			+ "The selected model ")
	@ApiResponses(value = {
			@ApiResponse(code = 404, message = "No device was found"),
			@ApiResponse(code = 405, message = "Device is not a Printer."),
			@ApiResponse(code = 500, message = "Server broken, please contact adminstrator")})
	@RolesAllowed("printers.manage")
	Map<String,String[]> getAvailableDrivers(
			@ApiParam(hidden = true) @Auth Session session
			);
	
	/*
	 *
	 */
	@GET
	@Path("getDrivers")
	@Produces(JSON_UTF8)
	@ApiOperation(value = "Get the list of the available drivers sorted by printer manufacturer",
			notes = "The result is a hashmap of arrays:"
			+ "{<br>"
			+ "&nbsp;&nbsp;&nbsp; manufacturer1: [ Model1, Model2, Model3 ],<br>"
			+ "&nbsp;&nbsp;&nbsp; manufacturer2: [ Model4, Model5, Model6 ]<br>"
			+ "}<br>"
			+ "The selected model ")
	@ApiResponses(value = {
			@ApiResponse(code = 404, message = "No device was found"),
			@ApiResponse(code = 405, message = "Device is not a Printer."),
			@ApiResponse(code = 500, message = "Server broken, please contact adminstrator")})
	@RolesAllowed("printers.manage")
	List<PrintersOfManufacturer> getDrivers(
			@ApiParam(hidden = true) @Auth Session session
			);

}
