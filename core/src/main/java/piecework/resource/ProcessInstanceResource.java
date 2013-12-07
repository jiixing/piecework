package piecework.resource;

import org.apache.cxf.jaxrs.ext.MessageContext;
import piecework.ApiResource;
import piecework.ApplicationResource;
import piecework.authorization.AuthorizationRole;
import piecework.model.OperationDetails;
import piecework.model.SearchResults;
import piecework.exception.StatusCodeError;
import piecework.model.ProcessInstance;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.apache.cxf.jaxrs.ext.multipart.MultipartBody;
import piecework.model.Submission;
import piecework.process.AttachmentQueryParameters;

/**
 * @author James Renfro
 */
@Path("instance")
@Produces({"application/xml","application/json"})
public interface ProcessInstanceResource extends ApplicationResource, ApiResource {

    @POST
    @Path("{processDefinitionKey}")
    @RolesAllowed({AuthorizationRole.INITIATOR})
    @Consumes({"application/xml","application/json"})
    Response create(@Context MessageContext context, @PathParam("processDefinitionKey") String processDefinitionKey, Submission submission) throws StatusCodeError;

    @POST
	@Path("{processDefinitionKey}")
	@RolesAllowed({AuthorizationRole.INITIATOR})
	@Consumes("application/x-www-form-urlencoded")
    Response create(@Context MessageContext context, @PathParam("processDefinitionKey") String processDefinitionKey, MultivaluedMap<String, String> formData) throws StatusCodeError;
	
	@POST
	@Path("{processDefinitionKey}")
	@RolesAllowed({AuthorizationRole.INITIATOR})
	@Consumes("multipart/form-data")
	Response createMultipart(@Context MessageContext context, @PathParam("processDefinitionKey") String processDefinitionKey, MultipartBody body) throws StatusCodeError;
	
    @GET
    @Path("{processDefinitionKey}/{processInstanceId}")
    @RolesAllowed({AuthorizationRole.OVERSEER})
    Response read(@PathParam("processDefinitionKey") String processDefinitionKey, @PathParam("processInstanceId") String processInstanceId) throws StatusCodeError;

    @PUT
    @Path("{processDefinitionKey}/{processInstanceId}")
    @RolesAllowed({AuthorizationRole.OVERSEER})
    @Consumes({"application/json", "application/xml"})
    Response update(@PathParam("processDefinitionKey") String processDefinitionKey, @PathParam("processInstanceId") String processInstanceId, ProcessInstance instance) throws StatusCodeError;

    @DELETE
    @Path("{processDefinitionKey}/{processInstanceId}")
    @RolesAllowed({AuthorizationRole.OVERSEER})
    @Consumes({"application/xml","application/json"})
    Response delete(@PathParam("processDefinitionKey") String processDefinitionKey, @PathParam("processInstanceId") String processInstanceId) throws StatusCodeError;

    @GET
    @Path("")
    @RolesAllowed({AuthorizationRole.OVERSEER})
    @Produces({"application/xml","application/json", "text/csv"})
    Response search(@Context MessageContext context) throws StatusCodeError;

    /*
     * SUBRESOURCES
     */
    @POST
    @Path("{processDefinitionKey}/{processInstanceId}/activation")
    @RolesAllowed({AuthorizationRole.USER, AuthorizationRole.OVERSEER})
    @Consumes({"application/x-www-form-urlencoded"})
    Response activate(@PathParam("processDefinitionKey") String processDefinitionKey, @PathParam("processInstanceId") String processInstanceId, @FormParam("reason") String reason) throws StatusCodeError;

    @POST
    @Path("{processDefinitionKey}/{processInstanceId}/activation")
    @RolesAllowed({AuthorizationRole.USER, AuthorizationRole.OVERSEER})
    @Consumes({"application/xml","application/json"})
    Response activate(@PathParam("processDefinitionKey") String processDefinitionKey, @PathParam("processInstanceId") String processInstanceId, OperationDetails reason) throws StatusCodeError;

    @POST
    @Path("{processDefinitionKey}/{processInstanceId}/attachment")
    @RolesAllowed({AuthorizationRole.USER, AuthorizationRole.OVERSEER})
    @Consumes("application/x-www-form-urlencoded")
    Response attach(@Context MessageContext context, @PathParam("processDefinitionKey") String processDefinitionKey, @PathParam("processInstanceId") String processInstanceId, MultivaluedMap<String, String> parameters) throws StatusCodeError;

    @POST
    @Path("{processDefinitionKey}/{processInstanceId}/attachment")
    @RolesAllowed({AuthorizationRole.USER, AuthorizationRole.OVERSEER})
    @Consumes("multipart/form-data")
    Response attach(@Context MessageContext context, @PathParam("processDefinitionKey") String processDefinitionKey, @PathParam("processInstanceId") String processInstanceId, MultipartBody body) throws StatusCodeError;

    @GET
    @Path("{processDefinitionKey}/{processInstanceId}/attachment")
    @RolesAllowed({AuthorizationRole.USER, AuthorizationRole.OVERSEER})
    Response attachments(@PathParam("processDefinitionKey") String processDefinitionKey, @PathParam("processInstanceId") String processInstanceId, @QueryParam("") AttachmentQueryParameters queryParameters) throws StatusCodeError;

    @GET
    @Path("{processDefinitionKey}/{processInstanceId}/attachment/{attachmentId}")
    @RolesAllowed({AuthorizationRole.USER, AuthorizationRole.OVERSEER})
    Response attachment(@PathParam("processDefinitionKey") String processDefinitionKey, @PathParam("processInstanceId") String processInstanceId, @PathParam("attachmentId") String attachmentId) throws StatusCodeError;

    @POST
    @Path("{processDefinitionKey}/{processInstanceId}/cancellation")
    @RolesAllowed({AuthorizationRole.USER, AuthorizationRole.OVERSEER})
    @Consumes("application/x-www-form-urlencoded")
    Response cancel(@PathParam("processDefinitionKey") String processDefinitionKey, @PathParam("processInstanceId") String processInstanceId, @FormParam("reason") String reason) throws StatusCodeError;

    @POST
    @Path("{processDefinitionKey}/{processInstanceId}/cancellation")
    @RolesAllowed({AuthorizationRole.USER, AuthorizationRole.OVERSEER})
    @Consumes({"application/xml","application/json"})
    Response cancel(@PathParam("processDefinitionKey") String processDefinitionKey, @PathParam("processInstanceId") String processInstanceId, OperationDetails reason) throws StatusCodeError;

    @DELETE
    @Path("{processDefinitionKey}/{processInstanceId}/attachment/{attachmentId}")
    @RolesAllowed({AuthorizationRole.USER, AuthorizationRole.OVERSEER})
    Response detach(@PathParam("processDefinitionKey") String processDefinitionKey, @PathParam("processInstanceId") String processInstanceId, @PathParam("attachmentId") String attachmentId) throws StatusCodeError;

    @GET
    @Path("{processDefinitionKey}/{processInstanceId}/history")
    @RolesAllowed({AuthorizationRole.USER, AuthorizationRole.OVERSEER})
    Response history(@PathParam("processDefinitionKey") String processDefinitionKey, @PathParam("processInstanceId") String processInstanceId) throws StatusCodeError;

    @DELETE
    @Path("{processDefinitionKey}/{processInstanceId}/value/{fieldName}/{valueId}")
    @RolesAllowed({AuthorizationRole.USER, AuthorizationRole.OVERSEER})
    Response remove(@PathParam("processDefinitionKey") String processDefinitionKey, @PathParam("processInstanceId") String processInstanceId, @PathParam("fieldName") String fieldName, @PathParam("valueId") String valueId) throws StatusCodeError;

    @POST
    @Path("{processDefinitionKey}/{processInstanceId}/suspension")
    @RolesAllowed({AuthorizationRole.USER, AuthorizationRole.OVERSEER})
    @Consumes({"application/x-www-form-urlencoded"})
    Response suspend(@PathParam("processDefinitionKey") String processDefinitionKey, @PathParam("processInstanceId") String processInstanceId, @FormParam("reason") String reason) throws StatusCodeError;

    @POST
    @Path("{processDefinitionKey}/{processInstanceId}/suspension")
    @RolesAllowed({AuthorizationRole.USER, AuthorizationRole.OVERSEER})
    @Consumes({"application/xml","application/json"})
    Response suspend(@PathParam("processDefinitionKey") String processDefinitionKey, @PathParam("processInstanceId") String processInstanceId, OperationDetails reason) throws StatusCodeError;

    @GET
    @Path("{processDefinitionKey}/{processInstanceId}/value/{fieldName}/{valueId}")
    @RolesAllowed({AuthorizationRole.USER, AuthorizationRole.OVERSEER})
    Response value(@PathParam("processDefinitionKey") String processDefinitionKey, @PathParam("processInstanceId") String processInstanceId, @PathParam("fieldName") String fieldName, @PathParam("valueId") String valueId) throws StatusCodeError;

    @POST
    @Path("{processDefinitionKey}/{processInstanceId}/value/{fieldName}")
    @RolesAllowed({AuthorizationRole.USER, AuthorizationRole.OVERSEER})
    @Consumes("text/plain")
    Response value(@Context MessageContext context, @PathParam("processDefinitionKey") String processDefinitionKey, @PathParam("processInstanceId") String processInstanceId, @PathParam("fieldName") String fieldName, String value) throws StatusCodeError;

    @POST
    @Path("{processDefinitionKey}/{processInstanceId}/value/{fieldName}")
    @RolesAllowed({AuthorizationRole.USER, AuthorizationRole.OVERSEER})
    @Consumes("multipart/form-data")
    Response value(@Context MessageContext context, @PathParam("processDefinitionKey") String processDefinitionKey, @PathParam("processInstanceId") String processInstanceId, @PathParam("fieldName") String fieldName, MultipartBody body) throws StatusCodeError;

    @GET
    @Path("{processDefinitionKey}/{processInstanceId}/value/{fieldId}")
    @RolesAllowed({AuthorizationRole.USER, AuthorizationRole.OVERSEER})
    Response values(@PathParam("processDefinitionKey") String processDefinitionKey, @PathParam("processInstanceId") String processInstanceId, @PathParam("fieldId") String fieldId) throws StatusCodeError;

}
