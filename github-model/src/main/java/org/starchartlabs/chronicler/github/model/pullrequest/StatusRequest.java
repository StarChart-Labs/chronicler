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
package org.starchartlabs.chronicler.github.model.pullrequest;

import java.util.Objects;

import javax.annotation.Nullable;

import org.starchartlabs.alloy.core.MoreObjects;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class StatusRequest {

    private static final Gson GSON = new GsonBuilder().create();

    private final State state;

    private final String description;

    private final String context;

    protected StatusRequest(State state, String description, String context) {
        this.state = Objects.requireNonNull(state);
        this.description = Objects.requireNonNull(description);
        this.context = Objects.requireNonNull(context);
    }

    public State getState() {
        return state;
    }

    public String getDescription() {
        return description;
    }

    public String getContext() {
        return context;
    }

    public String toJson() {
        return GSON.toJson(this);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getState(),
                getDescription(),
                getContext());
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        boolean result = false;

        if (obj instanceof StatusRequest) {
            StatusRequest compare = (StatusRequest) obj;

            result = Objects.equals(compare.getState(), getState())
                    && Objects.equals(compare.getDescription(), getDescription())
                    && Objects.equals(compare.getContext(), getContext());
        }

        return result;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(getClass()).omitNullValues()
                .add("state", getState())
                .add("description", getDescription())
                .add("context", getContext())
                .toString();
    }

}