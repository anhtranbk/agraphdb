package com.agraph.core;

import com.agraph.backend.BackendFactory;
import com.agraph.config.Config;
import com.agraph.config.ConfigDescriptor;
import com.agraph.config.Configurable;
import lombok.Data;
import lombok.experimental.Accessors;

@Accessors(chain = true, fluent = true)
public @Data class AGraphOptions implements Configurable {

    @ConfigDescriptor(value = "storage.engine")
    private String storageEngine;

    @ConfigDescriptor(value = "backend.name")
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
