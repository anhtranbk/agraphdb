package com.agraph.storage.backend;

import com.agraph.config.Config;
import com.agraph.config.ConfigDescriptor;
import com.agraph.config.Configurable;
import lombok.Data;
import lombok.experimental.Accessors;

@Accessors(fluent = true, chain = true)
public @Data class BackendOptions implements Configurable {

    @ConfigDescriptor(name = "backend.mutate.batchSize")
    private int batchSize;

    public BackendOptions() {
    }

    public BackendOptions(Config conf) {
        this.configure(conf);
    }
}
