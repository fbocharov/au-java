package ru.spbau.bocharov.lazy;

import java.util.function.Supplier;

public class SingleThreadLazyTest extends LazyTestBase {
    @Override
    public <T> Lazy<T> createLazy(Supplier<T> supplier) {
        return LazyFactory.createSingleThreadLazy(supplier);
    }
}