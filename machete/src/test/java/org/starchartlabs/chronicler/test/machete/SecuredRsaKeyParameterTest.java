/*
 * Copyright (c) Nov 2, 2018 StarChart Labs Authors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    romeara - initial API and implementation and/or initial documentation
 */
package org.starchartlabs.chronicler.test.machete;

import java.util.Map.Entry;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.starchartlabs.chronicler.machete.SecuredRsaKeyParameter;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.amazonaws.services.simplesystemsmanagement.AWSSimpleSystemsManagement;
import com.amazonaws.services.simplesystemsmanagement.model.GetParameterRequest;
import com.amazonaws.services.simplesystemsmanagement.model.GetParameterResult;
import com.amazonaws.services.simplesystemsmanagement.model.Parameter;

public class SecuredRsaKeyParameterTest {

    @Mock
    private AWSSimpleSystemsManagement systemManagementClient;

    @BeforeMethod
    public void prepareMocks() {
        MockitoAnnotations.initMocks(this);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void fromEnvNullSystemManagementClient() throws Exception {
        SecuredRsaKeyParameter.fromEnv(null, "ENV_VAR");
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void fromEnvNullEnvironmentVariable() throws Exception {
        SecuredRsaKeyParameter.fromEnv(systemManagementClient, null);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void fromEnvEnvironmentVariableNotSet() throws Exception {
        SecuredRsaKeyParameter.fromEnv(systemManagementClient, "ENV_VAR");
    }

    @Test
    public void get() throws Exception {
        Entry<String, String> existingEnv = System.getenv().entrySet().stream().findFirst()
                .orElseThrow(() -> new AssertionError("No existing environment to use for testing"));

        GetParameterRequest expectedRequest = new GetParameterRequest();
        expectedRequest.withName(existingEnv.getValue());
        expectedRequest.setWithDecryption(true);

        Parameter param = Mockito.mock(Parameter.class);
        Mockito.when(param.getValue()).thenReturn("value");

        GetParameterResult result = new GetParameterResult();
        result.setParameter(param);

        try {
            Mockito.when(systemManagementClient.getParameter(expectedRequest)).thenReturn(result);

            String value = SecuredRsaKeyParameter.fromEnv(systemManagementClient, existingEnv.getKey()).get();

            Assert.assertNotNull(value);
            Assert.assertEquals(value, "-----BEGIN RSA PRIVATE KEY-----\nvalue\n-----END RSA PRIVATE KEY-----");
        } finally {
            Mockito.verify(systemManagementClient).getParameter(expectedRequest);
            Mockito.verify(param).getValue();
        }
    }

}
