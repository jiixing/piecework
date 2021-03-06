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
package piecework.command;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import piecework.ServiceLocator;
import piecework.engine.Mediator;
import piecework.exception.*;

/**
 * @author James Renfro
 */
@Service
class CommandExecutor {

    private static final Logger LOG = Logger.getLogger(CommandExecutor.class);

    @Autowired
    private Mediator mediator;

    @Autowired
    private ServiceLocator serviceLocator;

    public <T, C extends AbstractCommand<T>> T execute(C command) throws BadRequestError, PieceworkException {
        command = mediator.before(command);
        if (command == null)
            throw new PieceworkException("No command provided");

        long start = 0;
        if (LOG.isDebugEnabled()) {
            start = System.currentTimeMillis();
            LOG.debug("Executing " + command.getClass());
        }

        try {
            T result = command.execute(serviceLocator);
            return result;
        } finally {

            if (LOG.isDebugEnabled()) {
                LOG.debug("Command completed in " + (System.currentTimeMillis() - start) + " ms");
            }
        }
    }

    public ServiceLocator getServiceLocator() {
        return serviceLocator;
    }
}
