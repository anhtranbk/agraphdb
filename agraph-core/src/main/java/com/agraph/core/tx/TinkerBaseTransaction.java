package com.agraph.core.tx;

import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.Transaction;
import org.apache.tinkerpop.gremlin.structure.util.AbstractTransaction;

import java.util.List;
import java.util.function.Consumer;

/**
 * @author <a href="https://github.com/tjeubaoit">tjeubaoit</a>
 */
public abstract class TinkerBaseTransaction extends AbstractTransaction {

    public TinkerBaseTransaction(Graph g) {
        super(g);
    }

    @Override
    protected void fireOnCommit() {

    }

    @Override
    protected void fireOnRollback() {

    }

    @Override
    protected void doReadWrite() {

    }

    @Override
    protected void doClose() {

    }

    @Override
    public boolean isOpen() {
        return false;
    }

    @Override
    public Transaction onReadWrite(Consumer<Transaction> consumer) {
        return null;
    }

    @Override
    public Transaction onClose(Consumer<Transaction> consumer) {
        return null;
    }

    @Override
    public void addTransactionListener(Consumer<Status> listener) {

    }

    @Override
    public void removeTransactionListener(Consumer<Status> listener) {

    }

    @Override
    public void clearTransactionListeners() {

    }

    protected abstract List<Consumer<Status>> consumers();
}
