package com.vcc.bigdata.common.config;

/**
 * TODO: Class description here.
 *
 * @author <a href="https://github.com/tjeubaoit">tjeubaoit</a>
 */
public class SubProperties extends Properties {

    private final Properties impl;
    private final String name;
    private final String group;

    public SubProperties(String group, String name, Properties p) {
        this.group = group;
        this.name = name;
        this.impl = p;
    }

    public SubProperties(Class<?> clazz, Properties p) {
        this(clazz.getSimpleName(), p);
    }

    public SubProperties(String name, Properties p) {
        this(null, name, p);
    }

    @Override
    public String getProperty(String key) {
        for (String prefix : new String[]{name, group}) {
            if (prefix == null) continue;
            String value = impl.getProperty(keyWithPrefix(prefix, key));
            if (value != null) return value;
        }
        return impl.getProperty(key);
    }

    @Override
    public synchronized boolean containsKey(Object key) {
        return impl.containsKey(keyWithPrefix(name, key))
                || (group != null && impl.containsKey(keyWithPrefix(group, key)))
                || impl.containsKey(key);
    }

    private String keyWithPrefix(String prefix, Object key) {
        return prefix + "." + key;
    }
}
