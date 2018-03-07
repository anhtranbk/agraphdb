package com.agraphdb.core;

/**
 * TODO: Class description here.
 *
 * @author <a href="https://github.com/tjeubaoit">tjeubaoit</a>
 */
public interface EdgeSet extends ElementSet<Edge> {

    /**
     * Convert an Iterable of edges to EdgeSet object
     * @param iterable iterable to be converted
     * @return a new EdgeSet object
     */
    static EdgeSet convert(Iterable<Edge> iterable) {
        return iterable::iterator;
    }
}
