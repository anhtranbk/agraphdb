package com.agraph.storage.rdbms;

import com.agraph.AGraph;
import com.agraph.storage.StorageEngine;

public abstract class RdbmsStorageEngine implements StorageEngine {

    protected final RdbmsStorageBackend backend;
    protected final AGraph graph;

    public RdbmsStorageEngine(AGraph graph, RdbmsStorageBackend backend) {
        this.graph = graph;
        this.backend = backend;
    }

    @Override
    public AGraph graph() {
        return graph;
    }

    @Override
    public RdbmsStorageBackend backend() {
        return backend;
    }

    @Override
    public void close() {
        backend.close();
    }
}
