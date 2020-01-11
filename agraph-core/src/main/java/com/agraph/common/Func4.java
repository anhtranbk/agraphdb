package com.agraph.common;

/**
 * @author <a href="https://github.com/tjeubaoit">tjeubaoit</a>
 */
@FunctionalInterface
public interface Func4<T1, T2, T3, T4, R> {

    R apply(T1 t1, T2 t2, T3 t3, T4 t4);
}
