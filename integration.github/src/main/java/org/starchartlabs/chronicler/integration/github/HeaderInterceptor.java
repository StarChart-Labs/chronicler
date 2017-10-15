/*
 * Copyright 2017 StarChart Labs Authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.starchartlabs.chronicler.integration.github;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.function.Supplier;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

// TODO romeara doc, test
public class HeaderInterceptor implements ClientHttpRequestInterceptor {

    private final Supplier<HttpHeaders> headerSupplier;

    public HeaderInterceptor(Supplier<HttpHeaders> headerSupplier) {
        this.headerSupplier = Objects.requireNonNull(headerSupplier);
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        HttpHeaders headers = Objects.requireNonNull(headerSupplier.get());

        for (Entry<String, List<String>> entry : headers.entrySet()) {
            request = addHeader(request, entry.getKey(), entry.getValue());
        }

        return execution.execute(request, body);
    }

    private HttpRequest addHeader(HttpRequest request, String header, Collection<String> values) {
        Objects.requireNonNull(request);
        Objects.requireNonNull(header);
        Objects.requireNonNull(values);

        values.forEach(value -> request.getHeaders().add(header, value));

        return request;
    }

}
