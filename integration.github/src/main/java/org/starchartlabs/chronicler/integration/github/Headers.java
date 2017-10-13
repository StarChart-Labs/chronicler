/*
 * Copyright (C) 2017 Black Duck Software Inc.
 * http://www.blackducksoftware.com/
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Black Duck Software ("Confidential Information"). You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Black Duck Software.
 */
package org.starchartlabs.chronicler.integration.github;

// TODO romeara doc, test
public final class Headers {

    public static final String WEBHOOK_EVENT_TYPE = "X-GitHub-Event";

    public static final String WEBHOOK_EVENT_ID = "X-GitHub-Delivery";

    public static final String WEBHOOK_SECURITY = "X-Hub-Signature";

    /**
     * Prevent instantiation of utility class
     */
    private Headers() throws InstantiationException {
        throw new InstantiationException("Cannot instantiate instance of utility class '" + getClass().getName() + "'");
    }

}
