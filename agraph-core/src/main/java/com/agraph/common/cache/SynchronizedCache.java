package com.agraph.common.cache;

/**
 * TODO: Class description here.
 *
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

    public synchronized boolean remove(K key) {
        return this.underlying.remove(key);
    }

    public synchronized long size() {
        return this.underlying.size();
    }
}
