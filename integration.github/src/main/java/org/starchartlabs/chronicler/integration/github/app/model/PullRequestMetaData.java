/*
 * Copyright 2017 StarChart Labs Authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.starchartlabs.chronicler.integration.github.app.model;

import java.util.Objects;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;

/**
 * Represents pull request data fields from webhook events of interest to the application
 *
 * <p>
 * Uses the Jackson library annotations to allow deserializing from a JSON string to a Java representation
 *
 * @author romeara
 * @since 0.1.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PullRequestMetaData {

    private final long id;

    private final long number;

    private final String url;

    private final String statusesUrl;

    /**
     * @param id
     *            GitHub identifier of the pull request
     * @param number
     *            The pull request number
     * @param url
     *            The API URL of the pull request
     * @param statusesUrl
     *            The URL for updating or reading pull request statuses
     * @since 0.1.0
     */
    @JsonCreator
    public PullRequestMetaData(@JsonProperty(value = "id", required = true) long id,
            @JsonProperty(value = "number", required = true) long number,
            @JsonProperty(value = "url", required = true) String url,
            @JsonProperty(value = "statuses_url", required = true) String statusesUrl) {
        this.id = id;
        this.number = number;
        this.url = Objects.requireNonNull(url);
        this.statusesUrl = Objects.requireNonNull(statusesUrl);
    }

    /**
     * @return GitHub identifier of the pull request
     * @since 0.1.0
     */
    public long getId() {
        return id;
    }

    /**
     * @return The pull request number
     * @since 0.1.0
     */
    public long getNumber() {
        return number;
    }

    /**
     * @return The API URL of the pull request
     * @since 0.1.0
     */
    public String getUrl() {
        return url;
    }

    /**
     * @return The URL for updating or reading pull request statuses
     * @since 0.1.0
     */
    public String getStatusesUrl() {
        return statusesUrl;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(),
                getNumber(),
                getUrl(),
                getStatusesUrl());
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        boolean result = false;

        if (obj instanceof PullRequestMetaData) {
            PullRequestMetaData compare = (PullRequestMetaData) obj;

            result = Objects.equals(compare.getId(), getId())
                    && Objects.equals(compare.getNumber(), getNumber())
                    && Objects.equals(compare.getUrl(), getUrl())
                    && Objects.equals(compare.getStatusesUrl(), getStatusesUrl());
        }

        return result;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(getClass()).omitNullValues()
                .add("id", getId())
                .add("number", getNumber())
                .add("url", getUrl())
                .add("statusesUrl", getStatusesUrl())
                .toString();
    }

}
