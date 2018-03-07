package com.vcc.bigdata.graphdb;

import com.vcc.bigdata.graphdb.repository.EdgeRepository;
import com.vcc.bigdata.graphdb.repository.VertexRepository;

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
