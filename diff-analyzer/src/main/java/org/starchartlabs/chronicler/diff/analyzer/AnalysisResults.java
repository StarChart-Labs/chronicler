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

import java.util.Objects;

import javax.annotation.Nullable;

import org.starchartlabs.alloy.core.MoreObjects;

public class AnalysisResults {

    private final boolean modifyingProductionFiles;

    private final boolean modifyingReleaseNotes;

    public AnalysisResults(boolean modifyingProductionFiles, boolean modifyingReleaseNotes) {
        this.modifyingProductionFiles = modifyingProductionFiles;
        this.modifyingReleaseNotes = modifyingReleaseNotes;
    }

    public boolean isModifyingProductionFiles() {
        return modifyingProductionFiles;
    }

    public boolean isModifyingReleaseNotes() {
        return modifyingReleaseNotes;
    }

    public boolean isDocumented() {
        return isModifyingReleaseNotes() || !isModifyingProductionFiles();
    }

    @Override
    public int hashCode() {
        return Objects.hash(isModifyingProductionFiles(),
                isModifyingReleaseNotes());
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        boolean result = false;

        if (obj instanceof AnalysisResults) {
            AnalysisResults compare = (AnalysisResults) obj;

            result = Objects.equals(compare.isModifyingProductionFiles(), isModifyingProductionFiles())
                    && Objects.equals(compare.isModifyingReleaseNotes(), isModifyingReleaseNotes());
        }

        return result;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(getClass()).omitNullValues()
                .add("modifyingProductionFiles", isModifyingProductionFiles())
                .add("modifyingReleaseNotes", isModifyingReleaseNotes())
                .toString();
    }

}
