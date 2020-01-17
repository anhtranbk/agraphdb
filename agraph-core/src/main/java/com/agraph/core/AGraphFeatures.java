package com.agraph.core;

import com.agraph.AGraph;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.VertexProperty;
import org.apache.tinkerpop.gremlin.structure.util.StringFactory;

public class AGraphFeatures implements Graph.Features {

    private final AGraph graph;

    public AGraphFeatures(AGraph graph) {
        this.graph = graph;
    }

    @Override
    public GraphFeatures graph() {
        return new GraphFeatures();
    }

    @Override
    public AGraphVertexFeatures vertex() {
        return new AGraphVertexFeatures();
    }

    @Override
    public AGraphEdgeFeatures edge() {
        return new AGraphEdgeFeatures();
    }

    @Override
    public String toString() {
        return StringFactory.featureString(this);
    }

    private static class AGraphDataTypeFeatures implements Graph.Features.DataTypeFeatures {
        @Override
        public boolean supportsMapValues() {
            return false;
        }

        @Override
        public boolean supportsMixedListValues() {
            return false;
        }

        @Override
        public boolean supportsBooleanArrayValues() {
            return false;
        }

        @Override
        public boolean supportsByteArrayValues() {
            return false;
        }

        @Override
        public boolean supportsDoubleArrayValues() {
            return false;
        }

        @Override
        public boolean supportsFloatArrayValues() {
            return false;
        }

        @Override
        public boolean supportsIntegerArrayValues() {
            return false;
        }

        @Override
        public boolean supportsStringArrayValues() {
            return false;
        }

        @Override
        public boolean supportsLongArrayValues() {
            return false;
        }

        @Override
        public boolean supportsSerializableValues() {
            return false;
        }

        @Override
        public boolean supportsUniformListValues() {
            return false;
        }
    }

    private static class AGraphElementFeatures implements Graph.Features.ElementFeatures {

        @Override
        public boolean supportsCustomIds() {
            return false;
        }

        @Override
        public boolean supportsAnyIds() {
            return false;
        }
    }

    private static class AGraphEdgePropertyFeatures extends AGraphDataTypeFeatures
            implements Graph.Features.EdgePropertyFeatures {
    }

    private static class AGraphVertexPropertyFeatures extends AGraphDataTypeFeatures
            implements Graph.Features.VertexPropertyFeatures {

        @Override
        public boolean supportsCustomIds() {
            return false;
        }

        @Override
        public boolean supportsAnyIds() {
            return false;
        }
    }

    private static class AGraphEdgeFeatures extends AGraphElementFeatures
            implements Graph.Features.EdgeFeatures {

        @Override
        public boolean supportsUpsert() {
            return false;
        }

        @Override
        public Graph.Features.EdgePropertyFeatures properties() {
            return new AGraphEdgePropertyFeatures();
        }
    }

    private static class AGraphVertexFeatures extends AGraphElementFeatures
            implements Graph.Features.VertexFeatures {

        @Override
        public VertexProperty.Cardinality getCardinality(String key) {
            return VertexProperty.Cardinality.single;
        }

        @Override
        public boolean supportsDuplicateMultiProperties() {
            return false;
        }

        @Override
        public boolean supportsUpsert() {
            return false;
        }

        @Override
        public VertexPropertyFeatures properties() {
            return new AGraphVertexPropertyFeatures();
        }
    }

    private static class GraphFeatures implements Graph.Features.GraphFeatures {

        @Override
        public boolean supportsComputer() {
            return false;
        }

        @Override
        public boolean supportsPersistence() {
            return true;
        }
    }
}
