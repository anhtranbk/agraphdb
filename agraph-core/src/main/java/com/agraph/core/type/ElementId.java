package com.agraph.core.type;

import org.jetbrains.annotations.NotNull;

public abstract class ElementId implements Comparable<ElementId> {

    public static final String VERTEX_SEPARATOR = ":";
    public static final String EDGE_SEPARATOR = "-";

    public abstract String asString();

    public abstract DataType type();

    public abstract boolean isEdge();

    public byte[] asBytes() {
        return asString().getBytes();
    }

    public int length() {
        return asBytes().length;
    }

    @Override
    public int compareTo(@NotNull ElementId elementId) {
        return this.asString().compareTo(elementId.asString());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ElementId)) return false;
        ElementId that = (ElementId) o;
        return this.asString().equals(that.asString());
    }

    @Override
    public int hashCode() {
        return asString().hashCode();
    }

    @Override
    public String toString() {
        return asString();
    }
}
