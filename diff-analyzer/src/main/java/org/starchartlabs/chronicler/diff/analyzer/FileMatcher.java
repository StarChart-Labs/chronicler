/*
 * Copyright (c) Jan 22, 2019 StarChart Labs Authors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    romeara - initial API and implementation and/or initial documentation
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
