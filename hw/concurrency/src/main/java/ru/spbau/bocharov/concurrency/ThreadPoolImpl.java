package ru.spbau.bocharov.concurrency;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class ThreadPoolImpl implements ThreadPool {
    public ThreadPoolImpl(int size) {
        for (int i = 0; i < size; i++) {
            Thread worker = new Thread(new Worker());
            worker.start();
            workers.add(worker);
        }
    }

    public <T> LightFuture<T> add(Supplier<T> supplier) {
        Task<T> task = new Task<>(supplier);
        pushTask(task);
        return task.future;
    }

    public void shutdown() throws InterruptedException {
        for (Thread worker : workers) {
            worker.interrupt();
            worker.join();
        }
        for (Task<?> task : taskQueue) {
            task.future.setException(new InterruptedException());
        }
        workers.clear();
        taskQueue.clear();
    }


    private class Worker implements Runnable {
        @Override
        public void run() {
            try {
                while (!Thread.interrupted()) {
                    Runnable task;
                    synchronized (taskQueue) {
                        while (taskQueue.isEmpty()) {
                            taskQueue.wait();
                        }

                        task = taskQueue.peek();
                        taskQueue.remove();
                        taskQueue.notifyAll();
                    }
                    task.run();
                }
            } catch (InterruptedException ignored) {}
        }
    }


    private class Task<T> implements Runnable {

        public Task(Supplier<T> s) {
            supplier = s;
        }

        @Override
        public void run() {
            Consumer<Task> consumer;
            try {
                future.setValue(supplier.get());
                consumer = ThreadPoolImpl.this::pushTask;
            } catch (Exception e) {
                future.setException(e);
                consumer = task -> task.future.setException(future.exception);
            }
            synchronized (dependentTasks) {
                dependentTasks.forEach(consumer);
                dependentTasks.clear();
            }
        }

        private <R> LightFuture<R> thenApply(Function<T, R> f) {
            Task<R> dependent = new Task<>(() -> f.apply(future.value));
            synchronized (dependentTasks) {
                // We can check for exception and value in future without locking on it
                // because even if there is some kind of race we just put task in queue
                // under lock on queue -- this guarantees us that correct action (exception
                // or value setting) will be performed on it.
                if (future.exception != null) {
                    dependent.future.setException(future.exception);
                } else if (future.value != null) {
                    ThreadPoolImpl.this.pushTask(dependent);
                } else {
                    dependentTasks.add(dependent);
                }
            }
            return dependent.future;
        }


        private final Supplier<T> supplier;
        private final LightFutureImpl<T> future = new LightFutureImpl<>(this);
        private final Queue<Task<?>> dependentTasks = new LinkedList<>();
    }


    private static class LightFutureImpl<T> implements LightFuture<T> {
        public LightFutureImpl(Task<T> task) {
            parentTask = task;
        }

        @Override
        public T get() throws LightExecutionException, InterruptedException {
            if (exception == null && value == null) {
                synchronized (readyEvent) {
                    while (exception == null && value == null) {
                        readyEvent.wait();
                    }
                }
            }

            if (exception != null) {
                throw exception;
            }

            return value;
        }

        @Override
        public boolean isReady() {
            return value != null || exception != null;
        }

        @Override
        public <R> LightFuture<R> thenApply(Function<T, R> f) {
            return parentTask.thenApply(f);
        }

        public void setValue(T val) {
            synchronized (readyEvent) {
                value = val;
                readyEvent.notifyAll();
            }
        }

        public void setException(Exception e) {
            LightExecutionException ex = new LightExecutionException(e);
            synchronized (readyEvent) {
                exception = ex;
                readyEvent.notifyAll();
            }
        }


        private volatile T value = null;
        private volatile LightExecutionException exception = null;
        private final Object readyEvent = new Object();
        private final Task<T> parentTask;
    }


    private <T> void pushTask(Task<T> task) {
        synchronized (taskQueue) {
            taskQueue.add(task);
            taskQueue.notifyAll();
        }
    }


    private final Queue<Task<?>> taskQueue = new LinkedList<>();
    private final List<Thread> workers = new LinkedList<>();
}
