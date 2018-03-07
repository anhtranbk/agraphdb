package com.vcc.bigdata.platform.hbase;

import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.util.Bytes;

import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * TODO: Class description here.
 *
 * @author <a href="https://github.com/tjeubaoit">tjeubaoit</a>
 */
public class HBaseUtils {

    public static final byte[] DEFAULT_DELIMITER = "|".getBytes();
    public static final byte[] EMPTY = new byte[0];
    public static final byte DELIMITER_REPLICATOR = " ".getBytes()[0];
    public static final int DEFAULT_MAX_BUCKET = 1000;

    public static <T> void putSet(byte[] family, byte[] name, Put put, Set<T> set) {
        for (T e : set) {
            byte[] qualifier = copyByteArrays(name, DEFAULT_DELIMITER, toBytes(e));
            put.addColumn(family, qualifier, EMPTY);
        }
    }

    public static <K, V> void putMap(byte[] family, byte[] name, Put put, Map<K, V> map) {
        for (Map.Entry<K, V> e : map.entrySet()) {
            byte[] qualifier = copyByteArrays(name, DEFAULT_DELIMITER, toBytes(e.getKey()));
            put.addColumn(family, qualifier, toBytes(e.getValue()));
        }
    }

    public static <T> void putList(byte[] family, byte[] name, Put put, List<T> list) {
        for (T e : list) {
            byte[] qualifier = copyByteArrays(name, DEFAULT_DELIMITER, toBytes(System.currentTimeMillis()));
            put.addColumn(family, qualifier, toBytes(e));
        }
    }

    public static <K, V> Put putByCompositeKey(byte[] family, Map<K, V> data, byte[]... compositeKeys) {
        Put put = new Put(createCompositeKey(compositeKeys));
        for (Map.Entry<K, V> e : data.entrySet()) {
            byte[] qualifier = toBytes(e.getKey());
            put.addColumn(family, qualifier, toBytes(e.getValue()));
        }
        return put;
    }

    public static Scan getByCompositeKey(byte[] family, byte[]... compositeKeys) {
        Scan scan = new Scan();
        scan.setRowPrefixFilter(createCompositeKey(compositeKeys));
        return scan;
    }

    public static <T> Set<T> getSet(byte[] family, byte[] name, Result res, Class<T> type) {
        boolean check = false;
        Set<T> set = new LinkedHashSet<>();
        for (Map.Entry<byte[], byte[]> e : res.getFamilyMap(family).entrySet()) {
            if (isColumnInCollection(e.getKey(), name)) {
                byte[] val = extractWideColumnValue(e.getKey(), name);
                set.add(fromBytes(val, type));
                if (!check) check = true;
                continue;
            }

            if (check) break;
        }
        return set;
    }

    public static <K, V> Map<K, V> getMap(byte[] family, byte[] name, Result res,
                                          Class<K> keyType, Class<V> valueType) {
        boolean check = false;
        Map<K, V> map = new LinkedHashMap<>();
        for (Map.Entry<byte[], byte[]> e : res.getFamilyMap(family).entrySet()) {
            if (isColumnInCollection(e.getKey(), name)) {
                byte[] val = extractWideColumnValue(e.getKey(), name);
                map.put(fromBytes(val, keyType), fromBytes(e.getValue(), valueType));
                if (!check) check = true;
                continue;
            }

            if (check) break;
        }
        return map;
    }

    public static <T> List<T> getList(byte[] family, byte[] name, Result res, Class<T> type) {
        boolean check = false;
        List<T> list = new LinkedList<>();
        for (Map.Entry<byte[], byte[]> e : res.getFamilyMap(family).entrySet()) {
            if (isColumnInCollection(e.getKey(), name)) {
                list.add(fromBytes(e.getValue(), type));
                if (!check) check = true;
                continue;
            }

            if (check) break;
        }
        return list;
    }

    private static byte[] extractWideColumnValue(byte[] qualifier, byte[] prefix) {
        byte[] val = new byte[qualifier.length - prefix.length - 1];
        System.arraycopy(qualifier, prefix.length + 1, val, 0, val.length);
        return val;
    }

    private static boolean isColumnInCollection(byte[] qualifier, byte[] name) {
        return startsWith(qualifier, name) && qualifier[name.length] == DEFAULT_DELIMITER[0];
    }

    public static <T> byte[] toBytes(T obj) {
        Class<?> type = obj.getClass();
        if (type.isAssignableFrom(Long.class)) {
            return Bytes.toBytes((Long) obj);
        } else if (type.isAssignableFrom(Integer.class)) {
            return Bytes.toBytes((Integer) obj);
        } else if (type.isAssignableFrom(Short.class)) {
            return Bytes.toBytes((Short) obj);
        } else if (type.isAssignableFrom(Float.class)) {
            return Bytes.toBytes((Float) obj);
        } else if (type.isAssignableFrom(Double.class)) {
            return Bytes.toBytes((Double) obj);
        } else if (type.isAssignableFrom(String.class)) {
            return Bytes.toBytes((String) obj);
        } else if (type.isAssignableFrom(BigDecimal.class)) {
            return Bytes.toBytes((BigDecimal) obj);
        } else if (type.isAssignableFrom(Boolean.class)) {
            return Bytes.toBytes(((Boolean) obj));
        } else if (type.isAssignableFrom(ByteBuffer.class)) {
            return Bytes.toBytes((ByteBuffer) obj);
        } else throw new UnsupportedOperationException("Do not support complex type");
    }

    @SuppressWarnings("unchecked")
    public static <T> T fromBytes(byte[] bytes, Class<T> type) {
        Object res;
        if (type.isAssignableFrom(Long.class)) {
            res = Bytes.toLong(bytes);
        } else if (type.isAssignableFrom(Integer.class)) {
            res = Bytes.toInt(bytes);
        } else if (type.isAssignableFrom(Short.class)) {
            res = Bytes.toShort(bytes);
        } else if (type.isAssignableFrom(Float.class)) {
            res = Bytes.toFloat(bytes);
        } else if (type.isAssignableFrom(Double.class)) {
            res = Bytes.toDouble(bytes);
        } else if (type.isAssignableFrom(String.class)) {
            res = Bytes.toString(bytes);
        } else if (type.isAssignableFrom(BigDecimal.class)) {
            res = Bytes.toBigDecimal(bytes);
        } else if (type.isAssignableFrom(Boolean.class)) {
            res = Bytes.toBoolean(bytes);
        } else if (type.isAssignableFrom(ByteBuffer.class)) {
            res = ByteBuffer.wrap(bytes);
        } else throw new UnsupportedOperationException("Do not support complex type");

        return (T) res;
    }

    public static byte[] createCompositeKey(byte[]... keys) {
        return createCompositeKey(DEFAULT_DELIMITER[0], keys);
    }

    public static byte[] createCompositeKey(byte delimiter, byte[]... keys) {
        int length = (keys.length - 1);
        for (byte[] bytes : keys) {
            length += bytes.length;
            for (int i = 0; i < bytes.length; i++) {
                if (bytes[i] == DEFAULT_DELIMITER[0]) {
                    bytes[i] = DELIMITER_REPLICATOR;
                }
            }
        }

        ByteBuffer buffer = ByteBuffer.allocate(length);
        for (int i = 0; i < keys.length; i++) {
            if (i > 0) buffer.put(delimiter);
            buffer.put(keys[i]);
        }

        return buffer.array();
    }

    public static List<ByteBuffer> extractCompositeKeys(byte[] compositeKey) {
        return extractCompositeKeys(DEFAULT_DELIMITER[0], compositeKey);
    }

    public static List<ByteBuffer> extractCompositeKeys(byte delimiter, byte[] compositeKey) {
        try {
            List<ByteBuffer> buffers = new ArrayList<>();
            int i = 0;
            for (int j = 0; j <= compositeKey.length; j++) {
                if (j == compositeKey.length || delimiter == compositeKey[j]) {
                    ByteBuffer buffer = ByteBuffer.allocate(j - i);
                    buffer.put(compositeKey, i, buffer.capacity());
                    i = j + 1;
                    buffers.add(buffer);
                }
            }
            return buffers;
        } catch (RuntimeException e) {
            throw new IllegalArgumentException("Not a valid composite key");
        }
    }

    public static byte[] copyByteArrays(Collection<byte[]> sources) {
        int length = 0;
        for (byte[] b : sources) {
            length += b.length;
        }

        byte[] dst = new byte[length];
        int i = 0;
        for (byte[] b : sources) {
            System.arraycopy(b, 0, dst, i, b.length);
            i += b.length;
        }

        return dst;
    }

    public static byte[] copyByteArrays(byte[]... sources) {
        return copyByteArrays(Arrays.asList(sources));
    }

    /**
     * Does this byte array begin with match array content?
     *
     * @param source Byte array to examine
     * @param match  Byte array to locate in <code>source</code>
     * @return true If the starting bytes are equal
     */
    public static boolean startsWith(byte[] source, byte[] match) {
        return startsWith(source, 0, match);
    }

    /**
     * Does this byte array begin with match array content?
     *
     * @param source Byte array to examine
     * @param offset An offset into the <code>source</code> array
     * @param match  Byte array to locate in <code>source</code>
     * @return true If the starting bytes are equal
     */
    public static boolean startsWith(byte[] source, int offset, byte[] match) {
        if (match.length > (source.length - offset)) {
            return false;
        }

        for (int i = 0; i < match.length; i++) {
            if (source[offset + i] != match[i]) {
                return false;
            }
        }
        return true;
    }

    /**
     * Build composite key that salted with a bucket
     *
     * @param maxBucket max number of buckets
     * @param seed      use to generate bucket
     * @param keys      raw keys that will be used to generate bucket prefixed composite key
     * @return new key salted with bucket
     */
    public static byte[] buildCompositeKeyWithBucket(int maxBucket, String seed, byte[]... keys) {
        byte[] bucket = String.valueOf(Math.abs(seed.hashCode() % maxBucket)).getBytes();
        byte[] key = HBaseUtils.createCompositeKey(keys);

        ByteBuffer buff = ByteBuffer.allocate(bucket.length + key.length + 1);
        buff.put(bucket);
        buff.put(DEFAULT_DELIMITER[0]);
        buff.put(key);
        return buff.array();
    }

    /**
     * Build composite key that salted with default bucket
     * {@link HBaseUtils#DEFAULT_MAX_BUCKET}
     *
     * @param seed use to generate bucket
     * @param keys raw keys that will be used to generate bucket prefixed composite key
     * @return new key salted with bucket
     */
    public static byte[] buildCompositeKeyWithBucket(String seed, byte[]... keys) {
        return buildCompositeKeyWithBucket(DEFAULT_MAX_BUCKET, seed, keys);
    }
}
