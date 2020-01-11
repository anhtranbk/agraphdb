package com.agraph.common.util;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Locale;

/**
 * TODO: Class description here.
 *
 * @author <a href="https://github.com/tjeubaoit">tjeubaoit</a>
 */
public class Strings {

    public static String format(String format, Object... params) {
        return String.format(Locale.US, format, params);
    }

    public static String join(String separator, String... strs) {
        return join(Arrays.asList(strs), separator);
    }

    public static <T> String join(T[] strs, String separator) {
        return join(Arrays.asList(strs), separator);
    }

    public static String join(Iterable<?> iterable, String str) {
        return join(iterable.iterator(), str, "", "");
    }

    public static String join(Iterator<?> iterator, String separator) {
        return join(iterator, separator, "", "");
    }

    public static String join(Iterable<?> iterable, String separator, String prefix, String suffix) {
        return join(iterable.iterator(), separator, prefix, suffix);
    }

    public static String join(Iterator<?> iterator, String separator, String prefix, String suffix) {
        if (!iterator.hasNext()) return "";

        StringBuilder sb = new StringBuilder();
        while (iterator.hasNext()) {
            sb.append(iterator.next());
            if (iterator.hasNext()) {
                sb.append(separator);
            }
        }

        String s = sb.toString();
        return s.isEmpty() ? s : prefix + sb.toString() + suffix;
    }

    public static boolean isNonEmpty(CharSequence source) {
        return source != null && source.length() > 0;
    }

    public static boolean isNullOrEmpty(CharSequence source) {
        return source == null || source.length() == 0;
    }

    public static boolean isNullOrStringEmpty(Object source) {
        return source == null || source.toString().isEmpty();
    }

    public static boolean isNullOrWhitespace(String source) {
        return source == null || source.trim().isEmpty();
    }

    public static boolean containsOnce(String source, String... strings) {
        for (String s : strings) {
            if (source.contains(s)) return true;
        }
        return false;
    }

    public static boolean containsAll(String source, String... strings) {
        for (String s : strings) {
            if (!source.contains(s)) return false;
        }
        return true;
    }

    public static String firstCharacters(String source, int numChars, boolean withThreeDot, int skip) {
        try {
            return source.substring(skip, numChars) + (withThreeDot ? "..." : "");
        } catch (IndexOutOfBoundsException ignored) {
            return source;
        }
    }
    public static String firstCharacters(String source, int numChars) {
        return firstCharacters(source, numChars, true, 0);
    }
    public static String firstCharacters(String source, int numChars, int skip) {
        return firstCharacters(source, numChars, false, skip);
    }

    public static String remove4bytesUnicodeSymbols(String source) {
        return source.replaceAll("[^\\u0000-\\uFFFF]", "");
    }

    public static String lastCharacters(String source, int numChars) {
        try {
            return source.substring(source.length() - numChars, source.length());
        } catch (IndexOutOfBoundsException e) {
            return source;
        }
    }

    public static String simplify(String source) {
        return source.toLowerCase().replaceAll("[.,:;?!\n\t]", "");
    }

    public static byte[] encode(String value) {
        return value.getBytes(StandardCharsets.UTF_8);
    }

    public static String decode(byte[] bytes) {
        return new String(bytes, StandardCharsets.UTF_8);
    }
}
