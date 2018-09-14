/*
 * Copyright (c) Sep 9, 2018 StarChart Labs Authors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    romeara - initial API and implementation and/or initial documentation
 */
package org.starchartlabs.chronicler.aws.webhook.router;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SNSEvent;
import com.amazonaws.services.lambda.runtime.events.SNSEvent.SNS;
import com.amazonaws.services.lambda.runtime.events.SNSEvent.SNSRecord;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

//https://developer.github.com/v3/activity/events/types/#pullrequestevent
//https://developer.github.com/v3/pulls
//https://developer.github.com/v3/pulls/#list-pull-requests-files
public class WebhookRouterRequestHandler implements RequestHandler<SNSEvent, String> {

    private static final ObjectMapper mapper = new ObjectMapper();

    private static final Set<String> RELEVANT_ACTIONS = Stream.of("opened", "edited", "closed", "reopened")
            .collect(Collectors.toSet());

    @Override
    public String handleRequest(SNSEvent input, Context context) {
        String result = input.getRecords().stream()
                .findAny()
                .map(SNSRecord::getSNS)
                .map(SNS::getMessage)
                .orElse("null");

        context.getLogger().log(result);

        return result;
        // try {
        // String tempResult = null;
        //
        // Optional<Map<String, String>> dispatchEvent = getRelevantPullRequestEvent(input)
        // .map(this::getPullRequestMessage);
        //
        // if (dispatchEvent.isPresent()) {
        // // TODO romeara send SQS message
        //
        // tempResult = "Sent event: " + dispatchEvent.get();
        // } else {
        // tempResult = "Event type received: " + input.get("github-event-type");
        // }
        //
        // context.getLogger().log(tempResult);
        //
        // return tempResult;
        // } catch (IOException e) {
        // throw new RuntimeException(e);
        // }
    }

    private Optional<PullRequestEvent> getRelevantPullRequestEvent(Map<String, Object> input) throws IOException {
        Optional<PullRequestEvent> result = Optional.empty();

        if (Objects.equals(input.get("github-event-type"), "pull_request")) {
            PullRequestEvent event = mapper.readerFor(PullRequestEvent.class)
                    .readValue(input.get("github-event-body").toString());
            result = Optional.of(event)
                    .filter(pr -> RELEVANT_ACTIONS.contains(pr.getAction()));
        }

        return result;
    }

    private Map<String, String> getPullRequestMessage(PullRequestEvent input) {
        Map<String, String> result = new HashMap<>();

        result.put("github-id", input.getPullRequest().getId());
        result.put("number", input.getPullRequest().getNumber().toString());
        result.put("statuses-url", input.getPullRequest().getStatusesUrl());
        result.put("files-url", input.getPullRequest().getUrl() + "/files");
        result.put("repo-installation-url", input.getPullRequest().getBase().getRepo().getUrl() + "/installation");

        return result;
    }

    private static class PullRequestEvent {

        private final String action;

        private final PullRequestData pullRequest;

        @JsonCreator
        public PullRequestEvent(@JsonProperty("action") String action,
                @JsonProperty("pull_request") PullRequestData pullRequest) {
            this.action = Objects.requireNonNull(action);
            this.pullRequest = Objects.requireNonNull(pullRequest);
        }

        public String getAction() {
            return action;
        }

        public PullRequestData getPullRequest() {
            return pullRequest;
        }

    }

    private static class PullRequestData {

        private final String id;

        private final Long number;

        private final String url;

        private final String statusesUrl;

        private final BaseData base;

        @JsonCreator
        public PullRequestData(@JsonProperty("id") String id,
                @JsonProperty("number") Long number,
                @JsonProperty("url") String url,
                @JsonProperty("statuses_url") String statusesUrl,
                @JsonProperty("base") BaseData base) {
            this.id = Objects.requireNonNull(id);
            this.number = Objects.requireNonNull(number);
            this.url = Objects.requireNonNull(url);
            this.statusesUrl = Objects.requireNonNull(statusesUrl);
            this.base = Objects.requireNonNull(base);
        }

        public String getId() {
            return id;
        }

        public Long getNumber() {
            return number;
        }

        public String getUrl() {
            return url;
        }

        public String getStatusesUrl() {
            return statusesUrl;
        }

        public BaseData getBase() {
            return base;
        }

    }

    private static class BaseData {

        private final RepoData repo;

        @JsonCreator
        public BaseData(@JsonProperty("repo") RepoData repo) {
            this.repo = Objects.requireNonNull(repo);
        }

        public RepoData getRepo() {
            return repo;
        }

    }

    private static class RepoData {

        private final String url;

        @JsonCreator
        public RepoData(@JsonProperty("url") String url) {
            this.url = Objects.requireNonNull(url);
        }

        public String getUrl() {
            return url;
        }

    }

}
