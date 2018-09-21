/*
 * Copyright (c) Sep 20, 2018 StarChart Labs Authors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    romeara - initial API and implementation and/or initial documentation
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
