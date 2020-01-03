package com.agraph.v1.repository;

import com.agraph.config.Configurable;

/**
 * TODO: Class description here.
 *
 * @author <a href="https://github.com/tjeubaoit">tjeubaoit</a>
 */
public interface RepositoryFactory extends Configurable {

    EdgeRepository edgeRepository();

    VertexRepository vertexRepository();
}
