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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import piecework.common.ViewContext;
import piecework.exception.StatusCodeError;
import piecework.identity.InternalUserDetails;
import piecework.identity.InternalUserDetailsService;
import piecework.identity.PersonSearchCriteria;
import piecework.model.ProcessInstance;
import piecework.model.SearchResults;
import piecework.model.User;
import piecework.resource.PersonResource;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import java.util.List;

/**
 * @author James Renfro
 */
@Service
public class PersonResourceVersion1 implements PersonResource {

    @Autowired
    Environment environment;

    @Autowired
    InternalUserDetailsService userDetailsService;

    @Override
    public SearchResults search(PersonSearchCriteria criteria) throws StatusCodeError {

        SearchResults.Builder resultsBuilder = new SearchResults.Builder()
                .resourceLabel("People")
                .resourceName(ProcessInstance.Constants.ROOT_ELEMENT_NAME)
                .link(getViewContext().getApplicationUri());

        List<InternalUserDetails> userDetails = userDetailsService.findUsersByDisplayName(criteria.getDisplayNameLike());
        if (userDetails != null) {
            for (InternalUserDetails userDetail : userDetails) {
                resultsBuilder.item(new User.Builder(userDetail).build());
            }
        }

        return resultsBuilder.build();
    }

    @Override
    public String getVersion() {
        return "v1";
    }

    @Override
    public ViewContext getViewContext() {
        String baseApplicationUri = environment.getProperty("base.application.uri");
        String baseServiceUri = environment.getProperty("base.service.uri");
        return new ViewContext(baseApplicationUri, baseServiceUri, getVersion(), "instance", "Instance");
    }
}