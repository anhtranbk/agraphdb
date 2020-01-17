package com.agraph.storage.rdbms;

import com.google.common.base.Preconditions;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Accessors(fluent = true)
@Getter
public class Column {

    private Column(String name, DataType type, int length, Object defaultValue,
                   boolean allowNull, boolean autoIncrement) {
        this.name = name;
        this.type = type;
        this.length = length;
        this.defaultValue = defaultValue;
        this.allowNull = allowNull;
        this.autoIncrement = autoIncrement;
    }

    private final String name;
    private final DataType type;
    private final int length;
    private final Object defaultValue;
    private final boolean allowNull;
    private final boolean autoIncrement;

    public static Builder builder(String name, DataType dataType) {
        return new Builder(name, dataType);
    }

    @Accessors(fluent = true, chain = true)
    @Setter
    public static class Builder {
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
            return new Column(name, type, length, defaultValue, allowNull, autoIncrement);
        }
    }

    public enum DataType {
        BOOLEAN,
        BYTE,
        SHORT,
        INT,
        LONG,
        FLOAT,
        DOUBLE,
        DATE,
        DATETIME,
        STRING,
        BYTE_ARRAY,
        BLOB,
        CLOB
    }
}
