package com.agraph.core.cache;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author <a href="https://github.com/tjeubaoit">tjeubaoit</a>
 */
public class JavaLRUCache<K, V> implements Cache<K, V> {
    private final LinkedHashMap<K, V> cache;

    public JavaLRUCache(final int maxSize) {
        this.cache = new LinkedHashMap<K, V>(maxSize, 0.75F, true) {
            protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
                return this.size() > maxSize;
            }
        };
    }

    @Override
    public Optional<V> get(K key) {
        return Optional.of(cache.get(key));
    }

    @Override
    public V put(K key, V value) {
        return this.cache.put(key, value);
    }

    @Override
    public void invalidate(K key) {
        this.cache.remove(key);
    }

    @Override
    public long size() {
        return this.cache.size();
    }

    @Override
    public Map<K, V> asMap() {
        return Collections.unmodifiableMap(cache);
    }
}
