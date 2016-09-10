package ru.spbau.bocharov.concurrency;

import org.junit.Test;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;


public class ThreadPoolImplTest {

    @Test
    public void testAtLeastThreadCountThreadsInThreadPool() throws InterruptedException {
        final boolean[] allArrived = {false};
        ThreadPool threadPool = new ThreadPoolImpl(THREAD_COUNT);
        CyclicBarrier barrier = new CyclicBarrier(THREAD_COUNT, () -> allArrived[0] = true);
        for (int i = 0; i < THREAD_COUNT; ++i) {
            threadPool.add(() -> {
                try {
                    return barrier.await();
                } catch (InterruptedException | BrokenBarrierException e) {
                    Thread.currentThread().interrupt();
                }
                return 0;
            });
        }
        Thread.sleep(100);
        assertTrue(allArrived[0]);
        threadPool.shutdown();
    }

    @Test
    public void testAtMostThreadCountThreadsInThreadPool() throws InterruptedException {
        final boolean[] allArrived = {false};
        ThreadPool threadPool = new ThreadPoolImpl(THREAD_COUNT);
        CyclicBarrier barrier = new CyclicBarrier(THREAD_COUNT + 1, () -> allArrived[0] = true);
        for (int i = 0; i < THREAD_COUNT + 1; ++i) {
            threadPool.add(() -> {
                try {
                    return barrier.await();
                } catch (InterruptedException | BrokenBarrierException e) {
                    Thread.currentThread().interrupt();
                }
                return 0;
            });
        }
        Thread.sleep(100);
        assertFalse(allArrived[0]);
        threadPool.shutdown();
    }

    @Test(expected = LightExecutionException.class)
    public void testExceptionForNotReadyTasksOnShutdown() throws InterruptedException {
        ThreadPool threadPool = new ThreadPoolImpl(THREAD_COUNT);
        for (int i = 0; i < THREAD_COUNT; ++i) {
            threadPool.add(() -> {
                try {
                    TimeUnit.DAYS.sleep(1);
                } catch (InterruptedException ignored) {
                    Thread.currentThread().interrupt();
                }
                return 0;
            });
        }
        LightFuture<Integer> future = threadPool.add(() -> 3);
        threadPool.shutdown();
        assertEquals(3, (int) future.get());
    }


    private static final int THREAD_COUNT = 10;
}