package com.agraph.common.util;

import com.agraph.common.Function;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

/**
 * Convert an iterable/iterator object to new an iterable object with different element type
 *
 * @author <a href="https://github.com/tjeubaoit">tjeubaoit</a>
 */
public abstract class IterableAdapter<S, R> implements Iterable<R> {

    private final Iterator<S> it;

    public IterableAdapter(Iterable<S> source) {
        this.it = source.iterator();
    }

    public IterableAdapter(Iterator<S> it) {
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

    public static <S, R> Iterable<R> from(Iterable<S> iterable, Function<S, R> func) {
        return new IterableAdapter<S, R>(iterable) {
            @Override
            protected R convert(S var) {
                return func.apply(var);
            }
        };
    }

    public static <S, R> Iterable<R> from(Iterator<S> iterator, Function<S, R> func) {
        return new IterableAdapter<S, R>(iterator) {
            @Override
            protected R convert(S var) {
                return func.apply(var);
            }
        };
    }
}
