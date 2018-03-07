package com.vcc.bigdata.graphdb;

import java.util.Map;

/**
 * TODO: Class description here.
 *
 * @author <a href="https://github.com/tjeubaoit">tjeubaoit</a>
 */
public interface Element {

    String id();

    String label();

    Object property(String key);

    Map<String, ?> properties();

    void putProperty(String key, Object value);

    void putProperties(Map<String, ?> map);
}
