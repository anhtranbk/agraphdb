package com.agraph;

import com.agraph.config.Config;
import com.agraph.core.serialize.Serializer;
import com.agraph.core.tx.TransactionBuilder;
import org.apache.tinkerpop.gremlin.process.computer.GraphComputer;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.T;

import java.io.Closeable;

public interface AGraph extends Graph, Closeable {

    String name();

    Config config();

    Serializer serializer();

    AGraphTransaction newTransaction();

    TransactionBuilder transactionBuilder();

    boolean isOpen();

    boolean isClosed();

    @Override
    AGraphVertex addVertex(final Object... keyValues);

    @Override
    default AGraphVertex addVertex(final String label) {
        return this.addVertex(T.label, label);
    }

    @Override
    AGraphTransaction tx();

    @Override
    void close();

    @Override
    default  <C extends GraphComputer> C compute(Class<C> graphComputerClass) throws IllegalArgumentException {
        throw Graph.Exceptions.graphDoesNotSupportProvidedGraphComputer(graphComputerClass);
    }

    @Override
    default GraphComputer compute() throws IllegalArgumentException {
        throw Graph.Exceptions.graphComputerNotSupported();
    }
}
