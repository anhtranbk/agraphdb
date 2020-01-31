package com.agraph.storage.backend.mysql;

import com.agraph.storage.Mutation;
import com.agraph.storage.backend.BackendException;
import com.agraph.storage.backend.BackendTransaction;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;

public class JdbcBackendTransaction implements BackendTransaction {

    private final Connection conn;

    public JdbcBackendTransaction(Connection conn) {
        this.conn = conn;
    }

    @Override
    public void beforeCommit(Collection<Mutation> mutations) {
        try {
            this.conn.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
            this.autoCommit(false);
        } catch (SQLException e) {
            throw new BackendException(e);
        }
    }

    @Override
    public void afterCommit() {
        this.autoCommit(true);
    }

    @Override
    public void commit() {
        try {
            this.conn.commit();
        } catch (SQLException e) {
            throw new BackendException(e);
        }
    }

    @Override
    public void beforeRollback(Collection<Mutation> mutations) {
        try {
            this.conn.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
            this.autoCommit(false);
        } catch (SQLException e) {
            throw new BackendException(e);
        }
    }

    @Override
    public void afterRollback() {
        this.autoCommit(true);
    }

    @Override
    public void rollback() {
        try {
            this.conn.rollback();
        } catch (SQLException e) {
            throw new BackendException(e);
        }
    }

    @Override
    public boolean autoCommit() {
        try {
            return this.conn.getAutoCommit();
        } catch (SQLException e) {
            throw new BackendException(e);
        }
    }

    @Override
    public void autoCommit(boolean autoCommit) {
        try {
            this.conn.setAutoCommit(autoCommit);
        } catch (SQLException e) {
            throw new BackendException(e);
        }
    }
}
