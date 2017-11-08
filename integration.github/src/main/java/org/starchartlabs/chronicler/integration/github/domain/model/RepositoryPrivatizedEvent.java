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
package org.starchartlabs.chronicler.integration.github.domain.model;

import java.util.Objects;

import javax.annotation.Nullable;

import com.google.common.base.MoreObjects;

//TODO romeara test
/**
 * Indicates that a GitHub repository has been moved from the "public" to "private" state
 *
 * @author romeara
 * @since 0.1.0
 */
public class RepositoryPrivatizedEvent {

    private final long id;

    private final String ownerName;

    private final String repositoryName;

    /**
     * @param id
     *            The GitHub identifier of the repository
     * @param ownerName
     *            The name of the organization/user which owns the repository
     * @param repositoryName
     *            The name of the repository
     * @since 0.1.0
     */
    public RepositoryPrivatizedEvent(long id, String ownerName, String repositoryName) {
        this.id = id;
        this.ownerName = Objects.requireNonNull(ownerName);
        this.repositoryName = Objects.requireNonNull(repositoryName);
    }

    /**
     * @return The GitHub identifier of the repository
     * @since 0.1.0
     */
    public long getId() {
        return id;
    }

    /**
     * @return The name of the organization/user which owns the repository
     * @since 0.1.0
     */
    public String getOwnerName() {
        return ownerName;
    }

    /**
     * @return The name of the repository
     * @since 0.1.0
     */
    public String getRepositoryName() {
        return repositoryName;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(),
                getOwnerName(),
                getRepositoryName());
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        boolean result = false;

        if (obj instanceof RepositoryPrivatizedEvent) {
            RepositoryPrivatizedEvent compare = (RepositoryPrivatizedEvent) obj;

            result = Objects.equals(compare.getId(), getId())
                    && Objects.equals(compare.getOwnerName(), getOwnerName())
                    && Objects.equals(compare.getRepositoryName(), getRepositoryName());
        }

        return result;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(getClass()).omitNullValues()
                .add("id", getId())
                .add("ownerName", getOwnerName())
                .add("repositoryName", getRepositoryName())
                .toString();
    }

}
