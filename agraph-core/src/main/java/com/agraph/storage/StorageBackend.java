package com.agraph.storage;

import com.agraph.backend.BackendTransaction;

public interface StorageBackend extends AutoCloseable {

    StorageFeatures getFeatures();

    BackendTransaction getTx();

    @Override
    void close();
}
