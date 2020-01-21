package com.agraph.core.tx;

import com.agraph.AGraph;
import com.agraph.AGraphEdge;
import com.agraph.AGraphTransaction;
import com.agraph.AGraphVertex;
import com.agraph.common.util.LazyIterator;
import com.agraph.common.util.StreamSupports;
import com.agraph.core.DefaultAGraph;
import com.agraph.core.InternalEdge;
import com.agraph.core.InternalVertex;
import com.agraph.core.type.EdgeId;
import com.agraph.core.type.VertexId;
import com.agraph.storage.StorageEngine;
import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.apache.tinkerpop.gremlin.structure.Direction;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Accessors(fluent = true)
public class DefaultTransaction implements AGraphTransaction {

    private enum TxState {
        BEGIN, COMMITTING, COMMITT_FAIL, ROLLBACKING, ROLLBACK_FAIL, CLEAN
    }

    private static final Logger logger = LoggerFactory.getLogger(DefaultTransaction.class);

    private final DefaultAGraph graph;
    private final StorageEngine storageEngine;
    private long txId;

    private final Map<VertexId, InternalVertex> vertexMap = new HashMap<>();
    private final Map<EdgeId, InternalEdge> edgeMap = new HashMap<>();

    private final Set<InternalVertex> rmVertices = new HashSet<>();
    private final Set<InternalEdge> rmEdges = new HashSet<>();

    @Getter
    private boolean hasModifications;
    private TxState state;

    public DefaultTransaction(DefaultAGraph graph, StorageEngine storageEngine) {
        this.graph = graph;
        this.state = TxState.BEGIN;
        this.txId = graph.idPool().generate();

        this.storageEngine = storageEngine;
        this.storageEngine.open(graph.config());
    }

    @Override
    public long txId() {
        return this.txId;
    }

    @Override
    public AGraph graph() {
        return this.graph;
    }

    @Override
    public Optional<AGraphVertex> findVertex(VertexId vId) {
        InternalVertex cachedV = vertexMap.get(vId);
        if (cachedV != null && cachedV.isLoaded()) {
            logger.debug("Found vertex in transaction cache: {}", vId);
            return Optional.of(cachedV);
        }
        Iterator<InternalVertex> itV = storageEngine.vertices(Collections.singleton(vId));
        if (itV.hasNext()) {
            logger.debug("Found vertex in backend: {}", vId);
            InternalVertex other = itV.next();
            vertexMap.put(vId, other);
            return Optional.of(other);
        }
        return Optional.empty();
    }

    @Override
    public Iterator<AGraphVertex> vertices(Iterable<VertexId> vertexIds) {
        Iterable<InternalVertex> cachedVertices;
        if (Iterables.isEmpty(vertexIds)) {
            cachedVertices = vertexMap.values();
        } else {
            cachedVertices = StreamSupports.stream(vertexIds)
                    .map(vertexMap::get)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        }
        return Iterators.concat(
                cachedVertices.iterator(),
                LazyIterator.of(() -> storageEngine.vertices(vertexIds))
        );
    }

    @Override
    public AGraphVertex addVertex(AGraphVertex aVertex) {
        assert aVertex instanceof InternalVertex;
        InternalVertex vertex = (InternalVertex) aVertex;

        InternalVertex cachedV = vertexMap.get(vertex.id());
        if (cachedV != null) {
            if (cachedV.isNew()) {
                logger.warn("Vertex is exists. Add duplicate vertex will replace old properties");
            }
            if (!cachedV.isRemoved()) {
                logger.debug("Merge properties for vertex {}", cachedV);
                cachedV.copyProperties(vertex);
            }
        }
        this.hasModifications = true;
        return vertexMap.put(vertex.id(), vertex);
    }

    @Override
    public void removeVertex(AGraphVertex aVertex) {
        this.hasModifications = true;
        InternalVertex vertex = this.vertexMap.remove(aVertex.id());
        if (vertex != null) {
            if (!vertex.isNew()) {
                this.rmVertices.add(vertex);
            }
            logger.debug("Vertex has been removed {}", vertex);
        }
    }

    @Override
    public Optional<AGraphEdge> findEdge(EdgeId edgeId) {
        InternalEdge cachedE = edgeMap.get(edgeId);
        if (cachedE != null && cachedE.isLoaded()) {
            logger.debug("Found edge in transaction cache: {}", edgeId);
            return Optional.of(cachedE);
        }
        Iterator<InternalEdge> itE = storageEngine.edges(Collections.singleton(edgeId));
        if (itE.hasNext()) {
            logger.debug("Found edge in backend: {}", edgeId);
            InternalEdge other = itE.next();
            edgeMap.put(edgeId, other);
            return Optional.of(other);
        }
        return Optional.empty();
    }

    @Override
    public Iterator<AGraphEdge> edges(Iterable<EdgeId> edgeIds) {
        Iterable<InternalEdge> cachedEdges;
        if (Iterables.isEmpty(edgeIds)) {
            cachedEdges = edgeMap.values();
        } else {
            cachedEdges = StreamSupports.stream(edgeIds)
                    .map(edgeMap::get)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        }
        return Iterators.concat(
                cachedEdges.iterator(),
                LazyIterator.of(() -> storageEngine.edges(edgeIds))
        );
    }

    @Override
    public Iterator<AGraphEdge> edges(AGraphVertex ownVertex,
                                      Direction direction, String... edgeLabels) {
        Iterable<InternalEdge> cachedEdges = edgeMap.values().stream()
                .filter(e -> e.outVertex().id().equals(ownVertex.id()))
                .collect(Collectors.toList());
        return Iterators.concat(
                cachedEdges.iterator(),
                LazyIterator.of(() -> storageEngine.edges(ownVertex.id(), direction, edgeLabels))
        );
    }

    @Override
    public Iterator<AGraphVertex> vertices(AGraphVertex ownVertex,
                                           Direction direction, String... edgeLabels) {
        return Iterators.transform(
                this.edges(ownVertex, direction, edgeLabels),
                AGraphEdge::inVertex
        );
    }

    @Override
    public AGraphEdge addEdge(AGraphEdge aEdge) {
        assert aEdge instanceof InternalEdge;
        InternalEdge edge = (InternalEdge) aEdge;

        InternalEdge cachedE = edgeMap.get(edge.id());
        if (cachedE != null) {
            if (cachedE.isNew()) {
                logger.warn("Edge is exists. Add duplicate edge will replace old one");
            }
            if (!cachedE.isRemoved()) {
                logger.debug("Merge properties for edge {}", cachedE);
                cachedE.copyProperties(edge);
            }
        }
        this.hasModifications = true;
        return edgeMap.put(edge.id(), edge);
    }

    @Override
    public void removeEdge(AGraphEdge aEdge) {
        this.hasModifications = true;
        InternalEdge edge = edgeMap.remove(aEdge.id());
        if (edge != null) {
            if (!edge.isNew()) {
                this.rmEdges.add(edge);
            }
            logger.debug("Edge has been removed {}", edge);
        }
    }

    @Override
    public void open() {
        if (isOpen()) {
            throw Transaction.Exceptions.transactionAlreadyOpen();
        } else {
            doOpen();
        }
    }

    @Override
    public void commit() {
        readWrite();
        doCommit();
        fireOnCommit();
    }

    @Override
    public void rollback() {
        readWrite();
        doRollback();
        fireOnRollback();
    }

    @Override
    public <G extends Graph> G createThreadedTx() {
        throw Transaction.Exceptions.threadedTransactionsNotSupported();
    }

    @Override
    public void readWrite() {
    }

    @Override
    public void close() {
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

    @Override
    public boolean isClosed() {
        return false;
    }

    /**
     * Called within {@link #open} if it is determined that the transaction is not yet open given {@link #isOpen}.
     * Implementers should assume the transaction is not yet started and should thus open one.
     */
    protected void doOpen() {

    }

    /**
     * Called with {@link #commit} after the {@link #onReadWrite(Consumer)} has been notified.  Implementers should
     * include their commit logic here.
     */
    protected void doCommit() {

    }

    /**
     * Called with {@link #rollback} after the {@link #onReadWrite(Consumer)} has been notified.  Implementers should
     * include their rollback logic here.
     */
    protected void doRollback() {

    }

    /**
     * Called within {@link #commit()} just after the internal call to {@link #doCommit()}. Implementations of this
     * method should raise {@link org.apache.tinkerpop.gremlin.structure.Transaction.Status#COMMIT} events to any
     * listeners added via {@link #addTransactionListener(Consumer)}.
     */
    protected void fireOnCommit() {

    }

    /**
     * Called within {@link #rollback()} just after the internal call to {@link #doRollback()} ()}. Implementations
     * of this method should raise {@link org.apache.tinkerpop.gremlin.structure.Transaction.Status#ROLLBACK} events
     * to any listeners added via {@link #addTransactionListener(Consumer)}.
     */
    protected void fireOnRollback() {

    }

    private void reset() {
        // Reset state, generate new tx ID
        this.txId = this.graph.idPool().generate();
        this.hasModifications = false;
        this.state = TxState.BEGIN;

        // Clear mutations
        this.vertexMap.clear();
        this.edgeMap.clear();
        this.rmVertices.clear();
        this.rmEdges.clear();
    }

    private void doReadWrite() {
        Iterable<InternalVertex> modifiedVertices = vertexMap.values().stream()
                .filter(v -> v.isModified() || v.isNew())
                .collect(Collectors.toList());

        Iterable<InternalEdge> modifiedEdges = edgeMap.values().stream()
                .filter(e -> e.isModified() || e.isNew())
                .collect(Collectors.toList());

        this.storageEngine.addVertexModifications(modifiedVertices);
        this.storageEngine.addEdgeModifications(modifiedEdges);

        this.storageEngine.addVertexRemovals(rmVertices);
        this.storageEngine.addEdgeRemovals(rmEdges);
    }
}
