/*
 * Copyright (c) Sep 26, 2018 StarChart Labs Authors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    romeara - initial API and implementation and/or initial documentation
 */
package org.starchartlabs.chronicler.diff.analyzer;

import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.Objects;
import java.util.Set;

import javax.annotation.Nullable;

import org.starchartlabs.alloy.core.MoreObjects;

//TODO romeara
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
