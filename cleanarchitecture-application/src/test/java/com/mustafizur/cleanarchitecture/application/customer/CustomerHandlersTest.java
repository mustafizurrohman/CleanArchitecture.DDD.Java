package com.mustafizur.cleanarchitecture.application.customer;

import com.mustafizur.cleanarchitecture.application.customer.command.ChangeCustomerEmailCommand;
import com.mustafizur.cleanarchitecture.application.customer.command.CreateCustomerCommand;
import com.mustafizur.cleanarchitecture.application.customer.handler.ChangeCustomerEmailHandler;
import com.mustafizur.cleanarchitecture.application.customer.handler.CreateCustomerHandler;
import com.mustafizur.cleanarchitecture.application.customer.handler.DeleteCustomerHandler;
import com.mustafizur.cleanarchitecture.application.port.out.CustomerRepository;
import com.mustafizur.cleanarchitecture.application.port.out.DomainEventPublisher;
import com.mustafizur.cleanarchitecture.core.domain.DomainEvent;
import com.mustafizur.cleanarchitecture.core.exception.ConflictException;
import com.mustafizur.cleanarchitecture.core.model.PageResult;
import com.mustafizur.cleanarchitecture.domain.customer.Customer;
import com.mustafizur.cleanarchitecture.domain.customer.CustomerId;
import com.mustafizur.cleanarchitecture.domain.customer.Email;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CustomerHandlersTest {

    @Test
    void createPersistsCustomerAndPublishesDomainEvent() {
        var repository = new InMemoryCustomerRepository();
        var publisher = new CapturingEventPublisher();
        var handler = new CreateCustomerHandler(repository, publisher);

        var view = handler.handle(new CreateCustomerCommand("  Ada   Lovelace ", "ADA@example.com"));

        assertThat(view.name()).isEqualTo("Ada Lovelace");
        assertThat(view.email()).isEqualTo("ada@example.com");
        assertThat(repository.findById(new CustomerId(view.id()))).isPresent();
        assertThat(publisher.events).hasSize(1);
    }

    @Test
    void duplicateEmailIsRejected() {
        var repository = new InMemoryCustomerRepository();
        var publisher = new CapturingEventPublisher();
        var handler = new CreateCustomerHandler(repository, publisher);

        handler.handle(new CreateCustomerCommand("Ada", "ada@example.com"));

        assertThatThrownBy(() -> handler.handle(new CreateCustomerCommand("Grace", "ADA@example.com")))
                .isInstanceOf(ConflictException.class);
    }

    @Test
    void changeEmailUpdatesAggregateAndPublishesEvent() {
        var repository = new InMemoryCustomerRepository();
        var publisher = new CapturingEventPublisher();
        var create = new CreateCustomerHandler(repository, publisher);
        var created = create.handle(new CreateCustomerCommand("Ada", "ada@example.com"));
        publisher.events.clear();

        var change = new ChangeCustomerEmailHandler(repository, publisher);
        var changed = change.handle(new ChangeCustomerEmailCommand(created.id(), "new@example.com"));

        assertThat(changed.email()).isEqualTo("new@example.com");
        assertThat(publisher.events).hasSize(1);
    }

    @Test
    void deleteSoftDeletesFromRepositoryView() {
        var repository = new InMemoryCustomerRepository();
        var publisher = new CapturingEventPublisher();
        var create = new CreateCustomerHandler(repository, publisher);
        var created = create.handle(new CreateCustomerCommand("Ada", "ada@example.com"));

        new DeleteCustomerHandler(repository).handle(new com.mustafizur.cleanarchitecture.application.customer.command.DeleteCustomerCommand(created.id()));

        assertThat(repository.findById(new CustomerId(created.id()))).isEmpty();
    }

    private static final class CapturingEventPublisher implements DomainEventPublisher {
        private final ArrayList<DomainEvent> events = new ArrayList<>();

        @Override
        public void publish(Collection<? extends DomainEvent> events) {
            this.events.addAll(events);
        }
    }

    private static final class InMemoryCustomerRepository implements CustomerRepository {
        private final Map<CustomerId, Customer> customers = new LinkedHashMap<>();

        @Override
        public Optional<Customer> findById(CustomerId id) {
            return Optional.ofNullable(customers.get(id));
        }

        @Override
        public Optional<Customer> findByEmail(Email email) {
            return customers.values().stream().filter(customer -> customer.email().equals(email)).findFirst();
        }

        @Override
        public PageResult<Customer> findAll(int page, int size) {
            var all = new ArrayList<>(customers.values());
            var from = Math.min(page * size, all.size());
            var to = Math.min(from + size, all.size());
            var totalPages = all.isEmpty() ? 0 : (int) Math.ceil((double) all.size() / size);
            return new PageResult<>(all.subList(from, to), page, size, all.size(), totalPages);
        }

        @Override
        public Customer save(Customer customer) {
            customers.put(customer.id(), customer);
            return customer;
        }

        @Override
        public void softDelete(CustomerId id) {
            customers.remove(id);
        }
    }
}
