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
package org.starchartlabs.chronicler.machete;

import java.util.Objects;
import java.util.function.Supplier;

import org.starchartlabs.alloy.core.Preconditions;
import org.starchartlabs.alloy.core.Strings;

import com.amazonaws.services.simplesystemsmanagement.AWSSimpleSystemsManagement;
import com.amazonaws.services.simplesystemsmanagement.AWSSimpleSystemsManagementClientBuilder;
import com.amazonaws.services.simplesystemsmanagement.model.GetParameterRequest;
import com.amazonaws.services.simplesystemsmanagement.model.GetParameterResult;

/**
 * Represents repeatable access to a string parameter stored on Amazon SSM
 *
 * <p>
 * Implements supplier logic to allow repeated reading of potentially changing values
 *
 * @author romeara
 * @since 0.1.0
 */
public class StringParameter implements Supplier<String> {

    private final AWSSimpleSystemsManagement systemsManagementClient;

    private final String parameterKey;

    /**
     * @param systemsManagementClient
     *            Client instance to use when reading from SSM
     * @param parameterKey
     *            The name of the parameter to access
     * @since 0.1.0
     */
    private StringParameter(AWSSimpleSystemsManagement systemsManagementClient, String parameterKey) {
        this.systemsManagementClient = Objects.requireNonNull(systemsManagementClient);
        this.parameterKey = Objects.requireNonNull(parameterKey);
    }

    @Override
    public String get() {
        GetParameterRequest getParameterRequest = new GetParameterRequest();
        getParameterRequest.withName(parameterKey);
        getParameterRequest.setWithDecryption(false);

        GetParameterResult result = systemsManagementClient.getParameter(getParameterRequest);

        return result.getParameter().getValue();
    }

    /**
     * Creates a new reference to a string parameter stored on Amazon SSM
     *
     * <p>
     * Uses the default AWS system management client
     *
     * @param environmentVariable
     *            The name of an environment variable where the name of the parameter to access is stored
     * @return A reference for accessing the stored SSM parameter
     * @since 0.1.0
     */
    public static StringParameter fromEnv(String environmentVariable) {
        return fromEnv(AWSSimpleSystemsManagementClientBuilder.defaultClient(), environmentVariable);
    }

    /**
     * Creates a new reference to a string parameter stored on Amazon SSM
     *
     * @param systemsManagementClient
     *            Client instance to use when reading from SSM
     * @param environmentVariable
     *            The name of an environment variable where the name of the parameter to access is stored
     * @return A reference for accessing the stored SSM parameter
     * @since 0.1.0
     */
    public static StringParameter fromEnv(AWSSimpleSystemsManagement systemsManagementClient,
            String environmentVariable) {
        Objects.requireNonNull(systemsManagementClient);
        Objects.requireNonNull(environmentVariable);

        String parameterKey = System.getenv(environmentVariable);

        Preconditions.checkArgument(parameterKey != null,
                () -> Strings.format("Environment variable '%s' is not set", environmentVariable));

        return new StringParameter(systemsManagementClient, parameterKey);
    }

}
