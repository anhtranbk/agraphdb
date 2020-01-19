package com.agraph.storage.rdbms.schema;

import com.google.common.base.Preconditions;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Accessors(fluent = true)
@Getter
public class Column {

    private Column(String name, DBType type, int length, Object defaultValue,
                   boolean allowNull, boolean autoIncrement) {
        this.name = name;
        this.type = type;
        this.length = length;
        this.defaultValue = defaultValue;
        this.allowNull = allowNull;
        this.autoIncrement = autoIncrement;
    }

    private final String name;
    private final DBType type;
    private final int length;
    private final Object defaultValue;
    private final boolean allowNull;
    private final boolean autoIncrement;

    public static Builder builder(String name, DBType DBType) {
        return new Builder(name, DBType);
    }

    @Accessors(fluent = true, chain = true)
    @Setter
    public static class Builder {
        private String name;
        private DBType type;
        private int length;
        private Object defaultValue;
        private boolean allowNull = true;
        private boolean autoIncrement = false;

        Builder(String name, DBType type) {
            this.name = name;
            this.type = type;
        }

        public Column build() {
            Preconditions.checkArgument(length > 0 || (type != DBType.VARCHAR && type != DBType.CHAR),
                    "Cannot create CHAR/VARCHAR column with zero-length");
            return new Column(name, type, length, defaultValue, allowNull, autoIncrement);
        }
    }
}
