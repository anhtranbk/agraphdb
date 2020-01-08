package com.agraph.examples;

import com.agraph.config.Config;
import com.agraph.v1.GraphDatabase;
import com.agraph.v1.GraphSession;
import com.agraph.v1.Vertex;

/**
 * Hello world!
 */
public class App {

    public static void main(String[] args) throws Exception {
        GraphSession session = GraphDatabase.open(new Config());
        Vertex v1 = session.addVertex("1", "test").get().first();
        Vertex v2 = session.addVertex("2", "test").get().first();
        session.addEdge("wtf", v1, v2);
        session.close();
    }
}
