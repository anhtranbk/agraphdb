/*
 * Copyright 2020 AGraph Authors
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with this
 * work for additional information regarding copyright ownership. The ASF
 * licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.agraph;

import com.agraph.core.DefaultGraph;
import com.google.common.base.Preconditions;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class AGraphFactory {

    private static final String NAME_REGEX = "^[A-Za-z][A-Za-z0-9_]{0,47}$";

    private static final Map<String, AGraph> graphs = new HashMap<>();

    public static synchronized AGraph open(Configuration config) {
        String name = config.getString("graph.name");
        Preconditions.checkArgument(name.matches(NAME_REGEX),
                "Invalid graph name '%s', valid graph name is up to " +
                "48 alpha-numeric characters and underscores " +
                "and only letters are supported as first letter. " +
                "Note: letter is case insensitive", name);
        name = name.toLowerCase();
        AGraph graph = graphs.get(name);
        if (graph == null || graph.isClosed()) {
            graph = new DefaultGraph(config);
            graphs.put(name, graph);
        }
        return graph;
    }

    public static AGraph open(String path) {
        return open(getLocalConfig(path));
    }

    public static AGraph open(URL url) {
        return open(getRemoteConfig(url));
    }

    private static PropertiesConfiguration getLocalConfig(String path) {
        File file = new File(path);
        Preconditions.checkArgument(file.exists() && file.isFile() && file.canRead(),
                "Please specify a proper config file rather than: %s",
                file.toString());
        try {
            return new PropertiesConfiguration(file);
        } catch (ConfigurationException e) {
            throw new AGraphException("Unable to load config file: %s", e, path);
        }
    }

    private static PropertiesConfiguration getRemoteConfig(URL url) {
        try {
            return new PropertiesConfiguration(url);
        } catch (ConfigurationException e) {
            throw new AGraphException("Unable to load remote config file: %s", e, url);
        }
    }
}
