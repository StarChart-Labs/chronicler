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

import java.util.List;
import java.util.Objects;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;

/**
 * Represents a JSON payload from a GitHub webhook event which describes changes to an installation of repositories of a
 * GitHub App overall
 *
 * @author romeara
 * @since 0.1.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class InstallationRepositoriesEvent {

    private final String action;

    private final InstallationMetaData installation;

    private final String repositorySelection;

    private final List<TargetRepositoryMetaData> repositoriesAdded;

    private final List<TargetRepositoryMetaData> repositoriesRemoved;

    /**
     * @param action
     *            The type of action that occurred ("deleted", etc)
     * @param installation
     *            Details of the installation the action occurred on
     * @param repositorySelection
     *            Indicates if all repositories ("all") or a sub-set ("selected") is involved
     * @param repositoriesAdded
     *            Repositories added to the installation
     * @param repositoriesRemoved
     *            Repositories removed from the installation
     * @since 0.1.0
     */
    @JsonCreator
    public InstallationRepositoriesEvent(@JsonProperty(value = "action", required = true) String action,
            @JsonProperty(value = "installation", required = true) InstallationMetaData installation,
            @JsonProperty(value = "repository_selection", required = true) String repositorySelection,
            @JsonProperty(value = "repositories_added",
            required = true) List<TargetRepositoryMetaData> repositoriesAdded,
            @JsonProperty(value = "repositories_removed",
            required = true) List<TargetRepositoryMetaData> repositoriesRemoved) {
        this.action = Objects.requireNonNull(action);
        this.installation = Objects.requireNonNull(installation);
        this.repositorySelection = Objects.requireNonNull(repositorySelection);
        this.repositoriesAdded = Objects.requireNonNull(repositoriesAdded);
        this.repositoriesRemoved = Objects.requireNonNull(repositoriesRemoved);
    }

    /**
     * @return The type of action that occurred ("deleted", etc)
     * @since 0.1.0
     */
    public String getAction() {
        return action;
    }

    /**
     * @return Details of the installation the action occurred on
     * @since 0.1.0
     */
    public InstallationMetaData getInstallation() {
        return installation;
    }

    /**
     * @return Indicates if all repositories ("all") or a sub-set ("selected") is involved
     * @since 0.1.0
     */
    public String getRepositorySelection() {
        return repositorySelection;
    }

    /**
     * @return Repositories added to the installation
     * @since 0.1.0
     */
    public List<TargetRepositoryMetaData> getRepositoriesAdded() {
        return repositoriesAdded;
    }

    /**
     * @return Repositories removed from the installation
     * @since 0.1.0
     */
    public List<TargetRepositoryMetaData> getRepositoriesRemoved() {
        return repositoriesRemoved;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getAction(),
                getInstallation(),
                getRepositorySelection(),
                getRepositoriesAdded(),
                getRepositoriesRemoved());
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        boolean result = false;

        if (obj instanceof InstallationRepositoriesEvent) {
            InstallationRepositoriesEvent compare = (InstallationRepositoriesEvent) obj;

            result = Objects.equals(compare.getAction(), getAction())
                    && Objects.equals(compare.getInstallation(), getInstallation())
                    && Objects.equals(compare.getRepositorySelection(), getRepositorySelection())
                    && Objects.equals(compare.getRepositoriesAdded(), getRepositoriesAdded())
                    && Objects.equals(compare.getRepositoriesRemoved(), getRepositoriesRemoved());
        }

        return result;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(getClass()).omitNullValues()
                .add("action", getAction())
                .add("installation", getInstallation())
                .add("repositorySelection", getRepositorySelection())
                .add("repositoriesAdded", getRepositoriesAdded())
                .add("repositoriesRemoved", getRepositoriesRemoved())
                .toString();
    }

}
