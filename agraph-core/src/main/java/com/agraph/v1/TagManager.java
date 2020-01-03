package com.agraph.v1;

import com.google.common.util.concurrent.ListenableFuture;
import com.agraph.common.utils.IterableAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO: Class description here.
 *
 * @author <a href="https://github.com/tjeubaoit">tjeubaoit</a>
 */
public class TagManager {

    private final GraphSession session;

    public TagManager(GraphSession session) {
        this.session = session;
    }

    public ListenableFuture<VertexSet> createTags(String... tags) {
        List<Vertex> vertices = new ArrayList<>(tags.length);
        for (String tag : tags) {
            vertices.add(Vertex.create(tag, "tag"));
        }
        return session.addVertices(vertices);
    }

    public ListenableFuture<EdgeSet> addTags(long ts, Vertex vertex, String... tags) {
        List<Edge> edges = new ArrayList<>(tags.length);
        for (String tag : tags) {
            Vertex vTag = Vertex.create(tag, "tag");
            edges.add(Edge.create("tag", vertex, vTag));
        }
        return session.addEdges(ts, edges);
    }

    public ListenableFuture<EdgeSet> addTags(Vertex vertex, String... tags) {
        return addTags(-1, vertex, tags);
    }

    public ElementSet<Vertex> findAllTaggedBy(String tag) {
        Vertex vTag = Vertex.create(tag, "tag");
        return session.vertices(vTag, Direction.BOTH, "tag");
    }

    public Iterable<String> getTags(Vertex vertex){
        return IterableAdapter.from(session.vertices(vertex, Direction.OUT, "tag"),
                AbstractElement::id);
    }
}
