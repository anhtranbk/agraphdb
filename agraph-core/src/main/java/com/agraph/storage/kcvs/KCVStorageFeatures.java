package com.agraph.storage.kcvs;

import com.agraph.storage.StorageFeatures;

public interface KCVStorageFeatures extends StorageFeatures {

    boolean dynamicColumnSupport();

    boolean hasScan();

    boolean hasTimestamps();

    boolean hasCellTTL();
}
