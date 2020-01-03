package com.agraph.core.hbase;

import com.agraph.config.Config;
import com.agraph.storage.hbase.AbstractRepository;
import org.apache.hadoop.hbase.TableName;

/**
 * TODO: Class description here.
 *
 * @author <a href="https://github.com/tjeubaoit">tjeubaoit</a>
 */
public class BaseRepository extends AbstractRepository {

    protected static final String DEFAULT_NAMESPACE = "default";
    private final String namespace;

    public BaseRepository(Config conf) {
        super(conf);
        this.namespace = conf.getString("graphdb.namespace", DEFAULT_NAMESPACE);
    }

    protected final TableName getTableName(String name) {
        return DEFAULT_NAMESPACE.equals(namespace)
                ? TableName.valueOf(name) : TableName.valueOf(namespace + ":" + name);
    }
}
