package ru.spbau.bocharov.lazy;

import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

import static org.junit.Assert.assertEquals;

public class SingleThreadLazyTest extends LazyTestBase {
    @Override
    public <T> Lazy<T> createLazy(Supplier<T> supplier) {
        return LazyFactory.createSingleThreadLazy(supplier);
    }

    @Test
    public void testShouldNotFailOnNullSupplier() throws Exception {
        assertEquals(
                createLazy(null).get(),
                null);
    }

    @Test
    public void testShouldCallGetExactlyOnce() throws Exception {
        AtomicInteger callCount = new AtomicInteger(0);
        Supplier<Integer> sup = callCount::incrementAndGet;

        Lazy<Integer> l = createLazy(sup);
        assertEquals(
                l.get().intValue(),
                1);
        assertEquals(
                l.get().intValue(),
                1);
    }
}