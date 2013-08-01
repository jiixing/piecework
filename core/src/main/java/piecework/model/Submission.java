/*
 * Copyright 2012 University of Washington
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
package piecework.model;

import java.util.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.apache.commons.lang.StringUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import piecework.enumeration.ActionType;
import piecework.model.bind.FormNameValueEntryMapAdapter;
import piecework.security.Sanitizer;
import piecework.util.ManyMap;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * @author James Renfro
 */
@XmlRootElement(name = Submission.Constants.ROOT_ELEMENT_NAME)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = Submission.Constants.TYPE_NAME)
@JsonIgnoreProperties(ignoreUnknown = true)
@Document(collection = "submission")
public class Submission {

    @XmlTransient
    @Id
    private final String submissionId;

    @XmlAttribute
    private final String alias;

    @XmlAttribute
    private final String processDefinitionKey;

    @XmlElement
    private final String processInstanceLabel;

    @XmlTransient
    private final String requestId;

    @XmlTransient
    private final String taskId;

    @XmlTransient
    private final ActionType action;

    @XmlJavaTypeAdapter(FormNameValueEntryMapAdapter.class)
    private final Map<String, List<? extends Value>> data;

    @XmlTransient
    @Transient
    private final Map<String, List<? extends Value>> restrictedData;

    @XmlElementWrapper(name="attachments")
    @XmlElementRef
    private final List<Attachment> attachments;

    @XmlTransient
    private final Date submissionDate;

    @XmlTransient
    private final String submitterId;

    private Submission() {
        this(new Submission.Builder());
    }

    private Submission(Submission.Builder builder) {
        this.submissionId = builder.submissionId;
        this.processDefinitionKey = builder.processDefinitionKey;
        this.alias = builder.alias;
        this.requestId = builder.requestId;
        this.taskId = builder.taskId;
        this.processInstanceLabel = builder.processInstanceLabel;
        this.attachments = Collections.unmodifiableList(builder.attachments);
        this.action = builder.action;
        this.data = Collections.unmodifiableMap((Map<String, List<? extends Value>>)builder.data);
        this.restrictedData = Collections.unmodifiableMap((Map<String, List<? extends Value>>)builder.restrictedData);
        this.submissionDate = builder.submissionDate;
        this.submitterId = builder.submitterId;
    }

    @JsonIgnore
    public String getSubmissionId() {
        return submissionId;
    }

    @JsonIgnore
    public String getRequestId() {
        return requestId;
    }

    @JsonIgnore
    public String getTaskId() {
        return taskId;
    }

    public String getAlias() {
        return alias;
    }

    public String getProcessDefinitionKey() {
        return processDefinitionKey;
    }

    public String getProcessInstanceLabel() {
        return processInstanceLabel;
    }

    @JsonIgnore
    public ActionType getAction() {
        return action;
    }

    public Map<String, List<? extends Value>> getData() {
        return data;
    }

    @JsonIgnore
    public Map<String, List<? extends Value>> getRestrictedData() {
        return restrictedData;
    }

    public List<Attachment> getAttachments() {
        return attachments;
    }

    @JsonIgnore
	public Date getSubmissionDate() {
        return submissionDate;
    }

    @JsonIgnore
    public String getSubmitterId() {
        return submitterId;
    }

    public final static class Builder {

        private String submissionId;
        private String requestId;
        private String taskId;
        private String processDefinitionKey;
        private String processInstanceLabel;
        private String alias;
        private ActionType action;
        private ManyMap<String, Value> data;
        private ManyMap<String, Value> restrictedData;
        private List<Attachment> attachments;
        private Date submissionDate;
        private String submitterId;

        public Builder() {
            super();
            this.attachments = new ArrayList<Attachment>();
            this.action = ActionType.COMPLETE;
        }

        public Builder(Submission submission, Sanitizer sanitizer) {
            this.submissionId = sanitizer.sanitize(submission.submissionId);
            this.processDefinitionKey = sanitizer.sanitize(submission.processDefinitionKey);
            this.processInstanceLabel = sanitizer.sanitize(submission.processInstanceLabel);
            this.alias = sanitizer.sanitize(submission.alias);
            this.requestId = sanitizer.sanitize(submission.requestId);
            this.taskId = sanitizer.sanitize(submission.taskId);
            this.submissionDate = submission.submissionDate;
            this.submissionId = sanitizer.sanitize(submissionId);
            this.action = submission.getAction();

            if (submission.data != null && !submission.data.isEmpty()) {
                this.data = new ManyMap<String, Value>(submission.data.size());

                for (Map.Entry<String, List<? extends Value>> entry : submission.data.entrySet()) {
                    List<? extends Value> values = entry.getValue();

                    for (Value value : values) {
                        if (value instanceof File) {
                            File file = File.class.cast(value);
                            File sanitized = new File.Builder()
                                    .name(sanitizer.sanitize(file.getName()))
                                    .contentType(sanitizer.sanitize(file.getContentType()))
                                    .location(sanitizer.sanitize(file.getLocation()))
                                    .link(sanitizer.sanitize(file.getLink()))
                                    .build();
                            this.data.putOne(entry.getKey(), sanitized);
                        } else {
                            this.data.putOne(entry.getKey(), new Value(sanitizer.sanitize(value.getValue())));
                        }
                    }
                }

            } else
                this.data = new ManyMap<String, Value>();

            if (submission.restrictedData != null && !submission.restrictedData.isEmpty())
                this.restrictedData = new ManyMap<String, Value>((Map<String,List<Value>>) submission.restrictedData);
            else
                this.restrictedData = new ManyMap<String, Value>();

            if (submission.attachments != null && !submission.attachments.isEmpty()) {
                this.attachments = new ArrayList<Attachment>();
                for (Attachment attachment : submission.attachments) {
                    this.attachments.add(new Attachment.Builder(attachment, sanitizer).build());
                }
            } else {
                this.attachments = new ArrayList<Attachment>();
            }
        }

        public Submission build() {
            return new Submission(this);
        }

        public Builder submissionId(String submissionId) {
            this.submissionId = submissionId;
            return this;
        }

        public Builder processDefinitionKey(String processDefinitionKey) {
            this.processDefinitionKey = processDefinitionKey;
            return this;
        }

        public Builder alias(String alias) {
            this.alias = alias;
            return this;
        }

        public Builder requestId(String requestId) {
            this.requestId = requestId;
            return this;
        }

        public Builder taskId(String taskId) {
            this.taskId = taskId;
            return this;
        }

        public Builder processInstanceLabel(String processInstanceLabel) {
            this.processInstanceLabel = processInstanceLabel;
            return this;
        }

        public Builder submissionDate(Date submissionDate) {
            this.submissionDate = submissionDate;
            return this;
        }

        public Builder submitterId(String submitterId) {
            this.submitterId = submitterId;
            return this;
        }

        public Builder attachment(Attachment attachment) {
            this.attachments.add(attachment);
            return this;
        }

        public Builder action(String action) {
            if (action != null)
                this.action = ActionType.valueOf(action);
            return this;
        }

        public Builder formValue(String key, String value) {
            this.data.putOne(key, new Value(value));
            return this;
        }

        public Builder formValue(String key, File file) {
            this.data.putOne(key, file);
            return this;
        }

        public Builder restrictedValue(String key, String value) {
            this.restrictedData.putOne(key, new Value(value));
            return this;
        }

        public Builder restrictedValue(String key, File file) {
            this.restrictedData.putOne(key, file);
            return this;
        }

        public String getProcessDefinitionKey() {
            return processDefinitionKey;
        }
    }

    public static class Constants {
        public static final String RESOURCE_LABEL = "Submission";
        public static final String ROOT_ELEMENT_NAME = "processInstance";
        public static final String TYPE_NAME = "SubmissionType";
    }
}
