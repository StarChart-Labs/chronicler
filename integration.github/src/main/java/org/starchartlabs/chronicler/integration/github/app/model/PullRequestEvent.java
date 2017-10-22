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
 * Represents a JSON payload from a GitHub webhook event which describes a change to a pull request
 *
 * @author romeara
 * @since 0.1.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PullRequestEvent {

    private final String action;

    private final PullRequestMetaData pullRequest;

    /**
     * @param action
     *            The type of action the event is representing. (Ex: opened, closed, etc)
     * @param pullRequest
     *            Specific meta-data about the pull request
     * @since 0.1.0
     */
    @JsonCreator
    public PullRequestEvent(@JsonProperty(value = "action", required = true) String action,
            @JsonProperty(value = "pull_request", required = true) PullRequestMetaData pullRequest) {
        this.action = Objects.requireNonNull(action);
        this.pullRequest = Objects.requireNonNull(pullRequest);
    }

    /**
     * @return The type of action the event is representing. (Ex: opened, closed, etc)
     * @since 0.1.0
     */
    public String getAction() {
        return action;
    }

    /**
     * @return Specific meta-data about the pull request
     * @since 0.1.0
     */
    public PullRequestMetaData getPullRequest() {
        return pullRequest;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getAction(),
                getPullRequest());
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        boolean result = false;

        if (obj instanceof PullRequestEvent) {
            PullRequestEvent compare = (PullRequestEvent) obj;

            result = Objects.equals(compare.getAction(), getAction())
                    && Objects.equals(compare.getPullRequest(), getPullRequest());
        }

        return result;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(getClass()).omitNullValues()
                .add("action", getAction())
                .add("pullRequest", getPullRequest())
                .toString();
    }
}
