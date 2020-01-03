package com.agraph;

import com.agraph.internal.VertexId;
import org.apache.tinkerpop.gremlin.structure.Vertex;

public interface AGraphVertex extends AGraphElement, Vertex {

    AGraph graph();

    VertexId id();

    AGraphEdge addEdge(final String label, final Vertex inVertex, final Object... keyValues);
}
