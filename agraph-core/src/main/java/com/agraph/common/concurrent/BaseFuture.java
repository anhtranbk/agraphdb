package com.agraph.common.concurrent;

import com.google.common.util.concurrent.ListenableFuture;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;

public abstract class BaseFuture<T> implements ListenableFuture<T> {

    @Override
    public void addListener(@NotNull Runnable runnable, @NotNull Executor executor) {
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
