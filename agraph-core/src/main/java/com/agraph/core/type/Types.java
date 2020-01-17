package com.agraph.core.type;

import com.agraph.common.tuple.Tuple2;
import com.agraph.core.serialize.BytesBuffer;
import com.agraph.exc.SerializationException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Date;
import java.util.UUID;

import static com.agraph.core.type.DataType.*;

public class Types {

    private static final int DEFAULT_CAPACITY = 128;

    public static byte[] encode(String val) {
        return encode(STRING, val);
    }

    public static byte[] encode(long val) {
        return encode(LONG, val);
    }

    public static byte[] encode(int val) {
        return encode(INT, val);
    }

    public static byte[] encode(double val) {
        return encode(DOUBLE, val);
    }

    public static byte[] encode(float val) {
        return encode(FLOAT, val);
    }

    public static byte[] encode(UUID val) {
        return encode(UUID, val);
    }

    public static byte[] encode(boolean val) {
        return encode(BOOLEAN, val);
    }

    public static byte[] encode(Date val) {
        return encode(DATE, val);
    }

    public static byte[] encode(DataType type, Object value) {
        final int capacity = estimateCapacity(type, value);
        final BytesBuffer buffer = BytesBuffer.allocate(capacity);
        buffer.put(type.code);
        switch (type) {
            case STRING:
                buffer.putString((String) value);
                break;
            case RAW:
                buffer.putVBytes((byte[]) value);
                break;
            case LONG:
                buffer.putLong((long) value);
                break;
            case INT:
                buffer.putInt((int) value);
                break;
            case UUID:
                buffer.putUUID((UUID) value);
                break;
            case BOOLEAN:
                buffer.putBoolean((boolean) value);
                break;
            case FLOAT:
                buffer.putFloat((float) value);
                break;
            case DOUBLE:
                buffer.putDouble((double) value);
                break;
            case DATE:
                buffer.putDate((Date) value);
                break;
            default:
                byte[] bytes = writeObjectStandardJava(value);
                buffer.putVBytes(bytes);
                break;
        }
        return buffer.bytes();
    }

    public static Tuple2<DataType, Object> decode(byte[] bytes) {
        final BytesBuffer buffer = BytesBuffer.wrap(bytes);
        final byte code = buffer.get();
        final Object val;
        final DataType type;

        if (STRING.code == code) {
            val = buffer.getString();
            type = STRING;

        } else if (DataType.RAW.code == code) {
            val = buffer.getVBytes();
            type = RAW;

        } else if (LONG.code == code) {
            val = buffer.getLong();
            type = LONG;

        } else if (INT.code == code) {
            val = buffer.getInt();
            type = INT;

        } else if (UUID.code == code) {
            val = buffer.getUUID();
            type = UUID;

        } else if (DATE.code == code) {
            val = buffer.getDate();
            type = DATE;

        } else if (BOOLEAN.code == code) {
            val = buffer.getBoolean();
            type = BOOLEAN;

        } else if (FLOAT.code == code) {
            val = buffer.getFloat();
            type = FLOAT;

        } else if (DOUBLE.code == code) {
            val = buffer.getDouble();
            type = DOUBLE;

        } else if (OBJECT.code == code) {
            val = readObjectStandardJava(buffer.getVBytes());
            type = OBJECT;
        } else throw new IllegalArgumentException("Invalid byte array format, malformed code value");

        return new Tuple2<>(type, val);
    }

    public static byte[] writeObjectStandardJava(Object object) {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(object);
            return bos.toByteArray();
        } catch (IOException e) {
            throw new SerializationException(e);
        }
    }

    public static Object readObjectStandardJava(byte[] bytes) {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes)) {
            ObjectInputStream ois = new ObjectInputStream(bis);
            return ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new SerializationException(e);
        }
    }

    public static boolean isLong(Object object) {
        try {
            long l = (Long) object;
            return true;
        } catch (ClassCastException e) {
            return false;
        }
    }

    public static boolean isInt(Object object) {
        try {
            int l = (Integer) object;
            return true;
        } catch (ClassCastException e) {
            return false;
        }
    }

    private static int estimateCapacity(DataType type, Object val) {
        final int capacity;
        if (type == LONG || type == DOUBLE || type == INT || type == FLOAT || type == DATE) {
            capacity = 8;
        } else if (type == UUID) {
            capacity = 16;
        } else if (type == BOOLEAN) {
            capacity = 1;
        } else if (type == RAW) {
            capacity = ((byte[]) val).length;
        } else if (type == STRING) {
            capacity = (int) Math.round(val.toString().length() * 1.5);
        } else capacity = DEFAULT_CAPACITY;
        // add one byte for type code
        return capacity + 1;
    }
}
