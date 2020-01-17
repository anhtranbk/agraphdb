package com.agraph.common.concurrent;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import org.apache.tinkerpop.gremlin.structure.T;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author <a href="https://github.com/tjeubaoit">tjeubaoit</a>
 */
public class AllInOneFuture<T> extends AbstractListenableFuture<List<T>> {

    private final List<Future<T>> futures = new LinkedList<>();

    @SuppressWarnings("unchecked")
    AllInOneFuture(Iterable<? extends Future<? extends T>> futures) {
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
}
