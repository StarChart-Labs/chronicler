/*
 * Copyright (c) Oct 1, 2018 StarChart Labs Authors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    romeara - initial API and implementation and/or initial documentation
 */
package org.starchartlabs.chronicler.calamari.core;

/**
 * Represents various data format identifiers which may be sent or requested to/from GitHub via Accept/Content-Type
 * headers in web requests
 *
 * @author romeara
 * @since 0.1.0
 */
public final class MediaTypes {

    /**
     * Media type used by GitHub app's during the preview period
     * 
     * @since 0.1.0
     */
    public static final String APP_PREVIEW = "application/vnd.github.machine-man-preview+json";

    /**
     * Prevent instantiation of utility class
     */
    private MediaTypes() throws InstantiationException {
        throw new InstantiationException("Cannot instantiate instance of utility class '" + getClass().getName() + "'");
    }

}
