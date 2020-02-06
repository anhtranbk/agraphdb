package com.agraph.core.type;

import com.agraph.common.util.Strings;
import com.agraph.core.serialize.SerializationException;
import com.google.common.base.Preconditions;
import lombok.Getter;
import lombok.experimental.Accessors;

@Accessors(fluent = true)
public class EdgeId extends ElementId {

    @Getter
    private final String label;
    @Getter
    private final VertexId outVertexId, inVertexId;
    private String cache;

    public EdgeId(String label, VertexId outVertexId, VertexId inVertexId) {
        Preconditions.checkArgument(
                !Strings.containsOnce(label, VERTEX_SEPARATOR, EDGE_SEPARATOR),
                "Label can not contains illegal characters: ['%s', '%s']",
                VERTEX_SEPARATOR, EDGE_SEPARATOR);

        this.label = label;
        this.outVertexId = outVertexId;
        this.inVertexId = inVertexId;
    }

    @Override
    public String asString() {
        if (cache == null) {
            this.cache = outVertexId.asString()
                    + EDGE_SEPARATOR + label + EDGE_SEPARATOR
                    + inVertexId.asString();
        }
        return this.cache;
    }

    @Override
    public DataType type() {
        return DataType.OBJECT;
    }

    @Override
    public boolean isEdge() {
        return true;
    }

    @Override
    public boolean isVertex() {
        return false;
    }

    public static EdgeId fromString(String id) {
        try {
            String[] parts = id.split(EDGE_SEPARATOR);
            String label = parts[1];
            String ov = parts[0];
            String iv = parts[2];

            VertexId ovId = VertexId.fromString(ov);
            VertexId ivId = VertexId.fromString(iv);

            return new EdgeId(label, ovId, ivId);
        } catch (Exception e) {
            throw new SerializationException(Strings.format("Could not parse edge Id: %s", id), e);
        }
    }
}
