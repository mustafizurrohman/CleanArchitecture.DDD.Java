package com.mustafizur.cleanarchitecture.application.diagnostics;

import com.mustafizur.cleanarchitecture.application.messaging.QueryHandler;
import com.mustafizur.cleanarchitecture.application.port.out.ExternalServicePort;
import com.mustafizur.cleanarchitecture.application.port.out.ExternalServicePort.ExternalServiceResult;

public final class PingExternalServiceHandler implements QueryHandler<PingExternalServiceQuery, ExternalServiceResult> {
    private final ExternalServicePort externalService;

    public PingExternalServiceHandler(ExternalServicePort externalService) {
        this.externalService = externalService;
    }

    @Override
    public Class<PingExternalServiceQuery> queryType() {
        return PingExternalServiceQuery.class;
    }

    @Override
    public ExternalServiceResult handle(PingExternalServiceQuery query) {
        return externalService.ping();
    }
}
