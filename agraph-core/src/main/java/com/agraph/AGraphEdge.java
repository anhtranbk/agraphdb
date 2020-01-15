package com.agraph;

import com.agraph.core.type.EdgeId;
import org.apache.tinkerpop.gremlin.structure.Edge;

public interface AGraphEdge extends AGraphElement, Edge {

    AGraph graph();

    EdgeId id();

    AGraphVertex outVertex();

    AGraphVertex inVertex();
}
