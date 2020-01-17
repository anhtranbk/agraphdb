package com.agraph;

import com.agraph.core.AGraphProperty;
import com.agraph.core.AbstractElement;
import com.agraph.core.type.ElementId;
import org.apache.tinkerpop.gremlin.structure.Element;
import org.apache.tinkerpop.gremlin.structure.Property;

import java.util.Map;

public interface AGraphElement extends Element, Statifiable, Cloneable {

    AGraph graph();

    ElementId id();

    String label();

    default AGraphTransaction tx() {
        return graph().tx();
    }

    default boolean isVertex() {
        return id().isVertex();
    }

    default boolean isEdge() {
        return id().isEdge();
    }

    Map<String, AGraphProperty<?>> asPropertiesMap();

    Map<String, Object> asValuesMap();

    /**
     * Get the value of a {@link Property} given it's key.
     * The default implementation calls {@link Element#property} and then returns the associated value.
     *
     * @return property value of null if property does not exist
     */
    <V> V valueOrNull(String key);

    void resetProperties();

    void copyProperties(AbstractElement element);

    AGraphElement copy();
}
