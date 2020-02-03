package com.agraph.storage.backend;

import com.agraph.common.util.Strings;
import com.agraph.storage.StorageBackend;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BackendFactoryRegistry {

    private static Map<String, Class<? extends BackendFactory>> providers = new ConcurrentHashMap<>();

    public static BackendFactory getFactory(String backend) {
        Class<? extends BackendFactory> clazz = providers.get(backend);
        if (clazz == null) {
            throw new BackendException("Not exists StorageBackend: " + backend);
        }
        try {
            return (BackendFactory) clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new BackendException();
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static void register(String name, String classPath) {
        ClassLoader classLoader = BackendFactoryRegistry.class.getClassLoader();
        Class<?> clazz;
        try {
            clazz = classLoader.loadClass(classPath);
        } catch (Exception e) {
            throw new BackendException(e);
        }

        // Check subclass
        boolean subclass = StorageBackend.class.isAssignableFrom(clazz);
        if (!subclass) {
            throw new BackendException(Strings.format("Class '%s' is not a subclass of " +
                    "class StorageBackend", clazz.getName()));
        }

        // Check exists
        if (providers.containsKey(name)) {
            throw new BackendException(Strings.format("Exists BackendStoreProvider: %s (%s)",
                    name, providers.get(name)));
        }

        // Register class
        providers.put(name, (Class) clazz);
    }
}
