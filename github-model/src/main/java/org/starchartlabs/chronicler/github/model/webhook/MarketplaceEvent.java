/*
 * Copyright (c) Jun 4, 2019 StarChart Labs Authors.
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
import java.util.Objects;

import javax.annotation.Nullable;

import org.starchartlabs.alloy.core.MoreObjects;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

//https://developer.github.com/marketplace/integrating-with-the-github-marketplace-api/github-marketplace-webhook-events/
public class MarketplaceEvent {

    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(MarketplaceEvent.class, new Deserializer())
            .create();

    private static final String EVENT_TYPE = "marketplace_purchase";

    // Action (action) [purchased|cancelled|pending_change|pending_change_cancelled|changed]
    private final String action;

    private final String accountLogin;

    private final String accountType;

    public MarketplaceEvent(String action, String accountLogin, String accountType) {
        this.action = Objects.requireNonNull(action);
        this.accountLogin = Objects.requireNonNull(accountLogin);
        this.accountType = Objects.requireNonNull(accountType);
    }

    public String getAction() {
        return action;
    }

    public String getAccountLogin() {
        return accountLogin;
    }

    public String getAccountType() {
        return accountType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getAction(),
                getAccountLogin(),
                getAccountType());
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        boolean result = false;

        if (obj instanceof MarketplaceEvent) {
            MarketplaceEvent compare = (MarketplaceEvent) obj;

            result = Objects.equals(compare.getAction(), getAction())
                    && Objects.equals(compare.getAccountLogin(), getAccountLogin())
                    && Objects.equals(compare.getAccountType(), getAccountType());
        }

        return result;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(getClass()).omitNullValues()
                .add("action", getAction())
                .add("accountLogin", getAccountLogin())
                .add("accountType", getAccountType())
                .toString();
    }

    public static boolean isCompatibleWithEventType(String eventType) {
        Objects.requireNonNull(eventType);

        return Objects.equals(EVENT_TYPE, eventType);
    }

    public static MarketplaceEvent fromJson(String json) {
        return GSON.fromJson(json, MarketplaceEvent.class);
    }

    private static final class Deserializer implements JsonDeserializer<MarketplaceEvent> {

        @Override
        public MarketplaceEvent deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            JsonObject event = json.getAsJsonObject();
            JsonObject marketplacePurchase = event.get("marketplace_purchase").getAsJsonObject();
            JsonObject account = marketplacePurchase.get("account").getAsJsonObject();

            String action = event.get("action").getAsString();

            String accountLogin = account.get("login").getAsString();
            String accountType = account.get("type").getAsString();

            return new MarketplaceEvent(action, accountLogin, accountType);
        }

    }

}
