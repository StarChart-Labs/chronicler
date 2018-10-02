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

import com.amazonaws.services.simplesystemsmanagement.AWSSimpleSystemsManagement;
import com.amazonaws.services.simplesystemsmanagement.AWSSimpleSystemsManagementClientBuilder;
import com.amazonaws.services.simplesystemsmanagement.model.GetParameterRequest;
import com.amazonaws.services.simplesystemsmanagement.model.GetParameterResult;

//TODO romeara
public class SecuredParameter implements Supplier<String> {

    private final AWSSimpleSystemsManagement systemsManagementClient;

    private final String parameterKey;

    private SecuredParameter(AWSSimpleSystemsManagement systemsManagementClient, String parameterKey) {
        this.systemsManagementClient = Objects.requireNonNull(systemsManagementClient);
        this.parameterKey = Objects.requireNonNull(parameterKey);
    }

    @Override
    public String get() {
        GetParameterRequest getParameterRequest = new GetParameterRequest();
        getParameterRequest.withName(parameterKey);
        getParameterRequest.setWithDecryption(true);

        GetParameterResult result = systemsManagementClient.getParameter(getParameterRequest);

        return result.getParameter().getValue();
    }

    public static SecuredParameter fromEnv(String environmentVariable) {
        return fromEnv(AWSSimpleSystemsManagementClientBuilder.defaultClient(), environmentVariable);
    }

    public static SecuredParameter fromEnv(AWSSimpleSystemsManagement systemsManagementClient,
            String environmentVariable) {
        Objects.requireNonNull(systemsManagementClient);
        Objects.requireNonNull(environmentVariable);

        String parameterKey = System.getenv(environmentVariable);

        return new SecuredParameter(systemsManagementClient, parameterKey);
    }

}
