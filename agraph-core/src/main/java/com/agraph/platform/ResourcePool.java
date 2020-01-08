package com.agraph.platform;

public interface ResourcePool<R> extends AutoCloseable {

    R getResource();

    void closeResource(R resource);

    @Override
    void close();
}
