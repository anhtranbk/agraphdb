package com.agraph.storage.rdbms;

import com.google.common.base.Preconditions;
import lombok.Setter;
import lombok.experimental.Accessors;

public interface Column {

    enum DataType {
        BYTE,
        SHORT,
        INTEGER,
        LONG,
        FLOAT,
        DOUBLE,
        STRING,
        BYTE_ARRAY,
        STRING_ARRAY
    }

    String name();

    DataType dataType();

    int length();

    Object defaultValue();

    boolean allowNull();

    boolean autoIncrement();

    static Builder builder(String name, DataType dataType) {
        return new Builder(name, dataType);
    }

    @Accessors(fluent = true, chain = true)
    @Setter
    class Builder {
        String name;
        DataType type;
        int length;
        Object defaultValue;
        boolean allowNull = true;
        boolean autoIncrement = false;

        Builder(String name, DataType type) {
            this.name = name;
            this.type = type;
        }

        public Column build() {
            Preconditions.checkArgument(length > 0 || type != DataType.STRING,
                    "Cannot create column String with zero-length");

            final Builder delegate = Builder.this;
            return new Column() {
                @Override
                public String name() {
                    return delegate.name;
                }

                @Override
                public DataType dataType() {
                    return delegate.type;
                }

                @Override
                public int length() {
                    return delegate.length;
                }

                @Override
                public Object defaultValue() {
                    return delegate.defaultValue;
                }

                @Override
                public boolean allowNull() {
                    return delegate.allowNull;
                }

                @Override
                public boolean autoIncrement() {
                    return delegate.autoIncrement;
                }
            };
        }
    }
}
