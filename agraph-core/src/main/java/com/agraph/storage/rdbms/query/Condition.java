package com.agraph.storage.rdbms.query;

public interface Condition {

    enum Operator {
        EQUALS,
        NOT_EQUALS,
        LESS,
        LESS_OR_EQUALS,
        GREATER,
        GREATER_OR_EQUALS,
        IN,
        NOT_IN,
        IS_NULL,
        IS_NOT_NULL,
        OR,
        AND
    }

    Operator operator();

    boolean isMulti();
}
