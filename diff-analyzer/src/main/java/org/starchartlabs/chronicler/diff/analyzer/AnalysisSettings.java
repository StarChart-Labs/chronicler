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

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import org.starchartlabs.alloy.core.MoreObjects;
import org.starchartlabs.calamari.core.auth.InstallationAccessToken;
import org.starchartlabs.chronicler.calamari.core.files.FileContentLoader;
import org.starchartlabs.chronicler.diff.analyzer.configuration.AnalysisSettingsYaml;
import org.starchartlabs.chronicler.diff.analyzer.configuration.PatternConditionsYaml;
import org.starchartlabs.chronicler.github.model.Requests;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

public class AnalysisSettings {

    private static final Path DEFAULT_SETTINGS_FILE = Paths.get("org", "starchartlabs", "chronicler", "diff",
            "analyzer", "defaultAnalysisSettings.yml");

    private static final AnalysisSettingsYaml DEFAULT_YAML = loadDefaultYaml();

    private static final AnalysisSettings DEFAULT_SETTINGS = fromFile(DEFAULT_YAML);

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

    public static AnalysisSettings forRepository(InstallationAccessToken accessToken, String repositoryUrl,
            String branch, String path) {
        Objects.requireNonNull(accessToken);
        Objects.requireNonNull(repositoryUrl);
        Objects.requireNonNull(branch);
        Objects.requireNonNull(path);

        Yaml yaml = new Yaml(new Constructor(AnalysisSettingsYaml.class));
        FileContentLoader contentLoader = new FileContentLoader(accessToken, Requests.USER_AGENT);

        return contentLoader.loadContents(repositoryUrl, branch, path)
                .map(s -> (AnalysisSettingsYaml) yaml.load(s))
                .map(AnalysisSettings::fromFile)
                .orElse(DEFAULT_SETTINGS);
    }

    // TODO obj methods

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(getClass()).omitNullValues()
                .add("productionFiles", productionFiles)
                .add("releaseNoteFiles", releaseNoteFiles)
                .toString();
    }

    public static Builder builder() {
        return new Builder();
    }

    private static AnalysisSettings fromFile(AnalysisSettingsYaml fileContents) {
        Objects.requireNonNull(fileContents);

        PatternConditionsYaml productionFiles = Optional.ofNullable(fileContents.getProductionFiles())
                .orElse(DEFAULT_YAML.getProductionFiles());
        PatternConditionsYaml releaseNoteFiles = Optional.ofNullable(fileContents.getReleaseNoteFiles())
                .orElse(DEFAULT_YAML.getReleaseNoteFiles());

        Builder builder = new Builder();

        if (productionFiles.getInclude() != null) {
            productionFiles.getInclude().stream()
            .forEach(builder::includeProduction);
        }

        if (productionFiles.getExclude() != null) {
            productionFiles.getExclude().stream()
            .forEach(builder::excludeProduction);
        }

        if (releaseNoteFiles.getInclude() != null) {
            releaseNoteFiles.getInclude().stream()
            .forEach(builder::includeReleaseNotes);
        }

        if (releaseNoteFiles.getExclude() != null) {
            releaseNoteFiles.getExclude().stream()
            .forEach(builder::excludeReleaseNotes);
        }

        return builder.build();
    }

    private static AnalysisSettingsYaml loadDefaultYaml() {
        Yaml yaml = new Yaml(new Constructor(AnalysisSettingsYaml.class));

        try (InputStream stream = AnalysisSettings.class.getClassLoader()
                .getResourceAsStream(DEFAULT_SETTINGS_FILE.toString())) {
            return yaml.load(stream);
        }catch(IOException e) {
            throw new RuntimeException("Error loading default analysis settings", e);
        }
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
