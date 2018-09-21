/*
 * Copyright (c) Sep 19, 2018 StarChart Labs Authors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    romeara - initial API and implementation and/or initial documentation
 */
package org.starchartlabs.chronicler.github.model.webhook;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Nullable;

import org.starchartlabs.alloy.core.MoreObjects;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

//https://developer.github.com/v3/activity/events/types/#pullrequestevent
//https://developer.github.com/v3/pulls/#get-a-single-pull-request
public class PullRequestEvent {

    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(PullRequestEvent.class, new Deserializer())
            .create();

    private static final String EVENT_TYPE = "pull_request";

    private static final Collection<String> FILE_CHANGE_TYPES = Stream.of("opened", "edited")
            .collect(Collectors.toSet());

    // PR id (.pull_request.id, number)
    private final long id;

    // PR Number (.number, int)
    private final long number;

    // PR action (.action, str)
    private final String action;

    private final String loggableRepositoryName;

    // PR URL (.pull_request.url, str)
    private final String pullRequestUrl;

    // Repo Url (.pull_request.base.repo.url, str)
    private final String baseRepositoryUrl;

    // Status (pull_request.statuses_url, str)
    private final String pullRequestStatusesUrl;

    // Commit? (.pull_request.head.sha, str)
    private final String headCommitSha;

    public PullRequestEvent(long id, long number, String action, String loggableRepositoryName, String pullRequestUrl,
            String baseRepositoryUrl, String pullRequestStatusesUrl, String headCommitSha) {
        this.id = id;
        this.number = number;
        this.action = Objects.requireNonNull(action);
        this.loggableRepositoryName = Objects.requireNonNull(loggableRepositoryName);
        this.pullRequestUrl = Objects.requireNonNull(pullRequestUrl);
        this.baseRepositoryUrl = Objects.requireNonNull(baseRepositoryUrl);
        this.pullRequestStatusesUrl = Objects.requireNonNull(pullRequestStatusesUrl);
        this.headCommitSha = Objects.requireNonNull(headCommitSha);
    }

    public long getId() {
        return id;
    }

    public long getNumber() {
        return number;
    }

    public String getAction() {
        return action;
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

    public String getPullRequestStatusesUrl() {
        return pullRequestStatusesUrl;
    }

    public String getHeadCommitSha() {
        return headCommitSha;
    }

    public boolean isFileChangeType() {
        return FILE_CHANGE_TYPES.contains(getAction());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(),
                getNumber(),
                getAction(),
                getLoggableRepositoryName(),
                getPullRequestUrl(),
                getBaseRepositoryUrl(),
                getPullRequestStatusesUrl(),
                getHeadCommitSha());
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        boolean result = false;

        if (obj instanceof PullRequestEvent) {
            PullRequestEvent compare = (PullRequestEvent) obj;

            result = Objects.equals(compare.getId(), getId())
                    && Objects.equals(compare.getNumber(), getNumber())
                    && Objects.equals(compare.getAction(), getAction())
                    && Objects.equals(compare.getLoggableRepositoryName(), getLoggableRepositoryName())
                    && Objects.equals(compare.getPullRequestUrl(), getPullRequestUrl())
                    && Objects.equals(compare.getBaseRepositoryUrl(), getBaseRepositoryUrl())
                    && Objects.equals(compare.getPullRequestStatusesUrl(), getPullRequestStatusesUrl())
                    && Objects.equals(compare.getHeadCommitSha(), getHeadCommitSha());
        }

        return result;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(getClass()).omitNullValues()
                .add("id", getId())
                .add("number", getNumber())
                .add("action", getAction())
                .add("loggableRepositoryName", getLoggableRepositoryName())
                .add("pullRequestUrl", getPullRequestUrl())
                .add("baseRepositoryUrl", getBaseRepositoryUrl())
                .add("pullRequestStatusesUrl", getPullRequestStatusesUrl())
                .add("headCommitSha", getHeadCommitSha())
                .toString();
    }

    public static boolean isCompatibleWithEventType(String eventType) {
        Objects.requireNonNull(eventType);

        return Objects.equals(EVENT_TYPE, eventType);
    }

    public static PullRequestEvent fromJson(String json) {
        Objects.requireNonNull(json);

        return GSON.fromJson(json, PullRequestEvent.class);
    }

    private static final class Deserializer implements JsonDeserializer<PullRequestEvent> {

        @Override
        public PullRequestEvent deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            JsonObject event = json.getAsJsonObject();
            JsonObject pullRequest = event.get("pull_request").getAsJsonObject();
            JsonObject base = pullRequest.get("base").getAsJsonObject();
            JsonObject baseRepo = base.get("repo").getAsJsonObject();
            JsonObject head = pullRequest.get("head").getAsJsonObject();

            long id = pullRequest.get("id").getAsLong();
            long number = event.get("number").getAsLong();
            String action = event.get("action").getAsString();
            String pullRequestUrl = pullRequest.get("url").getAsString();
            String baseRepositoryUrl = baseRepo.get("url").getAsString();
            String pullRequestStatusesUrl = pullRequest.get("statuses_url").getAsString();
            String headCommitSha = head.get("sha").getAsString();

            String loggableRepositoryName = baseRepo.get("owner").getAsJsonObject().get("login").getAsString()
                    + "/<private repository>";

            if (!baseRepo.get("private").getAsBoolean()) {
                loggableRepositoryName = baseRepo.get("full_name").getAsString();
            }

            return new PullRequestEvent(id, number, action, loggableRepositoryName, pullRequestUrl, baseRepositoryUrl,
                    pullRequestStatusesUrl, headCommitSha);
        }
    }

}
