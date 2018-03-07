package com.vcc.bigdata.graphdb.traversal;

import com.google.common.base.Preconditions;
import com.vcc.bigdata.graphdb.Direction;
import com.vcc.bigdata.graphdb.GraphSession;
import com.vcc.bigdata.graphdb.Vertex;

/**
 * TODO: Class description here.
 *
 * @author <a href="https://github.com/tjeubaoit">tjeubaoit</a>
 */
public abstract class AbstractTraversal implements Traversal {

    private final GraphSession session;
    private final Direction direction;
    private final Condition condition;
    private final Vertex root;

    public AbstractTraversal(GraphSession session, Direction direction,
                             Condition condition, Vertex root) {
        Preconditions.checkNotNull(root, "Root vertex cannot be null");
        Preconditions.checkNotNull(session);

        this.session = session;
        this.direction = direction;
        this.condition = condition;
        this.root = root;
    }

    public GraphSession getSession() {
        return session;
    }

    public Direction getDirection() {
        return direction;
    }

    public Condition getCondition() {
        return condition;
    }

    public Vertex getRoot() {
        return root;
    }
}
