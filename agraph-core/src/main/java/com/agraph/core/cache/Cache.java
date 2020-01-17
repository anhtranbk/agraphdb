package com.agraph.core.cache;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author <a href="https://github.com/tjeubaoit">tjeubaoit</a>
 */
public interface Cache<K, V> {

    Optional<V> get(K key);

    default V getOrNull(K key) {
        return get(key).orElse(null);
    }

    default Map<K, V> getAllPresent(Iterable<K> keys) {
        Map<K, V> m = new HashMap<>();
        for (K key : keys) {
            Optional<V> ops = get(key);
            ops.ifPresent(v -> m.put(key, v));
        }
        return m;
    }

    V put(K key, V value);

    default void putAll(Map<? extends K, ? extends V> m) {
        for (Map.Entry<? extends K, ? extends V> entry : m.entrySet()) {
            this.put(entry.getKey(), entry.getValue());
        }
    }

    default void putIfAbsent(K key, V value) {
        if (!get(key).isPresent()) {
            put(key, value);
        }
    }

    void invalidate(K key);

    default void invalidateAll(Iterable<? extends K> keys) {
        for (K key : keys) {
            invalidate(key);
        }
    }

    default void invalidateAll() {
        throw new UnsupportedOperationException();
    }

    long size();

    default Map<K, V> asMap() {
        throw new UnsupportedOperationException();
    }

    default long capacity() {
        throw new UnsupportedOperationException();
    }

    default void cleanUp() {
        throw new UnsupportedOperationException();
    }
}
