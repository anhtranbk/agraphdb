package com.agraph.core.mock;

import com.agraph.AGraph;
import com.agraph.AGraphEdge;
import com.agraph.AGraphTransaction;
import com.agraph.AGraphVertex;
import com.agraph.core.type.EdgeId;
import com.agraph.core.type.VertexId;
import org.apache.tinkerpop.gremlin.structure.Direction;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.Transaction;

import java.util.Iterator;
import java.util.Optional;
import java.util.function.Consumer;

public class MockTransaction implements AGraphTransaction {

    @Override
    public long txId() {
        return 0;
    }

    @Override
    public AGraph graph() {
        return null;
    }

    @Override
    public Optional<AGraphVertex> findVertex(VertexId vertexId) {
        return Optional.empty();
    }

    @Override
    public Iterator<AGraphVertex> vertices(Iterable<VertexId> vertexIds) {
        return null;
    }

    @Override
    public AGraphVertex addVertex(AGraphVertex vertex) {
        return null;
    }

    @Override
    public void removeVertex(AGraphVertex vertex) {

    }

    @Override
    public Optional<AGraphEdge> findEdge(EdgeId edgeId) {
        return Optional.empty();
    }

    @Override
    public Iterator<AGraphEdge> edges(Iterable<EdgeId> edgeIds) {
        return null;
    }

    @Override
    public Iterator<AGraphEdge> edges(AGraphVertex ownVertex, Direction direction, String... edgeLabels) {
        return null;
    }

    @Override
    public Iterator<AGraphVertex> vertices(AGraphVertex ownVertex, Direction direction, String... edgeLabels) {
        return null;
    }

    @Override
    public AGraphEdge addEdge(AGraphEdge edge) {
        return null;
    }

    @Override
    public void removeEdge(AGraphEdge edge) {

    }

    @Override
    public void open() {

    }

    @Override
    public void commit() {

    }

    @Override
    public void rollback() {

    }

    @Override
    public <G extends Graph> G createThreadedTx() {
        return null;
    }

    @Override
    public boolean isOpen() {
        return false;
    }

    @Override
    public void readWrite() {

    }

    @Override
    public void close() {

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

    @Override
    public boolean isClosed() {
        return false;
    }

    @Override
    public boolean hasModifications() {
        return false;
    }
}
