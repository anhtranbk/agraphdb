package com.agraph.core.type;

public enum DataType {
    OBJECT(1, "obj"),
    BOOLEAN(2, "bool"),
    INT(3, "int"),
    LONG(4, "long"),
    FLOAT(5, "float"),
    DOUBLE(6, "double"),
    STRING(7, "str"),
    UUID(8, "uuid"),
    DATE(9, "date"),
    RAW(0, "raw");

    final byte code;
    final String name;

    DataType(int code, String name) {
        this.code = (byte) code;
        this.name = name;
    }

    public String prefix() {
        return this.name().substring(1);
    }
}
