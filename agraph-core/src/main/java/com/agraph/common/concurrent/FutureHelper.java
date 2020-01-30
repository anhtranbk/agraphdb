package com.agraph.common.concurrent;

import java.util.Arrays;
import java.util.concurrent.Future;
import java.util.function.Function;

public class FutureHelper {

    public static <S, R> FutureAdapter<S, R> transform(Future<S> src, Function<S, R> func) {
        return new FutureAdapter<>(src, func);
    }

    public static <T> AllInOneFuture<T> allAsList(Iterable<? extends Future<? extends T>> futures) {
        return new AllInOneFuture<>(futures);
    }

    @SafeVarargs
    public static <T> AllInOneFuture<T> allAsList(Future<? extends T>... futures) {
        return allAsList(Arrays.asList(futures));
    }
}
