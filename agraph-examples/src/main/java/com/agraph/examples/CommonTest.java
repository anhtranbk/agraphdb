package com.agraph.examples;

import com.agraph.core.type.EdgeId;
import com.agraph.core.type.VertexId;
import com.agraph.storage.rdbms.query.Condition;
import com.agraph.storage.rdbms.query.Conditions;
import org.apache.tinkerpop.gremlin.structure.Direction;

import java.util.Arrays;
import java.util.UUID;

import static com.agraph.storage.rdbms.engine.Constants.LABEL_COL;
import static com.agraph.storage.rdbms.engine.Constants.VERTEX_DST_COL;
import static com.agraph.storage.rdbms.engine.Constants.VERTEX_SRC_COL;

public class CommonTest {

    static Condition createCondition(Direction direction, String vertexId, String... labels) {
        Condition condition;
        if (direction.equals(Direction.BOTH)) {
            Condition vertexCondition = Conditions.or(
                    Conditions.eq(VERTEX_SRC_COL, vertexId),
                    Conditions.eq(VERTEX_DST_COL, vertexId));
            condition = Conditions.and(
                    vertexCondition,
                    Conditions.in(LABEL_COL, Arrays.asList(labels)));
        } else {
            String targetCol = direction.equals(Direction.IN) ? VERTEX_DST_COL : VERTEX_SRC_COL;
            condition = Conditions.and(
                    Conditions.eq(targetCol, vertexId),
                    Conditions.in(LABEL_COL, Arrays.asList(labels)));
        }
        return condition;
    }

    static void testElementId() {
        VertexId v1 = new VertexId(System.currentTimeMillis(), "user");
        VertexId v2 = new VertexId(System.currentTimeMillis(), "user");
        VertexId v3 = new VertexId(UUID.randomUUID(), "uuid");
        VertexId v4 = new VertexId("hanoi", "location");

        System.out.println(v1);
        System.out.println(v2);
        System.out.println(v3);
        System.out.println(v4);

        VertexId v5 = VertexId.fromString(v2.asString());
        VertexId v6 = VertexId.fromBytes(v3.asBytes());

        System.out.println(v5);
        System.out.println(v6);
        System.out.println(v5.equals(v2));
        System.out.println(v6.equals(v3));

        EdgeId e1 = new EdgeId("friend", v1, v5);
        System.out.println(e1);
        System.out.println(EdgeId.fromString(e1.asString()));
        System.out.println(EdgeId.fromBytes(e1.asBytes()));
    }

    public static void main(String[] args) throws Exception {
    }
}
