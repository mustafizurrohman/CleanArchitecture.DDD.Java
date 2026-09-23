package com.mustafizur.cleanarchitecture.application.diagnostics;

import com.mustafizur.cleanarchitecture.application.messaging.Query;
import com.mustafizur.cleanarchitecture.application.port.out.ExternalServicePort.ExternalServiceResult;

public record PingExternalServiceQuery() implements Query<ExternalServiceResult> {
}
