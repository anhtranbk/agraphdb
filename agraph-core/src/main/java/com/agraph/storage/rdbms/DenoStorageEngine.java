package com.agraph.storage.rdbms;

import com.agraph.AGraph;
import com.agraph.AGraphEdge;
import com.agraph.AGraphVertex;
import com.agraph.core.internal.EdgeId;
import com.agraph.core.internal.VertexId;
import com.agraph.storage.StorageBackend;
import org.apache.tinkerpop.gremlin.structure.Direction;

import java.util.Iterator;
import java.util.concurrent.Future;

public abstract class DenoStorageEngine extends RdbmsStorageEngine {

    public DenoStorageEngine(RdbmsStorageBackend backend) {
        super(backend);
    }

    @Override
    public Future<?> initialize(AGraph graph) {
        return null;
    }

    @Override
    public Iterator<AGraphVertex> vertices(String... labels) {
        return null;
    }

    @Override
    public Iterator<AGraphVertex> vertices(Iterable<VertexId> vertexIds, String... labels) {
        return null;
    }

    @Override
    public Future<?> mutateVertices(Iterable<AGraphVertex> vertices) {
        return null;
    }

    @Override
    public Future<?> deleteVertices(Iterable<VertexId> vertexIds, String... labels) {
        return null;
    }

    @Override
    public Iterator<AGraphEdge> edges(VertexId vertexId, Direction direction, String... labels) {
        return null;
    }

    @Override
    public Iterator<AGraphEdge> edges(VertexId vertexId, Direction direction,
                                      Iterable<VertexId> vertexIds, String... labels) {
        return null;
    }

    @Override
    public Future<?> mutateEdges(Iterable<AGraphEdge> edges) {
        return null;
    }

    @Override
    public Future<?> deleteEdges(Iterable<EdgeId> edgeIds) {
        return null;
    }

    @Override
    public StorageBackend getBackend() {
        return null;
    }

    @Override
    public void close() {

    }
}
