package com.agraph;

import com.agraph.core.AGraphProperty;
import com.agraph.core.type.ElementId;
import org.apache.tinkerpop.gremlin.structure.Element;
import org.apache.tinkerpop.gremlin.structure.Property;

import java.util.Map;

public interface AGraphElement extends Element, Cloneable {

    default AGraph graph() {
        return this.tx().graph();
    }

    AGraphTransaction tx();

    ElementId id();

    String label();

    Map<String, AGraphProperty<?>> asPropertiesMap();

    Map<String, Object> asValuesMap();

    /**
     * Get the value of a {@link Property} given it's key.
     * The default implementation calls {@link Element#property} and then returns the associated value.
     *
     * @return property value of null if property does not exist
     */
    <V> V valueOrNull(String key);
}
