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
 * Represents a JSON payload from a GitHub webhook event which describes a change to a repository
 *
 * @author romeara
 * @since 0.1.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class RepositoryEvent {

    private final String action;

    private final RepositoryMetaData repository;

    /**
     * @param action
     *            The type of action the event is representing. (Ex: created, deleted, privatized, etc)
     * @param repository
     *            Specific meta-data about the repository
     * @since 0.1.0
     */
    @JsonCreator
    public RepositoryEvent(@JsonProperty(value = "action", required = true) String action,
            @JsonProperty(value = "repository", required = true) RepositoryMetaData repository) {
        this.action = Objects.requireNonNull(action);
        this.repository = Objects.requireNonNull(repository);
    }

    /**
     * @return The type of action the event is representing. (Ex: created, deleted, privatized, etc)
     * @since 0.1.0
     */
    public String getAction() {
        return action;
    }

    /**
     * @return Specific meta-data about the repository
     * @since 0.1.0
     */
    public RepositoryMetaData getRepository() {
        return repository;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getAction(),
                getRepository());
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        boolean result = false;

        if (obj instanceof RepositoryEvent) {
            RepositoryEvent compare = (RepositoryEvent) obj;

            result = Objects.equals(compare.getAction(), getAction())
                    && Objects.equals(compare.getRepository(), getRepository());
        }

        return result;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(getClass()).omitNullValues()
                .add("action", getAction())
                .add("repository", getRepository())
                .toString();
    }

}
