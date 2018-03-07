package com.agraphdb.core.traversal;

import com.agraphdb.core.Direction;
import com.agraphdb.core.Edge;
import com.agraphdb.core.GraphSession;
import com.agraphdb.core.Vertex;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * TODO: Class description here.
 *
 * @author <a href="https://github.com/tjeubaoit">tjeubaoit</a>
 */
public class BiDirectionBfsTraversal extends AbstractTraversal {

    private final Queue<Vertex> queue = new LinkedBlockingQueue<>();
    private final Set<Vertex> visitedVertices = new HashSet<>();
    private final Set<Edge> visitedEdges = new HashSet<>();
    private Iterator<Edge> currentEdges;
    private Vertex currentVertex;

    public BiDirectionBfsTraversal(GraphSession session, Vertex root, Condition condition) {
        super(session, Direction.BOTH, condition, root);
        this.queue.add(root);
        this.visitedVertices.add(root);
        this.currentVertex = root;
    }

    public BiDirectionBfsTraversal(GraphSession session, Vertex root) {
        this(session, root, new AlwaysTrueCondition());
    }

    @Override
    public Step nextStep() {
        Step step = nextStep0();
        if (step != null) return step;

        while (!queue.isEmpty()) {
            currentVertex = queue.poll();
            currentEdges = getSession().edges(currentVertex, getDirection()).iterator();
            step = nextStep0();
            if (step != null) return step;
        }
        return null;
    }

    private Step nextStep0() {
        while (currentEdges != null && currentEdges.hasNext()) {
            Edge edge = currentEdges.next();
            Direction d;
            Vertex vertex;
            if (currentVertex.equals(edge.outVertex())) {
                d = Direction.OUT;
                vertex = edge.inVertex();
            } else {
                d = Direction.IN;
                vertex = edge.outVertex();
            }

            if (getCondition().isValidStep(edge)) {
                if (visitedVertices.add(vertex) && !"tag".equals(vertex.label())) {
                    queue.add(vertex);
                }
                if (visitedEdges.add(edge)) return new Step(edge, vertex, d);
            }
        }
        return null;
    }
}
