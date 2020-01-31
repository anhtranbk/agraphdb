package com.agraph.storage.backend.mysql;

import com.agraph.AGraph;
import com.agraph.common.util.Systems;
import com.agraph.storage.StorageFeatures;
import com.agraph.storage.AbstractStorageBackend;
import com.agraph.storage.backend.ClientProvider;

import java.sql.Connection;

public class MySqlBackend extends AbstractStorageBackend<MySqlSession> {

    private final AGraph graph;
    private final MySqlOptions options;
    private final ClientProvider<Connection> clientProvider;

    public MySqlBackend(AGraph graph) {
        this.graph = graph;
        this.options = new MySqlOptions(this.graph.config());
        this.clientProvider = new JdbcClientProvider(this.options);

        // add shutdown hook to clean resources
        Systems.addShutdownHook(this::dispose);
    }

    @Override
    public String name() {
        return "mysql";
    }

    @Override
    public String version() {
        return "5.7";
    }

    @Override
    public StorageFeatures features() {
        return new MySqlFeatures();
    }

    @Override
    protected MySqlSession newSession() {
        return new MySqlSession(
                this.clientProvider,
                this.options,
                this.graph.ioThreadPool()
        );
    }

    private void dispose() {
        this.clientProvider.shutdown();
    }
}
