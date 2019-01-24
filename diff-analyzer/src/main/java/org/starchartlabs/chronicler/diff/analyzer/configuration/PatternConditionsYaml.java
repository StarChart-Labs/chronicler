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
