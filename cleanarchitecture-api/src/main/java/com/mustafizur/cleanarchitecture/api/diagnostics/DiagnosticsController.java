package com.mustafizur.cleanarchitecture.api.diagnostics;

import com.mustafizur.cleanarchitecture.application.diagnostics.PingExternalServiceQuery;
import com.mustafizur.cleanarchitecture.application.messaging.Mediator;
import com.mustafizur.cleanarchitecture.application.port.out.ExternalServicePort.ExternalServiceResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/diagnostics")
@Tag(name = "Diagnostics")
public class DiagnosticsController {
    private final Mediator mediator;

    public DiagnosticsController(Mediator mediator) {
        this.mediator = mediator;
    }

    @GetMapping("/outbound")
    @Operation(summary = "Demonstrate a resilient outbound HTTP call")
    public ExternalServiceResult outbound() {
        return mediator.send(new PingExternalServiceQuery());
    }
}
