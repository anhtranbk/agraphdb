package com.agraph.core.cache;

import com.agraph.common.cache.Cache;

public class ElementCache implements Cache<Long, Cacheable> {

    @Override
    public Cacheable get(Long key) {
        return null;
    }

    @Override
    public void put(Long key, Cacheable value) {

    }

    @Override
    public void invalidate(Long key) {

    }

    @Override
    public long size() {
        return 0;
    }
}
