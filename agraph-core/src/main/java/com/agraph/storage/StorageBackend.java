package com.agraph.storage;

import com.agraph.storage.backend.BackendSession;

public interface StorageBackend extends AutoCloseable {

    String name();

    String version();

    StorageFeatures features();

    BackendSession session();

    @Override
    void close();
}
