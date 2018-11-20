/*
 * Copyright (c) Nov 19, 2018 StarChart Labs Authors.
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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.annotation.Nullable;

import org.starchartlabs.alloy.core.MoreObjects;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

//https://developer.github.com/v3/activity/events/types/#installationevent
public class InstallationEvent {

    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(InstallationEvent.class, new Deserializer())
            .create();

    private static final String EVENT_TYPE = "installation";

    // Action (action) [created|deleted] installed or uninstalled (str)
    private final String action;

    private final String accountName;

    // Repositories (account.name/repositories[].(name|<private repository>)) (str[])
    private final List<String> loggableRepositoryNames;

    public InstallationEvent(String action, String accountName, List<String> loggableRepositoryNames) {
        this.action = Objects.requireNonNull(action);
        this.accountName = Objects.requireNonNull(accountName);
        this.loggableRepositoryNames = Objects.requireNonNull(loggableRepositoryNames);
    }

    public String getAction() {
        return action;
    }

    public String getAccountName() {
        return accountName;
    }

    public List<String> getLoggableRepositoryNames() {
        return loggableRepositoryNames;
    }

    public boolean isInstallation() {
        return Objects.equals("created", action);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getAction(),
                getAccountName(),
                getLoggableRepositoryNames());
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        boolean result = false;

        if (obj instanceof InstallationEvent) {
            InstallationEvent compare = (InstallationEvent) obj;

            result = Objects.equals(compare.getAction(), getAction())
                    && Objects.equals(compare.getAccountName(), getAccountName())
                    && Objects.equals(compare.getLoggableRepositoryNames(), getLoggableRepositoryNames());
        }

        return result;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(getClass()).omitNullValues()
                .add("action", getAction())
                .add("accountName", getAccountName())
                .add("loggableRepositoryNames", getLoggableRepositoryNames())
                .toString();
    }

    public static boolean isCompatibleWithEventType(String eventType) {
        Objects.requireNonNull(eventType);

        return Objects.equals(EVENT_TYPE, eventType);
    }

    public static InstallationEvent fromJson(String json) {
        return GSON.fromJson(json, InstallationEvent.class);
    }

    private static final class Deserializer implements JsonDeserializer<InstallationEvent> {

        @Override
        public InstallationEvent deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            JsonObject event = json.getAsJsonObject();
            JsonObject installation = event.get("installation").getAsJsonObject();
            JsonObject account = installation.get("account").getAsJsonObject();


            String action = event.get("action").getAsString();
            String owner = account.get("login").getAsString();

            List<String> loggableRepositoryNames = new ArrayList<>();

            if (event.has("repositories")) {
                JsonArray repositories = event.get("repositories").getAsJsonArray();

                for (JsonElement repository : repositories) {
                    loggableRepositoryNames.add(getLoggableName(owner, repository.getAsJsonObject()));
                }
            }

            return new InstallationEvent(action, owner, loggableRepositoryNames);
        }

        private String getLoggableName(String owner, JsonObject repository) {
            String loggableRepositoryName = owner + "/<private repository>";

            if (!repository.get("private").getAsBoolean()) {
                loggableRepositoryName = repository.get("full_name").getAsString();
            }

            return loggableRepositoryName;
        }
    }

}
