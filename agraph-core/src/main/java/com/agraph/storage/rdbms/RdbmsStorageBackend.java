package com.agraph.storage.rdbms;

import com.agraph.storage.Mutation;
import com.agraph.storage.StorageBackend;
import com.agraph.storage.StorageFeatures;
import com.agraph.storage.rdbms.query.Query;

import java.util.Iterator;
import java.util.List;

/**
 * @author <a href="https://github.com/tjeubaoit">tjeubaoit</a>
 */
public interface RdbmsStorageBackend extends StorageBackend {

    boolean isTableExists(String tableName);

    void createTable(String tableName, Iterable<Column> columns, String... keys);

    void createIndices(String tableName, Iterable<Index> indices);

    void mutate(String tableName, Iterable<Mutation> mutations);

    void delete(String tableName, String... keys);

    Iterator<Result> query(Query query);

    Iterator<Result> rawQuery(String query, List<Object> params);

    @Override
    default StorageFeatures getFeatures() {
        return new RdbmsStorageFeature();
    }
}
