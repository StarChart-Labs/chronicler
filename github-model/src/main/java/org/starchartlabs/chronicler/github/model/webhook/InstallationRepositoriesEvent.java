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
package org.starchartlabs.chronicler.github.model.webhook;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.starchartlabs.alloy.core.MoreObjects;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

//https://developer.github.com/v3/activity/events/types/#installationrepositoriesevent
public class InstallationRepositoriesEvent {

    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(InstallationRepositoriesEvent.class, new Deserializer())
            .create();

    private static final String EVENT_TYPE = "installation_repositories";

    // TODO
    // Action (action) [added|removed] installed or uninstalled (str)
    private final String action;

    private final String accountName;

    // Repositories (account.name/repositories_(added|removed)[].(name|<private repository>)) (str[])
    private final List<String> loggableRepositoryNames;

    public InstallationRepositoriesEvent(String action, String accountName, List<String> loggableRepositoryNames) {
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
        return Objects.equals("added", action);
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

        if (obj instanceof InstallationRepositoriesEvent) {
            InstallationRepositoriesEvent compare = (InstallationRepositoriesEvent) obj;

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

    public static InstallationRepositoriesEvent fromJson(String json) {
        return GSON.fromJson(json, InstallationRepositoriesEvent.class);
    }

    private static final class Deserializer implements JsonDeserializer<InstallationRepositoriesEvent> {

        /** Logger reference to output information to the application log files */
        private final Logger logger = LoggerFactory.getLogger(getClass());

        @Override
        public InstallationRepositoriesEvent deserialize(JsonElement json, Type typeOfT,
                JsonDeserializationContext context)
                        throws JsonParseException {
            JsonObject event = json.getAsJsonObject();
            JsonObject installation = event.get("installation").getAsJsonObject();
            JsonObject account = installation.get("account").getAsJsonObject();


            String action = event.get("action").getAsString();
            String owner = account.get("login").getAsString();

            JsonArray repositories = null;

            if (Objects.equals(action, "added")) {
                repositories = event.get("repositories_added").getAsJsonArray();
            } else if (Objects.equals(action, "removed")) {
                repositories = event.get("repositories_removed").getAsJsonArray();
            }

            List<String> loggableRepositoryNames = new ArrayList<>();

            if (repositories != null) {
                for (JsonElement repository : repositories) {
                    loggableRepositoryNames.add(getLoggableName(owner, repository.getAsJsonObject()));
                }
            } else {
                logger.error("Unexpected installtion_repositories action '{}'", action);
            }

            return new InstallationRepositoriesEvent(action, owner, loggableRepositoryNames);
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
