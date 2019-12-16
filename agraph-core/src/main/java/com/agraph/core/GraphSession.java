package com.agraph.core;

import com.agraph.core.repository.EdgeRepository;
import com.agraph.core.repository.VertexRepository;

import java.io.Closeable;

/**
 * TODO: Class description here.
 *
 * @author <a href="https://github.com/tjeubaoit">tjeubaoit</a>
 */
public interface GraphSession extends VertexFunctions, EdgeFunctions, Closeable {

    EdgeRepository edgeRepository();

    VertexRepository vertexRepository();

    @Override
    void close();
}
