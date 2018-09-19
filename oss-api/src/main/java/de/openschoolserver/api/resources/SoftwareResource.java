/* (c) 2017 Peter Varkoly <peter@varkoly.de> - all rights reserved */
package de.openschoolserver.api.resources;

import static de.openschoolserver.api.resources.Resource.*;
import javax.annotation.security.RolesAllowed;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
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
import de.openschoolserver.dao.SoftwareStatus;
import de.openschoolserver.dao.OssResponse;
import de.openschoolserver.dao.Session;

@Path("softwares")
@Api(value = "softwares")
public interface SoftwareResource {

    /* ################################
     * Functions to manage softwares  #
     * ################################
     *
     * Get softwares/all
     */
    @GET
    @Path("all")
    @Produces(JSON_UTF8)
    @ApiOperation(value = "Gets all Softwares.")
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "No category was found"),
            @ApiResponse(code = 500, message = "Server broken, please contact adminstrator")})
    @RolesAllowed("software.manage")
    List<Software> getAll(
            @ApiParam(hidden = true) @Auth Session session
            );

    /*
    * Get softwares/allInstallable
    */
   @GET
   @Path("allInstallable")
   @Produces(JSON_UTF8)
   @ApiOperation(value = "Gets all Softwares.")
   @ApiResponses(value = {
           @ApiResponse(code = 404, message = "No category was found"),
           @ApiResponse(code = 500, message = "Server broken, please contact adminstrator")})
   @RolesAllowed("software.manage")
   List<Software> getAllInstallable(
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
    @RolesAllowed("software.manage")
    Software getById(
            @ApiParam(hidden = true) @Auth Session session,
            @PathParam("softwareId") long softwareId
            );

    /*
        * POST softwares/getSoftwares
        */
       @POST
       @Path("getSoftwares")
       @Produces(JSON_UTF8)
       @ApiOperation(value = "Gets a list of software objects to the list of softwareIds.")
       @ApiResponses(value = {
               @ApiResponse(code = 500, message = "Server broken, please contact adminstrator")})
       @RolesAllowed("software.manage")
       List<Software> getSoftwares(
               @ApiParam(hidden = true) @Auth Session session,
               List<Long> softwareIds
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
    @RolesAllowed("software.manage")
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
    @ApiOperation(value = "Add a new version of software. If the software does not exists this will be create.<br>" +
                          "If the software does exists all older versions will be set to 'R'eplaced and the actuell version to 'A'.<br>" +
                          "The software version must be given! Now we only provides one actual version.")
    @ApiResponses(value = {
            // TODO so oder anders? @ApiResponse(code = 404, message = "At least one user was not found"),
            @ApiResponse(code = 500, message = "Server broken, please contact administrator")
    })
    @RolesAllowed("software.add")
    OssResponse add(
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
    OssResponse modify(
            @ApiParam(hidden = true) @Auth Session session,
            Software software
            );

    /*
     * POST softwares/addRequirements { hash }
     */
    @POST
    @Path("addRequirements")
    @Produces(JSON_UTF8)
    @ApiOperation(value = "Add a software requirement by name")
    @ApiResponses(value = {
            // TODO so oder anders? @ApiResponse(code = 404, message = "At least one user was not found"),
            @ApiResponse(code = 500, message = "Server broken, please contact administrator")
    })
    @RolesAllowed("software.modify")
    OssResponse addRequirements(
            @ApiParam(hidden = true) @Auth Session session,
            List<String> requirement
            );

    @PUT
    @Path("{softwareId}/{requirementId}")
    @Produces(JSON_UTF8)
    @ApiOperation(value = "Add a software requirement to a software")
    @ApiResponses(value = {
            // TODO so oder anders? @ApiResponse(code = 404, message = "At least one user was not found"),
            @ApiResponse(code = 500, message = "Server broken, please contact administrator")
    })
    @RolesAllowed("software.modify")
    OssResponse addRequirements(
            @ApiParam(hidden = true) @Auth Session session,
            @PathParam("softwareId")    long softwareId,
            @PathParam("requirementId") long requirementId
            );

    @DELETE
    @Path("{softwareId}/{requirementId}")
    @Produces(JSON_UTF8)
    @ApiOperation(value = "Remove a software requirement from a software")
    @ApiResponses(value = {
            // TODO so oder anders? @ApiResponse(code = 404, message = "At least one user was not found"),
            @ApiResponse(code = 500, message = "Server broken, please contact administrator")
    })
    @RolesAllowed("software.modify")
    OssResponse deleteRequirements(
            @ApiParam(hidden = true) @Auth Session session,
            @PathParam("softwareId")    long softwareId,
            @PathParam("requirementId") long requirementId
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
    OssResponse delete(
            @ApiParam(hidden = true) @Auth Session session,
            @PathParam("softwareId") long softwareId
            );


    /*#############################
     *  Manage software licenses  #
     * ############################
     *
     * POST softwares/{softwareId}/license
     */
    @POST
    @Path("{softwareId}/license")
    @Produces(JSON_UTF8)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @ApiOperation( value = "Creates licence(s) to a software" )
    @ApiResponses(value = {
                @ApiResponse(code = 500, message = "Server broken, please contact administrator")
    })
    @RolesAllowed("software.modify")
    OssResponse addLicenseToSoftware(
            @ApiParam(hidden = true) @Auth Session session,
            @PathParam("softwareId") long softwareId,
            @FormDataParam("licenseType") Character licenseType,
            @FormDataParam("count")          Integer   count,
            @FormDataParam("value")       String    value,
            @FormDataParam("file")  final InputStream fileInputStream,
            @FormDataParam("file")  final FormDataContentDisposition contentDispositionHeader
            );

    @GET
    @Path("{softwareId}/license")
    @Produces(JSON_UTF8)
    @ApiOperation( value = "Gets the licences to a software" )
    @ApiResponses(value = {
                @ApiResponse(code = 500, message = "Server broken, please contact administrator")
    })
    @RolesAllowed("software.modify")
    List<SoftwareLicense> getSoftwareLicenses(
            @ApiParam(hidden = true) @Auth Session session,
            @PathParam("softwareId") long softwareId
            );
    /*
     * POST softwares/licenses/{licenseId}
     */
    @POST
    @Path("licenses/{licenseId}")
    @Produces(JSON_UTF8)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @ApiOperation( value = "Modifies an existing license." )
    @ApiResponses(value = {
                @ApiResponse(code = 500, message = "Server broken, please contact administrator")
    })
    @RolesAllowed("software.modify")
    OssResponse modifyLicense(
            @ApiParam(hidden = true) @Auth Session session,
            @PathParam("licenseId") long licenseId,
            @FormDataParam("licenseType") Character licenseType,
            @FormDataParam("count")          Integer   count,
            @FormDataParam("value")       String    value,
            @FormDataParam("file") final InputStream fileInputStream,
            @FormDataParam("file") final FormDataContentDisposition contentDispositionHeader
            );

    /*
     * DELETE softwares/licenses/
     */
    @DELETE
    @Path("licenses/{licenseId}")
    @Produces(JSON_UTF8)
    @ApiOperation( value = "Deletes an existing licence." )
    @ApiResponses(value = {
                @ApiResponse(code = 500, message = "Server broken, please contact administrator")
    })
    @RolesAllowed("software.modify")
    OssResponse deleteLicense(
            @ApiParam(hidden = true) @Auth Session session,
            @PathParam("licenseId") long licenseId
            );

    /* ########################################
     * Functions to manage software download  #
     * ########################################
     */
    /*
     * GET softwares/available
     */
    @GET
    @Path("available")
    @Produces(JSON_UTF8)
    @ApiOperation(value = "Gets the available softwares from the CEPHALIX repository.")
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "No category was found"),
            @ApiResponse(code = 500, message = "Server broken, please contact adminstrator"),
            @ApiResponse(code = 600, message = "Connection to CEPHALIX software repository server is broken.")})
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
    notes = "The call must provide a list of softwares to be downloaded: "
            + "[ \"MSWhatever\", \"AnOtherProgram\" ] "
            + "The requirements will be solved automaticaly.")
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "No category was found"),
            @ApiResponse(code = 500, message = "Server broken, please contact adminstrator"),
            @ApiResponse(code = 600, message = "Connection to CEPHALIX software repository server is broken.")})
    @RolesAllowed("software.download")
    OssResponse download(
            @ApiParam(hidden = true) @Auth Session session,
            List<String> softwares
            );

    /*
     * PUT softwares/download/{softwareName}
     */
    @PUT
    @Path("download/{softwareName}")
    @Produces(JSON_UTF8)
    @ApiOperation(value = "Downloads a software from the CEPHALIX repository.")
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "No category was found"),
            @ApiResponse(code = 500, message = "Server broken, please contact adminstrator"),
            @ApiResponse(code = 600, message = "Connection to CEPHALIX software repository server is broken.")})
    @RolesAllowed("software.download")
    OssResponse downloadOne(
            @ApiParam(hidden = true) @Auth Session session,
            @PathParam("softwareName") String softwareName
            );

    /*
     * DELET softwares/downloaded/{softwareName}
     */
    @POST
    @Path("deleteDownloadedSoftwares")
    @Produces(JSON_UTF8)
    @ApiOperation(value = "Delets software downloaded from the CEPHALIX repository.")
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "No category was found"),
            @ApiResponse(code = 500, message = "Server broken, please contact adminstrator"),
            @ApiResponse(code = 600, message = "Connection to CEPHALIX software repository server is broken.")})
    @RolesAllowed("software.download")
    OssResponse deleteDownloadedSoftwares(
            @ApiParam(hidden = true) @Auth Session session,
            List<String> softwares
            );

    /*
     * GET softwares/downloadStatus
     */
    @GET
    @Path("downloadStatus")
    @Produces(TEXT)
    @ApiOperation(value = "Gets the names of the packages being dowloaded. Empty string means no download proceded.")
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Server broken, please contact adminstrator")
            })
    @RolesAllowed("software.download")
    String downloadStatus(
            @ApiParam(hidden = true) @Auth Session session
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
     * POST softwares/listUpdatesForSoftwarePackages
     */
    @POST
    @Path("listUpdatesForSoftwarePackages")
    @Produces(JSON_UTF8)
    @ApiOperation(value = "List the status of the downloaded software. ",
            notes = "This call delivers a list of maps of the downloaded software.<br>"
            + "[ {<br>"
            + "    name : name of the software,<br>"
            + "    versions : the new version of the software,<br>"
            + "} ]")
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "No category was found"),
            @ApiResponse(code = 500, message = "Server broken, please contact adminstrator")})
    @RolesAllowed("software.install")
    List<Map<String,String>> listUpdatesForSoftwarePackages(
            @ApiParam(hidden = true) @Auth Session session
    );

    /*
     * POST softwares/updatesSoftwares ['afasd','afasds','dsfasdfs']
     */
    @POST
    @Path("updatesSoftwares")
    @Produces(JSON_UTF8)
    @ApiOperation(value = "Downloads softwares from the CEPHALIX repository.",
    notes = "The call must provide a list of softwares to be downloaded: "
            + "[ \"MSWhatever\", \"AnOtherProgram\" ] "
            + "The requirements will be solved automaticaly.")
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "No category was found"),
            @ApiResponse(code = 500, message = "Server broken, please contact adminstrator"),
            @ApiResponse(code = 600, message = "Connection to CEPHALIX software repository server is broken.")})
    @RolesAllowed("software.download")
    OssResponse updatesSoftwares(
            @ApiParam(hidden = true) @Auth Session session,
            List<String> softwares
            );

    /**
     * Creates the salt state files for the minions.
     * @param session
     * @param category
     * @return  The result in an OssResult object
     */
    @PUT
    @Path("saveState")
    @Produces(JSON_UTF8)
    @ApiOperation(value = "Creates the salt state files for the minions.")
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "Software not found"),
            @ApiResponse(code = 500, message = "Server broken, please contact adminstrator")})
    @RolesAllowed("software.modify")
    OssResponse apply(
            @ApiParam(hidden = true) @Auth Session session
            );

    /**
     * Applies the high states created in the salt state files for the minions.
     * @param session
     * @param category
     * @return  The result in an OssResult object.
     */
    @PUT
    @Path("applyState")
    @Produces(JSON_UTF8)
    @ApiOperation(value = "Applies the high states created in the salt state files for the minions.")
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "Software not found"),
            @ApiResponse(code = 500, message = "Server broken, please contact adminstrator")})
    @RolesAllowed("software.modify")
    OssResponse applyState(
            @ApiParam(hidden = true) @Auth Session session
            );

    /**
     * Creates a new software installation set.
     * @param session
     * @param category
     * @return The result in an OssResult object.
     */
    @POST
    @Path("installations")
    @Produces(JSON_UTF8)
    @ApiOperation( value = "Creates a software installation" )
    @ApiResponses(value = {
                @ApiResponse(code = 500, message = "Server broken, please contact administrator")
    })
    @RolesAllowed("software.install")
    OssResponse createInstallation(
            @ApiParam(hidden = true) @Auth Session session,
            Category category
            );

    /*
     * GET softwares/installations
     */
    @GET
    @Path("installations")
    @Produces(JSON_UTF8)
    @ApiOperation( value = "Gets all installations" )
    @ApiResponses(value = {
                @ApiResponse(code = 500, message = "Server broken, please contact administrator")
    })
    @RolesAllowed("software.install")
    List<Category> getInstallations(
            @ApiParam(hidden = true) @Auth Session session
    );

    /*
     * GET softwares/installations
     */
    @GET
    @Path("installations/{installationId}")
    @Produces(JSON_UTF8)
    @ApiOperation( value = "Gets an installations by id." )
    @ApiResponses(value = {
                @ApiResponse(code = 500, message = "Server broken, please contact administrator")
    })
    @RolesAllowed("software.install")
    Category getInstallation(
            @ApiParam(hidden = true) @Auth Session session,
            @PathParam("installationId") long installationId
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
    OssResponse addSoftwareToInstalation(
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
    OssResponse addDeviceToInstalation(
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
    OssResponse addRoomToInstalation(
            @ApiParam(hidden = true) @Auth Session session,
            @PathParam("installationId") long installationId,
            @PathParam("roomId")         long roomId
            );

    /*
     * PUT softwares/installations/{installationId}/hwconfs/{hwconfId}
     */
    @PUT
    @Path("installations/{installationId}/hwconfs/{hwconfId}")
    @Produces(JSON_UTF8)
    @ApiOperation(value = "Adds a hwconf to an installation.")
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "Software not found"),
            @ApiResponse(code = 500, message = "Server broken, please contact adminstrator")})
    @RolesAllowed("software.install")
    OssResponse addHWConfToInstalation(
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
    OssResponse deleteInstalation(
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
    OssResponse deleteSoftwareFromInstalation(
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
    OssResponse deleteDeviceFromInstalation(
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
    OssResponse deleteRoomFromInstalation(
            @ApiParam(hidden = true) @Auth Session session,
            @PathParam("installationId") long installationId,
            @PathParam("roomId")         long roomId
            );

    /*
     * DELETE softwares/installations/{installationId}/hwconfs/{hwconfId}
     */
    @DELETE
    @Path("installations/{installationId}/hwconfs/{hwconfId}")
    @Produces(JSON_UTF8)
    @ApiOperation(value = "Deletes a hwconf from an installation.")
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "Software not found"),
            @ApiResponse(code = 500, message = "Server broken, please contact adminstrator")})
    @RolesAllowed("software.install")
    OssResponse deleteHWConfFromInstalation(
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
    @ApiOperation(value = "Gets the list of softwares in an installation.")
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

    /* #######################################################
     * Functions for the plugin by starting the clients.     #
     * In this case only the device name is accessible.      #
     * #######################################################
     *
     * PUT softwares/devicesByName/{deviceName}/{softwareName}/{version}
     */
    @PUT
    @Path("devicesByName/{deviceName}/{softwareName}/{version}")
    @Produces(JSON_UTF8)
    @ApiOperation(value = "Set a software on a device as installed in a given version."
            + " This will be called by the tool read_installed_software.pl by starting the clients.")
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "No category was found"),
            @ApiResponse(code = 500, message = "Server broken, please contact adminstrator")})
    @RolesAllowed("software.install")
    OssResponse setSoftwareInstalledOnDevice(
            @ApiParam(hidden = true) @Auth Session session,
            @ApiParam(value = "Name of the device",  required = true) @PathParam("deviceName")   String deviceName,
            @ApiParam(value = "Name of the software",required = true) @PathParam("softwareName") String softwareName,
            @ApiParam(value = "Software version",    required = true) @PathParam("version")  String version
            );

    @GET
    @Path("devicesByName/{deviceName}/licences")
    @Produces(TEXT)
    @ApiOperation(value = "Set a software on a device as installed in a given version.")
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "No category was found"),
            @ApiResponse(code = 500, message = "Server broken, please contact adminstrator")})
    @RolesAllowed("software.install")
    String getSoftwareLicencesOnDevice(
            @ApiParam(hidden = true) @Auth Session session,
            @ApiParam(value = "Name of the device",  required = true) @PathParam("deviceName")   String deviceName
            );


    /* ##########################################
     * Functions to get the installation status
     * ##########################################
     *
     * GET softwares/devices/{deviceId}
     */
    @GET
    @Path("devices/{deviceId}")
    @Produces(JSON_UTF8)
    @ApiOperation(value = "The state of the installation of software(s) on a device.",
              notes = "A call with ID < 1 as softwareId a list of all softwares in all version on a device. "
                + "The delivered list has following format:<br>"
                + "[ {<br>"
                + "&nbsp;&nbsp;&nbsp;softwareName : Name of the software<br>"
                + "&nbsp;&nbsp;&nbsp;deviceName   : Name of the device<br>"
                + "&nbsp;&nbsp;&nbsp;softwareversionId : Id of the SoftwareVersion<br>"
                + "&nbsp;&nbsp;&nbsp;version    : Version of the software<br>"
                + "&nbsp;&nbsp;&nbsp;status     : Installation status of this version<br>"
                + "&nbsp;&nbsp;&nbsp;manually   : Was the softwar installed manually<br>"
                + "} ]<br>"
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
    List<SoftwareStatus> getAllSoftwareStatusOnDevice(
            @ApiParam(hidden = true) @Auth Session session,
            @ApiParam(value = "ID of the device",  required = true) @PathParam("deviceId")   Long deviceId
            );


    /*
     * GET softwares/devices/{deviceId}/{softwareName}
     */
    @GET
    @Path("devices/{deviceId}/{softwareId}")
    @Produces(JSON_UTF8)
    @ApiOperation(value = "The state of the installation of software(s) on a device.",
              notes = "A call with ID < 1 as softwareId a list of all softwares in all version on a device. "
                + "The delivered list has following format:<br>"
                + "[ {<br>"
                + "&nbsp;&nbsp;&nbsp;softwareName : Name of the software<br>"
                + "&nbsp;&nbsp;&nbsp;deviceName   : Name of the device<br>"
                + "&nbsp;&nbsp;&nbsp;softwareversionId : Id of the SoftwareVersion<br>"
                + "&nbsp;&nbsp;&nbsp;version    : Version of the software<br>"
                + "&nbsp;&nbsp;&nbsp;status     : Installation status of this version<br>"
                + "&nbsp;&nbsp;&nbsp;manually   : Was the softwar installed manually<br>"
                + "} ]<br>"
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
    List<SoftwareStatus> getSoftwareStatusOnDevice(
            @ApiParam(hidden = true) @Auth Session session,
            @ApiParam(value = "ID of the device",  required = true) @PathParam("deviceId")   Long deviceId,
            @ApiParam(value = "ID of the software",required = true) @PathParam("sofwtwareId") Long softwareId
            );

    /*
     * GET softwares/{softwareId}/{softwareName}
     */
    @GET
    @Path("{softwareId}/status")
    @Produces(JSON_UTF8)
    @ApiOperation(value = "the state of the installation of software(s) on all devices.",
              notes = "The delivered list has following format:<br>"
                + "[ {<br>"
                + "&nbsp;&nbsp;&nbsp;softwareName : Name of the software<br>"
                + "&nbsp;&nbsp;&nbsp;deviceName   : Name of the device<br>"
                + "&nbsp;&nbsp;&nbsp;softwareversionId : Id of the SoftwareVersion<br>"
                + "&nbsp;&nbsp;&nbsp;version    : Version of the software<br>"
                + "&nbsp;&nbsp;&nbsp;status     : Installation status of this version<br>"
                + "&nbsp;&nbsp;&nbsp;manually   : Was the softwar installed manually<br>"
                + "} ]<br>"
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
    List<SoftwareStatus> getSoftwareStatus(
            @ApiParam(hidden = true) @Auth Session session,
            @ApiParam(value = "ID of the software",required = true) @PathParam("softwareId") Long softwareId
            );

    /*
     * GET softwares/rooms/{roomId}
     */
    @GET
    @Path("rooms/{roomId}")
    @Produces(JSON_UTF8)
    @ApiOperation(value = "the state of the installation status in a room.",
              notes = "The delivered list has following format:<br>"
                + "[ {<br>"
                + "&nbsp;&nbsp;&nbsp;softwareName : Name of the software<br>"
                + "&nbsp;&nbsp;&nbsp;deviceName   : Name of the device<br>"
                + "&nbsp;&nbsp;&nbsp;softwareversionId : Id of the SoftwareVersion<br>"
                + "&nbsp;&nbsp;&nbsp;version    : Version of the software<br>"
                + "&nbsp;&nbsp;&nbsp;status     : Installation status of this version<br>"
                + "&nbsp;&nbsp;&nbsp;manually   : Was the softwar installed manually<br>"
                + "} ]<br>"
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
    List<SoftwareStatus> getRoomsStatus(
            @ApiParam(hidden = true) @Auth Session session,
            @ApiParam(value = "ID of the software",required = true) @PathParam("roomId") Long roomId
            );

    /*
     * GET softwares/hwconfs/{hwconfId}
     */
    @GET
    @Path("hwconfs/{hwconfId}")
    @Produces(JSON_UTF8)
    @ApiOperation(value = "the state of the installation status of a hwconf.",
              notes = "The delivered list has following format:<br>"
                + "[ {<br>"
                + "&nbsp;&nbsp;&nbsp;softwareName : Name of the software<br>"
                + "&nbsp;&nbsp;&nbsp;deviceName   : Name of the device<br>"
                + "&nbsp;&nbsp;&nbsp;softwareversionId : Id of the SoftwareVersion<br>"
                + "&nbsp;&nbsp;&nbsp;version    : Version of the software<br>"
                + "&nbsp;&nbsp;&nbsp;status     : Installation status of this version<br>"
                + "&nbsp;&nbsp;&nbsp;manually   : Was the softwar installed manually<br>"
                + "} ]<br>"
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
    List<SoftwareStatus> getHWConsStatus(
            @ApiParam(hidden = true) @Auth Session session,
            @ApiParam(value = "ID of the software",required = true) @PathParam("hwconfId") Long hwconfId
            );
}
