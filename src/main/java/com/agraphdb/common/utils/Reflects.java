package com.agraphdb.common.utils;

import java.lang.reflect.InvocationTargetException;

/**
 * TODO: Class description here.
 *
 * @author <a href="https://github.com/tjeubaoit">tjeubaoit</a>
 */
@SuppressWarnings("unchecked")
public class Reflects {

    static final ClassLoader mainCl = Reflects.class.getClassLoader();

    @SuppressWarnings("unchecked")
    public static <T> Class<T> getClassInstance(T object) {
        return (Class<T>) object.getClass();
    }

    public static <T> T newInstance(Class<T> c) {
        try {
            return c.newInstance();
        } catch (IllegalAccessException | InstantiationException | NullPointerException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T newInstance(String className) {
        try {
            return (T) mainCl.loadClass(className).newInstance();
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T newInstance(String className, Class<?>[] parameterTypes, Object... parameters) {
        try {
            return (T) mainCl.loadClass(className).getConstructor(parameterTypes).newInstance(parameters);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException
                | NoSuchMethodException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
