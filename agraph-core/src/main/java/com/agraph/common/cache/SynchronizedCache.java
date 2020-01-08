package com.agraph.common.cache;

/**
 * @author <a href="https://github.com/tjeubaoit">tjeubaoit</a>
 */
public class SynchronizedCache<K, V> implements Cache<K, V> {

    private final Cache<K, V> underlying;

    public SynchronizedCache(Cache<K, V> underlying) {
        this.underlying = underlying;
    }

    public synchronized V get(K key) {
        return this.underlying.get(key);
    }

    public synchronized void put(K key, V value) {
        this.underlying.put(key, value);
    }

    @Override
    public void invalidate(K key) {
        this.underlying.invalidate(key);
    }

    public synchronized long size() {
        return this.underlying.size();
    }
}
