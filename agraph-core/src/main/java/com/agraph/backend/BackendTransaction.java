package com.agraph.backend;

public interface BackendTransaction extends AutoCloseable {

    void commit();

    void rollback();

    boolean autoCommit();

    void close();
}
