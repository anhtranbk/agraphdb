package com.agraph.core;

import com.agraph.AGraph;
import com.agraph.AGraphEdge;
import com.agraph.State;
import com.google.common.collect.Iterators;
import org.apache.tinkerpop.gremlin.structure.Direction;
import org.apache.tinkerpop.gremlin.structure.Property;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.structure.VertexProperty;
import org.apache.tinkerpop.gremlin.structure.util.StringFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

public class InternalEdge extends AbstractElement implements AGraphEdge {

    private final InternalVertex inVertex, outVertex;

    public InternalEdge(AGraph graph, String label, InternalVertex outVertex, InternalVertex inVertex) {
        super(graph, EdgeId.from(label, outVertex, inVertex), label);
        this.inVertex = inVertex;
        this.outVertex = outVertex;
    }

    @Override
    public EdgeId id() {
        return (EdgeId) super.id();
    }

    @Override
    public <V> Property<V> property(String key, V value) {
        AGraphEdgeProperty<V> newProperty = new AGraphEdgeProperty<>(this, key, value);
        this.putProperty(newProperty);
        return newProperty;
    }

    @Override
    public Iterator<Vertex> vertices(Direction direction) {
        Iterator<InternalVertex> it;
        if (Direction.IN.equals(direction)) {
            it = Collections.singletonList(inVertex).iterator();
        } else if (Direction.OUT.equals(direction)) {
            it = Collections.singletonList(outVertex).iterator();
        } else {
            it = Arrays.asList(inVertex, outVertex).iterator();
        }
        return Iterators.transform(it, v -> v);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <V> Iterator<Property<V>> properties(String... keys) {
        int propsCapacity = keys.length == 0 ? this.numProperties() : keys.length;
        List<Property<V>> props = new ArrayList<>(propsCapacity);

        if (keys.length == 0) {
            for (AGraphProperty<?> prop : this.properties().values()) {
                if (prop.isPresent()) {
                    props.add((Property<V>) prop);
                }
            }
        } else {
            for (String key : keys) {
                AGraphProperty<?> prop = this.getProperty(key);
                if (prop == null) continue;
                if (prop.isPresent()) {
                    props.add(this.getProperty(key));
                }
            }
        }
        return props.iterator();
    }

    @Override
    public void remove() {
        super.remove();
        this.tx().removeEdge(this);
    }

    @Override
    protected boolean ensureFilledProperties(boolean throwIfNotExist) {
        if (!isNew()) {
            return true;
        }
        Optional<AGraphEdge> op = tx().findEdge(this.id());
        if (!op.isPresent()) {
            if (throwIfNotExist) {
                throw new NoSuchElementException("Vertex does not exist");
            }
            return false;
        }
        AGraphEdge other = op.get();
        assert other instanceof InternalEdge;
        this.copyProperties((InternalEdge) other);
        return true;
    }

    @Override
    protected AbstractElement copy() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String toString() {
        return StringFactory.edgeString(this);
    }
}
