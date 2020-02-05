package com.agraph.core;

import com.agraph.AGraphEdge;
import com.agraph.AGraphTransaction;
import com.agraph.AGraphVertex;
import com.agraph.State;
import com.agraph.common.util.Strings;
import com.agraph.core.type.EdgeId;
import com.agraph.core.type.ElementId;
import com.agraph.core.type.VertexId;
import com.google.common.base.Preconditions;
import lombok.AccessLevel;
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
public abstract class AbstractElement implements InternalElement {

    protected static final Logger logger = LoggerFactory.getLogger(AbstractElement.class);

    private final ElementId id;
    private final String label;
    private final AGraphTransaction tx;
    private State state;

    /**
     * Original values of modified/removed properties. These are used to rollback state
     */
    @Getter(AccessLevel.NONE)
    private final Map<String, AGraphProperty<?>> originalProps = new HashMap<>();
    /**
     * Keeps track of all added properties in this transaction
     */
    private final Set<AGraphProperty<?>> modifiedProps = new HashSet<>();
    /**
     * Keeps track of all removed properties in this transaction
     */
    private final Set<AGraphProperty<?>> removedProps = new HashSet<>();

    @Getter(AccessLevel.NONE)
    private final Map<String, AGraphProperty<?>> props = new HashMap<>();

    public AbstractElement(AGraphTransaction tx, ElementId id, String label, State state) {
        Preconditions.checkNotNull(id, "Element Id cannot be null");
        Preconditions.checkState(tx.isOpen(), "Graph transaction has not been opened");
        ElementHelper.validateLabel(label);

        this.tx = tx;
        this.id = id;
        this.label = label;
        this.state = state;
    }

    @Override
    public Map<String, AGraphProperty<?>> asPropertiesMap() {
        this.ensureFilledProperties(true);
        return Collections.unmodifiableMap(this.props);
    }

    @Override
    public synchronized Map<String, Object> asValuesMap() {
        this.ensureFilledProperties(true);
        Map<String, Object> props = new HashMap<>(this.props.size());
        for (Map.Entry<String, AGraphProperty<?>> entry : this.props.entrySet()) {
            props.put(entry.getKey(), entry.getValue().value());
        }
        return props;
    }

    @Override
    public <V> V valueOrNull(String key) {
        this.ensureFilledProperties(true);
        AGraphProperty<?> prop = this.props.get(key);
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
        // revert to original value for all properties
        this.props.putAll(this.originalProps);
        this.originalProps.clear();
    }

    @Override
    public void copyProperties(InternalElement element) {
        this.ensureElementCanModify();
        AbstractElement element1 = (AbstractElement) element;
        if (isNew()) {
            this.props.putAll(element1.props);
        } else {
            element1.props.values().forEach(this::putProperty);
        }
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
    public Set<AGraphProperty<?>> removedProperties() {
        return Collections.unmodifiableSet(this.removedProps);
    }

    @Override
    public Set<AGraphProperty<?>> modifiedProperties() {
        return Collections.unmodifiableSet(this.modifiedProps);
    }

    @Override
    public <V> AGraphProperty<V> removeProperty(String key) {
        this.ensureElementCanModify();
        AGraphProperty<?> property = this.props.remove(key);
        if (property != null) {
            if (!isNew()) {
                // If element state is not NEW, removed property will be keep to
                // synchronize with backend database later
                logger.debug("Property removed: {}", property);
                this.removedProps.add(property);
                this.updateState(State.MODIFIED);
                this.keepOriginalPropertyIfNecessary(key);
            }
            this.props.remove(key);
            return (AGraphProperty<V>) property;
        }
        throw Property.Exceptions.propertyDoesNotExist(this, key);
    }

    @Override
    public void attachPropertiesUnchecked(Map<String, ?> props) {
        for (Map.Entry<String, ?> entry : props.entrySet()) {
            final String key = entry.getKey();
            this.props.put(key, createProperty(key, entry.getValue()));
        }
    }

    protected <V> void putProperty(AGraphProperty<V> prop) {
        this.ensureElementCanModify();
        if (!isNew()) {
            logger.debug("Property added or updated {}", prop);
            this.modifiedProps.add(prop);
            this.updateState(State.MODIFIED);
            this.keepOriginalPropertyIfNecessary(prop.key);
        }
        this.props.put(prop.key(), prop);
    }

    private void keepOriginalPropertyIfNecessary(String key) {
        if (this.props.containsKey(key) && !this.originalProps.containsKey(key)) {
            logger.debug("Keep original value for property {} of element {}", key, id);
            this.originalProps.put(key, this.props.get(key));
        }
    }

    protected Map<String, AGraphProperty<?>> autoFilledProperties() {
        this.ensureFilledProperties(true);
        return this.props;
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
                this.props.putAll(ops.get().asPropertiesMap());
                return true;
            }
        } else {
            Optional<AGraphEdge> ops = this.tx().findEdge((EdgeId) this.id);
            if (ops.isPresent()) {
                this.props.putAll(ops.get().asPropertiesMap());
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

    protected abstract AGraphProperty<?> createProperty(String key, Object value);
}
