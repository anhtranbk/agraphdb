package com.vcc.bigdata.graphdb;

/**
 * TODO: Class description here.
 *
 * @author <a href="https://github.com/tjeubaoit">tjeubaoit</a>
 */
public interface ElementSet<T extends Element> extends Iterable<T> {

    default T first() {
        return iterator().next();
    }

    /**
     * Convert an Iterable to ElementSet object
     * @param iterable iterable to be converted
     * @param <E> generic type
     * @return a new ElementSet object
     */
    static <E extends Element> ElementSet<E> convert(Iterable<E> iterable) {
        return iterable::iterator;
    }
}
