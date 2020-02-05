package com.agraph.storage.backend.mysql;

import com.agraph.common.concurrent.FutureHelper;
import com.agraph.common.util.Strings;
import com.agraph.storage.Mutation;
import com.agraph.storage.Result;
import com.agraph.storage.TableEntry;
import com.agraph.storage.backend.BackendException;
import com.agraph.storage.backend.BackendSession;
import com.agraph.storage.backend.BackendTransaction;
import com.agraph.storage.backend.ClientProvider;
import com.agraph.storage.rdbms.Index;
import com.agraph.storage.rdbms.query.Condition;
import com.agraph.storage.rdbms.query.Order;
import com.agraph.storage.rdbms.query.Query;
import com.agraph.storage.rdbms.schema.Argument;
import com.agraph.storage.rdbms.schema.Column;
import com.agraph.storage.rdbms.schema.TableDefine;
import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.ListenableFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

/**
 * @author <a href="https://github.com/tjeubaoit">tjeubaoit</a>
 */
public class MySqlSession implements BackendSession {

    private static final Logger logger = LoggerFactory.getLogger(MySqlSession.class);

    private final ExecutorService ioExecutor;
    private final MySqlOptions options;
    private final ClientProvider<Connection> clientProvider;

    private Connection conn;
    private boolean opened;
    private BackendTransaction backendTx;

    public MySqlSession(ClientProvider<Connection> clientProvider,
                        MySqlOptions options, ExecutorService ioExecutor) {
        this.ioExecutor = ioExecutor;
        this.options = options;
        this.clientProvider = clientProvider;
    }

    @Override
    public void open() {
        try {
            this.conn = this.clientProvider.getClient();
            Statement stm = conn.createStatement();
            stm.execute("SELECT 1");
            this.opened = true;
            this.backendTx = new JdbcBackendTransaction(conn);
        } catch (SQLException e) {
            throw new BackendException(e);
        }
    }

    @Override
    public boolean opened() {
        return this.opened;
    }

    @Override
    public ListenableFuture<Boolean> isTableExists(String tableName) {
        this.ensureConnectionOpened();
        return this.executeAsync(() -> {
            try {
                logger.debug("Check table exists: " + tableName);
                Statement stm = conn.createStatement();
                String sql = Strings.format("SELECT 1 FROM %s LIMIT 1", tableName);
                stm.execute(sql);
                return true;
            } catch (SQLException e) {
                logger.warn("Check table exists failed: " + e.getMessage());
                return false;
            }
        });
    }

    @Override
    public ListenableFuture<?> createTable(TableDefine tableDefine) {
        logger.debug("Create table: " + tableDefine.name());
        this.ensureConnectionOpened();

        StringBuilder sb = new StringBuilder();
        sb.append(Strings.format("CREATE TABLE IF NOT EXISTS %s (\n", tableDefine.name()));

        for (Column column : tableDefine.columns()) {
            sb.append("\t");
            sb.append(column.name()).append(" ");
            sb.append(MySqlUtils.dbTypeToString(column.type(), column.length()));
            if (!column.allowNull()) {
                sb.append(" NOT NULL");
            }
            if (column.defaultValue() != null) {
                sb.append(" DEFAULT ");
                Object obj = column.defaultValue();
                if (obj instanceof String) {
                    sb.append("'").append(obj).append("'");
                } else {
                    sb.append(obj);
                }
            } else if (column.autoIncrement()) {
                sb.append(" AUTO_INCREMENT");
            }
            sb.append(",\n");
        }

        String pkText = Strings.join(tableDefine.keys(), ", ");
        sb.append("\t").append("PRIMARY KEY (").append(pkText).append(")\n");
        sb.append(") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;");

        String sql = sb.toString();
        logger.debug("Create table with DDL:\n" + sql);

        return this.executeAsync(() -> {
            try {
                Statement stm = conn.createStatement();
                stm.execute(sql);
            } catch (SQLException e) {
                throw new BackendException(e);
            }
        });
    }

    @Override
    public ListenableFuture<?> createIndex(Index index) {
        logger.debug("Create index {} on table {}", index.name(), index.table());
        this.ensureConnectionOpened();
        StringBuilder sb = new StringBuilder();

        sb.append(index.isUnique() ? "CREATE UNIQUE INDEX " : "CREATE INDEX ");
        sb.append(index.name());
        sb.append(" ON ").append(index.table());
        sb.append(Strings.join(index.columns(), ",", "(", ")"));

        return executeAsync(() -> {
            try {
                Statement stm = conn.createStatement();
                stm.execute(sb.toString());
            } catch (SQLException e) {
                throw new BackendException(e);
            }
        });
    }

    @Override
    public ListenableFuture<?> mutate(List<Mutation> mutations) {
        return this.executeAsync(() -> doMutate(mutations));
    }

    @Override
    public Iterator<Result> query(Query query) {
        this.ensureConnectionOpened();
        StringBuilder sb = new StringBuilder();

        sb.append("SELECT ").append(Strings.join(query.columns(), ","));
        sb.append("\nFROM ").append(query.table());

        Condition condition = query.condition();
        sb.append("\nWHERE ").append(condition.asString());
        if (!query.orders().isEmpty()) {
            sb.append("\nORDER BY ");
            int i = 0;
            for (Order order : query.orders()) {
                if (i++ > 0) sb.append(", ");
                sb.append(order.column()).append(order.isAsc() ? " ASC" : " DESC");
            }
        }
        if (query.limit() > 0) {
            sb.append(" LIMIT ").append(query.limit());
        }
        if (query.offset() > 0) {
            sb.append(" OFFSET ").append(query.offset());
        }

        return doRawQuery(sb.toString(), MySqlUtils.parseConditionArguments(condition));
    }

    @Override
    public Iterator<Result> rawQuery(String query, List<Object> args) {
        return doRawQuery(query, args);
    }

    @Override
    public BackendTransaction tx() {
        return this.backendTx;
    }

    @Override
    public void close() {
        this.ensureConnectionOpened();
        try {
            if (conn != null && !conn.isClosed()) {
                logger.debug("Closing MySQL connection");
                conn.close();
                logger.info("MySQL connection closed");
            }
        } catch (SQLException e) {
            throw new BackendException(e);
        }
    }

    private ListenableFuture<?> executeAsync(Runnable runnable) {
        return FutureHelper.transform(this.ioExecutor.submit(runnable), fut -> fut);
    }

    private <V> ListenableFuture<V> executeAsync(Callable<V> func) {
        return FutureHelper.transform(this.ioExecutor.submit(func), fut -> fut);
    }

    private void ensureConnectionOpened() {
        Preconditions.checkState(opened(), "MySQL session has not been initialized");
    }

    private Iterator<Result> doRawQuery(String query, List<Object> args) {
        logger.debug("Execute query: " + query);
        this.ensureConnectionOpened();

        try (PreparedStatement ps = conn.prepareStatement(query)) {
            int i = 1;
            for (Object arg : args) {
                ps.setObject(i++, arg);
            }
            ResultSet rs = ps.executeQuery();
            return new JdbcResultSet(rs);
        } catch (SQLException e) {
            throw new BackendException(e);
        }
    }

    private void doMutate(Collection<Mutation> mutations) {
        int batchSize = this.options.batchSize();
        Map<String, BatchStatement> statements = new HashMap<>();
        logger.debug("Do mutate for {} mutations", mutations.size());

        try {
            for (Mutation mutation : mutations) {
                logger.debug("Do mutate on mutation with {} entries", mutation.entrySize());
                for (TableEntry entry : mutation.entries()) {
                    String template = buildTemplate(mutation.table(), mutation.action(), entry);
                    BatchStatement bs = statements.get(template);
                    if (bs == null) {
                        bs = new BatchStatement(conn.prepareStatement(template));
                        statements.put(template, bs);
                    }
                    int i = 0;
                    for (Argument arg : buildArgumentList(mutation.action(), entry)) {
                        bs.setArgument(++i, arg);
                    }
                    bs.addBatch();
                    if (bs.size() > batchSize) {
                        logger.debug("Execute batch for statement: " + bs);
                        bs.executeBatch();
                    }
                }
            }
            // execute all remaining entries
            for (BatchStatement bs : statements.values()) {
                bs.executeBatch();
            }
        } catch (SQLException e) {
            throw new BackendException(e);
        } finally {
            try {
                logger.debug("Try to close all opening prepared statements");
                for (BatchStatement bs : statements.values()) {
                    bs.close();
                }
            } catch (SQLException e) {
                logger.warn("Failed to close prepared statements", e);
            }
        }
    }

    private static String buildTemplate(String table, Mutation.Action action, TableEntry entry) {
        switch (action) {
            case ADD:
                return MySqlUtils.buildInsertTemplate(table, entry);
            case UPSERT:
                return MySqlUtils.buildUpsertTemplate(table, entry);
            case REMOVE:
                return MySqlUtils.buildRemoveTemplate(table, entry);
            case UPDATE:
                return MySqlUtils.buildUpdateTemplate(table, entry);
            default:
                throw new BackendException(new UnsupportedOperationException(
                        Strings.format("Mutation.Action %s is not supported", action)));
        }
    }

    private static List<Argument> buildArgumentList(Mutation.Action action, TableEntry entry) {
        switch (action) {
            case ADD:
                return MySqlUtils.buildInsertArgs(entry);
            case UPSERT:
                return MySqlUtils.buildUpsertArgs(entry);
            case REMOVE:
                return MySqlUtils.buildRemoveArgs(entry);
            case UPDATE:
                return MySqlUtils.buildUpdateArgs(entry);
            default:
                throw new BackendException(new UnsupportedOperationException(
                        Strings.format("Mutation.Action %s is not supported", action)));
        }
    }
}
