package com.agraph.core;

import com.agraph.AGraphTransaction;
import com.agraph.State;
import com.agraph.core.type.EdgeId;
import com.agraph.core.type.VertexId;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.apache.tinkerpop.gremlin.structure.Vertex;

import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="https://github.com/tjeubaoit">tjeubaoit</a>
 */
public class ElementBuilders {

    @Accessors(fluent = true, chain = true)
    @Setter
    static class VertexBuilder {
        private AGraphTransaction tx;
        private VertexId id;
        private String label = Vertex.DEFAULT_LABEL;
        private State state = State.NEW;
        private Map<String, AGraphVertexProperty<?>> properties = new HashMap<>();

        public VertexBuilder from(InternalVertex that) {
            this.tx = that.tx();
            this.id = that.id();
            this.label = that.label();
            this.state = that.state();
            this.properties(that.asPropertiesMap());
            return this;
        }

        public VertexBuilder property(AGraphVertexProperty<?> prop) {
            this.properties.put(prop.key, prop);
            return this;
        }

        public VertexBuilder properties(Map<String, ? extends AGraphProperty<?>> props) {
            for (AGraphProperty<?> prop : props.values()) {
                assert prop instanceof AGraphVertexProperty<?>;
                this.properties.put(prop.key, (AGraphVertexProperty<?>) prop);
            }
            return this;
        }

        public InternalVertex build() {
            return new InternalVertex(tx, id, label, state, properties);
        }
    }

    @Accessors(fluent = true, chain = true)
    @Setter
    static class EdgeBuilder {
        private AGraphTransaction tx;
        private EdgeId id;
        private String label = Vertex.DEFAULT_LABEL;
        private State state = State.NEW;
        private Map<String, AGraphEdgeProperty<?>> properties = new HashMap<>();
        private InternalVertex outVertex, inVertex;
        private long internalId;

        public EdgeBuilder from(InternalEdge that) {
            this.tx = that.tx();
            this.id = that.id();
            this.label = that.label();
            this.state = that.state();
            this.inVertex = that.inVertex();
            this.outVertex = that.outVertex();
            this.properties(that.asPropertiesMap());
            return this;
        }

        public EdgeBuilder property(AGraphEdgeProperty<?> prop) {
            this.properties.put(prop.key, prop);
            return this;
        }

        public EdgeBuilder properties(Map<String, ? extends AGraphProperty<?>> props) {
            for (AGraphProperty<?> prop : props.values()) {
                assert prop instanceof AGraphEdgeProperty<?>;
                this.properties.put(prop.key, (AGraphEdgeProperty<?>) prop);
            }
            return this;
        }

        public InternalEdge build() {
            if (id == null) {
                id = EdgeId.create(label, outVertex, inVertex);
            }
            InternalEdge edge = new InternalEdge(tx, id, label, state, outVertex, inVertex, properties);
            edge.assignInternalId(internalId);
            return edge;
        }
    }

    public static VertexBuilder vertexBuilder() {
        return new VertexBuilder();
    }

    public static EdgeBuilder edgeBuilder() {
        return new EdgeBuilder();
    }
}
