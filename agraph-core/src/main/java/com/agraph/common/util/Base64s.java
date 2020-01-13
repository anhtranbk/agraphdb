package com.agraph.common.util;

import java.util.Base64;

/**
 * @author <a href="https://github.com/tjeubaoit">tjeubaoit</a>
 */
public class Base64s {

    public static byte[] encode(String src, boolean urlSafe) {
        Base64.Encoder encoder = urlSafe ? Base64.getUrlEncoder() : Base64.getEncoder();
        return encoder.encode(Strings.toBytes(src));
    }

    public static byte[] encode(byte[] src, boolean urlSafe) {
        Base64.Encoder encoder = urlSafe ? Base64.getUrlEncoder() : Base64.getEncoder();
        return encoder.encode(src);
    }

    public static String encodeAsString(byte[] src, boolean urlSafe) {
        return Strings.fromBytes(encode(src, urlSafe));
    }

    public static byte[] decode(String src, boolean urlSafe) {
        Base64.Decoder decoder = urlSafe ? Base64.getUrlDecoder() : Base64.getDecoder();
        return decoder.decode(src);
    }

    public static byte[] decode(byte[] src, boolean urlSafe) {
        Base64.Decoder decoder = urlSafe ? Base64.getUrlDecoder() : Base64.getDecoder();
        return decoder.decode(src);
    }
}
