package com.agraph.v1.traversal;

import com.google.common.base.Preconditions;
import com.agraph.v1.Direction;
import com.agraph.v1.Edge;
import com.agraph.v1.GraphSession;
import com.agraph.v1.Vertex;

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
public class BfsTraversal extends AbstractTraversal {

    private final Queue<Vertex> queue = new LinkedBlockingQueue<>();
    private final Set<Vertex> visitedVertices = new HashSet<>();
    private Iterator<Edge> itEdges;

    public BfsTraversal(GraphSession session, Direction direction, Condition condition, Vertex root) {
        super(session, direction, condition, root);
        Preconditions.checkArgument(direction != Direction.BOTH,
                "Can not traversal graphs in both directions");

        this.queue.add(root);
        this.visitedVertices.add(root);
    }

    @Override
    public Step nextStep() {
        Direction d = getDirection();
        Step step = nextStep0();
        if (step != null) return step;

        while (!queue.isEmpty()) {
            Vertex v = queue.poll();
            itEdges = getSession().edges(v, d).iterator();
            step = nextStep0();
            if (step != null) return step;
        }
        return null;
    }

    private Step nextStep0() {
        Direction d = getDirection();
        while (itEdges != null && itEdges.hasNext()) {
            Edge edge = itEdges.next();
            Vertex vertex = d.equals(Direction.IN) ? edge.outVertex() : edge.inVertex();

            if (getCondition().isValidStep(edge)) {
                if (visitedVertices.add(vertex) && !"tag".equals(vertex.label())) {
                    queue.add(vertex);
                }
                return new Step(edge, vertex, d);
            }
        }
        return null;
    }
}
