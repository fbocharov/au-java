package ru.spbau.bocharov.FunctionalJava;

public interface Predicate<A> extends AbstractFunction1<A, Boolean> {

    default Predicate<A> or(Predicate<? super A> p) {
        return arg -> Predicate.this.apply(arg) || p.apply(arg);
    }

    default Predicate<A> and(Predicate<? super A> p) {
        return arg -> Predicate.this.apply(arg) && p.apply(arg);
    }

    default Predicate<A> not() {
        return arg -> !Predicate.this.apply(arg);
    }

    Predicate<Object> ALWAYS_TRUE  = arg -> true;
    Predicate<Object> ALWAYS_FALSE = arg -> false;
}
