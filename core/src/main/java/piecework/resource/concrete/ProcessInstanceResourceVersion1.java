/*
 * Copyright 2013 University of Washington
 *
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl1.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package piecework.resource.concrete;

import org.apache.cxf.jaxrs.ext.MessageContext;
import org.apache.cxf.jaxrs.ext.multipart.MultipartBody;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import piecework.AttachmentService;
import piecework.Constants;
import piecework.Versions;
import piecework.authorization.AuthorizationRole;
import piecework.common.ViewContext;
import piecework.exception.ForbiddenError;
import piecework.exception.NotFoundError;
import piecework.exception.PieceworkException;
import piecework.identity.IdentityHelper;
import piecework.model.*;
import piecework.model.Process;
import piecework.persistence.concrete.ExportInstanceProvider;
import piecework.process.AttachmentQueryParameters;
import piecework.process.HistoryFactory;
import piecework.resource.ProcessInstanceResource;
import piecework.security.Sanitizer;
import piecework.security.SecuritySettings;
import piecework.security.concrete.PassthroughSanitizer;
import piecework.service.*;
import piecework.ui.streaming.ExportStreamingOutput;
import piecework.ui.streaming.StreamingAttachmentContent;
import piecework.util.ManyMap;
import piecework.util.ProcessInstanceUtility;

import javax.ws.rs.core.*;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;
import java.util.List;
import java.util.Map;

/**
 * @author James Renfro
 */
@Service
public class ProcessInstanceResourceVersion1 implements ProcessInstanceResource {

    private static final Logger LOG = Logger.getLogger(ProcessInstanceResourceVersion1.class);

    @Autowired
    AttachmentService attachmentService;

    @Autowired
    IdentityHelper helper;

    @Autowired
    HistoryFactory historyFactory;

    @Autowired
    ProcessService processService;

    @Autowired
    ProcessInstanceService processInstanceService;

    @Autowired
    RequestService requestService;

	@Autowired
	Sanitizer sanitizer;

    @Autowired
    SecuritySettings securitySettings;

    @Autowired
    TaskService taskService;

    @Autowired
    ValuesService valuesService;

    @Autowired
    Versions versions;

    @Override
    public Response activate(String rawProcessDefinitionKey, String rawProcessInstanceId, String rawReason) throws PieceworkException {
        processInstanceService.activate(helper.getPrincipal(), rawProcessDefinitionKey, rawProcessInstanceId, rawReason);
        return Response.noContent().build();
    }

    @Override
    public Response activate(String rawProcessDefinitionKey, String rawProcessInstanceId, OperationDetails details) throws PieceworkException {
        String reason = sanitizer.sanitize(details.getReason());
        return activate(rawProcessDefinitionKey, rawProcessInstanceId, reason);
    }

    @Override
    public Response attach(MessageContext context, String rawProcessDefinitionKey, String rawProcessInstanceId, MultivaluedMap<String, String> formData) throws PieceworkException {
        return doAttach(context, rawProcessDefinitionKey, rawProcessInstanceId, formData, Map.class);
    }

    @Override
    public Response attach(MessageContext context, String rawProcessDefinitionKey, String rawProcessInstanceId, MultipartBody body) throws PieceworkException {
        return doAttach(context, rawProcessDefinitionKey, rawProcessInstanceId, body, MultipartBody.class);
    }

    @Override
    public Response attachments(String rawProcessDefinitionKey, String rawProcessInstanceId, AttachmentQueryParameters queryParameters) throws PieceworkException {
        Entity principal = helper.getPrincipal();
        Process process = processService.read(rawProcessDefinitionKey);
        ProcessInstance instance = processInstanceService.read(process, rawProcessInstanceId, true);

        if (!taskService.hasAllowedTask(process, instance, principal, false))
            throw new ForbiddenError();

        SearchResults searchResults = attachmentService.search(instance, queryParameters);
        return Response.ok(searchResults).build();
    }

    @Override
    public Response attachment(String rawProcessDefinitionKey, String rawProcessInstanceId, String rawAttachmentId) throws PieceworkException {
        Entity principal = helper.getPrincipal();
        Process process = processService.read(rawProcessDefinitionKey);
        ProcessInstance instance = processInstanceService.read(process, rawProcessInstanceId, true);
        String attachmentId = sanitizer.sanitize(rawAttachmentId);

        if (!taskService.hasAllowedTask(process, instance, principal, true))
            throw new ForbiddenError();

        StreamingAttachmentContent content = attachmentService.content(process, instance, attachmentId);

        if (content == null)
            throw new NotFoundError(Constants.ExceptionCodes.attachment_does_not_exist, attachmentId);

        String contentDisposition = new StringBuilder("attachment; filename=").append(content.getAttachment().getDescription()).toString();
        return Response.ok(content, content.getAttachment().getContentType()).header("Content-Disposition", contentDisposition).build();
    }

    @Override
    public Response cancel(String rawProcessDefinitionKey, String rawProcessInstanceId, String rawReason) throws PieceworkException {
        processInstanceService.cancel(helper.getPrincipal(), rawProcessDefinitionKey, rawProcessInstanceId, rawReason);
        return Response.noContent().build();
    }

    @Override
    public Response cancel(String rawProcessDefinitionKey, String rawProcessInstanceId, OperationDetails details) throws PieceworkException {
        return cancel(rawProcessDefinitionKey, rawProcessInstanceId, details.getReason());
    }

    @Override
	public Response create(MessageContext context, String rawProcessDefinitionKey, Submission rawSubmission) throws PieceworkException {
        return doCreate(context, rawProcessDefinitionKey, rawSubmission, Submission.class);
	}
	
	@Override
	public Response create(MessageContext context, String rawProcessDefinitionKey, MultivaluedMap<String, String> formData) throws PieceworkException {
        return doCreate(context, rawProcessDefinitionKey, formData, Map.class);
    }

	@Override
	public Response createMultipart(MessageContext context, String rawProcessDefinitionKey, MultipartBody body) throws PieceworkException {
        return doCreate(context, rawProcessDefinitionKey, body, MultipartBody.class);
    }

    @Override
    public Response detach(String rawProcessDefinitionKey, String rawProcessInstanceId, String rawAttachmentId) throws PieceworkException {
        Entity principal = helper.getPrincipal();
        processInstanceService.deleteAttachment(principal, rawProcessDefinitionKey, rawProcessInstanceId, rawAttachmentId);
        return Response.noContent().build();
    }

    @Override
    public Response history(String rawProcessDefinitionKey, String rawProcessInstanceId) throws PieceworkException {
        History history = historyFactory.history(rawProcessDefinitionKey, rawProcessInstanceId);
        return Response.ok(history).build();
    }

    @Override
	public Response read(String rawProcessDefinitionKey, String rawProcessInstanceId) throws PieceworkException {
		String processDefinitionKey = sanitizer.sanitize(rawProcessDefinitionKey);
		String processInstanceId = sanitizer.sanitize(rawProcessInstanceId);
		
		Process process = processService.read(processDefinitionKey);
        ProcessInstance instance = processInstanceService.read(process, processInstanceId, false);

        ProcessInstance.Builder builder = new ProcessInstance.Builder(instance)
                .processDefinitionKey(processDefinitionKey)
                .processDefinitionLabel(process.getProcessDefinitionLabel());

        return Response.ok(builder.build(versions.getVersion1())).build();
	}

    @Override
    public Response suspend(String rawProcessDefinitionKey, String rawProcessInstanceId, String rawReason) throws PieceworkException {
        Entity principal = helper.getPrincipal();
        processInstanceService.suspend(principal, rawProcessDefinitionKey, rawProcessInstanceId, rawReason);
        return Response.noContent().build();
    }

    @Override
    public Response suspend(String rawProcessDefinitionKey, String rawProcessInstanceId, OperationDetails details) throws PieceworkException {
        return suspend(rawProcessDefinitionKey, rawProcessInstanceId, details.getReason());
    }

    @Override
    public Response update(String rawProcessDefinitionKey, String rawProcessInstanceId, ProcessInstance rawInstance) throws PieceworkException {
        Entity principal = helper.getPrincipal();

        ProcessInstance instance = processInstanceService.update(principal, rawProcessDefinitionKey, rawProcessInstanceId, rawInstance);

        ResponseBuilder responseBuilder = Response.status(Status.NO_CONTENT);
        ViewContext context = versions.getVersion1();
        String location = context != null ? context.getApplicationUri(instance.getProcessDefinitionKey(), instance.getProcessInstanceId()) : null;
        if (location != null)
            responseBuilder.location(UriBuilder.fromPath(location).build());
        return responseBuilder.build();
    }

    @Override
	public Response delete(String rawProcessDefinitionKey, String rawProcessInstanceId) throws PieceworkException {
        Entity principal = helper.getPrincipal();

        ProcessInstance instance = processInstanceService.cancel(principal, rawProcessDefinitionKey, rawProcessInstanceId, null);
        ResponseBuilder responseBuilder = Response.status(Status.NO_CONTENT);
        ViewContext context = versions.getVersion1();
        String location = context != null ? context.getApplicationUri(instance.getProcessDefinitionKey(), instance.getProcessInstanceId()) : null;
        if (location != null)
            responseBuilder.location(UriBuilder.fromPath(location).build());
        return responseBuilder.build();
	}

	@Override
	public Response search(MessageContext context) throws PieceworkException {
        Entity principal = helper.getPrincipal();
        UriInfo uriInfo = context.getContext(UriInfo.class);
        List<MediaType> mediaTypes = context.getHttpHeaders().getAcceptableMediaTypes();

		MultivaluedMap<String, String> rawQueryParameters = uriInfo != null ? uriInfo.getQueryParameters() : null;

        if (mediaTypes != null && mediaTypes.contains(new MediaType("text", "csv"))) {
            String fileName = "export.csv";
            ExportInstanceProvider provider = processInstanceService.exportProvider(rawQueryParameters, principal);
            ExportStreamingOutput exportStreamingOutput = new ExportStreamingOutput(provider);
            return Response.ok(exportStreamingOutput, "text/csv").header("Content-Disposition", "attachment; filename=" + fileName).build();
        } else {
            SearchResults results = processInstanceService.search(rawQueryParameters, principal);
            return Response.ok(results).build();
        }
	}

    @Override
    public Response remove(MessageContext context, String rawProcessDefinitionKey, String rawProcessInstanceId, String rawFieldName, String rawValueId) throws PieceworkException {
        Entity principal = helper.getPrincipal();
        RequestDetails requestDetails = new RequestDetails.Builder(context, securitySettings).build();
        valuesService.delete(rawProcessDefinitionKey, rawProcessInstanceId, rawFieldName, rawValueId, requestDetails, principal);
        return Response.noContent().build();
    }

    @Override
    public Response value(String rawProcessDefinitionKey, String rawProcessInstanceId, String rawFieldName, String rawValueId) throws PieceworkException {
        Entity principal = helper.getPrincipal();
        Process process = processService.read(rawProcessDefinitionKey);
        ProcessInstance instance = processInstanceService.read(process, rawProcessInstanceId, false);
        String fieldName = sanitizer.sanitize(rawFieldName);
        String valueId = sanitizer.sanitize(rawValueId);

        if (!principal.hasRole(process, AuthorizationRole.OVERSEER) && !taskService.hasAllowedTask(process, instance, principal, true))
            throw new ForbiddenError(Constants.ExceptionCodes.active_task_required);

        return valuesService.read(instance, fieldName, valueId);
    }

    @Override
    public Response value(MessageContext context, String rawProcessDefinitionKey, String rawProcessInstanceId, String rawFieldName, String value) throws PieceworkException {
        Entity principal = helper.getPrincipal();
        RequestDetails requestDetails = new RequestDetails.Builder(context, securitySettings).build();
        String fieldName = sanitizer.sanitize(rawFieldName);

        ManyMap<String, Value> data = new ManyMap<String, Value>();
        data.putOne(fieldName, new Value(sanitizer.sanitize(value)));
        processInstanceService.updateField(requestDetails, rawProcessDefinitionKey, rawProcessInstanceId, fieldName, data, Map.class, principal);

        return Response.noContent().build();
    }

    @Override
    public Response value(MessageContext context, String rawProcessDefinitionKey, String rawProcessInstanceId, String rawFieldName, MultipartBody body) throws PieceworkException {
        Entity principal = helper.getPrincipal();
        RequestDetails requestDetails = new RequestDetails.Builder(context, securitySettings).build();
        String fieldName = sanitizer.sanitize(rawFieldName);

        ProcessInstance stored = processInstanceService.updateField(requestDetails, rawProcessDefinitionKey, rawProcessInstanceId, fieldName, body, MultipartBody.class, principal);
        return valueLocation(stored, fieldName);
    }

    private Response valueLocation(ProcessInstance stored, String fieldName) {
        Map<String, List<Value>> data = stored.getData();

        ViewContext version1 = versions.getVersion1();
        String location = null;
        if (data != null) {
            File file = ProcessInstanceUtility.firstFile(fieldName, data);

            if (file != null) {
                location = new File.Builder(file, new PassthroughSanitizer())
                                .processDefinitionKey(stored.getProcessDefinitionKey())
                                .processInstanceId(stored.getProcessInstanceId())
                                .fieldName(fieldName)
                                .build(version1)
                                .getLink();
            }
        }

        ResponseBuilder builder = Response.noContent();

        if (location != null)
            builder.header(HttpHeaders.LOCATION, location);

        return builder.build();
    }

    @Override
    public Response values(String rawProcessDefinitionKey, String rawProcessInstanceId, String rawFieldName) throws PieceworkException {
        Entity principal = helper.getPrincipal();
        Process process = processService.read(rawProcessDefinitionKey);
        ProcessInstance instance = processInstanceService.read(process, rawProcessInstanceId, false);
        String fieldName = sanitizer.sanitize(rawFieldName);

        if (!principal.hasRole(process, AuthorizationRole.OVERSEER) && !taskService.hasAllowedTask(process, instance, principal, true))
            throw new ForbiddenError(Constants.ExceptionCodes.active_task_required);

        List<Value> files = valuesService.searchValues(process, instance, fieldName);
        SearchResults searchResults = new SearchResults.Builder()
                .items(files)
                .build();

        return Response.ok(searchResults).build();
    }

    @Override
    public String getVersion() {
        return versions.getVersion1().getVersion();
    }

    private <T> Response doAttach(MessageContext context, String rawProcessDefinitionKey, String rawProcessInstanceId, T data, Class<T> type) throws PieceworkException {

        Entity principal = helper.getPrincipal();
        RequestDetails requestDetails = new RequestDetails.Builder(context, securitySettings).build();

        ProcessInstance instance = processInstanceService.attach(principal, requestDetails, rawProcessDefinitionKey, rawProcessInstanceId, data, type);
        SearchResults searchResults = attachmentService.search(instance, new AttachmentQueryParameters());

        return Response.ok(searchResults).build();
    }

    private <T> Response doCreate(MessageContext context, String rawProcessDefinitionKey, T data, Class<T> type) throws PieceworkException {

        Entity principal = helper.getPrincipal();
        RequestDetails requestDetails = new RequestDetails.Builder(context, securitySettings).build();

        ProcessInstance instance = processInstanceService.create(principal, requestDetails, rawProcessDefinitionKey, data, type);

        return Response.ok(new ProcessInstance.Builder(instance).build(versions.getVersion1())).build();
    }

}
