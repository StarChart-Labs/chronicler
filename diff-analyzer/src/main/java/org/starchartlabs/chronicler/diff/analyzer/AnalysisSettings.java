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

import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class AnalysisSettings {

    private final PatternConditions productionFiles;

    private final PatternConditions releaseNoteFiles;

    private AnalysisSettings(PatternConditions productionFiles, PatternConditions releaseNoteFiles) {
        this.productionFiles = Objects.requireNonNull(productionFiles);
        this.releaseNoteFiles = Objects.requireNonNull(releaseNoteFiles);
    }

    public boolean isProductionFile(String path) {
        Objects.requireNonNull(path);

        Path compare = Paths.get(path.trim().toLowerCase());

        return productionFiles.matches(compare);
    }

    public boolean isReleaseNoteFile(String path) {
        Objects.requireNonNull(path);

        Path compare = Paths.get(path.trim().toLowerCase());

        return releaseNoteFiles.matches(compare);
    }

    public static AnalysisSettings defaultSettings() {
        return AnalysisSettings.builder()
                .includeProduction("**/src/**")
                .excludeProduction("**/test/**")
                .includeReleaseNotes("**/CHANGE*LOG*")
                .includeReleaseNotes("**/RELEASE*NOTES*")
                .build();
    }

    // TODO forRepository

    // TODO obj methods

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private final Set<PathMatcher> includeProductionPatterns;

        private final Set<PathMatcher> excludeProductionPatterns;

        private final Set<PathMatcher> includeReleaseNotePatterns;

        private final Set<PathMatcher> excludeReleaseNotePatterns;

        private Builder() {
            includeProductionPatterns = new HashSet<>();
            excludeProductionPatterns = new HashSet<>();
            includeReleaseNotePatterns = new HashSet<>();
            excludeReleaseNotePatterns = new HashSet<>();
        }

        public Builder includeProduction(String pattern) {
            Objects.requireNonNull(pattern);

            includeProductionPatterns.add(toMatcher(pattern));

            return this;
        }

        public Builder excludeProduction(String pattern) {
            Objects.requireNonNull(pattern);

            excludeProductionPatterns.add(toMatcher(pattern));

            return this;
        }

        public Builder includeReleaseNotes(String pattern) {
            Objects.requireNonNull(pattern);

            includeReleaseNotePatterns.add(toMatcher(pattern));

            return this;
        }

        public Builder excludeReleaseNotes(String pattern) {
            Objects.requireNonNull(pattern);

            excludeReleaseNotePatterns.add(toMatcher(pattern));

            return this;
        }

        public AnalysisSettings build() {
            PatternConditions productionFiles = new PatternConditions(includeProductionPatterns,
                    excludeProductionPatterns);
            PatternConditions releaseNoteFiles = new PatternConditions(includeReleaseNotePatterns,
                    excludeReleaseNotePatterns);

            return new AnalysisSettings(productionFiles, releaseNoteFiles);
        }

        private PathMatcher toMatcher(String pattern) {
            Objects.requireNonNull(pattern);

            return FileSystems.getDefault().getPathMatcher("glob:" + pattern.trim().toLowerCase());
        }
    }

}
