package com.agraphdb.common.utils;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * TODO: Class description here.
 *
 * @author <a href="https://github.com/tjeubaoit">tjeubaoit</a>
 */
@SuppressWarnings("unchecked")
public class NullProtector {

    public static Optional<String> split(String source, String regex, int index) {
        try {
            return get(source.split(regex), index);
        } catch (NullPointerException e) {
            return Optional.empty();
        }
    }

    @SuppressWarnings("SameParameterValue")
    public static <T> Optional<T> get(List<T> list, int index) {
        try {
            return Optional.of(list.get(index));
        } catch (IndexOutOfBoundsException | NullPointerException ignored) {
            return Optional.empty();
        }
    }

    public static <T> Optional<T> get(T[] arr, int index) {
        try {
            return Optional.of(arr[index]);
        } catch (IndexOutOfBoundsException | NullPointerException ignored) {
            return Optional.empty();
        }
    }

    public static <T> Optional<T> first(Collection<T> coll) {
        return coll.isEmpty() ? Optional.empty() : Optional.ofNullable(coll.iterator().next());
    }

    public static <K, V> Optional<V> get(Map<K, ?> map, K key) {
        try {
            return Optional.of((V) map.get(key));
        } catch (NullPointerException e) {
            return Optional.empty();
        }
    }

    public static <K, V> V getOrNull(Map<K, ?> map, K key) {
        Object value = map.get(key);
        return value != null ? (V) value : null;
    }
}
