package com.agraph.core;

import com.agraph.config.Config;
import com.agraph.config.ConfigDescriptor;
import com.agraph.config.ConfigException;
import com.agraph.config.Configurable;
import com.agraph.core.serialize.DefaultSerializer;
import com.agraph.core.serialize.Serializer;
import com.agraph.storage.backend.BackendFactory;
import lombok.Data;
import lombok.experimental.Accessors;

@Accessors(chain = true, fluent = true)
public @Data class AGraphOptions implements Configurable {

    @ConfigDescriptor(name = "graph.name")
    private String name;

    @ConfigDescriptor(name = "storage.engine")
    private String storageEngine;

    @ConfigDescriptor(name = "storage.backend")
    private String backend;

    private Serializer serializer;

    public AGraphOptions(Config conf) {
        this.configure(conf);
    }

    public AGraphOptions() {
    }

    @SuppressWarnings("unchecked")
    @Override
    public void configure(Config conf) {
        try {
            Configurable.super.configure(conf);
            Class<Serializer> serializerCls = (Class<Serializer>)
                    conf.getClass("backend.serializer", DefaultSerializer.class);
            this.serializer = serializerCls.newInstance();
        } catch (Throwable t) {
            throw new ConfigException(t);
        }
    }

    public BackendFactory backendFactory() {
        return FactoryRegistry.getBackendFactory(this.backend);
    }
}
