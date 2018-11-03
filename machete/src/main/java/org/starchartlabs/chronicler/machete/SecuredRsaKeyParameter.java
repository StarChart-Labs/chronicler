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
 * Represents repeatable access to an encrypted RSA key parameter stored on Amazon SSM
 *
 * <p>
 * It is intended that the stored parameter is the value between the {@code -----BEGIN RSA PRIVATE KEY-----} and
 * {@code -----END RSA PRIVATE KEY-----}, excluding that header and footer - this utility re-adds those values to the
 * resulting value. This is due to an inability to store newlines within an SSM parameter, and RSA key libraries
 * expecting a specific format.
 *
 * <p>
 * Implements supplier logic to allow repeated reading of potentially changing values
 *
 * @author romeara
 * @since 0.1.0
 */
public class SecuredRsaKeyParameter implements Supplier<String> {

    private final AWSSimpleSystemsManagement systemsManagementClient;

    private final String parameterKey;

    /**
     * @param systemsManagementClient
     *            Client instance to use when reading from SSM
     * @param parameterKey
     *            The name of the parameter to access
     * @since 0.1.0
     */
    private SecuredRsaKeyParameter(AWSSimpleSystemsManagement systemsManagementClient, String parameterKey) {
        this.systemsManagementClient = Objects.requireNonNull(systemsManagementClient);
        this.parameterKey = Objects.requireNonNull(parameterKey);
    }

    @Override
    public String get() {
        GetParameterRequest getParameterRequest = new GetParameterRequest();
        getParameterRequest.withName(parameterKey);
        getParameterRequest.setWithDecryption(true);

        GetParameterResult result = systemsManagementClient.getParameter(getParameterRequest);

        String key = result.getParameter().getValue();

        return Strings.format("-----BEGIN RSA PRIVATE KEY-----\n%s\n-----END RSA PRIVATE KEY-----", key);
    }

    /**
     * Creates a new reference to a secured parameter stored on Amazon SSM that represents a RSA key
     *
     * <p>
     * Uses the default AWS system management client
     *
     * @param environmentVariable
     *            The name of an environment variable where the name of the parameter to access is stored
     * @return A secured parameter reference for accessing the stored SSM parameter
     * @since 0.1.0
     */
    public static SecuredRsaKeyParameter fromEnv(String environmentVariable) {
        return fromEnv(AWSSimpleSystemsManagementClientBuilder.defaultClient(), environmentVariable);
    }

    /**
     * Creates a new reference to a secured parameter stored on Amazon SSM that represents a RSA key
     *
     * @param systemsManagementClient
     *            Client instance to use when reading from SSM
     * @param environmentVariable
     *            The name of an environment variable where the name of the parameter to access is stored
     * @return A secured parameter reference for accessing the stored SSM parameter
     * @since 0.1.0
     */
    public static SecuredRsaKeyParameter fromEnv(AWSSimpleSystemsManagement systemsManagementClient,
            String environmentVariable) {
        Objects.requireNonNull(systemsManagementClient);
        Objects.requireNonNull(environmentVariable);

        String parameterKey = System.getenv(environmentVariable);

        Preconditions.checkArgument(parameterKey != null,
                () -> Strings.format("Environment variable '%s' is not set", environmentVariable));

        return new SecuredRsaKeyParameter(systemsManagementClient, parameterKey);
    }

}
