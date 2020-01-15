package com.agraph.core.type;

import com.agraph.core.serialize.BytesBuffer;
import com.agraph.common.tuple.Tuple2;
import com.agraph.exc.SerializationException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Date;
import java.util.UUID;

public class Types {

    private static final int DEFAULT_CAPACITY = 32;

    public static byte[] toBytes(DataType type, Object value) {
        final BytesBuffer buffer = BytesBuffer.allocate(DEFAULT_CAPACITY);
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

    public static Tuple2<DataType, Object> fromBytes(byte[] bytes) {
        final BytesBuffer buffer = BytesBuffer.wrap(bytes);
        final byte code = buffer.get();
        final Object val;
        final DataType type;

        if (DataType.STRING.code == code) {
            val = buffer.getString();
            type = DataType.STRING;

        } else if (DataType.RAW.code == code) {
            val = buffer.getVBytes();
            type = DataType.RAW;

        } else if (DataType.LONG.code == code) {
            val = buffer.getLong();
            type = DataType.LONG;

        } else if (DataType.INT.code == code) {
            val = buffer.getInt();
            type = DataType.INT;

        } else if (DataType.UUID.code == code) {
            val = buffer.getUUID();
            type = DataType.UUID;

        } else if (DataType.DATE.code == code) {
            val = buffer.getDate();
            type = DataType.DATE;

        } else if (DataType.BOOLEAN.code == code) {
            val = buffer.getBoolean();
            type = DataType.BOOLEAN;

        } else if (DataType.FLOAT.code == code) {
            val = buffer.getFloat();
            type = DataType.FLOAT;

        } else if (DataType.DOUBLE.code == code) {
            val = buffer.getDouble();
            type = DataType.DOUBLE;

        } else if (DataType.OBJECT.code == code) {
            val = readObjectStandardJava(buffer.getVBytes());
            type = DataType.OBJECT;
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
}
