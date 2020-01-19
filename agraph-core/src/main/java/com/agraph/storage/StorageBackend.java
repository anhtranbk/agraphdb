package com.agraph.storage;

import com.agraph.backend.BackendTransaction;

public interface StorageBackend extends AutoCloseable {

    StorageFeatures features();

    BackendTransaction backendTx();

    @Override
    void close();
}
