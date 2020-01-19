package com.agraph.storage.rdbms.mlae;

import com.agraph.AGraph;
import com.agraph.config.Config;
import com.agraph.core.InternalEdge;
import com.agraph.core.InternalVertex;
import com.agraph.core.type.EdgeId;
import com.agraph.core.type.VertexId;
import com.agraph.storage.Mutation;
import com.agraph.storage.MutationBuilder;
import com.agraph.storage.StorageException;
import com.agraph.storage.StorageFeatures;
import com.agraph.storage.StructureOptions;
import com.agraph.storage.rdbms.RdbmsStorageBackend;
import com.agraph.storage.rdbms.RdbmsStorageEngine;
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
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.agraph.storage.rdbms.mlae.Constants.EDGE_PROPS_TABLE;
import static com.agraph.storage.rdbms.mlae.Constants.EDGE_TABLE;
import static com.agraph.storage.rdbms.mlae.Constants.ID_COL;
import static com.agraph.storage.rdbms.mlae.Constants.KEY_COL;
import static com.agraph.storage.rdbms.mlae.Constants.LABEL_COL;
import static com.agraph.storage.rdbms.mlae.Constants.REF_ID_COL;
import static com.agraph.storage.rdbms.mlae.Constants.SYSTEM_TABLE;
import static com.agraph.storage.rdbms.mlae.Constants.VALUE_COL;
import static com.agraph.storage.rdbms.mlae.Constants.VERTEX_DST_ID_COL;
import static com.agraph.storage.rdbms.mlae.Constants.VERTEX_DST_LABEL_COL;
import static com.agraph.storage.rdbms.mlae.Constants.VERTEX_LARGE_PROPS_TABLE;
import static com.agraph.storage.rdbms.mlae.Constants.VERTEX_SRC_ID_COL;
import static com.agraph.storage.rdbms.mlae.Constants.VERTEX_SRC_LABEL_COL;
import static com.agraph.storage.rdbms.mlae.Constants.VERTEX_TABLE;

public class VuDaiStorageEngine extends RdbmsStorageEngine {

    private static final Logger logger = LoggerFactory.getLogger(VuDaiStorageEngine.class);

    private final Config conf;
    private final StorageFeatures features;
    private final StructureOptions structureOps;
    private final MutationBuilder muttBuilder;

    private final Map<String, TableDefine> tableDefines = new HashMap<>();
    private final List<Mutation> mutations = new ArrayList<>();

    public VuDaiStorageEngine(AGraph graph, RdbmsStorageBackend backend) {
        super(graph, backend);
        this.conf = graph.config();
        this.structureOps = new StructureOptions(conf);
        this.features = backend.features();
        this.muttBuilder = new Mutations(graph);
    }

    @Override
    public void open(Config conf) {
    }

    @Override
    public boolean isOpened() {
        return false;
    }

    @Override
    public void initialize() {
        if (!Futures.getUnchecked(backend.isTableExists(VERTEX_TABLE))) {
            logger.info("VERTEX_TABLE is not exist. Creating new");
            initVertexTable();
            logger.info("Create VERTEX_TABLE successfully");
        }
        if (!Futures.getUnchecked(backend.isTableExists(VERTEX_LARGE_PROPS_TABLE))) {
            logger.info("VERTEX_LARGE_PROPS_TABLE is not exist. Creating new");
            initVertexLargePropsTable();
            logger.info("Create VERTEX_LARGE_PROPS_TABLE successfully");
        }
        if (!Futures.getUnchecked(backend.isTableExists(EDGE_TABLE))) {
            logger.info("EDGE_TABLE is not exist. Creating new");
            initEdgeTable();
            logger.info("Create EDGE_TABLE successfully");
        }
        if (!Futures.getUnchecked(backend.isTableExists(EDGE_PROPS_TABLE))) {
            logger.info("EDGE_PROPERTIES_TABLE is not exist. Creating new");
            initEdgePropsTable();
            logger.info("Create EDGE_PROPERTIES_TABLE successfully");
        }
        if (!Futures.getUnchecked(backend.isTableExists(SYSTEM_TABLE))) {
            logger.info("SYSTEM_TABLE is not exist. Creating vertex new");
            initSystemTable();
            logger.info("Create SYSTEM_TABLE successfully");
        }
    }

    @Override
    public boolean isInitialized() {
        return false;
    }

    private void initVertexTable() {
        List<Column> keys = Arrays.asList(
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
                        .build());
        List<Column> cols = Collections.singletonList(
                Column.builder(VALUE_COL, DBType.VARBINARY).length(255).build());

        try {
            TableDefine td = TableDefine.create(VERTEX_TABLE, keys, cols);
            tableDefines.put(VERTEX_TABLE, td);
            backend.createTable(td).get();
        } catch (Exception e) {
            throw new StorageException(e);
        }
    }

    private void initVertexLargePropsTable() {
        List<Column> keys = Arrays.asList(
                Column.builder(ID_COL, DBType.BIGINT).allowNull(false).build(),
                Column.builder(KEY_COL, DBType.VARCHAR)
                        .length(structureOps.getMaxPropertyNameLength())
                        .allowNull(false)
                        .build());
        List<Column> cols = Collections.singletonList(
                Column.builder(VALUE_COL, DBType.VARBINARY)
                        .length(structureOps.getMaxPropertyValueLength())
                        .allowNull(false)
                        .build());
        try {
            TableDefine td = TableDefine.create(VERTEX_LARGE_PROPS_TABLE, keys, cols);
            tableDefines.put(VERTEX_LARGE_PROPS_TABLE, td);
            backend.createTable(td).get();
        } catch (Exception e) {
            throw new StorageException(e);
        }
    }

    private void initEdgeTable() {
        List<Column> keys = Arrays.asList(
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
                        .build());
        List<Column> cols = Collections.singletonList(
                Column.builder(REF_ID_COL, DBType.BIGINT)
                        .allowNull(false)
                        .autoIncrement(true)
                        .build());

        try {
            TableDefine td = TableDefine.create(EDGE_TABLE, keys, cols);
            tableDefines.put(EDGE_TABLE, td);
            backend.createTable(td).get();
        } catch (Exception e) {
            throw new StorageException(e);
        }
    }

    private void initEdgePropsTable() {
        List<Column> keys = Arrays.asList(
                Column.builder(ID_COL, DBType.BIGINT)
                        .allowNull(false).build(),
                Column.builder(KEY_COL, DBType.VARCHAR)
                        .length(structureOps.getMaxPropertyNameLength())
                        .allowNull(false)
                        .build());
        List<Column> cols = Collections.singletonList(
                Column.builder(VALUE_COL, DBType.VARBINARY)
                        .length(structureOps.getMaxPropertyNameLength())
                        .allowNull(false)
                        .build());
        try {
            TableDefine td = TableDefine.create(EDGE_PROPS_TABLE, keys, cols);
            tableDefines.put(EDGE_PROPS_TABLE, td);
            backend.createTable(td).get();
        } catch (Exception e) {
            throw new StorageException(e);
        }
    }

    private void initSystemTable() {
        List<Column> keys = Collections.singletonList(
                Column.builder(KEY_COL, DBType.VARCHAR)
                        .length(64)
                        .allowNull(false)
                        .build());
        List<Column> cols = Collections.singletonList(
                Column.builder(VALUE_COL, DBType.VARBINARY)
                        .length(128)
                        .allowNull(false)
                        .build());
        try {
            TableDefine td = TableDefine.create(EDGE_PROPS_TABLE, keys, cols);
            tableDefines.put(SYSTEM_TABLE, td);
            backend.createTable(td).get();
        } catch (Exception e) {
            throw new StorageException(e);
        }
    }

    @Override
    public Iterator<InternalVertex> vertices(String... labels) {
        Query query = Query.builder(VERTEX_TABLE)
                .addColumns()
                .condition(Conditions.in(LABEL_COL, Arrays.asList(labels)))
                .build();
        return Iterators.transform(backend.query(query), ElementFactory::createVertex);
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
        return Iterators.transform(backend.query(query), ElementFactory::createVertex);
    }

    @Override
    public Iterator<InternalEdge> edges(Iterable<EdgeId> edgeIds) {
        return null;
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
        return Iterators.transform(backend.query(query), ElementFactory::createEdge);
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
        return Iterators.transform(backend.query(query), ElementFactory::createEdge);
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
            logger.info("Start writing mutations into backend with size: {}", mutations.size());
            this.backend.mutate(this.mutations).get();
            logger.info("Write mutations succeeded");

            logger.info("Committing to backend database...");
            this.backend.backendTx().commit();
            logger.info("Commit to backend database succeeded");
        } catch (Exception e1) {
            logger.warn("Could not commit to backend database: " + e1.getMessage());
            this.rollbackBackendTx();
        } finally {
            reset();
        }
    }

    @Override
    public void rollbackBackendTx() {
        try {
            logger.info("Rolling back...");
            this.backend.backendTx().rollback();
        } catch (Exception e) {
            throw new StorageException(e);
        } finally {
            reset();
        }
    }

    public void reset() {
        this.mutations.clear();
    }
}
