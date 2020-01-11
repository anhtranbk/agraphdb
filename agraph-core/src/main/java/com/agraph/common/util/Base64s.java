package com.agraph.common.util;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * @author <a href="https://github.com/tjeubaoit">tjeubaoit</a>
 */
public class Base64s {

    public static byte[] encode(byte[] src, boolean urlSafe) {
        Base64.Encoder encoder = urlSafe ? Base64.getUrlEncoder() : Base64.getEncoder();
        return encoder.encode(src);
    }

    public static String encodeAsString(byte[] src, boolean urlSafe) {
        return new String(encode(src, urlSafe), StandardCharsets.UTF_8);
    }

    public static byte[] decode(byte[] src, boolean urlSafe) {
        Base64.Decoder decoder = urlSafe ? Base64.getUrlDecoder() : Base64.getDecoder();
        return decoder.decode(src);
    }

    public static String decodedAsString(byte[] src, boolean urlSafe) {
        return new String(decode(src, urlSafe), StandardCharsets.UTF_8);
    }
}
