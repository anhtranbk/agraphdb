package com.agraph.storage.rdbms.engine;

import com.agraph.AGraph;
import com.agraph.AGraphEdge;
import com.agraph.AGraphVertex;
import com.agraph.config.Config;
import com.agraph.core.EdgeId;
import com.agraph.core.VertexId;
import com.agraph.storage.StorageException;
import com.agraph.storage.StorageFeatures;
import com.agraph.storage.StructureOptions;
import com.agraph.storage.rdbms.Column;
import com.agraph.storage.rdbms.RdbmsStorageBackend;
import com.agraph.storage.rdbms.RdbmsStorageEngine;
import com.agraph.storage.rdbms.query.Condition;
import com.agraph.storage.rdbms.query.Conditions;
import com.agraph.storage.rdbms.query.Query;
import com.google.common.collect.Iterators;
import com.google.common.util.concurrent.Futures;
import org.apache.tinkerpop.gremlin.structure.Direction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Future;

import static com.agraph.storage.rdbms.engine.Constants.*;

public class KataStorageEngine extends RdbmsStorageEngine {

    private static final Logger logger = LoggerFactory.getLogger(KataStorageEngine.class);

    private final Config conf;
    private final StorageFeatures features;
    private final StructureOptions structureOps;

    public KataStorageEngine(AGraph graph, RdbmsStorageBackend backend) {
        super(graph, backend);
        this.conf = graph.getConfig();
        this.structureOps = new StructureOptions(conf);
        this.features = backend.getFeatures();
    }

    @Override
    public Future<?> initialize() {
        try {
            if (!backend.isTableExists(VERTEX_TABLE)) {
                logger.info("VERTEX_TABLE is not exists. Creating new");
                initVertexTable();
            }
            if (!backend.isTableExists(VERTEX_LARGE_PROPS_TABLE)) {
                logger.info("VERTEX_LARGE_PROPS_TABLE is not exists. Creating new");
                initVertexLargePropsTable();
            }
            if (!backend.isTableExists(EDGE_TABLE)) {
                logger.info("EDGE_TABLE is not exists. Creating new");
                initEdgeTable();
            }
            if (!backend.isTableExists(EDGE_PROPS_TABLE)) {
                logger.info("EDGE_PROPERTIES_TABLE is not exists. Creating new");
                initEdgePropsTable();
            }
            if (!backend.isTableExists(SYSTEM_TABLE)) {
                logger.info("SYSTEM_TABLE is not exists. Creating vertex new");
                initSystemTable();
            }
            return Futures.immediateFuture(0);
        } catch (StorageException e) {
            return Futures.immediateFailedFuture(e);
        }
    }

    private void initVertexTable() {
        List<Column> cols = Arrays.asList(
                Column.builder(ID_COL, Column.DataType.BYTE_ARRAY)
                        .length(structureOps.getMaxIdLength())
                        .allowNull(false)
                        .build(),
                Column.builder(LABEL_COL, Column.DataType.STRING)
                        .length(structureOps.getMaxLabelLength())
                        .allowNull(false)
                        .build(),
                Column.builder(KEY_COL, Column.DataType.STRING)
                        .length(structureOps.getMaxPropertyNameLength())
                        .allowNull(false)
                        .build(),
                Column.builder(VALUE_COL, Column.DataType.BYTE_ARRAY).length(255).build()
        );

        try {
            backend.createTable(VERTEX_TABLE, cols, ID_COL, LABEL_COL);
        } catch (StorageException e) {
            logger.error("Create vertex table failed", e);
            throw e;
        }
    }

    private void initVertexLargePropsTable() {
        List<Column> cols = Arrays.asList(
                Column.builder(ID_COL, Column.DataType.LONG).allowNull(false).build(),
                Column.builder(KEY_COL, Column.DataType.STRING)
                        .length(structureOps.getMaxPropertyNameLength())
                        .allowNull(false)
                        .build(),
                Column.builder(VALUE_COL, Column.DataType.BYTE_ARRAY)
                        .length(structureOps.getMaxPropertyValueLength())
                        .allowNull(false)
                        .build()
        );

        try {
            backend.createTable(VERTEX_LARGE_PROPS_TABLE, cols, ID_COL);
        } catch (StorageException e) {
            logger.error("Create vertex large properties table failed", e);
            throw e;
        }
    }

    private void initEdgeTable() {
        List<Column> cols = Arrays.asList(
                Column.builder(VERTEX_SRC_COL, Column.DataType.BYTE_ARRAY)
                        .length(structureOps.getMaxIdLength())
                        .allowNull(false)
                        .build(),
                Column.builder(VERTEX_DST_COL, Column.DataType.BYTE_ARRAY)
                        .length(structureOps.getMaxIdLength())
                        .allowNull(false)
                        .build(),
                Column.builder(VERTEX_SRC_LABEL_COL, Column.DataType.STRING)
                        .length(structureOps.getMaxLabelLength())
                        .allowNull(false)
                        .build(),
                Column.builder(VERTEX_DST_LABEL_COL, Column.DataType.STRING)
                        .length(structureOps.getMaxLabelLength())
                        .allowNull(false)
                        .build(),
                Column.builder(LABEL_COL, Column.DataType.BYTE_ARRAY)
                        .length(structureOps.getMaxLabelLength())
                        .allowNull(false)
                        .build(),
                Column.builder(REF_ID_COL, Column.DataType.LONG)
                        .allowNull(false)
                        .autoIncrement(true)
                        .build()
        );

        try {
            backend.createTable(EDGE_TABLE, cols,
                    VERTEX_SRC_COL, VERTEX_DST_COL,
                    VERTEX_SRC_LABEL_COL, VERTEX_DST_LABEL_COL, LABEL_COL);
        } catch (StorageException e) {
            logger.error("Create edge table failed", e);
            throw e;
        }
    }

    private void initEdgePropsTable() {
        List<Column> cols = Arrays.asList(
                Column.builder(ID_COL, Column.DataType.LONG).allowNull(false).build(),
                Column.builder(KEY_COL, Column.DataType.STRING)
                        .length(structureOps.getMaxPropertyNameLength())
                        .allowNull(false)
                        .build(),
                Column.builder(VALUE_COL, Column.DataType.BYTE_ARRAY)
                        .length(structureOps.getMaxPropertyNameLength())
                        .allowNull(false)
                        .build()
        );

        try {
            backend.createTable(EDGE_PROPS_TABLE, cols, ID_COL);
        } catch (StorageException e) {
            logger.error("Create edge properties table failed", e);
            throw e;
        }
    }

    private void initSystemTable() {
        List<Column> cols = Arrays.asList(
                Column.builder(KEY_COL, Column.DataType.STRING)
                        .length(64)
                        .allowNull(false)
                        .build(),
                Column.builder(VALUE_COL, Column.DataType.BYTE_ARRAY)
                        .length(128)
                        .allowNull(false)
                        .build()
        );

        try {
            backend.createTable(EDGE_PROPS_TABLE, cols, KEY_COL);
        } catch (StorageException e) {
            logger.error("Create system table failed", e);
            throw e;
        }
    }

    private void createIndices() {

    }

    @Override
    public Iterator<AGraphVertex> vertices(String... labels) {
        Query query = Query.builder(VERTEX_TABLE)
                .addColumn()
                .condition(Conditions.in(LABEL_COL, Arrays.asList(labels)))
                .build();
        return Iterators.transform(backend.query(query), ElementFactory::createVertex);
    }

    @Override
    public Iterator<AGraphVertex> vertices(Iterable<VertexId> vertexIds, String... labels) {
        Query query = Query.builder(VERTEX_TABLE)
                .addColumn()
                .condition(Conditions.and(
                        Conditions.in(ID_COL, vertexIds),
                        Conditions.in(LABEL_COL, Arrays.asList(labels))
                ))
                .build();
        return Iterators.transform(backend.query(query), ElementFactory::createVertex);
    }

    @Override
    public Future<?> mutateVertices(Iterable<AGraphVertex> vertices) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Future<?> deleteVertices(Iterable<VertexId> vertexIds, String... labels) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterator<AGraphEdge> edges(Iterable<EdgeId> edgeIds) {
        return null;
    }

    @Override
    public Iterator<AGraphEdge> edges(VertexId vertexId, Direction direction, String... labels) {
        Condition condition;
        if (direction.equals(Direction.BOTH)) {
            Condition vertexCondition = Conditions.or(
                    Conditions.eq(VERTEX_SRC_COL, vertexId),
                    Conditions.eq(VERTEX_DST_COL, vertexId));
            condition = Conditions.and(
                    vertexCondition,
                    Conditions.in(LABEL_COL, Arrays.asList(labels)));
        } else {
            String targetCol = direction.equals(Direction.IN) ? VERTEX_DST_COL : VERTEX_SRC_COL;
            condition = Conditions.and(
                    Conditions.eq(targetCol, vertexId),
                    Conditions.in(LABEL_COL, Arrays.asList(labels)));
        }
        Query query = Query.builder(VERTEX_TABLE)
                .addColumn()
                .condition(condition)
                .build();
        return Iterators.transform(backend.query(query), ElementFactory::createEdge);
    }

    @Override
    public Iterator<AGraphEdge> edges(VertexId ownVertexId, Direction direction,
                                      Iterable<VertexId> otherVertexIds, String... labels) {
        Condition directInCondition = Conditions.and(
                Conditions.eq(VERTEX_DST_COL, ownVertexId),
                Conditions.in(VERTEX_SRC_COL, otherVertexIds));

        Condition directionOutCondition = Conditions.and(
                Conditions.eq(VERTEX_SRC_COL, ownVertexId),
                Conditions.in(VERTEX_DST_COL, otherVertexIds));

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
                .addColumn()
                .condition(finalCondition)
                .build();
        return Iterators.transform(backend.query(query), ElementFactory::createEdge);
    }

    @Override
    public Future<?> mutateEdges(Iterable<AGraphEdge> edges) {
        return null;
    }

    @Override
    public Future<?> deleteEdges(Iterable<EdgeId> edgeIds) {
        return null;
    }
}
