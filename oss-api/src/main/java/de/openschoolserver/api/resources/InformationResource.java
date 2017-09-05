/* (c) 2017 Peter Varkoly <peter@varkoly.de> - all rights reserved */
package de.openschoolserver.api.resources;

import static de.openschoolserver.api.resources.Resource.JSON_UTF8;


import java.util.List;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import de.openschoolserver.dao.Announcement;
import de.openschoolserver.dao.Category;
import de.openschoolserver.dao.Contact;
import de.openschoolserver.dao.FAQ;
import de.openschoolserver.dao.Response;
import de.openschoolserver.dao.Session;
import io.dropwizard.auth.Auth;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;


@Path("informations")
@Api(value = "informations")
public interface InformationResource {
	
	@POST
    @Path("announcements")
    @Produces(JSON_UTF8)
    @ApiOperation(value = "Creates a ne announcement.")
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Server broken, please contact administrator")
    })
	@RolesAllowed("information.add")
    Response addAnnouncement(
    		@ApiParam(hidden = true) @Auth Session session,
    		Announcement annoncement
    );
    
	@POST
    @Path("contacts")
    @Produces(JSON_UTF8)
    @ApiOperation(value = "Creates a new contact.")
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Server broken, please contact administrator")
    })
	@RolesAllowed("information.add")
    Response addContact(
    		@ApiParam(hidden = true) @Auth Session session,
    		Contact contact
    );
    
	@POST
    @Path("faqs")
    @Produces(JSON_UTF8)
    @ApiOperation(value = "Creates a new FAQ.")
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Server broken, please contact administrator")
    })
	@RolesAllowed("information.add")
    Response addFAQ(
    		@ApiParam(hidden = true) @Auth Session session,
    		FAQ faq
    );
   
	@GET
    @Path("announcements")
    @Produces(JSON_UTF8)
    @ApiOperation(value = "Gets the contacts corresponding to an user.")
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Server broken, please contact administrator")
    })
    @PermitAll
    List<Announcement> getAnnouncements(
    		@ApiParam(hidden = true) @Auth Session session
    );
    
	@GET
    @Path("contacts")
    @Produces(JSON_UTF8)
    @ApiOperation(value = "Gets the contacts corresponding to an user.")
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Server broken, please contact administrator")
    })
    @PermitAll
    List<Contact> getContacts(
    		@ApiParam(hidden = true) @Auth Session session
    );
    
	@GET
    @Path("faqs")
    @Produces(JSON_UTF8)
    @ApiOperation(value = "Gets the FAQs corresponding to an user.")
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Server broken, please contact administrator")
    })
    @PermitAll
    List<FAQ> getFAQs(
    		@ApiParam(hidden = true) @Auth Session session
    );

	@POST
    @Path("announcements/{announcementId}")
    @Produces(JSON_UTF8)
    @ApiOperation(value = "Deletes an announcement.")
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Server broken, please contact administrator")
    })
	@RolesAllowed("information.delete")
    Response modifyAnnouncement(
    		@ApiParam(hidden = true) @Auth Session session,
    		@PathParam("announcementId") Long announcementId,
    		Announcement announcement
    );
    
	@POST
    @Path("contacts/{contactId}")
    @Produces(JSON_UTF8)
    @ApiOperation(value = "Deletes a contact.")
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Server broken, please contact administrator")
    })
	@RolesAllowed("information.delete")
    Response modifyContact(
    		@ApiParam(hidden = true) @Auth Session session,
    		@PathParam("contactId") Long contactId,
    		Contact contact
    );
    
	@POST
    @Path("faqs/{faqId}")
    @Produces(JSON_UTF8)
    @ApiOperation(value = "Delets a FAQ.")
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Server broken, please contact administrator")
    })
	@RolesAllowed("information.delete")
    Response modifyFAQ(
    		@ApiParam(hidden = true) @Auth Session session,
    		@PathParam("faqId") Long faqId,
    		FAQ faq
    );

	@DELETE
    @Path("announcements/{announcementId}")
    @Produces(JSON_UTF8)
    @ApiOperation(value = "Deletes an announcement.")
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Server broken, please contact administrator")
    })
	@RolesAllowed("information.delete")
    Response deleteAnnouncement(
    		@ApiParam(hidden = true) @Auth Session session,
    		@PathParam("announcementId") Long announcementId
    );
    
	@DELETE
    @Path("contacts/{contactId}")
    @Produces(JSON_UTF8)
    @ApiOperation(value = "Deletes a contact.")
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Server broken, please contact administrator")
    })
	@RolesAllowed("information.delete")
    Response deleteContact(
    		@ApiParam(hidden = true) @Auth Session session,
    		@PathParam("contactId") Long contactId
    );
    
	@DELETE
    @Path("faqs/{faqId}")
    @Produces(JSON_UTF8)
    @ApiOperation(value = "Delets a FAQ.")
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Server broken, please contact administrator")
    })
	@RolesAllowed("information.delete")
    Response deleteFAQ(
    		@ApiParam(hidden = true) @Auth Session session,
    		@PathParam("faqId") Long faqId
    );

	@GET
    @Path("announcements/categories")
    @Produces(JSON_UTF8)
    @ApiOperation(value = "Gets the contacts corresponding to an user.")
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Server broken, please contact administrator")
    })
    @PermitAll
    List<Category> getAnnouncementCategories(
    		@ApiParam(hidden = true) @Auth Session session
    );
    
	@GET
    @Path("contacts/categories")
    @Produces(JSON_UTF8)
    @ApiOperation(value = "Gets the contacts corresponding to an user.")
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Server broken, please contact administrator")
    })
    @PermitAll
    List<Category> getContactsCategories(
    		@ApiParam(hidden = true) @Auth Session session
    );
    
	@GET
    @Path("faqs/cagegories")
    @Produces(JSON_UTF8)
    @ApiOperation(value = "Gets the FAQs corresponding to an user.")
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Server broken, please contact administrator")
    })
    @PermitAll
    List<Category> getFAQCategories(
    		@ApiParam(hidden = true) @Auth Session session
    );
	
}
