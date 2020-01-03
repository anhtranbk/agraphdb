package com.agraph.storage;

public interface StorageBackend extends AutoCloseable {

    StorageFeatures getFeatures();

    BackendTransaction getTx();
}
