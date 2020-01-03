package com.agraph.core;

import com.agraph.AGraph;
import com.agraph.AGraphTransaction;
import org.apache.commons.configuration.Configuration;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Transaction;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;

public abstract class TinkerBaseGraph implements AGraph {

    protected static final Logger logger = LoggerFactory.getLogger(TinkerBaseGraph.class);

    @Override
    public boolean isOpen() {
        return getCurrentTx().isOpen();
    }

    @Override
    public boolean isClosed() {
        return getCurrentTx().isClosed();
    }

    @Override
    public Vertex addVertex(Object... keyValues) {
        return getCurrentTx().addVertex(keyValues);
    }

    @Override
    public Iterator<Vertex> vertices(Object... vertexIds) {
        return getCurrentTx().vertices(vertexIds);
    }

    @Override
    public Iterator<Edge> edges(Object... edgeIds) {
        return getCurrentTx().edges(edgeIds);
    }

    @Override
    public Transaction tx() {
        return getCurrentTx();
    }

    @Override
    public void close() throws Exception {
    }

    @Override
    public Configuration configuration() {
        return null;
    }

    protected abstract AGraphTransaction getCurrentTx();
}
