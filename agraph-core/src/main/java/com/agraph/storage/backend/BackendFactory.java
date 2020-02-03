package com.agraph.storage.backend;

import com.agraph.AGraph;
import com.agraph.storage.StorageBackend;

public interface BackendFactory {

    StorageBackend open(AGraph graph);
}
