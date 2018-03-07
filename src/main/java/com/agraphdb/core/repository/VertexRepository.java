package com.agraphdb.core.repository;

import com.agraphdb.core.Vertex;

/**
 * TODO: Class description here.
 *
 * @author <a href="https://github.com/tjeubaoit">tjeubaoit</a>
 */
public interface VertexRepository extends CrudRepository<Vertex> {

    Vertex findOne(String label, String id);

    Iterable<Vertex> findByLabel(String label);
}
