package com.vcc.bigdata.common.concurrency;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * TODO: Class description here.
 *
 * @author <a href="https://github.com/tjeubaoit">tjeubaoit</a>
 */
public class AllInOneFuture<T> implements Future<List<T>> {

    private final boolean allMustSuccess; // currently do not use this field
    private final List<Future<T>> futures = new LinkedList<>();

    @SuppressWarnings("unchecked")
    private AllInOneFuture(boolean allMustSuccess, Iterable<? extends Future<? extends T>> futures) {
        this.allMustSuccess = allMustSuccess;
        futures.forEach(fut -> AllInOneFuture.this.futures.add((Future<T>) fut));
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        for (Future<T> fut : futures) {
            if (!fut.cancel(mayInterruptIfRunning)) return false;
        }
        return true;
    }

    @Override
    public boolean isCancelled() {
        for (Future<T> fut : futures) {
            if (!fut.isCancelled()) return false;
        }
        return true;
    }

    @Override
    public boolean isDone() {
        for (Future<T> fut : futures) {
            if (!fut.isDone()) return false;
        }
        return true;
    }

    @Override
    public List<T> get() throws InterruptedException, ExecutionException {
        List<T> list = new ArrayList<>(futures.size());
        for (Future<T> fut : futures) {
            list.add(fut.get());
        }
        return list;
    }

    @Override
    public List<T> get(long timeout, @NotNull TimeUnit unit)
            throws InterruptedException, ExecutionException, TimeoutException {
        List<T> list = new ArrayList<>(futures.size());
        for (Future<T> fut : futures) {
            list.add(fut.get(timeout, unit));
        }
        return list;
    }

    public static <T> AllInOneFuture<T> from(boolean allMustSuccess,
                                             Iterable<? extends Future<? extends T>> futures) {
        return new AllInOneFuture<>(allMustSuccess, futures);
    }

    public static <T> AllInOneFuture<T> from(Iterable<? extends Future<? extends T>> futures) {
        return from(true, futures);
    }

    @SafeVarargs
    public static <T> AllInOneFuture<T> from(boolean allMustSuccess, Future<? extends T>... futures) {
        return from(allMustSuccess, Arrays.asList(futures));
    }

    @SafeVarargs
    public static <T> AllInOneFuture<T> from(Future<? extends T>... futures) {
        return from(true, futures);
    }
}
