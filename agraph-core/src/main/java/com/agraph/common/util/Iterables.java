package com.agraph.common.util;

import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.function.Function;

/**
 * Convert an iterable/iterator object to new an iterable object with different element type
 *
 * @author <a href="https://github.com/tjeubaoit">tjeubaoit</a>
 */
public abstract class Iterables<S, R> implements Iterable<R> {

    private final Iterator<S> it;

    public Iterables(Iterable<S> source) {
        this.it = source.iterator();
    }

    public Iterables(Iterator<S> it) {
        this.it = it;
    }

    @NotNull
    @Override
    public Iterator<R> iterator() {
        return new Iterator<R>() {

            @Override
            public boolean hasNext() {
                return it.hasNext();
            }

            @Override
            public R next() {
                return hasNext() ? convert(it.next()) : null;
            }
        };
    }

    protected abstract R convert(S source);

    public static <S, R> Iterable<R> transform(Iterable<S> iterable, Function<S, R> func) {
        return new Iterables<S, R>(iterable) {
            @Override
            protected R convert(S var) {
                return func.apply(var);
            }
        };
    }

    public static <S, R> Iterable<R> transform(Iterator<S> iterator, Function<S, R> func) {
        return new Iterables<S, R>(iterator) {
            @Override
            protected R convert(S var) {
                return func.apply(var);
            }
        };
    }
}
