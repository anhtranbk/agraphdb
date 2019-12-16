package com.agraph.core.traversal;

import com.agraph.core.Direction;
import com.agraph.core.GraphSession;
import com.agraph.core.Vertex;

/**
 * TODO: Class description here.
 *
 * @author <a href="https://github.com/tjeubaoit">tjeubaoit</a>
 */
public class DfsTraversal extends AbstractTraversal {

    public DfsTraversal(GraphSession session, Direction direction, Condition condition, Vertex root) {
        super(session, direction, condition, root);
    }

    @Override
    public Step nextStep() {
        throw new UnsupportedOperationException();
    }
}
