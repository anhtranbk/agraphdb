package com.agraphdb.storage.cassandra;

import com.google.common.net.HostAndPort;
import com.agraphdb.common.config.Properties;

import java.util.Collection;
import java.util.LinkedHashSet;

/**
 * TODO: Class description here.
 *
 * @author <a href="https://github.com/tjeubaoit">tjeubaoit</a>
 */
public class CassandraConfig {

    private final Collection<HostAndPort> hosts = new LinkedHashSet<>();
    private String clusterName;
    private String keyspace;

    public CassandraConfig(Properties p) {
        Collection<String> addresses = p.getCollection("cassandra.hosts");
        addresses.forEach(addr -> hosts.add(HostAndPort.fromString(addr)));
        this.clusterName = p.getProperty("cassandra.cluster.name");
        this.keyspace = p.getProperty("cassandra.keyspace");
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
