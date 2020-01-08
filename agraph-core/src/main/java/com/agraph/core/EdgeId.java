package com.agraph.core;

import com.agraph.AGraphVertex;
import com.agraph.v1.Edge;

public interface EdgeId extends ElementId {

    VertexId outVertexId();

    String outVertexLabel();

    VertexId inVertexId();

    String inVertexLabel();

    String label();

    static EdgeId from(String label, AGraphVertex outVertex, AGraphVertex inVertex) {
        throw new UnsupportedOperationException();
    }
}
