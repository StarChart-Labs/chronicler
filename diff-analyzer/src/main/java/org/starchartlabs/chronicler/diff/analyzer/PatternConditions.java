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

import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.Objects;
import java.util.Set;

import javax.annotation.Nullable;

import org.starchartlabs.alloy.core.MoreObjects;

/**
 * Represents the file patterns included and excluded when grouping a file based on its location in a project
 *
 * @author romeara
 * @since 0.1.0
 */
public class PatternConditions {

    private final Set<PathMatcher> include;

    private final Set<PathMatcher> exclude;

    public PatternConditions(Set<PathMatcher> include, Set<PathMatcher> exclude) {
        this.include = Objects.requireNonNull(include);
        this.exclude = Objects.requireNonNull(exclude);
    }

    public Set<PathMatcher> getInclude() {
        return include;
    }

    public Set<PathMatcher> getExclude() {
        return exclude;
    }

    public boolean matches(Path path) {
        Objects.requireNonNull(path);

        return include.stream().anyMatch(p -> p.matches(path))
                && exclude.stream().noneMatch(p -> p.matches(path));
    }

    @Override
    public int hashCode() {
        return Objects.hash(getInclude(),
                getExclude());
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        boolean result = false;

        if (obj instanceof PatternConditions) {
            PatternConditions compare = (PatternConditions) obj;

            result = Objects.equals(compare.getInclude(), getInclude())
                    && Objects.equals(compare.getExclude(), getExclude());
        }

        return result;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(getClass()).omitNullValues()
                .add("include", getInclude())
                .add("exclude", getExclude())
                .toString();
    }

}
