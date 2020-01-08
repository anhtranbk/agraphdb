package com.agraph;

import com.agraph.core.EdgeId;
import com.agraph.core.VertexId;
import org.apache.tinkerpop.gremlin.structure.Direction;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.T;
import org.apache.tinkerpop.gremlin.structure.Transaction;
import org.apache.tinkerpop.gremlin.structure.Vertex;

import java.util.Iterator;
import java.util.Optional;

public interface AGraphTransaction extends Transaction {

    AGraph graph();

    Optional<AGraphVertex> findVertex(VertexId vertexId, String label);

    Iterator<AGraphVertex> vertices(final Object... vertexIds);

    AGraphVertex addVertex(final Object... keyValues);

    default AGraphVertex addVertex(final String label) {
        return this.addVertex(T.label, label);
    }

    void removeVertex(AGraphVertex vertex);

    Optional<AGraphEdge> findEdge(EdgeId edgeId);

    Iterator<AGraphVertex> edges(final Object... edgeIds);

    Iterator<Edge> edges(AGraphVertex ownVertex, Direction direction, String... edgeLabels);

    Iterator<Vertex> vertices(AGraphVertex ownVertex, Direction direction, String... edgeLabels);

    AGraphEdge addEdge(final String label, final Vertex outVertex, final Vertex inVertex,
                       final Object... keyValues);

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
