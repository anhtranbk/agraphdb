package com.vcc.bigdata.common.cache;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * TODO: Class description here.
 *
 * @author <a href="https://github.com/tjeubaoit">tjeubaoit</a>
 */
public class LRUCache<K, V> implements Cache<K, V> {
    private final LinkedHashMap<K, V> cache;

    public LRUCache(final int maxSize) {
        this.cache = new LinkedHashMap<K, V>(16, 0.75F, true) {
            protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
                return this.size() > maxSize;
            }
        };
    }

    public V get(K key) {
        return this.cache.get(key);
    }

    public void put(K key, V value) {
        this.cache.put(key, value);
    }

    public boolean remove(K key) {
        return this.cache.remove(key) != null;
    }

    public long size() {
        return (long)this.cache.size();
    }
}
