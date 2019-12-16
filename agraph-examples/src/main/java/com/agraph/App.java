package com.agraph;

import com.agraph.common.config.Configuration;
import com.agraph.core.GraphDatabase;
import com.agraph.core.GraphSession;
import com.agraph.core.Vertex;

/**
 * Hello world!
 */
public class App {

    public static void main(String[] args) throws Exception {
        GraphSession session = GraphDatabase.open(new Configuration());
        Vertex v1 = session.addVertex("1", "test").get().first();
        Vertex v2 = session.addVertex("2", "test").get().first();
        session.addEdge("wtf", v1, v2);
        session.close();
    }
}
