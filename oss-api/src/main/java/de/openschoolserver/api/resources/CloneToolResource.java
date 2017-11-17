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

import io.dropwizard.auth.Auth;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import de.openschoolserver.dao.Clone;
import de.openschoolserver.dao.HWConf;
import de.openschoolserver.dao.OssResponse;
import de.openschoolserver.dao.Partition;
import de.openschoolserver.dao.Session;
import java.util.List;

@Path("clonetool")
@Api(value = "clonetool")
public interface CloneToolResource {
  
	/*
	 * Get clonetool/hwconf
	 */
	@GET
	@Path("hwconf")
	@Produces("text/plain")
	@ApiOperation(value = "Gets the id of the hardware configuration based on the IP-address of the session.")
	@ApiResponses(value = {
	        @ApiResponse(code = 404, message = "Device not found"),
	        @ApiResponse(code = 500, message = "Server broken, please contact adminstrator")})
	@PermitAll
	String getHWConf(
	        @ApiParam(hidden = true) @Auth Session session
	);
  
	/*
	 * Get clonetool/isMaster
	 */
	@GET
	@Path("isMaster")
	@Produces("text/plain")
	@ApiOperation(value = "Returns 'true' if the workstation of the session is master. Returns empty if not.")
	@ApiResponses(value = {
	        @ApiResponse(code = 404, message = "Device not found"),
	        @ApiResponse(code = 500, message = "Server broken, please contact adminstrator")})
	@PermitAll
	String isMaster(
	        @ApiParam(hidden = true) @Auth Session session
	);
  
	/*
	 * Get clonetool/devices/{deviceId}/isMaster
	 */
	@GET
	@Path("devices/{deviceId}/isMaster")
	@Produces("text/plain")
	@ApiOperation(value = "Returns 'true' if the workstation of the deviceId is master. Returns empty if not.")
	@ApiResponses(value = {
	        @ApiResponse(code = 404, message = "Device not found"),
	        @ApiResponse(code = 500, message = "Server broken, please contact adminstrator")})
	@PermitAll
	String isMaster(
	        @ApiParam(hidden = true) @Auth Session session,
	        @PathParam("deviceId") Long deviceId
	);
  
	/*
	 * Get clonetool/devices/{deviceId}/setMaster/{isMaster}
	 */
	@PUT
	@Path("devices/{deviceId}/setMaster/{isMaster}")
	@Produces(JSON_UTF8)
	@ApiOperation(value = "Returns 'true' if the workstation of the session is master. Returns empty if not.")
	@ApiResponses(value = {
	        @ApiResponse(code = 404, message = "Device not found"),
	        @ApiResponse(code = 500, message = "Server broken, please contact adminstrator")})
	@RolesAllowed("device.manage")
	OssResponse setMaster(
	        @ApiParam(hidden = true) @Auth Session session,
	        @PathParam("deviceId") Long deviceId,
	        @PathParam("isMaster") int isMaster
	);
  
	/*
	 * Get clonetool/devices/{deviceIisMaster
	 */
	@PUT
	@Path("devices/setMaster/{isMaster}")
	@Produces(JSON_UTF8)
	@ApiOperation(value = "Returns 'true' if the workstation of the session is master. Returns empty if not.")
	@ApiResponses(value = {
	        @ApiResponse(code = 404, message = "Device not found"),
	        @ApiResponse(code = 500, message = "Server broken, please contact adminstrator")})
	@RolesAllowed("device.manage")
	OssResponse setMaster(
	        @ApiParam(hidden = true) @Auth Session session,
	        @PathParam("isMaster") int isMaster
	);
  
	/*
	 * Get clonetool/all
	 */
	@GET
	@Path("all")
	@Produces(JSON_UTF8)
	@ApiOperation(value = "Gets all hardware configuration.")
	@ApiResponses(value = {
	        @ApiResponse(code = 404, message = "Device not found"),
	        @ApiResponse(code = 500, message = "Server broken, please contact adminstrator")})
	@RolesAllowed("device.add")
	List<HWConf> getAllHWConf(
	        @ApiParam(hidden = true) @Auth Session session
	);
       
	/*
	 * Get clonetool/{hwconfId}
	 */
	@GET
	@Path("{hwconfId}")
	@Produces(JSON_UTF8)
	@ApiOperation(value = "Gets a hardware configuration.")
	@ApiResponses(value = {
	        @ApiResponse(code = 404, message = "Device not found"),
	        @ApiResponse(code = 500, message = "Server broken, please contact adminstrator")})
	@RolesAllowed("device.add")
	HWConf getById(
	        @ApiParam(hidden = true) @Auth Session session,
	        @PathParam("hwconfId") Long hwconfId
	);
       
	/*
     * GET clonetool/{hwconfId}/description
    */
	@GET
	@Path("{hwconfId}/description")
	@Produces("text/plain")
	@ApiOperation(value = "Gets the description of a hardware configuration.")
	@ApiResponses(value = {
	        @ApiResponse(code = 404, message = "Device not found"),
	        @ApiResponse(code = 500, message = "Server broken, please contact adminstrator")})
	@RolesAllowed("device.manage")
	String getDescription(
	        @ApiParam(hidden = true) @Auth Session session,
	        @PathParam("hwconfId") Long hwconfId
	);
	
	/*
     * GET clonetool/{hwconfId}/partitions
    */
	@GET
	@Path("{hwconfId}/partitions")
	@Produces("text/plain")
	@ApiOperation(value = "Gets a space separated list of recorded partitions to a given hardware configuration.")
	@ApiResponses(value = {
	        @ApiResponse(code = 404, message = "Device not found"),
	        @ApiResponse(code = 500, message = "Server broken, please contact adminstrator")})
	@RolesAllowed("device.manage")
	String getPartitions(
	        @ApiParam(hidden = true) @Auth Session session,
	        @PathParam("hwconfId") Long hwconfId
	);
    
	/*
	 * Get clonetool/{hwconfId}/{partitionName}
	 */
	@GET
	@Path("{hwconfId}/{partitionName}")
	@Produces(JSON_UTF8)
	@ApiOperation(value = "Gets the configuration of a partition to a given hardware configuration.")
	@ApiResponses(value = {
	        @ApiResponse(code = 404, message = "Device not found"),
	        @ApiResponse(code = 500, message = "Server broken, please contact adminstrator")})
	@RolesAllowed("device.manage")
	Partition getPartition(
	        @ApiParam(hidden = true) @Auth Session session,
	        @PathParam("hwconfId") Long hwconfId,
	        @PathParam("partitionName") String partitionName
	);

	/*
	 * GET clonetool/{hwconfId}/{partitionName}/{key}
	 */
	@GET
	@Path("{hwconfId}/{partitionName}/{key}")
	@Produces("text/plain")
	@ApiOperation(value = "Gets the value of a key to a given partition." +
			      "The key may be: OS, Description, Join, Format, Itool" )
	@ApiResponses(value = {
	        @ApiResponse(code = 404, message = "Device not found"),
	        @ApiResponse(code = 500, message = "Server broken, please contact adminstrator")})
	@RolesAllowed("device.manage")
	String getConfigurationValue(
	        @ApiParam(hidden = true) @Auth Session session,
	        @PathParam("hwconfId") Long hwconfId,
	        @PathParam("partitionName") String partitionName,
	        @PathParam("key") String key
	);

	/*
	 * GET clonetool/roomsToRegister
	 */
	@GET
	@Path("roomsToRegister")
	@Produces("text/plain")
	@ApiOperation(value = "Gets a list of rooms to register." +
			      "The format is id name##id name" )
	@ApiResponses(value = {
	        @ApiResponse(code = 404, message = "Device not found"),
	        @ApiResponse(code = 500, message = "Server broken, please contact adminstrator")})
	@RolesAllowed("device.manage")
	String getRoomsToRegister(
	        @ApiParam(hidden = true) @Auth Session session
	);

	// POST and PUSH methodes.

	/*
	 * POST clonetool/ { hwconf hash }
	 */
	@POST
	@Path("hwconf")
	@Produces(JSON_UTF8)
	@ApiOperation(value = "Creates a new hardware configuration. And returns the hwoconfId")
	@ApiResponses(value = {
	        @ApiResponse(code = 404, message = "Device not found"),
	        @ApiResponse(code = 500, message = "Server broken, please contact adminstrator")})
	@RolesAllowed("device.add")
	OssResponse addHWConf(
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
	@RolesAllowed("device.add")
	OssResponse modifyHWConf(
	        @ApiParam(hidden = true) @Auth Session session,
	        @PathParam("hwconfId") Long hwconfId,
		    HWConf hwconf
	);
    
	/*
	 * PUT clonetool/{hwconfId}/{partition}
	 */
	@PUT
	@Path("{hwconfId}/{partitionName}")
	@Produces(JSON_UTF8)
	@ApiOperation(value = "Create a new not configured partition to a given hardware configuration." +
						  "Only the name (sdaXXX) is given. The other parameter must be set with an other put calls." )
	@ApiResponses(value = {
	        @ApiResponse(code = 404, message = "Device not found"),
	        @ApiResponse(code = 500, message = "Server broken, please contact adminstrator")})
	@RolesAllowed("device.add")
	OssResponse addPartition(
	        @ApiParam(hidden = true) @Auth Session session,
	        @PathParam("hwconfId") Long hwconfId,
	        @PathParam("partitionName") String partitionName
	);

	/*
	 * POST clonetool/{hwconfId}/addPartition
	 */
	@POST
	@Path("{hwconfId}/addPartition")
	@Produces(JSON_UTF8)
	@ApiOperation(value = "Create a new partition to a given hardware configuration.")
	@ApiResponses(value = {
	        @ApiResponse(code = 404, message = "Device not found"),
	        @ApiResponse(code = 500, message = "Server broken, please contact adminstrator")})
	@RolesAllowed("device.add")
	OssResponse addPartition(
	        @ApiParam(hidden = true) @Auth Session session,
	        @PathParam("hwconfId") Long hwconfId,
	        Partition partition
	);

	/*
	 * PUT clonetool/{hwconfId}/{partitionName}/{key}/{value}
	 */
	@PUT
	@Path("{hwconfId}/{partitionName}/{key}/{value}")
	@Produces(JSON_UTF8)
	@ApiOperation(value = "Sets the value of a key to a given partition." +
			      "The keys may be: OS, Description, Join, Format, Itool" )
	@ApiResponses(value = {
	        @ApiResponse(code = 404, message = "Device not found"),
	        @ApiResponse(code = 500, message = "Server broken, please contact adminstrator")})
	@RolesAllowed("device.add")
	OssResponse setConfigurationValue(
	        @ApiParam(hidden = true) @Auth Session session,
	        @PathParam("hwconfId") Long hwconfId,
	        @PathParam("partitionName") String partitionName,
	        @PathParam("key") String key,
	        @PathParam("value") String value
	);
    
	/*
	 * DELETE clonetool/{hwconfId}
	 */
	@DELETE
	@Path("{hwconfId}")
	@Produces(JSON_UTF8)
	@ApiOperation(value = "Updates a hardware configuration.")
	@ApiResponses(value = {
	        @ApiResponse(code = 404, message = "Device not found"),
	        @ApiResponse(code = 500, message = "Server broken, please contact adminstrator")})
	@RolesAllowed("device.delete")
	OssResponse delete(
	        @ApiParam(hidden = true) @Auth Session session,
	        @PathParam("hwconfId") Long hwconfId
	);

	/*
	 * DELETE clonetool/{hwconfId}/{partitionName}
	 */
	@DELETE
	@Path("{hwconfId}/{partitionName}")
	@Produces(JSON_UTF8)
	@ApiOperation(value = "Delets a partition to a given hardware configuration.")
	@ApiResponses(value = {
	        @ApiResponse(code = 404, message = "Device not found"),
	        @ApiResponse(code = 500, message = "Server broken, please contact adminstrator")})
	@RolesAllowed("device.add")
	OssResponse deletePartition(
	        @ApiParam(hidden = true) @Auth Session session,
	        @PathParam("hwconfId") Long hwconfId,
	        @PathParam("partitionName") String partitionName
	);

	/*
	 * DELETE clonetool/{hwconfId}/{partitionName}/{key}
	 */
	@DELETE
	@Path("{hwconfId}/{partitionName}/{key}")
	@Produces(JSON_UTF8)
	@ApiOperation(value = "Delets a key an the correspondig value from a partition." +
			      "The key may be: OS, Description, Join, Format, Itool" )
	@ApiResponses(value = {
	        @ApiResponse(code = 404, message = "Device not found"),
	        @ApiResponse(code = 500, message = "Server broken, please contact adminstrator")})
	@RolesAllowed("device.add")
	OssResponse deleteConfigurationValue(
	        @ApiParam(hidden = true) @Auth Session session,
	        @PathParam("hwconfId") Long hwconfId,
	        @PathParam("partitionName") String partitionName,
	        @PathParam("key") String key
	);
	
	/*
	 * 
	 */
	@POST
	@Path("{hwconfId}/cloning")
	@Produces(JSON_UTF8)
	@ApiOperation(value = "Creates the boot configuration for the automatical partitioning." +
						"This call have to provide a hash with following informations" +
						" devices    : [ IDs of devices ] " +
						" partitions : [ IDs of partitions ] " +
						" multicast  :  true/fals"
						)
	@RolesAllowed("device.add")
	OssResponse startCloning(
			@ApiParam(hidden = true) @Auth Session session,
			@PathParam("hwconfId") Long hwconfId,
			Clone parameters
			);

	/*
	 * 
	 */
	@DELETE
	@Path("{hwconfId}/cloning")
	@Produces(JSON_UTF8)
	@ApiOperation(value = "Removes the boot configuration for the automatical partitioning.")
	@RolesAllowed("device.add")
	OssResponse stopCloning(
			@ApiParam(hidden = true) @Auth Session session,
			@PathParam("hwconfId") Long hwconfId
			);

}
