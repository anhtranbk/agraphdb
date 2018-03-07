package com.vcc.bigdata.graphdb.traversal;

import com.vcc.bigdata.graphdb.Direction;
import com.vcc.bigdata.graphdb.Edge;
import com.vcc.bigdata.graphdb.GraphSession;
import com.vcc.bigdata.graphdb.Vertex;

/**
 * TODO: Class description here.
 *
 * @author <a href="https://github.com/tjeubaoit">tjeubaoit</a>
 */
public interface Traversal {

    Step nextStep();

    static Traversal create(GraphSession session,
                            Algorithm algorithm,
                            Vertex root) {
        return create(session, algorithm, root, Direction.OUT, new AlwaysTrueCondition());
    }

    static Traversal create(GraphSession session,
                            Algorithm algorithm,
                            Vertex root,
                            Direction direction,
                            Condition condition) {
        switch (algorithm) {
            case BFS:
                return new BfsTraversal(session, direction, condition, root);
            case DFS:
                return new DfsTraversal(session, direction, condition, root);
            default:
                throw new IllegalArgumentException("Invalid algorithm");
        }
    }

    enum Algorithm {
        BFS,
        DFS
    }

    class AlwaysTrueCondition implements Condition {

        @Override
        public boolean isValidStep(Edge edge) {
            return true;
        }
    }

    interface Condition {
        boolean isValidStep(Edge edge);
    }
}
