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
package piecework.test;

import piecework.Constants;
import piecework.model.*;
import piecework.model.Process;

import java.io.ByteArrayInputStream;
import java.util.UUID;

/**
 * @author James Renfro
 */
public class ExampleFactory {

    public static Content exampleContent(String root) {
        return new Content.Builder()
                .contentType("text/plain")
                .location(root + "/" + UUID.randomUUID().toString())
                .inputStream(new ByteArrayInputStream("This is a test".getBytes()))
                .build();
    }

    public static FormRequest exampleFormRequest(String formInstanceId) {
        return new FormRequest.Builder()
                .requestId(formInstanceId)
                .screen(exampleScreenWithTwoSections(Constants.ScreenTypes.WIZARD))
                .submissionType(Constants.SubmissionTypes.INTERIM)
                .build();
    }


    public static Field employeeNameField() {
        return new Field.Builder()
                .type(Constants.FieldTypes.TEXT)
                .label("Employee")
                .name("employeeName")
                .maxValueLength(40)
                .editable()
                .required()
                .build();
    }

    public static Field budgetNumberField() {
        return new Field.Builder()
                .type(Constants.FieldTypes.NUMBER)
                .label("Budget number")
                .name("budgetNumber")
                .defaultValue("100000")
                .constraint(new Constraint.Builder().type(Constants.ConstraintTypes.IS_NUMERIC).build())
                .maxValueLength(20)
                .editable()
                .required()
                .build();
    }

    public static Field supervisorIdField() {
        return new Field.Builder()
                .type(Constants.FieldTypes.TEXT)
                .label("Manager id")
                .name("supervisorId")
                .pattern("[a-z]{3}[0-9]{2}")
                .customValidity("Must be three lower-case letters, followed by two digits")
                .constraint(new Constraint.Builder().type(Constants.ConstraintTypes.IS_VALID_USER).build())
                .constraint(new Constraint.Builder()
                        .type(Constants.ConstraintTypes.IS_ONLY_REQUIRED_WHEN)
                        .name("budgetNumber")
                        .value("^100000$")
                        .build())
                .constraint(new Constraint.Builder()
                        .type(Constants.ConstraintTypes.IS_ONLY_VISIBLE_WHEN)
                                .name("budgetNumber")
                                .value("^100001$")
                                .or(new Constraint.Builder()
                                    .type(Constants.ConstraintTypes.IS_ONLY_VISIBLE_WHEN)
                                    .name("action")
                                    .value("^(bonus|promote)$")
                                    .build())
                        .build())
                .maxValueLength(40)
                .editable()
                .required()
                .build();
    }

    public static Field actionTypeField() {
        return new Field.Builder()
                .type(Constants.FieldTypes.SELECT_MULTIPLE)
                .label("Action to take")
                .name("action")
                .option(new Option.Builder().label("Grant bonus").value("bonus").build())
                .option(new Option.Builder().label("Reprimand").value("reprimand").build())
                .option(new Option.Builder().label("Promote").value("promote").build())
                .option(new Option.Builder().label("Demote").value("demote").build())
                .defaultValue("bonus")
                .editable()
                .required()
                .build();
    }

    public static Field locationField() {
        return new Field.Builder()
                .type(Constants.FieldTypes.SELECT_ONE)
                .label("Location")
                .name("location")
                .option(new Option.Builder().label("In-state").value("in state").build())
                .option(new Option.Builder().label("Out-of-state").value("out of state").selected().build())
                .option(new Option.Builder().label("Waiver").value("waiver").build())
                .editable()
                .required()
                .build();
    }

    public static Field descriptionField() {
        return new Field.Builder()
                .type(Constants.FieldTypes.TEXTAREA)
                .label("Description")
                .name("Description")
                .maxValueLength(4000)
                .build();
    }

    public static Field confirmationField() {
        return new Field.Builder()
                .type(Constants.FieldTypes.TEXT)
                .name("ConfirmationNumber")
                .constraint(new Constraint.Builder().type(Constants.ConstraintTypes.IS_CONFIRMATION_NUMBER).build())
                .maxValueLength(40)
                .build();
    }

    public static Field allowedField() {
        return new Field.Builder()
                .type(Constants.FieldTypes.CHECKBOX)
                .label("Is this employee allowed to go on vacation?")
                .name("Allowed")
                .option(new Option.Builder().value("Yes").label("Yes").build())
                .editable()
                .build();
    }

    public static Field applicableField() {
        return new Field.Builder()
                .type(Constants.FieldTypes.RADIO)
                .label("Is this form applicable to them?")
                .name("Applicable")
                .option(new Option.Builder().value("Yes").label("Yes").build())
                .option(new Option.Builder().value("No").label("No").build())
                .editable()
                .build();
    }

    public static Section exampleSectionWithTwoFields() {
        return new Section.Builder()
                .tagId("basic")
                .field(employeeNameField())
                .field(budgetNumberField())
                .button(new Button.Builder().label("Next").type("button-link").value("next").tooltip("Go to next step").build())
                .ordinal(1)
                .build();
    }

    public static Section exampleSectionWithOneField() {
        return new Section.Builder()
                .tagId("supplemental")
                .field(supervisorIdField())
                .field(actionTypeField())
                .field(locationField())
                .field(descriptionField())
                .field(allowedField())
                .field(applicableField())
                .ordinal(2)
                .build();
    }

    public static Section exampleSectionWithConfirmationNumber() {
        return new Section.Builder()
                .tagId("confirmation")
                .field(confirmationField())
                .ordinal(1)
                .build();
    }

    public static Screen exampleScreenWithTwoSections(String type) {

        return new Screen.Builder()
                .title("First screen")
                .type(type)
                .section(exampleSectionWithTwoFields())
                .section(exampleSectionWithOneField())
                .attachmentAllowed(false)
                .location("/test/example1.html")
                .build();
    }

    public static Screen exampleThankYouScreen() {
        return new Screen.Builder()
                .title("Second screen")
                .section(exampleSectionWithConfirmationNumber())
                .attachmentAllowed(false)
                .build();
    }

    public static Interaction exampleInteractionWithTwoScreens() {
        return new Interaction.Builder()
                .label("Example Interaction")
                .screen(exampleScreenWithTwoSections(Constants.ScreenTypes.WIZARD))
                .screen(exampleThankYouScreen())
                .build();
    }

    public static Form exampleForm() {
        Process process = exampleProcess();
        return new Form.Builder()
                .processDefinitionKey(process.getProcessDefinitionKey())
                .submissionType(Constants.SubmissionTypes.INTERIM)
                .formInstanceId("12345")
                .formValue(employeeNameField().getName(), "Joe Testington")
                .screen(exampleScreenWithTwoSections(Constants.ScreenTypes.STANDARD))
                .build();
    }

    public static Form exampleFormWithWizardTemplate() {
        Process process = exampleProcess();
        return new Form.Builder()
                .processDefinitionKey(process.getProcessDefinitionKey())
                .submissionType(Constants.SubmissionTypes.INTERIM)
                .formInstanceId("12345")
                .formValue(employeeNameField().getName(), "Joe Testington")
                .screen(exampleScreenWithTwoSections(Constants.ScreenTypes.WIZARD_TEMPLATE))
                .build();
    }

    public static Process exampleProcess() {
        return new Process.Builder()
                .processDefinitionKey("Demonstration")
                .interaction(exampleInteractionWithTwoScreens())
                .processDefinitionLabel("This is a demonstration process")
                .engine("activiti")
                .engineProcessDefinitionKey("example")
                .build();

    }

    public static ProcessInstance exampleProcessInstance() {
        Process process = exampleProcess();
        return new ProcessInstance.Builder()
                .processDefinitionKey(process.getProcessDefinitionKey())
                .processDefinitionLabel(process.getProcessDefinitionLabel())
                .engineProcessInstanceId(UUID.randomUUID().toString())
                .build();
    }


}
