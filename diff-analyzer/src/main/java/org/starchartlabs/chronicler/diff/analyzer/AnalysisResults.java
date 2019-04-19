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
