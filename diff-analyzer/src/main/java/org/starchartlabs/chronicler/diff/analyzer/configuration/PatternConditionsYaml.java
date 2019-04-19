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

import java.util.Collection;
import java.util.Objects;

import javax.annotation.Nullable;

import org.starchartlabs.alloy.core.MoreObjects;

/**
 * Represents a sub-set of files to include and exclude when determining file grouping for processing
 *
 * @author romeara
 * @since 0.2.0
 */
public class PatternConditionsYaml {

    private Collection<String> include;

    private Collection<String> exclude;

    public Collection<String> getInclude() {
        return include;
    }

    public void setInclude(Collection<String> include) {
        this.include = include;
    }

    public Collection<String> getExclude() {
        return exclude;
    }

    public void setExclude(Collection<String> exclude) {
        this.exclude = exclude;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getInclude(),
                getExclude());
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        boolean result = false;

        if (obj instanceof PatternConditionsYaml) {
            PatternConditionsYaml compare = (PatternConditionsYaml) obj;

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
