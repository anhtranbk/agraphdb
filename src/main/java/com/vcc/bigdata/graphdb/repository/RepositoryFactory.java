package com.vcc.bigdata.graphdb.repository;

import com.agraphdb.common.config.Configurable;

/**
 * TODO: Class description here.
 *
 * @author <a href="https://github.com/tjeubaoit">tjeubaoit</a>
 */
public interface RepositoryFactory extends Configurable {

    EdgeRepository edgeRepository();

    VertexRepository vertexRepository();
}
