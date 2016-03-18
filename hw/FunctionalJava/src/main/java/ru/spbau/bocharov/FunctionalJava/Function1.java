package ru.spbau.bocharov.FunctionalJava;

public interface Function1<A, R> extends AbstractFunction1<A, R> {
    default <R1> Function1<A, R1> compose(Function1<? super R, R1> g) {
        return arg -> g.apply(Function1.this.apply(arg));
    }
}