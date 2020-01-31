package com.agraph.storage.backend.mysql;

import com.agraph.storage.StorageFeatures;

public class MySqlFeatures implements StorageFeatures {

    @Override
    public boolean isPersistent() {
        return true;
    }

    @Override
    public boolean hasBuiltInTransaction() {
        return true;
    }

    @Override
    public boolean hasTxIsolation() {
        return true;
    }

    @Override
    public boolean batchSupport() {
        return true;
    }

    @Override
    public boolean counterSupport() {
        return true;
    }

    @Override
    public boolean secondaryIndexSupport() {
        return true;
    }
}
