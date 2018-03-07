package com.vcc.bigdata.graphdb.repository;

import com.vcc.bigdata.graphdb.Direction;
import com.vcc.bigdata.graphdb.Edge;
import com.vcc.bigdata.graphdb.Vertex;

/**
 * TODO: Class description here.
 *
 * @author <a href="https://github.com/tjeubaoit">tjeubaoit</a>
 */
public interface EdgeRepository extends CrudRepository<Edge> {

    Iterable<Edge> findByVertex(Vertex src, Direction direction, String label);
}
