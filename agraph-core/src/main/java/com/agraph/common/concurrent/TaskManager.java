package com.agraph.common.concurrent;

import com.agraph.common.util.Threads;
import com.agraph.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author <a href="https://github.com/tjeubaoit">tjeubaoit</a>
 */
public class TaskManager {

    private final int errorThreshold;
    private final ExecutorService executor;
    private final AtomicInteger errorCounter = new AtomicInteger();
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public TaskManager(Config config, ExecutorService executor) {
        this(config.getInt("task_manager.error.threshold", 100), executor);
    }

    public TaskManager(int errorThreshold) {
        this(errorThreshold, Executors.newCachedThreadPool());
    }

    public TaskManager(int errorThreshold, ExecutorService executor) {
        this.errorThreshold = errorThreshold;
        this.executor = executor;
    }

    /**
     * Try to submit task forever until task was scheduled for execution
     *
     * @param task task to submit
     * @return Future representing the task
     */
    public Future<?> trySubmitUntilSuccess(Runnable task, long interval) {
        Future<?> future;
        while ((future = trySubmit(task)) == null) {
            Threads.sleep(interval);
        }
        return future;
    }

    /**
     * @param task task to submit
     * @return Future representing the task or null if task cannot be
     * scheduled for execution
     */
    public Future<?> trySubmit(Runnable task) {
        try {
            return submit(task);
        } catch (RejectedExecutionException e) {
            return null;
        }
    }

    /**
     * @param task task to submit
     * @return Future representing the task
     * @throws RejectedExecutionException if task cannot be
     *                                    scheduled for execution
     */
    public Future<?> submit(Runnable task) {
        if (errorCounter.get() > errorThreshold) {
            // wait for failed task finish
            Threads.sleep(500);
            errorCounter.decrementAndGet();

            throw new TaskFailedLimitException(
                    "Number task error " + errorCounter.get() + ", threshold " + errorThreshold);
        }

        return executor.submit(() -> {
            try {
                task.run();
                if (errorCounter.get() > 0) errorCounter.decrementAndGet();
            } catch (Throwable t) {
                logger.error("Execution Exception", t);
                errorCounter.incrementAndGet();
            }
        });
    }

    /**
     * Reset to init state
     */
    public void reset() {
        errorCounter.set(0);
    }

    public ExecutorService executor() {
        return this.executor;
    }
}
