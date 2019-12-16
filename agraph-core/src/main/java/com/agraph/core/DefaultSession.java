package com.agraph.core;

import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import com.google.common.util.concurrent.ListenableFuture;
import com.agraph.common.concurrency.FutureAdapter;
import com.agraph.common.utils.IterableAdapter;
import com.agraph.common.utils.Utils;
import com.agraph.core.repository.EdgeRepository;
import com.agraph.core.repository.RepositoryFactory;
import com.agraph.core.repository.VertexRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * TODO: Class description here.
 *
 * @author <a href="https://github.com/tjeubaoit">tjeubaoit</a>
 */
public class DefaultSession implements GraphSession {

    private final EdgeRepository edgeRepository;
    private final VertexRepository vertexRepository;

    public DefaultSession(RepositoryFactory factory) {
        this.edgeRepository = factory.edgeRepository();
        this.vertexRepository = factory.vertexRepository();
    }

    @Override
    public EdgeRepository edgeRepository() {
        return this.edgeRepository;
    }

    @Override
    public VertexRepository vertexRepository() {
        return this.vertexRepository;
    }

    @Override
    public Edge edge(String label, Vertex outVertex, Vertex inVertex) {
        return edgeRepository.findOne(Edge.create(label, outVertex, inVertex));
    }

    @Override
    public EdgeSet edges(Vertex vertex, Direction direction, String... edgeLabels) {
        Iterable<Edge> results;
        if (direction.equals(Direction.BOTH)) {
            results = Iterables.concat(
                    findEdges(vertex, Direction.OUT, edgeLabels),
                    findEdges(vertex, Direction.IN, edgeLabels));
        } else {
            results = findEdges(vertex, direction, edgeLabels);
        }
        return EdgeSet.convert(results);
    }

    @Override
    public EdgeSet edgesByAdjVertexLabels(Vertex vertex, Direction direction, String... adjVertexLabels) {
        Set<String> lbSets = new HashSet<>(Arrays.asList(adjVertexLabels));
        Direction[] directions = direction.equals(Direction.BOTH)
                ? new Direction[]{Direction.IN, Direction.OUT} : new Direction[]{direction};

        List<Iterable<Edge>> iterableList = new ArrayList<>(2);
        for (Direction d : directions) {
            Iterable<Edge> edges = rx.Observable.from(findEdges(vertex, d))
                    .filter(edge -> {
                        Vertex adj = Direction.OUT.equals(d) ? edge.inVertex() : edge.outVertex();
                        return lbSets.isEmpty() || lbSets.contains(adj.label());
                    })
                    .toBlocking().toIterable();
            iterableList.add(edges);
        }
        return EdgeSet.convert(Iterables.concat(iterableList));
    }

    private Iterable<Edge> findEdges(Vertex vertex, Direction direction, String... edgeLabels) {
        Preconditions.checkArgument(Utils.notEquals(direction, Direction.BOTH));

        if (edgeLabels.length == 0) {
            return edgeRepository.findByVertex(vertex, direction, null);
        } else if (edgeLabels.length == 1) {
            return edgeRepository.findByVertex(vertex, direction, edgeLabels[0]);
        }

        List<Iterable<Edge>> iterableList = new ArrayList<>(edgeLabels.length);
        for (String label : edgeLabels) {
            iterableList.add(edgeRepository.findByVertex(vertex, direction, label));
        }
        return Iterables.concat(iterableList);
    }

    @Override
    public ListenableFuture<EdgeSet> addEdges(long ts, Collection<Edge> edges) {
        return FutureAdapter.from(edgeRepository.saveAll(edges), EdgeSet::convert);
    }

    @Override
    public ListenableFuture<EdgeSet> removeEdge(long ts, String label, Vertex outVertex, Vertex inVertex) {
        Edge edge = Edge.create(label, outVertex, inVertex);
        return FutureAdapter.from(edgeRepository.delete(edge), EdgeSet::convert);
    }

    @Override
    public VertexSet vertices(String... labels) {
        if (labels.length == 0) return VertexSet.convert(vertexRepository.findAll());

        List<Iterable<Vertex>> listVertices = new ArrayList<>(labels.length);
        for (String label : labels) {
            listVertices.add(vertexRepository.findByLabel(label));
        }
        return VertexSet.convert(Iterables.concat(listVertices));
    }

    @Override
    public Optional<Vertex> vertex(String id, String label) {
        Vertex vertex = vertexRepository.findOne(label, id);
        return vertex != null ? Optional.of(vertex) : Optional.empty();
    }

    @Override
    public VertexSet vertices(Vertex vertex, Direction direction, String... edgeLabels) {
        return VertexSet.convert(IterableAdapter.from(
                edges(vertex, direction, edgeLabels),
                edge -> edge.outVertex().equals(vertex) ? edge.inVertex() : edge.outVertex()));
    }

    @Override
    public VertexSet verticesByAdjVertexLabels(Vertex vertex, Direction direction,
                                               String... adjVertexLabels) {
        return VertexSet.convert(IterableAdapter.from(
                edgesByAdjVertexLabels(vertex, direction, adjVertexLabels),
                edge -> edge.outVertex().equals(vertex) ? edge.inVertex() : edge.outVertex()));
    }

    @Override
    public ListenableFuture<VertexSet> addVertices(long ts, Collection<Vertex> vertices) {
        return FutureAdapter.from(vertexRepository.saveAll(vertices), VertexSet::convert);
    }

    @Override
    public ListenableFuture<VertexSet> deleteVertex(long ts, String id, String label) {
        return FutureAdapter.from(vertexRepository.delete(Vertex.create(id, label)), VertexSet::convert);
    }

    @Override
    public void close() {
        edgeRepository.close();
        vertexRepository.close();
    }
}
