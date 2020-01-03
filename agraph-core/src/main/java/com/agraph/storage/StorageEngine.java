package com.agraph.storage;

import com.agraph.AGraph;
import com.agraph.AGraphEdge;
import com.agraph.AGraphVertex;
import com.agraph.common.tuple.Tuple3;
import com.agraph.core.internal.EdgeId;
import com.agraph.core.internal.VertexId;
import org.apache.tinkerpop.gremlin.structure.Direction;

import java.util.Iterator;
import java.util.concurrent.Future;

public interface StorageEngine extends AutoCloseable {

    Future<?> initialize(AGraph graph);

    Iterator<AGraphVertex> vertices(String... labels);

    Iterator<AGraphVertex> vertices(Iterable<VertexId> vertexIds, String... labels);

    Future<?> mutateVertices(Iterable<AGraphVertex> vertices);

    Future<?> deleteVertices(Iterable<VertexId> vertexIds, String... labels);

//    Iterator<AGraphEdge> edges(Iterable<EdgeId> edgeIds);
    
    Iterator<AGraphEdge> edges(Iterable<Tuple3<VertexId, VertexId, String>> edgeIds);

    Iterator<AGraphEdge> edges(VertexId vertexId, Direction direction, String... labels);

    Iterator<AGraphEdge> edges(VertexId vertexId, Direction direction,
                               Iterable<VertexId> vertexIds, String... labels);

    Future<?> mutateEdges(Iterable<AGraphEdge> edges);

    Future<?> deleteEdges(Iterable<EdgeId> edgeIds);

    StorageBackend getBackend();

    @Override
    void close();
}
