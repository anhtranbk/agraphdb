package com.vcc.bigdata.graphdb.traversal;

import com.vcc.bigdata.graphdb.Direction;
import com.vcc.bigdata.graphdb.GraphSession;
import com.vcc.bigdata.graphdb.Vertex;

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
