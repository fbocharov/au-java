package ru.spbau.bocharov.lazy;

import java.util.function.Supplier;

public class LockFreeLazyTest extends MultiThreadLazyTest {

    @Override
    public <T> Lazy<T> createLazy(Supplier<T> supplier) {
        return LazyFactory.createLockFreeLazy(supplier);
    }
}