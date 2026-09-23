package com.mustafizur.cleanarchitecture.core.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract class AggregateRoot<ID> {
    private final ID id;
    private final List<DomainEvent> domainEvents = new ArrayList<>();

    protected AggregateRoot(ID id) {
        this.id = Objects.requireNonNull(id, "id must not be null");
    }

    public final ID id() {
        return id;
    }

    protected final void raise(DomainEvent event) {
        domainEvents.add(Objects.requireNonNull(event));
    }

    public final List<DomainEvent> pullDomainEvents() {
        var events = List.copyOf(domainEvents);
        domainEvents.clear();
        return events;
    }
}
