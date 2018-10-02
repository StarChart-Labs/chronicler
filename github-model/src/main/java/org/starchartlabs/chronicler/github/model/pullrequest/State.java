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
package org.starchartlabs.chronicler.github.model.pullrequest;

import com.google.gson.annotations.SerializedName;

//TODO romeara intentionally package-private
enum State {

    @SerializedName("error")
    ERROR,

    @SerializedName("failure")
    FAILURE,

    @SerializedName("pending")
    PENDING,

    @SerializedName("success")
    SUCCESS,

    ;

}