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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;

/**
 * Represents installation data fields from webhook events of interest to the application
 *
 * <p>
 * Uses the Jackson library annotations to allow deserializing from a JSON string to a Java representation
 *
 * @author romeara
 * @since 0.1.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class InstallationMetaData {

    private final long id;

    private final OwnerMetaData account;

    /**
     * @param id
     *            GitHub identifier of the installation
     * @param account
     *            Details of the user/organization installed on
     * @since 0.1.0
     */
    @JsonCreator
    public InstallationMetaData(@JsonProperty(value = "id", required = true) long id,
            @JsonProperty(value = "account", required = true) OwnerMetaData account) {
        this.id = Objects.requireNonNull(id);
        this.account = Objects.requireNonNull(account);
    }

    /**
     * @return GitHub identifier of the installation since 0.1.0
     */
    public long getId() {
        return id;
    }

    /**
     * @return Details of the user/organization installed on since 0.1.0
     */
    public OwnerMetaData getAccount() {
        return account;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(),
                getAccount());
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        boolean result = false;

        if (obj instanceof InstallationMetaData) {
            InstallationMetaData compare = (InstallationMetaData) obj;

            result = Objects.equals(compare.getId(), getId())
                    && Objects.equals(compare.getAccount(), getAccount());
        }

        return result;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(getClass()).omitNullValues()
                .add("id", getId())
                .add("account", getAccount())
                .toString();
    }
}
