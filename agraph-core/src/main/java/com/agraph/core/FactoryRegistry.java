package com.agraph.core;

import com.agraph.AGraphException;
import com.agraph.storage.backend.BackendException;
import com.agraph.storage.backend.BackendFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class FactoryRegistry {

    private static Map<String, Class<? extends BackendFactory>> providers = new ConcurrentHashMap<>();

    public static BackendFactory getBackendFactory(String backend) {
        Class<? extends BackendFactory> clazz = providers.get(backend);
        if (clazz == null) {
            throw new AGraphException("Not exists StorageBackend: %s", backend);
        }
        try {
            return clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new AGraphException(e);
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static void registerBackendFactory(String name, String classPath) {
        ClassLoader classLoader = FactoryRegistry.class.getClassLoader();
        Class<?> clazz;
        try {
            clazz = classLoader.loadClass(classPath);
        } catch (Exception e) {
            throw new AGraphException(e);
        }

        // Check subclass
        boolean subclass = BackendFactory.class.isAssignableFrom(clazz);
        if (!subclass) {
            throw new BackendException("Class '%s' is not a subclass of " +
                    "class BackendFactory", clazz.getName());
        }

        // Check exists
        if (providers.containsKey(name)) {
            throw new BackendException("Exists BackendStoreProvider: %s (%s)",
                    name, providers.get(name));
        }

        // Register class
        providers.put(name, (Class) clazz);
    }
}
