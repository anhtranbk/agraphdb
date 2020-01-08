package com.agraph.storage;

import com.agraph.AGraphEdge;
import com.agraph.AGraphVertex;
import com.agraph.GraphComponent;
import com.agraph.core.EdgeId;
import com.agraph.core.VertexId;
import org.apache.tinkerpop.gremlin.structure.Direction;

import java.util.Iterator;
import java.util.concurrent.Future;

public interface StorageEngine extends GraphComponent, AutoCloseable {

    Future<?> initialize();

    Iterator<AGraphVertex> vertices(String... labels);

    Iterator<AGraphVertex> vertices(Iterable<VertexId> vertexIds, String... labels);

    Future<?> mutateVertices(Iterable<AGraphVertex> vertices);

    Future<?> deleteVertices(Iterable<VertexId> vertexIds, String... labels);

    Iterator<AGraphEdge> edges(Iterable<EdgeId> edgeIds);

    Iterator<AGraphEdge> edges(VertexId vertexId, Direction direction, String... labels);

    Iterator<AGraphEdge> edges(VertexId ownVertexId, Direction direction,
                               Iterable<VertexId> otherVertexIds, String... labels);

    Future<?> mutateEdges(Iterable<AGraphEdge> edges);

    Future<?> deleteEdges(Iterable<EdgeId> edgeIds);

    StorageBackend getBackend();

    @Override
    void close();
}
