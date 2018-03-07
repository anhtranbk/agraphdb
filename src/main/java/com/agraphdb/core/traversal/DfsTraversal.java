package com.agraphdb.core.traversal;

import com.agraphdb.core.Direction;
import com.agraphdb.core.GraphSession;
import com.agraphdb.core.Vertex;

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
