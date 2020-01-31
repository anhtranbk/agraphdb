package com.agraph.storage.backend;

import com.agraph.storage.Mutation;
import com.agraph.storage.Result;
import com.agraph.storage.rdbms.Index;
import com.agraph.storage.rdbms.query.Query;
import com.agraph.storage.rdbms.schema.TableDefine;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.Iterator;
import java.util.List;

public interface BackendSession extends AutoCloseable {

    void open();

    boolean opened();

    ListenableFuture<Boolean> isTableExists(String tableName);

    ListenableFuture<?> createTable(TableDefine tableDefine);

    ListenableFuture<?> createIndex(Index index);

    ListenableFuture<?> mutate(List<Mutation> mutations);

    Iterator<Result> query(Query query);

    Iterator<Result> rawQuery(String query, List<Object> args);

    BackendTransaction tx();

    @Override
    void close();
}
