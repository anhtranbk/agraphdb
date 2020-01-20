package com.agraph.core.mock;

import com.agraph.AGraphTransaction;
import com.agraph.AGraphVertex;
import com.agraph.config.Config;
import com.agraph.core.DefaultAGraph;
import com.agraph.core.tx.TransactionBuilder;
import org.apache.commons.configuration.Configuration;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Vertex;

import java.util.Iterator;

public class MockGraph extends DefaultAGraph {

    public MockGraph(Config aGraphConf) {
        super(aGraphConf);
    }

    @Override
    public String name() {
        return null;
    }

    @Override
    public Config config() {
        return null;
    }

    @Override
    public AGraphTransaction newTransaction() {
        return null;
    }

    @Override
    public TransactionBuilder transactionBuilder() {
        return null;
    }

    @Override
    public boolean isOpen() {
        return false;
    }

    @Override
    public boolean isClosed() {
        return false;
    }

    @Override
    public AGraphVertex addVertex(Object... keyValues) {
        return null;
    }

    @Override
    public Iterator<Vertex> vertices(Object... ids) {
        return null;
    }

    @Override
    public Iterator<Edge> edges(Object... ids) {
        return null;
    }

    @Override
    public AGraphTransaction tx() {
        return new MockTransaction();
    }

    @Override
    public void close() {
    }

    @Override
    public Variables variables() {
        return null;
    }

    @Override
    public Configuration configuration() {
        return null;
    }
}
