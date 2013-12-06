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
package piecework.identity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import piecework.authorization.DebugAccessAuthority;
import piecework.service.ProcessService;

import javax.annotation.PostConstruct;
import java.util.Collections;

/**
 * @author James Renfro
 */
public class DebugUserDetailsService implements UserDetailsService {

    @Autowired
    Environment environment;

    @Autowired
    ProcessService processService;

    private DebugAccessAuthority accessAuthority;

    @PostConstruct
    public void init() {
        this.accessAuthority = new DebugAccessAuthority(processService);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        String testUser = environment.getProperty("authentication.testuser");
        String id = username;
        String displayName = username;

        if (testUser != null && testUser.equals(id))
            displayName = environment.getProperty("authentication.testuser.displayName");

        UserDetails delegate = new org.springframework.security.core.userdetails.User(id, "none",
                Collections.singletonList(accessAuthority));
        return new IdentityDetails(delegate, id, id, displayName, "");
    }

}
