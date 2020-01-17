package com.agraph.core;

import com.agraph.AGraphEdge;
import com.agraph.AGraphElement;
import com.agraph.AGraphVertex;
import com.agraph.State;
import com.agraph.common.util.Strings;
import com.agraph.core.type.EdgeId;
import com.agraph.core.type.ElementId;
import com.agraph.core.type.VertexId;
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
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@SuppressWarnings({"unchecked", "UnusedReturnValue", "SameParameterValue"})
@Accessors(fluent = true)
@Getter
public abstract class AbstractElement implements AGraphElement {

    protected static final Logger logger = LoggerFactory.getLogger(AbstractElement.class);

    private final DefaultAGraph graph;
    private final ElementId id;
    private final String label;
    private State state;

    private final Map<String, AGraphProperty<?>> properties = new HashMap<>();
    private final Set<AGraphProperty<?>> removedProps = new HashSet<>();
    private final Set<AGraphProperty<?>> modifiedProps = new HashSet<>();

    public AbstractElement(DefaultAGraph graph, ElementId id, String label, State state,
                           Map<String, ? extends AGraphProperty<?>> props) {
        Preconditions.checkNotNull(id, "Element Id cannot be null");
        Preconditions.checkNotNull(graph, "Graph cannot be null");
        ElementHelper.validateLabel(label);

        this.graph = graph;
        this.id = id;
        this.label = label;
        this.state = state;
        this.properties.putAll(props);
    }

    @Override
    public Map<String, AGraphProperty<?>> asPropertiesMap() {
        this.ensureFilledProperties(true);
        return Collections.unmodifiableMap(this.properties);
    }

    @Override
    public synchronized Map<String, Object> asValuesMap() {
        this.ensureFilledProperties(true);
        Map<String, Object> props = new HashMap<>(this.properties.size());
        for (Map.Entry<String, AGraphProperty<?>> entry : this.properties.entrySet()) {
            props.put(entry.getKey(), entry.getValue().value());
        }
        return props;
    }

    @Override
    public <V> V valueOrNull(String key) {
        this.ensureFilledProperties(true);
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
        if (isNew()) {
            this.properties.clear();
        } else {
            this.properties.values().forEach(AGraphProperty::remove);
        }
    }

    @Override
    public void copyProperties(AbstractElement element) {
        this.ensureElementCanModify();
        if (isNew()) {
            this.properties.putAll(element.properties);
        } else {
            element.properties.values().forEach(this::putProperty);
        }
    }

    protected void updateState(State state) {
        if (state != State.REMOVED && this.state != state && this.state.nextState() != state) {
            final String msg = Strings.format(
                    "Illegal next state. Current: %s, expected: %s, got: %s",
                    this.state, this.state.nextState(), state);
            logger.warn(msg);
            return;
        }
        this.state = state;
    }

    protected Set<AGraphProperty<?>> removedProperties() {
        return Collections.unmodifiableSet(this.removedProps);
    }

    protected Set<AGraphProperty<?>> modifiedProperties() {
        return Collections.unmodifiableSet(this.modifiedProps);
    }

    protected <V> AGraphProperty<V> removeProperty(String key) {
        this.ensureElementCanModify();
        AGraphProperty<?> property = this.properties.remove(key);
        if (property != null) {
            if (!isNew()) {
                // If element state is not NEW, removed property will be keep to
                // synchronize with backend database later
                this.removedProps.add(property);
                this.updateState(State.MODIFIED);
                logger.debug("Property has been removed: {}", property);
            }
            return (AGraphProperty<V>) property;
        }
        throw Property.Exceptions.propertyDoesNotExist(this, key);
    }

    protected <V> void putProperty(AGraphProperty<V> property) {
        this.ensureElementCanModify();
        if (!isNew()) {
            logger.debug("Property has been added or updated {}", property);
            this.modifiedProps.add(property);
        }
        this.properties.put(property.key(), property);
        this.updateState(State.MODIFIED);
    }

    protected Map<String, AGraphProperty<?>> autoFilledProperties() {
        this.ensureFilledProperties(true);
        return this.properties;
    }

    /**
     * Modification on element is only allowed if element was not removed.
     * If current element state is LAGGED, element must be refreshed from
     * backend database before it can do any mutations
     */
    protected void ensureElementCanModify() {
        Preconditions.checkState(isPresent(), "Could not modify a removed element");
        if (isLagged()) {
            logger.debug("Element lagged, need reload properties from backend database");
            this.ensureFilledProperties(true);
        }
    }

    /**
     * Ensure all properties of element are loaded from backend database
     */
    protected boolean ensureFilledProperties(boolean throwIfNotExist) {
        if (!isLagged()) {
            logger.debug("Element has already loaded");
            return true;
        }
        if (isVertex()) {
            Optional<AGraphVertex> ops = this.tx().findVertex((VertexId) this.id);
            if (ops.isPresent()) {
                this.properties.putAll(ops.get().asPropertiesMap());
                return true;
            }

        } else {
            Optional<AGraphEdge> ops = this.tx().findEdge((EdgeId) this.id);
            if (ops.isPresent()) {
                this.properties.putAll(ops.get().asPropertiesMap());
                return true;
            }
        }
        if (throwIfNotExist) {
            throw new NoSuchElementException(Strings.format("Element does not exist: %s", this.id));
        } else {
            logger.error("Could not fill properties. Element does not exist: {}", this.id);
            return false;
        }
    }
}
