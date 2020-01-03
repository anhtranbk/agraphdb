package com.agraph.storage.cassandra;

import com.agraph.config.Config;
import com.google.common.net.HostAndPort;

import java.util.Collection;
import java.util.LinkedHashSet;

@SuppressWarnings("UnstableApiUsage")
public class CassandraConfig {

    private final Collection<HostAndPort> hosts = new LinkedHashSet<>();
    private String clusterName;
    private String keyspace;

    public CassandraConfig(Config conf) {
        Collection<String> addresses = conf.getCollection("cassandra.hosts");
        addresses.forEach(addr -> hosts.add(HostAndPort.fromString(addr)));
        this.clusterName = conf.getString("cassandra.cluster.name");
        this.keyspace = conf.getString("cassandra.keyspace");
    }

    public Collection<HostAndPort> getHosts() {
        return hosts;
    }

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    public String getKeyspace() {
        return keyspace;
    }

    public void setKeyspace(String keyspace) {
        this.keyspace = keyspace;
    }
}
