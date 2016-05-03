package ru.spbau.bocharov.concurrency;

import java.util.function.Function;

public interface LightFuture<T> {

    T get() throws LightExecutionException, InterruptedException;

    boolean isReady();

    <R> LightFuture<R> thenApply(Function<T, R> f);
}
