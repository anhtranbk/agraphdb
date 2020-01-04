package com.agraph.common.concurrency;

import com.google.common.util.concurrent.ListenableFuture;
import com.agraph.common.utils.Converter;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;

/**
 * TODO: Class description here.
 *
 * @author <a href="https://github.com/tjeubaoit">tjeubaoit</a>
 */
public class FutureAdapter<S, R> implements ListenableFuture<R> {

    private final Future<S> src;
    private final Converter<S, R> converter;
    private final AtomicReference<R> result = new AtomicReference<>();

    public FutureAdapter(Future<S> src, Converter<S, R> converter) {
        this.src = src;
        this.converter = converter;
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
            result.compareAndSet(null, converter.convert(src.get()));
        }
        return result.get();
    }

    @Override
    public R get(long timeout, @NotNull TimeUnit unit)
            throws InterruptedException, ExecutionException, TimeoutException {
        return converter.convert(src.get(timeout, unit));
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
                } catch (Exception ignored) {
                }
            });
        }
    }

    public static <S, R> FutureAdapter<S, R> from(Future<S> src, Converter<S, R> converter) {
        return new FutureAdapter<>(src, converter);
    }
}
