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
package piecework.form.handler;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;
import piecework.common.ViewContext;
import piecework.form.FormFactory;
import piecework.form.FormService;
import piecework.form.validation.ValidationService;
import piecework.process.ProcessInstanceService;
import piecework.exception.InternalServerError;
import piecework.exception.StatusCodeError;
import piecework.form.validation.FormValidation;
import piecework.model.*;
import piecework.model.Process;
import piecework.persistence.ProcessRepository;
import piecework.ui.StreamingPageContent;
import piecework.persistence.ContentRepository;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import java.io.IOException;


/**
 * @author James Renfro
 */
@Service
public class ResponseHandler {

    private static final Logger LOG = Logger.getLogger(ResponseHandler.class);

    @Autowired
    ContentRepository contentRepository;

    @Autowired
    Environment environment;

    @Autowired
    FormFactory formFactory;

    @Autowired
    FormService formService;

    @Autowired
    ProcessRepository processRepository;

    @Autowired
    ProcessInstanceService processInstanceService;

    @Autowired
    ValidationService validationService;


    public Response handle(FormRequest formRequest, Process process) throws StatusCodeError {
        return handle(formRequest, process, null, null);
    }

    public Response handle(FormRequest formRequest, Process process, Task task, FormValidation validation) throws StatusCodeError {

        Form form = formFactory.form(formRequest, process, task, validation);

        if (form != null && form.getScreen() != null && !form.getScreen().isReadonly()) {
            String location = form.getScreen().getLocation();

            if (StringUtils.isNotEmpty(location)) {
                // If the location is not blank then retrieve from that location
                Content content;
                if (location.startsWith("classpath:")) {
                    ClassPathResource resource = new ClassPathResource(location.substring("classpath:".length()));
                    try {
                        content = new Content.Builder().inputStream(resource.getInputStream()).contentType("text/html").build();
                    } catch (IOException e) {
                        throw new InternalServerError();
                    }
                } else if (location.startsWith("file:")) {
                    FileSystemResource resource = new FileSystemResource(location.substring("file:".length()));
                    try {
                        content = new Content.Builder().inputStream(resource.getInputStream()).contentType("text/html").build();
                    } catch (IOException e) {
                        throw new InternalServerError();
                    }
                } else {
                    content = contentRepository.findByLocation(location);
                }
                return Response.ok(new StreamingPageContent(form, content), content.getContentType()).build();
            }
        }

        return Response.ok(form).build();
    }

    public Response redirect(FormRequest formRequest, ViewContext viewContext) throws StatusCodeError {
        String hostUri = environment.getProperty("host.uri");
        return Response.status(Response.Status.SEE_OTHER).header(HttpHeaders.LOCATION, hostUri + formService.getFormViewContext().getApplicationUri(formRequest.getProcessDefinitionKey(), formRequest.getRequestId())).build();
    }

}
