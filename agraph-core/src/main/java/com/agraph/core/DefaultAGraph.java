package com.agraph.core;

import com.agraph.AGraph;
import com.agraph.AGraphEdge;
import com.agraph.AGraphTransaction;
import com.agraph.AGraphVertex;
import com.agraph.config.Config;
import com.agraph.config.ConfigUtils;
import com.agraph.core.idpool.IdPool;
import com.agraph.core.idpool.SequenceIdPool;
import com.agraph.core.serialize.Serializer;
import com.agraph.core.tx.TransactionBuilder;
import com.agraph.core.type.EdgeId;
import com.agraph.core.type.VertexId;
import com.agraph.storage.StorageBackend;
import com.google.common.collect.Iterators;
import lombok.experimental.Accessors;
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
import java.util.stream.Collectors;

@Accessors(fluent = true)
public class DefaultAGraph implements AGraph {

    private static final Logger logger = LoggerFactory.getLogger(DefaultAGraph.class);

    private final Config conf;
    private final AGraphOptions options;
    private final Serializer serializer;
    private final StorageBackend backend;

    private final ThreadLocal<AGraphTransaction> threadLocalTx = ThreadLocal.withInitial(() -> null);
    private final Map<Long, AGraphTransaction> txs = new HashMap<>();

    public DefaultAGraph(Configuration apacheConf) {
        this(ConfigUtils.fromApacheConfiguration(apacheConf));
    }

    public DefaultAGraph(Config conf) {
        logger.info("Initializing graph...");
        logger.debug("AGraph configuration:\n" + conf);

        this.conf = conf;
        this.options = new AGraphOptions(this.conf);
        this.serializer = this.options.serializer();

        logger.info("Opening backend '{}' for graph '{}'", options.backend(), options.name());
        this.backend = this.options.backendFactory().open(this);
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
        ElementHelper.legalPropertyKeyValueArray(keyValues);

        String label = ElementHelper.getLabelValue(keyValues).orElse(Vertex.DEFAULT_LABEL);
        Object id = ElementHelper.getIdValue(keyValues).orElse(this.idPool().generate());
        VertexId vId = new VertexId(id, label);

        InternalVertex vertex = ElementBuilders.vertexBuilder()
                .graph(this).id(vId).label(label)
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
        return new SequenceIdPool();
    }

    @Override
    public ExecutorService ioThreadPool() {
        return ForkJoinPool.commonPool();
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
