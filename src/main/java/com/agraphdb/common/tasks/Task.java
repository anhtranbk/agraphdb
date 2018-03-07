package com.agraphdb.common.tasks;

import java.util.concurrent.Callable;

/**
 * TODO: Class description here.
 *
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
