package com.agraph.core.hbase;

import com.agraph.config.Config;
import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.ListenableFuture;
import com.agraph.common.concurrency.FutureAdapter;
import com.agraph.common.utils.IterableAdapter;
import com.agraph.common.utils.Strings;
import com.agraph.common.utils.Utils;
import com.agraph.v1.Direction;
import com.agraph.v1.Edge;
import com.agraph.v1.Vertex;
import com.agraph.v1.repository.EdgeRepository;
import com.agraph.storage.hbase.HBaseRuntimeException;
import com.agraph.storage.hbase.HBaseUtils;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

/**
 * TODO: Class description here.
 *
 * @author <a href="https://github.com/tjeubaoit">tjeubaoit</a>
 */
public class HEdgeRepository extends BaseRepository implements EdgeRepository {

    public HEdgeRepository(Config conf) {
        super(conf);
    }

    @Override
    public Iterable<Edge> findAll() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Edge findOne(Edge entity) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ListenableFuture<Iterable<Edge>> delete(Edge entity) {
        Future<?> fut = execute("DeleteEdge", () -> {
            try (Table table = connection.getTable(getTableName(Constants.TB_EDGE))) {
                byte[] row1 = buildFullRowKey(entity.inVertex(), entity.outVertex(),
                        entity.label(), Direction.IN);
                byte[] row2 = buildFullRowKey(entity.outVertex(), entity.inVertex(),
                        entity.label(), Direction.OUT);

                table.delete(Arrays.asList(new Delete(row1), new Delete(row2)));
            } catch (IOException e) {
                throw new HBaseRuntimeException(e);
            }
        });
        return FutureAdapter.from(fut, o -> Collections.singleton(entity));
    }

    @Override
    public ListenableFuture<Iterable<Edge>> save(Edge entity) {
        return saveAll(Collections.singleton(entity));
    }

    @Override
    public ListenableFuture<Iterable<Edge>> saveAll(Collection<Edge> entities) {
        Future<?> fut = execute("SaveEdges", () -> {
            try (Table table = connection.getTable(getTableName(Constants.TB_EDGE))) {
                List<Put> puts = new ArrayList<>(entities.size() * 2);
                for (Edge entity : entities) {
                    Put put1 = createEdgePut(entity.outVertex(), entity.inVertex(), Direction.OUT,
                            entity.label(), entity.properties());
                    Put put2 = createEdgePut(entity.inVertex(), entity.outVertex(), Direction.IN,
                            entity.label(), entity.properties());

                    puts.add(put1);
                    puts.add(put2);
                }
                table.put(puts);
            } catch (IOException e) {
                throw new HBaseRuntimeException(e);
            }
        });
        return FutureAdapter.from(fut, o -> entities);
    }

    @Override
    public Iterable<Edge> findByVertex(Vertex src, Direction direction, String label) {
        Preconditions.checkArgument(label == null || Utils.notEquals(direction, Direction.BOTH),
                "Can only query bi-direction edge if label null");

        try (Table table = connection.getTable(getTableName(Constants.TB_EDGE))) {
            byte[] rowPrefix;
            if (label == null) {
                if (direction.equals(Direction.BOTH)) {
                    rowPrefix = buildPartialRowKey(src);
                } else {
                    rowPrefix = buildPartialRowKey(src, direction);
                }
            } else {
                rowPrefix = buildPartialRowKey(src, direction, label);
            }

            Scan scan = new Scan();
            scan.setRowPrefixFilter(rowPrefix);
            ResultScanner scanner = table.getScanner(scan);

            return IterableAdapter.from(scanner, result -> {
                EdgeKey key = EdgeKey.from(result.getRow());
                Direction d = key.direction;
                String elb = key.edgeLabel;
                Vertex dst = Vertex.create(key.dstId, key.dstLabel);

                Map<String, String> props = new HashMap<>();
                result.getFamilyMap(CF).forEach((k, v) -> {
                    if (HBaseUtils.startsWith(k, Constants.SYSTEM_PREFIX)) return;
                    props.put(Bytes.toString(k), Bytes.toString(v));
                });

                return Direction.OUT.equals(d)
                        ? Edge.create(elb, src, dst, props)
                        : Edge.create(elb, dst, src, props);
            });
        } catch (IOException e) {
            throw new HBaseRuntimeException(e);
        }
    }

    private static byte[] buildPartialRowKey(Vertex src) {
        return HBaseUtils.buildCompositeKeyWithBucket(
                src.id(), // seed
                src.label().getBytes(),
                src.id().getBytes()
        );
    }

    private static byte[] buildPartialRowKey(Vertex src, Direction direction) {
        return HBaseUtils.buildCompositeKeyWithBucket(
                src.id(), // seed
                src.label().getBytes(),
                src.id().getBytes(),
                direction == Direction.IN ? Constants.DIRECTION_IN : Constants.DIRECTION_OUT
        );
    }

    private static byte[] buildPartialRowKey(Vertex src, Direction direction, String label) {
        return HBaseUtils.buildCompositeKeyWithBucket(
                src.id(), // seed
                src.label().getBytes(),
                src.id().getBytes(),
                direction == Direction.IN ? Constants.DIRECTION_IN : Constants.DIRECTION_OUT,
                label.getBytes()
        );
    }

    private static byte[] buildFullRowKey(Vertex src, Vertex dst, String label, Direction direction) {
        Preconditions.checkArgument(direction != Direction.BOTH);
        return EdgeKey.from(src.label(), src.id(), direction, label, dst.label(), dst.id()).toBytes();
    }

    private static Put createEdgePut(Vertex src, Vertex dst, Direction direction, String label,
                                     Map<String, ?> edgeProperties) {
        byte[] row = buildFullRowKey(src, dst, label, direction);

        Put put = new Put(row);
        put.addColumn(CF, CQ_HIDDEN, HBaseUtils.EMPTY);
        edgeProperties.forEach((k, v) -> {
            if (Strings.isNullOrStringEmpty(v)) return;
            put.addColumn(CF, k.getBytes(), v.toString().getBytes());
        });

        return put;
    }
}
