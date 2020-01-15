package com.agraph.core;

import com.agraph.AGraph;
import com.agraph.AGraphElement;
import com.agraph.State;
import com.agraph.common.util.Strings;
import com.agraph.core.type.ElementId;
import com.google.common.base.Preconditions;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.apache.tinkerpop.gremlin.structure.Property;
import org.apache.tinkerpop.gremlin.structure.util.ElementHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@SuppressWarnings({"unchecked", "UnusedReturnValue", "SameParameterValue"})
@Accessors(fluent = true)
public abstract class AbstractElement implements AGraphElement {

    protected static final Logger logger = LoggerFactory.getLogger(AbstractElement.class);

    @Getter
    private final AGraph graph;
    @Getter
    private final ElementId id;
    @Getter
    private final String label;
    @Getter
    private State state;

    private final Map<String, AGraphProperty<?>> properties = new HashMap<>();
    private final Set<AGraphProperty<?>> rProperties = new HashSet<>();

    public AbstractElement(AGraph graph, ElementId id, String label) {
        this(graph, id, label, State.NEW);
    }

    public AbstractElement(AGraph graph, ElementId id, String label, State state) {
        Preconditions.checkNotNull(id, "Element Id cannot be null");
        Preconditions.checkNotNull(graph, "Graph cannot be null");
        ElementHelper.validateLabel(label);

        this.graph = graph;
        this.id = id;
        this.label = label;
        this.state = state;
    }

    @Override
    public void updateState(State state) {
        if (state != State.REMOVED && this.state != state && this.state.nextState() != state) {
            final String msg = Strings.format(
                    "Illegal next state. Current: %s, expected: %s, got: %s",
                    this.state, this.state.nextState(), state);
            logger.warn(msg);
            return;
        }
        this.state = state;
    }

    @Override
    public void remove() {
        this.updateState(State.REMOVED);
    }

    @Override
    public Map<String, AGraphProperty<?>> asPropertiesMap() {
        return Collections.unmodifiableMap(this.properties);
    }

    @Override
    public synchronized Map<String, Object> asValuesMap() {
        Map<String, Object> props = new HashMap<>(this.properties.size());
        for (Map.Entry<String, AGraphProperty<?>> entry : this.properties.entrySet()) {
            props.put(entry.getKey(), entry.getValue().value());
        }
        return props;
    }

    @Override
    public <V> V propertyValue(String key) {
        AGraphProperty<?> prop = this.properties.get(key);
        if (prop == null) {
            return null;
        }
        return (V) prop.value();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AbstractElement)) return false;
        AbstractElement that = (AbstractElement) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, label);
    }

    @Override
    public void resetProperties() {
        this.ensureElementCanModify();
        this.updateState(State.MODIFIED);
        this.properties.clear();
    }

    @Override
    public void copyProperties(AbstractElement element) {
        this.ensureElementCanModify();
        this.updateState(State.MODIFIED);
        this.properties.putAll(element.properties);
    }

    public Set<AGraphProperty<?>> removedProperties() {
        return Collections.unmodifiableSet(this.rProperties);
    }

    protected <V> AGraphProperty<V> removeProperty(String key) {
        this.ensureElementCanModify();
        AGraphProperty<?> property = this.properties.remove(key);
        if (property != null) {
            if (!isNew()) {
                // If element state is not NEW, removed property will be keep to
                // synchronize with the database later
                this.rProperties.add(property);
                this.updateState(State.MODIFIED);
            }
            logger.debug("Property has been removed: {}", property);
            return (AGraphProperty<V>) property;
        }
        throw Property.Exceptions.propertyDoesNotExist(this, key);
    }

    protected <V> void putProperty(AGraphProperty<V> prop) {
        this.ensureElementCanModify();
        this.updateState(State.MODIFIED);
        this.properties.put(prop.key(), prop);
    }

    protected Map<String, AGraphProperty<?>> autoFilledProperties() {
        this.ensureFilledProperties(true);
        return this.properties;
    }

    /**
     * Modification on element is only allowed if element was not removed.
     * If current element state is LAGGED, element must be refreshed from
     * the database before it can do any mutations
     */
    protected void ensureElementCanModify() {
        Preconditions.checkState(isPresent(), "Could not modify a removed element");
        if (isLagged()) {
            this.ensureFilledProperties(true);
        }
    }

    protected abstract boolean ensureFilledProperties(boolean throwIfNotExist);
}
