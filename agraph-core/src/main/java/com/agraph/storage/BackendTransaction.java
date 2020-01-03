package com.agraph.storage;

public interface BackendTransaction {

    void commit();

    void rollback();
}
