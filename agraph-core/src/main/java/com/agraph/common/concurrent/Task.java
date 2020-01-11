package com.agraph.common.concurrent;

import java.util.concurrent.Callable;

/**
 * @author <a href="https://github.com/tjeubaoit">tjeubaoit</a>
 */
public interface Task<V> extends Runnable, Callable<V> {

    @Override
    default void run() {
        call();
    }

    @Override
    V call();
}
