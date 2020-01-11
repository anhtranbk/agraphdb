package com.agraph.common.concurrent;

import com.google.common.util.concurrent.ListenableFuture;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

public class FutureAdapter<S, R> implements ListenableFuture<R> {

    private final Future<S> src;
    private final Function<S, R> func;
    private final AtomicReference<R> result = new AtomicReference<>();

    public FutureAdapter(Future<S> src, Function<S, R> func) {
        this.src = src;
        this.func = func;
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return src.cancel(mayInterruptIfRunning);
    }

    @Override
    public boolean isCancelled() {
        return src.isCancelled();
    }

    @Override
    public boolean isDone() {
        return src.isDone();
    }

    @Override
    public R get() throws InterruptedException, ExecutionException {
        if (result.get() == null) {
            result.compareAndSet(null, func.apply(src.get()));
        }
        return result.get();
    }

    @Override
    public R get(long timeout, @NotNull TimeUnit unit)
            throws InterruptedException, ExecutionException, TimeoutException {
        return func.apply(src.get(timeout, unit));
    }

    @Override
    public void addListener(@NotNull Runnable runnable, @NotNull Executor executor) {
        if (src instanceof ListenableFuture) {
            ((ListenableFuture<S>) src).addListener(runnable, executor);
        } else {
            executor.execute(() -> {
                try {
                    get();
                    runnable.run();
                } catch (ExecutionException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }

    public static <S, R> FutureAdapter<S, R> from(Future<S> src, Function<S, R> func) {
        return new FutureAdapter<>(src, func);
    }
}
