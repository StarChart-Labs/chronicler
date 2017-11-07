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
 * Represents repository data fields from webhook events of interest to the application
 *
 * <p>
 * Uses the Jackson library annotations to allow deserializing from a JSON string to a Java representation
 *
 * @author romeara
 * @since 0.1.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class RepositoryMetaData {

    private final long id;

    private final String name;

    private final String fullName;

    private final boolean privateRepository;

    private final String url;

    private final OwnerMetaData owner;

    /**
     * @param id
     *            GitHub identifier of the repository
     * @param name
     *            Name or the repository within the context of it's owning user or organization
     * @param fullName
     *            Full name of the repository, including owner context
     * @param privateRepository
     *            True if the repository is hidden from public view, false otherwise
     * @param url
     *            API URL for accessing GitHub information about the repository
     * @param owner
     *            Meta data describing the owner of the repository
     * @since 0.1.0
     */
    @JsonCreator
    public RepositoryMetaData(@JsonProperty(value = "id", required = true) long id,
            @JsonProperty(value = "name", required = true) String name,
            @JsonProperty(value = "full_name", required = true) String fullName,
            @JsonProperty(value = "private", required = true) boolean privateRepository,
            @JsonProperty(value = "url", required = true) String url,
            @JsonProperty(value = "owner", required = true) OwnerMetaData owner) {
        this.id = id;
        this.name = Objects.requireNonNull(name);
        this.fullName = Objects.requireNonNull(fullName);
        this.privateRepository = privateRepository;
        this.url = Objects.requireNonNull(url);
        this.owner = Objects.requireNonNull(owner);
    }

    /**
     * @return GitHub identifier of the repository
     * @since 0.1.0
     */
    public long getId() {
        return id;
    }

    /**
     * @return Name or the repository within the context of it's owning user or organization
     * @since 0.1.0
     */
    public String getName() {
        return name;
    }

    /**
     * @return Full name of the repository, including owner context
     * @since 0.1.0
     */
    public String getFullName() {
        return fullName;
    }

    /**
     * @return True if the repository is hidden from public view, false otherwise
     * @since 0.1.0
     */
    public boolean isPrivateRepository() {
        return privateRepository;
    }

    /**
     * @return API URL for accessing GitHub information about the repository
     * @since 0.1.0
     */
    public String getUrl() {
        return url;
    }

    /**
     * @return Meta data describing the owner of the repository
     * @since 0.1.0
     */
    public OwnerMetaData getOwner() {
        return owner;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(),
                getName(),
                getFullName(),
                isPrivateRepository(),
                getUrl(),
                getOwner());
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        boolean result = false;

        if (obj instanceof RepositoryMetaData) {
            RepositoryMetaData compare = (RepositoryMetaData) obj;

            result = Objects.equals(compare.getId(), getId())
                    && Objects.equals(compare.getName(), getName())
                    && Objects.equals(compare.getFullName(), getFullName())
                    && Objects.equals(compare.isPrivateRepository(), isPrivateRepository())
                    && Objects.equals(compare.getUrl(), getUrl())
                    && Objects.equals(compare.getOwner(), getOwner());
        }

        return result;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(getClass()).omitNullValues()
                .add("id", getId())
                .add("name", getName())
                .add("fullName", getFullName())
                .add("privateRepository", isPrivateRepository())
                .add("url", getUrl())
                .add("owner", getOwner())
                .toString();
    }

}
