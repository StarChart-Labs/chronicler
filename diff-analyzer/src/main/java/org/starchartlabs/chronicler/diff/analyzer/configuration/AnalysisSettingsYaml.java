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
package org.starchartlabs.chronicler.diff.analyzer.configuration;

import java.util.Objects;

import javax.annotation.Nullable;

import org.starchartlabs.alloy.core.MoreObjects;

/**
 * Java representation of YAML configuration file accepted for determining processing behavior
 *
 * @author romeara
 * @since 0.2.0
 */
public class AnalysisSettingsYaml {

    private PatternConditionsYaml productionFiles;

    private PatternConditionsYaml releaseNoteFiles;

    @Nullable
    public PatternConditionsYaml getProductionFiles() {
        return productionFiles;
    }

    public void setProductionFiles(PatternConditionsYaml productionFiles) {
        this.productionFiles = productionFiles;
    }

    @Nullable
    public PatternConditionsYaml getReleaseNoteFiles() {
        return releaseNoteFiles;
    }

    public void setReleaseNoteFiles(PatternConditionsYaml releaseNoteFiles) {
        this.releaseNoteFiles = releaseNoteFiles;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getProductionFiles(),
                getReleaseNoteFiles());
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        boolean result = false;

        if (obj instanceof AnalysisSettingsYaml) {
            AnalysisSettingsYaml compare = (AnalysisSettingsYaml) obj;

            result = Objects.equals(compare.getProductionFiles(), getProductionFiles())
                    && Objects.equals(compare.getReleaseNoteFiles(), getReleaseNoteFiles());
        }

        return result;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(getClass()).omitNullValues()
                .add("productionFiles", getProductionFiles())
                .add("releaseNoteFiles", getReleaseNoteFiles())
                .toString();
    }

}
