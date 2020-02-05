package com.agraph.storage.mlae;

import com.agraph.AGraph;
import com.agraph.core.DefaultGraph;
import com.agraph.core.InternalEdge;
import com.agraph.core.InternalVertex;
import com.agraph.core.type.EdgeId;
import com.agraph.core.type.VertexId;
import com.agraph.storage.Mutation;
import com.agraph.storage.MutationBuilder;
import com.agraph.storage.StorageBackend;
import com.agraph.storage.StorageEngine;
import com.agraph.storage.StorageException;
import com.agraph.storage.StorageFeatures;
import com.agraph.storage.StructureOptions;
import com.agraph.storage.backend.BackendSession;
import com.agraph.storage.rdbms.query.Condition;
import com.agraph.storage.rdbms.query.Conditions;
import com.agraph.storage.rdbms.query.Query;
import com.agraph.storage.rdbms.schema.Column;
import com.agraph.storage.rdbms.schema.DBType;
import com.agraph.storage.rdbms.schema.TableDefine;
import com.google.common.collect.Iterators;
import com.google.common.util.concurrent.Futures;
import org.apache.tinkerpop.gremlin.structure.Direction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static com.agraph.storage.mlae.Constants.EDGE_PROPS_TABLE;
import static com.agraph.storage.mlae.Constants.EDGE_TABLE;
import static com.agraph.storage.mlae.Constants.ID_COL;
import static com.agraph.storage.mlae.Constants.KEY_COL;
import static com.agraph.storage.mlae.Constants.LABEL_COL;
import static com.agraph.storage.mlae.Constants.REF_ID_COL;
import static com.agraph.storage.mlae.Constants.SYSTEM_TABLE;
import static com.agraph.storage.mlae.Constants.VALUE_COL;
import static com.agraph.storage.mlae.Constants.VERTEX_DST_ID_COL;
import static com.agraph.storage.mlae.Constants.VERTEX_DST_LABEL_COL;
import static com.agraph.storage.mlae.Constants.VERTEX_LARGE_PROPS_TABLE;
import static com.agraph.storage.mlae.Constants.VERTEX_SRC_ID_COL;
import static com.agraph.storage.mlae.Constants.VERTEX_SRC_LABEL_COL;
import static com.agraph.storage.mlae.Constants.VERTEX_TABLE;

public class VuDaiStorageEngine implements StorageEngine {

    private static final Logger logger = LoggerFactory.getLogger(VuDaiStorageEngine.class);

    private final StorageBackend backend;
    private final AGraph graph;

    private final StorageFeatures features;
    private final StructureOptions structureOps;
    private final MutationBuilder muttBuilder;
    private final List<Mutation> mutations = new ArrayList<>();

    private boolean initialized;

    public VuDaiStorageEngine(DefaultGraph graph, StorageBackend backend) {
        this.graph = graph;
        this.backend = backend;
        this.structureOps = new StructureOptions(graph.config());
        this.features = backend.features();
        this.muttBuilder = new Mutations(graph);
    }

    @Override
    public AGraph graph() {
        return graph;
    }

    @Override
    public StorageBackend backend() {
        return backend;
    }

    @Override
    public void initialize() {
        if (initialized) {
            logger.warn("Storage has already been initialized");
            return;
        }

        if (!Futures.getUnchecked(backend.session().isTableExists(VERTEX_TABLE))) {
            logger.info("VERTEX_TABLE is not exist");
            initVertexTable();
            logger.info("Create VERTEX_TABLE successfully");
        }

        if (!Futures.getUnchecked(backend.session().isTableExists(VERTEX_LARGE_PROPS_TABLE))) {
            logger.info("VERTEX_LARGE_PROPS_TABLE is not exist");
            initVertexLargePropsTable();
            logger.info("Create VERTEX_LARGE_PROPS_TABLE successfully");
        }

        if (!Futures.getUnchecked(backend.session().isTableExists(EDGE_TABLE))) {
            logger.info("EDGE_TABLE is not exist");
            initEdgeTable();
            logger.info("Create EDGE_TABLE successfully");
        }

        if (!Futures.getUnchecked(backend.session().isTableExists(EDGE_PROPS_TABLE))) {
            logger.info("EDGE_PROPERTIES_TABLE is not exist");
            initEdgePropsTable();
            logger.info("Create EDGE_PROPERTIES_TABLE successfully");
        }

        if (!Futures.getUnchecked(backend.session().isTableExists(SYSTEM_TABLE))) {
            logger.info("SYSTEM_TABLE is not exist");
            initSystemTable();
            logger.info("Create SYSTEM_TABLE successfully");
        }
    }

    @Override
    public boolean initialized() {
        return this.initialized;
    }

    private void initVertexTable() {
        TableDefine define = TableDefine.create(VERTEX_TABLE).columns(
                Column.builder(ID_COL, DBType.VARBINARY)
                        .length(structureOps.getMaxIdLength())
                        .allowNull(false)
                        .build(),
                Column.builder(LABEL_COL, DBType.VARCHAR)
                        .length(structureOps.getMaxLabelLength())
                        .allowNull(false)
                        .build(),
                Column.builder(KEY_COL, DBType.VARCHAR)
                        .length(structureOps.getMaxPropertyNameLength())
                        .allowNull(false)
                        .build(),
                Column.builder(VALUE_COL, DBType.VARBINARY).length(255).build()
        );
        define.keys(ID_COL, LABEL_COL, KEY_COL);

        try {
            backend.session().createTable(define).get();
        } catch (Throwable t) {
            throw new StorageException(t);
        }
    }

    private void initVertexLargePropsTable() {
        TableDefine define = TableDefine.create(VERTEX_LARGE_PROPS_TABLE).columns(
                Column.builder(ID_COL, DBType.BIGINT).allowNull(false).build(),
                Column.builder(KEY_COL, DBType.VARCHAR)
                        .length(structureOps.getMaxPropertyNameLength())
                        .allowNull(false)
                        .build(),
                Column.builder(VALUE_COL, DBType.VARBINARY)
                        .length(structureOps.getMaxPropertyValueLength())
                        .allowNull(false)
                        .build()
        );
        define.keys(ID_COL, KEY_COL);

        try {
            backend.session().createTable(define).get();
        } catch (Throwable t) {
            throw new StorageException(t);
        }
    }

    private void initEdgeTable() {
        TableDefine define = TableDefine.create(EDGE_TABLE).columns(
                Column.builder(VERTEX_SRC_ID_COL, DBType.VARBINARY)
                        .length(structureOps.getMaxIdLength())
                        .allowNull(false)
                        .build(),
                Column.builder(VERTEX_DST_ID_COL, DBType.VARBINARY)
                        .length(structureOps.getMaxIdLength())
                        .allowNull(false)
                        .build(),
                Column.builder(VERTEX_SRC_LABEL_COL, DBType.VARCHAR)
                        .length(structureOps.getMaxLabelLength())
                        .allowNull(false)
                        .build(),
                Column.builder(VERTEX_DST_LABEL_COL, DBType.VARCHAR)
                        .length(structureOps.getMaxLabelLength())
                        .allowNull(false)
                        .build(),
                Column.builder(LABEL_COL, DBType.VARBINARY)
                        .length(structureOps.getMaxLabelLength())
                        .allowNull(false)
                        .build(),
                Column.builder(REF_ID_COL, DBType.BIGINT)
                        .allowNull(false)
                        .autoIncrement(true)
                        .build()
        );
        define.keys(VERTEX_SRC_ID_COL, VERTEX_DST_ID_COL,
                VERTEX_SRC_LABEL_COL, VERTEX_DST_LABEL_COL,
                LABEL_COL);

        try {
            backend.session().createTable(define).get();
        } catch (Throwable t) {
            throw new StorageException(t);
        }
    }

    private void initEdgePropsTable() {
        TableDefine define = TableDefine.create(EDGE_PROPS_TABLE).columns(
                Column.builder(ID_COL, DBType.BIGINT)
                        .allowNull(false).build(),
                Column.builder(KEY_COL, DBType.VARCHAR)
                        .length(structureOps.getMaxPropertyNameLength())
                        .allowNull(false)
                        .build(),
                Column.builder(VALUE_COL, DBType.VARBINARY)
                        .length(structureOps.getMaxPropertyNameLength())
                        .allowNull(false)
                        .build());
        define.keys(ID_COL, KEY_COL);

        try {
            backend.session().createTable(define).get();
        } catch (Throwable t) {
            throw new StorageException(t);
        }
    }

    private void initSystemTable() {
        TableDefine define = TableDefine.create(SYSTEM_TABLE).columns(
                Column.builder(KEY_COL, DBType.VARCHAR)
                        .length(64)
                        .allowNull(false)
                        .build(),
                Column.builder(VALUE_COL, DBType.VARBINARY)
                        .length(128)
                        .allowNull(false)
                        .build()
        ).keys(KEY_COL);

        try {
            backend.session().createTable(define).get();
        } catch (Throwable t) {
            throw new StorageException(t);
        }
    }

    @Override
    public Iterator<InternalVertex> vertices(String... labels) {
        Query query = Query.builder(VERTEX_TABLE)
                .addColumns()
                .condition(Conditions.in(LABEL_COL, Arrays.asList(labels)))
                .build();
        return Iterators.transform(backend.session().query(query), ElementFactory::createVertex);
    }

    @Override
    public Iterator<InternalVertex> vertices(Iterable<VertexId> vertexIds, String... labels) {
        Query query = Query.builder(VERTEX_TABLE)
                .addColumns()
                .condition(Conditions.and(
                        Conditions.in(ID_COL, vertexIds),
                        Conditions.in(LABEL_COL, Arrays.asList(labels))
                ))
                .build();
        return Iterators.transform(backend.session().query(query), ElementFactory::createVertex);
    }

    @Override
    public Iterator<InternalEdge> edges(Iterable<EdgeId> edgeIds) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterator<InternalEdge> edges(VertexId ownVertexId, Direction direction, String... labels) {
        Condition condition;
        if (direction.equals(Direction.BOTH)) {
            Condition vertexCondition = Conditions.or(
                    Conditions.eq(VERTEX_SRC_ID_COL, ownVertexId),
                    Conditions.eq(VERTEX_DST_ID_COL, ownVertexId));
            condition = Conditions.and(
                    vertexCondition,
                    Conditions.in(LABEL_COL, Arrays.asList(labels)));
        } else {
            String targetCol = direction.equals(Direction.IN) ? VERTEX_DST_ID_COL : VERTEX_SRC_ID_COL;
            condition = Conditions.and(
                    Conditions.eq(targetCol, ownVertexId),
                    Conditions.in(LABEL_COL, Arrays.asList(labels)));
        }
        Query query = Query.builder(VERTEX_TABLE)
                .addColumns()
                .condition(condition)
                .build();
        return Iterators.transform(backend.session().query(query), ElementFactory::createEdge);
    }

    @Override
    public Iterator<InternalEdge> edges(VertexId ownVertexId, Direction direction,
                                        Iterable<VertexId> otherVertexIds, String... labels) {
        Condition directInCondition = Conditions.and(
                Conditions.eq(VERTEX_DST_ID_COL, ownVertexId),
                Conditions.in(VERTEX_SRC_ID_COL, otherVertexIds));

        Condition directionOutCondition = Conditions.and(
                Conditions.eq(VERTEX_SRC_ID_COL, ownVertexId),
                Conditions.in(VERTEX_DST_ID_COL, otherVertexIds));

        Condition finalCondition;
        if (direction.equals(Direction.BOTH)) {
            finalCondition = Conditions.and(
                    Conditions.or(directInCondition, directionOutCondition),
                    Conditions.in(LABEL_COL, Arrays.asList(labels))
            );
        } else {
            finalCondition = Conditions.and(
                    Direction.OUT.equals(direction) ? directionOutCondition : directInCondition,
                    Conditions.in(LABEL_COL, Arrays.asList(labels))
            );
        }
        Query query = Query.builder(VERTEX_TABLE)
                .addColumns()
                .condition(finalCondition)
                .build();
        return Iterators.transform(backend.session().query(query), ElementFactory::createEdge);
    }

    @Override
    public void addVertexModifications(Iterable<InternalVertex> vertices) {
        this.mutations.addAll(muttBuilder.fromModifiedVertices(vertices));
    }

    @Override
    public void addVertexRemovals(Iterable<InternalVertex> vertices) {
        this.mutations.addAll(muttBuilder.fromRemovedVertices(vertices));
    }

    @Override
    public void addEdgeModifications(Iterable<InternalEdge> edges) {
        this.mutations.addAll(muttBuilder.fromModifiedEdges(edges));
    }

    @Override
    public void addEdgeRemovals(Iterable<InternalEdge> edges) {
        this.mutations.addAll(muttBuilder.fromRemovedEdges(edges));
    }

    @Override
    public void beginBackendTx() {

    }

    @Override
    public void commitBackendTx() {
        try {
            logger.debug("Start writing mutations into backend with size: {}", mutations.size());
            this.backend.session().mutate(this.mutations).get();
            logger.debug("Write mutations succeeded");

            logger.debug("Committing to backend database...");
            BackendSession session = this.backend.session();
            session.tx().beforeCommit(this.mutations);
            session.tx().commit();

            logger.info("Commit to backend database succeeded with size: {}", mutations.size());
            session.tx().afterCommit();
        } catch (Throwable t) {
            logger.warn("Could not commit to backend database: " + t.getMessage());
            this.rollbackBackendTx();
        } finally {
            reset();
        }
    }

    @Override
    public void rollbackBackendTx() {
        try {
            logger.debug("Rolling back backend Tx...");
            BackendSession session = this.backend.session();
            session.tx().beforeRollback(this.mutations);
            session.tx().rollback();
            session.tx().afterRollback();
        } finally {
            reset();
        }
    }

    @Override
    public void close() {
        backend.close();
    }

    public void reset() {
        this.mutations.clear();
    }
}
