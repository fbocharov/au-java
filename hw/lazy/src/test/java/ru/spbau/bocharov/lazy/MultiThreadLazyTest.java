package ru.spbau.bocharov.lazy;

import org.junit.Test;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;

public class MultiThreadLazyTest extends LazyTestBase {

    private static final int TEST_THREADS_COUNT = 10;

    @Override
    public <T> Lazy<T> createLazy(Supplier<T> supplier) {
        return LazyFactory.createMultiThreadLazy(supplier);
    }

    @Test
    public void testShouldCallGetExactlyOnceEvenWithMultipleThreads()
            throws BrokenBarrierException, InterruptedException {
        AtomicInteger callCounter = new AtomicInteger(0);
        CyclicBarrier barrier = new CyclicBarrier(TEST_THREADS_COUNT);
        CyclicBarrier endBarrier = new CyclicBarrier(TEST_THREADS_COUNT + 1);

        Lazy<Integer> lazy = createLazy(callCounter::incrementAndGet);
        long count = Stream.generate(() -> new Thread(() -> {
            try {
                barrier.await();
                lazy.get();
                endBarrier.await();
            } catch (InterruptedException | BrokenBarrierException ignored) {
            }
        })).limit(TEST_THREADS_COUNT).peek(Thread::start).count();
        endBarrier.await();

        assertEquals(
                count,
                TEST_THREADS_COUNT);
        assertEquals(
                callCounter.get(),
                1);
    }
}