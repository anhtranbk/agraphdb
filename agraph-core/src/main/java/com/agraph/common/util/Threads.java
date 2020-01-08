package com.agraph.common.util;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * TODO: Class description here.
 *
 * @author <a href="https://github.com/tjeubaoit">tjeubaoit</a>
 */
public class Threads {

    private static final AtomicInteger poolNumber = new AtomicInteger(1);

    @Deprecated
    public static ExecutorService newThreadPool(int coreSize, int maxSize, int queueSize) {
        return newThreadPool(coreSize, maxSize, queueSize,
                "pool-" + poolNumber.getAndIncrement() + "-thread-", true);
    }

    @Deprecated
    public static ExecutorService newThreadPool(int coreSize, int maxSize, int queueSize, String prefix) {
        return newThreadPool(coreSize, maxSize, queueSize, prefix, true);
    }

    @Deprecated
    public static ExecutorService newThreadPool(int coreSize, int maxSize, int queueSize,
                                                String prefix, boolean daemon) {
        return newThreadPool(coreSize, maxSize, queueSize,
                new ThreadPool.DefaultThreadFactory(prefix, daemon));
    }

    @Deprecated
    public static ExecutorService newThreadPool(int coreSize, int maxSize, int queueSize,
                                                ThreadFactory threadFactory) {
        return newThreadPool(coreSize, maxSize, queueSize, 60L,
                TimeUnit.SECONDS, threadFactory);
    }

    @Deprecated
    public static ExecutorService newThreadPool(int coreSize, int maxSize, int queueSize,
                                                long keepAliveTimeout, TimeUnit timeUnit,
                                                ThreadFactory threadFactory) {
        BlockingQueue<Runnable> workQueue = queueSize <= 0
                ? new LinkedBlockingQueue<>()
                : new ArrayBlockingQueue<>(queueSize);
        return new ThreadPoolExecutor(coreSize,
                maxSize,
                keepAliveTimeout,
                timeUnit,
                workQueue,
                threadFactory);
    }

    public static void stopThreadPool(ExecutorService executor) {
        executor.shutdown();
    }

    public static void stopThreadPool(ExecutorService executor, long timeout, TimeUnit unit) {
        try {
            executor.shutdown();
            executor.awaitTermination(timeout, unit);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static void sleep(long duration, TimeUnit unit) {
        sleep(unit.toMillis(duration));
    }
}
