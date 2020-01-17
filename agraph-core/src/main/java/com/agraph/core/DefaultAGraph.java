package com.agraph.core;

import com.agraph.AGraph;
import com.agraph.AGraphEdge;
import com.agraph.AGraphTransaction;
import com.agraph.AGraphVertex;
import com.agraph.config.Config;
import com.agraph.config.ConfigUtils;
import com.agraph.core.serialize.DefaultSerializer;
import com.agraph.core.serialize.Serializer;
import com.agraph.core.tx.TransactionBuilder;
import com.agraph.core.type.EdgeId;
import com.agraph.core.type.VertexId;
import com.agraph.core.type.IdGenerator;
import com.google.common.collect.Iterators;
import io.reactivex.Observable;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.apache.commons.configuration.Configuration;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.structure.VertexProperty;
import org.apache.tinkerpop.gremlin.structure.util.ElementHelper;
import org.apache.tinkerpop.gremlin.structure.util.StringFactory;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

@Accessors(fluent = true)
public class DefaultAGraph implements AGraph {

    private final Config conf;
    @Getter
    private final AGraphOptions aGraphOps;
    @Getter
    private final TinkerOptions tinkerOps;
    @Getter
    private final Serializer serializer;

    private final ThreadLocal<AGraphTransaction> threadLocalTx = ThreadLocal.withInitial(() -> null);
    private final Map<Long, AGraphTransaction> txs = new HashMap<>();

    public DefaultAGraph(Configuration apacheConf) {
        this(ConfigUtils.fromApacheConfiguration(apacheConf));
    }

    public DefaultAGraph(Config aGraphConf) {
        this.conf = aGraphConf;
        this.aGraphOps = new AGraphOptions(this.conf);
        this.tinkerOps = new TinkerOptions(this.conf);
        this.serializer = new DefaultSerializer();
    }

    @Override
    public String name() {
        return this.tinkerOps.name();
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
        Object id = ElementHelper.getIdValue(keyValues).orElse(this.getIdGenerator().generate());
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
        Iterable<VertexId> vIds = Observable.fromArray(ids)
                .map(DefaultAGraph::validateAndGetVertexId)
                .filter(Objects::nonNull)
                .blockingIterable();
        return Iterators.transform(this.tx().vertices(vIds), v -> v);
    }

    @Override
    public Iterator<Edge> edges(Object... ids) {
        ElementHelper.validateMixedElementIds(AGraphEdge.class, ids);
        Iterable<EdgeId> eIds = Observable.fromArray(ids)
                .map(DefaultAGraph::validateAndGetEdgeId)
                .filter(Objects::nonNull)
                .blockingIterable();
        return Iterators.transform(this.tx().edges(eIds), e -> e);
    }

    @Override
    public String toString() {
        return StringFactory.graphString(this, this.name());
    }

    private AGraphTransaction getAutoStartTx() {
        throw new UnsupportedOperationException();
    }

    public IdGenerator getIdGenerator() {
        throw new UnsupportedOperationException();
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
