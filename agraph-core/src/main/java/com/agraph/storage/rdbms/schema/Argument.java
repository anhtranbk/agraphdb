package com.agraph.storage.rdbms.schema;

import lombok.Getter;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * @author <a href="https://github.com/tjeubaoit">tjeubaoit</a>
 */
@Accessors(fluent = true)
@Getter
public class Argument {

    private final DBType type;
    private final Object value;

    private Argument(DBType type, Object value) {
        this.type = type;
        this.value = value;
    }

    public static Argument of(DBType type, Object value) {
        return new Argument(type, value);
    }

    public static Argument of(boolean value) {
        return new Argument(DBType.BOOLEAN, value);
    }

    public static Argument of(byte value) {
        return new Argument(DBType.SMALLINT, value);
    }

    public static Argument of(short value) {
        return new Argument(DBType.SMALLINT, value);
    }

    public static Argument of(int value) {
        return new Argument(DBType.INT, value);
    }

    public static Argument of(long value) {
        return new Argument(DBType.BIGINT, value);
    }

    public static Argument of(float value) {
        return new Argument(DBType.FLOAT, value);
    }

    public static Argument of(double value) {
        return new Argument(DBType.DOUBLE, value);
    }

    public static Argument of(Date value) {
        return new Argument(DBType.DATE, value);
    }

    public static Argument ofTimestamp(long value) {
        return new Argument(DBType.TIMESTAMP, value);
    }

    public static Argument of(String value, boolean fixedLength) {
        return new Argument(fixedLength ? DBType.CHAR : DBType.VARCHAR, value);
    }

    public static Argument of(String value) {
        return of(value, false);
    }

    public static Argument of(byte[] value, boolean fixedLength) {
        return new Argument(fixedLength ? DBType.BINARY : DBType.VARBINARY, value);
    }

    public static Argument of(byte[] value) {
        return of(value, false);
    }
}
