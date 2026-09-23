package com.mustafizur.cleanarchitecture.application.messaging;

public interface CommandHandler<C extends Command<R>, R> {
    Class<C> commandType();
    R handle(C command);
}
