package com.agraph;

import com.agraph.core.internal.VertexId;
import org.apache.tinkerpop.gremlin.structure.Element;

public interface AGraphElement extends Element {

    AGraph graph();

    VertexId id();

    void remove();
}
