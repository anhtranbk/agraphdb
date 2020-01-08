package com.agraph.core;

import com.agraph.AGraphEdge;

public class AGraphEdgeProperty<V> extends AGraphProperty<V> {

    public AGraphEdgeProperty(AbstractElement owner, String key, V value) {
        super(owner, key, value);
        assert owner instanceof AGraphEdge;
    }

    @Override
    public AGraphEdge element() {
        return (AGraphEdge) this.owner;
    }
}
