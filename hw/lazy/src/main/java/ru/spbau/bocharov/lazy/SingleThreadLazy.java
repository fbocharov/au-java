package ru.spbau.bocharov.lazy;

import java.util.function.Supplier;

public class SingleThreadLazy<T> implements Lazy<T>{

    private T result = null;
    private Supplier<T> supplier = null;

    public SingleThreadLazy(Supplier<T> sup) {
        supplier = sup;
    }

    @Override
    public T get() {
        if (supplier != null) {
            result = supplier.get();
            supplier = null;
        }
        return result;
    }
}
