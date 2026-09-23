package com.mustafizur.cleanarchitecture.application.messaging;

public interface QueryHandler<Q extends Query<R>, R> {
    Class<Q> queryType();
    R handle(Q query);
}
