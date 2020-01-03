package com.agraph;

import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.T;
import org.apache.tinkerpop.gremlin.structure.Transaction;

import java.util.Optional;

public interface AGraphTransaction extends Graph, Transaction {

    AGraphVertex addVertex(final Object... keyValues);

    default AGraphVertex addVertex(final String label) {
        return this.addVertex(T.label, label);
    }

    Optional<AGraphVertex> vertex(String id, String label);

    Iterable<AGraphVertex> vertices(String... labels);

    @Override
    void commit();

    @Override
    void rollback();

    @Override
    boolean isOpen();

    boolean isClosed();

    boolean hasModifications();
}
