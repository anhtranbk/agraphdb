package com.agraph.core;

import com.agraph.AGraphEdge;
import com.agraph.AGraphVertex;
import com.agraph.core.type.EdgeId;
import com.google.common.base.Preconditions;
import com.google.common.collect.Iterators;
import io.reactivex.Observable;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.apache.tinkerpop.gremlin.structure.Direction;
import org.apache.tinkerpop.gremlin.structure.Property;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.structure.util.StringFactory;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.NoSuchElementException;

@Accessors(fluent = true)
public class InternalEdge extends AbstractElement implements AGraphEdge {

    private final InternalVertex inVertex, outVertex;
    @Getter
    private long internalId;

    public InternalEdge(DefaultAGraph graph, String label,
                        InternalVertex outVertex, InternalVertex inVertex) {
        super(graph, EdgeId.create(label, outVertex, inVertex), label);

        Preconditions.checkNotNull(outVertex, "Outgoing vertex can't be null");
        Preconditions.checkNotNull(inVertex, "Incoming vertex can't be null");

        this.inVertex = inVertex;
        this.outVertex = outVertex;
        this.internalId = graph.getIdGenerator().generate();
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

    @Override
    public AGraphVertex inVertex() {
        return this.inVertex;
    }

    @Override
    public AGraphVertex outVertex() {
        return this.outVertex;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <V> Iterator<Property<V>> properties(String... keys) {
        if (keys.length > 0) {
            return Observable.fromArray(keys)
                    .map(key -> (Property<V>) this.autoFilledProperties().get(key))
                    .filter(Property::isPresent)
                    .blockingIterable()
                    .iterator();
        } else {
            return Observable.fromIterable(this.autoFilledProperties().values())
                    .filter(AGraphProperty::isPresent)
                    .map(e -> (Property<V>) e)
                    .blockingIterable()
                    .iterator();
        }
    }

    @Override
    public void remove() {
        super.remove();
        this.tx().removeEdge(this);
    }

    @Override
    public boolean ensureFilledProperties(boolean throwIfNotExist) {
        if (!isLagged()) {
            logger.debug("Edge has already loaded");
            return true;
        }
        if (!this.tx().fillEdgeProperties(this)) {
            if (throwIfNotExist) {
                throw new NoSuchElementException("Edge does not exist: " + this.id());
            } else {
                logger.warn("Edge does not exist: {}", this.id());
                return false;
            }
        }
        return true;
    }

    @Override
    public AbstractElement copy() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String toString() {
        return StringFactory.edgeString(this);
    }

    public void assignInternalId(long id) {
        this.internalId = id;
    }
}
