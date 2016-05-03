package ru.spbau.bocharov.concurrency;

import org.junit.Test;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;


public class LightFutureImplTest {

    @Test
    public void testFutureGetSuccess() throws InterruptedException {
        ThreadPool pool = new ThreadPoolImpl(1);
        LightFuture<Integer> get5 = pool.add(() -> 5);
        assertEquals(5, (int) get5.get());
        pool.shutdown();
    }

    @Test(expected = LightExecutionException.class)
    public void testFutureGetException() throws InterruptedException {
        ThreadPool pool = new ThreadPoolImpl(1);
        LightFuture f1 = pool.add(() -> { throw new RuntimeException(); });
        f1.get();
        pool.shutdown();
    }

    @Test
    public void testFutureSeveralDependentTasks() throws InterruptedException {
        ThreadPool pool = new ThreadPoolImpl(1);
        LightFuture sleeper = pool.add(() -> {
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            return 0;
        });

        LightFuture<Integer> getN = pool.add(() -> NUMBER);

        List<LightFuture<Double>> futures = new LinkedList<>();

        for (int i = 0; i < ROOT_COUNT; ++i) {
            final int tmp = i;
            LightFuture<Double> f = getN.thenApply(num -> Math.pow(NUMBER, 1.0 / tmp));
            futures.add(f);
        }

        for (int i = 0; i < ROOT_COUNT; ++i) {
            assertEquals(Math.pow(NUMBER, 1.0 / i), futures.get(i).get(), 0.001);
        }

        pool.shutdown();
    }

    @Test
    public void testFutureThenApplySuccess() throws InterruptedException {
        ThreadPool pool = new ThreadPoolImpl(1);
        LightFuture sleeper = pool.add(() -> {
            try {
                TimeUnit.MICROSECONDS.sleep(10);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            return 0;
        });

        LightFuture<Integer> get100 = pool.add(() -> 100);
        LightFuture<Double> get10 = get100.thenApply(Math::sqrt);
        TimeUnit.SECONDS.sleep(1);
        assertEquals(100, (int) get100.get());
        assertEquals(10, get10.get(), 0.01);
        pool.shutdown();
    }

    @Test(expected = LightExecutionException.class)
    public void testFutureThenApplyException() throws InterruptedException {
        ThreadPool pool = new ThreadPoolImpl(1);
        LightFuture f1 = pool.add(() -> {
            throw new RuntimeException();
        });
        LightFuture f2 = f1.thenApply(obj -> obj.equals(""));
        f2.get();
        pool.shutdown();
    }

    private final int NUMBER     = 2048;
    private final int ROOT_COUNT = 4;
}
