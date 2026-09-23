package com.mustafizur.cleanarchitecture.application.port.out;

import com.mustafizur.cleanarchitecture.core.domain.DomainEvent;

import java.util.Collection;

public interface DomainEventPublisher {
    void publish(Collection<? extends DomainEvent> events);
}
