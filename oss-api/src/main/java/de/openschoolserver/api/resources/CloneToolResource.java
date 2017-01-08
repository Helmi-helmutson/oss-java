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
	 * Get clonetool/{hwconfId}
	 */
	@GET
	@Path("{hwconfId}")
	@Produces(JSON_UTF8)
	@ApiOperation(value = "Gets the configuration of a partition to a given hardware configuration.")
	@ApiResponses(value = {
	        @ApiResponse(code = 404, message = "Device not found"),
	        @ApiResponse(code = 500, message = "Server broken, please contact adminstrator")})
	@PermitAll
	HWConf getById(
	        @ApiParam(hidden = true) @Auth Session session,
	        @PathParam("hwconfId") Long hwconfId
	);
       
       /*
        * GET clonetool/{hwconfId}/partitions
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
	        @PathParam("hwconfId") Long hwconfId,
	        @PathParam("partition") String partition
	);
    
	/*
	 * Get clonetool/{hwconfId}/{partition}
	 */
	@GET
	@Path("{hwconfId}/{partition}")
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
	 * GET clonetool/{hwconfId}/{partition}/{key}
	 */
	@GET
	@Path("{hwconfId}/{partition}/{key}")
	@Produces(JSON_UTF8)
	@ApiOperation(value = "Gets the value of a key to a given partition.")
			      "The key may be: OS, Description, Join, Format, Itool" )
	@ApiResponses(value = {
	        @ApiResponse(code = 404, message = "Device not found"),
	        @ApiResponse(code = 500, message = "Server broken, please contact adminstrator")})
	@PermitAll
	String getConfigurationValue(
	        @ApiParam(hidden = true) @Auth Session session,
	        @PathParam("hwconfId") Long hwconfId,
	        @PathParam("partition") String partition,
	        @PathParam("key") String key
	);

	// POST and PUSH methodes.

	/*
	 * POST clonetool/hwconf
	 */
	@POST
	@Path("hwconf")
	@Produces(JSON_UTF8)
	@ApiOperation(value = "Creates a new hardware configuration. And returns the hwoconfId")
	@ApiResponses(value = {
	        @ApiResponse(code = 404, message = "Device not found"),
	        @ApiResponse(code = 500, message = "Server broken, please contact adminstrator")})
	@PermitAll
	Long createHWConf(
	        @ApiParam(hidden = true) @Auth Session session,
		HWConf hwconf
	);
       
	/*
	 * Post clonetool/{hwconfId}
	 */
	@POST
	@Path("{hwconfId}")
	@Produces(JSON_UTF8)
	@ApiOperation(value = "Updates a hardware configuration.")
	@ApiResponses(value = {
	        @ApiResponse(code = 404, message = "Device not found"),
	        @ApiResponse(code = 500, message = "Server broken, please contact adminstrator")})
	@PermitAll
	Boolean setConfiguration(
	        @ApiParam(hidden = true) @Auth Session session,
	        @PathParam("hwconfId") Long hwconfId,
		HWConf hwconf
	);
       
	/*
	 * POST clonetool/{hwconfIf}/{partition}
	 */
	@POST
	@Path("{hwconfIf}/{partition}")
	@Produces(JSON_UTF8)
	@ApiOperation(value = "Puts the configuration of a partition to a given hardware configuration.")
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
	 * PUT clonetool/{hwconfIf}/{partition}/{key}/{value}
	 */
	@PUT
	@Path("{hwconfIf}/{partition}/{key}/{value}")
	@Produces(JSON_UTF8)
	@ApiOperation(value = "Sets the value of a key to a given partition." +
			      "The keys may be: OS, Description, Join, Format, Itool" )
	@ApiResponses(value = {
	        @ApiResponse(code = 404, message = "Device not found"),
	        @ApiResponse(code = 500, message = "Server broken, please contact adminstrator")})
	@PermitAll
	Boolean setConfigurationValue(
	        @ApiParam(hidden = true) @Auth Session session,
	        @PathParam("hwconfIf") Long hwconfIf,
	        @PathParam("partition") String partition,
	        @PathParam("key") String key,
	        @PathParam("value") String value
	);
    
}

