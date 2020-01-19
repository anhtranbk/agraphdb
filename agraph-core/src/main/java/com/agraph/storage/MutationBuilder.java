package com.agraph.storage;

import com.agraph.core.InternalEdge;
import com.agraph.core.InternalVertex;

import java.util.Collection;

public interface MutationBuilder {

    Collection<Mutation> fromModifiedVertices(Iterable<InternalVertex> vertices);

    Collection<Mutation> fromRemovedVertices(Iterable<InternalVertex> vertices);

    Collection<Mutation> fromModifiedEdges(Iterable<InternalEdge> edges);

    Collection<Mutation> fromRemovedEdges(Iterable<InternalEdge> edges);
}
