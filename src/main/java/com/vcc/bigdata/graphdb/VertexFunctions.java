package com.vcc.bigdata.graphdb;

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

    /**
     * Find one vertex with properties by id and label
     *
     * @param id
     * @param label
     * @return
     */
    Optional<Vertex> vertex(String id, String label);

    /**
     * Find all vertices by their labels
     *
     * @param labels
     * @return
     */
    VertexSet vertices(String... labels);

    /**
     * Find adjacency vertices of a vertex
     *
     * @param vertex
     * @param direction
     * @param edgeLabels
     * @return
     */
    VertexSet vertices(Vertex vertex, Direction direction, String... edgeLabels);

    /**
     * Find adjacency vertices of a vertex, filtered by adjacency vertex's label
     *
     * @param vertex
     * @param adjVertexLabels
     * @return
     */
    VertexSet verticesByAdjVertexLabels(Vertex vertex, Direction direction, String... adjVertexLabels);

    /**
     * Add collection of vertices to graphdb
     *
     * @param ts       timestamp use as write time in backend storage (HBase/Cassandra)
     * @param vertices collection of vertices to save
     * @return java.util.concurrent.ListenableFuture<VertexSet>
     */
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

    /**
     * @param ts
     * @param label
     * @param id
     * @return java.util.concurrent.ListenableFuture<VertexSet>
     */
    ListenableFuture<VertexSet> deleteVertex(long ts, String id, String label);

    default ListenableFuture<VertexSet> deleteVertex(String id, String label) {
        return deleteVertex(-1, id, label);
    }
}
