package ru.spbau.bocharov.lazy;

import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

import static org.junit.Assert.assertEquals;

public abstract class LazyTestBase {

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

    @Test
    public void testShouldReturnSameObject() throws Exception {
        Lazy l = createLazy(Object::new);
        assertEquals(
                l.get(),
                l.get());
    }

    @Test
    public void testShouldCallGetInLazyManner() throws Exception {
        AtomicInteger callCount = new AtomicInteger(0);
        Supplier<Integer> sup = callCount::incrementAndGet;

        Lazy<Integer> l = createLazy(sup);
        assertEquals(
                callCount.get(),
                0);
        assertEquals(
                l.get().intValue(),
                1);
    }

    public abstract <T> Lazy<T> createLazy(Supplier<T> supplier);
}
