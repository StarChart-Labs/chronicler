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

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import javax.annotation.Nullable;

import org.starchartlabs.alloy.core.MoreObjects;
import org.starchartlabs.calamari.core.auth.InstallationAccessToken;
import org.starchartlabs.calamari.core.content.FileContentLoader;
import org.starchartlabs.chronicler.diff.analyzer.configuration.AnalysisSettingsYaml;
import org.starchartlabs.chronicler.diff.analyzer.configuration.PatternConditionsYaml;
import org.starchartlabs.chronicler.diff.analyzer.exception.InvalidConfigurationException;
import org.starchartlabs.chronicler.github.model.Requests;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.error.YAMLException;

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
            String branch, String path) throws InvalidConfigurationException {
        Objects.requireNonNull(accessToken);
        Objects.requireNonNull(repositoryUrl);
        Objects.requireNonNull(branch);
        Objects.requireNonNull(path);

        FileContentLoader contentLoader = new FileContentLoader(Requests.USER_AGENT);

        return contentLoader.loadContents(accessToken, repositoryUrl, branch, path)
                .flatMap(AnalysisSettings::fromYaml)
                .orElse(DEFAULT_SETTINGS);
    }

    public static Optional<AnalysisSettings> fromYaml(String yamlContents) throws InvalidConfigurationException {
        Objects.requireNonNull(yamlContents);

        Yaml yaml = new Yaml(new Constructor(AnalysisSettingsYaml.class));

        try {
            return Optional.ofNullable((AnalysisSettingsYaml) yaml.load(yamlContents))
                    .map(AnalysisSettings::fromFile);
        } catch (YAMLException e) {
            throw new InvalidConfigurationException("Error parsing YAML configuration", e);
        }
    }

    private static AnalysisSettings fromFile(AnalysisSettingsYaml fileContents) {
        Objects.requireNonNull(fileContents);

        PatternConditionsYaml productionFilesYaml = Optional.ofNullable(fileContents.getProductionFiles())
                .orElse(DEFAULT_YAML.getProductionFiles());
        PatternConditionsYaml releaseNoteFilesYaml = Optional.ofNullable(fileContents.getReleaseNoteFiles())
                .orElse(DEFAULT_YAML.getReleaseNoteFiles());

        PatternConditions productionFiles = toPatternConditions(productionFilesYaml);
        PatternConditions releaseNoteFiles = toPatternConditions(releaseNoteFilesYaml);

        return new AnalysisSettings(productionFiles, releaseNoteFiles);
    }

    private static PatternConditions toPatternConditions(PatternConditionsYaml yaml) {
        Objects.requireNonNull(yaml);

        Set<PathMatcher> includePatterns = new HashSet<>();
        Set<PathMatcher> excludePatterns = new HashSet<>();

        if (yaml.getInclude() != null) {
            yaml.getInclude().stream()
            .map(FileMatcher::new)
            .forEach(includePatterns::add);
        }

        if (yaml.getExclude() != null) {
            yaml.getExclude().stream()
            .map(FileMatcher::new)
            .forEach(excludePatterns::add);
        }

        return new PatternConditions(includePatterns, excludePatterns);
    }

    private static AnalysisSettingsYaml loadDefaultYaml() {
        Yaml yaml = new Yaml(new Constructor(AnalysisSettingsYaml.class));

        try (InputStream stream = AnalysisSettings.class.getClassLoader()
                .getResourceAsStream(DEFAULT_SETTINGS_FILE.toString())) {
            return yaml.load(stream);
        } catch (YAMLException e) {
            throw new InvalidConfigurationException("Error parsing default YAML configuration", e);
        } catch (IOException e) {
            throw new RuntimeException("Error loading default analysis settings", e);
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(productionFiles,
                releaseNoteFiles);
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        boolean result = false;

        if (obj instanceof AnalysisSettings) {
            AnalysisSettings compare = (AnalysisSettings) obj;

            result = Objects.equals(productionFiles, compare.productionFiles)
                    && Objects.equals(releaseNoteFiles, compare.releaseNoteFiles);
        }

        return result;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(getClass()).omitNullValues()
                .add("productionFiles", productionFiles)
                .add("releaseNoteFiles", releaseNoteFiles)
                .toString();
    }

}
