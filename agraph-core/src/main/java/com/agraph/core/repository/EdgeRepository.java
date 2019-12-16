package com.agraph.core.repository;

import com.agraph.core.Direction;
import com.agraph.core.Edge;
import com.agraph.core.Vertex;

/**
 * TODO: Class description here.
 *
 * @author <a href="https://github.com/tjeubaoit">tjeubaoit</a>
 */
public interface EdgeRepository extends CrudRepository<Edge> {

    Iterable<Edge> findByVertex(Vertex src, Direction direction, String label);
}
