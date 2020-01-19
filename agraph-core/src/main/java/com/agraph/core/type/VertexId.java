package com.agraph.core.type;

import com.agraph.common.tuple.Tuple2;
import com.agraph.common.util.Base64s;
import com.agraph.common.util.Strings;
import com.agraph.exc.SerializationException;
import com.google.common.base.Preconditions;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.apache.tinkerpop.gremlin.structure.Vertex;

import java.util.UUID;

/**
 * @author <a href="https://github.com/tjeubaoit">tjeubaoit</a>
 */
@Accessors(fluent = true)
public class VertexId extends ElementId {

    @Getter
    private final DataType type;
    @Getter
    private final Object value;
    @Getter
    private final String label;
    private String cache;

    public VertexId(Object value, String label) {
        Preconditions.checkArgument(
                !Strings.containsOnce(label, VERTEX_SEPARATOR, EDGE_SEPARATOR),
                "Label can not contains illegal characters: ['%s', '%s']",
                VERTEX_SEPARATOR, EDGE_SEPARATOR);

        if (Types.isLong(value)) {
            this.type = DataType.LONG;
        } else if (value.getClass().isAssignableFrom(String.class)) {
            Preconditions.checkArgument(
                    !value.toString().contains(VERTEX_SEPARATOR),
                    "String ID can't contains illegal characters: ['%s', '%s']",
                    VERTEX_SEPARATOR, EDGE_SEPARATOR);
            this.type = DataType.STRING;
        } else if (value.getClass().isAssignableFrom(UUID.class)) {
            this.type = DataType.UUID;
        } else {
            throw Vertex.Exceptions.userSuppliedIdsOfThisTypeNotSupported();
        }

        this.value = value;
        this.label = label;
    }

    public VertexId(DataType type, Object value, String label) {
        this.type = type;
        this.value = value;
        this.label = label;
    }

    @Override
    public String asString() {
        if (cache == null) {
            byte[] bytes = Types.encode(this.type, this.value);
            cache = label + VERTEX_SEPARATOR + Base64s.encodeAsString(bytes, false);
        }
        return cache;
    }

    @Override
    public byte[] asBytes() {
        return Types.encode(this.type, this.value);
    }

    @Override
    public boolean isEdge() {
        return false;
    }

    @Override
    public boolean isVertex() {
        return true;
    }

    public static VertexId fromBytes(byte[] bytes) {
        return fromString(Strings.fromBytes(bytes));
    }

    public static VertexId fromString(String id) {
        try {
            String[] parts = id.split(VERTEX_SEPARATOR);
            byte[] val = Base64s.decode(parts[1], false);
            Tuple2<DataType, Object> tuple2 = Types.decode(val);
            return new VertexId(tuple2._1(), tuple2._2(), parts[0]);
        } catch (Exception e) {
            throw new SerializationException(Strings.format("Could not parse vertex Id: %s", id), e);
        }
    }
}
