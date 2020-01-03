package com.agraph.core.tinkerpop;

import org.apache.tinkerpop.gremlin.structure.Graph;

import java.util.Optional;
import java.util.Set;

public class AGraphVariables implements Graph.Variables {

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
}
