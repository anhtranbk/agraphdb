package com.agraph.v1;

import com.agraph.common.utils.Reflects;
import com.agraph.config.Config;
import com.agraph.v1.repository.RepositoryFactory;

/**
 * TODO: Class description here.
 *
 * @author <a href="https://github.com/tjeubaoit">tjeubaoit</a>
 */
public interface GraphDatabase {

    static GraphSession open(Config config) {
        return open(null, config);
    }

    static GraphSession open(String namespace, Config config) {
        try {
            String clsName = config.getString("graphdb.storage.factory.class");
            RepositoryFactory factory = Reflects.newInstance(clsName);
            if (namespace != null) {
                config.set("graphdb.namespace", namespace);
            }
            factory.configure(config);
            return new DefaultSession(factory);
        } catch (Throwable t) {
            throw new GraphException(t);
        }
    }
}
