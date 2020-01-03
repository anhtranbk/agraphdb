package com.agraph.v1.repository;

import com.agraph.v1.Direction;
import com.agraph.v1.Edge;
import com.agraph.v1.Vertex;

/**
 * TODO: Class description here.
 *
 * @author <a href="https://github.com/tjeubaoit">tjeubaoit</a>
 */
public interface EdgeRepository extends CrudRepository<Edge> {

    Iterable<Edge> findByVertex(Vertex src, Direction direction, String label);
}
