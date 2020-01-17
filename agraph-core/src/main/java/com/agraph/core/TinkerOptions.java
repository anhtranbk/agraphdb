package com.agraph.core;

import com.agraph.config.Config;
import com.agraph.config.ConfigDescriptor;
import com.agraph.config.Configurable;
import lombok.Data;
import lombok.experimental.Accessors;

@Accessors(chain = true, fluent = true)
public @Data class TinkerOptions implements Configurable {

    @ConfigDescriptor(value = "graph.name")
    private String name;

    public TinkerOptions(Config conf) {
        this.configure(conf);
    }

    public TinkerOptions() {
    }
}
