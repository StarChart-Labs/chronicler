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
 * Represents owner data fields from webhook events of interest to the application
 *
 * <p>
 * Uses the Jackson library annotations to allow deserializing from a JSON string to a Java representation
 *
 * @author romeara
 * @since 0.1.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class OwnerMetaData {

    private final long id;

    private final String login;

    private final String type;

    private final String url;

    /**
     * @param id
     *            GitHub identifier of the owner
     * @param login
     *            Login of the owner
     * @param type
     *            Type of owner ("User", "Organization", etc)
     * @param url
     *            The GitHub API URL where more details about the owner are available
     * @since 0.1.0
     */
    @JsonCreator
    public OwnerMetaData(@JsonProperty(value = "id", required = true) long id,
            @JsonProperty(value = "login", required = true) String login,
            @JsonProperty(value = "type", required = true) String type,
            @JsonProperty(value = "url", required = true) String url) {
        this.id = Objects.requireNonNull(id);
        this.login = Objects.requireNonNull(login);
        this.type = Objects.requireNonNull(type);
        this.url = Objects.requireNonNull(url);
    }

    /**
     * @return GitHub identifier of the owner
     * @since 0.1.0
     */
    public long getId() {
        return id;
    }

    /**
     * @return Login of the owner
     * @since 0.1.0
     */
    public String getLogin() {
        return login;
    }

    /**
     * @return Type of owner ("User", "Organization", etc)
     * @since 0.1.0
     */
    public String getType() {
        return type;
    }

    /**
     * @return The GitHub API URL where more details about the owner are available
     * @since 0.1.0
     */
    public String getUrl() {
        return url;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(),
                getLogin(),
                getType(),
                getUrl());
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        boolean result = false;

        if (obj instanceof OwnerMetaData) {
            OwnerMetaData compare = (OwnerMetaData) obj;

            result = Objects.equals(compare.getId(), getId())
                    && Objects.equals(compare.getLogin(), getLogin())
                    && Objects.equals(compare.getType(), getType())
                    && Objects.equals(compare.getUrl(), getUrl());
        }

        return result;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(getClass()).omitNullValues()
                .add("id", getId())
                .add("login", getLogin())
                .add("type", getType())
                .add("url", getUrl())
                .toString();
    }

}
