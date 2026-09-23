package com.mustafizur.cleanarchitecture.infrastructure.http;

import com.mustafizur.cleanarchitecture.application.port.out.ExternalServicePort;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Component
public class ResilientExternalServiceClient implements ExternalServicePort {
    private final RestClient restClient;

    public ResilientExternalServiceClient(
            RestClient.Builder builder,
            @Value("${app.external-service.base-url:https://httpbin.org}") String baseUrl) {
        this.restClient = builder.baseUrl(baseUrl).build();
    }

    @Override
    @Retry(name = "externalService")
    @CircuitBreaker(name = "externalService", fallbackMethod = "fallback")
    public ExternalServiceResult ping() {
        @SuppressWarnings("unchecked")
        var body = restClient.get()
                .uri("/get?source=cleanarchitecture-ddd-java")
                .retrieve()
                .body(Map.class);
        return new ExternalServiceResult(true, "remote", body == null ? "empty response" : "HTTP call succeeded");
    }

    @SuppressWarnings("unused")
    private ExternalServiceResult fallback(Throwable throwable) {
        return new ExternalServiceResult(false, "fallback", throwable.getClass().getSimpleName());
    }
}
