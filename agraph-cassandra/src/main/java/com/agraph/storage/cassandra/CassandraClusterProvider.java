package com.agraph.storage.cassandra;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.ConsistencyLevel;
import com.datastax.driver.core.PoolingOptions;
import com.datastax.driver.core.ProtocolOptions;
import com.datastax.driver.core.QueryOptions;
import com.datastax.driver.core.SocketOptions;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

/**
 * TODO: Class description here.
 *
 * @author <a href="https://github.com/tjeubaoit">tjeubaoit</a>
 */
public class CassandraClusterProvider {

    private static Map<String, Cluster> clients = new TreeMap<>();

    public static Cluster getDefault(CassandraConfig config) {
        return getOrCreate("default", config);
    }

    public static synchronized Cluster getOrCreate(String name, CassandraConfig config) {
        return clients.computeIfAbsent(name, k -> initCassandraCluster(config));
    }

    static Cluster initCassandraCluster(CassandraConfig config) {
        Collection<InetSocketAddress> socketAddresses = new ArrayList<>(config.getHosts().size());
        config.getHosts().forEach(host -> socketAddresses.add(
                new InetSocketAddress(host.getHost(), host.getPort())));

        return Cluster.builder()
                .withPoolingOptions(new PoolingOptions().setMaxQueueSize(512))
                .withQueryOptions(new QueryOptions()
                        .setFetchSize(500)
                        .setConsistencyLevel(ConsistencyLevel.QUORUM))
                .withSocketOptions(new SocketOptions().setReadTimeoutMillis(60000))
                .withCompression(ProtocolOptions.Compression.LZ4)
                .withClusterName(config.getClusterName())
                .addContactPointsWithPorts(socketAddresses)
                .build();
    }
}
