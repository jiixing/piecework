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
package piecework.service;

import piecework.model.Group;

/**
 * @author Jiefeng Shen
 */
public interface GroupService {
     /** 
     * returns a group uniquely identified by a group ID. 
     * The unique identifier could be a group ID or a group name, as long as it
     * uniquely identifies a group. 
     * @param  groupId an Id (or a name) that unqiuely identifies a group.
     * @return         a Group object if found, null otherwise.
     */  
    public Group getGroupById(String groupId);
}
