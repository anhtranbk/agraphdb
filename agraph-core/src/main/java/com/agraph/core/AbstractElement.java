package com.agraph.core;

import com.agraph.AGraph;
import com.agraph.AGraphElement;
import com.agraph.State;
import com.agraph.common.util.Strings;
import com.google.common.base.Preconditions;
import org.apache.tinkerpop.gremlin.structure.util.ElementHelper;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public abstract class AbstractElement implements AGraphElement {

    private final AGraph graph;
    private final ElementId id;
    private final String label;

    private final Map<String, AGraphProperty<?>> properties = new HashMap<>();

    private State state;

    public AbstractElement(AGraph graph, ElementId id, String label) {
        this(graph, id, label, State.NEW);
    }

    public AbstractElement(AGraph graph, ElementId id, String label, State state) {
        Preconditions.checkNotNull(id, "Element Id cannot be null");
        Preconditions.checkArgument(Strings.isNonEmpty(label), "Label can not be null or empty");
        this.graph = graph;
        this.id = id;
        this.label = label;
        this.state = state;
    }

    @Override
    public AGraph graph() {
        return this.graph;
    }

    @Override
    public void remove() {
        ensureElementExists();
        updateState(State.REMOVED);
    }

    @Override
    public ElementId id() {
        return this.id;
    }

    @Override
    public String label() {
        return this.label;
    }

    @Override
    public State state() {
        return this.state;
    }

    @Override
    public void updateState(State state) {
        this.state = state;
    }

    /**
     * @return an Unmodifiable Map of properties
     */
    public Map<String, AGraphProperty<?>> getProperties() {
        return Collections.unmodifiableMap(this.properties);
    }

    public Map<String, Object> getPropertiesMap() {
        Map<String, Object> props = new HashMap<>();
        for (Map.Entry<String, AGraphProperty<?>> entry : this.properties.entrySet()) {
            props.put(entry.getKey(), entry.getValue().value());
        }
        return props;
    }

    @SuppressWarnings("unchecked")
    public <V> AGraphProperty<V> getProperty(String key) {
        return (AGraphProperty<V>) this.properties.get(key);
    }

    @SuppressWarnings("unchecked")
    public <V> V getPropertyValue(String key) {
        AGraphProperty<?> prop = this.properties.get(key);
        if (prop == null) {
            return null;
        }
        return (V) prop.value();
    }

    public boolean hasProperty(String key) {
        return this.properties.containsKey(key);
    }

    public int numProperties() {
        return this.properties.size();
    }

    public AGraphProperty<?> removeProperty(String key) {
        return this.properties.remove(key);
    }

    @SuppressWarnings("UnusedReturnValue")
    public <V> AGraphProperty<?> putProperty(AGraphProperty<V> prop) {
        return this.properties.put(prop.key(), prop);
    }

    public void resetProperties() {
        this.properties.clear();
    }

    public void copyProperties(AbstractElement element) {
        this.ensureElementExists();
        this.properties.clear();
        this.properties.putAll(element.getProperties());
    }

    protected Map<String, AGraphProperty<?>> properties() {
        ensureElementExists();
        ensureFilledProperties(true);
        return this.properties;
    }

    protected void ensureElementExists() {
        if (isRemoved()) {
            throw new IllegalStateException("Element was removed");
        }
    }

    @SuppressWarnings({"SameParameterValue", "UnusedReturnValue"})
    protected abstract boolean ensureFilledProperties(boolean throwIfNotExist);

    protected abstract AbstractElement copy();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AbstractElement)) return false;
        AbstractElement that = (AbstractElement) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(label, that.label);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, label);
    }
}
