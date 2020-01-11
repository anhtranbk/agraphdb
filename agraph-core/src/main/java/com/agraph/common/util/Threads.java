package com.agraph.common.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author <a href="https://github.com/tjeubaoit">tjeubaoit</a>
 */
public class Threads {

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
