package com.agraph;

import com.agraph.core.tinkerpop.AGraphFeatures;
import com.agraph.core.tinkerpop.AGraphVariables;
import com.agraph.core.transaction.TransactionBuilder;
import org.apache.tinkerpop.gremlin.process.computer.GraphComputer;
import org.apache.tinkerpop.gremlin.structure.Graph;

public interface AGraph extends Graph {

    AGraphTransaction newTransaction();

    TransactionBuilder transactionBuilder();

    boolean isOpen();

    boolean isClosed();

    @Override
    default  <C extends GraphComputer> C compute(Class<C> graphComputerClass) throws IllegalArgumentException {
        throw Graph.Exceptions.graphDoesNotSupportProvidedGraphComputer(graphComputerClass);
    }

    @Override
    default GraphComputer compute() throws IllegalArgumentException {
        throw Graph.Exceptions.graphComputerNotSupported();
    }

    @Override
    default Variables variables() {
        return new AGraphVariables();
    }

    @Override
    default Features features() {
        return new AGraphFeatures();
    }
}
