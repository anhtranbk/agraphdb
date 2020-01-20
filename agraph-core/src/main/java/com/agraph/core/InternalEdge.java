package com.agraph.core;

import com.agraph.AGraphEdge;
import com.agraph.State;
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
import java.util.Map;

@Accessors(fluent = true)
@Getter
public class InternalEdge extends AbstractElement implements AGraphEdge {

    private final InternalVertex inVertex, outVertex;
    private long internalId;

    public InternalEdge(DefaultAGraph graph, EdgeId id, String label, State state,
                        InternalVertex outVertex, InternalVertex inVertex,
                        Map<String, ? extends AGraphEdgeProperty<?>> props) {
        super(graph, id, label, state, props);

        Preconditions.checkNotNull(outVertex, "Outgoing vertex can't be null");
        Preconditions.checkNotNull(inVertex, "Incoming vertex can't be null");

        this.inVertex = inVertex;
        this.outVertex = outVertex;
        this.internalId = graph.idPool().generate();
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
        if (keys.length > 0) {
            return Observable.fromArray(keys)
                    .map(key -> (Property<V>) this.autoFilledProperties().get(key))
                    .filter(Property::isPresent)
                    .blockingIterable()
                    .iterator();
        } else {
            return Iterators.transform(
                    this.autoFilledProperties().values().iterator(),
                    e -> (Property<V>) e
            );
        }
    }

    @Override
    public void remove() {
        this.updateState(State.REMOVED);
        this.tx().removeEdge(this);
    }

    @Override
    public AbstractElement copy() {
        return ElementBuilders.edgeBuilder().from(this).build();
    }

    @Override
    public String toString() {
        return StringFactory.edgeString(this);
    }

    public void assignInternalId(long id) {
        if (id != 0) this.internalId = id;
    }
}
