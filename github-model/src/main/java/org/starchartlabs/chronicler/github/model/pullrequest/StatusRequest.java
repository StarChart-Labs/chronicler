/*
 * Copyright (c) Oct 1, 2018 StarChart Labs Authors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    romeara - initial API and implementation and/or initial documentation
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