package com.agraphdb.core;

import com.agraphdb.core.repository.EdgeRepository;
import com.agraphdb.core.repository.VertexRepository;

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
