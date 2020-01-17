package com.agraph;

import com.agraph.core.type.EdgeId;
import com.agraph.core.type.VertexId;
import org.apache.tinkerpop.gremlin.structure.Direction;
import org.apache.tinkerpop.gremlin.structure.Transaction;

import java.util.Collections;
import java.util.Iterator;
import java.util.Optional;

public interface AGraphTransaction extends Transaction {

    long id();

    AGraph graph();

    Optional<AGraphVertex> findVertex(VertexId vertexId);

    Iterator<AGraphVertex> vertices(Iterable<VertexId> vertexIds);

    default Iterator<AGraphVertex> vertices() {
        return vertices(Collections.emptyList());
    }

    AGraphVertex addVertex(final AGraphVertex vertex);

    void removeVertex(AGraphVertex vertex);

    Optional<AGraphEdge> findEdge(EdgeId edgeId);

    Iterator<AGraphEdge> edges(Iterable<EdgeId> edgeIds);

    default Iterator<AGraphEdge> edges() {
        return edges(Collections.emptyList());
    }

    Iterator<AGraphEdge> edges(AGraphVertex ownVertex, Direction direction, String... edgeLabels);

    Iterator<AGraphVertex> vertices(AGraphVertex ownVertex, Direction direction, String... edgeLabels);

    AGraphEdge addEdge(final AGraphEdge edge);

    void removeEdge(AGraphEdge edge);

    @Override
    void commit();

    @Override
    void rollback();

    @Override
    boolean isOpen();

    boolean isClosed();

    boolean hasModifications();
}
