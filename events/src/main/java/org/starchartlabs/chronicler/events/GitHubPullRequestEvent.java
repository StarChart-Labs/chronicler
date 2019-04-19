/*
 * Copyright 2019 StarChart-Labs Contributors (https://github.com/StarChart-Labs)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.starchartlabs.chronicler.events;

import java.util.Objects;

import javax.annotation.Nullable;

import org.starchartlabs.alloy.core.MoreObjects;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class GitHubPullRequestEvent {

    public static final String SUBJECT = GitHubPullRequestEvent.class.getName() + ":1";

    private static final Gson GSON = new GsonBuilder().create();

    private final long number;

    private final String loggableRepositoryName;

    private final String pullRequestUrl;

    private final String baseRepositoryUrl;

    private final String baseBranch;

    private final String pullRequestStatusesUrl;

    private final String headCommitSha;

    public GitHubPullRequestEvent(long number, String loggableRepositoryName, String pullRequestUrl,
            String baseRepositoryUrl, String baseBranch,
            String pullRequestStatusesUrl, String headCommitSha) {
        this.number = number;
        this.loggableRepositoryName = Objects.requireNonNull(loggableRepositoryName);
        this.pullRequestUrl = Objects.requireNonNull(pullRequestUrl);
        this.baseRepositoryUrl = Objects.requireNonNull(baseRepositoryUrl);
        this.baseBranch = Objects.requireNonNull(baseBranch);
        this.pullRequestStatusesUrl = Objects.requireNonNull(pullRequestStatusesUrl);
        this.headCommitSha = Objects.requireNonNull(headCommitSha);
    }

    public long getNumber() {
        return number;
    }

    public String getLoggableRepositoryName() {
        return loggableRepositoryName;
    }

    public String getPullRequestUrl() {
        return pullRequestUrl;
    }

    public String getBaseRepositoryUrl() {
        return baseRepositoryUrl;
    }

    public String getBaseBranch() {
        return baseBranch;
    }

    public String getPullRequestStatusesUrl() {
        return pullRequestStatusesUrl;
    }

    public String getHeadCommitSha() {
        return headCommitSha;
    }

    public String toJson() {
        return GSON.toJson(this);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getNumber(),
                getLoggableRepositoryName(),
                getPullRequestUrl(),
                getBaseRepositoryUrl(),
                getBaseBranch(),
                getPullRequestStatusesUrl(),
                getHeadCommitSha());
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        boolean result = false;

        if (obj instanceof GitHubPullRequestEvent) {
            GitHubPullRequestEvent compare = (GitHubPullRequestEvent) obj;

            result = Objects.equals(compare.getNumber(), getNumber())
                    && Objects.equals(compare.getLoggableRepositoryName(), getLoggableRepositoryName())
                    && Objects.equals(compare.getPullRequestUrl(), getPullRequestUrl())
                    && Objects.equals(compare.getBaseRepositoryUrl(), getBaseRepositoryUrl())
                    && Objects.equals(compare.getBaseBranch(), getBaseBranch())
                    && Objects.equals(compare.getPullRequestStatusesUrl(), getPullRequestStatusesUrl())
                    && Objects.equals(compare.getHeadCommitSha(), getHeadCommitSha());
        }

        return result;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(getClass()).omitNullValues()
                .add("number", getNumber())
                .add("loggableRepositoryName", getLoggableRepositoryName())
                .add("pullRequestUrl", getPullRequestUrl())
                .add("baseRepositoryUrl", getBaseRepositoryUrl())
                .add("baseBranch", getBaseBranch())
                .add("pullRequestStatusesUrl", getPullRequestStatusesUrl())
                .add("headCommitSha", getHeadCommitSha())
                .toString();
    }

    public static GitHubPullRequestEvent fromJson(String json) {
        Objects.requireNonNull(json);

        return GSON.fromJson(json, GitHubPullRequestEvent.class);
    }

}
