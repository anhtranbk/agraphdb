package com.agraph.core.cache;

import java.util.Optional;

/**
 * @author <a href="https://github.com/tjeubaoit">tjeubaoit</a>
 */
public class SynchronizedCache<K, V> implements Cache<K, V> {

    private final Cache<K, V> underlying;

    public SynchronizedCache(Cache<K, V> underlying) {
        this.underlying = underlying;
    }

    @Override
    public Optional<V> get(K key) {
        return underlying.get(key);
    }

    public synchronized V put(K key, V value) {
        return this.underlying.put(key, value);
    }

    @Override
    public void invalidate(K key) {
        this.underlying.invalidate(key);
    }

    public synchronized long size() {
        return this.underlying.size();
    }
}
