package com.agraph.core;

import com.agraph.AGraphElement;
import com.agraph.common.util.Strings;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import org.apache.tinkerpop.gremlin.structure.Property;
import org.apache.tinkerpop.gremlin.structure.util.ElementHelper;
import org.apache.tinkerpop.gremlin.structure.util.StringFactory;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;

public abstract class AGraphProperty<V> implements Property<V> {

    public static final Map<String, AGraphProperty<?>> EMPTY = ImmutableMap.of();

    protected final AGraphElement owner;
    protected final String key;
    protected final V value;

    private boolean removed = false;

    public AGraphProperty(AGraphElement owner, String key, V value) {
        Preconditions.checkNotNull(owner, "Owner can not be null");
        Preconditions.checkArgument(Strings.isNonEmpty(key), "Property key can not be null or empty");
        if (value == null) {
            throw Exceptions.propertyValueCanNotBeNull();
        }
        this.validatePropertyValue(value);

        this.owner = owner;
        this.key = key;
        this.value = value;
    }

    @Override
    public String key() {
        return key;
    }

    @Override
    public V value() throws NoSuchElementException {
        ensurePropertyExists();
        return value;
    }

    @Override
    public void remove() {
        ensurePropertyExists();
        this.removed = true;
    }

    @Override
    public boolean isPresent() {
        return !removed;
    }

    private void ensurePropertyExists() {
        if (removed) {
            throw Exceptions.propertyDoesNotExist();
        }
    }

    private void validatePropertyValue(V value) {
        throw Exceptions.dataTypeOfPropertyValueNotSupported(value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AGraphProperty)) return false;
        AGraphProperty<?> that = (AGraphProperty<?>) o;
        return Objects.equals(owner, that.owner) &&
                Objects.equals(key, that.key) &&
                Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return ElementHelper.hashCode(this);
    }

    @Override
    public String toString() {
        return StringFactory.propertyString(this);
    }
}
