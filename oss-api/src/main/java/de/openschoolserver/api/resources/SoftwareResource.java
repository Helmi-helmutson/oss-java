/* (c) 2017 Peter Varkoly <peter@varkoly.de> - all rights reserved */
package de.openschoolserver.api.resources;

import static de.openschoolserver.api.resources.Resource.JSON_UTF8;





import javax.annotation.security.PermitAll;
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

import io.dropwizard.auth.Auth;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import de.openschoolserver.dao.Category;
import de.openschoolserver.dao.Software;
import de.openschoolserver.dao.SoftwareLicense;
import de.openschoolserver.dao.Response;
import de.openschoolserver.dao.Session;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

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
	 * POST softwares/<softwareId> { hash }
	 */
	@POST
	@Path("{softwareId}")
	@Produces(JSON_UTF8)
	@ApiOperation(value = "Modify a software")
	@ApiResponses(value = {
			// TODO so oder anders? @ApiResponse(code = 404, message = "At least one user was not found"),
			@ApiResponse(code = 500, message = "Server broken, please contact administrator")
	})
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
	Response apply(
			@ApiParam(hidden = true) @Auth Session session
			);
	
	
	/*
	 * POST softwares/{softwareId}/license
	 */
	@POST
	@Path("{softwareId}/license")
    @Produces(JSON_UTF8)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @ApiOperation( value = "Creates licences to a software" )
    @ApiResponses(value = {
	            @ApiResponse(code = 500, message = "Server broken, please contact administrator")
	})
    @RolesAllowed("software.modify")
    Response addLicenseToSoftware(
    		@ApiParam(hidden = true) @Auth Session session,
    		@PathParam("softwareId") long softwareId,
    		SoftwareLicense softwareLicense,
            @FormDataParam("file") final InputStream fileInputStream,
            @FormDataParam("file") final FormDataContentDisposition contentDispositionHeader
            );
	
	/*
	 * POST software/installations
	 */
	@POST
	@Path("installations")
	@Produces(JSON_UTF8)
	@ApiOperation( value = "Creates a software installation" )
    @ApiResponses(value = {
	            @ApiResponse(code = 500, message = "Server broken, please contact administrator")
	})
	@RolesAllowed("software.install")
	Response createInstallation(
			@ApiParam(hidden = true) @Auth Session session,
			Category category
			);

	/*
	 * PUT softwares/installations/{installationId}/softwares/{softwareId}
	 */
	@PUT
	@Path("installations/{installationId}/softwares/{softwareId}")
	@Produces(JSON_UTF8)
	@ApiOperation(value = "Adds a software to an installation.")
	@ApiResponses(value = {
			@ApiResponse(code = 404, message = "Software not found"),
			@ApiResponse(code = 500, message = "Server broken, please contact adminstrator")})
	@RolesAllowed("software.install")
	Response addSoftwareToInstalation(
			@ApiParam(hidden = true) @Auth Session session,
			@PathParam("installationId") long installationId,
			@PathParam("softwareId")     long softwareId
			);
	/*
	 * PUT softwares/installations/{installationId}/devices/{deviceId}
	 */
	@PUT
	@Path("installations/{installationId}/devices/{deviceId}")
	@Produces(JSON_UTF8)
	@ApiOperation(value = "Adds a device to an installation.")
	@ApiResponses(value = {
			@ApiResponse(code = 404, message = "Software not found"),
			@ApiResponse(code = 500, message = "Server broken, please contact adminstrator")})
	@RolesAllowed("software.install")
	Response addDeviceToInstalation(
			@ApiParam(hidden = true) @Auth Session session,
			@PathParam("installationId") long installationId,
			@PathParam("deviceId")       long deviceId
			);

	/*
	 * PUT softwares/installations/{installationId}/rooms/{roomId}
	 */
	@PUT
	@Path("installations/{installationId}/rooms/{roomId}")
	@Produces(JSON_UTF8)
	@ApiOperation(value = "Adds a room to an installation.")
	@ApiResponses(value = {
			@ApiResponse(code = 404, message = "Software not found"),
			@ApiResponse(code = 500, message = "Server broken, please contact adminstrator")})
	@RolesAllowed("software.install")
	Response addRoomToInstalation(
			@ApiParam(hidden = true) @Auth Session session,
			@PathParam("installationId") long installationId,
			@PathParam("roomId")         long roomId
			);

	/*
	 * PUT softwares/installations/{installationId}/hwconfs/{deviceId}
	 */
	@PUT
	@Path("installations/{installationId}/hwconfs/{hwconfId}")
	@Produces(JSON_UTF8)
	@ApiOperation(value = "Adds a hwconf to an installation.")
	@ApiResponses(value = {
			@ApiResponse(code = 404, message = "Software not found"),
			@ApiResponse(code = 500, message = "Server broken, please contact adminstrator")})
	@RolesAllowed("software.install")
	Response addHWConfToInstalation(
			@ApiParam(hidden = true) @Auth Session session,
			@PathParam("installationId") long installationId,
			@PathParam("hwconfId")       long hwconfId
			);
	
	/*
	 * DELETE softwares/installations/{installationId}
	 */
	@DELETE
	@Path("installations/{installationId}")
	@Produces(JSON_UTF8)
	@ApiOperation(value = "Delets a defined installation.")
	@ApiResponses(value = {
			@ApiResponse(code = 404, message = "Software not found"),
			@ApiResponse(code = 500, message = "Server broken, please contact adminstrator")})
	@RolesAllowed("software.install")
	Response deleteInstalation(
			@ApiParam(hidden = true) @Auth Session session,
			@PathParam("installationId") long installationId
			);
	
	/*
	 * DELETE softwares/installations/{installationId}/softwares/{softwareId}
	 */
	@DELETE
	@Path("installations/{installationId}/softwares/{softwareId}")
	@Produces(JSON_UTF8)
	@ApiOperation(value = "Delets a software from an installation.")
	@ApiResponses(value = {
			@ApiResponse(code = 404, message = "Software not found"),
			@ApiResponse(code = 500, message = "Server broken, please contact adminstrator")})
	@RolesAllowed("software.install")
	Response deleteSoftwareFromInstalation(
			@ApiParam(hidden = true) @Auth Session session,
			@PathParam("installationId") long installationId,
			@PathParam("softwareId")     long softwareId
			);


	/*
	 * DELETE softwares/installations/{installationId}/devices/{deviceId}
	 */
	@DELETE
	@Path("installations/{installationId}/devices/{deviceId}")
	@Produces(JSON_UTF8)
	@ApiOperation(value = "Delets a device from an installation.")
	@ApiResponses(value = {
			@ApiResponse(code = 404, message = "Software not found"),
			@ApiResponse(code = 500, message = "Server broken, please contact adminstrator")})
	@RolesAllowed("software.install")
	Response deleteDeviceFromInstalation(
			@ApiParam(hidden = true) @Auth Session session,
			@PathParam("installationId") long installationId,
			@PathParam("deviceId")       long deviceId
			);

	/*
	 * DELETE softwares/installations/{installationId}/rooms/{roomId}
	 */
	@DELETE
	@Path("installations/{installationId}/rooms/{roomId}")
	@Produces(JSON_UTF8)
	@ApiOperation(value = "Delets a room from an installation.")
	@ApiResponses(value = {
			@ApiResponse(code = 404, message = "Software not found"),
			@ApiResponse(code = 500, message = "Server broken, please contact adminstrator")})
	@RolesAllowed("software.install")
	Response deleteRoomFromInstalation(
			@ApiParam(hidden = true) @Auth Session session,
			@PathParam("installationId") long installationId,
			@PathParam("roomId")         long roomId
			);

	/*
	 * DELETE softwares/installations/{installationId}/hwconfs/{deviceId}
	 */
	@DELETE
	@Path("installations/{installationId}/hwconfs/{hwconfId}")
	@Produces(JSON_UTF8)
	@ApiOperation(value = "Deletes a hwconf from an installation.")
	@ApiResponses(value = {
			@ApiResponse(code = 404, message = "Software not found"),
			@ApiResponse(code = 500, message = "Server broken, please contact adminstrator")})
	@RolesAllowed("software.install")
	Response deleteHWConfFromInstalation(
			@ApiParam(hidden = true) @Auth Session session,
			@PathParam("installationId") long installationId,
			@PathParam("hwconfId")       long hwconfId
			);

	/*
	 * GET softwares/installations/{installationId}/softwares
	 */
	@GET
	@Path("installations/{installationId}/softwares")
	@Produces(JSON_UTF8)
	@ApiOperation(value = "Gets the list of software in an installation.")
	@ApiResponses(value = {
			@ApiResponse(code = 404, message = "Software not found"),
			@ApiResponse(code = 500, message = "Server broken, please contact adminstrator")})
	@RolesAllowed("software.install")
	List<Long> getSoftwares(
			@ApiParam(hidden = true) @Auth Session session,
			@PathParam("installationId") long installationId
	);
	
	/*
	 * GET softwares/installations/{installationId}/devices
	 */
	@GET
	@Path("installations/{installationId}/devices")
	@Produces(JSON_UTF8)
	@ApiOperation(value = "Gets the list of devices in an installation.")
	@ApiResponses(value = {
			@ApiResponse(code = 404, message = "Software not found"),
			@ApiResponse(code = 500, message = "Server broken, please contact adminstrator")})
	@RolesAllowed("software.install")
	List<Long> getDevices(
			@ApiParam(hidden = true) @Auth Session session,
			@PathParam("installationId") long installationId
	);
	
	/*
	 * GET softwares/installations/{installationId}/rooms
	 */
	@GET
	@Path("installations/{installationId}/rooms")
	@Produces(JSON_UTF8)
	@ApiOperation(value = "Gets the list of rooms in an installation.")
	@ApiResponses(value = {
			@ApiResponse(code = 404, message = "Software not found"),
			@ApiResponse(code = 500, message = "Server broken, please contact adminstrator")})
	@RolesAllowed("software.install")
	List<Long> getRooms(
			@ApiParam(hidden = true) @Auth Session session,
			@PathParam("installationId") long installationId
	);
	
	/*
	 * GET softwares/installations/{installationId}/hwconfs
	 */
	@GET
	@Path("installations/{installationId}/hwconfs")
	@Produces(JSON_UTF8)
	@ApiOperation(value = "Gets the list of hwconfs in an installation.")
	@ApiResponses(value = {
			@ApiResponse(code = 404, message = "Software not found"),
			@ApiResponse(code = 500, message = "Server broken, please contact adminstrator")})
	@RolesAllowed("software.install")
	List<Long> getHWConfs(
			@ApiParam(hidden = true) @Auth Session session,
			@PathParam("installationId") long installationId
	);
	
	/*
	 * GET softwares/available
	 */
	@GET
	@Path("available")
	@Produces(JSON_UTF8)
	@ApiOperation(value = "Gets the available softwares from the CEPHALIX repository.")
	@ApiResponses(value = {
			@ApiResponse(code = 404, message = "No category was found"),
			@ApiResponse(code = 500, message = "Server broken, please contact adminstrator")})
	@RolesAllowed("software.download")
	List<Map<String,String>> getAvailable(
			@ApiParam(hidden = true) @Auth Session session
			);
	
	/*
	 * POST softwares/download ['afasd','afasds','dsfasdfs']
	 */
	@POST
	@Path("download")
	@Produces(JSON_UTF8)
	@ApiOperation(value = "Downloads softwares from the CEPHALIX repository.",
	notes = "The call must provide a list of softwares to be downloaded:<br>"
			+ "[ \"MSWhatever\", \"AnOtherProgram\" ]<br>"
			+ "The requirements will be solved automaticaly.")
	@ApiResponses(value = {
			@ApiResponse(code = 404, message = "No category was found"),
			@ApiResponse(code = 500, message = "Server broken, please contact adminstrator")})
	@RolesAllowed("software.download")
	Response download(
			@ApiParam(hidden = true) @Auth Session session,
			List<String> softwares
			);
	
	/*
	 * POST softwares/download/{softwareName}
	 */
	@PUT
	@Path("download/{softwareName}")
	@Produces(JSON_UTF8)
	@ApiOperation(value = "Downloads a software from the CEPHALIX repository.")
	@ApiResponses(value = {
			@ApiResponse(code = 404, message = "No category was found"),
			@ApiResponse(code = 500, message = "Server broken, please contact adminstrator")})
	@RolesAllowed("software.download")
	Response downloadOne(
			@ApiParam(hidden = true) @Auth Session session,
			@PathParam("softwareName") String softwareName
			);
	
	/*
	 * DELETE softwares/remove ["MsofficeYASfa","fadsfa","asfa"]
	 */
	@DELETE
	@Path("remove")
	@Produces(JSON_UTF8)
	@ApiOperation(value = "Remove softwares downloaded from the CEPHALIX repository.")
	@ApiResponses(value = {
			@ApiResponse(code = 404, message = "No category was found"),
			@ApiResponse(code = 500, message = "Server broken, please contact adminstrator")})
	@RolesAllowed("software.download")
	Response removeSoftwares(
			@ApiParam(hidden = true) @Auth Session session,
			List<String> softwares
			);
	
	/*
	 * POST softwares/listDownloadedSoftware
	 */
	@POST
	@Path("listDownloadedSoftware")
	@Produces(JSON_UTF8)
	@ApiOperation(value = "List the status of the downloaded software. ",
			notes = "This call delivers a list of maps of the downloaded software.<br>"
			+ "[ {<br>"
			+ "    name : name of the software,<br>"
			+ "    versions : the version of the software,<br>"
			+ "    update : this field contains the version of the available update,<br>"
			+ "    updateDescription : this field contains the desription of the available update,<br>"
			+ "} ]")
	@ApiResponses(value = {
			@ApiResponse(code = 404, message = "No category was found"),
			@ApiResponse(code = 500, message = "Server broken, please contact adminstrator")})
	@RolesAllowed("software.install")
	List<Map<String,String>> listDownloadedSoftware(
			@ApiParam(hidden = true) @Auth Session session
			);
	
	/*
	 * PUT softwares/devicesByName/{deviceName}/{softwareName}/{version}
	 */
	@PUT
	@Path("devicesByName/{deviceName}/{softwareName}/{version}")
	@Produces(JSON_UTF8)
	@ApiOperation(value = "Set a software on a device as installed in a given version.")
	@ApiResponses(value = {
			@ApiResponse(code = 404, message = "No category was found"),
			@ApiResponse(code = 500, message = "Server broken, please contact adminstrator")})
	@RolesAllowed("software.install")
	Response setSoftwareInstalledOnDevice(
			@ApiParam(hidden = true) @Auth Session session,
			@ApiParam(value = "Name of the device",  required = true) @PathParam("deviceName")   String deviceName,
			@ApiParam(value = "Name of the software",required = true) @PathParam("softwareName") String softwareName,
			@ApiParam(value = "Software version",    required = true) @PathParam("version")  String version
			);
	
	/*
	 * PUT softwares/devices/{deviceId}/{softwareName}/{version}
	 */
	@PUT
	@Path("devices/{deviceId}/{softwareName}/{version}")
	@Produces(JSON_UTF8)
	@ApiOperation(value = "Set a software on a device as installed in a given version.")
	@ApiResponses(value = {
			@ApiResponse(code = 404, message = "No category was found"),
			@ApiResponse(code = 500, message = "Server broken, please contact adminstrator")})
	@RolesAllowed("software.install")
	Response setSoftwareInstalledOnDeviceById(
			@ApiParam(hidden = true) @Auth Session session,
			@ApiParam(value = "ID of the device",    required = true) @PathParam("deviceId")   Long deviceId,
			@ApiParam(value = "Name of the software",required = true) @PathParam("softwareName") String softwareName,
			@ApiParam(value = "Software version",    required = true) @PathParam("version")  String version
			);
	
	/*
	 * DELETE softwares/devicesByName/{deviceName}/{softwareName}/{version}
	 */
	@DELETE
	@Path("devicesByName/{deviceName}/{softwareName}/{version}")
	@Produces(JSON_UTF8)
	@ApiOperation(value = "Set a software on a device as deinstalled in a given version.")
	@ApiResponses(value = {
			@ApiResponse(code = 404, message = "No category was found"),
			@ApiResponse(code = 500, message = "Server broken, please contact adminstrator")})
	@RolesAllowed("software.install")
	Response deleteSoftwareInstalledOnDevice(
			@ApiParam(hidden = true) @Auth Session session,
			@ApiParam(value = "Name of the device",  required = true) @PathParam("deviceName")   String deviceName,
			@ApiParam(value = "Name of the software",required = true) @PathParam("softwareName") String softwareName,
			@ApiParam(value = "Software version",    required = true) @PathParam("version")  String version
			);
	
	/*
	 * DELETE softwares/devices/{deviceId}/{softwareName}/{version}
	 */
	@DELETE
	@Path("devices/{deviceId}/{softwareName}/{version}")
	@Produces(JSON_UTF8)
	@ApiOperation(value = "Set a software on a device as deinstalled in a given version.")
	@ApiResponses(value = {
			@ApiResponse(code = 404, message = "No category was found"),
			@ApiResponse(code = 500, message = "Server broken, please contact adminstrator")})
	@RolesAllowed("software.install")
	Response deleteSoftwareInstalledOnDeviceById(
			@ApiParam(hidden = true) @Auth Session session,
			@ApiParam(value = "ID of the device",    required = true) @PathParam("deviceId")   Long deviceId,
			@ApiParam(value = "Name of the software",required = true) @PathParam("softwareName") String softwareName,
			@ApiParam(value = "Software version",    required = true) @PathParam("version")  String version
			);
	
	/*
	 * GET softwares/devicesByName/{deviceName}/{softwareName}/{version}
	 */
	@GET
	@Path("devicesByName/{deviceName}/{softwareName}/{version}")
	@Produces(JSON_UTF8)
	@ApiOperation(value = "Gets the state of the installation of software(s) on a device.",
		      notes = "The parameters softwareName and version are not mandatory. "
		            + "A call without version delivers the list of all versions of a software on a device. "
			    + "A call without softwareName and version delivers a list of all softwares in all version on a device. "
			    + "The delivered list has following format:<br>"
			    + "[ {<br>"
			    + "&nbsp;&nbsp;&nbsp;name       : Name of the software<br>"
			    + "&nbsp;&nbsp;&nbsp;softwareId : Id of the software<br>"
			    + "&nbsp;&nbsp;&nbsp;version    : Version of the software<br>"
			    + "&nbsp;&nbsp;&nbsp;status     : Installation status of this version<br>"
			    + "} ]"
			    + "There are following installation states:<br>"
			    + "I  -> installed<br>"
			    + "IS -> installation scheduled<br>"
			    + "MD -> manuell deinstalled<br>"
			    + "DS -> deinstallation scheduled<br>"
			    + "DF -> deinstallation failed<br>"
			    + "IF -> installation failed<br>"

		)
	@ApiResponses(value = {
			@ApiResponse(code = 404, message = "No category was found"),
			@ApiResponse(code = 500, message = "Server broken, please contact adminstrator")})
	@RolesAllowed("software.install")
	List<Map<String,String>> getSoftwareStatusOnDevice(
			@ApiParam(hidden = true) @Auth Session session,
			@ApiParam(value = "Name of the device",  required = true)  @PathParam("deviceName")   String deviceName,
			@ApiParam(value = "Name of the software",required = false) @PathParam("softwareName") String softwareName,
			@ApiParam(value = "Software version",    required = false) @PathParam("version")  String version
			);
	
	/*
	 * GET softwares/devices/{deviceId}/{softwareName}/{version}
	 */
	@GET
	@Path("devices/{deviceId}/{softwareName}/{version}")
	@Produces(JSON_UTF8)
	@ApiOperation(value = "the state of the installation of software(s) on a device.",
		      notes = "The parameters softwareName and version are not mandatory. "
		            + "A call without version delivers the list of all versions of a software on a device. "
			    + "A call without softwareName and version delivers a list of all softwares in all version on a device. "
			    + "The delivered list has following format:<br>"
			    + "[ {<br>"
			    + "&nbsp;&nbsp;&nbsp;name       : Name of the software<br>"
			    + "&nbsp;&nbsp;&nbsp;softwareId : Id of the software<br>"
			    + "&nbsp;&nbsp;&nbsp;version    : Version of the software<br>"
			    + "&nbsp;&nbsp;&nbsp;status     : Installation status of this version<br>"
			    + "} ]"
			    + "There are following installation states:<br>"
			    + "I  -> installed<br>"
			    + "IS -> installation scheduled<br>"
			    + "MD -> manuell deinstalled<br>"
			    + "DS -> deinstallation scheduled<br>"
			    + "DF -> deinstallation failed<br>"
			    + "IF -> installation failed"
	)
	@ApiResponses(value = {
			@ApiResponse(code = 404, message = "No category was found"),
			@ApiResponse(code = 500, message = "Server broken, please contact adminstrator")})
	@RolesAllowed("software.install")
	List<Map<String,String>> getSoftwareStatusOnDeviceById(
			@ApiParam(hidden = true) @Auth Session session,
			@ApiParam(value = "Name of the device",  required = true)  @PathParam("deviceId")   Long deviceId,
			@ApiParam(value = "Name of the software",required = false) @PathParam("softwareName") String softwareName,
			@ApiParam(value = "Software version",    required = false) @PathParam("version")  String version
			);
	
}
