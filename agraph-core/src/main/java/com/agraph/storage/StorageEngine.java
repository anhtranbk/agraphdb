package com.agraph.storage;

import com.agraph.GraphComponent;
import com.agraph.config.Config;
import com.agraph.core.InternalEdge;
import com.agraph.core.InternalVertex;
import com.agraph.core.type.EdgeId;
import com.agraph.core.type.VertexId;
import org.apache.tinkerpop.gremlin.structure.Direction;

import java.util.Iterator;

public interface StorageEngine extends GraphComponent, AutoCloseable {

    void open(Config conf); // open connect to backend db

    boolean opened();

    void initialize(); // init db and table for graph

    boolean initialized();

    StorageBackend backend();

    void beginBackendTx();

    void commitBackendTx();

    void rollbackBackendTx();

    @Override
    void close();

    Iterator<InternalVertex> vertices(String... labels);

    Iterator<InternalVertex> vertices(Iterable<VertexId> vertexIds, String... labels);

    Iterator<InternalEdge> edges(Iterable<EdgeId> edgeIds);

    Iterator<InternalEdge> edges(VertexId ownVertexId, Direction direction, String... labels);

    Iterator<InternalEdge> edges(VertexId ownVertexId, Direction direction,
                                 Iterable<VertexId> otherVertexIds, String... labels);

    void addVertexModifications(Iterable<InternalVertex> vertices);

    void addVertexRemovals(Iterable<InternalVertex> vertices);

    void addEdgeModifications(Iterable<InternalEdge> edges);

    void addEdgeRemovals(Iterable<InternalEdge> edges);
}
