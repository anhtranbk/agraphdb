package com.agraph.core;

import com.agraph.AGraph;
import com.agraph.AGraphEdge;
import com.agraph.AGraphException;
import com.agraph.AGraphTransaction;
import com.agraph.AGraphVertex;
import com.agraph.common.util.Threads;
import com.agraph.config.Config;
import com.agraph.config.ConfigUtils;
import com.agraph.core.idpool.IdPool;
import com.agraph.core.idpool.SequenceIdPool;
import com.agraph.core.serialize.Serializer;
import com.agraph.core.type.EdgeId;
import com.agraph.core.type.VertexId;
import com.agraph.storage.StorageBackend;
import com.google.common.collect.Iterators;
import org.apache.commons.configuration.Configuration;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.structure.VertexProperty;
import org.apache.tinkerpop.gremlin.structure.util.ElementHelper;
import org.apache.tinkerpop.gremlin.structure.util.StringFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class DefaultAGraph implements AGraph {

    private static final Logger logger = LoggerFactory.getLogger(DefaultAGraph.class);

    private final Config conf;
    private final AGraphOptions options;
    private final Serializer serializer;
    private final StorageBackend backend;

    private final ThreadLocal<AGraphTransaction> threadLocalTx = ThreadLocal.withInitial(() -> null);
    private final Map<Long, AGraphTransaction> txs = new HashMap<>();
    private final AtomicBoolean closed = new AtomicBoolean();

    private final ExecutorService ioThreadPool;
    private final IdPool idPool;

    public DefaultAGraph(Configuration apacheConf) {
        this(ConfigUtils.fromApacheConfiguration(apacheConf));
    }

    public DefaultAGraph(Config conf) {
        logger.info("Initializing graph...");
        logger.debug("AGraph configuration:\n" + conf);

        this.conf = conf;
        this.options = new AGraphOptions(this.conf);
        this.serializer = this.options.serializer();
        this.ioThreadPool = ForkJoinPool.commonPool();
        this.idPool = new SequenceIdPool();

        logger.info("Opening backend '{}' for graph '{}'", options.backend(), options.name());
        this.backend = this.options.backendFactory().open(this);

        this.closed.set(false);
    }

    @Override
    public String name() {
        return this.options.name();
    }

    @Override
    public StorageBackend backend() {
        return this.backend;
    }

    @Override
    public Serializer serializer() {
        return this.serializer;
    }

    @Override
    public AGraphTransaction tx() {
        return getAutoStartTx();
    }

    @Override
    public void close() {
        logger.info("Closing all active tx. There are {} current active txs", txs.size());
        for (Map.Entry<Long, AGraphTransaction> entry : txs.entrySet()) {
            AGraphTransaction tx = entry.getValue();
            if (!tx.isOpen()) continue;
            try {
                logger.debug("Close tx {} associate with thread {}", tx.txId(), entry.getKey());
                tx.close();
            } catch (RuntimeException e) {
                logger.error("Unable to close transaction {}", tx, e);
            }
        }

        logger.info("Waiting all IO tasks completed...");
        try {
            Threads.stopThreadPool(this.ioThreadPool, 5, TimeUnit.MINUTES);
        } catch (Throwable t) {
            logger.error("Could not shutdown IO thread pool", t);
        }

        logger.info("Close and clean backend resources");
        this.backend.close();

        logger.info("Graph {} has been closed", this.options.name());
        this.closed.set(true);
    }

    @Override
    public Variables variables() {
        return new AGraphVariables(this);
    }

    @Override
    public Features features() {
        return new AGraphFeatures(this);
    }

    @Override
    public Configuration configuration() {
        return ConfigUtils.toApacheConfiguration(this.conf);
    }

    @Override
    public Config config() {
        return this.conf;
    }

    @Override
    public AGraphTransaction newTransaction() {
        return null;
    }

    @Override
    public boolean isOpen() {
        return !this.closed.get();
    }

    @Override
    public boolean isClosed() {
        return this.closed.get();
    }

    @Override
    public AGraphVertex addVertex(Object... keyValues) {
        ElementHelper.legalPropertyKeyValueArray(keyValues);

        String label = ElementHelper.getLabelValue(keyValues).orElse(Vertex.DEFAULT_LABEL);
        Object id = ElementHelper.getIdValue(keyValues).orElse(this.idPool().generate());
        VertexId vId = new VertexId(id, label);

        InternalVertex vertex = ElementBuilders.vertexBuilder()
                .tx(this.tx()).id(vId).label(label)
                .build();
        ElementHelper.attachProperties(vertex, VertexProperty.Cardinality.single, keyValues);

        return this.tx().addVertex(vertex);
    }

    @Override
    public Iterator<Vertex> vertices(Object... ids) {
        ElementHelper.validateMixedElementIds(AGraphVertex.class, ids);
        Iterable<VertexId> vIds = Arrays.stream(ids)
                .map(DefaultAGraph::validateAndGetVertexId)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        return Iterators.transform(this.tx().vertices(vIds), v -> v);
    }

    @Override
    public Iterator<Edge> edges(Object... ids) {
        ElementHelper.validateMixedElementIds(AGraphEdge.class, ids);
        Iterable<EdgeId> eIds = Arrays.stream(ids)
                .map(DefaultAGraph::validateAndGetEdgeId)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        return Iterators.transform(this.tx().edges(eIds), e -> e);
    }

    @Override
    public String toString() {
        return StringFactory.graphString(this, this.name());
    }

    private AGraphTransaction getAutoStartTx() {
        throw new UnsupportedOperationException();
    }

    @Override
    public IdPool idPool() {
        return this.idPool;
    }

    @Override
    public ExecutorService ioThreadPool() {
        return this.ioThreadPool;
    }

    private void verifyOpened() {
        if (!this.isOpen()) {
            throw new AGraphException("Transaction has not been opened");
        }
    }

    private static VertexId validateAndGetVertexId(Object rawId) {
        Class<?> cls = rawId.getClass();
        if (cls.isAssignableFrom(AGraphVertex.class)) {
            return ((AGraphVertex) rawId).id();
        } else if (cls.isAssignableFrom(VertexId.class)) {
            return (VertexId) rawId;
        } else if (rawId instanceof String) {
            return VertexId.fromString(rawId.toString());
        } else return null;
    }

    private static EdgeId validateAndGetEdgeId(Object rawId) {
        Class<?> cls = rawId.getClass();
        if (cls.isAssignableFrom(AGraphVertex.class)) {
            return ((AGraphEdge) rawId).id();
        } else if (cls.isAssignableFrom(VertexId.class)) {
            return (EdgeId) rawId;
        } else if (rawId instanceof String) {
            return EdgeId.fromString(rawId.toString());
        } else return null;
    }
}
