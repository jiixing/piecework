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
package piecework.exception;

import piecework.model.Form;

import java.net.URI;

/**
 * @author James Renfro
 */
@Deprecated
public class InternalFormException extends FormBuildingException {

    private final Form form;
    private final String location;

    public InternalFormException(Form form, String location) {
        this.form = form;
        this.location = location;
    }

    public Form getForm() {
        return form;
    }

    public String getLocation() {
        return location;
    }
}
