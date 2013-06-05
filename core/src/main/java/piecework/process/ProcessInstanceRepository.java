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
package piecework.process;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import piecework.model.*;

import java.util.List;
import java.util.Set;

/**
 * @author James Renfro
 */
public interface ProcessInstanceRepository extends MongoRepository<ProcessInstance, String> {

    Set<ProcessInstance> findByKeywordsRegex(String keyword);

    @Query(value="{ 'processInstanceId' : { $in: ?0 }, 'keywords' : { $regex: ?1 } }")
    Set<ProcessInstance> findByProcessInstanceIdInAndKeywordsRegex(Iterable<String> processInstanceIds, String keyword);

}