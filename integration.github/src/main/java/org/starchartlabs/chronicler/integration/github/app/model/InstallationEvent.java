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
 * Represents a JSON payload from a GitHub webhook event which describes changes to an installation of a GitHub App
 * overall
 *
 * @author romeara
 * @since 0.1.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class InstallationEvent {

    private final String action;

    private final InstallationMetaData installation;

    /**
     * @param action
     *            The type of action that occurred ("deleted", etc)
     * @param installation
     *            Details of the installation the action occurred on
     * @since 0.1.0
     */
    @JsonCreator
    public InstallationEvent(@JsonProperty(value = "action", required = true) String action,
            @JsonProperty(value = "installation", required = true) InstallationMetaData installation) {
        this.action = Objects.requireNonNull(action);
        this.installation = Objects.requireNonNull(installation);
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

    @Override
    public int hashCode() {
        return Objects.hash(getAction(),
                getInstallation());
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        boolean result = false;

        if (obj instanceof InstallationEvent) {
            InstallationEvent compare = (InstallationEvent) obj;

            result = Objects.equals(compare.getAction(), getAction())
                    && Objects.equals(compare.getInstallation(), getInstallation());
        }

        return result;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(getClass()).omitNullValues()
                .add("action", getAction())
                .add("installation", getInstallation())
                .toString();
    }
}
