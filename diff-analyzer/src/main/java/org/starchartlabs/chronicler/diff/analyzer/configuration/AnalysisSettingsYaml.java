/*
 * Copyright (c) Jan 21, 2019 StarChart Labs Authors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    romeara - initial API and implementation and/or initial documentation
 */
package org.starchartlabs.chronicler.diff.analyzer.configuration;

import java.util.Objects;

import javax.annotation.Nullable;

import org.starchartlabs.alloy.core.MoreObjects;

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
