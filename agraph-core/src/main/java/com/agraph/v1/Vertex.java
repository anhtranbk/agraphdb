package com.agraph.v1;

import java.util.Collections;
import java.util.Map;

/**
 * TODO: Class description here.
 *
 * @author <a href="https://github.com/tjeubaoit">tjeubaoit</a>
 */
public class Vertex extends AbstractElement {

    private Vertex(String id, String label, Map<String, ?> props) {
        super(id, label, props);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Vertex) {
            Vertex v = ((Vertex) obj);
            return v.id().equals(this.id()) && v.label().equals(this.label());
        }
        return false;
    }

    public static Vertex create(String id, String label, Map<String, ?> props) {
        return new Vertex(id, label, props);
    }

    public static Vertex create(String id, String label) {
        return new Vertex(id, label, Collections.emptyMap());
    }
}
