package com.agraph.common.utils;

import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

/**
 * Convert an iterable object to new an iterable object with different item type
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

    public static <S, R> IterableAdapter<S, R> from(Iterable<S> source, Converter<S, R> converter) {
        return new IterableAdapter<S, R>(source) {
            @Override
            protected R convert(S source) {
                return converter.convert(source);
            }
        };
    }
}
