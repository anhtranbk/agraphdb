package com.agraph.common.util;

import com.agraph.common.Func0;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;

/**
 * Only get underlying iterator if necessary
 * @param <E> Type of elements return by this iterator
 */
public class LazyIterator<E> implements Iterator<E> {

    private static final Logger logger = LoggerFactory.getLogger(LazyIterator.class);

    private final Func0<Iterator<E>> func;
    private Iterator<E> underlying;

    public LazyIterator(Func0<Iterator<E>> func) {
        this.func = func;
    }

    @Override
    public boolean hasNext() {
        if (underlying == null) {
            logger.debug("Calling init function to create underlying iterator");
            underlying = this.func.apply();
        }
        return underlying.hasNext();
    }

    @Override
    public E next() {
        return hasNext() ? this.underlying.next() : null;
    }

    public static <T> LazyIterator<T> of(Func0<Iterator<T>> func) {
        return new LazyIterator<>(func);
    }
}
