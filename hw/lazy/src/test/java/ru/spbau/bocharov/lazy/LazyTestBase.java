package ru.spbau.bocharov.lazy;

import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

import static org.junit.Assert.assertEquals;

public abstract class LazyTestBase {

    @Test
    public void testShouldReturnSameObject() throws Exception {
        Lazy l = createLazy(Object::new);
        assertEquals(
                l.get(),
                l.get());
    }

    @Test
    public void testShouldRunSupplierInLazyManner() throws Exception {
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

    @Test
    public void testShouldNotRunTwiceSupplierWhichReturnsNull() throws Exception {
        AtomicInteger callCount = new AtomicInteger(0);
        Lazy l = createLazy(() -> {
            callCount.incrementAndGet();
            return null;
        });

        l.get();
        assertEquals(
                callCount.intValue(),
                1);

        l.get();
        assertEquals(
                callCount.intValue(),
                1);
    }

    @Test
    public void testShouldNotRunTwiceSupplierWhichReturnsSupplier() {
        AtomicInteger callCount = new AtomicInteger(0);
        Lazy l = createLazy(() -> {
            callCount.incrementAndGet();
            return this;
        });

        l.get();
        assertEquals(
                callCount.intValue(),
                1);

        l.get();
        assertEquals(
                callCount.intValue(),
                1);
    }

    public abstract <T> Lazy<T> createLazy(Supplier<T> supplier);
}
