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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;

/**
 * Represents a JSON payload from a GitHub webhook event which confirms webhook setup
 *
 * @author romeara
 * @since 0.1.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PingEvent {

    private final String zen;

    /**
     * @param zen
     *            Randomly generated wisdom from GitHub
     * @since 0.1.0
     */
    public PingEvent(@JsonProperty(value = "zen", required = true) String zen) {
        this.zen = Objects.requireNonNull(zen);
    }

    /**
     * @return Randomly generated wisdom from GitHub
     * @since 0.1.0
     */
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

}
