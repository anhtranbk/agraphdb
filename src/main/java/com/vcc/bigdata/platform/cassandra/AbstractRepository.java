package com.vcc.bigdata.platform.cassandra;

import com.datastax.driver.core.Session;
import com.vcc.bigdata.common.config.Properties;
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
    protected final Properties props;
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    public AbstractRepository(Properties props) {
        CassandraConfig conf = new CassandraConfig(props);
        this.session = createSession(conf);
        this.props = props;
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
