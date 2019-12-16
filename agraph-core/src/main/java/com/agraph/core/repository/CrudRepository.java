package com.agraph.core.repository;

import com.google.common.util.concurrent.ListenableFuture;
import com.agraph.core.Element;

import java.io.Closeable;
import java.util.Collection;

/**
 * TODO: Class description here.
 *
 * @author <a href="https://github.com/tjeubaoit">tjeubaoit</a>
 */
public interface CrudRepository<T extends Element> extends Closeable {

    Iterable<T> findAll();

    T findOne(T entity);

    ListenableFuture<? extends Iterable<T>> delete(T entity);

    ListenableFuture<? extends Iterable<T>> save(T entity);

    ListenableFuture<? extends Iterable<T>> saveAll(Collection<T> entities);

    @Override
    void close();
}
