package ru.spbau.bocharov.lazy;

import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CyclicBarrier;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class MultiThreadLazyTest extends LazyTestBase {

    private static final int TEST_THREADS_COUNT = 10;

    @Override
    public <T> Lazy<T> createLazy(Supplier<T> supplier) {
        return LazyFactory.createMultiThreadLazy(supplier);
    }


    @Test
    public void testShouldReturnSameObjectFromMultipleThreads()
            throws BrokenBarrierException, InterruptedException {
        List<Object> list = new CopyOnWriteArrayList<>();
        CyclicBarrier barrier = new CyclicBarrier(TEST_THREADS_COUNT);
        CyclicBarrier endBarrier = new CyclicBarrier(TEST_THREADS_COUNT + 1);

        Lazy l = createLazy(Object::new);
        Stream.generate(() -> new Thread(() -> {
            try {
                barrier.await();
                list.add(l.get());
                endBarrier.await();
            } catch (InterruptedException | BrokenBarrierException ignored) {
            }
        })).limit(TEST_THREADS_COUNT).peek(Thread::start).count();
        endBarrier.await();

        list.forEach(o -> assertSame(list.get(0), o));
    }

    @Test
    public void testShouldRunSupplierLessThan2TimesInEachThread()
            throws BrokenBarrierException, InterruptedException {
        Map<Thread, Integer> callCountMap = new ConcurrentHashMap<>();
        CyclicBarrier barrier = new CyclicBarrier(TEST_THREADS_COUNT);
        CyclicBarrier endBarrier = new CyclicBarrier(TEST_THREADS_COUNT + 1);

        Lazy l = createLazy(() -> {
            Thread currentThread = Thread.currentThread();
            callCountMap.put(currentThread, callCountMap.get(currentThread) + 1);
            return new Object();
        });
        Stream.generate(() -> new Thread(() -> {
            try {
                callCountMap.put(Thread.currentThread(), 0);
                barrier.await();
                l.get();
                endBarrier.await();
            } catch (InterruptedException | BrokenBarrierException ignored) {
            }
        })).limit(TEST_THREADS_COUNT).peek(Thread::start).count();
        endBarrier.await();

        callCountMap.values().forEach(callCount -> assertTrue(callCount < 2));
    }
}