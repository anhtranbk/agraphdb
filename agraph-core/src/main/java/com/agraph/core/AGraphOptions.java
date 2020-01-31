package com.agraph.core;

import com.agraph.storage.backend.BackendFactory;
import com.agraph.config.Config;
import com.agraph.config.ConfigDescriptor;
import com.agraph.config.Configurable;
import lombok.Data;
import lombok.experimental.Accessors;

@Accessors(chain = true, fluent = true)
public @Data class AGraphOptions implements Configurable {

    @ConfigDescriptor(name = "graph.name")
    private String name;

    @ConfigDescriptor(name = "storage.engine")
    private String storageEngine;

    @ConfigDescriptor(name = "backend.name")
    private String backend;

    private Class<?> backendFactoryCls;

    public AGraphOptions(Config conf) {
        this.configure(conf);
    }

    public AGraphOptions() {
    }

    @Override
    public void configure(Config conf) {
        Configurable.super.configure(conf);
        backendFactoryCls = conf.getClass("backend.factory.class", BackendFactory.class);
    }
}
