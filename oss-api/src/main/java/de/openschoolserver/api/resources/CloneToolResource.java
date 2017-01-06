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
import de.openschoolserver.dao.HWConf;
import de.openschoolserver.dao.Partition;
import de.openschoolserver.dao.Session;

@Path("clonetool")
@Api(value = "clonetool")
public interface CloneToolResource {
       
	/*
	 * Get clonetool/hwconf
	 */
	@GET
	@Path("hwconf")
	@Produces(JSON_UTF8)
	@ApiOperation(value = "Gets the id of the hardwareconfiguration based on the IP-address of the session.")
	@ApiResponses(value = {
	        @ApiResponse(code = 404, message = "Device not found"),
	        @ApiResponse(code = 500, message = "Server broken, please contact adminstrator")})
	@PermitAll
	Long getHWConf(
	        @ApiParam(hidden = true) @Auth Session session
	);
       
	/*
	 * Get clonetool/{hwconf}
	 */
	@GET
	@Path("{hwconf}")
	@Produces(JSON_UTF8)
	@ApiOperation(value = "Gets the configuration of a partition to a given hardware configuration.")
	@ApiResponses(value = {
	        @ApiResponse(code = 404, message = "Device not found"),
	        @ApiResponse(code = 500, message = "Server broken, please contact adminstrator")})
	@PermitAll
	HWConf getConfiguration(
	        @ApiParam(hidden = true) @Auth Session session,
	        @PathParam("hwconf") String hwconf
	);
       
       /*
        * GET clonetool/{hwconf}/partitions
        */
	@GET
	@Path("partitions")
	@Produces(JSON_UTF8)
	@ApiOperation(value = "Gets the colomn separated list of recorded partitions to a given hardware configuration.")
	@ApiResponses(value = {
	        @ApiResponse(code = 404, message = "Device not found"),
	        @ApiResponse(code = 500, message = "Server broken, please contact adminstrator")})
	@PermitAll
	String getPartitions(
	        @ApiParam(hidden = true) @Auth Session session,
	        @PathParam("hwconf") String hwconf,
	        @PathParam("partition") String partition
	);
    
	/*
	 * Get clonetool/{hwconf}/{partition}
	 */
	@GET
	@Path("{hwconf}/{partition}")
	@Produces(JSON_UTF8)
	@ApiOperation(value = "Gets the configuration of a partition to a given hardware configuration.")
	@ApiResponses(value = {
	        @ApiResponse(code = 404, message = "Device not found"),
	        @ApiResponse(code = 500, message = "Server broken, please contact adminstrator")})
	@PermitAll
	Partition getPartition(
	        @ApiParam(hidden = true) @Auth Session session,
	        @PathParam("partition") String partition
	);

	/*
	 * GET clonetool/{hwconf}/{partition}/os
	 */
	@GET
	@Path("{hwconf}/{partition}/os")
	@Produces(JSON_UTF8)
	@ApiOperation(value = "Gets the OS to a given partition.")
	@ApiResponses(value = {
	        @ApiResponse(code = 404, message = "Device not found"),
	        @ApiResponse(code = 500, message = "Server broken, please contact adminstrator")})
	@PermitAll
	String getOS(
	        @ApiParam(hidden = true) @Auth Session session,
	        @PathParam("hwconf") String hwconf,
	        @PathParam("partition") String partition
	);
    
	/*
        * GET clonetool/{hwconf}/{partition}/join
        */
	@GET
	@Path("{howconf}/{partition}/join")
	@Produces(JSON_UTF8)
	@ApiOperation(value = "Gets the join type to a given partition.")
	@ApiResponses(value = {
	        @ApiResponse(code = 404, message = "Device not found"),
	        @ApiResponse(code = 500, message = "Server broken, please contact adminstrator")})
	@PermitAll
	String getJoin(
	        @ApiParam(hidden = true) @Auth Session session,
	        @PathParam("hwconf") String hwconf,
	        @PathParam("partition") String partition
	);
    
	/*
	 * GET clonetool/{hwconf}/{partition}/description
	 */
	@GET
	@Path("{hwconf}/{partition}/description")
	@Produces(JSON_UTF8)
	@ApiOperation(value = "Gets the description to a given partition.")
	@ApiResponses(value = {
	        @ApiResponse(code = 404, message = "Device not found"),
	        @ApiResponse(code = 500, message = "Server broken, please contact adminstrator")})
	@PermitAll
	String getDescription(
	        @ApiParam(hidden = true) @Auth Session session,
	        @PathParam("hwconf") String hwconf,
	        @PathParam("partition") String partition
	);
    
	/*
	 * GET clonetool/{hwconf}/{partition}/format
	 */
	@GET
	@Path("{hwconf}/{partition}/format")
	@Produces(JSON_UTF8)
	@ApiOperation(value = "Gets the filesystem format to a given partition.")
	@ApiResponses(value = {
	        @ApiResponse(code = 404, message = "Device not found"),
	        @ApiResponse(code = 500, message = "Server broken, please contact adminstrator")})
	@PermitAll
	String getFormat(
	        @ApiParam(hidden = true) @Auth Session session,
		@PathParam("hwconf") String hwconf,
	        @PathParam("partition") String partition
	);
    
	/*
	 * GET clonetool/{hwconf}/{partition}/itool
	 */
	@GET
	@Path("{hwconf}/{partition}/itool")
	@Produces(JSON_UTF8)
	@ApiOperation(value = "Gets the used imaging tool to a given partition.")
	@ApiResponses(value = {
	        @ApiResponse(code = 404, message = "Device not found"),
	        @ApiResponse(code = 500, message = "Server broken, please contact adminstrator")})
	@PermitAll
	String getItool(
	        @ApiParam(hidden = true) @Auth Session session,
		@PathParam("hwconf") String hwconf,
	        @PathParam("partition") String partition
	);

	// POST and PUSH methodes.

	/*
	 * POST clonetool/hwconf
	 */
	@POST
	@Path("hwconf")
	@Produces(JSON_UTF8)
	@ApiOperation(value = "Creates a new hardware configuration.")
	@ApiResponses(value = {
	        @ApiResponse(code = 404, message = "Device not found"),
	        @ApiResponse(code = 500, message = "Server broken, please contact adminstrator")})
	@PermitAll
	Boolean createHWConf(
	        @ApiParam(hidden = true) @Auth Session session,
		HWConf hwconf
	);
       
	/*
	 * Get clonetool/{hwconf}
	 */
	@POST
	@Path("{hwconf}")
	@Produces(JSON_UTF8)
	@ApiOperation(value = "Updates a hardware configuration.")
	@ApiResponses(value = {
	        @ApiResponse(code = 404, message = "Device not found"),
	        @ApiResponse(code = 500, message = "Server broken, please contact adminstrator")})
	@PermitAll
	Boolean setConfiguration(
	        @ApiParam(hidden = true) @Auth Session session,
	        @PathParam("hwconf") String hwconf,
		HWConf hwconf
	);
       
	/*
	 * POST clonetool/{hwconf}/{partition}
	 */
	@POST
	@Path("{hwconf}/{partition}")
	@Produces(JSON_UTF8)
	@ApiOperation(value = "Gets the configuration of a partition to a given hardware configuration.")
	@ApiResponses(value = {
	        @ApiResponse(code = 404, message = "Device not found"),
	        @ApiResponse(code = 500, message = "Server broken, please contact adminstrator")})
	@PermitAll
	Boolean setPartition(
	        @ApiParam(hidden = true) @Auth Session session,
	        @PathParam("partition") String partition,
		Partition partition
	);

	/*
	 * PUT clonetool/{hwconf}/{partition}/os/{os}
	 */
	@PUT
	@Path("{hwconf}/{partition}/os/{os}")
	@Produces(JSON_UTF8)
	@ApiOperation(value = "Gets the OS to a given partition.")
	@ApiResponses(value = {
	        @ApiResponse(code = 404, message = "Device not found"),
	        @ApiResponse(code = 500, message = "Server broken, please contact adminstrator")})
	@PermitAll
	Boolean getOS(
	        @ApiParam(hidden = true) @Auth Session session,
	        @PathParam("hwconf") String hwconf,
	        @PathParam("partition") String partition,
	        @PathParam("os") String os
	);
    
	/*
        * GET clonetool/{hwconf}/{partition}/join/{join}
        */
	@GET
	@Path("{howconf}/{partition}/join/{join}")
	@Produces(JSON_UTF8)
	@ApiOperation(value = "Gets the join type to a given partition.")
	@ApiResponses(value = {
	        @ApiResponse(code = 404, message = "Device not found"),
	        @ApiResponse(code = 500, message = "Server broken, please contact adminstrator")})
	@PermitAll
	Boolean getJoin(
	        @ApiParam(hidden = true) @Auth Session session,
	        @PathParam("hwconf") String hwconf,
	        @PathParam("partition") String partition.
	        @PathParam("os") String os
	);
    
	/*
	 * GET clonetool/{hwconf}/{partition}/description
	 */
	@GET
	@Path("{hwconf}/{partition}/description/{description}")
	@Produces(JSON_UTF8)
	@ApiOperation(value = "Gets the description to a given partition.")
	@ApiResponses(value = {
	        @ApiResponse(code = 404, message = "Device not found"),
	        @ApiResponse(code = 500, message = "Server broken, please contact adminstrator")})
	@PermitAll
	Boolean getDescription(
	        @ApiParam(hidden = true) @Auth Session session,
	        @PathParam("hwconf") String hwconf,
	        @PathParam("partition") String partition,
	        @PathParam("join") String join
	);
    
	/*
	 * GET clonetool/{hwconf}/{partition}/format/{format}
	 */
	@GET
	@Path("{hwconf}/{partition}/format/{format}")
	@Produces(JSON_UTF8)
	@ApiOperation(value = "Gets the filesystem format to a given partition.")
	@ApiResponses(value = {
	        @ApiResponse(code = 404, message = "Device not found"),
	        @ApiResponse(code = 500, message = "Server broken, please contact adminstrator")})
	@PermitAll
	Boolean getFormat(
	        @ApiParam(hidden = true) @Auth Session session,
		@PathParam("hwconf") String hwconf,
	        @PathParam("partition") String partition,
	        @PathParam("format") String format
	);
    
	/*
	 * GET clonetool/{hwconf}/{partition}/itool/{itool}
	 */
	@GET
	@Path("{hwconf}/{partition}/itool/{itool}")
	@Produces(JSON_UTF8)
	@ApiOperation(value = "Gets the used imaging tool to a given partition.")
	@ApiResponses(value = {
	        @ApiResponse(code = 404, message = "Device not found"),
	        @ApiResponse(code = 500, message = "Server broken, please contact adminstrator")})
	@PermitAll
	Boolean getItool(
	        @ApiParam(hidden = true) @Auth Session session,
		@PathParam("hwconf") String hwconf,
	        @PathParam("partition") String partition,
	        @PathParam("format") String itool
	);
    

}

