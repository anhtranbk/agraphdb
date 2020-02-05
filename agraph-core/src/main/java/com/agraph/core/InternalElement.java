package com.agraph.core;

import com.agraph.AGraphElement;
import com.agraph.Statifiable;

import java.util.Map;
import java.util.Set;

/**
 * Internal Element interface adding methods that should only be used by AGraph
 */
public interface InternalElement extends AGraphElement, Statifiable {

    Set<AGraphProperty<?>> modifiedProperties();

    Set<AGraphProperty<?>> removedProperties();

    default boolean isVertex() {
        return id().isVertex();
    }

    default boolean isEdge() {
        return id().isEdge();
    }

    @SuppressWarnings("UnusedReturnValue")
    <V> AGraphProperty<V> removeProperty(String key);

    void resetProperties();

    void copyProperties(InternalElement element);

    void attachPropertiesUnchecked(Map<String, ?> props);

    AGraphElement copy();
}
