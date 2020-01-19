package com.agraph.core.serialize;

import com.agraph.core.type.DataType;
import com.agraph.core.type.VertexId;
import com.agraph.exc.SerializationException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;

import static com.agraph.core.type.DataType.BOOLEAN;
import static com.agraph.core.type.DataType.DATE;
import static com.agraph.core.type.DataType.DOUBLE;
import static com.agraph.core.type.DataType.FLOAT;
import static com.agraph.core.type.DataType.INT;
import static com.agraph.core.type.DataType.LONG;
import static com.agraph.core.type.DataType.OBJECT;
import static com.agraph.core.type.DataType.RAW;
import static com.agraph.core.type.DataType.STRING;
import static com.agraph.core.type.DataType.UUID;

@SuppressWarnings("unchecked")
public interface Serializer {

    default byte[] write(VertexId vId) {
        return write(vId.type(), vId.value());
    }

    default byte[] write(long val) {
        return write(LONG, val);
    }

    default byte[] write(String val) {
        return write(STRING, val);
    }

    default byte[] write(java.util.UUID val) {
        return write(UUID, val);
    }

    default byte[] write(int val) {
        return write(INT, val);
    }

    default byte[] write(double val) {
        return write(DOUBLE, val);
    }

    default byte[] write(float val) {
        return write(FLOAT, val);
    }

    default byte[] write(boolean val) {
        return write(BOOLEAN, val);
    }

    default byte[] write(Date val) {
        return write(DATE, val);
    }

    default <T> void writeUnsafe(T obj, OutputStream out) {
        try {
            byte[] serialized = writeUnsafe(obj);
            out.write(serialized);
        } catch (IOException e) {
            throw new SerializationException(e);
        }
    }

    default <T> byte[] writeUnsafe(T obj) {
        Class<?> type = obj.getClass();
        if (type.isAssignableFrom(Long.class)) {
            return write(LONG, obj);
        } else if (type.isAssignableFrom(byte[].class)) {
            return write(RAW, obj);
        } else if (type.isAssignableFrom(String.class)) {
            return write(STRING, obj);
        } else if (type.isAssignableFrom(Integer.class)) {
            return write(INT, obj);
        } else if (type.isAssignableFrom(Date.class)) {
            return write(DATE, obj);
        } else if (type.isAssignableFrom(java.util.UUID.class)) {
            return write(UUID, obj);
        } else if (type.isAssignableFrom(Float.class)) {
            return write(FLOAT, obj);
        } else if (type.isAssignableFrom(Double.class)) {
            return write(DOUBLE, obj);
        } else if (type.isAssignableFrom(Boolean.class)) {
            return write(BOOLEAN, obj);
        } else {
            return write(OBJECT, obj);
        }
    }

    byte[] write(DataType type, Object obj);

    default <T> T readUnchecked(byte[] serialized) {
        try {
            return (T) read(serialized);
        } catch (ClassCastException e) {
            throw new SerializationException(e);
        }
    }

    default Object read(InputStream in) {
        BytesBuffer buffer = BytesBuffer.allocate(512);
        int nBytes;
        while (true) {
            try {
                byte[] bytes = new byte[buffer.asByteBuffer().capacity()];
                nBytes = in.read(bytes);
                if (nBytes < 0) break;
                buffer.put(bytes, 0, nBytes);
            } catch (IOException e) {
                throw new SerializationException(e);
            }
        }
        return read(buffer.bytes());
    }

    Object read(byte[] serialized);
}
