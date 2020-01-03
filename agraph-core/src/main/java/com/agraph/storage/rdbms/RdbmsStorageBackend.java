package com.agraph.storage.rdbms;

import com.agraph.storage.MutationList;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface RdbmsStorageBackend {

    void createTable(String tableName, Map<String, Object> cols, List<String> primaryKeys);

    void mutate(String tableName, MutationList mutations);

    void delete(String tableName, Set<ByteBuffer> primaryKeys);
}
