package com.agraph.storage.cassandra;

import com.agraph.config.Config;
import com.datastax.driver.core.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;

/**
 * TODO: Class description here.
 *
 * @author <a href="https://github.com/tjeubaoit">tjeubaoit</a>
 */
public abstract class AbstractRepository implements Closeable {

    protected final Session session;
    protected final Config conf;
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    public AbstractRepository(Config conf) {
        CassandraConfig cassandraConfig = new CassandraConfig(conf);
        this.session = createSession(cassandraConfig);
        this.conf = conf;
    }

    @Override
    public void close() {
        if (!session.isClosed()) session.close();
    }

    public Session getSession() {
        return session;
    }

    protected Session createSession(CassandraConfig conf) {
        return CassandraClusterProvider.getDefault(conf).connect(conf.getKeyspace());
    }
}
