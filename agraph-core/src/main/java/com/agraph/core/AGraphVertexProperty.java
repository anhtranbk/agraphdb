package com.agraph.core;

import com.agraph.AGraphVertex;
import com.agraph.State;
import org.apache.tinkerpop.gremlin.structure.Property;
import org.apache.tinkerpop.gremlin.structure.VertexProperty;

import java.util.Iterator;

public class AGraphVertexProperty<V> extends AGraphProperty<V> implements VertexProperty<V> {

    public AGraphVertexProperty(AbstractElement owner, String key, V value) {
        super(owner, key, value);
        assert owner instanceof AGraphVertex;
    }

    @Override
    public AGraphVertex element() {
        return (AGraphVertex) this.owner;
    }

    @Override
    public Object id() {
        return owner.id().toString() + "_" + key;
    }

    @Override
    public <U> Property<U> property(String key, U value) {
        throw VertexProperty.Exceptions.metaPropertiesNotSupported();
    }

    @Override
    public <U> Iterator<Property<U>> properties(final String... propertyKeys) {
        throw VertexProperty.Exceptions.metaPropertiesNotSupported();
    }
}
