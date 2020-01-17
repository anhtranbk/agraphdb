package com.agraph.core;

import com.agraph.AGraph;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.util.StringFactory;

import java.util.Optional;
import java.util.Set;

public class AGraphVariables implements Graph.Variables {

    private final AGraph graph;

    public AGraphVariables(AGraph graph) {
        this.graph = graph;
    }

    @Override
    public Set<String> keys() {
        throw new UnsupportedOperationException();
    }

    @Override
    public <R> Optional<R> get(String key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void set(String key, Object value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void remove(String key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String toString() {
        return StringFactory.graphVariablesString(this);
    }
}
