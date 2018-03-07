package com.vcc.bigdata.graphdb.hbase;

import com.vcc.bigdata.common.config.Properties;
import com.vcc.bigdata.platform.hbase.AbstractRepository;
import org.apache.hadoop.hbase.TableName;

/**
 * TODO: Class description here.
 *
 * @author <a href="https://github.com/tjeubaoit">tjeubaoit</a>
 */
public class BaseRepository extends AbstractRepository {

    protected static final String DEFAULT_NAMESPACE = "default";
    private final String namespace;

    public BaseRepository(Properties props) {
        super(props);
        this.namespace = props.getProperty("graphdb.namespace", DEFAULT_NAMESPACE);
    }

    protected final TableName getTableName(String name) {
        return DEFAULT_NAMESPACE.equals(namespace)
                ? TableName.valueOf(name) : TableName.valueOf(namespace + ":" + name);
    }
}
