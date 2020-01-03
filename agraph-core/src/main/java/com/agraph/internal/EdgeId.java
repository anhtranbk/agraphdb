package com.agraph.internal;

import org.apache.tinkerpop.gremlin.structure.Direction;

public interface EdgeId extends ElementId {

    VertexId inVertexId();

    VertexId outVertexId();

    String label();

    Direction direction();
}
