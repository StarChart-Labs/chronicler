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

import org.starchartlabs.alloy.core.Strings;

import com.amazonaws.services.simplesystemsmanagement.AWSSimpleSystemsManagement;
import com.amazonaws.services.simplesystemsmanagement.AWSSimpleSystemsManagementClientBuilder;
import com.amazonaws.services.simplesystemsmanagement.model.GetParameterRequest;
import com.amazonaws.services.simplesystemsmanagement.model.GetParameterResult;

//TODO romeara
public class SecuredRsaKeyParameter implements Supplier<String> {

    private final AWSSimpleSystemsManagement systemsManagementClient;

    private final String parameterKey;

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

    public static SecuredRsaKeyParameter fromEnv(String environmentVariable) {
        return fromEnv(AWSSimpleSystemsManagementClientBuilder.defaultClient(), environmentVariable);
    }

    public static SecuredRsaKeyParameter fromEnv(AWSSimpleSystemsManagement systemsManagementClient,
            String environmentVariable) {
        Objects.requireNonNull(systemsManagementClient);
        Objects.requireNonNull(environmentVariable);

        String parameterKey = System.getenv(environmentVariable);

        return new SecuredRsaKeyParameter(systemsManagementClient, parameterKey);
    }

}
