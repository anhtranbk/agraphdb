package com.agraph.core;

import com.agraph.AGraphTransaction;
import com.agraph.AGraphVertex;
import org.apache.commons.configuration.Configuration;
import org.apache.tinkerpop.gremlin.process.computer.GraphComputer;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.Transaction;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.structure.util.AbstractTransaction;
import org.apache.tinkerpop.gremlin.structure.util.ElementHelper;
import org.apache.tinkerpop.gremlin.structure.util.TransactionException;

import java.util.Iterator;
import java.util.Optional;
import java.util.function.Consumer;

public class TinkerBaseTransaction extends AbstractTransaction implements AGraphTransaction {

    public TinkerBaseTransaction(Graph g) {
        super(g);
    }

    @Override
    public AGraphVertex addVertex(Object... keyValues) {
        ElementHelper.legalPropertyKeyValueArray();
        return null;
    }

    @Override
    public <C extends GraphComputer> C compute(Class<C> graphComputerClass) throws IllegalArgumentException {
        return null;
    }

    @Override
    public GraphComputer compute() throws IllegalArgumentException {
        return null;
    }

    @Override
    public Iterator<Vertex> vertices(Object... vertexIds) {
        return null;
    }

    @Override
    public Iterator<Edge> edges(Object... edgeIds) {
        return null;
    }

    @Override
    public Transaction tx() {
        return null;
    }

    @Override
    public Variables variables() {
        return null;
    }

    @Override
    public Configuration configuration() {
        return null;
    }

    @Override
    public Optional<AGraphVertex> vertex(String id, String label) {
        return Optional.empty();
    }

    @Override
    public Iterable<AGraphVertex> vertices(String... labels) {
        return null;
    }

    @Override
    public boolean isClosed() {
        return false;
    }

    @Override
    public boolean hasModifications() {
        return false;
    }

    @Override
    protected void doOpen() {

    }

    @Override
    protected void doCommit() throws TransactionException {

    }

    @Override
    protected void doRollback() throws TransactionException {

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
}
