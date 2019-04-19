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
package org.starchartlabs.chronicler.diff.analyzer;

import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.Objects;

import javax.annotation.Nullable;

import org.starchartlabs.alloy.core.MoreObjects;

/**
 * Wrapper for standard {@link PathMatcher} which retains the basic pattern used for easier logging and debugging within
 * client applications
 *
 * @author romeara
 * @since 0.2.0
 */
public class FileMatcher implements PathMatcher {

    private final String pathPattern;

    private final PathMatcher delegate;

    public FileMatcher(String pathPattern) {
        this.pathPattern = Objects.requireNonNull(pathPattern.trim().toLowerCase());
        delegate = FileSystems.getDefault().getPathMatcher("glob:" + this.pathPattern.trim().toLowerCase());
    }

    @Override
    public boolean matches(Path path) {
        return delegate.matches(path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pathPattern);
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        boolean result = false;

        if (obj instanceof FileMatcher) {
            FileMatcher compare = (FileMatcher) obj;

            result = Objects.equals(pathPattern, compare.pathPattern);
        }

        return result;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(getClass()).omitNullValues()
                .add("pathPattern", pathPattern)
                .toString();
    }

}
