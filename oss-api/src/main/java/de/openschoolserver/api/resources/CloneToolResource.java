package de.openschoolserver.api.resources;

import static de.openschoolserver.api.resources.Resource.JSON_UTF8;

import java.util.List;
import javax.annotation.security.PermitAll;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import io.dropwizard.auth.Auth;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import de.openschoolserver.dao.Device;
import de.openschoolserver.dao.Partition;
import de.openschoolserver.dao.Session;

@Path("clonetool")
@Api(value = "clonetool")
public interface CloneToolResource {
       
       /*
        * Get clonetool/{partition}
        */
       @GET
       @Path("{partition}")
       @Produces(JSON_UTF8)
    @ApiOperation(value = "Gets the configuration of a partition to a given device. The hw configuration should be found by the session.")
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "Device not found"),
            @ApiResponse(code = 500, message = "Server broken, please contact adminstrator")})
    @PermitAll
    Partition getPartition(
            @ApiParam(hidden = true) @Auth Session session,
            @PathParam("partition") String partition
    );
       
       /*
        * Get clonetool/configuration
        */
       @GET
       @Path("configuration/{hwconf}")
       @Produces(JSON_UTF8)
    @ApiOperation(value = "Gets the configuration of a partition to a given device. The hw configuration should be found by the session.")
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "Device not found"),
            @ApiResponse(code = 500, message = "Server broken, please contact adminstrator")})
    @PermitAll
    List<Partition> getConfiguration(
            @ApiParam(hidden = true) @Auth Session session,
            @PathParam("hwconf") String hwconf
    );
       
       /*
        * GET clonetool/partitions
        */
    @GET
    @Path("partitions")
    @Produces(JSON_UTF8)
    @ApiOperation(value = "Gets the recorded partitions to a given device. The hw configuration should be found by the session.")
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "Device not found"),
            @ApiResponse(code = 500, message = "Server broken, please contact adminstrator")})
    @PermitAll
    String getPartitions(
            @ApiParam(hidden = true) @Auth Session session,
            @PathParam("partition") String partition
    );
    
       /*
        * GET clonetool/os/{partition}
        */
    @GET
    @Path("os/{partition}")
    @Produces(JSON_UTF8)
    @ApiOperation(value = "Gets the OS to a given partition. The hw configuration should be found by the session.")
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "Device not found"),
            @ApiResponse(code = 500, message = "Server broken, please contact adminstrator")})
    @PermitAll
    String getOS(
            @ApiParam(hidden = true) @Auth Session session,
            @PathParam("partition") String partition
    );
    
    /*
        * GET clonetool/join/{partition}
        */
    @GET
    @Path("join/{partition}")
    @Produces(JSON_UTF8)
    @ApiOperation(value = "Gets the join type to a given partition. The hw configuration should be found by the session.")
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "Device not found"),
            @ApiResponse(code = 500, message = "Server broken, please contact adminstrator")})
    @PermitAll
    String getJoin(
            @ApiParam(hidden = true) @Auth Session session,
            @PathParam("partition") String partition
    );
    
    /*
        * GET clonetool/description/{partition}
        */
    @GET
    @Path("description/{partition}")
    @Produces(JSON_UTF8)
    @ApiOperation(value = "Gets the description to a given partition. The hw configuration should be found by the session.")
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "Device not found"),
            @ApiResponse(code = 500, message = "Server broken, please contact adminstrator")})
    @PermitAll
    String getDescription(
            @ApiParam(hidden = true) @Auth Session session,
            @PathParam("partition") String partition
    );
    
    /*
        * GET clonetool/format/{partition}
        */
    @GET
    @Path("format/{partition}")
    @Produces(JSON_UTF8)
    @ApiOperation(value = "Gets the filesystem format to a given partition. The hw configuration should be found by the session.")
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "Device not found"),
            @ApiResponse(code = 500, message = "Server broken, please contact adminstrator")})
    @PermitAll
    String getFormat(
            @ApiParam(hidden = true) @Auth Session session,
            @PathParam("partition") String partition
    );
    
    /*
        * GET clonetool/format/{partition}
        */
    @GET
    @Path("itool/{partition}")
    @Produces(JSON_UTF8)
    @ApiOperation(value = "Gets the used imaging tool to a given partition. The hw configuration should be found by the session.")
    @ApiResponses(value = {
    @Path("format/{partition}")
    @Produces(JSON_UTF8)
    @ApiOperation(value = "Gets the filesystem format to a given partition. The hw configuration should be found by the session.")
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "Device not found"),
            @ApiResponse(code = 500, message = "Server broken, please contact adminstrator")})
    @PermitAll
    String getFormat(
            @ApiParam(hidden = true) @Auth Session session,
            @PathParam("partition") String partition
    );
    
    /*
        * GET clonetool/format/{partition}
        */
    @GET
    @Path("itool/{partition}")
    @Produces(JSON_UTF8)
    @ApiOperation(value = "Gets the used imaging tool to a given partition. The hw configuration should be found by the session.")
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "Device not found"),
            @ApiResponse(code = 500, message = "Server broken, please contact adminstrator")})
    @PermitAll
    String getItool(
            @ApiParam(hidden = true) @Auth Session session,
            @PathParam("partition") String partition
    );
    

}

