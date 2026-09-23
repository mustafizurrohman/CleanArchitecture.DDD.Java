package com.mustafizur.cleanarchitecture.infrastructure.messaging;

import com.mustafizur.cleanarchitecture.application.messaging.Command;
import com.mustafizur.cleanarchitecture.application.messaging.CommandHandler;
import com.mustafizur.cleanarchitecture.application.messaging.Mediator;
import com.mustafizur.cleanarchitecture.application.messaging.Query;
import com.mustafizur.cleanarchitecture.application.messaging.QueryHandler;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class SpringMediator implements Mediator {
    private static final Logger log = LoggerFactory.getLogger(SpringMediator.class);
    private static final long SLOW_USE_CASE_MILLIS = 500;

    private final Map<Class<?>, CommandHandler<?, ?>> commandHandlers;
    private final Map<Class<?>, QueryHandler<?, ?>> queryHandlers;
    private final Validator validator;
    private final TransactionTemplate commandTransaction;
    private final TransactionTemplate queryTransaction;

    public SpringMediator(
            List<CommandHandler<?, ?>> commandHandlers,
            List<QueryHandler<?, ?>> queryHandlers,
            Validator validator,
            PlatformTransactionManager transactionManager) {
        this.commandHandlers = indexCommands(commandHandlers);
        this.queryHandlers = indexQueries(queryHandlers);
        this.validator = validator;
        this.commandTransaction = new TransactionTemplate(transactionManager);
        this.queryTransaction = new TransactionTemplate(transactionManager);
        this.queryTransaction.setReadOnly(true);
    }

    @Override
    public <R> R send(Command<R> command) {
        validate(command);
        return timed(command.getClass().getSimpleName(), () -> commandTransaction.execute(status -> invoke(command)));
    }

    @Override
    public <R> R send(Query<R> query) {
        validate(query);
        return timed(query.getClass().getSimpleName(), () -> queryTransaction.execute(status -> invoke(query)));
    }

    private void validate(Object request) {
        var violations = validator.validate(request);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }

    @SuppressWarnings("unchecked")
    private <R> R invoke(Command<R> command) {
        var handler = (CommandHandler<Command<R>, R>) commandHandlers.get(command.getClass());
        if (handler == null) {
            throw new IllegalStateException("No command handler registered for " + command.getClass().getName());
        }
        return handler.handle(command);
    }

    @SuppressWarnings("unchecked")
    private <R> R invoke(Query<R> query) {
        var handler = (QueryHandler<Query<R>, R>) queryHandlers.get(query.getClass());
        if (handler == null) {
            throw new IllegalStateException("No query handler registered for " + query.getClass().getName());
        }
        return handler.handle(query);
    }

    private <T> T timed(String operation, SupplierWithResult<T> supplier) {
        var started = System.nanoTime();
        try {
            return supplier.get();
        } finally {
            var durationMs = (System.nanoTime() - started) / 1_000_000;
            if (durationMs >= SLOW_USE_CASE_MILLIS) {
                log.warn("Slow application use case operation={} durationMs={}", operation, durationMs);
            } else {
                log.debug("Application use case completed operation={} durationMs={}", operation, durationMs);
            }
        }
    }

    private static Map<Class<?>, CommandHandler<?, ?>> indexCommands(List<CommandHandler<?, ?>> handlers) {
        var result = new HashMap<Class<?>, CommandHandler<?, ?>>();
        handlers.forEach(handler -> register(result, handler.commandType(), handler));
        return Map.copyOf(result);
    }

    private static Map<Class<?>, QueryHandler<?, ?>> indexQueries(List<QueryHandler<?, ?>> handlers) {
        var result = new HashMap<Class<?>, QueryHandler<?, ?>>();
        handlers.forEach(handler -> register(result, handler.queryType(), handler));
        return Map.copyOf(result);
    }

    private static <T> void register(Map<Class<?>, T> target, Class<?> type, T handler) {
        if (target.put(type, handler) != null) {
            throw new IllegalStateException("More than one handler registered for " + type.getName());
        }
    }

    @FunctionalInterface
    private interface SupplierWithResult<T> {
        T get();
    }
}
