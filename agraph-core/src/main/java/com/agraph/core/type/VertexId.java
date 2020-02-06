package com.agraph.core.type;

import com.agraph.common.tuple.Tuple2;
import com.agraph.common.util.Base64s;
import com.agraph.common.util.Strings;
import com.agraph.core.serialize.SerializationException;
import com.google.common.base.Preconditions;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.apache.tinkerpop.gremlin.structure.Vertex;

import java.util.UUID;

/**
 * @author <a href="https://github.com/tjeubaoit">tjeubaoit</a>
 */
@Accessors(fluent = true)
@Getter
public class VertexId extends ElementId {

    private final DataType type;
    private final Object value;
    private final String prefix;
    @Getter(AccessLevel.NONE)
    private String cache;

    public VertexId(Object value, String label) {
        Preconditions.checkArgument(
                !Strings.containsOnce(label, VERTEX_SEPARATOR, EDGE_SEPARATOR),
                "Prefix can not contains illegal characters: ['%s', '%s']",
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
        this.prefix = label;
    }

    public VertexId(byte[] rawValue, String label) {
        Tuple2<DataType, Object> tuple2 = Types.decode(rawValue);
        this.type = tuple2._1;
        this.value = tuple2._2;
        this.prefix = label;
    }

    @Override
    public String asString() {
        if (cache == null) {
            byte[] bytes = Types.encode(this.type, this.value);
            cache = prefix + VERTEX_SEPARATOR + Base64s.encodeAsString(bytes, false);
        }
        return cache;
    }

    @Override
    public boolean isEdge() {
        return false;
    }

    @Override
    public boolean isVertex() {
        return true;
    }

    public static VertexId fromString(String id) {
        try {
            String[] parts = id.split(VERTEX_SEPARATOR);
            byte[] rawVal = Base64s.decode(parts[1], false);
            return new VertexId(rawVal, parts[0]);
        } catch (Exception e) {
            throw new SerializationException(Strings.format("Could not parse vertex Id: %s", id), e);
        }
    }
}
