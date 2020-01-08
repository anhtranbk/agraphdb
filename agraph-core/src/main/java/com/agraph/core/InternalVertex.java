package com.agraph.core;

import com.agraph.AGraph;
import com.agraph.AGraphEdge;
import com.agraph.AGraphVertex;
import com.agraph.State;
import org.apache.tinkerpop.gremlin.structure.Direction;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.structure.VertexProperty;
import org.apache.tinkerpop.gremlin.structure.util.ElementHelper;
import org.apache.tinkerpop.gremlin.structure.util.StringFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * @author <a href="https://github.com/tjeubaoit">tjeubaoit</a>
 */
public class InternalVertex extends AbstractElement implements AGraphVertex {

    public InternalVertex(AGraph graph, VertexId id) {
        super(graph, id, Vertex.DEFAULT_LABEL);
    }

    public InternalVertex(AGraph graph, VertexId id, String label) {
        super(graph, id, label, State.NEW);
    }

    public InternalVertex(AGraph graph, VertexId id, String label, State state) {
        super(graph, id, label, state);
    }

    @Override
    public VertexId id() {
        return (VertexId) super.id();
    }

    @Override
    public AGraphEdge addEdge(String label, Vertex inVertex, Object... keyValues) {
        return this.tx().addEdge(label, this, inVertex, keyValues);
    }

    @Override
    public <V> VertexProperty<V> property(VertexProperty.Cardinality cardinality,
                                          String key, V value, Object... keyValues) {
        if (keyValues.length != 0) {
            throw VertexProperty.Exceptions.metaPropertiesNotSupported();
        }
        if (cardinality != VertexProperty.Cardinality.single) {
            throw new UnsupportedOperationException("Cardinality list or set is not supported");
        }
        AGraphVertexProperty<V> newProperty = new AGraphVertexProperty<>(this, key, value);
        this.putProperty(newProperty);
        return newProperty;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <V> Iterator<VertexProperty<V>> properties(String... keys) {
        int propsCapacity = keys.length == 0 ? this.numProperties() : keys.length;
        List<VertexProperty<V>> props = new ArrayList<>(propsCapacity);

        if (keys.length == 0) {
            for (AGraphProperty<?> prop : this.properties().values()) {
                assert prop instanceof VertexProperty;
                if (prop.isPresent()) {
                    props.add((VertexProperty<V>) prop);
                }
            }
        } else {
            for (String key : keys) {
                AGraphProperty<?> prop = this.getProperty(key);
                if (prop == null) continue;

                assert prop instanceof VertexProperty;
                if (prop.isPresent()) {
                    props.add((VertexProperty<V>) this.getProperty(key));
                }
            }
        }
        return props.iterator();
    }

    @Override
    public Iterator<Edge> edges(Direction direction, String... edgeLabels) {
        return this.tx().edges(this, direction, edgeLabels);
    }

    @Override
    public Iterator<Vertex> vertices(Direction direction, String... edgeLabels) {
        return this.tx().vertices(this, direction, edgeLabels);
    }

    @Override
    public void remove() {
        super.remove();
        this.tx().removeVertex(this);
    }

    @Override
    protected boolean ensureFilledProperties(boolean throwIfNotExist) {
        if (!isNew()) {
            return true;
        }
        Optional<AGraphVertex> op = tx().findVertex(this.id(), this.label());
        if (!op.isPresent()) {
            if (throwIfNotExist) {
                throw new NoSuchElementException("Vertex does not exist");
            }
            return false;
        }
        AGraphVertex other = op.get();
        assert other instanceof InternalVertex;
        this.copyProperties((InternalVertex) other);
        return true;
    }

    @Override
    protected AbstractElement copy() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String toString() {
        return StringFactory.vertexString(this);
    }
}
