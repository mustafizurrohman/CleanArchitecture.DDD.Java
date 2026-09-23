package com.mustafizur.cleanarchitecture.api.customer;

import com.mustafizur.cleanarchitecture.application.customer.CustomerView;
import com.mustafizur.cleanarchitecture.application.customer.command.ChangeCustomerEmailCommand;
import com.mustafizur.cleanarchitecture.application.customer.command.CreateCustomerCommand;
import com.mustafizur.cleanarchitecture.application.customer.command.DeactivateCustomerCommand;
import com.mustafizur.cleanarchitecture.application.customer.command.DeleteCustomerCommand;
import com.mustafizur.cleanarchitecture.application.customer.query.GetCustomerByIdQuery;
import com.mustafizur.cleanarchitecture.application.customer.query.ListCustomersQuery;
import com.mustafizur.cleanarchitecture.application.messaging.Mediator;
import com.mustafizur.cleanarchitecture.core.model.PageResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/customers")
@Tag(name = "Customers")
public class CustomerController {
    private final Mediator mediator;

    public CustomerController(Mediator mediator) {
        this.mediator = mediator;
    }

    @PostMapping
    @Operation(summary = "Create a customer")
    public ResponseEntity<CustomerView> create(@Valid @RequestBody CreateCustomerRequest request) {
        var created = mediator.send(new CreateCustomerCommand(request.name(), request.email()));
        return ResponseEntity.created(URI.create("/api/v1/customers/" + created.id())).body(created);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a customer")
    public CustomerView get(@PathVariable UUID id) {
        return mediator.send(new GetCustomerByIdQuery(id));
    }

    @GetMapping
    @Operation(summary = "List customers")
    public PageResult<CustomerView> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return mediator.send(new ListCustomersQuery(page, size));
    }

    @PutMapping("/{id}/email")
    @Operation(summary = "Change customer email")
    public CustomerView changeEmail(@PathVariable UUID id, @Valid @RequestBody ChangeEmailRequest request) {
        return mediator.send(new ChangeCustomerEmailCommand(id, request.email()));
    }

    @PatchMapping("/{id}/deactivate")
    @Operation(summary = "Deactivate a customer")
    public CustomerView deactivate(@PathVariable UUID id) {
        return mediator.send(new DeactivateCustomerCommand(id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Soft-delete a customer")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        mediator.send(new DeleteCustomerCommand(id));
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
