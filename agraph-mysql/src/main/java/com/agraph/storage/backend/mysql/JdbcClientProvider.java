package com.agraph.storage.backend.mysql;

import com.agraph.storage.backend.BackendException;
import com.agraph.storage.backend.ClientProvider;
import com.mysql.cj.jdbc.Driver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class JdbcClientProvider implements ClientProvider<Connection> {

    private static final Logger logger = LoggerFactory.getLogger(JdbcClientProvider.class);

    private final MySqlOptions options;
    private final List<Connection> connections = new ArrayList<>();

    public JdbcClientProvider(MySqlOptions options) {
        this.options = options;
    }

    @Override
    public Connection getClient() {
        try {
            logger.info("Initializing MySQL connection with uri={}", this.options.uri());
            Class.forName(Driver.class.getName());
            Connection conn = DriverManager.getConnection(this.options.uri(),
                    this.options.username(), this.options.password());
            logger.info("MySQL connection initialized");

            this.connections.add(conn);
            return conn;
        } catch (SQLException | ClassNotFoundException e) {
            throw new BackendException(e);
        }
    }

    @Override
    public void shutdown() {
        logger.info("Close all opening connections...");
        connections.forEach(conn -> {
            try {
                if (!conn.isClosed()) {
                    conn.close();
                }
            } catch (SQLException t) {
                throw new BackendException(t);
            }
        });
    }
}
