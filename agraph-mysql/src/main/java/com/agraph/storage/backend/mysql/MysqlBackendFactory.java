package com.agraph.storage.backend.mysql;

import com.agraph.AGraph;
import com.agraph.storage.StorageBackend;
import com.agraph.storage.backend.BackendFactory;

public class MysqlBackendFactory implements BackendFactory {

    private MySqlBackend instance;

    @Override
    public StorageBackend open(AGraph graph) {
        if (instance == null) {
            instance = new MySqlBackend(graph);
        }
        return instance;
    }
}
