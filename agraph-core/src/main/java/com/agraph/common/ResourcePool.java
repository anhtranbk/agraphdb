package com.agraph.common;

public interface ResourcePool<R> extends AutoCloseable {

    R getResource();

    void closeResource(R resource);

    @Override
    void close();
}
