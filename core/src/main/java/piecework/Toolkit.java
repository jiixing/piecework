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
package piecework;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import piecework.engine.ProcessEngineFacade;
import piecework.identity.IdentityService;
import piecework.persistence.ProcessInstanceRepository;
import piecework.identity.IdentityHelper;

/**
 * @author James Renfro
 */
@Service
public class Toolkit {

    private static final Logger LOG = Logger.getLogger(Toolkit.class);

    @Autowired
    Environment environment;

    @Autowired
    ProcessEngineFacade facade;

    @Autowired
    IdentityHelper helper;

    @Autowired
    IdentityService identityService;

    @Autowired
    MongoTemplate mongoOperations;

    @Autowired
    ProcessInstanceRepository processInstanceRepository;

    public Environment getEnvironment() {
        return environment;
    }

    public ProcessEngineFacade getFacade() {
        return facade;
    }

    public IdentityHelper getHelper() {
        return helper;
    }

    public IdentityService getIdentityService() {
        return identityService;
    }

    public MongoTemplate getMongoOperations() {
        return mongoOperations;
    }

    public ProcessInstanceRepository getProcessInstanceRepository() {
        return processInstanceRepository;
    }
}