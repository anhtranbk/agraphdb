package com.agraph.storage.rdbms;

import com.agraph.storage.Index;
import com.agraph.storage.Mutation;
import com.agraph.storage.Result;
import com.agraph.storage.StorageBackend;
import com.agraph.storage.StorageFeatures;
import com.agraph.storage.rdbms.schema.TableDefine;
import com.agraph.storage.rdbms.query.Query;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * @author <a href="https://github.com/tjeubaoit">tjeubaoit</a>
 */
public interface RdbmsStorageBackend extends StorageBackend {

    ListenableFuture<Boolean> isTableExists(String tableName);

    ListenableFuture<?> createTable(TableDefine tableDefine);

    ListenableFuture<?> createIndex(Index index);

    ListenableFuture<?> mutate(Collection<Mutation> mutations);

    Iterator<Result> query(Query query);

    Iterator<Result> rawQuery(String query, List<Object> params);

    @Override
    default StorageFeatures features() {
        return new RdbmsStorageFeature();
    }
}
