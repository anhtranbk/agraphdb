package com.agraphdb.core.repository;

import com.agraphdb.core.Direction;
import com.agraphdb.core.Edge;
import com.agraphdb.core.Vertex;

/**
 * TODO: Class description here.
 *
 * @author <a href="https://github.com/tjeubaoit">tjeubaoit</a>
 */
public interface EdgeRepository extends CrudRepository<Edge> {

    Iterable<Edge> findByVertex(Vertex src, Direction direction, String label);
}
