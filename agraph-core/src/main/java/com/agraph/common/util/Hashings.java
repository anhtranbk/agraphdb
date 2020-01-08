package com.agraph.common.util;

import com.google.common.hash.Hashing;

import java.nio.charset.Charset;

/**
 * TODO: Class description here.
 *
 * @author <a href="https://github.com/tjeubaoit">tjeubaoit</a>
 */
public class Hashings {

    public static byte[] sha1(String input) {
        return Hashing.sha1().hashString(input, Charset.forName("utf-8")).asBytes();
    }

    public static byte[] sha1(byte[] input) {
        return Hashing.sha1().hashBytes(input).asBytes();
    }

    public static String sha1AsHex(String input) {
        return Hashing.sha1().hashString(input, Charset.forName("utf-8")).toString();
    }

    public static String sha1AsHex(byte[] input) {
        return Hashing.sha1().hashBytes(input).toString();
    }

    public static String sha1AsBase64(String input, boolean urlSafe) {
        return Base64s.encodeAsString(sha1(input), urlSafe);
    }

    public static String sha1AsBase64(byte[] input, boolean urlSafe) {
        return Base64s.encodeAsString(sha1(input), urlSafe);
    }
}
