package com.agraph.common.util;

import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

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
}
