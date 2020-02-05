package com.agraph.core;

import com.agraph.AGraphTransaction;
import com.agraph.State;
import com.agraph.core.type.EdgeId;
import com.agraph.core.type.VertexId;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.apache.tinkerpop.gremlin.structure.Vertex;

/**
 * @author <a href="https://github.com/tjeubaoit">tjeubaoit</a>
 */
public class ElementBuilders {

    @Accessors(fluent = true, chain = true)
    @Setter
    public static class VertexBuilder {
        private AGraphTransaction tx;
        private VertexId id;
        private String label = Vertex.DEFAULT_LABEL;
        private State state = State.NEW;

        public VertexBuilder from(InternalVertex that) {
            this.tx = that.tx();
            this.id = that.id();
            this.label = that.label();
            this.state = that.state();
            return this;
        }

        public InternalVertex build() {
            return new InternalVertex(tx, id, label, state);
        }
    }

    @Accessors(fluent = true, chain = true)
    @Setter
    public static class EdgeBuilder {
        private AGraphTransaction tx;
        private EdgeId id;
        private String label = Vertex.DEFAULT_LABEL;
        private State state = State.NEW;
        private InternalVertex outVertex, inVertex;
        private long internalId;

        public EdgeBuilder from(InternalEdge that) {
            this.tx = that.tx();
            this.id = that.id();
            this.label = that.label();
            this.state = that.state();
            this.inVertex = that.inVertex();
            this.outVertex = that.outVertex();
            return this;
        }

        public InternalEdge build() {
            if (id == null) {
                id = EdgeId.create(label, outVertex, inVertex);
            }
            InternalEdge edge = new InternalEdge(tx, id, label, state, outVertex, inVertex);
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
