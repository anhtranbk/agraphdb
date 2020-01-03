package com.agraph;

import org.apache.tinkerpop.gremlin.structure.Direction;
import org.apache.tinkerpop.gremlin.structure.Edge;

public interface AGraphEdge extends AGraphElement, Edge {

    default AGraphVertex outVertex() {
        return (AGraphVertex)this.vertices(Direction.OUT).next();
    }

    default AGraphVertex inVertex() {
        return (AGraphVertex)this.vertices(Direction.IN).next();
    }
}
