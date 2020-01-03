package com.agraph.core.cassandra;

import com.agraph.config.Config;
import com.datastax.driver.core.BatchStatement;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.ResultSetFuture;
import com.datastax.driver.core.Row;
import com.google.common.collect.Iterables;
import com.google.common.util.concurrent.ListenableFuture;
import com.agraph.common.concurrency.FutureAdapter;
import com.agraph.common.utils.DateTimes;
import com.agraph.common.utils.IterableAdapter;
import com.agraph.common.utils.Maps;
import com.agraph.common.utils.Utils;
import com.agraph.v1.Vertex;
import com.agraph.v1.repository.VertexRepository;
import com.agraph.storage.cassandra.AbstractRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * TODO: Class description here.
 *
 * @author <a href="https://github.com/tjeubaoit">tjeubaoit</a>
 */
public class CVertexRepository extends AbstractRepository implements VertexRepository {

    private static final AtomicBoolean statementPrepared = new AtomicBoolean(false);
    private static PreparedStatement psInsert, psUpdateDt, psDelete;

    public CVertexRepository(Config conf) {
        super(conf);
        if (statementPrepared.compareAndSet(false, true)) {
            psUpdateDt = session.prepare(
                    "UPDATE vertices SET ts = ?, p = p + ? WHERE salt = ? AND label = ? AND id = ?");
            psInsert = session.prepare(
                    "INSERT INTO vertices (salt, label, id, dt) VALUES (?, ?, ?, ?) USING TIMESTAMP ?");
            psDelete = session.prepare(
                    "DELETE FROM vertices WHERE salt = ? AND label = ? AND id = ?");
        }
    }

    @Override
    public Iterable<Vertex> findAll() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Vertex findOne(Vertex entity) {
        return findOne(entity.label(), entity.id());
    }

    @Override
    public Vertex findOne(String label, String id) {
        String query = "SELECT * FROM vertices WHERE salt = ? AND label = ? AND id = ?";
        Row row = session.execute(query, createSaltFrom(id), label, id).one();
        if (row == null) return null;

        return Vertex.create(
                row.getString("id"),
                row.getString("label"),
                row.getMap("p", String.class, String.class));
    }

    @SuppressWarnings("unchecked")
    @Override
    public Iterable<Vertex> findByLabel(String label) {
        String query = "SELECT * FROM vertices WHERE salt = ? AND label = ?";
        List<Iterable<Vertex>> list = new ArrayList<>(NUMBER_SALT);

        for (int i = 1; i <= NUMBER_SALT; i++) {
            ResultSet rs = session.execute(query, String.valueOf(i), label);

            Iterable<Vertex> vertices =  IterableAdapter.from(rs, row -> {
                String id = row.getString("id");
                Map<String, String> p = row.getMap("p", String.class, String.class);
                return Vertex.create(id, label, p);
            });
            list.add(vertices);
        }

        return Iterables.concat(list);
    }

    @Override
    public ListenableFuture<Iterable<Vertex>> delete(Vertex entity) {
        ResultSetFuture fut = session.executeAsync(psDelete.bind(
                createSaltFrom(entity.id()),
                entity.label(),
                entity.id()));
        return FutureAdapter.from(fut, rs -> Collections.singleton(entity));
    }

    @Override
    public ListenableFuture<Iterable<Vertex>> save(Vertex entity) {
        return saveAll(Collections.singleton(entity));
    }

    @Override
    public ListenableFuture<Iterable<Vertex>> saveAll(Collection<Vertex> entities) {
        BatchStatement bs = new BatchStatement();
        for (Vertex vertex : entities) {
            String salt = createSaltFrom(vertex.id());

            bs.add(psInsert.bind(
                    salt,
                    vertex.label(),
                    vertex.id(),
                    DateTimes.currentDateAsString(),
                    Utils.reverseTimestamp()));

            bs.add(psUpdateDt.bind(
                    new Date(),
                    Maps.convertToTextMap(vertex.properties()),
                    salt,
                    vertex.label(),
                    vertex.id()));
        }
        return FutureAdapter.from(session.executeAsync(bs), rs -> entities);
    }

    private static String createSaltFrom(String input) {
        return String.valueOf(Math.abs(input.hashCode()) % NUMBER_SALT);
//        return Hashings.sha1AsHex(input).substring(0, 1);
    }

    private static final int NUMBER_SALT = 10;
}
