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
package org.starchartlabs.chronicler.test.integration.github.app.impl;

import java.util.Objects;
import java.util.Optional;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.starchartlabs.chronicler.integration.github.app.impl.GitHubWebhookAppService;
import org.starchartlabs.chronicler.integration.github.app.impl.WebhookEvents;
import org.starchartlabs.chronicler.integration.github.app.impl.WebhookVerifier;
import org.starchartlabs.chronicler.integration.github.domain.model.PullRequestAlteredEvent;
import org.starchartlabs.chronicler.integration.github.domain.model.RepositoryPrivatizedEvent;
import org.starchartlabs.chronicler.test.integration.github.ClasspathFileReader;
import org.starchartlabs.chronicler.test.integration.github.TestFiles;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import uk.org.lidalia.slf4jtest.LoggingEvent;
import uk.org.lidalia.slf4jtest.TestLogger;
import uk.org.lidalia.slf4jtest.TestLoggerFactory;

public class GitHubWebhookAppServiceTest {

    /** Logger reference to output information to the application log files */
    private static final Logger logger = LoggerFactory.getLogger(GitHubWebhookAppServiceTest.class);

    private final Gson gson = new Gson();

    @Mock
    private WebhookVerifier webhookVerifier;

    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    private TestLogger testLogger;

    private GitHubWebhookAppService githubWebhookAppService;

    @BeforeMethod
    public void setup(){
        MockitoAnnotations.initMocks(this);

        testLogger = TestLoggerFactory.getTestLogger(GitHubWebhookAppService.class);
        githubWebhookAppService = new GitHubWebhookAppService(webhookVerifier, applicationEventPublisher);
    }

    @AfterMethod
    public void teardown(ITestResult result) {
        logger.trace("Completed test ({})", result);

        Mockito.verifyNoMoreInteractions(webhookVerifier,
                applicationEventPublisher);

        TestLoggerFactory.clear();
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void constructNullWebhookVerifier() throws Exception {
        new GitHubWebhookAppService(null, applicationEventPublisher);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void constructNullApplicationEventPublisher() throws Exception {
        new GitHubWebhookAppService(webhookVerifier, null);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void acceptPayloadNullEventType() throws Exception {
        githubWebhookAppService.acceptPayload("securityKey", null, "payload");
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void acceptPayloadNullPayload() throws Exception {
        githubWebhookAppService.acceptPayload("securityKey", "eventType", null);
    }

    @Test
    public void acceptPayloadInvalidSecurity() throws Exception {
        String securityKey = "securityKey";
        String payload = "payload";

        Mockito.when(webhookVerifier.isPayloadLegitimate(securityKey, payload)).thenReturn(false);

        boolean result = githubWebhookAppService.acceptPayload(securityKey, "eventType", payload);

        Assert.assertFalse(result);

        Mockito.verify(webhookVerifier).isPayloadLegitimate(securityKey, payload);
    }

    @Test
    public void acceptPayloadPingEvent() throws Exception {
        String securityKey = "key";
        String eventType = WebhookEvents.PING;
        String payload = ClasspathFileReader.readToString(TestFiles.PING_EVENT);

        Mockito.when(webhookVerifier.isPayloadLegitimate(securityKey, payload)).thenReturn(true);

        githubWebhookAppService.acceptPayload(securityKey, eventType, payload);

        // Verify that single logging action taken
        LoggingEvent expected = LoggingEvent.info("Received ping event. GitHub imparts wisdom: {}", "GitHub zen");

        Optional<LoggingEvent> actual = testLogger.getLoggingEvents().stream()
                .filter(input -> Objects.equals(input, expected))
                .findAny();

        Assert.assertTrue(actual.isPresent(), "No logging matching expected statements");

        Mockito.verify(webhookVerifier).isPayloadLegitimate(securityKey, payload);
    }

    @Test
    public void acceptPayloadPullRequestEventAlteredOpened() throws Exception {
        String securityKey = "key";
        String eventType = WebhookEvents.PULL_REQUEST;
        String payload = ClasspathFileReader.readToString(TestFiles.PULL_REQUEST_EVENT);

        JsonObject updatedAction = new JsonParser().parse(payload).getAsJsonObject();
        updatedAction.addProperty("action", "opened");

        payload = gson.toJson(updatedAction);

        PullRequestAlteredEvent expectedEvent = new PullRequestAlteredEvent(
                34778301,
                1,
                "https://api.github.com/repos/baxterthehacker/public-repo/pulls/1",
                "https://api.github.com/repos/baxterthehacker/public-repo/statuses/0d1a26e67d8f5eaf1f6ba5c57fc3c7d91ac0fd1c");

        Mockito.when(webhookVerifier.isPayloadLegitimate(securityKey, payload)).thenReturn(true);

        githubWebhookAppService.acceptPayload(securityKey, eventType, payload);

        Mockito.verify(applicationEventPublisher).publishEvent(expectedEvent);
        Mockito.verify(webhookVerifier).isPayloadLegitimate(securityKey, payload);
    }

    @Test
    public void acceptPayloadPullRequestEventAlteredEdited() throws Exception {
        String securityKey = "key";
        String eventType = WebhookEvents.PULL_REQUEST;
        String payload = ClasspathFileReader.readToString(TestFiles.PULL_REQUEST_EVENT);

        JsonObject updatedAction = new JsonParser().parse(payload).getAsJsonObject();
        updatedAction.addProperty("action", "edited");

        payload = gson.toJson(updatedAction);

        PullRequestAlteredEvent expectedEvent = new PullRequestAlteredEvent(
                34778301,
                1,
                "https://api.github.com/repos/baxterthehacker/public-repo/pulls/1",
                "https://api.github.com/repos/baxterthehacker/public-repo/statuses/0d1a26e67d8f5eaf1f6ba5c57fc3c7d91ac0fd1c");

        Mockito.when(webhookVerifier.isPayloadLegitimate(securityKey, payload)).thenReturn(true);

        githubWebhookAppService.acceptPayload(securityKey, eventType, payload);

        Mockito.verify(applicationEventPublisher).publishEvent(expectedEvent);
        Mockito.verify(webhookVerifier).isPayloadLegitimate(securityKey, payload);
    }

    @Test
    public void acceptPayloadPullRequestEventAlteredSynchronize() throws Exception {
        String securityKey = "key";
        String eventType = WebhookEvents.PULL_REQUEST;
        String payload = ClasspathFileReader.readToString(TestFiles.PULL_REQUEST_EVENT);

        JsonObject updatedAction = new JsonParser().parse(payload).getAsJsonObject();
        updatedAction.addProperty("action", "synchronize");

        payload = gson.toJson(updatedAction);

        PullRequestAlteredEvent expectedEvent = new PullRequestAlteredEvent(
                34778301,
                1,
                "https://api.github.com/repos/baxterthehacker/public-repo/pulls/1",
                "https://api.github.com/repos/baxterthehacker/public-repo/statuses/0d1a26e67d8f5eaf1f6ba5c57fc3c7d91ac0fd1c");

        Mockito.when(webhookVerifier.isPayloadLegitimate(securityKey, payload)).thenReturn(true);

        githubWebhookAppService.acceptPayload(securityKey, eventType, payload);

        Mockito.verify(applicationEventPublisher).publishEvent(expectedEvent);
        Mockito.verify(webhookVerifier).isPayloadLegitimate(securityKey, payload);
    }

    @Test
    public void acceptPayloadPullRequestEventAlteredReopened() throws Exception {
        String securityKey = "key";
        String eventType = WebhookEvents.PULL_REQUEST;
        String payload = ClasspathFileReader.readToString(TestFiles.PULL_REQUEST_EVENT);

        JsonObject updatedAction = new JsonParser().parse(payload).getAsJsonObject();
        updatedAction.addProperty("action", "reopened");

        payload = gson.toJson(updatedAction);

        PullRequestAlteredEvent expectedEvent = new PullRequestAlteredEvent(
                34778301,
                1,
                "https://api.github.com/repos/baxterthehacker/public-repo/pulls/1",
                "https://api.github.com/repos/baxterthehacker/public-repo/statuses/0d1a26e67d8f5eaf1f6ba5c57fc3c7d91ac0fd1c");

        Mockito.when(webhookVerifier.isPayloadLegitimate(securityKey, payload)).thenReturn(true);

        githubWebhookAppService.acceptPayload(securityKey, eventType, payload);

        Mockito.verify(applicationEventPublisher).publishEvent(expectedEvent);
        Mockito.verify(webhookVerifier).isPayloadLegitimate(securityKey, payload);
    }

    @Test
    public void acceptPayloadPullRequestEventNotAltered() throws Exception {
        String securityKey = "key";
        String eventType = WebhookEvents.PULL_REQUEST;
        String payload = ClasspathFileReader.readToString(TestFiles.PULL_REQUEST_EVENT);

        JsonObject updatedAction = new JsonParser().parse(payload).getAsJsonObject();
        updatedAction.addProperty("action", "closed");

        payload = gson.toJson(updatedAction);

        Mockito.when(webhookVerifier.isPayloadLegitimate(securityKey, payload)).thenReturn(true);

        githubWebhookAppService.acceptPayload(securityKey, eventType, payload);

        // Verify that single logging action taken
        LoggingEvent expected = LoggingEvent.debug("Received pull request event ({}: {})",
                "https://api.github.com/repos/baxterthehacker/public-repo/pulls/1", "closed");

        Optional<LoggingEvent> actual = testLogger.getLoggingEvents().stream()
                .filter(input -> Objects.equals(input, expected))
                .findAny();

        Assert.assertTrue(actual.isPresent(), "No logging matching expected statements");

        Mockito.verify(webhookVerifier).isPayloadLegitimate(securityKey, payload);
    }

    @Test
    public void acceptPayloadRepositoryEventPrivatized() throws Exception {
        String securityKey = "key";
        String eventType = WebhookEvents.REPOSITORY;
        String payload = ClasspathFileReader.readToString(TestFiles.REPOSITORY_EVENT);

        JsonObject updatedAction = new JsonParser().parse(payload).getAsJsonObject();
        updatedAction.addProperty("action", "privatized");

        payload = gson.toJson(updatedAction);

        RepositoryPrivatizedEvent expectedEvent = new RepositoryPrivatizedEvent(
                27496774,
                "baxterandthehackers",
                "new-repository");

        Mockito.when(webhookVerifier.isPayloadLegitimate(securityKey, payload)).thenReturn(true);

        githubWebhookAppService.acceptPayload(securityKey, eventType, payload);

        Mockito.verify(applicationEventPublisher).publishEvent(expectedEvent);
        Mockito.verify(webhookVerifier).isPayloadLegitimate(securityKey, payload);
    }

    @Test
    public void acceptPayloadRepositoryEventNotPrivatized() throws Exception {
        String securityKey = "key";
        String eventType = WebhookEvents.REPOSITORY;
        String payload = ClasspathFileReader.readToString(TestFiles.REPOSITORY_EVENT);

        JsonObject updatedAction = new JsonParser().parse(payload).getAsJsonObject();
        updatedAction.addProperty("action", "created");

        payload = gson.toJson(updatedAction);

        Mockito.when(webhookVerifier.isPayloadLegitimate(securityKey, payload)).thenReturn(true);

        githubWebhookAppService.acceptPayload(securityKey, eventType, payload);

        // Verify that single logging action taken
        LoggingEvent expected = LoggingEvent.debug("Received repository event ({}: {})",
                "baxterandthehackers/new-repository", "created");

        Optional<LoggingEvent> actual = testLogger.getLoggingEvents().stream()
                .filter(input -> Objects.equals(input, expected))
                .findAny();

        Assert.assertTrue(actual.isPresent(), "No logging matching expected statements");

        Mockito.verify(webhookVerifier).isPayloadLegitimate(securityKey, payload);
    }

    @Test
    public void acceptPayloadInstallationEvent() throws Exception {
        String securityKey = "key";
        String eventType = WebhookEvents.INSTALLATION;
        String payload = ClasspathFileReader.readToString(TestFiles.INSTALLATION_EVENT);

        Mockito.when(webhookVerifier.isPayloadLegitimate(securityKey, payload)).thenReturn(true);

        githubWebhookAppService.acceptPayload(securityKey, eventType, payload);

        // Verify that single logging action taken
        LoggingEvent expected = LoggingEvent.info("Received installation event ({}:{})", "octocat", "deleted");

        Optional<LoggingEvent> actual = testLogger.getLoggingEvents().stream()
                .filter(input -> Objects.equals(input, expected))
                .findAny();

        Assert.assertTrue(actual.isPresent(), "No logging matching expected statements");

        Mockito.verify(webhookVerifier).isPayloadLegitimate(securityKey, payload);
    }

    @Test
    public void acceptPayloadInstallationRepositoriesEvent() throws Exception {
        String securityKey = "key";
        String eventType = WebhookEvents.INSTALLATION_REPOSITORIES;
        String payload = ClasspathFileReader.readToString(TestFiles.INSTALLATION_REPOSITORIES_EVENT);

        Mockito.when(webhookVerifier.isPayloadLegitimate(securityKey, payload)).thenReturn(true);

        githubWebhookAppService.acceptPayload(securityKey, eventType, payload);

        // Verify that single logging action taken
        LoggingEvent expected = LoggingEvent.info(
                "Received installation_repositories event ((): {}), (Added: {}), (Removed: {})",
                "octocat", "removed", "", "octocat/Hello-World");

        Optional<LoggingEvent> actual = testLogger.getLoggingEvents().stream()
                .filter(input -> Objects.equals(input, expected))
                .findAny();

        Assert.assertTrue(actual.isPresent(), "No logging matching expected statements");

        Mockito.verify(webhookVerifier).isPayloadLegitimate(securityKey, payload);
    }

    @Test
    public void acceptPayloadUnrecognizedEventType() throws Exception {
        String securityKey = "key";
        String eventType = "huh-wat";
        String payload = ClasspathFileReader.readToString(TestFiles.INSTALLATION_REPOSITORIES_EVENT);

        Mockito.when(webhookVerifier.isPayloadLegitimate(securityKey, payload)).thenReturn(true);

        githubWebhookAppService.acceptPayload(securityKey, eventType, payload);

        // Verify that single logging action taken
        LoggingEvent expected = LoggingEvent.warn("Unrecognized event type {}", "huh-wat");

        Optional<LoggingEvent> actual = testLogger.getLoggingEvents().stream()
                .filter(input -> Objects.equals(input, expected))
                .findAny();

        Assert.assertTrue(actual.isPresent(), "No logging matching expected statements");

        Mockito.verify(webhookVerifier).isPayloadLegitimate(securityKey, payload);
    }

}
