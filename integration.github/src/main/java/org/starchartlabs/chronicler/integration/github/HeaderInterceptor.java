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
