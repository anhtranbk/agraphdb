package com.agraph.internal;

import com.agraph.AGraph;
import com.agraph.AGraphElement;
import org.apache.tinkerpop.gremlin.structure.Property;

import java.util.Iterator;

public class AbstractElement implements AGraphElement {

    @Override
    public AGraph graph() {
        return null;
    }

    @Override
    public VertexId id() {
        return null;
    }

    @Override
    public String label() {
        return null;
    }

    @Override
    public void remove() {

    }

    @Override
    public <V> Property<V> property(String key, V value) {
        return null;
    }

    @Override
    public <V> Iterator<? extends Property<V>> properties(String... propertyKeys) {
        return null;
    }
}
