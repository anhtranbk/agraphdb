package com.agraph.common.util;

import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

public abstract class Iterables2 {

    /**
     * Convert an iterable/iterator object to new an iterable object with different element type
     */
    public static <S, R> Iterable<R> transform(Iterable<S> iterable, Function<S, R> func) {
        return new IterableAdapter<S, R>(iterable) {
            @Override
            protected R convert(S var) {
                return func.apply(var);
            }
        };
    }

    /**
     * Convert an iterable/iterator object to new an iterable object with different element type
     */
    public static <S, R> Iterable<R> transform(Iterator<S> iterator, Function<S, R> func) {
        return new IterableAdapter<S, R>(iterator) {
            @Override
            protected R convert(S var) {
                return func.apply(var);
            }
        };
    }

    public static <T> T lastItem(Iterable<T> seq) {
        if (seq instanceof List) {
            List<T> list = (List<T>) seq;
            return list.get(list.size() - 1);
        }
        Iterator<T> iterator = seq.iterator();
        T last = iterator.next();
        while (iterator.hasNext()) last = iterator.next();
        return last;
    }
}
