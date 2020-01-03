package com.agraph.core.internal;

import com.agraph.v1.Direction;
import com.agraph.v1.Vertex;
import com.agraph.v1.VertexSet;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

/**
 * TODO: Class description here.
 *
 * @author <a href="https://github.com/tjeubaoit">tjeubaoit</a>
 */
public interface VertexFunctions {

    Optional<Vertex> vertex(String id, String label);

    VertexSet vertices(String... labels);

    VertexSet vertices(Vertex vertex, Direction direction, String... edgeLabels);

    VertexSet verticesByAdjVertexLabels(Vertex vertex, Direction direction, String... adjVertexLabels);

    ListenableFuture<VertexSet> addVertices(long ts, Collection<Vertex> vertices);

    default ListenableFuture<VertexSet> addVertex(long ts, Vertex vertex) {
        return addVertices(ts, Collections.singleton(vertex));
    }

    default ListenableFuture<VertexSet> addVertex(String id, String label, Map<String, Object> props) {
        return addVertex(-1, Vertex.create(id, label, props));
    }

    default ListenableFuture<VertexSet> addVertex(String id, String label) {
        return addVertex(-1, Vertex.create(id, label));
    }

    default ListenableFuture<VertexSet> addVertex(Vertex vertex) {
        return addVertex(-1, vertex);
    }

    default ListenableFuture<VertexSet> addVertices(Collection<Vertex> vertices) {
        return addVertices(-1, vertices);
    }

    ListenableFuture<VertexSet> deleteVertex(long ts, String id, String label);

    default ListenableFuture<VertexSet> deleteVertex(String id, String label) {
        return deleteVertex(-1, id, label);
    }
}
