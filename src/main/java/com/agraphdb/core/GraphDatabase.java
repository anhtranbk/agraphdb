package com.agraphdb.core;

import com.agraphdb.common.config.Properties;
import com.agraphdb.common.utils.Reflects;
import com.agraphdb.core.repository.RepositoryFactory;

/**
 * TODO: Class description here.
 *
 * @author <a href="https://github.com/tjeubaoit">tjeubaoit</a>
 */
public interface GraphDatabase {

    static GraphSession open(Properties props) {
        return open(null, props);
    }

    static GraphSession open(String namespace, Properties props) {
        try {
            RepositoryFactory factory = Reflects.newInstance(props.getProperty("graphdb.storage.factory.class"));
            if (namespace != null) props.setProperty("graphdb.namespace", namespace);
            factory.configure(props);
            return new DefaultSession(factory);
        } catch (Throwable t) {
            throw new GraphException(t);
        }
    }
}
