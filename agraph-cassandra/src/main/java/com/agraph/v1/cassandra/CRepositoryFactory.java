package com.agraph.v1.cassandra;

import com.agraph.config.Config;
import com.google.common.base.Preconditions;
import com.agraph.v1.repository.EdgeRepository;
import com.agraph.v1.repository.RepositoryFactory;
import com.agraph.v1.repository.VertexRepository;

/**
 * TODO: Class description here.
 *
 * @author <a href="https://github.com/tjeubaoit">tjeubaoit</a>
 */
public class CRepositoryFactory implements RepositoryFactory {

    private Config conf;

    @Override
    public EdgeRepository edgeRepository() {
        Preconditions.checkNotNull(conf, "Repository Factory must be configure first");
        return new CEdgeRepository(conf);
    }

    @Override
    public VertexRepository vertexRepository() {
        Preconditions.checkNotNull(conf, "Repository Factory must be configure first");
        return new CVertexRepository(conf);
    }

    @Override
    public void configure(Config conf) {
        this.conf = conf;
    }
}
