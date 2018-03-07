package com.vcc.bigdata.common.utils;

import com.google.common.base.Preconditions;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * TODO: Class description here.
 *
 * @author <a href="https://github.com/tjeubaoit">tjeubaoit</a>
 */
@SuppressWarnings("unchecked")
public class Maps {

    public static <K, V> V getAndRemove(Map<K, ?> map, K key) {
        Object value = map.get(key);
        map.remove(key);
        return (V) value;
    }

    public static <K, V> V getOrNull(Map<K, ?> map, K key) {
        Object value = map.get(key);
        return value != null ? (V) value : null;
    }

    public static Map<String, Object> initFromKeyValues(Object... keyValues) {
        Preconditions.checkArgument(keyValues.length % 2 == 0);
        Map<String, Object> map = new TreeMap<>();
        for (int i = 0; i < keyValues.length; i += 2) {
            map.put(keyValues[i].toString(), keyValues[i + 1]);
        }
        return map;
    }

    public static void putIfNotNullOrEmpty(Map<String, Object> map, String key, Object value) {
        if (!Strings.isNullOrStringEmpty(value)) {
            map.put(key, value);
        }
    }

    public static Map<String, String> convertToTextMap(Map<String, ?> input) {
        Map<String, String> output = new LinkedHashMap<>();
        for (Map.Entry<String, ?> entry : input.entrySet()) {
            if (entry.getValue() == null) continue;
            output.put(entry.getKey(), String.valueOf(entry.getValue()));
        }
        return output;
    }
}
