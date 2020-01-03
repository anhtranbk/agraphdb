package com.agraph.storage;

public interface StorageFeatures {

    boolean isPersistent();

    boolean hasBuiltInTransaction();

    boolean hasTxIsolation();

    boolean batchSupport();

    boolean counterSupport();

    boolean secondaryIndexSupport();
}
