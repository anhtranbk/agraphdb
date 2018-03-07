package com.vcc.bigdata.graphdb;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * TODO: Class description here.
 *
 * @author <a href="https://github.com/tjeubaoit">tjeubaoit</a>
 */
@SuppressWarnings("unchecked")
public abstract class AbstractElement implements Element {

    private final String id;
    private final String label;
    private final Map<String, Object> properties = new HashMap<>();

    public AbstractElement(String id, String label, Map<String, ?> properties) {
        this.id = id;
        this.label = label;
        this.properties.putAll(properties);
    }

    public AbstractElement(String id) {
        this(id, null, Collections.emptyMap());
    }

    @Override
    public String id() {
        return this.id;
    }

    @Override
    public String label() {
        return this.label;
    }

    @Override
    public Object property(String key) {
        return this.properties.get(key);
    }

    public Object property(String key, Object defVal) {
        return this.properties.getOrDefault(key, defVal);
    }

    @Override
    public Map<String, ?> properties() {
        return this.properties;
    }

    @Override
    public void putProperty(String key, Object value) {
        this.properties.put(key, value);
    }

    @Override
    public void putProperties(Map<String, ?> map) {
        this.properties.putAll(map);
    }

    @Override
    public String toString() {
        return this.label() + ":" + this.id();
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }
}
