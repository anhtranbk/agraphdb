package com.agraph.v1.hbase;

import com.agraph.config.Config;
import com.google.common.util.concurrent.Futures;
import com.agraph.common.util.ThreadPool;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * TODO: Class description here.
 *
 * @author <a href="https://github.com/tjeubaoit">tjeubaoit</a>
 */
public abstract class AbstractRepository implements Closeable {

    public static final byte[] CF = "cf".getBytes();
    public static final byte[] CQ_HIDDEN = "__h".getBytes();

    protected final Connection connection;
    private final boolean asyncMode;

    public AbstractRepository(Config conf) {
        this.connection = HBaseConnectionProvider.getDefault(HBaseConfig.loadConfig());
        this.asyncMode = conf.getBool("hbase.client.async.mode", false);
    }

    protected void createTablesIfNotExists(byte[] family, TableName... names) {
        try (Admin admin = connection.getAdmin()) {
            for (TableName name : names) {
                if (admin.tableExists(name)) continue;
                admin.createTable(new HTableDescriptor(name).addFamily(new HColumnDescriptor(family)));
            }
        } catch (IOException e) {
            throw new HBaseRuntimeException(e);
        }
    }

    @Override
    public void close() {
        try {
            if (!connection.isClosed()) connection.close();
        } catch (IOException e) {
            throw new HBaseRuntimeException(e);
        }
    }

    /**
     * @param name task name, use to monitoring
     * @param task task to execute
     *
     * @return a Future representing pending completion of the task
     */
    protected final Future<?> execute(String name, Runnable task) {
        if (asyncMode) {
            return Futures.immediateFuture(0);
//            return new SyncCommand<>("HBaseClient", name, () -> {
//                task.run();
//                return null;
//            }).queue();
        } else {
            try {
                task.run();
                return Futures.immediateFuture(0);
            } catch (Throwable t) {
                return Futures.immediateFailedFuture(t);
            }
        }
    }

    protected static ExecutorService initInternalThreadPool(Config conf) {
        return ThreadPool.builder()
                .setCoreSize(conf.getInt("hbase.client.threadpool.core.size",
                        Runtime.getRuntime().availableProcessors()))
                .setQueueSize(conf.getInt("hbase.client.threadpool.queue.size", 512))
                .setNamePrefix("HBaseClient-pool-worker")
                .setDaemon(true)
                .build();
    }

    protected static byte[] toByteSafe(Object obj) {
        try {
            return HBaseUtils.toBytes(obj);
        } catch (NullPointerException e) {
            return null;
        }
    }
}
