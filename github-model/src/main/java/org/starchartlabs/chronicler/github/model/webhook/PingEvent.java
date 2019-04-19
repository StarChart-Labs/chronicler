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

import java.util.Objects;

import javax.annotation.Nullable;

import org.starchartlabs.alloy.core.MoreObjects;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

//https://developer.github.com/webhooks/#ping-event
public class PingEvent {

    private static final Gson GSON = new GsonBuilder().create();

    private static final String EVENT_TYPE = "ping";

    private final String zen;

    public PingEvent(String zen) {
        this.zen = Objects.requireNonNull(zen);
    }

    public String getZen() {
        return zen;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getZen());
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        boolean result = false;

        if (obj instanceof PingEvent) {
            PingEvent compare = (PingEvent) obj;

            result = Objects.equals(compare.getZen(), getZen());
        }

        return result;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(getClass()).omitNullValues()
                .add("zen", getZen())
                .toString();
    }

    public static boolean isCompatibleWithEventType(String eventType) {
        Objects.requireNonNull(eventType);

        return Objects.equals(EVENT_TYPE, eventType);
    }

    public static PingEvent fromJson(String json) {
        return GSON.fromJson(json, PingEvent.class);
    }

}
