package com.mustafizur.cleanarchitecture.application.messaging;

public interface Mediator {
    <R> R send(Command<R> command);
    <R> R send(Query<R> query);
}
