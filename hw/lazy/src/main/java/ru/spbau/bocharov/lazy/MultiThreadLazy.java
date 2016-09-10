package ru.spbau.bocharov.lazy;

import java.util.function.Supplier;

public class MultiThreadLazy<T> implements Lazy<T> {

    private volatile T value = null;
    private volatile Supplier<T> supplier = null;

    public MultiThreadLazy(Supplier<T> sup) {
        supplier = sup;
    }

    @Override
    public T get() {
        if (supplier != null) {
            synchronized (this) {
                if (supplier != null) {
                    value = supplier.get();
                    supplier = null;
                }
            }
        }
        return value;
    }
}
