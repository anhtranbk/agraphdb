package com.vcc.bigdata.graphdb;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

/**
 * TODO: Class description here.
 *
 * @author <a href="https://github.com/tjeubaoit">tjeubaoit</a>
 */
public class Edge extends AbstractElement {

    private final Vertex inVertex;
    private final Vertex outVertex;

    private Edge(String label, Vertex inVertex, Vertex outVertex, Map<String, ?> properties) {
        super(null, label, properties);
        this.inVertex = inVertex;
        this.outVertex = outVertex;
    }

    public Iterator<Vertex> vertices(Direction direction) {
        return direction == Direction.BOTH
                ? Arrays.asList(getVertex(Direction.OUT), getVertex(Direction.IN)).iterator()
                : Collections.singleton(getVertex(direction)).iterator();
    }

    public Vertex outVertex() {
        return this.vertices(Direction.OUT).next();
    }

    public Vertex inVertex() {
        return this.vertices(Direction.IN).next();
    }

    public Iterator<Vertex> bothVertices() {
        return this.vertices(Direction.BOTH);
    }

    private Vertex getVertex(Direction direction) throws IllegalArgumentException {
        if (!Direction.IN.equals(direction) && !Direction.OUT.equals(direction)) {
            throw new IllegalArgumentException("Invalid direction: " + direction);
        }
        return Direction.IN.equals(direction) ? inVertex : outVertex;
    }

    @Override
    public String toString() {
        return outVertex + "--" + label() + "-->" + inVertex;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Edge) {
            Edge e = ((Edge) obj);
            return e.inVertex.equals(this.inVertex)
                    && e.outVertex.equals(this.outVertex)
                    && e.label().equals(this.label());
        }
        return false;
    }

    public static Edge create(String label, Vertex outVertex, Vertex inVertex) {
        return new Edge(label, inVertex, outVertex, Collections.emptyMap());
    }

    public static Edge create(String label, Vertex outVertex, Vertex inVertex, Map<String, ?> properties) {
        return new Edge(label, inVertex, outVertex, properties);
    }
}
