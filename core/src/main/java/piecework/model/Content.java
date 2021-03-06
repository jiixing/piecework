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
package piecework.model;

import org.apache.log4j.Logger;
import org.springframework.core.io.Resource;
import piecework.ui.Streamable;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Date;

/**
 * Abstraction on GridFSFile or GridFSResource -- not a document, but to make GridFS resources
 * look like other model objects for the purpose of keeping the code readable.
 *
 * @author James Renfro
 */
public class Content implements Serializable, Streamable {

    private static final Logger LOG = Logger.getLogger(Content.class);

    private final String contentId;
    private final String contentType;
    private final String filename;
    private final String location;
    private final InputStream inputStream;
    private final Resource resource;
    private final String md5;
    private final Date lastModified;
    private final Long length;

    private Content() {
        this(new Builder());
    }

    private Content(Builder builder) {
        this.contentId = builder.contentId;
        this.contentType = builder.contentType;
        this.filename = builder.filename;
        this.location = builder.location;
        this.inputStream = builder.inputStream;
        this.resource = builder.resource;
        this.md5 = builder.md5;
        this.lastModified = builder.lastModified;
        this.length = builder.length;
    }

    public String getContentId() {
        return contentId;
    }

    public String getContentType() {
        return contentType;
    }

    public String getName() {
        return filename;
    }

    public String getFilename() {
        return filename;
    }

    public String getLocation() {
        return location;
    }

    @Deprecated
    // Use getResource instead where possible
    public InputStream getInputStream() {
        if (resource != null) {
            try {
                return resource.getInputStream();
            } catch (IOException ioe) {
                LOG.error("Caught an io exception trying to grab the input stream from the resource object", ioe);
                return null;
            }
        }
        return inputStream;
    }

    public Resource getResource() {
        return resource;
    }

    public String getMd5() {
        return md5;
    }

    public Date getLastModified() {
        return lastModified;
    }

    public Long getLength() {
        return length;
    }

    public final static class Builder {

        private String contentId;
        private String contentType;
        private String filename;
        private String location;
        private InputStream inputStream;
        private Resource resource;
        private String md5;
        private Date lastModified;
        private Long length;

        public Builder() {

        }

        public Builder(Content content) {
            this.contentId = content.contentId;
            this.contentType = content.contentType;
            this.filename = content.filename;
            this.location = content.location;
            this.inputStream = content.inputStream;
            this.resource = content.resource;
            this.md5 = content.md5;
            this.lastModified = content.lastModified;
            this.length = content.length;
        }

        public Content build() {
            return new Content(this);
        }

        public Builder contentId(String contentId) {
            this.contentId = contentId;
            return this;
        }

        public Builder contentType(String contentType) {
            this.contentType = contentType;
            return this;
        }

        public Builder filename(String filename) {
            this.filename = filename;
            return this;
        }

        public Builder location(String location) {
            this.location = location;
            return this;
        }

        public Builder inputStream(InputStream inputStream) {
            this.inputStream = inputStream;
            return this;
        }

        public Builder resource(Resource resource) {
            this.resource = resource;
            return this;
        }

        public Builder md5(String md5) {
            this.md5 = md5;
            return this;
        }

        public Builder lastModified(Date lastModified) {
            this.lastModified = lastModified;
            return this;
        }

        public Builder lastModified(long lastModified) {
            this.lastModified = new Date(lastModified);
            return this;
        }

        public Builder length(Long length) {
            this.length = length;
            return this;
        }
    }

}
