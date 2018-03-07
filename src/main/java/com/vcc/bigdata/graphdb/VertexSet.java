package com.vcc.bigdata.graphdb;

/**
 * TODO: Class description here.
 *
 * @author <a href="https://github.com/tjeubaoit">tjeubaoit</a>
 */
public interface VertexSet extends ElementSet<Vertex> {

    /**
     * Convert an Iterable of vertices to VertexSet object
     * @param iterable iterable to be converted
     * @return a new VertexSet object
     */
    static VertexSet convert(Iterable<Vertex> iterable) {
        return iterable::iterator;
    }
}
