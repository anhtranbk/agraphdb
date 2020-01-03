package com.agraph.v1.hbase;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;

import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

/**
 * TODO: Class description here.
 *
 * @author <a href="https://github.com/tjeubaoit">tjeubaoit</a>
 */
public class HBaseConnectionProvider {

    private static Map<String, Connection> clients = new TreeMap<>();

    public static Connection getDefault(Configuration conf) {
        return getOrCreate("default", conf);
    }

    public static synchronized Connection getOrCreate(String name, Configuration conf) {
        return clients.computeIfAbsent(name, k -> initHbaseConnection(conf));
    }

    static Connection initHbaseConnection(Configuration conf) {
        try {
            return ConnectionFactory.createConnection(conf);
        } catch (IOException e) {
            throw new HBaseRuntimeException(e);
        }
    }
}
