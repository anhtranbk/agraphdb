package com.agraph.storage.rdbms;

import com.agraph.storage.StorageEngine;

public abstract class RdbmsStorageEngine implements StorageEngine {

    protected final RdbmsStorageBackend backend;

    public RdbmsStorageEngine(RdbmsStorageBackend backend) {
        this.backend = backend;
    }
}
