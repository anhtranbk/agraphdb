package com.agraph.core.internal;

import com.agraph.v1.Direction;
import com.agraph.v1.Edge;
import com.agraph.v1.EdgeSet;
import com.agraph.v1.Vertex;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

/**
 * TODO: Class description here.
 *
 * @author <a href="https://github.com/tjeubaoit">tjeubaoit</a>
 */
public interface EdgeFunctions {

    Edge edge(String label, Vertex outVertex, Vertex inVertex);

    EdgeSet edges(Vertex vertex, Direction direction, String... edgeLabels);

    EdgeSet edgesByAdjVertexLabels(Vertex vertex, Direction direction, String... adjVertexLabels);

    ListenableFuture<EdgeSet> addEdges(long ts, Collection<Edge> edges);

    default ListenableFuture<EdgeSet> addEdge(long ts, Edge edge) {
        return addEdges(ts, Collections.singleton(edge));
    }

    default ListenableFuture<EdgeSet> addEdge(Edge edge) {
        return addEdge(-1, edge);
    }

    default ListenableFuture<EdgeSet> addEdges(Collection<Edge> edges) {
        return addEdges(-1, edges);
    }

    default ListenableFuture<EdgeSet> addEdge(String label,
                                              Vertex outVertex,
                                              Vertex inVertex,
                                              Map<String, Object> edgeProps) {
        return addEdge(Edge.create(label, outVertex, inVertex, edgeProps));
    }

    default ListenableFuture<EdgeSet> addEdge(String label, Vertex outVertex, Vertex inVertex) {
        return addEdge(label, outVertex, inVertex, Collections.emptyMap());
    }

    ListenableFuture<EdgeSet> removeEdge(long ts, String label, Vertex outVertex, Vertex inVertex);

    default ListenableFuture<EdgeSet> removeEdge(String label, Vertex outVertex, Vertex inVertex) {
        return removeEdge(-1, label, outVertex, inVertex);
    }
}
