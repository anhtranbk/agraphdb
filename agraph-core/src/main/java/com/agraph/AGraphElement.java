package com.agraph;

import org.apache.tinkerpop.gremlin.structure.Element;

public interface AGraphElement extends Element, Statifiable, Cloneable {

    AGraph graph();

    default AGraphTransaction tx() {
        return graph().tx();
    }
}
