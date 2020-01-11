package com.agraph.common.util;

import com.google.common.primitives.UnsignedBytes;

import java.util.Arrays;
import java.util.Comparator;

public class Bytes {

    public static final long BASE = 1024L;
    public static final long KB = BASE;
    public static final long MB = KB * BASE;
    public static final long GB = MB * BASE;

    private static final Comparator<byte[]> CMP = UnsignedBytes.lexicographicalComparator();

    public static int compare(byte[] bytes1, byte[] bytes2) {
        return CMP.compare(bytes1, bytes2);
    }

    public static byte[] concat(byte[] bytes1, byte[] bytes2) {
        byte[] result = new byte[bytes1.length + bytes2.length];
        System.arraycopy(bytes1, 0, result, 0, bytes1.length);
        System.arraycopy(bytes2, 0, result, bytes1.length, bytes2.length);
        return result;
    }

    public static boolean prefixWith(byte[] bytes, byte[] prefix) {
        if (bytes.length < prefix.length) {
            return false;
        }
        for (int i = 0; i < prefix.length; i++) {
            if (bytes[i] != prefix[i]) {
                return false;
            }
        }
        return true;
    }

    public static boolean contains(byte[] bytes, byte value) {
        for (byte b : bytes) {
            if (b == value) {
                return true;
            }
        }
        return false;
    }

    public static int indexOf(byte[] bytes, byte value) {
        for (int i = 0; i < bytes.length; i++) {
            if (bytes[i] == value) {
                return i;
            }
        }
        return -1;
    }

    public static boolean equals(byte[] bytes1, byte[] bytes2) {
        return Arrays.equals(bytes1, bytes2);
    }

    public static String toHex(byte b) {
        return toHex(new byte[]{b});
    }

    public static String toHex(byte[] bytes) {
        throw new UnsupportedOperationException();
    }

    public static byte[] fromHex(String hex) {
        throw new UnsupportedOperationException();
    }

    public static String toString(byte[] bytes) {
        return Strings.decode(bytes);
    }
}
