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
package org.starchartlabs.chronicler.github.model;

import okhttp3.Request;

//TODO romeara
public final class Requests {

    public static final String USER_AGENT = "StarChart-Labs/Chronicler";

    /**
     * Prevent instantiation of utility class
     */
    private Requests() throws InstantiationException {
        throw new InstantiationException("Cannot instantiate instance of utility class '" + getClass().getName() + "'");
    }

    public static Request.Builder newRequest() {
        return new Request.Builder()
                .header("User-Agent", USER_AGENT);
    }
}
