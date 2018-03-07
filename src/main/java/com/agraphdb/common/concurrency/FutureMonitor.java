package com.agraphdb.common.concurrency;

import com.agraphdb.common.config.Properties;
import com.agraphdb.common.lifecycle.AbstractLifeCycle;
import com.agraphdb.common.utils.Threads;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * TODO: Class description here.
 *
 * @author <a href="https://github.com/tjeubaoit">tjeubaoit</a>
 */
public class FutureMonitor extends AbstractLifeCycle {

    private final Map<Future<?>, Runnable> futureMap = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduledExecutor;
    private final long initialDelay, delay;

    public FutureMonitor(Properties p) {
        scheduledExecutor = Executors.newSingleThreadScheduledExecutor();
        initialDelay = p.getLongProperty("future_monitor.initial.delay.ms", 100);
        delay = p.getLongProperty("future_monitor.delay.ms",  500);
    }

    @Override
    protected void onStart() {
        scheduledExecutor.scheduleWithFixedDelay(this::onHandle,
                initialDelay, delay, TimeUnit.MILLISECONDS);
    }

    @Override
    protected void onStop() {
        Threads.stopThreadPool(scheduledExecutor, 5, TimeUnit.MINUTES);
    }

    /**
     * Add future to monitoring
     * @param fut future to be monitored
     * @param oneDone task will be execute once the future is done
     */
    public void addFuture(Future<?> fut, Runnable oneDone) {
        futureMap.put(fut, oneDone);
    }

    protected void onHandle() {
        List<Future<?>> futures = new LinkedList<>();
        System.out.println(futureMap.size());
        futureMap.forEach((fut, task) -> {
            if (!fut.isDone()) return;
            try {
                fut.get();
                task.run();
            } catch (Exception ignored) {
            } finally {
                futures.add(fut);
            }
        });
        futures.forEach(futureMap::remove);
    }
}
